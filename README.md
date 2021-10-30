# Payment Order Processor Service

RabbitMQ is a message broker which accepts and forward the message

- Producer publish message to the Exchange.
- Exchange redirects the message to the Queue based on Routing Key.
- Routing Key works as binding key between Exchange and Queue.
- Consumer receives the message from the Queue.

### Exchanges
- Direct Exchange sends message to the queue where routing key = binding key.
- Fanout Exchange sends message to all the bound queues.
- Topic Exchange sends message to the multiple queues where routing key partially matches with binding key.
- Header Exchange sends message to queue based on header property key value instead of routing key. Routing key not required while binding.
- Default Exchange sends to queue where routing key = queue name. No need to mention exchange while sending message. Its nameless exchange

### Download and Install RabbitMQ

Install RabbitMQ in windows :
-----------------------------
0. Check ERlang and RabbitMQ compatible version https://rabbitmq.com/which-erlang.html
1. Download and install ERlang https://www.erlang.org/downloads
2. Download and install RabbitMQ https://rabbitmq.com/install-windows.html
3. Go to RabbitMQ Server install Directory sbin folder C:\Program Files\RabbitMQ Server\rabbitmq_server-x.y.z\sbin
4. Run command rabbitmq-plugins enable rabbitmq_management
5. Open browser and enter http://127.0.0.1:15672 to redirect to RabbitMQ Dashboard
6. Login with default username and password is guest