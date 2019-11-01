package com.paytmmall.spellchecker.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@Service
public class CatalogTokenCache implements CacheApi<String, Double> {

    private static final Cache<String, Double> cache = Caffeine.newBuilder()
            .build();

    @Override
    public void put(String s, Double v) {
        cache.put(s, v);
    }

    @Override
    public Double get(String s) {
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
    public void putAll(Map<String, Double> map) {
        cache.putAll(map);
    }

    @Override
    public ConcurrentMap<String, Double> getAll() {
        return cache.asMap();
    }

    @Override
    public Set<String> keySet(){
        ConcurrentMap<String, Double> stringGenericCacheDTOConcurrentMap =
                cache.asMap();
        return stringGenericCacheDTOConcurrentMap.keySet();
    }
}
