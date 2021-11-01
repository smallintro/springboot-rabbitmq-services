package io.github.smallintro.rabbitmq.paymentprocessor.service;

import io.github.smallintro.rabbitmq.paymentprocessor.constant.RabbitMqConstants;
import io.github.smallintro.rabbitmq.paymentprocessor.model.PaymentInfo;
import io.github.smallintro.rabbitmq.paymentprocessor.model.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqProducer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqProducer.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    // Direct Exchange
    public void publishMessageToQueue(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);
        logger.info("Sending message to Direct Exchange -> Alpha");
        rabbitTemplate.convertAndSend(RabbitMqConstants.Exchange.DIRECT,
                RabbitMqConstants.getRoutingKey(RabbitMqConstants.Exchange.DIRECT, RabbitMqConstants.Queue.ALPHA),
                paymentStatus);
    }

    // Fan-out Exchange
    public void publishMessageToAllQueues(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);
        logger.info("Sending message to Fanout Exchange -> All");
        // no routing key required for the fanout exchange
        rabbitTemplate.convertAndSend(RabbitMqConstants.Exchange.FANOUT, "", paymentStatus);
    }

    // Topic Exchange
    public void publishMessageToMultipleQueues(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);
        logger.info("Sending message to Topic Exchange -> Alpha, Default");
        // routing works as regex pattern. Message will be forwarded to multiple queues,
        // whose routing key partially matches with the binding key pattern
        // This message will be sent to Alpha as well as Default queue, because binding key for default queue is topic_exchange.*
        rabbitTemplate.convertAndSend(RabbitMqConstants.Exchange.TOPIC,
                RabbitMqConstants.getRoutingKey(RabbitMqConstants.Exchange.TOPIC, RabbitMqConstants.Queue.ALPHA),
                paymentStatus);
    }

    // Headers Exchange
    public void publishMessageToMatchingHeaders(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(RabbitMqConstants.HEADER_KEY, paymentInfo.getPaymentPriority());
        MessageConverter messageConverter = new Jackson2JsonMessageConverter();
        Message message = messageConverter.toMessage(paymentStatus, messageProperties);
        logger.info("Sending message to Header Exchange -> Alpha(Low), Beta(High)");
        // no routing key required for the header exchange. Routing will be done based on header property
        rabbitTemplate.convertAndSend(RabbitMqConstants.Exchange.HEADER, "", message);
    }

    // Default Exchange
    public void publishMessageToDefault(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);
        // routing key will be queue name for default exchange. Exchange name not required
        rabbitTemplate.convertAndSend(RabbitMqConstants.Queue.BETA, paymentStatus);
    }

}
