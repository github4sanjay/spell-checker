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
    private static final Logger LOGGER = LoggerFactory.getLogger(CombinedNormaliser.class);

    @Autowired
    private CatalogTokensNormaliser catalogTokensNormaliser;

    @Autowired
    private EnglishDictionaryNormaliser englishDictionaryNormaliser;

    @Autowired
    private UserQueryTokenNormaliser userQueryTokenNormaliser;

    @Override
    public void normalise() throws IOException {
        CompletableFuture<Void> future1 = getCompletableFutureForNormaliser(catalogTokensNormaliser,
                "Catalog Tokens");
        CompletableFuture<Void> future2 = getCompletableFutureForNormaliser(englishDictionaryNormaliser,
                "English Dictionary");
        CompletableFuture<Void> future3 = getCompletableFutureForNormaliser(userQueryTokenNormaliser,
                "User query tokens");

        CompletableFuture<Void> combinedFuture
                = CompletableFuture.allOf(future1, future2, future3);

        try {
            combinedFuture.get(); // wait for all three to complete
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error occurred during normalisation of token files ", e);
            throw new CustomExceptions.FileReadException(e.getMessage());
        }
    }

    private CompletableFuture<Void> getCompletableFutureForNormaliser(Normaliser normaliser, String source) {
        return CompletableFuture.runAsync(() -> {
            try {
                normaliser.normalise();
            } catch (IOException e) {
                LOGGER.error("Error during {} file read. Exception: {}", source, e);
                throw new CustomExceptions.FileReadException(e.getMessage());
            }
        });
    }

}
