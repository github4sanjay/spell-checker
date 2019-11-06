package com.paytmmall.spellchecker.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;


@Component
public class OrderedSubsets implements CacheApi<Integer, String[]> {

    private static final Cache<Integer, String[]> cache = Caffeine.newBuilder()
            .build();

    @Override
    public void put(Integer s, String[] v) {
        cache.put(s, v);
    }

    @Override
    public String[] get(Integer s) {
        return cache.getIfPresent(s);
    }

    @Override
    public void clearAll() {
        cache.invalidateAll();
    }

    @Override
    public void clear(Integer s) {
        cache.invalidate(s);
    }

    @Override
    public void initialLoad() {
    }

    @Override
    public void putAll(Map<Integer, String[]> map) {
        cache.putAll(map);
    }

    @Override
    public ConcurrentMap<Integer, String[]> getAll() {
        return cache.asMap();
    }

    @Override
    public Set<Integer> keySet() {
        ConcurrentMap<Integer, String[]> stringGenericCacheDTOConcurrentMap =
                cache.asMap();
        return stringGenericCacheDTOConcurrentMap.keySet();
    }
}
