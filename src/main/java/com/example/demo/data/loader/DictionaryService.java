package com.example.demo.data.loader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

@Service
public class DictionaryService {
    @Value("${output.file.location}")
    private String normalisedFileLocation;

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



    public static  void readFileInMap(File file, Map<String, Double> Map) throws IOException{
        String st = "";

        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((st = br.readLine()) != null){
            String [] temp_row = st.split(" ");
            int len = temp_row.length;
            if (len < 2)
                continue;

            String key = temp_row[len-2];
            key = key.trim();
            key = key.toLowerCase();

            Double count = Double.parseDouble(temp_row[len-1]);

            Map.put(key,count);
        }

    }
    public void process() throws IOException {


        File file_eng = new File(normalisedFileLocation+"/"+englishNormalisedFileName);
        File file_catalog = new File(normalisedFileLocation+"/"+catalogQueryNormalisedFileName);
        File file_user_queries = new File(normalisedFileLocation+"/"+userQueryNormalisedFileName);



        Map<String, Double> englishThresholdMap = new HashMap<>();
        Map<String, Double> catalogThresholdMap = new HashMap<>();


        Map<String, Double> catalogMap = new HashMap<>();
        Map<String, Double> englishMap = new HashMap<>();
        Map<String, Double> userMap = new HashMap<>();

        readFileInMap(file_eng, englishMap);
        readFileInMap(file_catalog, catalogMap);
        readFileInMap(file_user_queries, userMap);


        // if key is found in english than add its value to catalog map and remove the etry from english map
        for(String key : catalogMap.keySet()){
            if(englishMap.get(key)!=null  && englishMap.get(key) >=0){ // present in english
                double existingCount = englishMap.get(key);
                existingCount-=Constants.ENGLISH_DICTIONARY_RANGE_MIN;
                existingCount += catalogMap.get(key);
                if(existingCount > Constants.CATALOG_TOKENS_RANGE_MAX)
                    existingCount= Constants.CATALOG_TOKENS_RANGE_MAX;
                catalogMap.put(key,existingCount);
                englishMap.remove(key);
            }
        }

        HashSet<String> removeKeys = new HashSet<>();
        for(String key : userMap.keySet()) {
            if (catalogMap.get(key) != null ) {
                double existingCount = catalogMap.get(key);
                existingCount-= Constants.CATALOG_TOKENS_RANGE_MIN;
                existingCount+=userMap.get(key);


                userMap.put(key,existingCount);
                catalogMap.remove(key);
            }
            else{
                removeKeys.add(key);
            }
        }

        Iterator<String> it = removeKeys.iterator();

        while(it.hasNext()){
            String tempKey = it.next();
            userMap.remove(tempKey);
        }

        removeKeys.clear();

        for (String key : catalogMap.keySet()) {
            double value = catalogMap.get(key);
            if (value <= Constants.CATALOG_THRESHOLD) {
                removeKeys.add(key);
                catalogThresholdMap.put(key,value);
            }
        }

        it = removeKeys.iterator();

        while(it.hasNext()){
            String tempKey = it.next();
            catalogMap.remove(tempKey);
        }

        removeKeys.clear();


        for (String key : englishMap.keySet()) {
            double value = englishMap.get(key);
            if (value <= Constants.ENGLISH_THRESHOLD) {
                removeKeys.add(key);
                englishThresholdMap.put(key,value);
            }
        }

        it = removeKeys.iterator();

        while(it.hasNext()){
            String tempKey = it.next();
            englishMap.remove(tempKey);
        }


        FileWriter fw = new FileWriter(dictionaryFileLocation+"/"+dictionaryFileName);
        for(String key : userMap.keySet()){
            fw.write(key+" "+userMap.get(key));
            fw.write("\n");
        }
        for(String key : catalogMap.keySet()){
            fw.write(key+" "+catalogMap.get(key));
            fw.write("\n");
        }
        for(String key : englishMap.keySet()){
            fw.write(key+" "+englishMap.get(key));
            fw.write("\n");
        }
        fw.close();
        System.out.println("dictionary file write complete");

        fw = new FileWriter(thresholdFileLocation+"/"+catalogThresholdFileName);
        for(String key : catalogThresholdMap.keySet()){
            fw.write(key+" "+catalogThresholdMap.get(key));
            fw.write("\n");
        }
        fw.close();
        System.out.println("catalog threshold file write complete");

        fw = new FileWriter(thresholdFileLocation+""+englishThresholdFileName);
        for(String key : englishThresholdMap.keySet()){
            fw.write(key+" "+englishThresholdMap.get(key));
            fw.write("\n");
        }
        fw.close();
        System.out.println("english threshold file write complete");

    }
}

