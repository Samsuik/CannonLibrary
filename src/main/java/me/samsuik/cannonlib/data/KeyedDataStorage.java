package me.samsuik.cannonlib.data;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class KeyedDataStorage {
    private final Map<DataKey<?>, Object> storedData = new HashMap<>();

    public <T> T remove(final DataKey<T> key) {
        return (T) this.storedData.remove(key);
    }

    public <T> T put(final DataKey<T> key, final T obj) {
        return (T) this.storedData.put(key, obj);
    }

    public <T> T get(final DataKey<T> key) {
        return (T) this.storedData.get(key);
    }

    public <T> T getOrDefault(final DataKey<T> key, final T obj) {
        return (T) this.storedData.getOrDefault(key, obj);
    }
    public boolean containsKey(final DataKey<?> key) {
        return this.storedData.containsKey(key);
    }

    public void copyDataFrom(final KeyedDataStorage dataStorage) {
        this.storedData.putAll(dataStorage.storedData);
    }
}
