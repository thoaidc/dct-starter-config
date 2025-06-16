package com.dct.base.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@SuppressWarnings("unused")
public abstract class BaseOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(BaseOAuth2AuthenticationSuccessHandler.class);

    /**
     * Customize the business logic when OAuth2 authentication is successful here <p>
     * For example: create an access token, create default account information, etc <p>
     * In this case, we create default account information for the user from the data provided by Google,
     * along with the access_token stored in an HTTP-only cookie
     *
     * @param request the request which caused the successful authentication
     * @param response the response
     * @param authentication the <tt>Authentication</tt> object which was created during the authentication process
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        handle(request, response, authentication);
    }

    public abstract void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
}
