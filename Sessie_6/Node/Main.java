import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        // write your code here

        //STARTING NODE
        //BOOTSTRAP/DISCOVERY

        //Verify local files
        ArrayList<String> fileList = new ArrayList<>();
        fileList = scanFiles();

        //Iterate trough list of files
        Iterator it = fileList.iterator();

        while (it.hasNext()) {
            //Make file
            File file = new File((String)it.next());

            System.out.println("file: " +file.getFilename()+ ", with hash: " +file.getHash());

            //Report hash of file to naming server
            send(String.valueOf(file.getHash()));

            //Receive info on replicated node
            String IP = receive();

            //Send file to replicated node
            System.out.println("The replicated node of file " +file.getFilename()+ " is, node with IP " +IP+ ".");
            System.out.println(" ");
        }
    }

    public static ArrayList<String> scanFiles() {
        try (Stream<Path> walk = Files.walk(Paths.get("C:\\Users\\mathi\\Desktop\\nodeFiles"))) {
            ArrayList<String> fileList = new ArrayList<>();

            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

            //remove full directory, so that only the filename remains and add to "fileList"
            for (String x : result) {
                x = x.replaceAll("[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|,]",
                        "");

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


    public static void send(String hash) throws IOException {
        Socket s = new Socket(("127.0.0.1"), 9999); //IP-address of server, port of server
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
        ServerSocket s = new ServerSocket(9998);
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
