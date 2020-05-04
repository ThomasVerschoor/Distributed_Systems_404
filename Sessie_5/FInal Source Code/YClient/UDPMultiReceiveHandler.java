import java.io.IOException;
import java.net.*;

public class UDPMultiReceiveHandler extends Thread{

    private static int multiCastPort;
    private static String hostName;
    private static InetAddress multicastAddress;
    private static MulticastSocket multicastSocket;
    private static boolean running = true;

    public UDPMultiReceiveHandler(String hostName, InetAddress multicastAddress, int multiCastPort){
        UDPMultiReceiveHandler.hostName = hostName;
        UDPMultiReceiveHandler.multicastAddress = multicastAddress;
        UDPMultiReceiveHandler.multiCastPort = multiCastPort;
        System.out.println("Starting UDPMultiRecieve handler: ");
        try {
            multicastSocket = new MulticastSocket(UDPMultiReceiveHandler.multiCastPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            multicastSocket.joinGroup(UDPMultiReceiveHandler.multicastAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run(){
        while (running) {
            byte[] buffer = new byte[255];
            while (UDPMultiReceiveHandler.running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    multicastSocket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                onDataReceived(packet);
            }
        }
    }

    public void shutdown() {
        System.out.println("Quiting UDPMultiRecieve handler...");
        if(UDPMultiReceiveHandler.multicastSocket.isClosed()) {
            running = false;
        }else{
            UDPMultiReceiveHandler.multicastSocket.close();
            running = false;
        }
    }

    private void onDataReceived(DatagramPacket packet) {
        InetAddress address = packet.getAddress();
        String data = new String(packet.getData(), 0, packet.getLength());
        if (!data.equals("Start," + hostName))
        {
            System.out.println("MULTI[" + address + "]UDP packet received: " + data);
            handleData(address, data);
        }
    }


    private void handleData(InetAddress hostAddress, String data) {
        //Start,<hostName>
        int index = data.indexOf(",");
        String command = null;
        String message = null;
        if (index != -1) {
            command = data.substring(0,index);
            message = data.substring(index+1);
        }
        assert command != null;
        switch (command){
            case "Start":
                int ID = YClient.hashCode(message);
                YClient.update(ID,hostAddress);
                break;
            default:
                break;
        }
    }
}
