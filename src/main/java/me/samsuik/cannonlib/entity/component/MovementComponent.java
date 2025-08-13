package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.Collisions;
import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.component.SimpleComponent;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.MathUtil;
import me.samsuik.cannonlib.physics.shape.AABB;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

public final class MovementComponent implements SimpleComponent<Entity> {
    private final boolean collisions;

    public MovementComponent(final boolean collisions) {
        this.collisions = collisions;
    }

    @Override
    public void action0(final Entity entity, final int tick) {
        Vec3d movement = entity.momentum;
        final Vec3d stuckSpeed = entity.getData(EntityDataKeys.STUCK_SPEED);
        if (stuckSpeed != null) {
            movement = movement.mul(stuckSpeed);
            entity.removeData(EntityDataKeys.STUCK_SPEED);
        }

        final Vec3d relative;
        if (entity.getWorld() != null) {
            relative = this.collide(entity, movement);
        } else {
            relative = movement;
        }

        final double relativeMovementSqr = relative.magnitudeSquared();
        if (relativeMovementSqr > MathUtil.EPSILON || /* >=1.21.2 */ movement.magnitudeSquared() - relativeMovementSqr < MathUtil.EPSILON) {
            entity.position = entity.position.add(relative);
        }

        if (stuckSpeed != null) {
            entity.momentum = Vec3d.zero();
        } else if (!movement.equals(relative)) {
            entity.momentum = new Vec3d(
                    movement.x() == relative.x() ? movement.x() : 0.0,
                    movement.y() == relative.y() ? movement.y() : 0.0,
                    movement.z() == relative.z() ? movement.z() : 0.0
            );
        }

        entity.onGround = movement.y() < 0.0 && movement.y() != relative.y();
    }

    private Vec3d collide(final Entity entity, final Vec3d movement) {
        final World world = entity.getWorld();
        AABB entityBB = Shapes.entityBoundingBox(entity.position, 0.98f);

        double moveX = movement.x();
        double moveY = movement.y();
        double moveZ = movement.z();

        if (moveY != 0.0) {
            for (final Shape shape : this.fetchCollisions(world, entityBB, 0.0, moveY, 0.0)) {
                moveY = shape.collideY(entityBB, moveY);
            }
            entityBB = entityBB.move(0.0, moveY, 0.0);
        }

        final boolean xSmaller = Math.abs(moveX) < Math.abs(moveZ);
        if (xSmaller && moveZ != 0.0) {
            for (final Shape shape : this.fetchCollisions(world, entityBB, 0.0, 0.0, moveZ)) {
                moveZ = shape.collideZ(entityBB, moveZ);
            }
            entityBB = entityBB.move(0.0, 0.0, moveZ);
        }

        if (moveX != 0.0) {
            for (final Shape shape : this.fetchCollisions(world, entityBB, moveX, 0.0, 0.0)) {
                moveX = shape.collideX(entityBB, moveX);
            }
            if (!xSmaller) {
                entityBB = entityBB.move(moveX, 0.0, 0.0);
            }
        }

        if (!xSmaller && moveZ != 0.0) {
            for (final Shape shape : this.fetchCollisions(world, entityBB, 0.0, 0.0, moveZ)) {
                moveZ = shape.collideZ(entityBB, moveZ);
            }
        }
        return new Vec3d(moveX, moveY, moveZ);
    }

    private Collisions fetchCollisions(
            final World world,
            final AABB entityBB,
            final double moveX,
            final double moveY,
            final double moveZ
    ) {
        final AABB collisionBB = entityBB.expandTo(entityBB.move(moveX, moveY, moveZ));
        return world.getCollisions(collisionBB, this.collisions);
    }
}
