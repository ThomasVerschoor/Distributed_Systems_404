import org.w3c.dom.events.EventException;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import static java.lang.StrictMath.abs;

public class YClient {

    private static final int recievePort = 4500;
    private static final int sendPort = 4501;
    private static final int multiCastPort = 3456;
    private static final String multicastAddress = "225.6.7.8";
    private static int previousNodeID = -1;
    private static int currentNodeID = -1;
    private static int nextNodeID = -1;
    private static int amountOtherNodes = -1;
    private static boolean running = true;
    private static Semaphore update = new Semaphore(1);

    public static void main(String[] args){
        Scanner scanny = new Scanner(System.in);
        //Hostname
        System.out.println("Choose your hostname: ");
        String hostName = scanny.nextLine();
        //hostID
        YClient.currentNodeID = hashCode(hostName);
        YClient.nextNodeID = YClient.currentNodeID;
        YClient.previousNodeID = YClient.currentNodeID;
        //ServerIP
        System.out.println("Give the IP of the server: ");
        String sAddress = scanny.nextLine();
        InetAddress serverAddress = null;
        try {
            serverAddress = InetAddress.getByName(sAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //UDPHandler
        UDPReceiveHandler UDPHandler = new UDPReceiveHandler(YClient.recievePort);
        UDPHandler.start();
        //UDPMultiHandler
        UDPMultiReceiveHandler UDPMultiReceiveHandler = null;
        try {
            UDPMultiReceiveHandler = new UDPMultiReceiveHandler(hostName, InetAddress.getByName(YClient.multicastAddress),YClient.multiCastPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (UDPMultiReceiveHandler != null) {
            UDPMultiReceiveHandler.start();
        }
        //Discover
        try {
            sendMultiCast("Start,"+hostName, InetAddress.getByName(YClient.multicastAddress));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(YClient.running) {
            String command = scanny.nextLine();
            switch (command) {
                case "Exit":
                    String toSend = "Exit,Curr: " + YClient.currentNodeID + ",Prev: " + YClient.previousNodeID + ",Next: " + YClient.nextNodeID;
                    sendUnicast(toSend, serverAddress);
                    UDPHandler.shutdown();
                    if (UDPMultiReceiveHandler != null) {
                        UDPMultiReceiveHandler.shutdown();
                    }
                    YClient.running = false;
                    break;
                case "help":
                    System.out.println("List of all commands:");
                    System.out.println("Leave network: 'Exit'");
                    System.out.println("Get hashMap of nodes on server: 'getMap'");
                    System.out.println("Get hashMap of files on server: 'getFileMap'");
                    System.out.println("Add file to server: 'addFile'");
                    System.out.println("Get owner of file on server: 'getFileOwner'");
                    break;

                case "getMap":
                    try {
                        sendGET("getMap",sAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "getFileMap":
                    try {
                        sendGET("getFileMap",sAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "addFile":
                    System.out.println("Give the name for the file to be added: ");
                    String name = scanny.nextLine();
                    try {
                        sendPUT("addFile/"+name,sAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "getFileOwner":
                    System.out.println("Give the name of the file you want to find the owner of: ");
                    name = scanny.nextLine();
                    try {
                        sendGET("getFileOwner/"+name,sAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
        System.out.println("Quiting program, till next time!");
        System.exit(0);
    }

    private static void sendPUT(String command, String address) throws IOException {
        URL url = new URL("http://"+address+":8080/"+command);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
        }else {
            System.out.println("PUT request failed");
        }
    }

    private static void sendGET(String command, String address) throws IOException {
        URL url = new URL("http://"+address+":8080/"+command);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
        }else {
            System.out.println("GET request failed");
        }
    }

    private static void sendMultiCast(String message, InetAddress address){
        System.out.println("Sending multicast: ["+address+"]: "+message);
        if (message != null) {
            MulticastSocket UDPSocket = null; //create new socket
            try {
                UDPSocket = new MulticastSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length, address, YClient.multiCastPort);
            try {
                if (UDPSocket != null) {
                    UDPSocket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (UDPSocket != null) {
                UDPSocket.close();
            }
        }
    }

    private static void sendUnicast(String message, InetAddress adress){
        System.out.println("Sending unicast: ["+adress+"]: "+message);
        if (message != null){
            DatagramSocket UDPSocket = null;
            try {
                UDPSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length, adress, sendPort);
            try {
                if (UDPSocket != null) {
                    UDPSocket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (UDPSocket != null) {
                UDPSocket.close();
            }
        }
    }

    public static int hashCode(String name) {
        long max = 2147483647;
        long min = -2147483647;

        double result = (name.hashCode()+max)*(327680d/(max+abs(min)));

        return (int)result;
    }

    public static void update(int id, InetAddress hostAddress) {
        try {
            update.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        YClient.amountOtherNodes = YClient.amountOtherNodes + 1;
        System.out.println("New node detected with hash: "+id+" , other nodes on network: " + YClient.amountOtherNodes);
        //Als de eerste client joined in het netwerk
        if (YClient.currentNodeID == YClient.nextNodeID && YClient.currentNodeID == YClient.previousNodeID) {
            YClient.previousNodeID = id;
            YClient.nextNodeID = id;
        } else {
            //Niet de eerste, maar de tweede
            if (YClient.nextNodeID == YClient.previousNodeID){
                if (YClient.currentNodeID < id) {
                    YClient.nextNodeID = id;
                    sendUnicast("I ["+YClient.currentNodeID+"], have put you as my nextNode",hostAddress);
                }
                else {
                    YClient.previousNodeID = id;
                    sendUnicast("I ["+YClient.currentNodeID+"], have put you as my previousNode",hostAddress);
                }
            }
            //Normale werking
            else if (YClient.currentNodeID < id && id < YClient.nextNodeID){
                YClient.nextNodeID = id;
                sendUnicast("I ["+YClient.currentNodeID+"], have put you as my nextNode",hostAddress);
            }
            //Normale werking
            else if (YClient.previousNodeID < id && id < YClient.currentNodeID) {
                YClient.previousNodeID = id;
                sendUnicast("I ["+YClient.currentNodeID+"], have put you as my previousNode",hostAddress);
            }
            //Als je op het einde van de ring zit
            else if (YClient.currentNodeID < id && YClient.nextNodeID < id && YClient.currentNodeID > YClient.nextNodeID){
                YClient.nextNodeID = id;
                sendUnicast("I ["+YClient.currentNodeID+"], have put you as my nextNode",hostAddress);
            }
            //Als je in het begin van de ring zit
            else if (YClient.previousNodeID < id && YClient.currentNodeID < id && YClient.previousNodeID > YClient.currentNodeID){
                YClient.previousNodeID = id;
                sendUnicast("I ["+YClient.currentNodeID+"], have put you as my previousNode",hostAddress);
            }
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: "+YClient.amountOtherNodes);
        System.out.println("Previous ID: " + YClient.previousNodeID + " || Current ID: "+YClient.currentNodeID+" || Next ID: " + YClient.nextNodeID);
        System.out.println("Give a command: <help> for a list of all commands");
        System.out.println("----------------------------------------------------");
        update.release();
    }

    public static void updateInitial(String message) {
        //VB message: 3
        //VB message: 3, Previous ID: 68465, Next ID: 321846
        try {
            update.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String amount;
        String previous;
        String next;
        int index = message.indexOf(",");
        if (index != -1) {
            amount = message.substring(0, index);
            index = (message.indexOf("Previous ID: ")) + 13;
            int indexx = message.indexOf(", Next ID: ");
            previous = message.substring(index, indexx);
            index = (message.indexOf("Next ID: ")) + 9;
            next = message.substring(index, message.length());
            YClient.amountOtherNodes = Integer.parseInt(amount);
            YClient.nextNodeID = Integer.parseInt(next);
            YClient.previousNodeID = Integer.parseInt(previous);
        }
        else
            YClient.amountOtherNodes = Integer.parseInt(message);
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: "+YClient.amountOtherNodes);
        System.out.println("Previous ID: " + YClient.previousNodeID + " || Current ID: "+YClient.currentNodeID+" || Next ID: " + YClient.nextNodeID);
        System.out.println("Give a command: <help> for a list of all commands");
        System.out.println("----------------------------------------------------");
        update.release();
    }

    public static void exitUpdateNext(String message) {
        try {
            update.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String newID;
        String exitID;
        int index = message.indexOf(",");
        newID = message.substring(0,index);
        exitID = message.substring(index+1);
        System.out.println(exitID+" sends exit, updating nextNodeID to : "+newID);
        YClient.nextNodeID = Integer.parseInt(newID);
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: "+YClient.amountOtherNodes);
        System.out.println("Previous ID: " + YClient.previousNodeID + " || Current ID: "+YClient.currentNodeID+" || Next ID: " + YClient.nextNodeID);
        System.out.println("Give a command: <help> for a list of all commands");
        System.out.println("----------------------------------------------------");
        update.release();
    }

    public static void exitUpdatePrev(String message) {
        try {
            update.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String newID;
        String exitID;
        int index = message.indexOf(",");
        newID = message.substring(0,index);
        exitID = message.substring(index+1);
        System.out.println(exitID+" sends exit, updating previousNodeID to : "+newID);
        YClient.nextNodeID = Integer.parseInt(newID);
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: "+YClient.amountOtherNodes);
        System.out.println("Previous ID: " + YClient.previousNodeID + " || Current ID: "+YClient.currentNodeID+" || Next ID: " + YClient.nextNodeID);
        System.out.println("Give a command: <help> for a list of all commands");
        System.out.println("----------------------------------------------------");
        update.release();
    }
}