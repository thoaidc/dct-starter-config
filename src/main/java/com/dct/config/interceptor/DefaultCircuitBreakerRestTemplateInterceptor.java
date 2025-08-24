package com.dct.config.interceptor;

import com.dct.model.exception.BaseInternalServerException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Default implementation of {@link BaseCircuitBreakerRestTemplateInterceptor} that applies
 * Resilience4j's {@link CircuitBreaker},
 * {@link Retry}, and {@link TimeLimiter}
 * to outgoing HTTP requests made with {@link org.springframework.web.client.RestTemplate}
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Intercept outgoing HTTP requests from RestTemplate</li>
 *   <li>Wrap request execution in a {@link CircuitBreaker} to prevent repeated calls to failing endpoints</li>
 *   <li>Optionally wrap with {@link Retry} if provided, to reattempt failed calls based on configuration</li>
 *   <li>Optionally wrap with {@link TimeLimiter} if provided, to enforce a maximum execution time for the HTTP call</li>
 *   <li>Convert failures into {@link BaseInternalServerException} with diagnostic logging</li>
 * </ul>
 *
 * <h2>Execution Flow:</h2>
 * <ol>
 *   <li>Create a {@link Supplier} that executes the actual HTTP request</li>
 *   <li>Decorate the supplier with {@link CircuitBreaker}.</li>
 *   <li>If {@link Retry} is present, further decorate the supplier with retry logic</li>
 *   <li>If {@link TimeLimiter} is present, run the supplier asynchronously with a timeout limit</li>
 *   <li>Otherwise, execute the supplier synchronously</li>
 * </ol>
 *
 * <h2>Usage:</h2>
 * <pre>{@code
 *    @Bean
 *    public RestTemplate restTemplate(DefaultCircuitBreakerRestTemplateInterceptor interceptor) {
 *        RestTemplate restTemplate = new RestTemplate();
 *        restTemplate.getInterceptors().add(interceptor);
 *        return restTemplate;
 *    }
 * }</pre>
 *
 * <p>This ensures that all HTTP requests executed via the configured RestTemplate
 * will automatically benefit from circuit breaker, retry, and timeout protections
 *
 * @author thoaidc
 */
public class DefaultCircuitBreakerRestTemplateInterceptor extends BaseCircuitBreakerRestTemplateInterceptor {

    private static final Logger log = LoggerFactory.getLogger(DefaultCircuitBreakerRestTemplateInterceptor.class);
    private static final String ENTITY_NAME = "com.dct.config.interceptor.DefaultCircuitBreakerRestTemplateInterceptor";
    private final CircuitBreaker circuitBreaker;
    private final TimeLimiter timeLimiter;
    private final Retry retry;

    /**
     * Creates a default RestTemplate interceptor with Circuit Breaker, optional TimeLimiter, and optional Retry.
     *
     * @param circuitBreaker the Resilience4j CircuitBreaker instance (required)
     * @param timeLimiter    optional Resilience4j TimeLimiter instance (nullable)
     * @param retry          optional Resilience4j Retry instance (nullable)
     */
    public DefaultCircuitBreakerRestTemplateInterceptor(CircuitBreaker circuitBreaker,
                                                        @Nullable TimeLimiter timeLimiter,
                                                        @Nullable Retry retry) {
        this.circuitBreaker = circuitBreaker;
        this.timeLimiter = timeLimiter;
        this.retry = retry;
    }

    /**
     * Intercepts the HTTP request and applies CircuitBreaker, Retry, and/or TimeLimiter decorations.
     *
     * @param request   the HTTP request
     * @param body      the request body as byte array
     * @param execution the request execution
     * @return the HTTP response
     */
    @Override
    public ClientHttpResponse handle(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        log.info("[CIRCUIT_BREAKER_INTERCEPTOR] - Intercepted request: {} {}", request.getMethod(), request.getURI());

        // Base supplier with CircuitBreaker
        log.debug("[CIRCUIT_BREAKER] - Supplier wrapped with CircuitBreaker instance '{}'", circuitBreaker.getName());
        Supplier<ClientHttpResponse> supplier = getClientHttpResponseSupplier(request, body, execution);

        // Optionally wrap with Retry
        if (Objects.nonNull(retry)) {
            log.debug("[CIRCUIT_BREAKER_RETRY] - Supplier wrapped with Retry instance '{}'", retry.getName());
            supplier = Retry.decorateSupplier(retry, supplier);
        }

        // If TimeLimiter present → run async + wrap with TimeLimiter
        if (Objects.nonNull(timeLimiter)) {
            log.debug("[CIRCUIT_BREAKER_TIME_LIMITER] - Callable decorated with TimeLimiter: '{}'", timeLimiter.getName());
            // Reassign this variable because used in lambda expression should be final or effectively final
            Supplier<ClientHttpResponse> finalSupplier = supplier;

            Callable<ClientHttpResponse> decorated = TimeLimiter.decorateFutureSupplier(
                    timeLimiter,
                    () -> {
                        log.debug(
                                "[CIRCUIT_BREAKER_TIME_LIMITER] - Starting async execution with timeout {}ms",
                                timeLimiter.getTimeLimiterConfig().getTimeoutDuration().toMillis()
                        );
                        return CompletableFuture.supplyAsync(finalSupplier);
                    }
            );

            try {
                ClientHttpResponse result = decorated.call();
                log.debug("[CIRCUIT_BREAKER_INTERCEPTOR] - HTTP call with time limiter completed successfully");
                return result;
            } catch (Exception e) {
                throw circuitBreakerException(e);
            }
        }

        // If no TimeLimiter → run synchronously
        try {
            ClientHttpResponse result = supplier.get();
            log.debug("[CIRCUIT_BREAKER_INTERCEPTOR] - HTTP call completed successfully");
            return result;
        } catch (Exception e) {
            throw circuitBreakerException(e);
        }
    }

    /**
     * Builds a supplier that executes the actual HTTP request and decorates it with a CircuitBreaker.
     */
    private Supplier<ClientHttpResponse> getClientHttpResponseSupplier(HttpRequest request,
                                                                       byte[] body,
                                                                       ClientHttpRequestExecution execution) {
        Supplier<ClientHttpResponse> supplier = () -> {
            log.debug("[CB_REST_TEMPLATE] - Executing actual HTTP call to {}", request.getURI());

            try {
                ClientHttpResponse response = execution.execute(request, body);
                log.debug("[CB_REST_TEMPLATE] - Received response: {} {}", response.getStatusCode(), response.getStatusText());
                return response;
            } catch (IOException e) {
                log.error("[CB_REST_TEMPLATE] - IOException occurred: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        };

        return CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
    }

    /**
     * Converts execution exceptions into a {@link BaseInternalServerException} for consistent error handling.
     */
    private BaseInternalServerException circuitBreakerException(Exception e) {
        log.error("[CIRCUIT_BREAKER_INTERCEPTOR] - Request failed: {}", e.getMessage(), e);
        return BaseInternalServerException.builder()
                .entityName(ENTITY_NAME)
                .originalMessage(e.getMessage())
                .error(e.getCause())
                .build();
    }
}
