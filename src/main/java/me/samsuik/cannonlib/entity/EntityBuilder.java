package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.*;

public final class EntityBuilder {
    private Vec3d position = Vec3d.zero();
    private Vec3d momentum = Vec3d.zero();
    private final Set<Rotation> alignments = new HashSet<>();
    private final List<Component<Entity>> components = new ArrayList<>();

    public EntityBuilder position(final Vec3d position) {
        this.position = Objects.requireNonNull(position, "position cannot be null");
        return this;
    }

    public EntityBuilder momentum(final Vec3d momentum) {
        this.momentum = Objects.requireNonNull(momentum, "momentum cannot be null");
        return this;
    }

    public EntityBuilder alignment(final Rotation... alignments) {
        return this.alignment(Arrays.asList(alignments));
    }

    public EntityBuilder alignment(final List<Rotation> alignments) {
        for (final Rotation alignment : alignments) {
            final Rotation checkedAlignment = Objects.requireNonNull(alignment, "alignment cannot be null");
            final Rotation oppositeAlignment = checkedAlignment.getOpposite();

            if (this.alignments.contains(oppositeAlignment)) {
                throw new IllegalArgumentException("conflicting alignment (provided: %s, contains: %s)".formatted(checkedAlignment, oppositeAlignment));
            }

            this.alignments.add(checkedAlignment);
        }
        return this;
    }

    @SafeVarargs
    public final EntityBuilder components(final Component<Entity>... components) {
        return this.components(Arrays.asList(components));
    }

    public EntityBuilder components(final List<Component<Entity>> components) {
        for (final Component<Entity> component : components) {
            this.components.add(Objects.requireNonNull(component, "component cannot be null"));
        }
        return this;
    }

    public Entity build() {
        final Entity entity = new Entity();
        entity.position = this.position;
        entity.momentum = this.momentum;

        for (final Rotation alignment : this.alignments) {
            entity.align(alignment);
        }

        for (final Component<Entity> component : this.components) {
            entity.getComponents().addComponent(component);
        }

        return entity;
    }
}
