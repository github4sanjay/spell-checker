package com.paytmmall.spellchecker.dictionary.normaliser.impl;

import com.paytmmall.spellchecker.cache.CacheApi;
import com.paytmmall.spellchecker.cache.UserQueryTokenCache;
import com.paytmmall.spellchecker.dictionary.Constants;
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
public class UserQueryTokenNormaliser implements Normaliser {
    private static final Logger logger = LoggerFactory.getLogger(UserQueryTokenNormaliser.class);

    @Value("${input.file.location}")
    private String inputFileLocation;

    @Value("${user.query.file.name}")
    private String inputFileName;

    @Autowired
    private UserQueryTokenCache userQueryTokenCache;

    @Override
    public void normalise() throws IOException {
        Range<Double> range = this.getRange(inputFileLocation + "/" + inputFileName,
                this.userQueryTokenCache);
        this.normaliserUtil(range, this.userQueryTokenCache);
        System.out.println("User tokens file write complete");
    }

    private Range<Double> getRange(String filePath, CacheApi<String, Double> cacheApi) throws IOException {
        File file = ResourceUtil.getFile(inputFileLocation + "/" + inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st = "";
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;

        while ((st = br.readLine()) != null) {
            String[] temp_row = st.split(","); // split every row into token, impression, click
            int len = temp_row.length;
            if (len != 3){ // if row is not well formatted than ignore it
                logger.error("invalid row format for userQueryToken file ", st);
                continue;
            }

            String name = "";
            double priority = 0.0;
            String[] queries = temp_row[len - 3].split(" ");

            double clicks, impressions;
            try {
                clicks = Double.parseDouble(temp_row[len - 2]);
                impressions = Double.parseDouble(temp_row[len - 1]);
            } catch (Exception e) {
                logger.error("invalid row values for userQueryToken file ", e);
                continue;
            }

            for (String query : queries) {
                priority = clicks * Constants.CLICKS_WEIGHTAGE + impressions * Constants.IMPRESSIONS_WEIGHTAGE;

                maximum = Math.max(priority, maximum);
                minimum = Math.min(priority, minimum);

                name = query;
                name = name.toLowerCase();
                name = name.trim();

                if (FilterKeywordsUtil.isStopWord(name) || FilterKeywordsUtil.isWHiteListedToken(name)) continue;

                double temp_value = 0.0;

                if (userQueryTokenCache.get(name) != null) {
                    temp_value = userQueryTokenCache.get(name);
                }

                if (temp_value > priority) priority = temp_value;

                userQueryTokenCache.put(name, priority);
            }

        }

        logger.info("userQueryTokens file has minimum value of {}",minimum);
        logger.info("userQueryTokens file has maximum value of {}",maximum);

        return Range.between(minimum, maximum);

    }
}

