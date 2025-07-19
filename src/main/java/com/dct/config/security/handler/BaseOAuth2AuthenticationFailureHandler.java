package com.dct.config.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

@SuppressWarnings("unused")
public abstract class BaseOAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    /**
     * Called when an error occurs during the OAuth2 authentication process <p>
     * Respond directly to the client through the response object <p>
     * In this case, send a custom JSON response <p>
     * You can add other business logic here, such as sending a redirect
     *
     * @param request the request during which the authentication attempt occurred
     * @param response the response
     * @param exception the exception which was thrown to reject the authentication request
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        handle(request, response, exception);
    }

    public abstract void handle(HttpServletRequest request,
                                HttpServletResponse response,
                                AuthenticationException e) throws IOException;
}
