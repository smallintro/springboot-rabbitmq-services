package io.github.smallintro.rabbitmq.messageprocessor.service;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.SetQueue;
import io.github.smallintro.rabbitmq.messageprocessor.constant.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component
public class AmqpManager {
    private static final Logger logger = LoggerFactory.getLogger(AmqpManager.class);

    @Value("${spring.rabbitmq.host:localhost}")
    private static String host;

    @Value("${spring.rabbitmq.port:5672}")
    private static Integer port;

    @Value("${spring.rabbitmq.username:guest}")
    private static String username;

    @Value("${spring.rabbitmq.password:guest}")
    private static String password;

    private static Connection connection;
    private static SetQueue<Channel> channelSet = new SetQueue<>();

    public static Connection getConnection() throws IOException, TimeoutException {
        if (connection == null) {
            logger.info("creating connection with amqp://{}:{}@{}:{}/",username, password, host, port);
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(host);
            connectionFactory.setPort(port);
            connectionFactory.setUsername(username);
            connectionFactory.setPassword(password);
            connection = connectionFactory.newConnection();
            logger.info(String.format("Connection created with %s", connection.getAddress().getHostName()));
        }
        return connection;
    }

    public static Channel getChannel() throws IOException {
        /* if (!channelSet.isEmpty()) {
            Channel channel = channelSet.poll();
            if (!channel.isOpen()) {
                connection.openChannel(channel.getChannelNumber());
                logger.info(String.format("Channel %d opened", channel.getChannelNumber()));
            }
            return channel;
        } */
        Channel channel = connection.createChannel();
        channelSet.addIfNotPresent(channel);
        logger.info(String.format("Channel %d create", channel.getChannelNumber()));
        return channel;
    }

    public static void createExchanges() {
        // Create queues - (exchange, type, durable, autoDelete, arguments)
        try {
            Channel channel = getChannel();
            // Exchange of type direct
            channel.exchangeDeclare(CommonConstants.Exchange.DIRECT, BuiltinExchangeType.DIRECT, true, false, null);
            // Exchange of type fanout
            channel.exchangeDeclare(CommonConstants.Exchange.FANOUT, BuiltinExchangeType.FANOUT, true, true, null);

            // Exchange of type topic
            // We can set an alternative exchange for any exchange.
            // if no queue is bound to the exchange with the matching routing key, message will be lost.
            // if alternative exchange is set then the message will be forwarded to the alternative exchange before lost.
            // alternative exchange can be of any type, Fandot exchange is mostly used as this exchange forwards message to all the bound queues
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("alternate-exchange", CommonConstants.Exchange.FANOUT); // exchange.fanout is set as alternative exchange for exchange.topic
            channel.exchangeDeclare(CommonConstants.Exchange.TOPIC, BuiltinExchangeType.TOPIC, true, false, arguments);

            // Exchange of type headers
            channel.exchangeDeclare(CommonConstants.Exchange.HEADERS, BuiltinExchangeType.HEADERS, true, true, null);
            logger.info("Exchanges are created");
            closeChannel(channel);
        } catch (IOException e) {
            logger.error(String.format("Create Exchanges failed. %s", e.getMessage()));
        }
    }

    public static void createQueues() {
        // Create queues - (queue, durable, exclusive, autoDelete, arguments)
        try {
            Channel channel = getChannel();
            channel.queueDeclare(CommonConstants.Queue.ALPHA, true, false, false, null);
            channel.queueDeclare(CommonConstants.Queue.BETA, true, false, false, null);
            channel.queueDeclare(CommonConstants.Queue.DEFAULT, true, false, false, null);
            logger.info("Queues are created");
            closeChannel(channel);
        } catch (IOException e) {
            logger.error(String.format("Create Queues failed. %s", e.getMessage()));
        }
    }

    public static void createBinding() {
        // Create bindings - (queue, exchange, routingKey)
        try {
            Channel channel = getChannel();
            channel.queueBind(CommonConstants.Queue.ALPHA, CommonConstants.Exchange.DIRECT, CommonConstants.Route.INTERNAL);
            channel.queueBind(CommonConstants.Queue.BETA, CommonConstants.Exchange.DIRECT, CommonConstants.Route.EXTERNAL);
            channel.queueBind(CommonConstants.Queue.DEFAULT, CommonConstants.Exchange.DIRECT, CommonConstants.Route.INTERNAL);

            channel.queueBind(CommonConstants.Queue.ALPHA, CommonConstants.Exchange.FANOUT, CommonConstants.Route.ALL);
            channel.queueBind(CommonConstants.Queue.BETA, CommonConstants.Exchange.FANOUT, CommonConstants.Route.ALL);
            channel.queueBind(CommonConstants.Queue.DEFAULT, CommonConstants.Exchange.FANOUT, CommonConstants.Route.ALL);

            // routing key with regular expression *, # and . allowed
            // * = exactly one word, # = zero or more word(s), . = word delimiter
            channel.queueBind(CommonConstants.Queue.ALPHA, CommonConstants.Exchange.TOPIC, CommonConstants.Route.INTERNAL_PATTERN);
            channel.queueBind(CommonConstants.Queue.BETA, CommonConstants.Exchange.TOPIC, CommonConstants.Route.EXTERNAL_PATTERN);
            channel.queueBind(CommonConstants.Queue.DEFAULT, CommonConstants.Exchange.TOPIC, CommonConstants.Route.ALL_PATTERN);

            /**
             * There are 2 types of headers matching allowed which are any (similar to logical OR) or all (similar to logical AND).
             * x-match = any means, a message should contain at least one of the headers that Queue is linked with,
             * x-match = all, a message should contain all the headers that Queue is linked with
             */
            // Create bindings - (queue, exchange, routingKey, headers) - routingKey != null
            Map<String, Object> alphaHeaders = new HashMap<>();
            alphaHeaders.put("x-match", "any"); //Match any of the header
            alphaHeaders.put(CommonConstants.HEADER_KEY_1, CommonConstants.HeaderValue.HIGH);
            alphaHeaders.put(CommonConstants.HEADER_KEY_2, CommonConstants.HeaderValue.SHORT);
            Map<String, Object> betaHeaders = new HashMap<>();
            betaHeaders.put("x-match", "all"); //Match all the header
            betaHeaders.put(CommonConstants.HEADER_KEY_1, CommonConstants.HeaderValue.LOW);
            betaHeaders.put(CommonConstants.HEADER_KEY_2, CommonConstants.HeaderValue.LONG);
            channel.queueBind(CommonConstants.Queue.ALPHA, CommonConstants.Exchange.HEADERS, "", alphaHeaders);
            channel.queueBind(CommonConstants.Queue.BETA, CommonConstants.Exchange.HEADERS, "", betaHeaders);

            // Two different exchanges of same or different type also can be bind together
            // message from source exchange will be forwarded to target exchange matching with the routing key
            // target exchange will further forward the message to the queue, bound with target exchange with matching routing key rule used to bind two exchanges.
            channel.exchangeBind(CommonConstants.Exchange.DIRECT, CommonConstants.Exchange.TOPIC, CommonConstants.Route.EXTERNAL);

            logger.info("Bindings are created");
            closeChannel(channel);
        } catch (IOException e) {
            logger.error(String.format("Create Bindings failed. %s", e.getMessage()));
        }
    }

    public static void closeConnection() {
        try {
            if (null != connection && connection.isOpen()) {
                connection.close();
            }
        } catch (IOException e) {
            logger.error(String.format("Connection close failed. %s", e.getMessage()));
        }
        logger.info(String.format("Connection closed with %s", connection.getAddress().getHostName()));
    }

    public static void closeChannel(Channel channel) {
        try {
            if (null != channel && channel.isOpen()) {
                channel.close();
            }
        } catch (IOException | TimeoutException e) {
            logger.error(String.format("Channel %d  close failed. %s", channel.getChannelNumber(), e.getMessage()));
        }
        logger.info(String.format("Channel %d closed", channel.getChannelNumber()));
    }

    public static void closeChannels() {
        while (!channelSet.isEmpty()) {
            closeChannel(channelSet.poll());
        }
    }
}
