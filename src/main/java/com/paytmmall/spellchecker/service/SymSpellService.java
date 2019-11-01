package com.paytmmall.spellchecker.service;

import com.paytmmall.spellchecker.library.spellchecker.SuggestItem;

import java.io.IOException;
import java.util.List;

public interface SymSpellService {

    void onStartup() throws IOException;

    public List<SuggestItem> lookup(String input);
    public SuggestItem lookupCompound(String input);
}
