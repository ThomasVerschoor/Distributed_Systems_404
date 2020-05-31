package distributed.yproject.server;

import java.io.IOException;
import java.net.*;
import java.util.Map;

public class UDPServer extends Thread {

    private boolean running = true;
    private DatagramSocket recieveSocket;
    private int recievePort;
    private int sendPort;
    public static int amountOfNodes = 0;

    public UDPServer(int recievePort, int sendPort) {
        this.recievePort = recievePort;
        this.sendPort = sendPort;
        System.out.println("Starting UDP server listening on port: "+this.recievePort);
        try {
            this.recieveSocket = new DatagramSocket(this.recievePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[255];
        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                recieveSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            onDataReceived(packet);
        }
    }

    public void shutdown() {
        if (recieveSocket.isClosed())
            running = false;
        else{
            recieveSocket.close();
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
        //Example message:Exit,Curr: 654654654,Prev: 65456872,Next: 2635486454
        amountOfNodes = NodeHandler.nodes.size();

        int index = data.indexOf(",");
        String command = null;
        String message = null;
        if (index != -1) {
            command = data.substring(0, index);
            message = data.substring(index + 1);
        }
        assert command != null;
        switch (command) {
            case "Exit":
                //Extract data from message
                amountOfNodes = amountOfNodes - 1;
                String current;
                String previous;
                String next;
                index = (message.indexOf("Curr: ")) + 6;
                int indexx = message.indexOf(",Prev: ");
                current = message.substring(index, indexx);
                indexx = indexx + 7;
                index = message.indexOf(",Next: ");
                previous = message.substring(indexx, index);
                index = index + 7;
                next = message.substring(index);

                //Get IP of previous and next node
                String previousIP = NodeHandler.nodes.get(Integer.parseInt(previous));
                previousIP = previousIP.substring(1);
                String nextIP = NodeHandler.nodes.get(Integer.parseInt(next));
                nextIP = nextIP.substring(1);
                if (NodeHandler.nodes.size() == 1) {
                    System.out.println("Last node left the network...");
                    NodeHandler.removeNode(NodeHandler.nodes.get(Integer.parseInt(current)));
                }
                else{
                    try {
                        sendUniCast("NewNext," + next + "," + current, InetAddress.getByName(previousIP));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    try {
                        sendUniCast("NewPrev," + previous + "," + current, InetAddress.getByName(nextIP));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    NodeHandler.removeNode(NodeHandler.nodes.get(Integer.parseInt(current)));
                    for (Map.Entry<Integer, String> entry : NodeHandler.nodes.entrySet()) {
                        try {
                            String IP = entry.getValue();
                            IP = IP.substring(1);
                            sendUniCast("Exit", InetAddress.getByName(IP));
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            default:
                break;
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Amount of nodes in the network: " + amountOfNodes);
        System.out.println("Map: " + NodeHandler.nodes);
        System.out.println("----------------------------------------------------");
    }

    private void sendUniCast(String message, InetAddress hostAddress){
        System.out.println("Sending unicast: [" + hostAddress + "]: " + message);
        if (message != null) {
            DatagramSocket UDPSocket = null;
            try {
                UDPSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, hostAddress, sendPort);
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