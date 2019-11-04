package com.paytmmall.spellchecker.dictionary.normaliser.impl;

import com.paytmmall.spellchecker.cache.EnglishDictionaryCache;
import com.paytmmall.spellchecker.dictionary.Constants;
import com.paytmmall.spellchecker.dictionary.normaliser.Normaliser;
import com.paytmmall.spellchecker.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;

import static java.util.stream.Collectors.toMap;

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
        File file = ResourceUtil.getFile(inputFileLocation+"/"+inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st ="";
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;
        Double count = 0.0;
        while ((st = br.readLine()) != null){
            boolean skip = false;
            String [] temp_row = st.split(" ");
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

            try{
               count = Double.parseDouble(temp_row[len-1]);
            }
            catch (Exception e){
                System.out.println("error occured in english dictionary while file read "+temp_row[len-1]);
                continue;
            }
            if(count <minimum)minimum = count;

            if(count> maximum) maximum = count;

            englishDictionaryCache.put(key,count);
        }

        for(String key : englishDictionaryCache.keySet()){
            double fetchedValue = englishDictionaryCache.get(key);
            double value = (Constants.ENGLISH_DICTIONARY_RANGE_MAX-Constants.ENGLISH_DICTIONARY_RANGE_MAX) *((fetchedValue-minimum)/(maximum- minimum))+Constants.ENGLISH_DICTIONARY_RANGE_MIN;
            englishDictionaryCache.put(key,value);
        }
       System.out.println("english dictionary tokens file write complete");

    }
}

