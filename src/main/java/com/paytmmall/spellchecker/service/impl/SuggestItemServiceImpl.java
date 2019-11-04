package com.paytmmall.spellchecker.service.impl;

import com.paytmmall.spellchecker.library.spellchecker.SuggestItem;
import com.paytmmall.spellchecker.service.SuggestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestItemServiceImpl implements SuggestItemService {

    @Autowired
    private SymSpellServiceImpl symSpellService;

    @Override
    public List<SuggestItem> getSuggestItems(String input, Integer editDistance) {
        return symSpellService.lookup(input, editDistance);
    }
}
