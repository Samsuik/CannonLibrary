package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.data.DataKey;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class EntityComponents {
    public static final Component<Entity> GRAVITY = (entity, tick) -> entity.momentum = entity.momentum.add(0.0, -0.04, 0.0);
    public static final Component<Entity> MOVE_ENTITY_WITH_COLLISION = new MovementComponent(0.98f, true);
    public static final Component<Entity> MOVE_ENTITY_WITHOUT_COLLISION = new MovementComponent(0.98f, false);
    public static final Component<Entity> FRICTION = (entity, tick) -> {
        if (entity.onGround) {
            entity.momentum = entity.momentum.mul(0.7, -0.5, 0.7);
        }
    };
    public static final Component<Entity> DRAG = (entity, tick) -> entity.momentum = entity.momentum.scale(0.98);
    public static final Component<Entity> CLONE = clone(1, 0);
    public static final Component<Entity> REMOVE = removeAtTick(0);
    public static final Component<Entity> REMOVE_AFTER_80_TICKS = removeAtTick(80);
    public static final Component<Entity> ENTITY_TICK_WITH_COLLISION = tick(true, 80);
    public static final Component<Entity> ENTITY_TICK_WITHOUT_COLLISION = tick(false, 80);
    public static final Component<Entity> LOGGER = logger("");

    public static Component<Entity> transform(final int runAt, final BiConsumer<Entity, Integer> transform) {
        return new TransformComponent(runAt, transform);
    }

    public static Component<Entity> cloneOne() {
        return clone(0, 1);
    }

    public static Component<Entity> cloneInclusive(final int runAt, final int amount) {
        return clone(runAt, amount - 1);
    }

    public static Component<Entity> clone(final int runAt, final int amount) {
        return new CloneComponent(runAt, amount);
    }

    public static Component<Entity> sand() {
        return fallingBlock(Blocks.SAND, 1);
    }

    public static Component<Entity> fallingBlock(final Block block, final int amount) {
        return new FallingBlockComponent(block, amount);
    }

    public static Component<Entity> explode() {
        return explode(0, 1, 0);
    }

    public static Component<Entity> explode(final int fuse, final int amount) {
        return explode(fuse, amount, 0);
    }

    public static Component<Entity> explode(final int fuse, final int amount, final int flags) {
        return new ExplodeComponent(fuse, amount, flags);
    }

    public static Component<Entity> removeAtTick(final int when) {
        return Component.later(when, Entity::remove);
    }

    public static Component<Entity> tick(final boolean collision, final int removeAfter) {
        final Component<Entity> movement = collision ? MOVE_ENTITY_WITH_COLLISION : MOVE_ENTITY_WITHOUT_COLLISION;
        final Component<Entity> removal = removeAfter != 80 ? removeAtTick(removeAfter) : REMOVE_AFTER_80_TICKS;
        return Component.wrap(List.of(GRAVITY, movement, FRICTION, DRAG, removal));
    }

    public static <T> Component<Entity> data(final DataKey<T> key, final T obj) {
        return Component.<Entity>user(entity -> entity.putData(key, obj)).limit(1);
    }

    public static Component<Entity> logger(final String name, final Object... ex) {
        return logger(entity -> List.of(name), ex);
    }

    public static Component<Entity> logger(final Function<Entity, List<String>> entityInfo, final Object... ex) {
        return new LoggerComponent(entityInfo, ex);
    }
}
