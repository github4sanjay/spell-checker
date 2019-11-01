package com.paytmmall.spellchecker.dictionary.normaliser.impl;

import com.paytmmall.spellchecker.cache.EnglishDictionaryCache;
import com.paytmmall.spellchecker.dictionary.Constants;
import com.paytmmall.spellchecker.dictionary.normaliser.Normaliser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public void normalise() throws FileNotFoundException, IOException {
        File file = new File(inputFileLocation+"/"+inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st ="";

        //Map<String, Double> reverseSortedMap = new HashMap<>();

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
                System.out.println("error occured in english dictionary while file read");
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
       System.out.println("english dictionary tokens file write complete");

    }
}

