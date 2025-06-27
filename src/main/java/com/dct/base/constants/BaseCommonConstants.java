package com.dct.base.constants;

/**
 * Contains the common configuration constants for the project without security configurations
 * @author thoaidc
 */
public interface BaseCommonConstants {

    String DEFAULT_CREATOR = "SYSTEM"; // Used instead of the default user value mentioned above to store in database

    // The location where the resource bundle files for i18n messages are stored
    String[] DEFAULT_MESSAGE_SOURCE_BASENAME = { "classpath:i18n/messages" };
    String DEFAULT_MESSAGE_SOURCE_ENCODING = "UTF-8"; // Specifies the charset for i18n messages

    // The paths that will be ignored by interceptors when processing requests
    String[] DEFAULT_INTERCEPTOR_EXCLUDED_PATTERNS = {
        "/**.html",
        "/**.js",
        "/**.css",
        "/**.webp",
        "/**.jpg",
        "/**.jpeg",
        "/**.gif",
        "/**.svg",
        "/**.png",
        "/**.ico",
        "/uploads/**",
        "/file/**",
        "/login",
        "/error**",
        "/i18n/**"
    };

    interface UPLOAD_RESOURCES {
        String DEFAULT_DIRECTORY = "/uploads/";
        String DEFAULT_PREFIX_PATH = "/uploads/";
        String[] DEFAULT_VALID_IMAGE_FORMATS = { ".png", ".jpg", ".jpeg", ".gif", ".svg", ".webp", ".webm" };
        String[] COMPRESSIBLE_IMAGE_FORMATS = { ".png", ".jpg", ".jpeg", ".webp" };
        String DEFAULT_IMAGE_FORMAT = ".webp";
        String DEFAULT_IMAGE_PATH_FOR_ERROR = "/error_image.webp";
        String PNG = "png";
        String WEBP = "webp";
        String JPG = "jpg";
        String JPEG = "jpeg";

        String[] DEFAULT_PATTERNS = {
            "/uploads/**"
        };

        String[] DEFAULT_LOCATIONS = {
            "/uploads/"
        };
    }

    /**
     * Configures the handling of static resources <p>
     * Static resource requests listed in the {@link STATIC_RESOURCES#DEFAULT_PATTERNS} section will be automatically searched for
     * and mapped to the directories listed in the {@link STATIC_RESOURCES#DEFAULT_LOCATIONS} section
     */
    interface STATIC_RESOURCES {

        String[] DEFAULT_PATTERNS = {
            "/**.html",
            "/**.js",
            "/**.css",
            "/**.webp",
            "/**.jpg",
            "/**.jpeg",
            "/**.gif",
            "/**.svg",
            "/**.png",
            "/**.ico",
            "/uploads/**",
            "/file/**",
            "/i18n/**"
        };

        String[] DEFAULT_LOCATIONS = {
            "classpath:/static/",
            "classpath:/static/i18n/"
        };
    }

    interface Socket {
        String[] DEFAULT_BROKER_PREFIXES = { "/topic" };
        String[] DEFAULT_APPLICATION_PREFIXES = { "/api/ws" };
        String[] DEFAULT_ENDPOINTS = { "/ws" };
    }
}
