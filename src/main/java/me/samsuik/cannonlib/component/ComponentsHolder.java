package me.samsuik.cannonlib.component;

import java.util.List;

public interface ComponentsHolder<U extends ComponentsHolder<?>> {
    default void addAllComponents(final List<Component<U>> components) {
        this.getComponents().addAllComponents(components);
    }

    default void addComponent(final Component<U> component) {
        this.getComponents().addComponent(component);
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
