package com.paytmmall.spellchecker.service;

import com.paytmmall.spellchecker.library.spellchecker.SuggestItem;
import java.util.List;

public interface SymSpellService {

    public List<SuggestItem> lookup(String input);
    public SuggestItem lookupCompound(String input);
}
