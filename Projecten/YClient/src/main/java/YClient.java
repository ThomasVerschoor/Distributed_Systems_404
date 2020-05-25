
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.log;

public class YClient {

    private static final int receivePort = 4499;
    private static final int sendPort = 4501;
    private static final int multiCastPort = 3456;
    private static final int TCPServerSendPort = 5501;
    private static final int TCPFileSendPort = 5502;
    private static final int TCPFileReceivePort = 5502;
    private static final int TCPReplicatedFileSendPort = 5503;
    private static final int TCPReplicatedFileReceivePort = 5503;
    private static final String multicastAddress = "225.6.7.8";
    private static InetAddress inetmulticastAdress;
    private static InetAddress serverAddress;
    private static String stringServerAddress;
    private static int previousNodeID = -1;
    private static int currentNodeID = -1;
    private static int nextNodeID = -1;
    private static int amountOtherNodes = -1;
    public static String hostName;
    private static boolean running = true;
    private static Semaphore update = new Semaphore(1);
    private static Semaphore exit = new Semaphore(1);
    private static Logger logger;
    private static FileHandler fileHandler;

    public static void main(String[] args) {
        //Logger
        LogManager.getLogManager().reset();
        logger = Logger.getLogger("YClient");
        try {
            fileHandler = new FileHandler("YClient.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setLevel(Level.ALL);
        Scanner scanny = new Scanner(System.in);
        //Hostname
        System.out.println("Choose your hostname: ");
        hostName = scanny.nextLine();
        //hostID
        currentNodeID = hashCode(hostName);
        nextNodeID = currentNodeID;
        previousNodeID = currentNodeID;
        //ServerIP
        System.out.println("Give the IP of the server: ");
        stringServerAddress = scanny.nextLine();
        serverAddress = null;
        try {
            serverAddress = InetAddress.getByName(stringServerAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //UDPHandler
        System.out.println("Starting UDP UNICAST listener on port: "+receivePort);
        UDPReceiveHandler UDPHandler = new UDPReceiveHandler(receivePort,logger);
        UDPHandler.start();
        //UDPMultiHandler
        try {
            inetmulticastAdress = InetAddress.getByName(multicastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Starting UDP MULTICAST listener on port: "+multiCastPort+" with address: "+ inetmulticastAdress);
        UDPMultiReceiveHandler UDPMultiHandler = new UDPMultiReceiveHandler(hostName, inetmulticastAdress, multiCastPort,logger);
        UDPMultiHandler.start();
        //TCPFileHandler
        System.out.println("Starting TCP File listener on port: "+TCPFileReceivePort);
        TCPFileReceiveHandler TCPFileReceive = new TCPFileReceiveHandler(TCPFileReceivePort,logger);
        TCPFileReceive.start();
        //TCPReplicatedHandler
        System.out.println("Starting TCP Replicated File listener on port: "+TCPReplicatedFileReceivePort);
        TCPReplicatedFileReceiveHandler TCPReplicatedFileReceive = new TCPReplicatedFileReceiveHandler(TCPReplicatedFileReceivePort,logger);
        TCPReplicatedFileReceive.start();
        //Discover
        try {
            System.out.println("Starting Discovery...");
            sendMultiCast("Start," + hostName, InetAddress.getByName(multicastAddress));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Replication start
        System.out.println("Starting replication start");
        TimedUpdates timedUpdates = new TimedUpdates(logger);
        timedUpdates.start();
        //run client
        while (running) {
            String command = scanny.nextLine();
            switch (command) {
                case "Exit":
                    System.out.println("----------------------------------------------------");
                    String toSend = "Exit,Curr: " + currentNodeID + ",Prev: " + previousNodeID + ",Next: " + nextNodeID;
                    System.out.println("Sending Exit to server and other nodes...");
                    try {
                        System.out.println("Starting replication shutdown process");
                        shutdownSend();
                        try {
                            Thread.currentThread().sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("shutdown replication process complete");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendUnicast(toSend, serverAddress);
                    UDPHandler.shutdown();
                    UDPMultiHandler.shutdown();
                    TCPFileReceive.shutdown();
                    TCPReplicatedFileReceive.shutdown();
                    for(Handler h:logger.getHandlers())
                        h.close();
                    running = false;
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
                        sendGET("getMap", stringServerAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
        System.out.println("Quiting program, till next time!");
        System.out.println("----------------------------------------------------");
        System.exit(0);
    }

    private static void shutdownSend() throws IOException {
        int previous = YClient.previousNodeID;
        //System.out.println("previous: " +YClient.previousNodeID);
        String fileLocation = System.getProperty("user.dir");
        ArrayList<String> fileList = new ArrayList<>();
        //fileLocation = fileLocation.concat("/receivedFiles"); //for Linux
        fileLocation = fileLocation.concat("\\receivedFiles"); //for windows

        try (Stream<Path> walk = Files.walk(Paths.get(fileLocation))) {
            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
            for (String x : result) {
                x = x.replaceAll("[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|,]","");
                String[] parts = x.split("receivedFiles");
                String file = parts[1];
                System.out.println("filename: " +file);
                fileList.add(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<String> it = fileList.iterator();
        while (it.hasNext()) {
            String file = it.next();
            //System.out.println("filename: " +file);
            if (!file.contains(".log")) {
                int index = file.indexOf(".");
                String fileName = file.substring(0,index);
                String locationLine = Files.readAllLines(Paths.get(fileLocation+"\\"+fileName+".log")).get(1);
                String[] location = locationLine.split(" ", 3);
                //System.out.println("Download location: " +hashCode(location[2]));
                if (hashCode(location[2]) == previous) {
                    String previousOfPreviousID = sendGET("getPreviousNode/"+previousNodeID,stringServerAddress);
                    //System.out.println("Previous ID: " +previousOfPreviousID);
                    String IP = sendGET("getIP/"+previousOfPreviousID,stringServerAddress);
                    //System.out.println("IP: " +IP);
                    TCPReplicatedFileSendHandler tcpReplicatedFileSendHandler = new TCPReplicatedFileSendHandler(TCPReplicatedFileSendPort,IP,file,fileName+".log",hostName,logger);
                    tcpReplicatedFileSendHandler.start();
                } else {
                    String IP = sendGET("getIP/"+previousNodeID,stringServerAddress);
                    TCPReplicatedFileSendHandler tcpReplicatedFileSendHandler = new TCPReplicatedFileSendHandler(TCPReplicatedFileSendPort,IP,file,fileName+".log",hostName,logger);
                    tcpReplicatedFileSendHandler.start();
                }
            }
        }
    }

    private static String sendPUT(String command, String address) throws IOException {
        logger.log(Level.INFO,"[MAIN] Sending HTTP PUT to YServer: "+"http://" + address + ":420/" + command);
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
            return response.toString();
        } else {
            System.out.println("PUT request failed");
            return "PUT request failed";
        }
    }

    private static String sendGET(String command, String address) throws IOException {
        logger.log(Level.INFO,"[MAIN] Sending HTTP GET to YServer: "+"http://" + address + ":420/" + command);
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
            return "GET request failed";
        }
    }

    private static void sendMultiCast(String message, InetAddress address) {
        logger.log(Level.INFO,"[MAIN] Sending multicast: [" + address + "]: " + message);
        if (message != null) {
            MulticastSocket UDPSocket = null; //create new socket
            try {
                UDPSocket = new MulticastSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, multiCastPort);
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
        logger.log(Level.INFO,"[MAIN] Sending unicast: [" + adress + "]: " + message);
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
        amountOtherNodes = amountOtherNodes + 1;
        System.out.println("New node detected with hash: " + id + " , other nodes on network: " + amountOtherNodes);
        //Als de eerste client joined in het netwerk
        if (currentNodeID == nextNodeID && currentNodeID == previousNodeID) {
            previousNodeID = id;
            nextNodeID = id;
        } else {
            //Niet de eerste, maar de tweede
            if (nextNodeID == previousNodeID) {
                if (currentNodeID < id) {
                    nextNodeID = id;
                    sendUnicast("I [" + currentNodeID + "], have put you as my nextNode", hostAddress);
                } else {
                    previousNodeID = id;
                    sendUnicast("I [" + currentNodeID + "], have put you as my previousNode", hostAddress);
                }
            }
            //Normale werking
            else if (currentNodeID < id && id < nextNodeID) {
                nextNodeID = id;
                sendUnicast("I [" + currentNodeID + "], have put you as my nextNode", hostAddress);
            }
            //Normale werking
            else if (previousNodeID < id && id < currentNodeID) {
                previousNodeID = id;
                sendUnicast("I [" + currentNodeID + "], have put you as my previousNode", hostAddress);
            }
            //Als je op het einde van de ring zit
            else if (currentNodeID < id && nextNodeID < id && currentNodeID > nextNodeID) {
                nextNodeID = id;
                sendUnicast("I [" + currentNodeID + "], have put you as my nextNode", hostAddress);
            }
            //Als je in het begin van de ring zit
            else if (previousNodeID < id && currentNodeID < id && previousNodeID > currentNodeID) {
                previousNodeID = id;
                sendUnicast("I [" + currentNodeID + "], have put you as my previousNode", hostAddress);
            }
        }
        System.out.println(" ");
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: " + amountOtherNodes);
        System.out.println("Previous ID: " + previousNodeID + " || Current ID: " + currentNodeID + " || Next ID: " + nextNodeID);
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
            //replicationStart(serverAddress);
            amount = message.substring(0, index);
            index = (message.indexOf("Previous ID: ")) + 13;
            int indexx = message.indexOf(", Next ID: ");
            previous = message.substring(index, indexx);
            index = (message.indexOf("Next ID: ")) + 9;
            next = message.substring(index);
            amountOtherNodes = Integer.parseInt(amount);
            nextNodeID = Integer.parseInt(next);
            previousNodeID = Integer.parseInt(previous);
        } else {
            System.out.println("Not starting replication process since you are the first  node in the network");
            amountOtherNodes = Integer.parseInt(message);
        }

        System.out.println(" ");
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: " + amountOtherNodes);
        System.out.println("Previous ID: " + previousNodeID + " || Current ID: " + currentNodeID + " || Next ID: " + nextNodeID);
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
        nextNodeID = Integer.parseInt(newID);
        System.out.println(" ");
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: " + amountOtherNodes);
        System.out.println("Previous ID: " + previousNodeID + " || Current ID: " + currentNodeID + " || Next ID: " + nextNodeID);
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
        nextNodeID = Integer.parseInt(newID);
        update.release();
    }

    public static void exit() {
        try {
            exit.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        amountOtherNodes = amountOtherNodes - 1;
        System.out.println("A node left the network");
        System.out.println(" ");
        System.out.println("----------------------------------------------------");
        System.out.println("Other nodes in the network: " + amountOtherNodes);
        System.out.println("Previous ID: " + previousNodeID + " || Current ID: " + currentNodeID + " || Next ID: " + nextNodeID);
        System.out.println("Give a command: <help> for a list of all commands");
        System.out.println("----------------------------------------------------");
        System.out.println(" ");
    }

    public static void replicateFile(String name) {
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
            logger.log(Level.SEVERE,"[MAIN] Something went wrong trying to connect to YServer with address: "+serverAddress+" on port: "+TCPServerSendPort);
            e.printStackTrace();
        }
        NodeFile file = new NodeFile(name);
        try {
            dataOutputStream.writeUTF(hostName+","+file.getFilename()); //hostName,fileName
            logger.log(Level.INFO,"[MAIN] Sending TCP: ["+serverAddress+"]: "+hostName+","+file.getFilename());
            dataOutputStream.flush();
            received = dataInputStream.readUTF();
            logger.log(Level.INFO,"[MAIN] [" + socket.getInetAddress() + "]TCP packet recieved: " + received);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,"[MAIN] Something went wrong went wrong while talking to YServer for replicationIP with address: "+serverAddress+" on port: "+TCPServerSendPort);
        }
        logger.log(Level.INFO,"[MAIN] Requesting thread to send file: "+file.getFilename()+" to destination: "+received);
        System.out.println("Sending replicated file: "+file.getFilename()+" to: "+received);
        TCPFileSendHandler TCPFileSend = new TCPFileSendHandler(received,file.getFilename(),hostName,TCPFileSendPort,file.getFilename(),logger);
        TCPFileSend.start();
        try {
            logger.log(Level.INFO,"[MAIN] closing I/O streams and socket with address: "+serverAddress+" on port: "+TCPServerSendPort);
            outputStream.close();
            dataOutputStream.close();
            inputStream.close();
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE,"[MAIN] Something went wrong went while closing I/O Streams and socket with address: "+serverAddress+" on port: "+TCPServerSendPort);
        }
    }

    public static void deleteFile(String fileName) {
        System.out.println("Syncing deleted file "+fileName+" from system");
        String toSend = "Delete,"+fileName;
        sendMultiCast(toSend, inetmulticastAdress);
    }

    public static void removeReplicatedFile(String message) {
        System.out.println("Some node deleted an original file, deleting this replicated file locally: "+message+" (with it's log file)");
        int index;
        index = message.indexOf(".");
        String fileName = message.substring(0,index);
        String fileName1 = fileName+".log";
        //TODO: Linux - Windows change
        String path = System.getProperty("user.dir");
        String path1 = path+"\\receivedFiles\\"+message; //For Windows not the log file path
        String path2 = path+"\\receivedFiles\\"+fileName1; //For Windows for log file path
        //String path1 = path+"/receivedFiles/"+message; //For Windows not the log file path
        //String path2 = path+"/receivedFiles/"+fileName1; //For Windows for log file path
        try {
            logger.log(Level.INFO,"[MAIN] Deleting file at location: "+path1);
            Files.deleteIfExists(Paths.get(path1));
            logger.log(Level.INFO,"[MAIN] Deleting file at location: "+path2);
            Files.deleteIfExists(Paths.get(path2));
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.INFO,"[MAIN] Something went wrong while trying to delete the replicated file: "+message);
        }
    }
}