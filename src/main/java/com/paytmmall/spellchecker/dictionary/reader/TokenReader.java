package com.paytmmall.spellchecker.dictionary.reader;

import com.paytmmall.spellchecker.cache.CacheApi;
import org.apache.commons.lang3.Range;
import org.javatuples.Pair;

import java.io.IOException;

public interface TokenReader {
    public void read() throws IOException;
    
    public default void normaliserUtil(Range<Double> normalisedRange, Range<Double> range, CacheApi<String,
            Pair<Double,Double>> cacheApi) {
        for (String key : cacheApi.keySet()) {
            Pair<Double, Double> score = cacheApi.get(key);
            double fetchedValue = score.getValue0();
            double value = (
                    normalisedRange.getMaximum() - normalisedRange.getMinimum()
            ) * (
                    (fetchedValue - range.getMinimum()) / (range.getMaximum() - range.getMinimum())
            ) + normalisedRange.getMinimum();
            cacheApi.put(key, score.setAt1(value));
        }
    }
}