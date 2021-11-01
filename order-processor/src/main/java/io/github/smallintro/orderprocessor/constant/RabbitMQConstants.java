package io.github.smallintro.orderprocessor.constant;

public interface RabbitMQConstants {
    public interface Queue {
        String ALPHA = "alpha_queue";
        String BETA = "beta_queue";
        String DEFAULT = "default_queue";
    }

    public interface Exchange {
        String DIRECT = "direct_exchange";
        String FANOUT = "fanout_exchange";
        String TOPIC = "topic_exchange";
        String HEADER = "header_exchange";
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
