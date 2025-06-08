package com.dct.base.constants;

import com.dct.base.dto.auth.BaseAuthTokenDTO;

@SuppressWarnings("unused")
public interface BaseSecurityConstants {

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

        // Configure Content-Security-Policy (CSP), which controls how resources are loaded and executed on the website
        // The main goal is to minimize the risk of attacks such as Cross-Site Scripting (XSS) or Code Injection
        String SECURITY_POLICY = "default-src 'self';" + // Only allow resources to be loaded from the same domain
                // Only allow embedded content (iframes) from the same domain (self) or from URLs with schema data
                " frame-src 'self' data:;" +
                // Only allow script execution from the same domain (self), unsafe-inline (allows inline scripts),
                // unsafe-eval (allows eval()), and from the domain https://storage.googleapis.com
                " script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com;" +
                // Only allow styles from the same domain (self) and allow inline CSS (unsafe-inline)
                " style-src 'self' 'unsafe-inline';" +
                // Only allow images from the same domain (self) and from URLs with schema data
                " img-src 'self' data:;" +
                // Only allow fonts from the same domain (self) and from URLs with schema data
                " font-src 'self' data:";

        // Permissions-Policy limits access to sensitive browser features, helping protect user privacy
        String PERMISSIONS_POLICY = "camera=(), " + // Prevent any origin from using the camera
                "fullscreen=(self), " + // Only allow current domain (self) to use full screen mode
                "geolocation=(), " + // Prevent access to GPS location services
                "gyroscope=(), " + // Prevent use of gyroscope sensor
                "magnetometer=(), " + // Prevent use of magnetometer sensor
                "microphone=(), " + // Prevent microphone access
                "midi=(), " + // Prevent access to MIDI (digital musical instruments)
                "payment=(), " + // Prevent use of payment features
                "sync-xhr=()"; // Prevent use of synchronous XMLHttpRequest requests
    }
}
