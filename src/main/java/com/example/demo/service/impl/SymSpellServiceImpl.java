package com.example.demo.service.impl;

import com.example.demo.library.spellchecker.SuggestItem;
import com.example.demo.library.spellchecker.SymSpell;
import com.example.demo.service.SymSpellService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.util.List;

@Service
public class SymSpellServiceImpl implements SymSpellService {
    private SymSpell.Verbosity suggestionVerbosity = SymSpell.Verbosity.All; //Top, Closest, All
    private int maxEditDistanceLookup; //max edit distance per lookup (maxEditDistanceLookup<=maxEditDistanceDictionary)
    private SymSpell symSpell;

    @PostConstruct
    private void onStartup() throws FileNotFoundException {
        int maxEditDistanceLookup = 3;
        symSpell = new SymSpell(-1, maxEditDistanceLookup, -1, 10);//, (byte)18);
        this.maxEditDistanceLookup = maxEditDistanceLookup;
        int termIndex = 0;
        int countIndex = 1;
        String path = "data/frequency_dictionary_en_82_765.txt";
        if(!symSpell.loadDictionary(path, termIndex, countIndex))throw new FileNotFoundException("File not found");
    }

    public List<SuggestItem> lookup(String input){
        return symSpell.lookup(input, suggestionVerbosity, maxEditDistanceLookup);
    }

    public SuggestItem lookupCompound(String input){
        return symSpell.lookupCompound(input, maxEditDistanceLookup).get(0);
    }
}
