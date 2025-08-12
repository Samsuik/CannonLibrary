package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.component.EntityComponents;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class EntityHelpers {
    public static Entity withComponents(final List<Component<Entity>> components) {
        return create(entity -> {}, components);
    }

    public static Entity create(final Consumer<Entity> consumer, final List<Component<Entity>> components) {
        final Entity entity = new Entity();
        consumer.accept(entity);
        entity.addAllComponents(components);
        return entity;
    }

    @SafeVarargs
    public static Entity createTnt(
            final Vec3d position,
            final int fuse,
            final int amount,
            final Component<Entity>... extraComponents
    ) {
        final List<Component<Entity>> components = List.of(
                EntityComponents.tick(true, 80),
                EntityComponents.explode(fuse, amount)
        );
        return create(
                entity -> entity.position = position,
                Component.concatLists(components, Arrays.asList(extraComponents))
        );
    }

    @SafeVarargs
    public static Entity createFallingBlock(
            final Vec3d position,
            final Block block,
            final int amount,
            final Component<Entity>... extraComponents
    ) {
        final List<Component<Entity>> components = List.of(
                EntityComponents.tick(true, 80),
                EntityComponents.fallingBlock(block, amount)
        );
        return create(
                entity -> entity.position = position,
                Component.concatLists(components, Arrays.asList(extraComponents))
        );
    }

    public static Entity createFromCannonDebugString(
            final String positionString,
            final String momentumString,
            final List<Component<Entity>> components
    ) {
        return createFromCannonDebugString(positionString + " " + momentumString, components);
    }

    public static Entity createFromCannonDebugString(final String debugString, final List<Component<Entity>> components) {
        final Entity entity = new Entity();
        entity.position = vec3dFromDebugString(debugString, 0);
        entity.momentum = vec3dFromDebugString(debugString, 3);
        entity.addAllComponents(components);
        return entity;
    }

    private static Vec3d vec3dFromDebugString(final String debugString, final int in) {
        final String[] parts = debugString.split(" ");
        final double x = Double.parseDouble(parts[in]);
        final double y = Double.parseDouble(parts[in+1]);
        final double z = Double.parseDouble(parts[in+2]);
        return new Vec3d(x, y, z);
    }
}
