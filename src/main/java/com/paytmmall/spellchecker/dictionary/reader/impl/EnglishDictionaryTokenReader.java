package com.paytmmall.spellchecker.dictionary.reader.impl;

import com.paytmmall.spellchecker.cache.CacheApi;
import com.paytmmall.spellchecker.cache.EnglishDictionaryCache;
import com.paytmmall.spellchecker.dictionary.Constants;
import com.paytmmall.spellchecker.dictionary.reader.TokenReader;
import com.paytmmall.spellchecker.metrics.MetricsAgent;
import com.paytmmall.spellchecker.util.FilterKeywordsUtil;
import com.paytmmall.spellchecker.util.ResourceUtil;
import org.apache.commons.lang3.Range;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class EnglishDictionaryTokenReader implements TokenReader {
    private static final Logger logger = LoggerFactory.getLogger(EnglishDictionaryTokenReader.class);

    @Value("${input.file.location}")
    private String inputFileLocation;

    @Value("${english.file.name}")
    private String inputFileName;

    @Autowired
    private EnglishDictionaryCache englishDictionaryCache;

    @Autowired
    private MetricsAgent metricsAgent;

    @Override
    public void read() throws IOException {
        Range<Double> range = this.getRange(inputFileLocation + "/" + inputFileName,
                this.englishDictionaryCache);
        this.normaliserUtil(
                Range.between(Constants.ENGLISH_DICTIONARY_RANGE_MIN, Constants.ENGLISH_DICTIONARY_RANGE_MAX),
                range,
                this.englishDictionaryCache
        );
        logger.info("English dictionary tokens file write complete");
    }

    private Range<Double> getRange(String filePath, CacheApi<String, Pair<Double, Double>> cacheApi) throws IOException {
        File file = ResourceUtil.getFile(inputFileLocation + "/" + inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = "";
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;
        double count = 0.0;
        while ((st = br.readLine()) != null) {
            String[] temp_row = st.split(" "); // row format assuming keyword count
            int len = temp_row.length;

            if (len < 2) {
                metricsAgent.recordMetricsEvents("invalid_file_row_english_dictionary");
                logger.warn("invalid row format for English Dictionary file ", st);
                continue;
            }

            String key = temp_row[len - 2];
            double existingValue =0.0;
            key = key.trim();
            key = key.toLowerCase();

            if (FilterKeywordsUtil.isStopWord(key) || FilterKeywordsUtil.isWHiteListedToken(key)) continue;

            try {
                count = Double.parseDouble(temp_row[len - 1]);
            } catch (Exception e) {
                metricsAgent.recordMetricsEvents("invalid_file_row_english_dictionary");
                logger.error("invalid row values for English Dictionary file ", e);
                continue;
            }
            Pair<Double, Double> score = englishDictionaryCache.get(key);
            if (score != null) {
                existingValue = score.getValue0();
                count += existingValue;
                score = score.setAt0(count);
            }else {
                score = Pair.with(count, null);
            }
            minimum = Math.min(count, minimum);
            maximum = Math.max(count, maximum);
            englishDictionaryCache.put(key, score);
        }

        logger.info("English Dictionary file has minimum value of {}", minimum);
        logger.info("English Dictionary file has maximum value of {}", maximum);

        return Range.between(minimum, maximum);
    }
}

