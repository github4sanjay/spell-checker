package com.example.demo.data.loader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Service
public class EnglishDictionaryNormalisationService {

    @Value("${input.file.location}")
    private String inputFileLocation;

    @Value("${output.file.location}")
    private String outputFileLocation;

    @Value("${english.file.name}")
    private String inputFileName;

    @Value("${english.normalised.file.name}")
    private String outputFileName;

    public void process() throws FileNotFoundException, IOException {

        File file = new File(inputFileLocation+"/"+inputFileName);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st ="";

        Map<String, Double> reverseSortedMap = new HashMap<>();

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

            if(skip== true) continue;

            try{
               count = Double.parseDouble(temp_row[len-1]);
            }
            catch (Exception e){
                System.out.println("error occured in english dictionary while file read");
                continue;
            }
            if(count <minimum)minimum = count;

            if(count> maximum) maximum = count;

            reverseSortedMap.put(key,count);
        }


        for(String key : reverseSortedMap.keySet()){
            double fetchedValue = reverseSortedMap.get(key);
            double value = (Constants.ENGLISH_DICTIONARY_RANGE_MAX-Constants.ENGLISH_DICTIONARY_RANGE_MAX) *((fetchedValue-minimum)/(maximum- minimum))+Constants.ENGLISH_DICTIONARY_RANGE_MIN;
            reverseSortedMap.put(key,value);
        }



        Map<String, Double> sortedByValueDesc = reverseSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        FileWriter fw = new FileWriter(outputFileLocation+"/"+outputFileName);
        for(String key : sortedByValueDesc.keySet()){
            fw.write(key+" "+sortedByValueDesc.get(key));
            fw.write("\n");
        }
        fw.close();
        System.out.println("english dictionary tokens file write complete");

    }
}

