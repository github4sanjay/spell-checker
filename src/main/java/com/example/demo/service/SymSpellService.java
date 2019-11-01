package com.example.demo.service;

import com.example.demo.library.spellchecker.SuggestItem;
import java.util.List;

public interface SymSpellService {

    public List<SuggestItem> lookup(String input);
    public SuggestItem lookupCompound(String input);
}
