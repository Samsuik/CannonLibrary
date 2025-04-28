package me.samsuik.cannonlib.world.component;

import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.physics.vec3.Vec3i;
import me.samsuik.cannonlib.world.World;

public final class WorldComponents {
    public static Component<World> clip(final Vec3i guiderPos, final boolean checkWall) {
        return clip(guiderPos, guiderPos, checkWall);
    }

    public static Component<World> clip(final Vec3i guiderPos, final Vec3i clipUpToPos, final boolean checkWall) {
        return new TestClipsComponent(guiderPos, clipUpToPos, checkWall).limit(1);
    }
}
