package com.paytmmall.spellchecker.dictionary.normaliser.impl;

import com.paytmmall.spellchecker.dictionary.normaliser.Normaliser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CombinedNormaliser implements Normaliser {

    @Autowired
    private CatalogTokensNormaliser catalogTokensNormaliser;

    @Autowired
    private EnglishDictionaryNormaliser englishDictionaryNormaliser;

    @Autowired
    private UserQueryTokenNormaliser userQueryTokenNormaliser;

    @Override
    public void normalise() throws IOException {
        catalogTokensNormaliser.normalise();
        englishDictionaryNormaliser.normalise();
        userQueryTokenNormaliser.normalise();
    }
}
