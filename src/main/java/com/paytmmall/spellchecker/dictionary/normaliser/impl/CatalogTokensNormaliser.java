package com.paytmmall.spellchecker.dictionary.normaliser.impl;

import com.paytmmall.spellchecker.cache.CatalogTokenCache;
import com.paytmmall.spellchecker.dictionary.Constants;
import com.paytmmall.spellchecker.dictionary.normaliser.Normaliser;
import com.paytmmall.spellchecker.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class CatalogTokensNormaliser implements Normaliser {

    @Value("${input.file.location}")
    private String inputFileLocation;

    @Value("${catalog.file.name}")
    private String inputFileName;

    @Autowired
    private CatalogTokenCache catalogTokenCache;

    @Override
    public void normalise() throws IOException {
        File file = ResourceUtil.getFile(inputFileLocation+"/"+inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st ="";
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;

        while ((st = br.readLine()) != null){
            boolean skip = false;
            String [] temp_row = st.split("=>");
            int len = temp_row.length;
            if(len <2){
                continue;
            }
            String key = temp_row[len-2];
            key = key.trim();
            key = key.toLowerCase();
            if(Constants.STOP_WORDS.contains(key)) continue;
            for(int j=0 ; j < Constants.WHITELISTED_TOKENS.size();j++){
                if(key.contains(Constants.WHITELISTED_TOKENS.get(j))){
                    skip= true;
                    break;
                }
            }

            if(skip) continue;

            Double count = Double.parseDouble(temp_row[len-1]);

            double existingValue = 0.0;
            if (catalogTokenCache.get(key) != null) {
                existingValue = catalogTokenCache.get(key);
                count += existingValue;
            }
            if(count <minimum)minimum = count;
            if(count> maximum) maximum = count;

            catalogTokenCache.put(key,count);
        }

        System.out.println(minimum);
        System.out.println(maximum);

        for(String key : catalogTokenCache.keySet()){
            double fetchedValue = catalogTokenCache.get(key);
            double value = (Constants.CATALOG_TOKENS_RANGE_MAX-Constants.CATALOG_TOKENS_RANGE_MIN) *((fetchedValue-minimum)/(maximum- minimum))+Constants.CATALOG_TOKENS_RANGE_MIN;
            catalogTokenCache.put(key,value);
        }
        System.out.println("catalog tokens file write complete");
    }
}

