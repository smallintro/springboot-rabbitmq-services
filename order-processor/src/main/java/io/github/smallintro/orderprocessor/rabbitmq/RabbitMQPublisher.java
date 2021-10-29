package io.github.smallintro.orderprocessor.rabbitmq;

import io.github.smallintro.orderprocessor.constant.RabbitMQConstants;
import io.github.smallintro.orderprocessor.model.PaymentInfo;
import io.github.smallintro.orderprocessor.model.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQPublisher {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQPublisher.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    // Direct Exchange
    public void publishMessageToQueue(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);
        rabbitTemplate.convertAndSend(RabbitMQConstants.Exchange.DIRECT,
                RabbitMQConstants.getRoutingKey(RabbitMQConstants.Exchange.DIRECT, RabbitMQConstants.Queue.ALPHA),
                paymentStatus);
    }

    // Fanout Exchange
    public void publishMessageToAllQueues(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);
        // no routing key required for the fanout exchange
        rabbitTemplate.convertAndSend(RabbitMQConstants.Exchange.FANOUT, "", paymentStatus);
    }

    // Topic Exchange
    public void publishMessageToMultipleQueues(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);
        // routing works as regex pattern. Message will be forwarded to multiple queues,
        // whose routing key partially matches with the binding key pattern
        // This message will be sent to Alpha as well as Default queue, because binding key for default queue is topic_exchange.*
        rabbitTemplate.convertAndSend(RabbitMQConstants.Exchange.TOPIC,
                RabbitMQConstants.getRoutingKey(RabbitMQConstants.Exchange.TOPIC, RabbitMQConstants.Queue.ALPHA),
                paymentStatus);
    }

    // Headers Exchange
    public void publishMessageToMatchingHeaders(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(RabbitMQConstants.HEADER_KEY, paymentInfo.getPaymentPriority());
        MessageConverter messageConverter = new SimpleMessageConverter();
        Message message = messageConverter.toMessage(paymentStatus, messageProperties);

        // no routing key required for the header exchange. Routing will be done based on header property
        rabbitTemplate.convertAndSend(RabbitMQConstants.Exchange.HEADER, "", message);
    }

    // Default Exchange
    public void publishMessageToDefault(PaymentInfo paymentInfo, String requestId) {
        PaymentStatus paymentStatus = new PaymentStatus(requestId, "Accepted",
                "Payment is in progress", paymentInfo);
        // routing key will be queue name for default exchange. Exchange name not required
        rabbitTemplate.convertAndSend(RabbitMQConstants.Queue.BETA, paymentStatus);
    }

}
