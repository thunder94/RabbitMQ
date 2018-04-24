import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Technician {

    public static void main(String[] argv) throws Exception {

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME1 = "exchange44";
        channel.exchangeDeclare(EXCHANGE_NAME1, BuiltinExchangeType.DIRECT);

        //set technician skills
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter first skill:");
        String firstSkill = br.readLine();
        System.out.println("Enter second skill:");
        String secondSkill = br.readLine();

        // queue & bind
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME1, firstSkill);
        channel.queueBind(queueName, EXCHANGE_NAME1, secondSkill);
        channel.queueBind(queueName, EXCHANGE_NAME1, "admin");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
                String[] splited = message.split("\\s+");
                if(!splited[0].equals("Admin:")) {
                    String reply = splited[1] + " " + splited[2] + " " + "done";
                    channel.basicPublish(EXCHANGE_NAME1, splited[0], null, reply.getBytes("UTF-8"));
                    channel.basicPublish(EXCHANGE_NAME1, "log", null, reply.getBytes("UTF-8"));
                }
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }
}