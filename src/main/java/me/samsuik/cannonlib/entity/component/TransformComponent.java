package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.component.SimpleComponent;
import me.samsuik.cannonlib.entity.Entity;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public final class TransformComponent implements SimpleComponent<Entity> {
    private static final AtomicInteger TRANSFORM_COUNTER = new AtomicInteger();
    private final BiConsumer<Entity, Integer> transformEntity;

    public TransformComponent(final BiConsumer<Entity, Integer> transform) {
        this.transformEntity = transform;
    }

    @Override
    public void action0(final Entity entity, final int tick) {
        this.transformEntity.accept(entity, TRANSFORM_COUNTER.getAndIncrement());
    }
}
