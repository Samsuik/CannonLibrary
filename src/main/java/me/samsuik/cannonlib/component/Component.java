package me.samsuik.cannonlib.component;

import me.samsuik.cannonlib.entity.data.DataKeys;
import me.samsuik.cannonlib.entity.data.KeyedDataStorageHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Component<U extends ComponentsHolder<?>> {
    default boolean action0(final U user, final int tick) {
        this.action(user, tick);
        return true;
    }

    void action(final U user, final int tick);

    static <U extends ComponentsHolder<?>> Component<U> self(final Component<U> component) {
        return component;
    }

    static <U extends ComponentsHolder<?>> Component<U> user(final Consumer<U> consumer) {
        return (user, tick) -> consumer.accept(user);
    }

    static <U extends ComponentsHolder<?>> Component<U> later(final int waitForTick, final Consumer<U> consumer) {
        return user(consumer).afterOrAtTick(waitForTick).limit(1);
    }

    @SafeVarargs
    static <U extends ComponentsHolder<?>> List<Component<U>> concatLists(final List<Component<U>>... toJoin) {
        final List<Component<U>> joinedComponents = new ArrayList<>();
        for (final List<Component<U>> moreComponents : toJoin) {
            joinedComponents.addAll(moreComponents);
        }
        return joinedComponents;
    }

    static <U extends ComponentsHolder<?>> Component<U> join(final List<Component<U>> components) {
        if (components.isEmpty()) {
            throw new IllegalArgumentException("empty list");
        }
        Component<U> component = components.getFirst();
        for (final Component<U> otherComponent : components) {
            if (otherComponent != component) {
                component = component.and(otherComponent);
            }
        }
        return component;
    }

    static <U extends ComponentsHolder<?>> Component<U> wrap(final List<Component<U>> components) {
        return (user, tick) -> {
            for (final Component<U> component : components) {
                component.action(user, tick);
            }
        };
    }

    default Component<U> compose(final Component<U> component) {
        return this.condition(component::action0);
    }

    default Component<U> and(final Component<U> component) {
        return component.condition(Component.this::action0);
    }

    default Component<U> or(final Component<U> component) {
        return component.unless(Component.this::action0);
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
        return new Component<>() {
            @Override
            public void action(final U user, final int tick) {
                this.action0(user, tick);
            }

            @Override
            public boolean action0(final U user, final int tick) {
                return condition.test(user, tick) && Component.this.action0(user, tick);
            }
        };
    }

    default Component<U> repeat(final int times) {
        return new Component<>() {
            @Override
            public void action(final U user, final int tick) {
                this.action0(user, tick);
            }

            @Override
            public boolean action0(final U user, final int tick) {
                boolean nested = false;
                if (user instanceof KeyedDataStorageHolder holder) {
                    nested = holder.putData(DataKeys.REPEAT, true);
                }

                boolean success = true;
                for (int count = 0; count < times; ++count) {
                    success &= Component.this.action0(user, tick);
                }

                if (!nested && user instanceof KeyedDataStorageHolder holder) {
                    holder.removeData(DataKeys.REPEAT);
                }
                return success;
            }
        };
    }

    default Component<U> limit(final int limit) {
        return new Component<>() {
            private int ticks = 0;

            @Override
            public void action(final U user, final int tick) {
                if (this.ticks++ < limit) {
                    Component.this.action(user, tick);
                }
                // todo: see comment in Components
                if (this.ticks >= limit) {
                    user.removeCurrentComponent();
                }
            }
        };
    }
}
