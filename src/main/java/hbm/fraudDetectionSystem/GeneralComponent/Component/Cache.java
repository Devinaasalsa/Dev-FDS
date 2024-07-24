package hbm.fraudDetectionSystem.GeneralComponent.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache<T> {
    public final Map<String, CacheObject<T>> cacheMap = new ConcurrentHashMap<>();

    public void put(String key, T value, long expirationTime) {
        long expirationTimeInMillis = expirationTime * 1000L;
        long currentTimeInMillis = System.currentTimeMillis();
        CacheObject<T> cacheObject = new CacheObject<>(value, currentTimeInMillis + expirationTimeInMillis);
        cacheMap.put(key, cacheObject);
    }

    public CacheObject<T> get(String key) {
        return cacheMap.get(key);
    }

    public void remove(String key) {
        cacheMap.remove(key);
    }

    public int size() {
        return cacheMap.size();
    }

    public boolean containsKey(String key) {
        return cacheMap.containsKey(key);
    }

    public void clear() {
        cacheMap.clear();
    }

    public boolean isEmpty() {
        return cacheMap.isEmpty();
    }

    public class CacheObject<T> {
        private T value;
        private long expirationTime;

        public CacheObject(T value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        public T getValue() {
            return value;
        }

        public long getExpirationTime() {
            return expirationTime;
        }

        public boolean isValid() {
            return expirationTime > System.currentTimeMillis();
        }
    }

}
