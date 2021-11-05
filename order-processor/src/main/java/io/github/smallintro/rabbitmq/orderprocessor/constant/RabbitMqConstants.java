package io.github.smallintro.rabbitmq.orderprocessor.constant;

public interface RabbitMqConstants {
    public interface Queue {
        String ALPHA = "queue-alpha";
        String BETA = "queue-beta";
        String DEFAULT = "queue-default";
    }

    public interface Exchange {
        String DIRECT = "exchange-direct";
        String FANOUT = "exchange-fanout";
        String TOPIC = "exchange-topic";
        String HEADER = "exchange-header";
    }

    String HEADER_KEY = "priority";

    public interface HeaderValue {
        String LOW = "low";
        String HIGH = "high";
    }

    static String getRoutingKey(String exchangeName, String queueName) {
        return exchangeName.concat(".").concat(queueName);
    }


}
