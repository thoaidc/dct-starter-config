package com.dct.config.autoconfig;

import com.dct.model.config.properties.CorsProps;
import com.dct.model.config.properties.CorsProps.CorsMapping;
import com.dct.model.config.properties.SocketProps;
import com.dct.model.constants.ActivateStatus;
import com.dct.model.constants.BasePropertiesConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_SOCKET, havingValue = ActivateStatus.ENABLED_VALUE)
@EnableConfigurationProperties(SocketProps.class)
@EnableWebSocketMessageBroker
public class WebSocketAutoConfiguration implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketAutoConfiguration.class);
    private final SocketProps socketProps;
    private final CorsProps corsProps;

    public WebSocketAutoConfiguration(SocketProps socketProps, CorsProps corsProps) {
        this.socketProps = socketProps;
        this.corsProps = corsProps;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.debug("[SOCKET_AUTO_CONFIG] - Socket subscribe prefixes: {}", (Object) socketProps.getBrokerPrefixes());
        // Allow clients to subscribe to topics
        config.enableSimpleBroker(socketProps.getBrokerPrefixes());
        // Client sends message to server using this prefix
        config.setApplicationDestinationPrefixes(socketProps.getApplicationPrefixes());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        Map<String, CorsMapping> corsMappings = Optional.ofNullable(corsProps).orElseGet(CorsProps::new).getPatterns();
        String[] ALLOWED_ORIGIN_PATTERNS = corsMappings.values()
                .stream()
                .map(CorsMapping::getAllowedOrigins)
                .flatMap(Collection::stream)
                .distinct() // Remove duplicates pattern
                .toArray(String[]::new);

        log.debug("[SOCKET_AUTO_CONFIG] - Web socket allowed origins: {}", (Object) ALLOWED_ORIGIN_PATTERNS);
        registry.addEndpoint(socketProps.getEndpoints()).setAllowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS);
    }
}
