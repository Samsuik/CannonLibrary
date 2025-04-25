package me.samsuik.cannonlib.entity.data;

public interface KeyedDataStorageHolder {
    default <T> T removeData(final DataKey<T> key) {
        return this.getData().remove(key);
    }

    default <T> T putData(final DataKey<T> key, final T obj) {
        return this.getData().put(key, obj);
    }

    default <T> T getData(final DataKey<T> key) {
        return this.getData().get(key);
    }

    default <T> T getDataOrDefault(final DataKey<T> key, final T obj) {
        return this.getData().getOrDefault(key, obj);
    }

    default boolean hasData(final DataKey<?> key) {
        return this.getData().containsKey(key);
    }

    KeyedDataStorage getData();
}
