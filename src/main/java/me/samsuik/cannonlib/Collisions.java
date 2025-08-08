package me.samsuik.cannonlib;

import me.samsuik.cannonlib.physics.shape.Shape;

import java.util.Iterator;
import java.util.List;

public final class Collisions implements Iterable<Shape> {
    private final List<Shape> blockCollisions;
    private final List<Shape> globalCollisions;

    public Collisions(final List<Shape> blockCollisions, final List<Shape> globalCollisions) {
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

        public ShapeIterator(final List<Shape> blockCollisions, final List<Shape> globalCollisions) {
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
