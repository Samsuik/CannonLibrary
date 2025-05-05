package me.samsuik.cannonlib.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Components<U extends ComponentsHolder<?>> {
    private List<Component<U>> components = new ArrayList<>();
    private int tick = 0;
    private int componentIndex = -1;
    private boolean ticking = false;
    private boolean dirty = false;
    private boolean removed = false;

    private void copyForWrite() {
        if (this.dirty) {
            this.components = new ArrayList<>(this.components);
            this.dirty = false;
        }
    }

    public void addComponent(final Component<U> component) {
        this.copyForWrite();
        this.components.add(component);
    }

    public void removeCurrentComponent() {
        if (!this.ticking) {
            throw new IllegalStateException("Not currently ticking any components");
        } else if (!this.removed) {
            return;
        }

        final int executingIndex = this.componentIndex + 1;
        final Component<U> component = this.components.get(executingIndex);
        if (component != null) {
            this.removeComponent(component);
            this.removed = true;
            this.componentIndex--;
        }
    }

    public void removeComponent(final Component<?> component) {
        this.dirty |= this.ticking; // don't interfere while ticking
        this.copyForWrite();
        this.components.remove(component);
    }

    public boolean hasComponent(final Component<?> component) {
        return this.components.contains(component);
    }

    public List<Component<U>> components() {
        return Collections.unmodifiableList(this.components);
    }

    public void copyComponentsFrom(final Components<U> another) {
        this.dirty = another.dirty = true;
        this.tick = another.tick;
        this.components = another.components;
    }

    public void continueTicking(final U user, final Components<U> components) {
        this.tickComponents(user, components.componentIndex + 1);
    }

    public void tickComponents(final U user, final int start) {
        final boolean recursive = this.ticking;
        final int currentTick = this.tick;
        final int componentIndex = this.componentIndex;
        this.ticking = true;
        this.dirty = true;

        for (final Component<U> component : this.components) {
            this.removed = false;
            if (this.componentIndex >= start) {
                component.action(user, tick);
            }
            this.componentIndex++;
        }

        if (!recursive) {
            this.tick = currentTick + 1;
            this.ticking = false;
            this.componentIndex = -1;
        } else {
            // restore component index
            this.componentIndex = componentIndex;
        }
    }
}
