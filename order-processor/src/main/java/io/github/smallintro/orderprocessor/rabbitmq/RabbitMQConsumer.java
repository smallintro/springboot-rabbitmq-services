package io.github.smallintro.orderprocessor.rabbitmq;

import io.github.smallintro.orderprocessor.constant.RabbitMQConstants;
import io.github.smallintro.orderprocessor.model.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @RabbitListener(queues = RabbitMQConstants.Queue.ALPHA)
    public void consumeMessageFromAlpha(PaymentStatus paymentStatus) {
        logger.info(String.format("[%s] Received payment status: %s", RabbitMQConstants.Queue.ALPHA, paymentStatus));
        // logic to process the payment
    }

    @RabbitListener(queues = RabbitMQConstants.Queue.BETA)
    public void consumeMessageFromBeta(PaymentStatus paymentStatus) {
        logger.info(String.format("[%s] Received payment status: %s", RabbitMQConstants.Queue.BETA, paymentStatus));
        // logic to process the payment
    }

    @RabbitListener(queues = RabbitMQConstants.Queue.DEFAULT)
    public void consumeMessageFromDefault(PaymentStatus paymentStatus) {
        logger.info(String.format("[%s] Received payment status: %s", RabbitMQConstants.Queue.DEFAULT, paymentStatus));
        // logic to process the payment
    }
}
