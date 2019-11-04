package com.paytmmall.spellchecker.service.impl;

import com.paytmmall.spellchecker.cache.DeletesKeywords;
import com.paytmmall.spellchecker.cache.Dictionary;
import com.paytmmall.spellchecker.cache.OriginalWordsCache;
import com.paytmmall.spellchecker.library.spellchecker.SuggestItem;
import com.paytmmall.spellchecker.library.spellchecker.SymSpell;
import com.paytmmall.spellchecker.service.SymSpellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
public class SymSpellServiceImpl implements SymSpellService {
    private SymSpell.Verbosity suggestionVerbosity = SymSpell.Verbosity.All; //Top, Closest, All
    private int maxEditDistanceLookup; //max edit distance per lookup (maxEditDistanceLookup<=maxEditDistanceDictionary)
    private SymSpell symSpell;

    @Value("${dictionary.file.location}")
    private String dictionaryFileLocation;

    @Value("${dictionary.file.name}")
    private String dictionaryFileName;

    @Autowired
    private OriginalWordsCache originalWordsCache;

    @Autowired
    private Dictionary dictionary;

    @Autowired
    private DeletesKeywords deletesKeywords;

    @Override
    public void onStartup() throws IOException {
        int maxEditDistanceLookup = 3;
        symSpell = new SymSpell(-1, maxEditDistanceLookup, -1, 1, originalWordsCache, dictionary, deletesKeywords);//, (byte)18);
        
        this.maxEditDistanceLookup = maxEditDistanceLookup;
        int termIndex = 0;
        int countIndex = 1;
        String path = dictionaryFileLocation+"/"+dictionaryFileName;
        symSpell.loadDictionary();
    }

    @Override
    public List<SuggestItem> lookup(String input){
        return symSpell.lookup(input, suggestionVerbosity, maxEditDistanceLookup);
    }

    @Override
    public SuggestItem lookupCompound(String input){
        return symSpell.lookupCompound(input, maxEditDistanceLookup).get(0);
    }
}
