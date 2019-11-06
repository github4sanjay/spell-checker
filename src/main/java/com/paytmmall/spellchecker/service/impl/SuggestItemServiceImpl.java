package com.paytmmall.spellchecker.service.impl;

import com.paytmmall.spellchecker.library.spellchecker.SuggestItem;
import com.paytmmall.spellchecker.metrics.MetricsAgent;
import com.paytmmall.spellchecker.service.SuggestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestItemServiceImpl implements SuggestItemService {

    @Autowired
    private SymSpellServiceImpl symSpellService;

    @Autowired
    MetricsAgent metricsAgent;

    @Override
    public List<SuggestItem> getSuggestItems(String input, Integer editDistance) {
        List<SuggestItem> lookup = symSpellService.lookup(input, editDistance);

        if(lookup.size() ==0){ // if we got zero spell check results
            metricsAgent.recordMetricsEvents("zero_results_spell_check");
        }
        if (lookup.size() > 0 && lookup.get(0).distance == 0) { // if we got a result with zero ed.ie. exact match
            metricsAgent.recordMetricsEvents("exact_match_spell_check");
        }
        return lookup;
    }
}
