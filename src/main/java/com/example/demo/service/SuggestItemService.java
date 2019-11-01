package com.example.demo.service;

import com.example.demo.library.spellchecker.SuggestItem;

import java.util.List;

public interface SuggestItemService {
    public List<SuggestItem> getSuggestItems(String input);
}
