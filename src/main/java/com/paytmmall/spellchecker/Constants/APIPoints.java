package com.paytmmall.spellchecker.Constants;

public enum APIPoints {

    GET_SUGGEST_ITEMS_V1("/api/v1/suggest-items");

    private String apiURIRegex;

    APIPoints(String uriRegex) {
        apiURIRegex = uriRegex;
    }

    public static APIPoints deserialize(String uri) {
        if (uri == null || uri.trim().length() == 0) {
            return null;
        }
        if (uri.contains(GET_SUGGEST_ITEMS_V1.apiURIRegex)) {
            return GET_SUGGEST_ITEMS_V1;
        }
        return null;
    }
}
