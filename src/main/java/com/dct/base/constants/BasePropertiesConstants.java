package com.dct.base.constants;

/**
 * Contains the prefixes for the config property files <p>
 * Refer to these files in the <a href="">com/dct/base/config/properties</a> directory for more details
 *
 * @author thoaidc
 */
public interface BasePropertiesConstants {

    String SECURITY_CONFIG = "dct.base.security.auth";
    String SECRET_KEY_PROPERTY = "base64-secret-key";
    String DATASOURCE_CONFIG = "spring.datasource";
    String HIKARI_CONFIG = "spring.datasource.hikari";
    String HIKARI_DATASOURCE_CONFIG = "spring.datasource.hikari.data-source-properties";
    String UPLOAD_CONFIG = "dct.base.upload";
}
