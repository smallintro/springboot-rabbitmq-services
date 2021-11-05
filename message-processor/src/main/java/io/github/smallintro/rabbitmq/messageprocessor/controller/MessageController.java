package io.github.smallintro.rabbitmq.messageprocessor.controller;

import io.github.smallintro.rabbitmq.messageprocessor.service.AmqpManager;
import io.github.smallintro.rabbitmq.messageprocessor.service.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageProducer messagePublisher;

    @GetMapping("/{message_priority}/{message_text}")
    public ResponseEntity postPayment(@PathVariable("message_priority") final int priority, @PathVariable("message_text") final String message) {
        logger.info(String.format("Payment request received with message_priority: %s", priority));
        try {
            switch (priority) {
                case 1:
                case 2:
                    messagePublisher.publishMessageToMatchingHeaders(message, priority);
                    break;
                case 3:
                    messagePublisher.publishMessageToMultipleQueues(message);
                    break;
                case 4:
                    messagePublisher.publishMessageToQueue(message);
                    break;
                case 5:
                    messagePublisher.publishMessageToAllQueues(message);
                    break;
                default:
                    messagePublisher.publishMessageToDefault(message);
            }
            return new ResponseEntity(String.format("message is accepted and will be forwarded as per priority %d", priority), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(String.format("message processing failed because of %s", ex.getMessage()));
            return new ResponseEntity(String.format("message processing failed because of %s", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
