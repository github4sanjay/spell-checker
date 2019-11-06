package com.paytmmall.spellchecker.dictionary.normaliser;

import com.paytmmall.spellchecker.cache.CacheApi;
import org.apache.commons.lang3.Range;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Normaliser {
    public void normaliserUtil(Range<Double> normalisedRange, Range<Double> range, CacheApi<String,
            Pair<Double, Double>> cacheApi) {
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