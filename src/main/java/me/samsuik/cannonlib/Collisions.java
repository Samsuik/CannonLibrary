package me.samsuik.cannonlib;

import me.samsuik.cannonlib.physics.shape.Shape;

import java.util.Iterator;

public final class Collisions implements Iterable<Shape> {
    private final Iterable<Shape> blockCollisions;
    private final Iterable<Shape> globalCollisions;

    public Collisions(final Iterable<Shape> blockCollisions, final Iterable<Shape> globalCollisions) {
        this.blockCollisions = blockCollisions;
        this.globalCollisions = globalCollisions;
    }

    @Override
    public Iterator<Shape> iterator() {
        return new ShapeIterator(this.blockCollisions, this.globalCollisions);
    }

    private static final class ShapeIterator implements Iterator<Shape> {
        private final Iterator<Shape> blockCollisionsItr;
        private final Iterator<Shape> globalCollisionsItr;

        public ShapeIterator(final Iterable<Shape> blockCollisions, final Iterable<Shape> globalCollisions) {
            this.blockCollisionsItr = blockCollisions.iterator();
            this.globalCollisionsItr = globalCollisions.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.blockCollisionsItr.hasNext() || this.globalCollisionsItr.hasNext();
        }

        @Override
        public Shape next() {
            if (this.blockCollisionsItr.hasNext()) {
                return this.blockCollisionsItr.next();
            } else if (this.globalCollisionsItr.hasNext()) {
                return this.globalCollisionsItr.next();
            }
            return null;
        }
    }
}
