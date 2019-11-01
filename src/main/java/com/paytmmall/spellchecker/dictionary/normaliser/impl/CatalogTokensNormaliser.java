package com.paytmmall.spellchecker.dictionary.normaliser.impl;

import com.paytmmall.spellchecker.cache.CatalogTokenCache;
import com.paytmmall.spellchecker.dictionary.Constants;
import com.paytmmall.spellchecker.dictionary.normaliser.Normaliser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

import static java.util.stream.Collectors.toMap;

@Service
public class CatalogTokensNormaliser implements Normaliser {

    @Value("${input.file.location}")
    private String inputFileLocation;

    @Value("${catalog.file.name}")
    private String inputFileName;

    @Autowired
    private CatalogTokenCache catalogTokenCache;

    public void normalise() throws  IOException {

        File file = new File(inputFileLocation+"/"+inputFileName);
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
//
//
//
//        Map<String, Double> sortedByValueDesc = reverseSortedMap.entrySet()
//                .stream()
//                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
//                .collect(toMap(Map.Entry::getKey,
//                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
//
//
//        FileWriter fw = new FileWriter(outputFileLocation+"/"+outputFileName);
//        for(String key : sortedByValueDesc.keySet()){
//            fw.write(key+" "+sortedByValueDesc.get(key));
//            fw.write("\n");
//        }
//        fw.close();
        System.out.println("catalog tokens file write complete");

    }
}

