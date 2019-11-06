package com.paytmmall.spellchecker.dictionary.normaliser;

import com.paytmmall.spellchecker.cache.CacheApi;
import com.paytmmall.spellchecker.dictionary.Constants;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

public interface Normaliser {
    public void normalise() throws IOException;

    public default void normaliserUtil(Range<Double> range, CacheApi<String, Pair<Double,Double>> cacheApi) {
        for (String key : cacheApi.keySet()) {
            double fetchedValue = cacheApi.get(key);
            double value = (
                    Constants.CATALOG_TOKENS_RANGE_MAX - Constants.CATALOG_TOKENS_RANGE_MIN
            ) * (
                    (fetchedValue - range.getMinimum()) / (range.getMaximum() - range.getMinimum())
            ) + Constants.CATALOG_TOKENS_RANGE_MIN;
            cacheApi.put(key, value);
        }
    }
}