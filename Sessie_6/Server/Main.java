import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        HashMap<Integer, String> nodes = new HashMap<>();

        //add nodes to map
        nodes.put(7000, "Mathijs");
        nodes.put(40000, "Thomas");
        nodes.put(98000, "Liam");
        nodes.put(110690, "Bilal");
        nodes.put(111000, "Jorre");

        //print out nodes in network
        System.out.println("Nodes in network: ");
        Iterator<Integer> itr = nodes.keySet().iterator();
        while(itr.hasNext()) {
            int next = itr.next();
            System.out.print(next+ " ");
            System.out.println(nodes.get(next));
        }

        //System.out.println(" ");

        while(true) {
            //receive hash from node
            String hash = receive();
            //parse hash to int
            int hashint = Integer.parseInt(hash);
            //System.out.println(hashint);
            //make file
            File file = new File(hashint, nodes);
            //get key + ip
            String IP = nodes.get(file.getNodeID());
            System.out.println("The replicated node of file " +file.getHash()+ " is, node with IP " +IP+ ".");
            //send reply
            send(IP);
        }
    }

    public static void send(String hash) throws IOException {
        Socket s = new Socket(("127.0.0.1"), 9998); //IP-address of server, port of server
        //get output stream from socket
        OutputStream outputStream = s.getOutputStream();
        //create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        //write the message we want to send
        dataOutputStream.writeUTF(hash);
        //send the message
        dataOutputStream.flush();
        //close output stream
        dataOutputStream.close();
        //close socket
        s.close();
    }

    public static String receive() throws IOException {
        ServerSocket s = new ServerSocket(9999);
        //System.out.println("ServerSocket awaiting connections...");
        Socket socket = s.accept(); // blocking call, this will wait until a connection is attempted on this port.
        //System.out.println("Connection from " + socket + "!");

        // get the input stream from the connected socket
        InputStream inputStream = socket.getInputStream();
        // create a DataInputStream so we can read data from it.
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        // read the message from the socket
        String message = dataInputStream.readUTF();
        //System.out.println("The message sent from the socket was: " + message);

        //System.out.println("Closing sockets.");
        s.close();
        socket.close();

        return message;
    }

}
