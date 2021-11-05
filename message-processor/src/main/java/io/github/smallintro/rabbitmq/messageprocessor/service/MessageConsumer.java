package io.github.smallintro.rabbitmq.messageprocessor.service;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import io.github.smallintro.rabbitmq.messageprocessor.constant.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    public static void consumeMessages() throws IOException {
        Channel channel = AmqpManager.getChannel();
        DeliverCallback deliverCallback = (s, delivery) -> {
            logger.info(String.format("[DeliverCallback] Message received is %s", new String(delivery.getBody())));
            logger.info(String.format("Exchange Info: %s", delivery.getEnvelope()));
        };
        CancelCallback cancelCallback = s -> {
            logger.error(String.format("[CancelCallback]: %s", s));
        };
        channel.basicConsume(CommonConstants.Queue.ALPHA, true, deliverCallback, cancelCallback);
        channel.basicConsume(CommonConstants.Queue.BETA, true, deliverCallback, cancelCallback);
        channel.basicConsume(CommonConstants.Queue.DEFAULT, true, deliverCallback, cancelCallback);
    }

    public static void subscribeMessage() throws IOException {
        Channel channel = AmqpManager.getChannel();
        channel.basicConsume(CommonConstants.Queue.ALPHA, true, ((consumerTag, message) -> {
            logger.info(consumerTag);
            logger.info(String.format("Message from [%s] is %s", CommonConstants.Queue.ALPHA, new String(message.getBody())));
            logger.info(String.format("Exchange Info: %s", message.getEnvelope()));
        }), consumerTag -> {
            logger.info(consumerTag);
        });
        channel.basicConsume(CommonConstants.Queue.BETA, true, ((consumerTag, message) -> {
            logger.info(consumerTag);
            logger.info(String.format("Message from [%s] is %s", CommonConstants.Queue.BETA, new String(message.getBody())));
            logger.info(String.format("Exchange Info: %s", message.getEnvelope()));
        }), consumerTag -> {
            logger.info(consumerTag);
        });
        channel.basicConsume(CommonConstants.Queue.DEFAULT, true, ((consumerTag, message) -> {
            logger.info(consumerTag);
            logger.info(String.format("Message from [%s] is %s", CommonConstants.Queue.DEFAULT, new String(message.getBody())));
            logger.info(String.format("Exchange Info: %s", message.getEnvelope()));
        }), consumerTag -> {
            logger.info(consumerTag);
        });
    }

    public static void startConsumer() {
        Thread subscribe = new Thread(() -> {
            try {
                subscribeMessage();
               // consumeMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        subscribe.start();
    }

}
