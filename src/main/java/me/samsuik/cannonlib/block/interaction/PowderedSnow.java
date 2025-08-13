package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

public final class PowderedSnow implements Interaction {
    @Override
    public void touched(final Entity entity) {
        entity.putData(EntityDataKeys.STUCK_SPEED, new Vec3d(0.9f, 1.5f, 0.9f));
    }

    @Override
    public Shape collisionShape(final Shape shape, final Entity entity) {
        final float fallen = entity.getDataOrDefault(EntityDataKeys.FALLEN, 0.0f);
        return fallen > 2.5f || entity.hasData(EntityDataKeys.FALLING_BLOCK) ? Shapes.POWDER_SNOW : Shapes.EMPTY_SHAPE;
    }
}
