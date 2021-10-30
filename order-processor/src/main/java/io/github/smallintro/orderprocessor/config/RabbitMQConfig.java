package io.github.smallintro.orderprocessor.config;

import io.github.smallintro.orderprocessor.constant.RabbitMQConstants;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration is same for producer and consumer
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue queue() {
        // this queue will be created with random name by amqp and will be auto deleted
        // can also specify the naming strategy as argument
        return new AnonymousQueue();
    }

    @Bean
    public Queue queueAlpha() {
        return new Queue(RabbitMQConstants.Queue.ALPHA, false);
    }

    @Bean
    public Queue queueBeta() {
        return new Queue(RabbitMQConstants.Queue.BETA, false);
    }

    @Bean
    public Queue queueDefault() {
        return new Queue(RabbitMQConstants.Queue.DEFAULT);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(RabbitMQConstants.Exchange.DIRECT);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitMQConstants.Exchange.FANOUT);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitMQConstants.Exchange.TOPIC);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(RabbitMQConstants.Exchange.HEADER);
    }

    @Bean
    public Binding bindingAlphaQDirectEx(Queue queueAlpha, DirectExchange directExchange) {
        return BindingBuilder.bind(queueAlpha)
                .to(directExchange)
                .with(RabbitMQConstants.getRoutingKey(RabbitMQConstants.Exchange.DIRECT, RabbitMQConstants.Queue.ALPHA));
    }

    @Bean
    public Binding bindingBetaQDirectEx(Queue queueBeta, DirectExchange directExchange) {
        return BindingBuilder.bind(queueBeta)
                .to(directExchange)
                .with(RabbitMQConstants.getRoutingKey(RabbitMQConstants.Exchange.DIRECT, RabbitMQConstants.Queue.BETA));
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
                .with(RabbitMQConstants.getRoutingKey(RabbitMQConstants.Exchange.TOPIC, RabbitMQConstants.Queue.ALPHA));
    }

    @Bean
    public Binding bindingBetaQTopicEx(Queue queueBeta, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueBeta)
                .to(topicExchange)
                .with(RabbitMQConstants.getRoutingKey(RabbitMQConstants.Exchange.TOPIC, RabbitMQConstants.Queue.BETA));
    }

    @Bean
    public Binding bindingDefaultQTopicEx(Queue queueDefault, TopicExchange topicExchange) {
        // routing key is given as * for matching pattern topic_exchange.*
        // If message is sent using topic exchange to topic_exchange.{any name} this queue will also get that message
        return BindingBuilder.bind(queueDefault)
                .to(topicExchange)
                .with(RabbitMQConstants.getRoutingKey(RabbitMQConstants.Exchange.TOPIC, "*"));
    }

    @Bean
    public Binding bindingAlphaQHeaderEx(Queue queueAlpha, HeadersExchange headersExchange) {
        // no routing key required for header exchange.
        // message will be sent to matching header key's value
        return BindingBuilder.bind(queueAlpha)
                .to(headersExchange).where(RabbitMQConstants.HEADER_KEY).matches(RabbitMQConstants.HeaderValue.LOW);
    }

    @Bean
    public Binding bindingBetaQHeaderEx(Queue queueBeta, HeadersExchange headersExchange) {
        // no routing key required for header exchange.
        // message will be sent to matching header key's value
        return BindingBuilder.bind(queueBeta)
                .to(headersExchange).where(RabbitMQConstants.HEADER_KEY).matches(RabbitMQConstants.HeaderValue.HIGH);
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
}



