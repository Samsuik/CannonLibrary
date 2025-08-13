package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.data.DataKey;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

public final class EntityDataKeys {
    public static final DataKey<Boolean> TNT = DataKey.namespace("entity", "tnt");
    public static final DataKey<Boolean> FALLING_BLOCK = DataKey.namespace("entity", "falling_block");
    public static final DataKey<Float> FALLEN = DataKey.namespace("entity", "fallen");
    public static final DataKey<Vec3d> STUCK_SPEED = DataKey.namespace("entity", "stuck_speed");
    public static final DataKey<Float> SIZE = DataKey.namespace("entity", "size");
    public static final DataKey<Boolean> REPEAT = DataKey.namespace("components", "repeat");
    public static final DataKey<Boolean> COPY = DataKey.namespace("entity", "is_copy");
    public static final DataKey<String> NAME = DataKey.namespace("entity", "name");
    public static final DataKey<Integer> STACKED = DataKey.namespace("entity", "stacked");
    public static final DataKey<Integer> EXPLODED = DataKey.namespace("entity", "exploded");
    public static final DataKey<Double> HAMMER_RATIO = DataKey.namespace("entity", "hammer_ratio");
}
