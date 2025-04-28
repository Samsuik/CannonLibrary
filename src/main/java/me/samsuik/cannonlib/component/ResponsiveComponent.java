package me.samsuik.cannonlib.component;

public interface ResponsiveComponent<U extends ComponentsHolder<U>> extends Component<U> {
    @Override
    default void action(final U user, final int tick) {
        this.success(user, tick);
    }

    @Override
    default boolean action0(final U user, final int tick) {
        return this.success(user, tick);
    }

    boolean success(final U user, final int tick);
}
