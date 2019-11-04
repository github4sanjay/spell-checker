package com.paytmmall.spellchecker.service;

import com.paytmmall.spellchecker.library.spellchecker.SuggestItem;
import io.swagger.models.auth.In;

import java.util.List;

public interface SuggestItemService {
    public List<SuggestItem> getSuggestItems(String input, Integer editDistance);
}
