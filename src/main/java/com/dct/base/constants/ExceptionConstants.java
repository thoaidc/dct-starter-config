package com.dct.base.constants;

/**
 * Messages for exceptions with internationalization (I18n) here<p>
 * The constant content corresponds to the message key in the resources bundle files in directories such as:
 * <ul>
 *   <li><a href="">resources/i18n/messages</a></li>
 * </ul>
 * These paths are defined in {@link CommonConstants#MESSAGE_SOURCE_BASENAME}
 *
 * @author thoaidc
 */
@SuppressWarnings("unused")
public interface ExceptionConstants {

    // I18n exception
    String TRANSLATE_NOT_FOUND = "exception.i18n.notFound";
}
