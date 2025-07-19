package com.dct.config.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public class DefaultBaseOAuth2AuthenticationSuccessHandler extends BaseOAuth2AuthenticationSuccessHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Custom logic here
        System.out.println("----------- Logged in successfully -----------");
    }
}
