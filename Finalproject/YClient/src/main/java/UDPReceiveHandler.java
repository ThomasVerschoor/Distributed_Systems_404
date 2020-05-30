import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPReceiveHandler extends Thread {

    private int recievePort;
    private DatagramSocket recieveSocket;
    private boolean running = true;
    private Logger logger;

    public UDPReceiveHandler(int recievePort, Logger logger) {
        this.recievePort = recievePort;
        this.logger = logger;
        this.logger.log(Level.INFO,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] Starting UDPRecieve handler listening on port: "+this.recievePort);
        try {
            this.logger.log(Level.INFO,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] Trying to create UDP UNICAST Socket..");
            recieveSocket = new DatagramSocket(this.recievePort);
            this.logger.log(Level.INFO,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] Done creating UDP UNICAST Socket...");
        } catch (SocketException e) {
            e.printStackTrace();
            this.logger.log(Level.SEVERE,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] Something went wrong trying to create the UDP UNICAST Socket");
        }
    }

    @Override
    public void run() {
        logger.log(Level.INFO,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] ID and name of UDP UNICAST Thread");
        while (running) {
            byte[] buffer = new byte[255];
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    recieveSocket.receive(packet);
                    logger.log(Level.INFO,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] UNICAST detected!");
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.log(Level.SEVERE,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] Something went wrong while waiting for UNICAST,it could be that client exited and socket was closed!");
                }
                onDataReceived(packet);
            }
        }
        recieveSocket.close();
    }

    public void shutdown() {
        logger.log(Level.INFO,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] Quiting UDPRecieve handler...");
        System.out.println("Quiting UDPRecieve handler...");
        if (recieveSocket.isClosed()) {
            running = false;
        } else {
            recieveSocket.close();
            logger.log(Level.INFO,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] Closing UNICAST socket");
            running = false;
        }
    }

    private void onDataReceived(DatagramPacket packet) {
        InetAddress address = packet.getAddress();
        String data = new String(packet.getData(), 0, packet.getLength());
        logger.log(Level.INFO,"[" + UDPReceiveHandler.currentThread().getId() + " | " + UDPReceiveHandler.currentThread().getName() + "] UNI[" + address + "]UDP packet received: " + data);
        handleData(data);
    }

    private void handleData(String data) {
        //Other nodes in the network,<amount>, Previous ID: <ID>, Next ID: <ID>
        //NewNext,<nextID>,<current>
        //NewPrev,<nextID>,<current>
        //Exit
        int index = data.indexOf(",");
        String command = null;
        String message = null;
        if (index != -1) {
            command = data.substring(0, index);
            message = data.substring(index + 1);
        }else {
            YClient.exit();
            command = " "; //fix nullpointerexception
        }

        assert command != null;
        switch (command) {
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

