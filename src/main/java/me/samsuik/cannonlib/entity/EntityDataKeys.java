package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.data.DataKey;

public final class EntityDataKeys {
    public static final DataKey<Boolean> REPEAT = new DataKey<>("components:repeat");
    public static final DataKey<Boolean> COPY = new DataKey<>("entity:is_copy");
    public static final DataKey<String> NAME = new DataKey<>("entity:name");
    public static final DataKey<Integer> STACKED = new DataKey<>("entity:stacked");
    public static final DataKey<Integer> EXPLODED = new DataKey<>("entity:exploded");
    public static final DataKey<Double> HAMMER_RATIO = new DataKey<>("entity:hammer_ratio");
}
