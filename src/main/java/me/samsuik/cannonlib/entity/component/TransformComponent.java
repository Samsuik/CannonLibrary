package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public final class TransformComponent implements Component<Entity> {
    private static final AtomicInteger COUNTER = new AtomicInteger();
    private final int transformAtTick;
    private final BiConsumer<Entity, Integer> transformEntity;

    public TransformComponent(final int transformAtTick, final BiConsumer<Entity, Integer> transform) {
        this.transformAtTick = transformAtTick;
        this.transformEntity = transform;
    }

    @Override
    public void action(final Entity entity, final int tick) {
        if (tick >= this.transformAtTick) {
            entity.removeCurrentComponent();
            this.transformEntity.accept(entity, COUNTER.getAndIncrement());
        }
    }
}
