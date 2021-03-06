package io.github.smallintro.rabbitmq.orderprocessor.config;

import io.github.smallintro.rabbitmq.orderprocessor.constant.RabbitMqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration is same for producer and consumer
 */
@Configuration
@Slf4j
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.rabbitmq.port:5672}")
    private Integer port;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    /**
     * ConnectionFactory is manage by Spring boot by default.
     * We can override if really required to do so.
     * @return ConnectionFactory
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        log.info("creating connection with amqp://{}:{}@{}:{}/",username, password, host, port);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public Queue queue() {
        // this queue will be created with random name by amqp and will be auto deleted
        // can also specify the naming strategy as argument
        return new AnonymousQueue();
    }

    @Bean
    public Queue queueAlpha() {
        return new Queue(RabbitMqConstants.Queue.ALPHA, false);
    }

    @Bean
    public Queue queueBeta() {
        return new Queue(RabbitMqConstants.Queue.BETA, false);
    }

    @Bean
    public Queue queueDefault() {
        return new Queue(RabbitMqConstants.Queue.DEFAULT);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(RabbitMqConstants.Exchange.DIRECT);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitMqConstants.Exchange.FANOUT);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitMqConstants.Exchange.TOPIC);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(RabbitMqConstants.Exchange.HEADER);
    }

    @Bean
    public Binding bindingAlphaQDirectEx(Queue queueAlpha, DirectExchange directExchange) {
        return BindingBuilder.bind(queueAlpha)
                .to(directExchange)
                .with(RabbitMqConstants.getRoutingKey(RabbitMqConstants.Exchange.DIRECT, RabbitMqConstants.Queue.ALPHA));
    }

    @Bean
    public Binding bindingBetaQDirectEx(Queue queueBeta, DirectExchange directExchange) {
        return BindingBuilder.bind(queueBeta)
                .to(directExchange)
                .with(RabbitMqConstants.getRoutingKey(RabbitMqConstants.Exchange.DIRECT, RabbitMqConstants.Queue.BETA));
    }

    @Bean
    public Binding bindingAlphaQFanoutEx(Queue queueAlpha, FanoutExchange fanoutExchange) {
        // no routing key required for fanout exchange.
        // message will be sent to all the binding queues
        return BindingBuilder.bind(queueAlpha)
                .to(fanoutExchange);
    }

    @Bean
    public Binding bindingBetaQFanoutEx(Queue queueBeta, FanoutExchange fanoutExchange) {
        // no routing key required for fanout exchange.
        // message will be sent to all the binding queues
        return BindingBuilder.bind(queueBeta)
                .to(fanoutExchange);
    }

    @Bean
    public Binding bindingAlphaQTopicEx(Queue queueAlpha, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueAlpha)
                .to(topicExchange)
                .with(RabbitMqConstants.getRoutingKey(RabbitMqConstants.Exchange.TOPIC, RabbitMqConstants.Queue.ALPHA));
    }

    @Bean
    public Binding bindingBetaQTopicEx(Queue queueBeta, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueBeta)
                .to(topicExchange)
                .with(RabbitMqConstants.getRoutingKey(RabbitMqConstants.Exchange.TOPIC, RabbitMqConstants.Queue.BETA));
    }

    @Bean
    public Binding bindingDefaultQTopicEx(Queue queueDefault, TopicExchange topicExchange) {
        // routing key is given as * for matching pattern topic_exchange.*
        // If message is sent using topic exchange to topic_exchange.{any name} this queue will also get that message
        return BindingBuilder.bind(queueDefault)
                .to(topicExchange)
                .with(RabbitMqConstants.getRoutingKey(RabbitMqConstants.Exchange.TOPIC, "*"));
    }

    @Bean
    public Binding bindingAlphaQHeaderEx(Queue queueAlpha, HeadersExchange headersExchange) {
        // no routing key required for header exchange.
        // message will be sent to matching header key's value
        return BindingBuilder.bind(queueAlpha)
                .to(headersExchange).where(RabbitMqConstants.HEADER_KEY).matches(RabbitMqConstants.HeaderValue.LOW);
    }

    @Bean
    public Binding bindingBetaQHeaderEx(Queue queueBeta, HeadersExchange headersExchange) {
        // no routing key required for header exchange.
        // message will be sent to matching header key's value
        return BindingBuilder.bind(queueBeta)
                .to(headersExchange).where(RabbitMqConstants.HEADER_KEY).matches(RabbitMqConstants.HeaderValue.HIGH);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory){
        AmqpAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        QueueBuilder queueBuilder = QueueBuilder.durable(RabbitMqConstants.Queue.BACKUP);
        rabbitAdmin.declareQueue(queueBuilder.build());
        return rabbitAdmin;
    }
}



