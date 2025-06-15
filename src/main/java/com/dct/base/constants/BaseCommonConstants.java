package com.dct.base.constants;

/**
 * Contains the common configuration constants for the project without security configurations
 * @author thoaidc
 */
public interface BaseCommonConstants {

    String DEFAULT_CREATOR = "SYSTEM"; // Used instead of the default user value mentioned above to store in database

    // The location where the resource bundle files for i18n messages are stored
    String[] MESSAGE_SOURCE_BASENAME = { "classpath:i18n/messages" };
    String MESSAGE_SOURCE_ENCODING = "UTF-8"; // Specifies the charset for i18n messages

    interface UPLOAD_RESOURCES {
        String DEFAULT_DIRECTORY = "/uploads/";
        String PREFIX_PATH = "/uploads/";
        String[] VALID_IMAGE_FORMATS = { ".png", ".jpg", ".jpeg", ".gif", ".svg", ".webp", ".webm" };
        String[] COMPRESSIBLE_IMAGE_FORMATS = { ".png", ".jpg", ".jpeg", ".webp" };
        String DEFAULT_IMAGE_FORMAT = ".webp";
        String DEFAULT_IMAGE_PATH_FOR_ERROR = PREFIX_PATH + "error/error_image.webp";
        String PNG = "png";
        String WEBP = "webp";
        String JPG = "jpg";
        String JPEG = "jpeg";
    }

    /**
     * Configures the handling of static resources <p>
     * Static resource requests listed in the {@link STATIC_RESOURCES#PATHS} section will be automatically searched for
     * and mapped to the directories listed in the {@link STATIC_RESOURCES#LOCATIONS} section
     */
    interface STATIC_RESOURCES {

        String[] PATHS = {
            "/**.js",
            "/**.css",
            "/**.svg",
            "/**.png",
            "/**.ico",
            "/content/**",
            "/uploads/**",
            "/i18n/**"
        };

        String[] LOCATIONS = {
            "classpath:/static/",
            "classpath:/static/i18n/"
        };
    }
}
