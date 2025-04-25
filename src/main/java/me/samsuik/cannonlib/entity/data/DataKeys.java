package me.samsuik.cannonlib.entity.data;

import me.samsuik.cannonlib.entity.helpers.CannonRatio;

public final class DataKeys {
    public static final DataKey<Boolean> COPY = new DataKey<>("data:copy");
    public static final DataKey<Boolean> REPEAT = new DataKey<>("components:repeat");
    public static final DataKey<CannonRatio.RatioEntityData> CANNON_RATIO_DATA_KEY = new DataKey<>("cannon-ratio:data");
}
