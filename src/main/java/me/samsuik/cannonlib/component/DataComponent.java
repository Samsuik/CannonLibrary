package me.samsuik.cannonlib.component;

public interface DataComponent<U extends ComponentsHolder<?>, T> extends Component<U> {
    @Override
    default boolean action(U user, int tick) {
        return this.data(user, tick, null) != null;
    }

    T data(final U user, final int tick, final T value);

    default DataComponent<U, T> pass(final DataComponent<U, T>... components) {
        return (u, t, v) -> {
            T val = v;
            for (final DataComponent<U, T> component : components) {
                val = component.data(u, t, v);
            }
            return val;
        };
    }
}
