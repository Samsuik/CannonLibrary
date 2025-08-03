package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.block.interaction.BlockInteractions;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public final class FallingBlockComponent implements Component<Entity> {
    private static final ThreadLocal<Boolean> TICKING = ThreadLocal.withInitial(() -> false);
    private final Block block;
    private final int amount;
    private final boolean concrete;

    public FallingBlockComponent(final Block block, final int amount) {
        this.block = block;
        this.amount = amount;
        this.concrete = block.has(BlockInteractions.CONCRETE_POWDER);
    }

    @Override
    public boolean action(final Entity entity, final int tick) {
        if (!entity.onGround || TICKING.get()) {
            return false;
        }

        entity.remove();

        for (int count = 0; count < this.amount; ++count) {
            if (count != 0 || entity.hasData(EntityDataKeys.REPEAT)) {
                entity.getEntityState().apply(entity);
                TICKING.set(true);
                entity.tick();
                TICKING.set(false);
            }

            final World world = entity.getWorld();
            final Vec3i blockPos = entity.position.toVec3i();
            final Block presentBlock = world.getBlockAtRaw(blockPos);
            if (presentBlock == Blocks.MOVING_PISTON) {
                return false;
            }

            final Block stack = this.blockToStack(world, blockPos, presentBlock);
            if (stack == null) {
                break;
            } else {
                entity.putData(EntityDataKeys.STACKED, COUNTER.getAndIncrement());
                world.setBlock(blockPos, stack);
            }
        }

        return true;
    }

    private Block blockToStack(
            final World world,
            final Vec3i blockPos,
            final Block presentBlock
    ) {
        // Concrete solidifies when in contact with water
        if (this.concrete && presentBlock.has(BlockInteractions.WATER)) {
            return Blocks.CONCRETE;
        }

        // As an optimisation falling blocks will only break if the block below is air
        final Block belowBlock = world.getBlockAtRaw(blockPos.down());
        if (belowBlock != Blocks.AIR && (presentBlock == null || presentBlock.replace())) {
            return this.block;
        }

        return null;
    }
}
