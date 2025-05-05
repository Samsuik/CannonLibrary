package me.samsuik.cannonlib;

import me.samsuik.cannonlib.entity.Entity;

import java.util.*;
import java.util.function.Predicate;

public final class EntityList extends AbstractCollection<Entity> {
    private Entity[] entities = new Entity[0];
    private int size;
    private boolean ticking;

    private int indexOf(final Entity entity) {
        int index = this.size;
        final Entity[] entities = this.entities;
        for (;;) {
            if (--index == -1 || entities[index] == entity) {
                return index;
            }
        }
    }

    private void ensureCapacity(final int size) {
        if (size > this.entities.length) {
            this.entities = Arrays.copyOf(this.entities, size);
        } else {
            this.copyArrayWhenTicking();
        }
    }

    private void set(final int index, final Entity entity) {
        this.ensureCapacity(index + 1);
        this.size++;
        this.entities[index] = entity;
    }

    public void addAllAfter(final Entity entity, final Collection<Entity> entities) {
        final int index = this.indexOf(entity);
        if (index == -1) {
            throw new IllegalArgumentException("unknown entity");
        }

        final int size = this.size;
        this.ensureCapacity(size + entities.size());

        int nextIndex = index + 1;
        if (size != nextIndex) {
            System.arraycopy(this.entities, nextIndex, this.entities, nextIndex + entities.size(), size - nextIndex);
        }

        for (final Entity otherEntity : entities) {
            this.set(nextIndex++, otherEntity);
        }
    }

    @Override
    public boolean add(final Entity entity) {
        this.set(this.size, entity);
        return true;
    }

    public void remove(final Entity entity) {
        final int index = this.indexOf(entity);
        if (index >= 0) {
            this.copyArrayWhenTicking();

            final Entity[] entities = this.entities;
            final int newSize = this.size - 1;

            if (newSize != index) {
                System.arraycopy(entities, index + 1, entities, index, newSize - index);
            }

            this.size = newSize;
            entities[newSize] = null;
        }
    }

    @Override
    public boolean removeIf(final Predicate<? super Entity> predicate) {
        this.copyArrayWhenTicking();
        final Entity[] entities = this.entities;
        final int size = this.size;

        int head = -1;
        for (int index = 0; index < size; ++index) {
            final Entity entity = entities[index];
            if (predicate.test(entity)) {
                continue;
            }
            entities[++head] = entity;
        }

        for (int index = ++head; index < size; ++index) {
            entities[index] = null;
        }

        this.size = head;
        return size != head;
    }

    private void copyArrayWhenTicking() {
        if (this.ticking) {
            this.entities = Arrays.copyOf(this.entities, this.entities.length);
            this.ticking = false;
        }
    }

    public void setTicking(final boolean ticking) {
        this.ticking = ticking;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<Entity> iterator() {
        return new EntityIterator(this.entities, this.size);
    }

    private static final class EntityIterator implements Iterator<Entity> {
        private final Entity[] entities;
        private final int size;
        private int index = 0;

        private EntityIterator(final Entity[] entities, final int size) {
            this.entities = entities;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return this.index < this.size;
        }

        @Override
        public Entity next() {
            return this.entities[this.index++];
        }
    }
}
