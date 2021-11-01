package io.github.smallintro.orderprocessor.controller;

import io.github.smallintro.orderprocessor.model.PaymentInfo;
import io.github.smallintro.orderprocessor.rabbitmq.RabbitMQPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/order")
public class PaymentOrderController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentOrderController.class);

    @Autowired
    RabbitMQPublisher messagePublisher;

    @PostMapping("/payment/{request_id}")
    public ResponseEntity postPayment(@PathVariable("request_id") final String requestId, @RequestBody PaymentInfo paymentInfo) {
        logger.info(String.format("Payment request received with request_id: %s", requestId));
        try {
            paymentInfo.setPaymentId(UUID.randomUUID().toString());
            messagePublisher.publishMessageToQueue(paymentInfo, requestId);
            // messagePublisher.publishMessageToAllQueues(paymentInfo, requestId);
            // messagePublisher.publishMessageToMultipleQueues(paymentInfo, requestId);
            // messagePublisher.publishMessageToMatchingHeaders(paymentInfo, requestId);
            // messagePublisher.publishMessageToDefault(paymentInfo, requestId);
            return new ResponseEntity(String.format("Payment request with request_id: %s is accepted", requestId), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(String.format("Payment request failed with request_id: %s", requestId));
            return new ResponseEntity(String.format("Payment request with request_id: %s is failed because of %s", requestId, ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
