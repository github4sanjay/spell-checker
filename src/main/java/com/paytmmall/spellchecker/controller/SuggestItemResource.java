package com.paytmmall.spellchecker.controller;

import com.paytmmall.spellchecker.library.spellchecker.SuggestItem;
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

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<List<SuggestItem>> getSuggestItems(@RequestParam String input){
        return new ResponseEntity<>(suggestItemService.getSuggestItems(input), HttpStatus.OK);
    }
}
