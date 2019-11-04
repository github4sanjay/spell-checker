package com.paytmmall.spellchecker.util;

import com.paytmmall.spellchecker.dictionary.Constants;

public class FilterKeywordsUtil {

    public static boolean isStopWord(String name) {
        if (Constants.STOP_WORDS.contains(name)) return true;

        return false;
    }

    public static boolean isWHiteListedToken(String name) {
        for (int i = 0; i < Constants.WHITELISTED_TOKENS.size(); i++) {
            if (name.contains(Constants.WHITELISTED_TOKENS.get(i))) {
                return true;
            }
        }
        return false;
    }
}
