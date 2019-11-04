package com.paytmmall.spellchecker.dictionary.normaliser.impl;

import com.paytmmall.spellchecker.cache.EnglishDictionaryCache;
import com.paytmmall.spellchecker.dictionary.Constants;
import com.paytmmall.spellchecker.dictionary.normaliser.Normaliser;
import com.paytmmall.spellchecker.util.FilterKeywordsUtil;
import com.paytmmall.spellchecker.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class EnglishDictionaryNormaliser implements Normaliser {

    @Value("${input.file.location}")
    private String inputFileLocation;

    @Value("${english.file.name}")
    private String inputFileName;

    @Autowired
    private EnglishDictionaryCache englishDictionaryCache;

    @Override
    public void normalise() throws FileNotFoundException, IOException {
        File file = ResourceUtil.getFile(inputFileLocation + "/" + inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = "";
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;
        Double count = 0.0;
        while ((st = br.readLine()) != null) {
            String[] temp_row = st.split(" "); // row format assuming keyword count
            int len = temp_row.length;

            if (len < 2) continue;

            String key = temp_row[len - 2];
            key = key.trim();
            key = key.toLowerCase();

            if (FilterKeywordsUtil.isStopWord(key) || FilterKeywordsUtil.isWHiteListedToken(key)) continue;

            try {
                count = Double.parseDouble(temp_row[len - 1]);
            } catch (Exception e) {
                System.out.println("error occured in english dictionary while file read " + temp_row[len - 1]);
                continue;
            }
            minimum = (count < minimum) ? count : minimum;
            maximum = (count > maximum) ? count : maximum;

            englishDictionaryCache.put(key, count);
        }


        System.out.println(maximum);
        System.out.println(minimum);

        normaliseEnglishTokensUtil(maximum, minimum);
        System.out.println("english dictionary tokens file write complete");

    }

    private void normaliseEnglishTokensUtil(double maximum, double minimum) {
        for (String key : englishDictionaryCache.keySet()) {
            double fetchedValue = englishDictionaryCache.get(key);
            double value = (Constants.CATALOG_TOKENS_RANGE_MAX - Constants.CATALOG_TOKENS_RANGE_MIN) * ((fetchedValue - minimum) / (maximum - minimum)) + Constants.CATALOG_TOKENS_RANGE_MIN;
            englishDictionaryCache.put(key, value);
        }
    }
}

