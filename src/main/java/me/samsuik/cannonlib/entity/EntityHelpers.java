package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.List;

public final class EntityHelpers {
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
        components.forEach(entity::addComponent);
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
