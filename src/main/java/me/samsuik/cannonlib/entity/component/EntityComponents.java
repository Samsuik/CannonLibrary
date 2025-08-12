package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.component.SimpleComponent;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.data.DataKey;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.explosion.Explosion;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class EntityComponents {
    public static final SimpleComponent<Entity> GRAVITY = (entity, tick) -> entity.momentum = entity.momentum.add(0.0, -0.04, 0.0);
    public static final SimpleComponent<Entity> MOVE_ENTITY_WITH_COLLISION = new MovementComponent(0.98f, true);
    public static final SimpleComponent<Entity> MOVE_ENTITY_WITHOUT_COLLISION = new MovementComponent(0.98f, false);
    public static final SimpleComponent<Entity> FRICTION = (entity, tick) -> {
        if (entity.onGround) {
            entity.momentum = entity.momentum.mul(0.7, -0.5, 0.7);
        }
    };
    public static final SimpleComponent<Entity> DRAG = (entity, tick) -> entity.momentum = entity.momentum.scale(0.98);
    public static final Component<Entity> CLONE = clone(1, 0);
    public static final Component<Entity> REMOVE = removeAtTick(0);
    public static final Component<Entity> REMOVE_AFTER_80_TICKS = removeAtTick(80);
    public static final Component<Entity> ENTITY_TICK_WITH_COLLISION = tick(true, 80);
    public static final Component<Entity> ENTITY_TICK_WITHOUT_COLLISION = tick(false, 80);
    public static final Component<Entity> LOGGER = entityLogger(entity -> List.of(entity.getDataOrDefault(EntityDataKeys.NAME, "")));

    public static HammerRatioComponent hammerRatio() {
        return hammerRatio(100);
    }

    public static HammerRatioComponent hammerRatio(final int hammerTnt) {
        return new HammerRatioComponent(hammerTnt);
    }

    public static SimpleComponent<Entity> removeEntities() {
        return Component.user(entity -> {
            final World world = entity.getWorld();
            final List<Entity> entities = new ArrayList<>(world.getEntityList());
            for (final Entity otherEntity : entities) {
                if (otherEntity.shouldRemove()) {
                    continue;
                }
                if (otherEntity != entity) {
                    world.removeEntity(otherEntity);
                }
                otherEntity.remove();
            }
        });
    }

    public static Component<Entity> transform(final int runAt, final BiConsumer<Entity, Integer> transform) {
        return new TransformComponent(transform).atTick(runAt);
    }

    public static Component<Entity> cloneOne() {
        return clone(0, 1);
    }

    public static Component<Entity> cloneInclusive(final int runAt, final int amount) {
        return clone(runAt, amount - 1);
    }

    public static Component<Entity> clone(final int runAt, final int amount) {
        return new CloneComponent(amount).once().afterOrAtTick(runAt);
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
        return new ExplodeComponent(amount, flags).afterOrAtTick(fuse);
    }

    public static Component<Entity> impact(final int runAt, final Vec3d source, final int amount) {
        return impact(source, amount).atTick(runAt);
    }

    public static Component<Entity> impact(final Vec3d source, final int amount) {
        return Component.user(entity -> entity.momentum.add(Explosion.impact(entity.position, source).scale(amount)));
    }

    public static Component<Entity> removeAtTick(final int when) {
        return Component.later(when, Entity::remove);
    }

    public static Component<Entity> tick(final boolean collision, final int removeAfter) {
        final Component<Entity> movement = collision ? MOVE_ENTITY_WITH_COLLISION : MOVE_ENTITY_WITHOUT_COLLISION;
        final Component<Entity> removal = removeAfter != 80 ? removeAtTick(removeAfter) : REMOVE_AFTER_80_TICKS;
        return Component.wrap(List.of(GRAVITY, movement, FRICTION, DRAG, removal));
    }

    public static Component<Entity> tickTnt(final boolean collision, final int fuse, final int amount) {
        final Component<Entity> tick = collision ? ENTITY_TICK_WITH_COLLISION : ENTITY_TICK_WITHOUT_COLLISION;
        return Component.wrap(List.of(tick, explode(fuse, amount)));
    }

    public static Component<Entity> tickFallingBlock(final boolean collision, final Block type, final int amount) {
        final Component<Entity> tick = collision ? ENTITY_TICK_WITH_COLLISION : ENTITY_TICK_WITHOUT_COLLISION;
        return Component.wrap(List.of(tick, fallingBlock(type, amount)));
    }

    public static Component<Entity> name(final String entityName) {
        return spawnWithData(EntityDataKeys.NAME, entityName);
    }

    public static <T> Component<Entity> spawnWithData(final DataKey<T> key, final T obj) {
        return Component.<Entity>user(entity -> entity.putData(key, obj)).atTick(0);
    }

    public static <T> Component<Entity> data(final DataKey<T> key, final Function<Entity, T> func) {
        return Component.user(entity -> entity.putData(key, func.apply(entity)));
    }

    public static Component<Entity> entityLogger(final String name, final Object... ex) {
        return entityLogger(entity -> List.of(name), ex);
    }

    public static Component<Entity> entityLogger(final Function<Entity, List<Object>> entityInfo, final Object... ex) {
        return new LoggerComponent(entityInfo, Arrays.asList(ex), true);
    }

    public static Component<Entity> logger(final Object... ex) {
        return logger(entity -> List.of(), ex);
    }

    public static Component<Entity> logger(final Function<Entity, List<Object>> entityInfo, final Object... ex) {
        return new LoggerComponent(entityInfo, Arrays.asList(ex), false);
    }
}
