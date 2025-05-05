package me.samsuik.cannonlib;

import me.samsuik.cannonlib.physics.shape.Shape;

import java.util.Iterator;
import java.util.List;

public final class Collisions implements Iterable<Shape> {
    private final List<Shape> blockCollisions;
    private final List<Shape> globalCollisions;

    public Collisions(List<Shape> blockCollisions, List<Shape> globalCollisions) {
        this.blockCollisions = blockCollisions;
        this.globalCollisions = globalCollisions;
    }

    @Override
    public Iterator<Shape> iterator() {
        return new ShapeIterator(this.blockCollisions, this.globalCollisions);
    }

    private static final class ShapeIterator implements Iterator<Shape> {
        private final Iterator<Shape> iterator1;
        private final Iterator<Shape> iterator2;

        public ShapeIterator(final List<Shape> list1, final List<Shape> list2) {
            this.iterator1 = list1.iterator();
            this.iterator2 = list2.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator1.hasNext() || this.iterator2.hasNext();
        }

        @Override
        public Shape next() {
            if (this.iterator1.hasNext()) {
                return this.iterator1.next();
            } else if (this.iterator2.hasNext()) {
                return this.iterator2.next();
            }
            return null;
        }
    }
}
