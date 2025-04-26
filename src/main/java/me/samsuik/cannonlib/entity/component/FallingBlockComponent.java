package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.data.DataKeys;
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
    public void action(final Entity entity, final int tick) {
        if (!entity.onGround || TICKING.get()) {
            return;
        }

        for (int count = 0; count < this.amount; ++count) {
            if (count != 0 || entity.getData(DataKeys.REPEAT)) {
                entity.getEntityState().apply(entity);
                TICKING.set(true);
                entity.tick();
                TICKING.set(false);
            }

            final World world = entity.getWorld();
            final Vec3i blockPos = entity.position.toVec3i();
            final Block presentBlock = world.getBlockAt(blockPos);
            if (presentBlock == Blocks.MOVING_PISTON) {
                return;
            }

            // It would be too intensive to look for entity/global collisions to stack on.
            // As a compromise you have to "opt in" to falling blocks breaking when trying to stack midair.
            final Block belowBlock = world.getBlockAt(blockPos.add(0, -1, 0));
            if (belowBlock != Blocks.AIR && (presentBlock == null || presentBlock.replace())) {
                world.setBlock(blockPos, block);
            } else {
                break;
            }
        }

        entity.putData(DataKeys.STACKED, true);
        entity.removeCurrentComponent();
        entity.remove();
    }
}
