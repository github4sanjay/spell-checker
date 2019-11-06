package com.paytmmall.spellchecker.controller;

import com.paytmmall.spellchecker.exception.CustomExceptions;
import com.paytmmall.spellchecker.library.spellchecker.SuggestItem;
import com.paytmmall.spellchecker.metrics.MetricsAgent;
import com.paytmmall.spellchecker.service.SuggestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suggest-items")
public class SuggestItemResource {

    @Autowired
    private SuggestItemService suggestItemService;

    @Autowired
    MetricsAgent metricsAgent;

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<List<SuggestItem>> getSuggestItems(@RequestParam String input, @RequestParam Integer editDistance) {
        if (input == null || input.isEmpty())
            throw new CustomExceptions.InvalidRequestException("Input cannot be null or empty");
        else if (editDistance == null)
            throw new CustomExceptions.InvalidRequestException("Edit distance is required");
        return new ResponseEntity<>(suggestItemService.getSuggestItems(input, editDistance), HttpStatus.OK);
    }
}
