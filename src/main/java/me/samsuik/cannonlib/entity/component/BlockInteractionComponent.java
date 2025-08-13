package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.component.SimpleComponent;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.MathUtil;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public final class BlockInteractionComponent implements SimpleComponent<Entity> {
    @Override
    public void action0(final Entity entity, final int tick) {
        // update fall distance
        final Vec3d position = entity.position;
        if (entity.onGround) {
            entity.putData(EntityDataKeys.FALLEN, 0.0f);
        } else {
            final double fell = entity.getEntityState().position().y() - position.y();
            if (fell < 0.0) {
                final float fallen = entity.getDataOrDefault(EntityDataKeys.FALLEN, 0.0f);
                entity.putData(EntityDataKeys.FALLEN, fallen - (float) fell);
            }
        }

        // interact with blocks
        final double half = (double) (0.98f / 2.0f) + MathUtil.EPSILON;
        final int minX = MathUtil.floor(position.x() - half);
        final int minY = MathUtil.floor(position.y());
        final int minZ = MathUtil.floor(position.z() - half);
        final int maxX = MathUtil.floor(position.x() + half);
        final int maxY = MathUtil.floor(position.y() + (half * 2.0));
        final int maxZ = MathUtil.floor(position.z() + half);

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    final Vec3i blockPos = new Vec3i(x, y, z);
                    final Block block = entity.getWorld().getBlockAtRaw(blockPos);

                    if (block != null) {
                        block.interaction().touched(entity);
                    }
                }
            }
        }
    }
}
