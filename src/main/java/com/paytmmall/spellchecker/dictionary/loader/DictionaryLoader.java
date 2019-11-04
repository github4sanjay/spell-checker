package com.paytmmall.spellchecker.dictionary.loader;

import com.paytmmall.spellchecker.cache.CatalogTokenCache;
import com.paytmmall.spellchecker.cache.Dictionary;
import com.paytmmall.spellchecker.cache.EnglishDictionaryCache;
import com.paytmmall.spellchecker.cache.UserQueryTokenCache;
import com.paytmmall.spellchecker.dictionary.Constants;
import com.paytmmall.spellchecker.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class DictionaryLoader {

    @Value("${dictionary.file.location}")
    private String dictionaryFileLocation;

    @Value("${threshold.file.location}")
    private String thresholdFileLocation;

    @Value("${english.normalised.file.name}")
    private String englishNormalisedFileName;

    @Value("${user.query.normalised.file.name}")
    private String userQueryNormalisedFileName;

    @Value("${catalog.normalised.file.name}")
    private String catalogQueryNormalisedFileName;

    @Value("${english.threshold.file.name}")
    private String englishThresholdFileName;

    @Value("${catalog.threshold.file.name}")
    private String catalogThresholdFileName;

    @Value("${dictionary.file.name}")
    private String dictionaryFileName;

    @Autowired
    private CatalogTokenCache catalogTokenCache;

    @Autowired
    private EnglishDictionaryCache englishDictionaryCache;

    @Autowired
    private UserQueryTokenCache userQueryTokenCache;

    @Autowired
    Dictionary dictionary;

    public void onStartup() throws IOException {

        Map<String, Double> englishThresholdMap = new HashMap<>();
        Map<String, Double> catalogThresholdMap = new HashMap<>();

        // if key is found in english than add its value to catalog map and remove the etry from english map
        for(String key : catalogTokenCache.keySet()){
            if(englishDictionaryCache.get(key)!=null  && englishDictionaryCache.get(key) >=0){ // present in english
                double existingCount = englishDictionaryCache.get(key);
                existingCount-= Constants.ENGLISH_DICTIONARY_RANGE_MIN;
                existingCount += catalogTokenCache.get(key);
                if(existingCount > Constants.CATALOG_TOKENS_RANGE_MAX)
                    existingCount= Constants.CATALOG_TOKENS_RANGE_MAX;
                catalogTokenCache.put(key,existingCount);
                englishDictionaryCache.clear(key);
            }
        }

        for(String key : userQueryTokenCache.keySet()) {
            if (catalogTokenCache.get(key) != null ) {
                double existingCount = catalogTokenCache.get(key);
                existingCount-= Constants.CATALOG_TOKENS_RANGE_MIN;
                existingCount+=userQueryTokenCache.get(key);

                userQueryTokenCache.put(key,existingCount);
                catalogTokenCache.clear(key);
            }
            else{
                userQueryTokenCache.clear(key);
            }
        }


        for (String key : catalogTokenCache.keySet()) {
            double value = catalogTokenCache.get(key);
            if (value <= Constants.CATALOG_THRESHOLD) {
                catalogTokenCache.clear(key);
                catalogThresholdMap.put(key,value);
            }
        }

        for (String key : englishDictionaryCache.keySet()) {
            double value = englishDictionaryCache.get(key);
            if (value <= Constants.ENGLISH_THRESHOLD) {
                englishDictionaryCache.clear(key);
                englishThresholdMap.put(key,value);
            }
        }

        File file = ResourceUtil.getFile(dictionaryFileLocation+"/"+dictionaryFileName);
        FileWriter fw = new FileWriter(file);
        for(String key : userQueryTokenCache.keySet()){
            fw.write(key+" "+userQueryTokenCache.get(key));
            fw.write("\n");
            dictionary.put(key,userQueryTokenCache.get(key));
        }
        for(String key : catalogTokenCache.keySet()){
            fw.write(key+" "+catalogTokenCache.get(key));
            fw.write("\n");
            dictionary.put(key,catalogTokenCache.get(key));
        }
        for(String key : englishDictionaryCache.keySet()){
            fw.write(key+" "+englishDictionaryCache.get(key));
            fw.write("\n");
            dictionary.put(key,englishDictionaryCache.get(key));
        }
        fw.close();
        System.out.println("dictionary file write complete");

        fw = new FileWriter(ResourceUtil.getFile(thresholdFileLocation+"/"+catalogThresholdFileName));
        for(String key : catalogThresholdMap.keySet()){
            fw.write(key+" "+catalogThresholdMap.get(key));
            fw.write("\n");
        }
        fw.close();
        System.out.println("catalog threshold file write complete");

        fw = new FileWriter(ResourceUtil.getFile(thresholdFileLocation+"/"+englishThresholdFileName));
        for(String key : englishThresholdMap.keySet()){
            fw.write(key+" "+englishThresholdMap.get(key));
            fw.write("\n");
        }
        fw.close();
        System.out.println("english threshold file write complete");
    }
}

