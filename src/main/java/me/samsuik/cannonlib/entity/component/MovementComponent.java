package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.component.SimpleComponent;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.shape.AABB;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

public final class MovementComponent implements SimpleComponent<Entity> {
    private final float entitySize;
    private final boolean collisions;

    public MovementComponent(final float entitySize, final boolean collisions) {
        this.entitySize = entitySize;
        this.collisions = collisions;
    }

    @Override
    public void action0(final Entity entity, final int tick) {
        final Vec3d movement = entity.momentum;
        final Vec3d relative;
        if (this.collisions && entity.getWorld() != null) {
            relative = this.collide(entity, movement);
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

    private Vec3d collide(final Entity entity, final Vec3d movement) {
        final Iterable<Shape> collisions = entity.getWorld().getCollisions();
        AABB entityBB = Shapes.entityBoundingBox(entity.position, this.entitySize);

        double moveX = movement.x();
        double moveY = movement.y();
        double moveZ = movement.z();

        if (moveY != 0.0) {
            for (final Shape shape : collisions) {
                moveY = shape.collideY(entityBB, moveY);
            }
            entityBB = entityBB.move(0.0, moveY, 0.0);
        }

        final boolean xSmaller = Math.abs(moveX) < Math.abs(moveZ);
        if (xSmaller && moveZ != 0.0) {
            for (final Shape shape : collisions) {
                moveZ = shape.collideZ(entityBB, moveZ);
            }
            entityBB = entityBB.move(0.0, 0.0, moveZ);
        }

        if (moveX != 0.0) {
            for (final Shape shape : collisions) {
                moveX = shape.collideX(entityBB, moveX);
            }
            if (!xSmaller) {
                entityBB = entityBB.move(moveX, 0.0, 0.0);
            }
        }

        if (!xSmaller && moveZ != 0.0) {
            for (final Shape shape : collisions) {
                moveZ = shape.collideZ(entityBB, moveZ);
            }
        }
        return new Vec3d(moveX, moveY, moveZ);
    }
}
