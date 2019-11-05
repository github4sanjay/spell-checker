package com.paytmmall.spellchecker.dictionary.normaliser.impl;

import com.paytmmall.spellchecker.dictionary.normaliser.Normaliser;
import com.paytmmall.spellchecker.exception.CustomExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CombinedNormaliser implements Normaliser {
    private static final Logger logger = LoggerFactory.getLogger(CombinedNormaliser.class);


    @Autowired
    private CatalogTokensNormaliser catalogTokensNormaliser;

    @Autowired
    private EnglishDictionaryNormaliser englishDictionaryNormaliser;

    @Autowired
    private UserQueryTokenNormaliser userQueryTokenNormaliser;

    @Override
    public void normalise() throws IOException {
        CompletableFuture<Void> future1
                = CompletableFuture.runAsync(() -> {
            try {
                catalogTokensNormaliser.normalise();
            } catch (IOException e) {
                logger.error("Error during Catalog Token file read ",e);
                throw new CustomExceptions.FileReadException(e.getMessage());
            }
        });
        CompletableFuture<Void> future2
                = CompletableFuture.runAsync(() -> {
            try {
                englishDictionaryNormaliser.normalise();
            } catch (IOException e) {
                logger.error("Error during English Dictionary file read ",e);
                throw new CustomExceptions.FileReadException(e.getMessage());
            }
        });
        CompletableFuture<Void> future3
                = CompletableFuture.runAsync(() -> {
            try {
                userQueryTokenNormaliser.normalise();
            } catch (IOException e) {
                logger.error("Error during User Query Token file read ",e);
                throw new CustomExceptions.FileReadException(e.getMessage());
            }
        });

        CompletableFuture<Void> combinedFuture
                = CompletableFuture.allOf(future1, future2, future3);

        try {
            combinedFuture.get(); // wait for all three to complete
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error occured during normalisation of token files ",e);
            throw new CustomExceptions.FileReadException(e.getMessage());
        }
    }

}
