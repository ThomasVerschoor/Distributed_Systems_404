import java.io.IOException;
import java.net.*;

public class UDPReceiveHandler extends Thread {

    private static int recievePort;
    private static DatagramSocket recieveSocket;
    private static boolean running = true;

    public UDPReceiveHandler(int recievePort){
        UDPReceiveHandler.recievePort = recievePort;
        System.out.println("Starting UDPRecieve handler: ");
        try {
            recieveSocket = new DatagramSocket(UDPReceiveHandler.recievePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while (running) {
            byte[] buffer = new byte[255];
            while (UDPReceiveHandler.running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    recieveSocket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                onDataReceived(packet);
            }
        }
    }

    public void shutdown() {
        System.out.println("Quiting UDPRecieve handler...");
        if(UDPReceiveHandler.recieveSocket.isClosed()) {
            running = false;
        }else{
            UDPReceiveHandler.recieveSocket.close();
            running = false;
        }
    }

    private void onDataReceived(DatagramPacket packet) {
        InetAddress address = packet.getAddress();
        String data = new String(packet.getData(), 0, packet.getLength());
        System.out.println("UNI[" + address + "]UDP packet received: " + data);
        handleData(data);
    }

    private void handleData(String data) {
        //Other nodes in the network,<amount>, Previous ID: <ID>, Next ID: <ID>
        //NewNext,<nextID>,<current>
        //NewPrev,<nextID>,<current>
        int index = data.indexOf(",");
        String command = null;
        String message = null;
        if (index != -1) {
            command = data.substring(0,index);
            message = data.substring(index+1);
        }
        assert command != null;
        switch (command){
            case "Other nodes in the network":
                YClient.updateInitial(message);
                break;
            case "NewNext":
                YClient.exitUpdateNext(message);
                break;
            case "NewPrev":
                YClient.exitUpdatePrev(message);
                break;
            default:
                break;
        }
    }
}

