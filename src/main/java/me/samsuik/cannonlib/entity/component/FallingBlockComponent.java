package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public final class FallingBlockComponent implements Component<Entity> {
    private static final ThreadLocal<Boolean> TICKING = ThreadLocal.withInitial(() -> false);
    private final Block block;
    private final int amount;

    public FallingBlockComponent(final Block block, final int amount) {
        this.block = block;
        this.amount = amount;
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

            // As an optimisation falling blocks will only break if the block below is air
            final Block belowBlock = world.getBlockAtRaw(blockPos.down());
            if (belowBlock != Blocks.AIR && (presentBlock == null || presentBlock.replace())) {
                entity.putData(EntityDataKeys.STACKED, COUNTER.getAndIncrement());
                world.setBlock(blockPos, this.block);
            } else {
                break;
            }
        }

        return true;
    }
}
