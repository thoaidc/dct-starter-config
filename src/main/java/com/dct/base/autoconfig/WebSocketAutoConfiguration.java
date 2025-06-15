package com.dct.base.autoconfig;

import com.dct.base.constants.BasePropertiesConstants;
import com.dct.base.security.config.BaseCorsRequestMatchersConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_SOCKET, havingValue = "true")
@EnableWebSocketMessageBroker
public class WebSocketAutoConfiguration implements WebSocketMessageBrokerConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAutoConfiguration.class);
    private static final String ENTITY_NAME = "WebSocketAutoConfiguration";
    private final BaseCorsRequestMatchersConfig corsRequestMatchersConfig;

    public WebSocketAutoConfiguration(BaseCorsRequestMatchersConfig corsRequestMatchersConfig) {
        this.corsRequestMatchersConfig = corsRequestMatchersConfig;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.debug("[{}] - Socket connection is available to subscribe in: /topic/", ENTITY_NAME);
        // Allow clients to subscribe to topics
        config.enableSimpleBroker("/topic");
        // Client sends message to server using this prefix
        config.setApplicationDestinationPrefixes("/api/ws");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] ALLOW_ORIGIN_PATTERNS = corsRequestMatchersConfig.getAllowedOriginPatterns().toArray(new String[0]);
        registry.addEndpoint("/ws").setAllowedOriginPatterns(ALLOW_ORIGIN_PATTERNS);
    }
}
