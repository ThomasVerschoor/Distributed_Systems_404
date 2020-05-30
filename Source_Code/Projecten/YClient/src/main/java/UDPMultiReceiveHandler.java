import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPMultiReceiveHandler extends Thread {

    private int multiCastPort;
    private String hostName;
    private InetAddress multicastAddress;
    private MulticastSocket multicastSocket;
    private boolean running = true;
    private Logger logger;

    public UDPMultiReceiveHandler(String hostName, InetAddress multicastAddress, int multiCastPort, Logger logger) {
        this.hostName = hostName;
        this.multicastAddress = multicastAddress;
        this.multiCastPort = multiCastPort;
        this.logger = logger;
        this.logger.log(Level.INFO,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] Starting UDP MULTICAST listener on port: "+this.multiCastPort+" with address: "+this.multicastAddress);
        try {
            this.logger.log(Level.INFO,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] Trying to create UDP MULTICAST Socket...");
            multicastSocket = new MulticastSocket(this.multiCastPort);
            this.logger.log(Level.INFO,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] Done creating UDP MULTICAST Socket...");
        } catch (IOException e) {
            e.printStackTrace();
            this.logger.log(Level.SEVERE,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] Something went wrong trying to created UDP MULTICAST Socket");
        }
        try {
            this.logger.log(Level.INFO,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] Trying to join MULTICAST Group...");
            multicastSocket.joinGroup(this.multicastAddress);
            this.logger.log(Level.INFO,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] Done joining MULTICAST Group...");
        } catch (IOException e) {
            e.printStackTrace();
            this.logger.log(Level.SEVERE,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] Something went wrong trying to join the MULTICAST Group");
        }

    }

    @Override
    public void run() {
        logger.log(Level.INFO,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] ID and name of UDP MULTICAST Thread");
        while (running) {
            byte[] buffer = new byte[255];
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    multicastSocket.receive(packet);
                    logger.log(Level.INFO,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] MULTICAST detected!");
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.log(Level.SEVERE,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] Something went wrong while waiting for MULTICAST,it could be that client exited and socket was closed!");
                }
                onDataReceived(packet);
            }
        }
    }

    public void shutdown() {
        System.out.println("Quiting UDPMultiRecieve handler...");
        if (multicastSocket.isClosed()) {
            running = false;
        } else {
            logger.log(Level.INFO,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] closing MULTICAST socket");
            multicastSocket.close();
            running = false;
        }
    }

    private void onDataReceived(DatagramPacket packet) {
        InetAddress address = packet.getAddress();
        String data = new String(packet.getData(), 0, packet.getLength());
        if (!data.equals("Start," + hostName)) {
            logger.log(Level.INFO,"[" + UDPMultiReceiveHandler.currentThread().getId() + " | " + UDPMultiReceiveHandler.currentThread().getName() + "] MULTI[" + address + "]UDP packet received: " + data);
            handleData(address, data);
        }
    }


    private void handleData(InetAddress hostAddress, String data) {
        //Start,<hostName>
        //Delete,<fileName>
        int index = data.indexOf(",");
        String command = null;
        String message = null;
        if (index != -1) {
            command = data.substring(0, index);
            message = data.substring(index + 1);
        }
        assert command != null;
        switch (command) {
            case "Start":
                int ID = YClient.hashCode(message);
                YClient.update(ID, hostAddress);
                break;

            case "Delete":
                YClient.removeReplicatedFile(message);
                break;

            default:
                break;
        }
    }
}
