import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {



    public static void main(String[] args) throws Exception {
        int number = 0;     //number of nodes in the network
        int currentID = 0;

        //get hostname and IP input from user
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Give hostname of this node: ");
        String hostname = keyboard.nextLine();
        System.out.println(" ");
        System.out.println("Give IP address of this node: ");
        String IP = keyboard.nextLine();

        //node information
        Node thisNode = new Node(hostname, IP);

        //multicast hostname and IP of new node
        multicast(thisNode.getHostname(), thisNode.getIP()); //send hostname and IP with multicast

        //receive message from server
        String message1 = receiveUnicastMessage();

        //convert message1 to int
        number = Integer.valueOf(message1);
        System.out.println(" ");
        System.out.println("There are currently " +number+ " other nodes in the network.");
        currentID = thisNode.getCurrentID();



        if (number < 1) {
            //no other nodes, so nextID = prevID = currentID
            thisNode.setPreviousID(currentID);
            thisNode.setNextID(currentID);

            System.out.println(" ");
            System.out.println("PreviousID: " +thisNode.getPreviousID()+ " CurrentID: " +currentID+  " NextID: " +thisNode.getNextID());
        }
        else {

            //convert message1 and message2 to int
            message1 = receiveUnicastMessage();
            String message2 = receiveUnicastMessage();

            int currentSending = Integer.valueOf(message1);
            int nextPrev = Integer.valueOf(message2);

            if (currentSending < nextPrev) {
                thisNode.setPreviousID(currentSending);
            }
            else if (nextPrev < currentSending) {
                thisNode.setNextID(currentSending);
            }

            System.out.println(" ");
            System.out.println("PreviousID: " +thisNode.getPreviousID()+ " CurrentID: " +currentID+  " NextID: " +thisNode.getNextID());

        }

        //listen for new nodes
        //and update parameters
        while(true) {
            String newNodeHostname = receiveMulticastMessage();
            String newNodeIP = receiveMulticastMessage();

            Node newNode = new Node(newNodeHostname, newNodeIP);

            if (currentID < newNode.getCurrentID() && newNode.getCurrentID() < thisNode.getNextID()) {
                //update nextID
                thisNode.setNextID(newNode.getCurrentID());
                //send updated parameters to new node
                unicast(createMessage(Integer.toString(thisNode.getCurrentID())), newNode.getIP());
                unicast(createMessage(Integer.toString(thisNode.getNextID())), newNode.getIP());

                System.out.println(" ");
                System.out.println("PreviousID: " +thisNode.getPreviousID()+ " CurrentID: " +currentID+  " NextID: " +thisNode.getNextID());
            }
            else if (thisNode.getPreviousID() < newNode.getCurrentID() && newNode.getCurrentID() < thisNode.getCurrentID()) {
                //update nextID
                thisNode.setPreviousID(newNode.getCurrentID());
                //send updated parameters to new node
                unicast(createMessage(Integer.toString(thisNode.getCurrentID())), newNode.getIP());
                unicast(createMessage(Integer.toString(thisNode.getPreviousID())), newNode.getIP());

                System.out.println(" ");
                System.out.println("PreviousID: " +thisNode.getPreviousID()+ " CurrentID: " +currentID+  " NextID: " +thisNode.getNextID());
            }
        }
    }

    public static String createMessage (String message){
        int lenght = message.length();
        System.out.println(lenght);
        String msgToSend = message;
        System.out.println(msgToSend);

        for ( int i=lenght;i<6;i++)
        {
            msgToSend = "0"+msgToSend;
        }

        return msgToSend;
    }

    public static String receiveMulticastMessage() throws Exception {
        System.out.println("Listening for multicast message.");

        byte[] buffer = new byte[100];
        System.setProperty("java.net.preferIPv4Stack", "true");
        InetAddress group = InetAddress.getByName("225.6.7.8");     //multicast address
        MulticastSocket multicastSocket = new MulticastSocket(3456);        //same port
        multicastSocket.joinGroup(group);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        multicastSocket.receive(packet);        //will not get past line until it receives packet



        System.out.println(new String(buffer)); //print out received number to console
        System.out.println("Socket closed");

        return new String(buffer);
    }

    public static String receiveUnicastMessage() throws Exception {
        System.out.println("Listening for unicast message.");

        byte[] buffer = new byte[6];
        System.setProperty("java.net.preferIPv4Stack", "true");
        DatagramSocket socket = new DatagramSocket(3455);        //same port
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);        //will not get past line until it receives packet
        socket.close();

        //System.out.println(new String(packet.getData())); //print out received number to console
        //System.out.println("Socket closed");

        return new String(packet.getData());
    }

    public static void unicast(String message, String IP) {
        System.setProperty("java.net.preferIPv4Stack", "true");     //ipv4 gebruiken
        try{
            //wait 10 ms so that data is certainly read by receiving side
            TimeUnit.SECONDS.sleep(5);

            InetAddress address = InetAddress.getByName(IP);
            DatagramSocket socket = new DatagramSocket(); //create new socket
            System.out.println("message sent");
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, 3456);

            //wait 10 ms so that data is certainly read by receiving side
            TimeUnit.MILLISECONDS.sleep(10);

            socket.send(packet);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void multicast(String hostname, String IP) {
        //Multicast IP and hostname to all nodes in network including server
        System.out.println("Multicast: " +hostname+ ", " +IP);

        System.setProperty("java.net.preferIPv4Stack", "true");     //ipv4 gebruiken
        try{
            InetAddress group = InetAddress.getByName("225.6.7.8"); //multicast address
            MulticastSocket socket = new MulticastSocket(); //create new socket
            //System.out.println("message sent");
            DatagramPacket packet = new DatagramPacket(hostname.getBytes(), hostname.length(), group, 3456);
            socket.send(packet);

            //wait 10 ms so that data is certainly read by receiving side
            TimeUnit.MILLISECONDS.sleep(10);

            packet = new DatagramPacket(IP.getBytes(), IP.length(), group, 3456);
            socket.send(packet);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
