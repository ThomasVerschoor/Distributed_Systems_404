package distributed.yproject.server;

import java.io.IOException;
import java.net.*;

public class UDPMultiServer extends Thread{

    private static boolean running = true;
    private static MulticastSocket multicastSocket;
    private static int sendPort;
    private static int multicastPort;
    private static String multicastAddress;

    public UDPMultiServer(int multicastPort, String multicastAddress, int sendPort) {
        UDPMultiServer.multicastAddress = multicastAddress;
        UDPMultiServer.sendPort = sendPort;
        UDPMultiServer.multicastPort = multicastPort;
        System.out.println("Starting UDPMulti server listening on port: "+multicastPort);
        System.out.println("Adding the multicastSocket to group with IP: "+UDPMultiServer.multicastAddress);
        try {
            UDPMultiServer.multicastSocket = new MulticastSocket(UDPMultiServer.multicastPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            UDPMultiServer.multicastSocket.joinGroup(InetAddress.getByName(UDPMultiServer.multicastAddress));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[255];
        while (UDPMultiServer.running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                UDPMultiServer.multicastSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            onDataReceived(packet);
        }
    }

    public void shutdown() {
        if (UDPMultiServer.multicastSocket.isClosed())
            UDPMultiServer.running = false;
        else{
            UDPMultiServer.multicastSocket.close();
            UDPMultiServer.running = false;
        }
    }

    private void onDataReceived(DatagramPacket packet) {
        InetAddress address = packet.getAddress();
        String data = new String(packet.getData(), 0, packet.getLength());
        System.out.println("MULTI[" + address + "]UDP packet received: " + data);
        handleData(address, data);
    }

    private void handleData(InetAddress hostAddress, String data) {
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
                NodeHandler.addNode(message, String.valueOf(hostAddress));
                if (NodeHandler.nodes.size() == 1)
                    sendUniCast("Other nodes in the network," + (NodeHandler.nodes.size() - 1), hostAddress);
                else
                    sendUniCast("Other nodes in the network," + (NodeHandler.nodes.size() - 1) + ", Previous ID: " + NodeHandler.getPrevious(NodeHandler.getKey(String.valueOf(hostAddress))) + ", Next ID: " + NodeHandler.getNext(NodeHandler.getKey(String.valueOf(hostAddress))), hostAddress);
                break;

            default:
                break;
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Amount of nodes in the network: " + NodeHandler.nodes.size());
        System.out.println("Map: " + NodeHandler.nodes);
        System.out.println("FileMap: " + FileHandler.files);
        System.out.println("----------------------------------------------------");
    }

    private void sendUniCast(String message, InetAddress hostAddress) {
        System.out.println("Sending unicast: [" + hostAddress + "]: " + message);
        if (message != null) {
            DatagramSocket UDPSocket = null;
            try {
                UDPSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, hostAddress, UDPMultiServer.sendPort);
            try {
                assert UDPSocket != null;
                UDPSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UDPSocket.close();
        }
    }
}