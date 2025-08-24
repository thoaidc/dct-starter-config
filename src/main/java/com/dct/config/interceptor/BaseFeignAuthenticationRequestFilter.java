package com.dct.config.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Abstract base class for Feign request interceptors that handle authentication or
 * request customization before the request is sent.
 *
 * <p>Feign automatically detects and applies all {@link RequestInterceptor} beans
 * in the Spring context to every Feign client request. This means any subclass of
 * {@code BaseFeignAuthenticationRequestFilter} that is registered as a Spring bean
 * will automatically be executed without additional configuration.
 *
 * <p>Usage:
 * <ul>
 *   <li>Create a subclass and implement the {@link #handle(RequestTemplate)} method
 *       to add authentication headers, tokens, or any other request modifications.</li>
 *   <li>Register the subclass as a Spring bean (e.g., annotate with {@code @Component}
 *       or declare it in a {@code @Configuration} class with {@code @Bean}).</li>
 *   <li>Feign will call the {@link #apply(RequestTemplate)} method for every outgoing
 *       request, which delegates to {@link #handle(RequestTemplate)} for your custom logic.</li>
 * </ul>
 *
 * <p>Example:
 * <pre>{@code
 * @Component
 * public class MyAuthInterceptor extends BaseFeignAuthenticationRequestFilter {
 *     @Override
 *     public void handle(RequestTemplate requestTemplate) {
 *         requestTemplate.header("Authorization", "Bearer " + tokenService.getToken());
 *     }
 * }
 * }</pre>
 *
 * @author thoaidc
 */
@SuppressWarnings("unused")
public abstract class BaseFeignAuthenticationRequestFilter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        handle(template);
    }

    /**
     * Implement this method to customize the Feign request before it is sent.
     * This method is called for every outgoing Feign request.
     *
     * @param requestTemplate the Feign {@link RequestTemplate} to customize
     */
    public abstract void handle(RequestTemplate requestTemplate);
}
