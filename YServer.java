import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class YServer {

    public static int hostHash = -1;
    public static String hostName = null;

    static HashMap<Integer, String> nodes = new HashMap<>();


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give the server socket: ");
        int chosenSocket = Integer.parseInt(scanner.nextLine());
        ServerSocket serverSocket = new ServerSocket(chosenSocket);
        DataInputStream dataIn;
        DataOutputStream dataOut;
        while (true){
            XML.main(nodes);
            Socket socket = null;
            try {
                socket = serverSocket.accept(); //Nieuwe connectie...
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                System.out.println("A new client is connected: " + socket + " With IP: "+ socket.getInetAddress());
                System.out.println("Starting IniThread for this client");
                Thread IniThread = new YiniClient(socket,dataIn,dataOut);
                IniThread.start();
                System.out.println("Working thread ");
                while(IniThread.isAlive()){
                    System.out.print("");
                }
                System.out.println("IniThread done.");
                if (hostName != null && hostHash != -1){
                    if (!hostName.equals("Exit")){
                        nodes.put(hostHash, String.valueOf(socket.getInetAddress()));
                        System.out.println("Current map: "+ nodes);
                        hostName = null;
                        hostHash = -1;
                    }
                }
                System.out.println("Starting ClientThread for this client");
                Thread ClientThread = new YClientHandler(socket,dataIn,dataOut);
                ClientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class YiniClient extends Thread{
    final DataInputStream IniDataIn;
    final DataOutputStream IniDataOut;
    final Socket IniSocket;

    public YiniClient(Socket socket, DataInputStream dataIn, DataOutputStream dataOut){
        IniDataIn = dataIn;
        IniDataOut = dataOut;
        IniSocket = socket;
    }

    @Override
    public void run(){
        String received;
        try{
            IniDataOut.writeUTF("(IniThread) Enter the name you would like to use for this node: ");
            received = IniDataIn.readUTF();
            if (received.equals("Exit")){
                System.out.println("(IniThread) Client " + IniSocket + " sends exit...");
                System.out.println("(IniThread) Closing this connection.");
                deleteClient(IniSocket);
                IniSocket.close();
                System.out.println("(IniThread) Connection closed");
            }else{
                System.out.println("(IniThread) Node has chosen " + received + " as name, executing hashing function");
                YServer.hostHash = hashCode(received);
                YServer.hostName = received;
                IniDataOut.writeUTF("(IniThread) "+received+" has been set succesfully as name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteClient(Socket iniSocket) {
        YServer.nodes.entrySet().removeIf(entry -> String.valueOf(IniSocket.getInetAddress()).equals(entry.getValue()));
        System.out.println("Deleted from map!, new map ->"+String.valueOf(YServer.nodes));
    }

    public int hashCode(String hostname) {
        int hash = hostname.hashCode();
        if (hash > 0){
            return (int) (hostname.hashCode() % 32768);
        } else{
            return (int) (hostname.hashCode() % 32768 * (-1));
        }
    }
}

class YClientHandler extends Thread {
    final DataInputStream ClientDataIn;
    final DataOutputStream ClientDataOut;
    final Socket ClientSocket;

    public YClientHandler(Socket socket, DataInputStream dataIn, DataOutputStream dataOut){
        ClientDataIn = dataIn;                                                           // maken we een nieuwe instantie van ClientHandler
        ClientDataOut = dataOut;
        ClientSocket = socket;
    }

    @Override
    public void run(){
        String toReturn;
        String received;
        while(true){
            try{
                ClientDataOut.writeUTF("(ClientThread) Enter a command: ");
                received = ClientDataIn.readUTF();
                if(received.equals("Exit")){
                    System.out.println("(ClientThread) Client " + ClientSocket + " sends exit...");
                    System.out.println("(ClientThread) Closing this connection.");
                    deleteClient(ClientSocket);
                    ClientSocket.close();
                    System.out.println("(ClientThread) Connection closed");
                    break;
                }else{
                    if (received.equals("hashmap")){
                        System.out.println("User: "+ClientSocket+" requesting hashmap");
                        toReturn = String.valueOf(YServer.nodes);
                        ClientDataOut.writeUTF(toReturn);
                    }else
                        ClientDataOut.writeUTF("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteClient(Socket clientSocket) {
        YServer.nodes.entrySet().removeIf(entry -> String.valueOf(clientSocket.getInetAddress()).equals(entry.getValue()));
        System.out.println("Deleted from map!, new map ->"+String.valueOf(YServer.nodes));
    }
}

class XML {

    public static final String xmlFilePath = "C:\\Users\\liamh\\Desktop\\6-distributed\\ProjectY\\xmlfile.xml";

    public static void main(HashMap <Integer, String> nodes) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            // root element: nameserver
            Element root = document.createElement("NameServer");
            document.appendChild(root);
            // employee element
            Element hashmaps = document.createElement("Available_Hashmaps");
            root.appendChild(hashmaps);
            //nodes.forEach((key,value) -> System.out.println("k"+ key + "v"+value));
            //nodes.forEach((key,value) -> addXML(root,document,id,key.toString(),value));
            int ctr = 1;
            for (HashMap.Entry node : nodes.entrySet()) {
                addXML(root,document,ctr,node.getKey().toString(),node.getValue().toString());
                ctr = ctr+1;
            }
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new java.io.File(xmlFilePath));
            transformer.transform(domSource, streamResult);
            System.out.println("Done creating XML File");
        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    public static void addXML(Element root, Document document, int id, String hash, String ip){
        String hashId = Integer.toString(id);
        Element hashmaps = document.createElement("Available_Hashmaps");
        root.appendChild(hashmaps);
        Attr attr2 = document.createAttribute("id");
        attr2.setValue(hashId);
        hashmaps.setAttributeNode(attr2);
        Element id2 = document.createElement("value");
        id2.appendChild(document.createTextNode(hash));
        hashmaps.appendChild(id2);
        Element ip2 = document.createElement("Ip_Address");
        ip2.appendChild(document.createTextNode(ip));
        hashmaps.appendChild(ip2);
        root.appendChild(hashmaps);
    }
}