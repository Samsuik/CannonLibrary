package me.samsuik.cannonlib.scenario;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ClippingScenario implements Scenario {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClippingScenario.class);
    private final Vec3i clipPos;
    private final boolean checkWall;

    public ClippingScenario(final Vec3i clipPos, final boolean checkWall) {
        this.clipPos = clipPos;
        this.checkWall = checkWall;
    }

    @Override
    public boolean test(final World world) {
        // Make sure the wall is watered before further testing
        for (int blockY = -64; blockY < 320; ++blockY) {
            final Vec3i blockPosY = this.clipPos.mul(1, 0, 1).add(0, blockY, 0);
            if (world.getBlockAt(blockPosY) == null) {
                world.setBlock(blockPosY, Blocks.WATER);
            }
        }

        final World snapshot = world.snapshot();
        snapshot.keepTicking();

        final int stackTop = getSandStackTop(snapshot);

        final Map<Vec3i, Block> wallBlocks = new HashMap<>();
        for (final Rotation rotation : Rotation.values()) {
            if (rotation.getAxis().isHorizontal()) {
                //for (int blockY = this.clipPos.y() - 2; blockY <= stackTop; ++blockY) {
                final int blockY = this.clipPos.y() - 1;
                final Vec3i blockPosY = this.clipPos.mul(1, 0, 1).add(0, blockY, 0);
                final Vec3i direction = rotation.getDirection().toVec3i();
                final Vec3i wallPos = blockPosY.add(direction);

                wallBlocks.put(wallPos, world.getBlockAt(wallPos));
                //}
            }
        }

        // Calculate how much it understacks and if it was able to break any wallBlocks
        final Map<Vec3i, ClipAAAA> clips = this.simulateClipping(world, wallBlocks, stackTop);

        boolean severeClip = false;
        for (final Map.Entry<Vec3i, ClipAAAA> entry : clips.entrySet()) {
            final ClipAAAA clip = entry.getValue();
            final int blocksAboveBarrel = entry.getKey().y() - this.clipPos.y() + 1;

            final StringBuilder builder = new StringBuilder();
            builder.append("understack: ").append(clip.blocksUnderStacked());
            builder.append(", rel-stack y ").append(clip.newStackTop - stackTop);
            if (this.checkWall) {
                builder.append(", destroyed wall: ").append(clip.destroyedWall());
            }
            if (clip.blocksUnderStacked() > stackTop - blocksAboveBarrel || this.checkWall && !clip.destroyedWall()) {
                builder.append(" - clipped");
                severeClip = true;
            }
            LOGGER.info("@ +Y: {}, {}", blocksAboveBarrel, builder);
        }

        return !severeClip;
    }

    private int getSandStackTop(final World snapshot) {
        for (int blockY = 319; blockY >= -64; --blockY) {
            final Vec3i blockPosY = this.clipPos.mul(1, 0, 1).add(0, blockY, 0);
            final Block block = snapshot.getBlockAt(blockPosY);

            if (block == null || block == Blocks.SAND) {
                return blockY;
            }
        }
        return -64;
    }

    private Map<Vec3i, ClipAAAA> simulateClipping(final World world, final Map<Vec3i, Block> wallBlocks, final int stackTop) {
        final Map<Vec3i, ClipAAAA> clips = new LinkedHashMap<>();
        for (int blockY = this.clipPos.y(); blockY <= stackTop + 3; ++blockY) {
            final Vec3i pos = this.clipPos.mul(1, 0, 1).add(0, blockY, 0);
            final World snapshot = world.snapshot();

            // create clip
            snapshot.setBlock(pos, Blocks.BEDROCK);
            snapshot.keepTicking();

            boolean destroyedWall = false;
            for (final Map.Entry<Vec3i, Block> entry : wallBlocks.entrySet()) {
                if (snapshot.getBlockAt(entry.getKey()) != entry.getValue()) {
                    destroyedWall = true;
                }
            }

            final int newStackTop = this.getSandStackTop(snapshot);
            final int underStack = newStackTop >= stackTop ? 0 : blockY - newStackTop - 1;

            clips.put(pos, new ClipAAAA(underStack, newStackTop, destroyedWall));

            LOGGER.info("{} {} {}", blockY - 1, stackTop, newStackTop);
        }
        return clips;
    }

    private record ClipAAAA(int blocksUnderStacked, int newStackTop, boolean destroyedWall) {}
}
