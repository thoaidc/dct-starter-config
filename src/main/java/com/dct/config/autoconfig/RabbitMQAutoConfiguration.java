package com.dct.config.autoconfig;

import com.dct.model.config.properties.RabbitMQProps;
import com.dct.model.constants.ActivateStatus;
import com.dct.model.constants.BasePropertiesConstants;
import com.dct.model.exception.BaseIllegalArgumentException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ErrorHandler;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@AutoConfiguration
@EnableConfigurationProperties(RabbitMQProps.class)
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_RABBIT_MQ, havingValue = ActivateStatus.ENABLED_VALUE)
public class RabbitMQAutoConfiguration {
    private final RabbitMQProps rabbitMQProps;
    private final ObjectMapper objectMapper;
    private static final String ENTITY_NAME = "com.dct.config.autoconfig.RabbitMQAutoConfiguration";
    private static final Logger log = LoggerFactory.getLogger(RabbitMQAutoConfiguration.class);

    public RabbitMQAutoConfiguration(RabbitMQProps rabbitMQProps, ObjectMapper objectMapper) {
        if (Objects.isNull(rabbitMQProps)) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "Missing RabbitMQ config in application.yml");
        }

        this.rabbitMQProps = rabbitMQProps;
        this.objectMapper = objectMapper;
    }

    /**
     * Defines a direct exchange, where messages are routed based on routingKey
     * @return the {@link DirectExchange} instance
     */
    @Bean
    public DirectExchange directExchange() {
        if (Objects.isNull(rabbitMQProps.getExchange()) || Objects.isNull(rabbitMQProps.getExchange().getDirect())) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "No default direct exchange was declared");
        }

        return new DirectExchange(rabbitMQProps.getExchange().getDirect());
    }

    /**
     * Creates and registers message queues based on configuration
     * @return a list of {@link Queue}
     */
    @Bean
    public List<Queue> declareQueues(RabbitAdmin rabbitAdmin) {
        if (Objects.isNull(rabbitMQProps.getQueues())) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "No queues was defined!");
        }

        List<Queue> queues = new ArrayList<>();
        boolean durable = true; // The queue persists after RabbitMQ restarts
        boolean exclusive = false; // The queue is not limited to one connection
        boolean autoDelete = false; // Do not automatically delete the queue when there are no more consumers

        for (RabbitMQProps.Queue queueConfig : rabbitMQProps.getQueues().values()) {
            String queueName = Optional.ofNullable(queueConfig).map(RabbitMQProps.Queue::getName).orElse(null);

            if (StringUtils.hasText(queueName)) {
                Queue queue = new Queue(queueName, durable, exclusive, autoDelete);
                rabbitAdmin.declareQueue(queue);
                queues.add(queue);
            }
        }

        return queues;
    }

    /**
     * Defines bindings between queues and the direct exchange <p>
     * Connect queues to Direct Exchange using corresponding routingKey
     * @param directExchange the direct exchange
     * @return a list of {@link Binding}
     */
    @Bean
    public List<Binding> bindingQueues(DirectExchange directExchange, RabbitAdmin rabbitAdmin, List<Queue> queues) {
        List<Binding> bindings = new ArrayList<>();
        Map<String, String> queueNameToRoutingKey = new HashMap<>();

        for (RabbitMQProps.Queue queueConfig : rabbitMQProps.getQueues().values()) {
            if (StringUtils.hasText(queueConfig.getName()) && StringUtils.hasText(queueConfig.getRoutingKey())) {
                queueNameToRoutingKey.put(queueConfig.getName(), queueConfig.getRoutingKey());
            }
        }

        for (Queue queue : queues) {
            String queueName = queue.getName();
            String routingKey = queueNameToRoutingKey.get(queueName);

            if (StringUtils.hasText(routingKey)) {
                Binding binding = BindingBuilder.bind(queue).to(directExchange).with(routingKey);
                rabbitAdmin.declareBinding(binding);
                bindings.add(binding);
                log.info("Bound queue: `{}` with routing key: `{}`", queueName, routingKey);
            } else {
                log.warn("Queue `{}` does not have a corresponding routing key", queueName);
            }
        }

        return bindings;
    }

    /**
     * Create a connection to RabbitMQ with the information host, port, username, password, virtualHost
     * @return the {@link ConnectionFactory} instance
     */
    @Primary
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(String.join(",", rabbitMQProps.getServers()));
        connectionFactory.setPort(rabbitMQProps.getPort());
        connectionFactory.setUsername(rabbitMQProps.getUsername());
        connectionFactory.setPassword(rabbitMQProps.getPassword());
        connectionFactory.setVirtualHost(rabbitMQProps.getVirtualHost());
        connectionFactory.addConnectionListener(connection ->
            log.info("Successfully connected to RabbitMQ at: {}, {}", rabbitMQProps.getServers(), rabbitMQProps.getPort())
        );
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    /**
     * Use {@link Jackson2JsonMessageConverter} to automatically convert messages between JSON and Object
     * @return default {@link MessageConverter} for use
     */
    @Bean
    public MessageConverter defaultMessageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * Creates a RabbitMQ template for sending messages
     * @param connectionFactory the connection factory
     * @return the RabbitTemplate instance
     */
    @Primary
    @Bean(name = "customRabbitTemplate")
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setReplyTimeout(rabbitMQProps.getProducer().getReplyTimeout());
        return rabbitTemplate;
    }

    /**
     * Configures the RabbitMQ listener container factory <p>
     * Set the number of Consumers to process concurrently <p>
     * Helps optimize performance by pre-fetching multiple messages <p>
     * Handling errors with {@link RabbitMQAutoConfiguration#errorHandler()} <p>
     * Manually acknowledge messages ({@link AcknowledgeMode#MANUAL}), which helps avoid data loss if an error occurs
     *
     * @param connectionFactory the connection factory
     * @return the SimpleRabbitListenerContainerFactory instance
     */
    @Primary
    @Bean("rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                               MessageConverter messageConverter) {
        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // Resend message for reprocessing when a message fails during processing by the consumer
        factory.setDefaultRequeueRejected(true);
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(rabbitMQProps.getConsumer().getConcurrentConsumer());
        factory.setMaxConcurrentConsumers(rabbitMQProps.getConsumer().getMaxConcurrentConsumer());
        factory.setPrefetchCount(rabbitMQProps.getConsumer().getPrefetchCount());
        factory.setErrorHandler(errorHandler());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(new CustomFatalExceptionStrategy());
    }

    public static class CustomFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {
        private final Logger log = LoggerFactory.getLogger(CustomFatalExceptionStrategy.class);

        @Override
        public boolean isFatal(@Nonnull Throwable error) {
            if (error instanceof ListenerExecutionFailedException exception) {
                String queueName = exception.getFailedMessage().getMessageProperties().getConsumerQueue();
                log.error("[RABBIT_MQ_ERROR] - Failed to process message from queue: {}", queueName, error);
                log.error("[RABBIT_MQ_ERROR] - Failed message: {}", exception.getFailedMessage());
            }

            return super.isFatal(error);
        }
    }
}
