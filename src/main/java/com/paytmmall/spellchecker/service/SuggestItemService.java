package com.paytmmall.spellchecker.service;

import com.paytmmall.spellchecker.library.spellchecker.SuggestItem;

import java.util.List;

public interface SuggestItemService {
    public List<SuggestItem> getSuggestItems(String input);
}
