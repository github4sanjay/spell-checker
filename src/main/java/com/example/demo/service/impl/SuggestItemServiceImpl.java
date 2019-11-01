package com.example.demo.service.impl;

import com.example.demo.library.spellchecker.SuggestItem;
import com.example.demo.service.SuggestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestItemServiceImpl implements SuggestItemService {

    @Autowired
    private SymSpellServiceImpl symSpellService;

    @Override
    public List<SuggestItem> getSuggestItems(String input) {
        return symSpellService.lookup(input);
    }
}
