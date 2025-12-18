package com.dct.config.autoconfig;

import com.dct.model.config.properties.SocketProps;
import com.dct.model.constants.ActivateStatus;
import com.dct.model.constants.BasePropertiesConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.*;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_SOCKET, havingValue = ActivateStatus.ENABLED_VALUE)
@EnableConfigurationProperties(SocketProps.class)
@EnableWebSocketMessageBroker
public class WebSocketAutoConfiguration implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketAutoConfiguration.class);
    private final ApplicationContext applicationContext;
    private final SocketProps socketProps;

    public WebSocketAutoConfiguration(ApplicationContext applicationContext, SocketProps socketProps) {
        this.applicationContext = applicationContext;
        this.socketProps = socketProps;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.debug("[SOCKET_AUTO_CONFIG] - Socket subscribe prefixes: {}", (Object) socketProps.getBrokerPrefixes());
        long[] heartbeats = new long[] {socketProps.getServerHeartbeatInterval(), socketProps.getClientHeartbeatInterval()};
        // Allow clients to subscribe to topics
        config.enableSimpleBroker(socketProps.getBrokerPrefixes())
                .setHeartbeatValue(heartbeats)
                .setTaskScheduler(heartbeatScheduler());
        // Client sends message to server using this prefix
        config.setApplicationDestinationPrefixes(socketProps.getApplicationPrefixes());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] ALLOWED_ORIGIN_PATTERNS = socketProps.getAllowedOriginPatterns();
        log.debug("[SOCKET_AUTO_CONFIG] - Web socket allowed origins: {}", (Object) ALLOWED_ORIGIN_PATTERNS);
        List<HandshakeInterceptor> wsHandshakeInterceptors = socketProps.getInterceptors()
                .stream()
                .map(this::getInterceptorInstance)
                .toList();

        StompWebSocketEndpointRegistration socketEndpointRegistration = registry.addEndpoint(socketProps.getEndpoints())
                .setAllowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS)
                .addInterceptors(wsHandshakeInterceptors.toArray(new HandshakeInterceptor[0]));

        StompWebSocketEndpointRegistration sockJsEndpointRegistration = registry.addEndpoint("/ws-sock-js")
                .setAllowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS)
                .addInterceptors(wsHandshakeInterceptors.toArray(new HandshakeInterceptor[0]));

        if (Objects.nonNull(socketProps.getHandshakeHandler())) {
            socketEndpointRegistration.setHandshakeHandler(getHandlerInstance(socketProps.getHandshakeHandler()));
            sockJsEndpointRegistration.setHandshakeHandler(getHandlerInstance(socketProps.getHandshakeHandler()));
        }

        sockJsEndpointRegistration.withSockJS();
    }

    private TaskScheduler heartbeatScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        threadPoolTaskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        // Ensure that running tasks are completed when the server shuts down
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    private HandshakeHandler getHandlerInstance(Class<?> clazz) {
        // If the handler is a Spring bean, get it from the context
        Map<String, ?> beans = applicationContext.getBeansOfType(clazz);

        if (!beans.isEmpty()) {
            return (HandshakeHandler) beans.values().iterator().next();
        }

        // If it is not Spring bean, create instance yourself
        try {
            return (HandshakeHandler) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize Handshake handler: " + clazz.getName(), e);
        }
    }

    private HandshakeInterceptor getInterceptorInstance(Class<?> clazz) {
        // If the interceptor is a Spring bean, get it from the context
        Map<String, ?> beans = applicationContext.getBeansOfType(clazz);

        if (!beans.isEmpty()) {
            return (HandshakeInterceptor) beans.values().iterator().next();
        }

        // If it is not Spring bean, create instance yourself
        try {
            return (HandshakeInterceptor) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize Handshake interceptor: " + clazz.getName(), e);
        }
    }
}
