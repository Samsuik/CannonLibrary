package me.samsuik.cannonlib.component;

public interface SimpleComponent<U extends ComponentsHolder<?>> extends Component<U> {
    @Override
    default boolean action(final U user, final int tick) {
        this.action0(user, tick);
        return true;
    }

    void action0(final U user, final int tick);
}
