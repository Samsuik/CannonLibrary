package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.MovementCollisions;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

public final class MovementComponent implements Component<Entity> {
    private final float entitySize;
    private final boolean collisions;

    public MovementComponent(final float entitySize, final boolean collisions) {
        this.entitySize = entitySize;
        this.collisions = collisions;
    }

    @Override
    public void action(final Entity entity, final int tick) {
        final Vec3d movement = entity.momentum;
        final Vec3d relative;
        if (this.collisions) {
            relative = MovementCollisions.collide(entity, movement, this.entitySize);
        } else {
            relative = movement;
        }

        final boolean ground;
        if (this.collisions && !movement.equals(relative)) {
            entity.momentum = movement.mul(
                    movement.x() != relative.x() ? 0.0 : 1.0,
                    movement.y() != relative.y() ? 0.0 : 1.0,
                    movement.z() != relative.z() ? 0.0 : 1.0
            );
            ground = movement.y() < 0.0 && movement.y() != relative.y();
        } else {
            ground = false;
        }

        entity.onGround = ground;
        entity.position = entity.position.add(relative);
    }
}
