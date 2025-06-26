package com.dct.base.constants;

import com.dct.base.dto.auth.BaseAuthTokenDTO;
import com.dct.base.security.config.BaseSecurityFilterChainConfig;
import com.dct.base.autoconfig.InterceptorAutoConfiguration;
import org.springframework.http.HttpMethod;

public interface BaseSecurityConstants {

    // The encryption complexity in PasswordEncoder's algorithm (between 4 and 31)
    // Higher values mean the password is harder to attack, but too high will reduce performance
    int BCRYPT_COST_FACTOR = 12;

    /**
     * The corresponding keys to store information in the payload of a JWT token <p>
     * See {@link com.dct.base.security.jwt.BaseJwtProvider#generateToken(BaseAuthTokenDTO)} for details
     */
    interface TOKEN_PAYLOAD {
        String USER_ID = "userId";
        String USERNAME = "username";
        String AUTHORITIES = "authorities";
    }

    interface COOKIES {
        // The key of the cookie storing the JWT token, which is HTTP-only
        // This cookie is automatically sent with requests by browser but cannot be accessed by JavaScript
        String HTTP_ONLY_COOKIE_ACCESS_TOKEN = "dct_access_token";
    }

    interface HEADER {

        // The request header storing the JWT token, used in cases where the token is not found in the HTTP-only cookies
        String AUTHORIZATION_HEADER = "Authorization";
        String AUTHORIZATION_GATEWAY_HEADER = "Authorization-Gateway";
        String TOKEN_TYPE = "Bearer "; // JWT token type
    }

    /**
     * The paths for security configuration in {@link BaseSecurityFilterChainConfig} <p>
     * Requests matching the patterns below will have their own specific security rules applied <p>
     * Requests not listed will require authentication by default
     */
    interface REQUEST_MATCHERS {
        String[] DEFAULT_PUBLIC_API_PATTERNS = {
            "/",
            "/**.html",
            "/**.css",
            "/**.js",
            "/**.ico",
            "/i18n/**",
            "/uploads/**",
            "/register",
            "/login",
            "/p/**",
            "/api/p/**",
            "/login/oauth2/**"
        };
    }

    /**
     * The configurations applied in the CORS filter in {@link InterceptorAutoConfiguration#defaultCorsFilter}
     */
    interface CORS {
        String DEFAULT_APPLY_FOR = "/**"; // CORS filter is applied to all requests
        String[] DEFAULT_ALLOWED_HEADERS = {
            "Content-Type",     // Content format
            "Authorization",    // Authentication token
            "Accept",           // Client-expected content
            "Origin",           // Origin of the request
            "X-CSRF-Token",     // Anti-CSRF token
            "X-Requested-With", // Ajax request markup
            "Access-Control-Allow-Origin", // Server response header
            "X-App-Version",    // Application version (optional)
            "X-Device-ID"       // Device ID (optional)
        };

        String[] DEFAULT_ALLOWED_REQUEST_METHODS = {
            HttpMethod.GET.name(),
            HttpMethod.PUT.name(),
            HttpMethod.POST.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()
        };

        String[] DEFAULT_ALLOWED_ORIGIN_PATTERNS = {"*"}; // The list of domains allowed to access the resources. * means all
        boolean DEFAULT_ALLOW_CREDENTIALS = true; // Allow sending cookies or authentication information
    }
}
