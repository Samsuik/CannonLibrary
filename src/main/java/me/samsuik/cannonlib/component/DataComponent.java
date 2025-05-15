package me.samsuik.cannonlib.component;

import java.util.List;
import java.util.function.Function;

public interface DataComponent<U extends ComponentsHolder<?>, T> extends Component<U> {
    @Override
    default boolean action(U user, int tick) {
        return this.data(user, tick, null) != null;
    }

    T data(final U user, final int tick, final T value);

    default DataComponent<U, T> run(final DataComponentValueConsumer<U, T> valueConsumer) {
        return this.and((u, t, v) -> {
            valueConsumer.consume(u, t, v);
            return v;
        });
    }

    default DataComponent<U, T> and(final DataComponent<U, T> component) {
        return (u, t, v) -> {
            T val = DataComponent.this.data(u, t, v);
            if (val != null) {
                val = component.data(u, t, val);
            }
            return val;
        };
    }

    default DataComponent<U, T> then(final CreateComponentsFunction<U, T> components) {
        return (u, t, v) -> {
            final Function<T, List<Component<U>>> func = val -> components.create(u, t, val);
            return this.then(func).data(u, t, v);
        };
    }

    default DataComponent<U, T> then(final Function<T, List<Component<U>>> components) {
        return (u, t, v) -> {
            T val = DataComponent.this.data(u, t, v);
            if (val == null) {
                return null;
            }

            for (final Component<U> component : components.apply(val)) {
                if (val == null) {
                    continue;
                }

                if (component instanceof DataComponent<?,?> dataComponent) {
                    final DataComponent<U, T> unchecked = (DataComponent<U, T>) dataComponent;
                    val = unchecked.data(u, t, val);
                } else {
                    component.action(u, t);
                }
            }

            return val;
        };
    }

    interface CreateComponentsFunction<U extends ComponentsHolder<?>, T> {
        List<Component<U>> create(final U user, final int tick, final T value);
    }

    interface DataComponentValueConsumer<U extends ComponentsHolder<?>, T> {
        void consume(final U user, final int tick, final T value);
    }
}
