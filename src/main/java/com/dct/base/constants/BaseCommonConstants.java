package com.dct.base.constants;

/**
 * Contains the common configuration constants for the project without security configurations
 * @author thoaidc
 */
public interface BaseCommonConstants {

    // The location where the resource bundle files for i18n messages are stored
    String[] MESSAGE_SOURCE_BASENAME = { "classpath:i18n/messages" };

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
}
