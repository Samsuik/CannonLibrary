package me.samsuik.cannonlib.world;

import me.samsuik.cannonlib.entity.Entity;

import java.util.*;
import java.util.function.Predicate;

public final class EntityList extends AbstractCollection<Entity> {
    private Entity[] entities = new Entity[0];
    private final Map<Entity, Integer> entityIndexMap = new IdentityHashMap<>();
    private int size;
    private volatile boolean ticking;

    private int indexOf(final Entity entity) {
        return this.entityIndexMap.getOrDefault(entity, -1);
    }

    private void ensureCapacity(final int size) {
        if (size > this.entities.length) {
            this.entities = Arrays.copyOf(this.entities, size);
        } else {
            this.copyArrayWhenTicking();
        }
    }

    private boolean setToIndex(final int index, final Entity entity) {
        final Integer currentIndex = this.entityIndexMap.putIfAbsent(entity, index);
        if (currentIndex != null) {
            this.ensureCapacity(index + 1);
            this.size++;
            this.entities[index] = entity;
            return true;
        }
        return false;
    }

    public void addAllAfter(final Entity entity, final Collection<Entity> entities) {
        int index = this.indexOf(entity);
        if (index == -1) {
            throw new IllegalArgumentException("entity is not inside this list");
        }

        this.ensureCapacity(this.size + entities.size());
        System.arraycopy(this.entities, index + 1, this.entities, index + entities.size(), this.size - index + 1);

        for (final Entity otherEntity : entities) {
            this.setToIndex(++index, otherEntity);
        }
    }

    @Override
    public boolean add(final Entity entity) {
        return this.setToIndex(this.size, entity);
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
            this.entityIndexMap.remove(entity);
        }
    }

    @Override
    public boolean removeIf(final Predicate<? super Entity> predicate) {
        final Entity[] entities = this.entities;
        final int size = this.size;
        int head = -1;
        for (int index = 0; index < size; ++index) {
            final Entity entity = entities[index];
            if (predicate.test(entity) || ++head == index) {
                continue;
            }
            entities[head] = entity;
        }
        for (int index = ++head; index < size; ++index) {
            entities[index] = null;
        }
        this.size = head;
        return size != head;
    }

    private void copyArrayWhenTicking() {
        if (this.ticking) {
            this.entities = Arrays.copyOf(this.entities, this.size);
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
        private int index = -1;

        private EntityIterator(final Entity[] entities, final int size) {
            this.entities = entities;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return ++this.index < this.size;
        }

        @Override
        public Entity next() {
            return this.entities[this.index];
        }
    }
}
