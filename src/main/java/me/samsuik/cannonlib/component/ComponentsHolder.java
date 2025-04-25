package me.samsuik.cannonlib.component;

public interface ComponentsHolder<U extends ComponentsHolder<?>> {
    default void addComponent(final Component<U> component) {
        this.getComponents().addComponent(component);
    }

    default void removeCurrentComponent() {
        this.getComponents().removeCurrentComponent();
    }

    default void removeComponent(final Component<?> component) {
        this.getComponents().removeComponent(component);
    }

    default boolean hasComponent(final Component<U> component) {
        return this.getComponents().hasComponent(component);
    }

    default void tickComponents(final U user) {
        this.getComponents().tickComponents(user, -1);
    }

    Components<U> getComponents();
}
