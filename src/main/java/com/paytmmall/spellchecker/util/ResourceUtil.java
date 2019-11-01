package com.paytmmall.spellchecker.util;

import com.paytmmall.spellchecker.exception.CustomExceptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public class ResourceUtil {
    public static File getFile(String location){
        Resource resource = new ClassPathResource(location);
        try {
            return resource.getFile();
        } catch (IOException e) {
            throw new CustomExceptions.FileNotFoundException("File not found in path"+ location);
        }
    }
}
