package com.paytmmall.spellchecker.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public interface CacheApi<K, V> {
    void put(K k, V v);

    V get(K k);

    void clearAll();

    void clear(K k);

    void initialLoad();

    void putAll(Map<K, V> map);

    ConcurrentMap<K, V> getAll();

    Set<K> keySet();
}
