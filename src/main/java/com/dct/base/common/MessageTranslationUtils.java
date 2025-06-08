package com.dct.base.common;

import com.dct.base.constants.BaseExceptionConstants;
import com.dct.base.dto.response.BaseResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * Provide common processing functions for the entire application
 * @author thoaidc
 */
@SuppressWarnings("unused")
public class MessageTranslationUtils {

    private static final Logger log = LoggerFactory.getLogger(MessageTranslationUtils.class);
    private final MessageSource messageSource; // Spring boot service for I18n

    public MessageTranslationUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Get the internationalized content (I18n) of the key based on the current locale in the application
     * @param messageKey The code corresponding to the internationalized content to be retrieved
     * @param args Arguments passed to use dynamic values for message
     * @return Value of {@link BaseExceptionConstants#TRANSLATE_NOT_FOUND} if not found message I18n
     */
    public String getMessageI18n(String messageKey, Object ...args) {
        log.debug("Translate message for key: '{}'", messageKey);
        // The value of Locale represents the current region, here used to determine the language type to translate
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(messageKey, args, null, locale);

        if (StringUtils.hasText(message))
            return message;

        return messageSource.getMessage(BaseExceptionConstants.TRANSLATE_NOT_FOUND, null, "", locale);
    }

    /**
     * Check if message found in i18n message file then return, otherwise return null
     * @param messageKey The code corresponding to the internationalized content to be retrieved
     * @param args Arguments passed to use dynamic values for message
     * @return null if not found message
     */
    public String checkMessageI18n(String messageKey, Object ...args) {
        // The value of Locale represents the current region, here used to determine the language type to translate
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(messageKey, args, null, locale);

        if (StringUtils.hasText(message))
            return message;

        return null;
    }

    /**
     * Set the translated message I18n for the response body. Can be used by ResponseFilter (Self-Defined)
     * @param responseDTO response before sending to client
     * @return response after it has been translated and is ready to be sent to the client
     */
    public BaseResponseDTO setResponseMessageI18n(BaseResponseDTO responseDTO) {
        String messageKey = responseDTO.getMessage();

        if (StringUtils.hasText(messageKey)) {
            String messageTranslated = checkMessageI18n(messageKey);

            if (StringUtils.hasText(messageTranslated))
                responseDTO.setMessage(messageTranslated);
        } else {
            responseDTO.setMessage(getMessageI18n(BaseExceptionConstants.TRANSLATE_NOT_FOUND));
        }

        return responseDTO;
    }
}
