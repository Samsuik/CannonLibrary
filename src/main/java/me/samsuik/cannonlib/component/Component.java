package me.samsuik.cannonlib.component;

import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.data.KeyedDataStorageHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Component<U extends ComponentsHolder<?>> {
    AtomicInteger COUNTER = new AtomicInteger();

    boolean action(final U user, final int tick);

    static <U extends ComponentsHolder<?>> Component<U> self(final SimpleComponent<U> component) {
        return component;
    }

    static <U extends ComponentsHolder<?>> SimpleComponent<U> user(final Consumer<U> consumer) {
        return (user, tick) -> consumer.accept(user);
    }

    static <U extends ComponentsHolder<?>> Component<U> later(final int waitForTick, final Consumer<U> consumer) {
        return user(consumer).afterOrAtTick(waitForTick);
    }

    @SafeVarargs
    static <U extends ComponentsHolder<?>> List<Component<U>> concatLists(final List<Component<U>>... toJoin) {
        final List<Component<U>> joinedComponents = new ArrayList<>();
        for (final List<Component<U>> moreComponents : toJoin) {
            joinedComponents.addAll(moreComponents);
        }
        return joinedComponents;
    }

    static <U extends ComponentsHolder<?>> Component<U> wrap(final List<Component<U>> components) {
        return (user, tick) -> {
            boolean success = false;
            for (final Component<U> component : components) {
                success = component.action(user, tick);
            }
            return success;
        };
    }

    default Component<U> compose(final Component<U> component) {
        return this.condition(component::action);
    }

    default Component<U> and(final Component<U> component) {
        return component.condition(Component.this::action);
    }

    default Component<U> or(final Component<U> component) {
        return component.unless(Component.this::action);
    }

    default Component<U> beforeTick(final int expectedTick) {
        return this.condition((user, tick) -> tick < expectedTick);
    }

    default Component<U> afterOrAtTick(final int expectedTick) {
        return this.condition((user, tick) -> tick >= expectedTick);
    }

    default Component<U> atTick(final int expectedTick) {
        return this.condition((user, tick) -> tick == expectedTick);
    }

    default Component<U> unless(final Predicate<U> condition) {
        return this.unless((user, tick) -> condition.test(user));
    }

    default Component<U> unless(final BiPredicate<U, Integer> condition) {
        return this.condition((user, tick) -> !condition.test(user, tick));
    }

    default Component<U> condition(final Predicate<U> condition) {
        return this.condition((user, tick) -> condition.test(user));
    }

    default Component<U> condition(final BiPredicate<U, Integer> condition) {
        return (user, tick) -> condition.test(user, tick) && Component.this.action(user, tick);
    }

    default Component<U> repeat(final int times) {
        return (user, tick) -> {
            boolean nested = false;
            if (user instanceof KeyedDataStorageHolder holder) {
                nested = holder.putData(EntityDataKeys.REPEAT, true);
            }

            boolean success = true;
            for (int count = 0; count < times; ++count) {
                success &= Component.this.action(user, tick);
            }

            if (!nested && user instanceof KeyedDataStorageHolder holder) {
                holder.removeData(EntityDataKeys.REPEAT);
            }
            return success;
        };
    }

    default Component<U> limit(final int limit) {
        return new Component<>() {
            private int times = 0;

            @Override
            public boolean action(final U user, final int tick) {
                boolean success = false;
                if (this.times++ < limit) {
                    // user.removeCurrentComponent();
                    success = Component.this.action(user, tick);
                }
                return success;
            }
        };
    }
}
