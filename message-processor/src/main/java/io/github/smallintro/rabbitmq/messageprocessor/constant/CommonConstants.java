package io.github.smallintro.rabbitmq.messageprocessor.constant;

public interface CommonConstants {
    String AMQP_URL = "amqp://guest:guest@localhost:5672/";

    public interface Queue {
        String ALPHA = "queue.alpha";
        String BETA = "queue.beta";
        String DEFAULT = "queue.default";
    }

    public interface Exchange {
        String DIRECT = "exchange.direct";
        String FANOUT = "exchange.fanout";
        String TOPIC = "exchange.topic";
        String HEADERS = "exchange.headers";
    }

    public interface Route {
        String INTERNAL = "route.internal";
        String EXTERNAL = "route.external";
        String ALL = "";
        String INTERNAL_PATTERN = "*.internal";
        String EXTERNAL_PATTERN = "route.*";
        String ALL_PATTERN = "route.#";
    }

    String HEADER_KEY_1 = "priority";
    String HEADER_KEY_2 = "message-length";

    public interface HeaderValue {
        String LOW = "low";
        String HIGH = "high";
        String SHORT = "short message";
        String LONG = "long message";
    }

    static String getRoutingKey(String exchangeName, String queueName) {
        return exchangeName.concat(".").concat(queueName);
    }
}
