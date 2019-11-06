package com.paytmmall.spellchecker.dictionary.reader.impl;

import com.paytmmall.spellchecker.cache.CacheApi;
import com.paytmmall.spellchecker.cache.CatalogTokenCache;
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
public class CatalogTokensTokenReader implements TokenReader {

    private static final Logger logger = LoggerFactory.getLogger(CatalogTokensTokenReader.class);

    @Value("${input.file.location}")
    private String inputFileLocation;

    @Value("${catalog.file.name}")
    private String inputFileName;

    @Autowired
    private CatalogTokenCache catalogTokenCache;

    @Autowired
    MetricsAgent metricsAgent;

    @Override
    public void read() throws IOException {
        Range<Double> range = this.getRange(inputFileLocation + "/" + inputFileName,
                this.catalogTokenCache);
        this.normaliserUtil(
                Range.between(Constants.CATALOG_TOKENS_RANGE_MIN, Constants.CATALOG_TOKENS_RANGE_MAX),
                range,
                this.catalogTokenCache
        );
        logger.info("Catalog tokens file write complete");
    }

    private Range<Double> getRange(String filePath, CacheApi<String, Pair<Double, Double>> cacheApi) throws IOException {
        File file = ResourceUtil.getFile(inputFileLocation + "/" + inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = "";
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;

        while ((st = br.readLine()) != null) {

            String[] temp_row = st.split("=>");
            int len = temp_row.length;
            if (len < 2) {
                metricsAgent.recordMetricsEvents("invalid_file_row_catalog_tokens");
                logger.warn("invalid row format for Catalog Tokens file ", st);
                continue;
            }
            String key = temp_row[len - 2];
            key = key.trim();
            key = key.toLowerCase();

            if (FilterKeywordsUtil.isStopWord(key) || FilterKeywordsUtil.isWHiteListedToken(key)) continue;

            double count = Double.parseDouble(temp_row[len - 1]);

            double existingValue = 0.0;
            Pair<Double, Double> score = catalogTokenCache.get(key);
            if (score != null) {
                existingValue = score.getValue0();
                count += existingValue;
                score = score.setAt0(count);
            }else {
                score = Pair.with(count, null);
            }

            minimum = Math.min(count, minimum);
            maximum = Math.max(count, maximum);

            catalogTokenCache.put(key, score);
        }

        logger.info("Catalog Tokens file has minimum value of {}", minimum);
        logger.info("Catalog Tokens file has maximum value of {}", minimum);

        return Range.between(minimum, maximum);
    }
}

