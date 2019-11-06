package com.paytmmall.spellchecker.dictionary.reader.impl;

import com.paytmmall.spellchecker.dictionary.reader.TokenReader;
import com.paytmmall.spellchecker.exception.CustomExceptions;
import com.paytmmall.spellchecker.metrics.MetricsAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CombinedTokenReader implements TokenReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombinedTokenReader.class);

    @Autowired
    private CatalogTokensTokenReader catalogTokensNormaliser;

    @Autowired
    private EnglishDictionaryTokenReader englishDictionaryNormaliser;

    @Autowired
    private UserQueryTokenTokenReader userQueryTokenNormaliser;

    @Autowired
    private MetricsAgent metricsAgent;

    @Override
    public void read() throws IOException {
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
            metricsAgent.recordMetricsEvents("normalisation_error");
            LOGGER.error("Error occurred during normalisation of token files ", e);
            throw new CustomExceptions.FileReadException(e.getMessage());
        }
    }

    private CompletableFuture<Void> getCompletableFutureForNormaliser(TokenReader tokenReader, String source) {
        return CompletableFuture.runAsync(() -> {
            try {
                tokenReader.read();
            } catch (IOException e) {
                metricsAgent.recordMetricsEvents("normalisation_file_read_error");
                LOGGER.error("Error during {} file read. Exception: {}", source, e);
                throw new CustomExceptions.FileReadException(e.getMessage());
            }
        });
    }

}
