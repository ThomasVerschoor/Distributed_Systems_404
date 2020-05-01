import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Main {

    public static void main(String[] args) {
        System.out.println("Multicast_Client!");
        System.setProperty("java.net.preferIPv4Stack", "true");
        try{
            InetAddress group = InetAddress.getByName("225.6.7.8");     //same group
            MulticastSocket multicastSocket = new MulticastSocket(3456);        //same port
            multicastSocket.joinGroup(group);

            int i;
            for (i = 0; i<10;i++) {      //delaying a bit, listen longer
                byte[] buffer = new byte[100];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);        //will not get past line until it receives packet
                System.out.println(new String(buffer)); //print out received packet to console

            }
            System.out.println("Socket closed");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
