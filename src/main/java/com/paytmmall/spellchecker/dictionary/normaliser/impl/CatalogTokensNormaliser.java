package com.paytmmall.spellchecker.dictionary.normaliser.impl;

import com.paytmmall.spellchecker.cache.CacheApi;
import com.paytmmall.spellchecker.cache.CatalogTokenCache;
import com.paytmmall.spellchecker.dictionary.normaliser.Normaliser;
import com.paytmmall.spellchecker.util.FilterKeywordsUtil;
import com.paytmmall.spellchecker.util.ResourceUtil;
import org.apache.commons.lang3.Range;
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
public class CatalogTokensNormaliser implements Normaliser {

    private static final Logger logger = LoggerFactory.getLogger(CatalogTokensNormaliser.class);

    @Value("${input.file.location}")
    private String inputFileLocation;

    @Value("${catalog.file.name}")
    private String inputFileName;

    @Autowired
    private CatalogTokenCache catalogTokenCache;

    @Override
    public void normalise() throws IOException {
        Range<Double> range = this.getRange(inputFileLocation + "/" + inputFileName,
                this.catalogTokenCache);
        this.normaliserUtil(range, this.catalogTokenCache);
        System.out.println("Catalog tokens file write complete");
    }

    private Range<Double> getRange(String filePath, CacheApi<String, Double> cacheApi) throws IOException {
        File file = ResourceUtil.getFile(inputFileLocation + "/" + inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = "";
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;

        while ((st = br.readLine()) != null) {

            String[] temp_row = st.split("=>");
            int len = temp_row.length;
            if (len < 2) {
                logger.error("invalid row format for Catalog Tokens file ", st);
                continue;
            }
            String key = temp_row[len - 2];
            key = key.trim();
            key = key.toLowerCase();

            if (FilterKeywordsUtil.isStopWord(key) || FilterKeywordsUtil.isWHiteListedToken(key)) continue;

            double count = Double.parseDouble(temp_row[len - 1]);

            double existingValue = 0.0;
            if (catalogTokenCache.get(key) != null) {
                existingValue = catalogTokenCache.get(key);
                count += existingValue;
            }

            minimum = Math.min(count, minimum);
            maximum = Math.max(count, maximum);

            catalogTokenCache.put(key, count);
        }

        logger.info("Catalog Tokens file has minimum value of {}",minimum);
        logger.info("Catalog Tokens file has maximum value of {}",minimum);

        return Range.between(minimum, maximum);
    }
}

