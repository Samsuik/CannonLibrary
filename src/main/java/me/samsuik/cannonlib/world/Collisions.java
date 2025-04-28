package me.samsuik.cannonlib.world;

import me.samsuik.cannonlib.physics.shape.Shape;

import java.util.Iterator;
import java.util.List;

public final class Collisions implements Iterable<Shape> {
    private final ShapeIterator collisionItr;

    public Collisions(final List<Shape> globalCollisions, final List<Shape> blockCollisions) {
        this.collisionItr = new ShapeIterator(globalCollisions, blockCollisions);
    }

    @Override
    public Iterator<Shape> iterator() {
        return this.collisionItr;
    }

    private static final class ShapeIterator implements Iterator<Shape> {
        private final Iterator<Shape> iterator1;
        private final Iterator<Shape> iterator2;

        public ShapeIterator(final List<Shape> list1, final List<Shape> list3) {
            this.iterator1 = list1.iterator();
            this.iterator2 = list3.iterator();
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
