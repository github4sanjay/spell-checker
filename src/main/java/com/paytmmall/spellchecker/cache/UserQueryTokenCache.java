package com.paytmmall.spellchecker.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;


import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@Component
public class UserQueryTokenCache implements CacheApi<String, Pair<Double, Double>> {

    private static final Cache<String, Pair<Double, Double>> cache = Caffeine.newBuilder()
            .build();

    @Override
    public void put(String s, Pair<Double, Double> v) {
        cache.put(s, v);
    }

    @Override
    public Pair<Double, Double> get(String s) {
        return cache.getIfPresent(s);
    }

    @Override
    public void clearAll() {
        cache.invalidateAll();
    }

    @Override
    public void clear(String s) {
        cache.invalidate(s);
    }

    @Override
    public void initialLoad() {
    }

    @Override
    public void putAll(Map<String, Pair<Double, Double>> map) {
        cache.putAll(map);
    }

    @Override
    public ConcurrentMap<String, Pair<Double, Double>> getAll() {
        return cache.asMap();
    }

    @Override
    public Set<String> keySet() {
        ConcurrentMap<String, Pair<Double, Double>> stringGenericCacheDTOConcurrentMap =
                cache.asMap();
        return stringGenericCacheDTOConcurrentMap.keySet();
    }
}
