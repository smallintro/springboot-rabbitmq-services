# Payment Order Processor Service

RabbitMQ is an open-source message broker which accepts and forward the messages.
AMQP is a standard protocol for interoperability between messaging middleware.
AMQP main characteristics are: Security, Reliability, Interoperability, Open standard.

- Producer publish message to the Exchange.
- Exchange redirects the message to the Queue based on Routing Key.
- Routing Key works as binding key between Exchange and Queue.
- Consumer receives the message from the Queue.

### Exchanges
- Direct Exchange sends message to the queue where routing key = binding key.
- Fanout Exchange sends message to all the bound queues.
- Topic Exchange sends message to the multiple queues where routing key partially matches with binding key.
- Header Exchange sends message to queue based on header property key value instead of routing key. Routing key not required while binding.
- Default Exchange sends message to queue where routing key = queue name. No need to mention exchange while sending message. Its nameless exchange.
- Dead Letter Exchange: If no matching queue is found then the message is silently dropped. RabbitMQ provides this Amqp exchange which captures the messages that are not deliverable.

Install RabbitMQ Server on Windows:
-----------------------------
Guide to install and configure RabbitMQ on Windows https://rabbitmq.com/install-windows.html
1. Check ERlang and RabbitMQ compatible version https://rabbitmq.com/which-erlang.html
2. Download and install ERlang https://erlang.org/download/otp_versions_tree.html
> https://github.com/erlang/otp/releases/download/OTP-23.3.4/otp_win64_23.3.4.exe
3. Download and install RabbitMQ https://github.com/rabbitmq/rabbitmq-server/releases
> https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.9.8/rabbitmq-server-3.9.8.exe

Run RabbitMQ Server on Windows:
-----------------------------
1. Go to RabbitMQ Server install Directory sbin folder C:\Program Files\RabbitMQ Server\rabbitmq_server-x.y.z\sbin
2. Run command rabbitmq-plugins enable rabbitmq_management
3. Access RabbitMQ Management Dashboard http://127.0.0.1:15672
4. Login as guest user with guest password

Install RabbitMQ Server using Docker and Docker-Compose:
-------------------------------------------------------
Check docker and docker compose version installed.
Create a docker compose file and run command docker-compose up to download and install rabbitmq docker image.
> $ docker -v
> $ docker-compose -v
> $ docker-compose up
