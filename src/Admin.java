import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Admin {

    public static void main(String[] argv) throws Exception {

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME1 = "exchange44";
        channel.exchangeDeclare(EXCHANGE_NAME1, BuiltinExchangeType.DIRECT);

        // queue & bind
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME1, "log");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
            }
        };

        channel.basicConsume(queueName, true, consumer);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Enter info message:");
            String message = "Admin: " + br.readLine();
            channel.basicPublish(EXCHANGE_NAME1, "admin", null, message.getBytes("UTF-8"));
        }
    }
}