package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.shape.AABB;
import me.samsuik.cannonlib.physics.shape.Shape;

import java.util.Iterator;
import java.util.List;

public final class MovementCollisions {
    public static Vec3d collide(final Entity entity, final Vec3d movement, final float entitySize) {
        final Iterable<Shape> collisions = getRelevantCollisions(entity);
        AABB entityBB = Shapes.entityBoundingBox(entity.position, entitySize);

        double moveX = movement.x();
        double moveY = movement.y();
        double moveZ = movement.z();

        if (moveY != 0.0) {
            for (final Shape shape : collisions) {
                moveY = shape.collideY(entityBB, moveY);
            }
            entityBB = entityBB.move(0.0, moveY, 0.0);
        }

        final boolean xSmaller = movement.x() < movement.z();
        if (xSmaller && movement.z() != 0.0) {
            for (final Shape shape : collisions) {
                moveZ = shape.collideZ(entityBB, moveZ);
            }
            entityBB = entityBB.move(0.0, 0.0, moveZ);
        }

        if (movement.x() != 0.0) {
            for (final Shape shape : collisions) {
                moveX = shape.collideX(entityBB, moveX);
            }
            if (!xSmaller) {
                entityBB = entityBB.move(moveX, 0.0, 0.0);
            }
        }

        if (!xSmaller && movement.z() != 0.0) {
            for (final Shape shape : collisions) {
                moveZ = shape.collideZ(entityBB, moveZ);
            }
        }
        return new Vec3d(moveX, moveY, moveZ);
    }

    private static Iterable<Shape> getRelevantCollisions(final Entity entity) {
        final World world = entity.getWorld();
        final List<Shape> globalCollisions = world != null ? world.getGlobalCollisions() : List.of();
        final List<Shape> blockCollisions = world != null ? world.getBlockCollisions() : List.of();
        return () -> new ShapeIterator(globalCollisions, blockCollisions);
    }

    private static class ShapeIterator implements Iterator<Shape> {
        private final Iterator<Shape> iterator1;
        private final Iterator<Shape> iterator3;

        public ShapeIterator(final List<Shape> list1, final List<Shape> list3) {
            this.iterator1 = list1.iterator();
            this.iterator3 = list3.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator1.hasNext()
                || this.iterator3.hasNext();
        }

        @Override
        public Shape next() {
            if (this.iterator1.hasNext()) {
                return this.iterator1.next();
            } else if (this.iterator3.hasNext()) {
                return this.iterator3.next();
            }
            return null;
        }

        @Override
        public void remove() {}
    }
}
