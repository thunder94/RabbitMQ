import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Doctor {

    public static void main(String[] argv) throws Exception {

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME1 = "exchange44";
        channel.exchangeDeclare(EXCHANGE_NAME1, BuiltinExchangeType.DIRECT);

        //set doctor name
        System.out.println("Enter doctor name:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String doctorName = br.readLine();

        // queue & bind
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME1, doctorName);
        channel.queueBind(queueName, EXCHANGE_NAME1, "admin");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
            }
        };

        channel.basicConsume(queueName, true, consumer);

        while (true) {
            // read msg
            System.out.println("Enter body part and patient's surname: ");
            String input = br.readLine();
            String[] splited = input.split("\\s+");
            if(splited[0].equals("elbow")) {
                String message = doctorName + " " + "elbow" + " " + splited[1];
                channel.basicPublish(EXCHANGE_NAME1, "elbow", null, message.getBytes("UTF-8"));
                channel.basicPublish(EXCHANGE_NAME1, "log", null, message.getBytes("UTF-8"));
            } else if(splited[0].equals("knee")) {
                String message = doctorName + " " + "knee" + " " + splited[1];
                channel.basicPublish(EXCHANGE_NAME1, "knee", null, message.getBytes("UTF-8"));
                channel.basicPublish(EXCHANGE_NAME1, "log", null, message.getBytes("UTF-8"));
            } else if(splited[0].equals("hip")) {
                String message = doctorName + " " + "hip" + " " + splited[1];
                channel.basicPublish(EXCHANGE_NAME1, "hip", null, message.getBytes("UTF-8"));
                channel.basicPublish(EXCHANGE_NAME1, "log", null, message.getBytes("UTF-8"));
            }
        }
    }
}