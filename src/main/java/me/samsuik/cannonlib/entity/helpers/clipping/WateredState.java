package me.samsuik.cannonlib.entity.helpers.clipping;

public enum WateredState {
    WATERED("wet"),
    PARTIAL("dry below"),
    DRY("dry");

    private final String friendlyName;

    WateredState(final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public final String getFriendlyName() {
        return this.friendlyName;
    }
}
