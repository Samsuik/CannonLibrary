package me.samsuik.cannonlib.entity.helpers;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3i;
import me.samsuik.cannonlib.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class ClippingBlocks {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClippingBlocks.class);
    private static final List<Block> CLIP_BLOCKS = List.of(Blocks.BEDROCK, Blocks.COBBLESTONE, Blocks.SLAB.rotate(Rotation.UP));

    public static void logForDiscord(
            final World world,
            final Vec3i guiderPos,
            final boolean checkWall
    ) {
        logForDiscord(world, guiderPos, guiderPos, checkWall, true);
    }

    public static void logForDiscord(
            final World world,
            final Vec3i guiderPos,
            final Vec3i clipToPos,
            final boolean checkWall,
            final boolean checkConsistency
    ) {
        final World snapshot = world.snapshot();
        final List<Entity> entities = new ArrayList<>(snapshot.getEntityList());
        snapshot.keepTicking();

        final List<StackHeight> stackHeights = StackHeight.getStackHeights(entities);
        final List<ClippedBlock> clips = getClippingBlocks(
                world, guiderPos, clipToPos, stackHeights, checkConsistency
        );

        LOGGER.info("```js");
        LOGGER.info("// Guider-Y: {}", guiderPos.y());
        for (final StackHeight height : stackHeights) {
            LOGGER.info(height.information(false));
        }

        LOGGER.info("```");

        if (clips.isEmpty()) {
            return;
        }

        LOGGER.info("```js");
        for (final ClippedBlock clippedBlock : clips) {
            LOGGER.info(clippedBlock.information(guiderPos, checkWall));
        }
        LOGGER.info("```");
    }

    public static List<ClippedBlock> getClippingBlocks(
            final World world,
            final Vec3i guiderPos,
            final Vec3i clipToPos,
            final List<StackHeight> stackHeights,
            final boolean checkConsistency
    ) {
        final Map<Vec3i, Block> wallBlocks = new HashMap<>();
        for (final Rotation rotation : Rotation.values()) {
            if (rotation.getAxis().isHorizontal()) {
                final Vec3i blockPosY = guiderPos.setY(guiderPos.y() - 1);
                final Vec3i wallPos = blockPosY.move(rotation);
    
                wallBlocks.put(wallPos, world.getBlockAtRaw(wallPos));
            }
        }

        final Vec3i clipPos;
        if (!stackHeights.isEmpty() && guiderPos.x() == clipToPos.x() && guiderPos.z() == clipToPos.z()) {
            clipPos = stackHeights.getFirst().position();
        } else {
            clipPos = clipToPos;
        }

        final List<ClippedBlock> clips = new ArrayList<>();
        final int stackTop = stackHeights.stream()
                .mapToInt(StackHeight::top)
                .max()
                .orElse(Integer.MIN_VALUE);

        final int highestClipY = Math.max(stackTop + 3, clipToPos.y());
        for (final Block block : CLIP_BLOCKS) {
            for (int blockY = guiderPos.y(); blockY <= highestClipY; ++blockY) {
                final Vec3i position = clipPos.setY(blockY);
                final Optional<ClippedBlock> clip = clipWithBlock(
                        world, position, block, wallBlocks, stackHeights, checkConsistency
                );
                clip.ifPresent(clips::add);
            }
        }

        return clips;
    }
    
    private static Optional<ClippedBlock> clipWithBlock(
            final World world,
            final Vec3i position,
            final Block block,
            final Map<Vec3i, Block> wallBlocks,
            final List<StackHeight> stackHeights,
            final boolean checkConsistency
    ) {
        final int attempts = checkConsistency ? 256 : 6;
        for (int count = 0; count < attempts; ++count) {
            final World snapshot = world.snapshot();
            final List<Entity> entities = new ArrayList<>(snapshot.getEntityList());

            // create clip in world
            snapshot.setBlock(position, block);
            snapshot.keepTicking();

            boolean destroyedWall = false;
            for (final Map.Entry<Vec3i, Block> entry : wallBlocks.entrySet()) {
                if (snapshot.getBlockAtRaw(entry.getKey()) != entry.getValue()) {
                    destroyedWall = true;
                }
            }

            // calculate stack heights
            final List<StackHeight> newStackHeights = StackHeight.getStackHeights(entities);
            final boolean stackedUpToClip = newStackHeights.stream()
                    .anyMatch(stackHeight -> position.y() - 1 == stackHeight.top());

            if ((!stackedUpToClip || !destroyedWall) && !stackHeights.equals(newStackHeights)) {
                return Optional.of(new ClippedBlock(position, block, newStackHeights, destroyedWall, count != 0));
            }
        }

        return Optional.empty();
    }

    public record ClippedBlock(Vec3i position, Block block, List<StackHeight> stackHeights, boolean destroyedWall, boolean inconsistent) {
        public String information(final Vec3i guiderPos, final boolean checkWall) {
            final StringBuilder builder = new StringBuilder();
//            final int blocksAboveBarrel = this.position.y() - guiderPos.y() + 1;
            builder.append("Y: %s \"%s\"".formatted(this.position.y(), this.block().name()));

            int stackTop = Integer.MIN_VALUE;
            for (final StackHeight stack : this.stackHeights) {
                stackTop = Math.max(stackTop, stack.top());
            }

            builder.append(" understacked: %d [".formatted(this.position.y() - stackTop - 1));

            for (int i = 0; i < this.stackHeights.size(); i++) {
                if (i != 0) {
                    builder.append(" ");
                }
                builder.append(this.stackHeights.get(i));
            }

            builder.append("]");

            if (this.inconsistent) {
                builder.append(" - inconsistent");
            }

            if (stackTop < guiderPos.y() - 2) {
                builder.append(" - clipped");
            } else if (checkWall && !this.destroyedWall()) {
                builder.append(" - unable to scatter");
            }

            return builder.toString();
        }
    }
}
