package com.paytmmall.spellchecker.listener;

import com.paytmmall.spellchecker.dictionary.loader.DictionaryLoader;
import com.paytmmall.spellchecker.dictionary.normaliser.impl.CombinedNormaliser;
import com.paytmmall.spellchecker.service.SymSpellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class StartupApplicationListener{

    @Autowired
    private SymSpellService symSpellService;

    @Autowired
    private DictionaryLoader dictionaryLoader;

    @Autowired
    private CombinedNormaliser combinedNormaliser;

    @PostConstruct
    public void onApplicationEvent() {
    // order is important
        try {
            combinedNormaliser.normalise();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dictionaryLoader.onStartup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            symSpellService.onStartup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
