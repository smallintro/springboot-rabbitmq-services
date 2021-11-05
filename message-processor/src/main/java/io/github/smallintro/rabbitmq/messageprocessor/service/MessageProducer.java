package io.github.smallintro.rabbitmq.messageprocessor.service;


import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import io.github.smallintro.rabbitmq.messageprocessor.constant.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessageProducer {
    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    // Direct Exchange
    public void publishMessageToQueue(String message) throws IOException {
        logger.info("Sending message to Direct Exchange -> Alpha");
        Channel channel = AmqpManager.getChannel();
        channel.basicPublish(CommonConstants.Exchange.DIRECT, CommonConstants.Route.INTERNAL, null, message.getBytes());
        AmqpManager.closeChannel(channel);
    }

    // Fan-out Exchange
    public void publishMessageToAllQueues(String message) throws IOException {
        logger.info("Sending message to Fanout Exchange -> All");
        // no routing key required for the fanout exchange
        Channel channel = AmqpManager.getChannel();
        channel.basicPublish(CommonConstants.Exchange.FANOUT, "", null, message.getBytes());
        AmqpManager.closeChannel(channel);
    }

    // Topic Exchange
    public void publishMessageToMultipleQueues(String message) throws IOException {
        logger.info("Sending message to Topic Exchange -> Alpha, Default");
        // routing works as regex pattern. Message will be forwarded to multiple queues,
        // whose routing key partially matches with the binding key pattern
        // This message will be sent to BETA as well as DEFAULT queue, because binding key for both queues are matching with route.*
        Channel channel = AmqpManager.getChannel();
        channel.basicPublish(CommonConstants.Exchange.TOPIC, CommonConstants.Route.EXTERNAL, null, message.getBytes());
        AmqpManager.closeChannel(channel);
    }

    // Headers Exchange
    public void publishMessageToMatchingHeaders(String message, int priority) throws IOException {
        Map<String, Object> headerMap = new HashMap<>();
        if (priority == 1) {
            headerMap.put(CommonConstants.HEADER_KEY_1, CommonConstants.HeaderValue.HIGH);
        } else {
            headerMap.put(CommonConstants.HEADER_KEY_1, CommonConstants.HeaderValue.LOW);
        }
        if (message.length() <= 160) {
            headerMap.put(CommonConstants.HEADER_KEY_2, CommonConstants.HeaderValue.SHORT);
        } else {
            headerMap.put(CommonConstants.HEADER_KEY_2, CommonConstants.HeaderValue.LONG);
        }

        BasicProperties properties = new BasicProperties().builder().headers(headerMap).build();
        logger.info(String.format("Sending message to Header Exchange -> HEADER_KEY_1: %s, HEADER_KEY_2: %S",
                headerMap.get(CommonConstants.HEADER_KEY_1), headerMap.get(CommonConstants.HEADER_KEY_2)));
        // no routing key required for the header exchange. Routing will be done based on header property
        Channel channel = AmqpManager.getChannel();
        channel.basicPublish(CommonConstants.Exchange.HEADERS, "", properties, message.getBytes());
        AmqpManager.closeChannel(channel);
    }

    // Default Exchange
    public void publishMessageToDefault(String message) throws IOException {
        // When exchange name is not specified during sending message (message is sent to queue directly) ,
        // amqp will create a default exchange with random name as forward to message to queue via that exchange
        // Default exchange is binded to all the queues using queue name as routing key. routing key = queue name
        Channel channel = AmqpManager.getChannel();
        channel.basicPublish("", CommonConstants.Queue.DEFAULT, null, message.getBytes());
        AmqpManager.closeChannel(channel);
    }

}
