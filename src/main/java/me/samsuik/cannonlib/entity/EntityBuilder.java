package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.*;

public final class EntityBuilder {
    private Vec3d position = Vec3d.zero();
    private Vec3d momentum = Vec3d.zero();
    private final Map<Rotation, Block> alignments = new LinkedHashMap<>();
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
        final Map<Rotation, Block> blockAlignments = new LinkedHashMap<>();
        for (final Rotation rotation : alignments) {
            blockAlignments.put(rotation, Blocks.BEDROCK);
        }
        return this.alignment(blockAlignments);
    }

    public EntityBuilder alignment(final Map<Rotation, Block> alignments) {
        for (final Map.Entry<Rotation, Block> blockAlignment : alignments.entrySet()) {
            final Rotation alignment = Objects.requireNonNull(blockAlignment.getKey(), "alignment cannot be null");
            final Block block = Objects.requireNonNull(blockAlignment.getValue(), "block cannot be null");
            final Rotation oppositeAlignment = alignment.getOpposite();

            if (this.alignments.containsKey(oppositeAlignment)) {
                throw new IllegalArgumentException("conflicting alignment (provided: %s, contains: %s)".formatted(alignment, oppositeAlignment));
            }

            this.alignments.put(alignment, block);
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

        for (final Map.Entry<Rotation, Block> blockAlignment : this.alignments.entrySet()) {
            entity.align(blockAlignment.getKey(), blockAlignment.getValue());
        }

        entity.addAllComponents(this.components);
        return entity;
    }
}
