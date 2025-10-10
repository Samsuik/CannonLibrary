package me.samsuik.cannonlib.entity.helpers.clipping;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.helpers.StackHeight;
import me.samsuik.cannonlib.explosion.Explosion;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3i;
import me.samsuik.cannonlib.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class ClippingBlocks {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClippingBlocks.class);
    private static final List<Block> CLIP_BLOCKS = List.of(
//            Blocks.BEDROCK,
//            Blocks.SLAB.rotate(Rotation.UP),
//            Blocks.TOP_TRAPDOOR,
//            Blocks.CEILING_AMETHYST_CLUSTER,
//            Blocks.CEILING_GRINDSTONE,
//            Blocks.CEILING_BELL,
//            Blocks.CHAINS,
//            Blocks.RODS,
            Blocks.COBBLESTONE,
            Blocks.OBSIDIAN.durability(2).name("obsidian 2x"),
            Blocks.OBSIDIAN.durability(3).name("obsidian 3x"),
            Blocks.OBSIDIAN.durability(4).name("obsidian 4x"),
            Blocks.OBSIDIAN.durability(5).name("obsidian 5x")
    );

    public static ClipInformation getClipInformation(final World world, final Vec3i guiderPos, final boolean checkConsistency) {
        return getClipInformation(world, guiderPos, CLIP_BLOCKS, checkConsistency);
    }

    public static ClipInformation getClipInformation(
            final World world,
            final Vec3i guiderPos,
            final List<Block> blocks,
            final boolean checkConsistency
    ) {
        final World snapshot = world.snapshot();
        final List<Entity> entities = new ArrayList<>(snapshot.getEntityList());
        snapshot.keepTicking();

        for (int waterTicks = 0; waterTicks < 5; ++waterTicks) {
            snapshot.tick();
        }

        final List<StackHeight> stackHeights = StackHeight.getStackHeights(entities);
        final Vec3i stackPos = stackHeights.isEmpty() ? Vec3i.zero() : stackHeights.getFirst().position();
        final int stackTop = stackHeights.stream()
                .mapToInt(StackHeight::top)
                .max()
                .orElse(Integer.MIN_VALUE);

        final int highestClipY = Math.max(stackTop + 16, guiderPos.y());
        final WallState wallState = findWallBlocks(world, snapshot, stackPos, highestClipY, guiderPos.y());
        final List<ClippedBlock> clippedBlocks = new ArrayList<>();

        for (final Block block : blocks) {
            for (int blockY = guiderPos.y(); blockY <= highestClipY; ++blockY) {
                final Vec3i position = stackPos.setY(blockY);
                final Optional<ClippedBlock> clip = clipWithBlock(
                        world, position, block, wallState, stackHeights, checkConsistency
                );
                clip.ifPresent(clippedBlocks::add);
            }
        }

        clippedBlocks.removeIf(clippedBlock -> clippedBlock.severity(guiderPos, stackTop) == Severity.NONE);
        return new ClipInformation(clippedBlocks, stackHeights, wallState, stackTop);
    }

    private static WallState findWallBlocks(
            final World world,
            final World snapshot,
            final Vec3i stackPos,
            final int highestClipY,
            final int guiderY
    ) {
        final WallState wallState = new WallState();
        for (int blockY = stackPos.y() - 8; blockY <= highestClipY; ++blockY) {
            for (final Rotation rotation : Rotation.values()) {
                if (rotation.getAxis().isHorizontal()) {
                    final Vec3i wallPos = stackPos.setY(blockY).move(rotation);
                    final Block presentBlock = world.getBlockAt(wallPos);
                    wallState.addBlock(wallPos, presentBlock);
                }
            }
        }

        wallState.updateState(snapshot, guiderY);
        return wallState;
    }
    
    private static Optional<ClippedBlock> clipWithBlock(
            final World world,
            final Vec3i position,
            final Block block,
            final WallState wallState,
            final List<StackHeight> stackHeights,
            final boolean checkConsistency
    ) {
        final int attempts = checkConsistency ? 128 : 1;
        for (int count = 0; count < attempts; ++count) {
            final World snapshot = world.snapshot();
            final List<Entity> entities = new ArrayList<>(snapshot.getEntityList());
            final WateredState wateredState = wallState.pushedWater()
                    ? WateredState.values()[count % 3] : WateredState.DRY;

            if (wallState.pushedWater() && wateredState != WateredState.WATERED) {
                final int toDrain = wateredState == WateredState.DRY ? 8 : 1;
                for (int offset = toDrain; offset > 0; offset--) {
                    snapshot.removeBlock(position.sub(0, offset, 0));
                }
            }

            // when checking consistency we want to weight the explosion strength
            if (count >= 6) {
                Explosion.setMoreVariation(true);
            }

            // add a block above the sand stack to see if it clips
            snapshot.setBlock(position, block);
            snapshot.keepTicking();

            if (count >= 6) {
                Explosion.setMoreVariation(false);
            }

            if (wallState.pushedWater()) {
                for (int waterTicks = 0; waterTicks < 5; ++waterTicks) {
                    snapshot.tick();
                }
            }

            // calculate stack heights
            final List<StackHeight> newStackHeights = StackHeight.getStackHeights(entities);
            final boolean stackedUpToClip = newStackHeights.stream()
                    .anyMatch(stackHeight -> position.y() - 1 == stackHeight.top());

            final int state = wallState.getState(snapshot, false);
            if (wallState.problem(state) || !stackedUpToClip && !stackHeights.equals(newStackHeights)) {
                return Optional.of(new ClippedBlock(position, block, wateredState, newStackHeights, wallState, state, count > 0));
            }
        }

        return Optional.empty();
    }
}
