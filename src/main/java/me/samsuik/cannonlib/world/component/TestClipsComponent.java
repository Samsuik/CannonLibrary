package me.samsuik.cannonlib.world.component;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.ResponsiveComponent;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3i;
import me.samsuik.cannonlib.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class TestClipsComponent implements ResponsiveComponent<World> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestClipsComponent.class);
    private static final List<Block> CLIP_BLOCKS = List.of(Blocks.BEDROCK, Blocks.COBBLESTONE, Blocks.SLAB.rotate(Rotation.UP));
    private final Vec3i guiderPos;
    private final Vec3i clipToPos;
    private final boolean checkWall;

    public TestClipsComponent(final Vec3i guiderPos, final Vec3i clipToPos, final boolean checkWall) {
        this.guiderPos = guiderPos;
        this.clipToPos = clipToPos;
        this.checkWall = checkWall;
    }

    @Override
    public boolean success(final World world, final int tick) {
        final World snapshot = world.snapshot();
        final List<Entity> entities = new ArrayList<>(snapshot.getEntityList());
        snapshot.keepTicking();

        final Map<Vec3i, Block> wallBlocks = new HashMap<>();
        for (final Rotation rotation : Rotation.values()) {
            if (rotation.getAxis().isHorizontal()) {
                final Vec3i blockPosY = this.guiderPos.setY(this.guiderPos.y() - 1);
                final Vec3i direction = rotation.getDirection().toVec3i();
                final Vec3i wallPos = blockPosY.add(direction);

                wallBlocks.put(wallPos, world.getBlockAt(wallPos));
            }
        }

        // Calculate how much it understacks and if it was able to break any wallBlocks
        final Map<String, Integer> sandPositions = this.getStackedSandPositions(entities);
        final Map<Vec3i, ClipResult> clips = this.simulateClipping(world, wallBlocks, sandPositions);

        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, Integer> stack : sandPositions.entrySet()) {
            builder.append("-> \"" + stack.getKey() + "\": " + stack.getValue());
        }
        LOGGER.info("@ {}", builder);

        boolean severeClip = false;
        for (final Map.Entry<Vec3i, ClipResult> entry : clips.entrySet()) {
            final int blocksAboveBarrel = entry.getKey().y() - this.guiderPos.y() + 1;
            final ClipResult clip = entry.getValue();
            final StringBuilder stacks = new StringBuilder();

            int stackTop = Integer.MIN_VALUE;
            for (final Map.Entry<String, Integer> stack : clip.underStacked()) {
                stacks.append("-> \"" + stack.getKey() + "\": " + stack.getValue());
                stackTop = Math.max(stackTop, stack.getValue());
            }

            if (this.checkWall) {
                stacks.append(", destroyed wall: ").append(clip.destroyedWall());
            }

            if (stackTop < this.guiderPos.y() - 2 || this.checkWall && !clip.destroyedWall()) {
                stacks.append(" - clipped");
                severeClip = true;
            }

            LOGGER.info("@ [+Y: {} \"{}\"]: {}", blocksAboveBarrel, clip.block().name(), stacks);
        }

        return !severeClip;
    }

    private Map<String, Integer> getStackedSandPositions(final List<Entity> entities) {
        final Map<String, Integer> heights = new HashMap<>();
        for (final Entity entity : entities) {
            if (entity.hasData(EntityDataKeys.STACKED)) {
                final String name = entity.getDataOrDefault(EntityDataKeys.NAME, "unknown");
                final int present = heights.getOrDefault(name, Integer.MIN_VALUE);
                final int entityPos = entity.position.toVec3i().y();
                heights.put(name, Math.max(present, entityPos));
            }
        }
        return heights;
    }

    private Map<Vec3i, ClipResult> simulateClipping(
            final World world,
            final Map<Vec3i, Block> wallBlocks,
            final Map<String, Integer> sandPositions
    ) {
        final Map<Vec3i, ClipResult> clips = new LinkedHashMap<>();
        final int stackTop = sandPositions.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(Integer.MIN_VALUE);

        final int highestClipY = Math.max(stackTop + 3, this.clipToPos.y());
        for (int blockY = this.guiderPos.y(); blockY <= highestClipY; ++blockY) {
            final Vec3i blockPos = this.guiderPos.setY(blockY);
            for (final Block block : CLIP_BLOCKS) {
                this.clipWithBlock(world, blockPos, block, wallBlocks, sandPositions, clips);
            }
        }

        return clips;
    }

    private void clipWithBlock(
            final World world,
            final Vec3i blockPos,
            final Block block,
            final Map<Vec3i, Block> wallBlocks,
            final Map<String, Integer> sandPositions,
            final Map<Vec3i, ClipResult> clips
    ) {
        final World snapshot = world.snapshot();
        final List<Entity> entities = new ArrayList<>(snapshot.getEntityList());

        // create clip
        snapshot.setBlock(blockPos, block);
        snapshot.keepTicking();

        boolean destroyedWall = false;
        for (final Map.Entry<Vec3i, Block> entry : wallBlocks.entrySet()) {
            if (snapshot.getBlockAt(entry.getKey()) != entry.getValue()) {
                destroyedWall = true;
            }
        }

        // Calculate how much it under stacked
        final List<Map.Entry<String, Integer>> underStacked = new ArrayList<>();
        final Map<String, Integer> newSandPositions = this.getStackedSandPositions(entities);
        for (final Map.Entry<String, Integer> entry : sandPositions.entrySet()) {
            final int newStackTop = newSandPositions.get(entry.getKey());
            final int oldStackTop = sandPositions.get(entry.getKey());
            final int underStack = newStackTop >= oldStackTop ? 0 : blockPos.y() - newStackTop - 1;
            underStacked.add(Map.entry(entry.getKey(), underStack));
        }

        clips.put(blockPos, new ClipResult(underStacked, block, destroyedWall));
    }

    private record ClipResult(List<Map.Entry<String, Integer>> underStacked, Block block, boolean destroyedWall) {}
}
