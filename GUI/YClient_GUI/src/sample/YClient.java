package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.StrictMath.abs;


public class YClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("JavaFX/Start.fxml"));
        primaryStage.setTitle("Distributed Systems application");
        primaryStage.setScene(new Scene(root, 882, 494));
        primaryStage.show();
    }

    public static String command;
    private static final int receivePort = 4500;
    private static final int sendPort = 4501;
    private static final int multiCastPort = 3456;
    private static final int TCPServerSendPort = 5501;
    private static final int TCPFileSendPort = 5502;
    private static final int TCPFileReceivePort = 5502;
    private static final String multicastAddress = "225.6.7.8";
    private static InetAddress InetmulticastAdress;
    private static InetAddress serverAddress;
    private static int previousNodeID = -1;
    private static int currentNodeID = -1;
    private static int nextNodeID = -1;
    private static int amountOtherNodes = -1;
    private static String hostName;
    private static boolean running = true;
    private static Semaphore update = new Semaphore(1);


    public static void main(String[] args) {
        launch(args);
        Scanner scanny = new Scanner(System.in);
        //Hostname
        //System.out.println("Choose your hostname: ");
        //hostName = scanny.nextLine();
        //hostID

        /*
        YClient.currentNodeID = hashCode(hostName);
        YClient.nextNodeID = YClient.currentNodeID;
        YClient.previousNodeID = YClient.currentNodeID;
        //ServerIP
        System.out.println("Give the IP of the server: ");
        String sAddress = scanny.nextLine();
        serverAddress = null;

        */

        YClient.currentNodeID = hashCode(Controller.hostName);
        YClient.nextNodeID = YClient.currentNodeID;
        YClient.previousNodeID = YClient.currentNodeID;
        //ServerIP
        //System.out.println("Give the IP of the server: ");
        String sAddress = Controller.IP;
        serverAddress = null;
        try {
            serverAddress = InetAddress.getByName(sAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("UDPHandler");
        //UDPHandler
        UDPReceiveHandler UDPHandler = new UDPReceiveHandler(YClient.receivePort);
        UDPHandler.start();
        //UDPMultiHandler
        try {
            YClient.InetmulticastAdress = InetAddress.getByName(YClient.multicastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("UDPMultiReceiveHandler");
        UDPMultiReceiveHandler UDPMultiHandler = new UDPMultiReceiveHandler(hostName, YClient.InetmulticastAdress, YClient.multiCastPort);
        UDPMultiHandler.start();
        //Discover
        try {
            sendMultiCast("Start," + hostName, InetAddress.getByName(YClient.multicastAddress));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("TCPFileReceiver");
        //TCPFileHandler
        TCPFileReceiveHandler TCPFileReceive = new TCPFileReceiveHandler(TCPFileReceivePort);
        TCPFileReceive.start();

        System.out.println("Run client");
        //run client
        while (YClient.running) {
            command = scanny.nextLine();
            //String command = ClientController.pollString();
            switch (command) {
                case "Exit":
                    System.out.println("----------------------------------------------------");
                    String toSend = "Exit,Curr: " + YClient.currentNodeID + ",Prev: " + YClient.previousNodeID + ",Next: " + YClient.nextNodeID;
                    System.out.println("----------------------------------------------------");
                    sendUnicast(toSend, serverAddress);
                    UDPHandler.shutdown();
                    UDPMultiHandler.shutdown();
                    TCPFileReceive.shutdown();
                    YClient.running = false;
                    break;

                case "help":
                    System.out.println("----------------------------------------------------");
                    System.out.println("List of all commands:");
                    System.out.println("Leave network: 'Exit'");
                    System.out.println("Get hashMap of nodes on server: 'getMap'");
                    System.out.println("----------------------------------------------------");
                    break;

                case "getMap":
                    try {
                        sendGET("getMap", sAddress);
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

    private static void replicationStart(InetAddress serverAddress) {
        System.out.println(" ");
        System.out.println("----------------------------------------------------");
        System.out.println("Starting replication process...");
        String location = System.getProperty("user.dir");
        ArrayList<String> fileList = new ArrayList<>();
        fileList = scanFiles(location);
        if (fileList.size() != 0) {
            Iterator<String> iterator = fileList.iterator();
            System.out.println("Creating TCP connection with server on port: "+TCPServerSendPort);
            while(iterator.hasNext()){
                Socket socket = null;
                OutputStream outputStream = null;
                InputStream inputStream = null;
                DataOutputStream dataOutputStream = null;
                DataInputStream dataInputStream = null;
                String received = null;
                try {
                    socket = new Socket(serverAddress, TCPServerSendPort);
                    outputStream = Objects.requireNonNull(socket).getOutputStream();
                    dataOutputStream = new DataOutputStream(outputStream);
                    inputStream = Objects.requireNonNull(socket).getInputStream();
                    dataInputStream = new DataInputStream(Objects.requireNonNull(inputStream));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                NodeFile file = new NodeFile(iterator.next());
                try {
                    dataOutputStream.writeUTF(hostName+","+file.getFilename()); //hostName,fileName
                    System.out.println("Sending TCP: ["+serverAddress+"]: "+hostName+","+file.getFilename());
                    dataOutputStream.flush();
                    received = dataInputStream.readUTF();
                    System.out.println("[" + socket.getInetAddress() + "]TCP packet recieved: " + received);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TCPFileSendHandler TCPFileSend = new TCPFileSendHandler(received,file.getFilename(),hostName,TCPFileSendPort,file.getFilename());
                TCPFileSend.start();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Closing TCP connection on port: "+TCPServerSendPort);
        }else
            System.out.println("No files on this node, waiting for replicated files from other nodes...");
        System.out.println("----------------------------------------------------");
    }

    private static ArrayList<String> scanFiles(String fileLocation) {
        fileLocation = fileLocation.concat("/nodeFiles"); //for Linux
        //fileLocation = fileLocation.concat("\\nodeFiles"); //for windows
        try (Stream<Path> walk = Files.walk(Paths.get(fileLocation))) {
            ArrayList<String> fileList = new ArrayList<>();
            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
            //remove full directory, so that only the filename remains and add to "fileList"
            for (String x : result) {
                x = x.replaceAll("[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|,]","");
                String[] parts = x.split("nodeFiles"); //add directory name here
                String file = parts[1];
                fileList.add(file);
            }
            return fileList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void sendPUT(String command, String address) throws IOException {
        URL url = new URL("http://" + address + ":420/" + command);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
        } else {
            System.out.println("PUT request failed");
        }
    }

    private static String sendGET(String command, String address) throws IOException {
        URL url = new URL("http://" + address + ":420/" + command);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
            return response.toString();

        } else {
            System.out.println("GET request failed");
            return null;
        }
    }

    private static void sendMultiCast(String message, InetAddress address) {
        System.out.println("Sending multicast: [" + address + "]: " + message);
        if (message != null) {
            MulticastSocket UDPSocket = null; //create new socket
            try {
                UDPSocket = new MulticastSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, YClient.multiCastPort);
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

    private static void sendUnicast(String message, InetAddress adress) {
        System.out.println("Sending unicast: [" + adress + "]: " + message);
        if (message != null) {
            DatagramSocket UDPSocket = null;
            try {
                UDPSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, adress, sendPort);
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

        double result = (name.hashCode() + max) * (327680d / (max + abs(min)));

        return (int) result;
    }

    public static void update(int id, InetAddress hostAddress) {
        try {
            update.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        YClient.amountOtherNodes = YClient.amountOtherNodes + 1;
        System.out.println("New node detected with hash: " + id + " , other nodes on network: " + YClient.amountOtherNodes);
        //Als de eerste client joined in het netwerk
        if (YClient.currentNodeID == YClient.nextNodeID && YClient.currentNodeID == YClient.previousNodeID) {
            YClient.previousNodeID = id;
            YClient.nextNodeID = id;
        } else {
            //Niet de eerste, maar de tweede
            if (YClient.nextNodeID == YClient.previousNodeID) {
                if (YClient.currentNodeID < id) {
                    YClient.nextNodeID = id;
                    sendUnicast("I [" + YClient.currentNodeID + "], have put you as my nextNode", hostAddress);
                } else {
                    YClient.previousNodeID = id;
                    sendUnicast("I [" + YClient.currentNodeID + "], have put you as my previousNode", hostAddress);
                }
            }
            //Normale werking
            else if (YClient.currentNodeID < id && id < YClient.nextNodeID) {
                YClient.nextNodeID = id;
                sendUnicast("I [" + YClient.currentNodeID + "], have put you as my nextNode", hostAddress);
            }
            //Normale werking
            else if (YClient.previousNodeID < id && id < YClient.currentNodeID) {
                YClient.previousNodeID = id;
                sendUnicast("I [" + YClient.currentNodeID + "], have put you as my previousNode", hostAddress);
            }
            //Als je op het einde van de ring zit
            else if (YClient.currentNodeID < id && YClient.nextNodeID < id && YClient.currentNodeID > YClient.nextNodeID) {
                YClient.nextNodeID = id;
                sendUnicast("I [" + YClient.currentNodeID + "], have put you as my nextNode", hostAddress);
            }
            //Als je in het begin van de ring zit
            else if (YClient.previousNodeID < id && YClient.currentNodeID < id && YClient.previousNodeID > YClient.currentNodeID) {
                YClient.previousNodeID = id;
                sendUnicast("I [" + YClient.currentNodeID + "], have put you as my previousNode", hostAddress);
            }
        }
        System.out.println(" ");
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: " + YClient.amountOtherNodes);
        System.out.println("Previous ID: " + YClient.previousNodeID + " || Current ID: " + YClient.currentNodeID + " || Next ID: " + YClient.nextNodeID);
        System.out.println("Give a command: <help> for a list of all commands");
        System.out.println("----------------------------------------------------");
        System.out.println(" ");
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
            replicationStart(serverAddress);
            amount = message.substring(0, index);
            index = (message.indexOf("Previous ID: ")) + 13;
            int indexx = message.indexOf(", Next ID: ");
            previous = message.substring(index, indexx);
            index = (message.indexOf("Next ID: ")) + 9;
            next = message.substring(index);
            YClient.amountOtherNodes = Integer.parseInt(amount);
            YClient.nextNodeID = Integer.parseInt(next);
            YClient.previousNodeID = Integer.parseInt(previous);
        } else {
            System.out.println("Not starting replication process since you are the first  node in the network");
            YClient.amountOtherNodes = Integer.parseInt(message);
        }

        System.out.println(" ");
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: " + YClient.amountOtherNodes);
        System.out.println("Previous ID: " + YClient.previousNodeID + " || Current ID: " + YClient.currentNodeID + " || Next ID: " + YClient.nextNodeID);
        System.out.println("Give a command: <help> for a list of all commands");
        System.out.println("----------------------------------------------------");
        System.out.println(" ");
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
        newID = message.substring(0, index);
        exitID = message.substring(index + 1);
        System.out.println(exitID + " sends exit, updating nextNodeID to : " + newID);
        YClient.nextNodeID = Integer.parseInt(newID);
        System.out.println(" ");
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: " + YClient.amountOtherNodes);
        System.out.println("Previous ID: " + YClient.previousNodeID + " || Current ID: " + YClient.currentNodeID + " || Next ID: " + YClient.nextNodeID);
        System.out.println("Give a command: <help> for a list of all commands");
        System.out.println("----------------------------------------------------");
        System.out.println(" ");
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
        newID = message.substring(0, index);
        exitID = message.substring(index + 1);
        System.out.println(exitID + " sends exit, updating previousNodeID to : " + newID);
        YClient.nextNodeID = Integer.parseInt(newID);
        System.out.println(" ");
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: " + YClient.amountOtherNodes);
        System.out.println("Previous ID: " + YClient.previousNodeID + " || Current ID: " + YClient.currentNodeID + " || Next ID: " + YClient.nextNodeID);
        System.out.println("Give a command: <help> for a list of all commands");
        System.out.println("----------------------------------------------------");
        System.out.println(" ");
        update.release();
    }

}
