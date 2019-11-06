package com.paytmmall.spellchecker.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {

    @Autowired
    private MessageSource messageSource;

    public String get(String errorCode, Object... args) {
        return messageSource.getMessage(errorCode, args, LocaleContextHolder.getLocale());
    }

    public String get(int errorCode, Object... args) {
        return messageSource.getMessage(Integer.toString(errorCode), args, LocaleContextHolder.getLocale());
    }

    public String get(int errorCode) {
        return messageSource.getMessage(Integer.toString(errorCode), null, LocaleContextHolder.getLocale());
    }

    public String get(String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }

}
