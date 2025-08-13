package me.samsuik.cannonlib;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ScheduledBlockTicks {
    private final List<BlocksToTick> blocksToTicks = new ArrayList<>();

    public void tick(final World world) {
        if (this.blocksToTicks.isEmpty()) {
            return;
        }

        final BlocksToTick blocksToTick = this.blocksToTicks.removeFirst();
        for (final ScheduledBlock scheduledBlock : blocksToTick.scheduled) {
            final Block expectedBlock = scheduledBlock.block();
            final Vec3i blockPos = scheduledBlock.position();
            if (world.getBlockAtRaw(blockPos) == expectedBlock) {
                expectedBlock.interaction().onTick(world, blockPos, expectedBlock);
            }
        }
    }

    public void scheduleBlockTick(final int tick, final Vec3i blockPos, final Block block) {
        while (tick >= this.blocksToTicks.size()) {
            this.blocksToTicks.add(new BlocksToTick());
        }

        final ScheduledBlock scheduledBlock = new ScheduledBlock(blockPos, block);
        this.blocksToTicks.get(tick).scheduleBlockTick(scheduledBlock);
    }

    public void clear() {
        this.blocksToTicks.clear();
    }

    public void copy(final ScheduledBlockTicks blockTicks) {
        for (final BlocksToTick blocksToTick : blockTicks.blocksToTicks) {
            this.blocksToTicks.add(blocksToTick.copy());
        }
    }

    private static class BlocksToTick {
        private final List<ScheduledBlock> scheduled = new ArrayList<>();
        private final Set<Vec3i> positions = new HashSet<>();

        public void scheduleBlockTick(final ScheduledBlock scheduledBlock) {
            if (this.positions.add(scheduledBlock.position())) {
                this.scheduled.add(scheduledBlock);
            }
        }

        public BlocksToTick copy() {
            final BlocksToTick blocksToTick = new BlocksToTick();
            blocksToTick.scheduled.addAll(this.scheduled);
            blocksToTick.positions.addAll(this.positions);
            return blocksToTick;
        }
    }

    private record ScheduledBlock(Vec3i position, Block block) {}
}
