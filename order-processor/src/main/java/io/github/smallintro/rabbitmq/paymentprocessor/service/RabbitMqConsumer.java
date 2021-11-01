package io.github.smallintro.rabbitmq.paymentprocessor.service;

import io.github.smallintro.rabbitmq.paymentprocessor.constant.RabbitMqConstants;
import io.github.smallintro.rabbitmq.paymentprocessor.model.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConsumer.class);

    @RabbitListener(queues = RabbitMqConstants.Queue.ALPHA)
    public void consumeMessageFromAlpha(PaymentStatus paymentStatus) {
        logger.info(String.format("[%s] Received payment status: %s", RabbitMqConstants.Queue.ALPHA, paymentStatus));
        // logic to process the payment
    }

    @RabbitListener(queues = RabbitMqConstants.Queue.BETA)
    public void consumeMessageFromBeta(PaymentStatus paymentStatus) {
        logger.info(String.format("[%s] Received payment status: %s", RabbitMqConstants.Queue.BETA, paymentStatus));
        // logic to process the payment
    }

    @RabbitListener(queues = RabbitMqConstants.Queue.DEFAULT)
    public void consumeMessageFromDefault(PaymentStatus paymentStatus) {
        logger.info(String.format("[%s] Received payment status: %s", RabbitMqConstants.Queue.DEFAULT, paymentStatus));
        // logic to process the payment
    }
}
