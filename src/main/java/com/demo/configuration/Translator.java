package com.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Dịch các chuỗi dựa trên mã thông báo (message code) và locale hiện tại.
 */
@Component
public class Translator {

    private static ResourceBundleMessageSource messageSource;

    @Autowired
    public Translator(ResourceBundleMessageSource messageSource) {
        Translator.messageSource = messageSource;
    }

    public static String toLocale(String msgCode) {
        Locale locale = LocaleContextHolder.getLocale(); // lấy ra locale hiện tại
        return messageSource.getMessage(msgCode, null, locale);
    }
}
