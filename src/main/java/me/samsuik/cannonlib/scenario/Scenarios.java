package me.samsuik.cannonlib.scenario;

import me.samsuik.cannonlib.physics.vec3.Vec3i;

public final class Scenarios {
    public static ClippingScenario clip(final Vec3i clipPos, final boolean checkWall) {
        return new ClippingScenario(clipPos, checkWall);
    }
}
