package me.samsuik.cannonlib.data;

@SuppressWarnings("unused")
public record DataKey<T>(String key) {
    public static <T> DataKey<T> namespace(final String parent, final String name) {
        return new DataKey<>(parent + ":" + name);
    }
}
