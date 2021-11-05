package io.github.smallintro.rabbitmq.messageprocessor;

import io.github.smallintro.rabbitmq.messageprocessor.service.AmqpManager;
import io.github.smallintro.rabbitmq.messageprocessor.service.MessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class MessageProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageProcessorApplication.class, args);
    }

    @Autowired
    MessageConsumer consumer;

    @PostConstruct
    public void initAmpq() throws IOException, TimeoutException {
        AmqpManager.getConnection();
        AmqpManager.getChannel();
        AmqpManager.createQueues();
        AmqpManager.createExchanges();
        AmqpManager.createBinding();
        consumer.startConsumer();
    }

    @PreDestroy
    public void destroyAmqp() throws IOException, TimeoutException {
        AmqpManager.closeChannels();
        AmqpManager.closeConnection();
    }
}
