package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.data.DataKey;

public final class EntityDataKeys {
    public static final DataKey<Boolean> COPY = new DataKey<>("entity:is_copy");
    public static final DataKey<Boolean> REPEAT = new DataKey<>("components:repeat");
    public static final DataKey<String> NAME = new DataKey<>("entity:name");
    public static final DataKey<Boolean> STACKED = new DataKey<>("entity:name");
    public static final DataKey<Boolean> EXPLODED = new DataKey<>("entity:name");
}
