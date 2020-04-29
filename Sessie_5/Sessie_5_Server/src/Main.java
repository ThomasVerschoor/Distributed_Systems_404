import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        // write your code here
        HashMap<Integer, String> nodes = new HashMap<Integer, String>();

        while (true) {
            String hostname = receiveMessage();
            System.out.println("received hostname");
            String IP = receiveMessage();
            System.out.println("received IP");

            Node newNode = new Node(hostname, IP);

            nodes.put(newNode.getID(), IP);
            System.out.println("New node added to list with hash: " + newNode.getID() + " and IP: " + newNode.getIP());

            String message = createMessage(Integer.toString(nodes.size() - 1));

            sendMessage(message, newNode.getIP());
            System.out.println("There are currently " + (nodes.size() - 1) + " other nodes in the network.");
        }
    }

    public static void sendMessage(String message, String IP) {

        System.setProperty("java.net.preferIPv4Stack", "true");     //ipv4 gebruiken
        try {
            InetAddress address = InetAddress.getByName(IP); //replace by ip
            DatagramSocket socket = new DatagramSocket(3457); //create new socket
            System.out.println("message sent");
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, 3455);

            //wait 10 ms so that data is certainly read by receiving side
            TimeUnit.MILLISECONDS.sleep(10);

            socket.send(packet);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createMessage(String message) {
        int length = message.length();
        System.out.println("Adding " + (6 - length) + " zeros to the message.");
        String msgToSend = message;
        System.out.println(msgToSend);

        for (int i = length; i < 6; i++) {
            msgToSend = "0" + msgToSend;
        }

        return msgToSend;
    }

    public static String receiveMessage() {
        System.out.println("Listening for multicast message");

        byte[] buffer = new byte[100];
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
            InetAddress group = InetAddress.getByName("225.6.7.8");     //multicast address
            MulticastSocket multicastSocket = new MulticastSocket(3456);        //same port
            multicastSocket.joinGroup(group);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            multicastSocket.receive(packet);        //will not get past line until it receives packet
            //System.out.println("Message received: " +new String(buffer)); //print out received number to console
            //System.out.println("Socket closed");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String(buffer);
    }
}
