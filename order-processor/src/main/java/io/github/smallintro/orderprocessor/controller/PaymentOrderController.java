package io.github.smallintro.orderprocessor.controller;

import io.github.smallintro.orderprocessor.model.PaymentInfo;
import io.github.smallintro.orderprocessor.rabbitmq.RabbitMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
    RabbitMQProducer messagePublisher;

    @PostMapping("/payment/{request_id}")
    public ResponseEntity postPayment(@PathVariable("request_id") final String requestId, @RequestBody PaymentInfo paymentInfo) {
        logger.info(String.format("Payment request received with request_id: %s", requestId));
        try {
            if (StringUtils.hasLength(paymentInfo.getPaymentId())) {
                paymentInfo.setPaymentId(UUID.randomUUID().toString());
            }
            if (StringUtils.hasLength(paymentInfo.getPaymentPriority())) {
                // Send to Headers Exchange if payment priority is set
                messagePublisher.publishMessageToMatchingHeaders(paymentInfo, requestId);
            } else if (paymentInfo.getPaymentAmount() > 49999.99) {
                // Send to Topic Exchange if Payment amount is greater than 49999.99
                messagePublisher.publishMessageToMultipleQueues(paymentInfo, requestId);
            } else if ("POSTPAID".equals(paymentInfo.getPaymentType())) {
                // Send to Direct Exchange if its Postpaid Payment
                messagePublisher.publishMessageToQueue(paymentInfo, requestId);
            } else if ("PREPAID".equals(paymentInfo.getPaymentType())) {
                // Send to Fan-out Exchange if its Prepaid Payment
                messagePublisher.publishMessageToAllQueues(paymentInfo, requestId);
            } else {
                // Use Default Exchange if no condition matched
                messagePublisher.publishMessageToDefault(paymentInfo, requestId);
            }
            return new ResponseEntity(String.format("Payment request with request_id: %s is accepted", requestId), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(String.format("Payment request with request_id: %s is failed because of %s", requestId, ex.getMessage()));
            return new ResponseEntity(String.format("Payment request with request_id: %s is failed. Check request or retry after sometime", requestId),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
