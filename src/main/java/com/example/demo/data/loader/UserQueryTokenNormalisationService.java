package com.example.demo.data.loader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Service
public class UserQueryTokenNormalisationService {


    @Value("${input.file.location}")
    private String inputFileLocation;

    @Value("${output.file.location}")
    private String outputFileLocation;

    @Value("${user.query.file.name}")
    private String inputFileName;

    @Value("${user.query.normalised.file.name}")
    private String outputFileName;


    public void process() throws IOException {


        File file = new File(inputFileLocation+"/"+inputFileName);


        BufferedReader br = new BufferedReader(new FileReader(file));

        String st ="";

        Map<String, Double> reverseSortedMap = new HashMap<>();
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;

        while ((st = br.readLine()) != null){
            String [] temp_row = st.split(",");
            int len = temp_row.length;
            if(len !=3)
                continue;

            String name = "";
            double priority = 0.0;
            String[] queries = temp_row[len-3].split(" ");

            int query_len = queries.length;

            double clicks, impressions;
            try{
                clicks = Double.parseDouble(temp_row[len -2]);
                impressions = Double.parseDouble(temp_row[len -1]);

            }
            catch (Exception e){
                continue;
            }


            for (int j = 0; j < query_len; j++) {
                priority = clicks*.8 + impressions*.2;

                if (priority > maximum)
                    maximum = priority;

                if (priority < minimum)
                    minimum = priority;

                boolean skip = false;
                name = queries[j];
                name = name.toLowerCase();
                name = name.trim();
                if(Constants.STOP_WORDS.contains(name))continue;


                for(int k=0 ; k < Constants.WHITELISTED_TOKENS.size();k++){
                    if(name.contains(Constants.WHITELISTED_TOKENS.get(k))){
                        skip= true;
                        break;
                    }
                }

                if(skip== true) continue;

                double temp_value = 0.0;

                if(reverseSortedMap.get(name)!=null){
                    temp_value = reverseSortedMap.get(name);
                }

                if(temp_value > priority) priority = temp_value;

                reverseSortedMap.put(name, priority);
            }

        }

        System.out.println(minimum);
        System.out.println(maximum);


        for(String key : reverseSortedMap.keySet()){
            double fetchedValue = reverseSortedMap.get(key);
            double value = (Constants.USER_QUERY_RANGE_MAX-Constants.USER_QUERY_RANGE_MIN) *((fetchedValue-minimum)/(maximum- minimum))+Constants.USER_QUERY_RANGE_MIN;

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
        System.out.println("user query tokens file write complete");
    }
}

