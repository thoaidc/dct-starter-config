package com.dct.config.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

/**
 * Abstract base class for {@link ClientHttpRequestInterceptor} implementations
 * that apply Circuit Breaker, Retry, TimeLimiter, or similar fault-tolerance
 * patterns to outgoing HTTP requests made via {@link org.springframework.web.client.RestTemplate}.
 *
 * <p>This base class delegates the {@link #intercept(HttpRequest, byte[], ClientHttpRequestExecution)}
 * method to the abstract {@link #handle(HttpRequest, byte[], ClientHttpRequestExecution)} method,
 * which must be implemented by subclasses to define the actual interception logic.
 *
 * <h2>Important Notes:</h2>
 * <ul>
 *   <li>
 *       Unlike Feign's {@code RequestInterceptor}, RestTemplate does <b>not</b> automatically
 *       register {@link ClientHttpRequestInterceptor} beans.
 *       You must explicitly add an implementation of this class to your RestTemplate instance
 *   </li>
 *   <li>
 *       Common usage is to implement this base class to wrap the request execution
 *       in a Circuit Breaker, Retry, and/or TimeLimiter from libraries like Resilience4j
 *   </li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 *    @Component
 *    public class MyCircuitBreakerInterceptor extends BaseCircuitBreakerRestTemplateInterceptor {
 *        @Override
 *        public ClientHttpResponse handle(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
 *            return circuitBreaker.executeSupplier(() -> execution.execute(request, body));
 *        }
 *    }
 *
 *    @Configuration
 *    public class RestTemplateConfig {
 *        @Bean
 *        public RestTemplate restTemplate(MyCircuitBreakerInterceptor interceptor) {
 *            RestTemplate restTemplate = new RestTemplate();
 *            restTemplate.getInterceptors().add(interceptor);
 *            return restTemplate;
 *        }
 *    }
 * }</pre>
 *
 * This ensures that all outgoing requests via the configured RestTemplate
 * will pass through the Circuit Breaker logic defined in {@link #handle}
 *
 * @author thoaidc
 */
public abstract class BaseCircuitBreakerRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request,
                                        @NonNull byte[] body,
                                        @NonNull ClientHttpRequestExecution execution) {
        return handle(request, body, execution);
    }

    /**
     * Implement this method to apply custom interception logic to the outgoing HTTP request,
     * such as wrapping the call in a Circuit Breaker, Retry, or TimeLimiter.
     *
     * @param request   the HTTP request
     * @param body      the request body as a byte array
     * @param execution the request execution to proceed with the request
     * @return the HTTP response
     */
    public abstract ClientHttpResponse handle(HttpRequest request, byte[] body, ClientHttpRequestExecution execution);
}
