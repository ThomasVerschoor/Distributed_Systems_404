import java.io.*;
import java.io.File;
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

    static String path = "C:\\Users\\mathi\\Desktop\\nodeFiles";

    public static void main(String[] args) throws IOException {
        // write your code here

        //STARTING NODE
        //BOOTSTRAP/DISCOVERY

        //Verify local files
        ArrayList<String> fileList = new ArrayList<>();
        fileList = scanFiles();

        //if node has files stored
        if (fileList.size() != 0) {
            //Iterate trough list of files
            Iterator it = fileList.iterator();

            while (it.hasNext()) {
                //Make file
                NodeFile file = new NodeFile((String)it.next());

                System.out.println("file: " +file.getFilename()+ ", with hash: " +file.getHash());

                //Report hash of file to naming server
                sendMessage(file.getFilename());

                //Receive info on replicated node
                String IP = receiveMessage();

                //Send file to replicated node
                System.out.println("The replicated node of file " +file.getFilename()+ " is, node with IP " +IP+ ".");
                System.out.println(" ");
                sendMessage(file.getFilename()); //send filename of file that's being sent
                sendFile(IP, file.getFilename()); //

                String filename = receiveMessage(); //get filename of file that's being sent
                receiveFile(filename); //receive file

                //make log
            }

        }
        //if nodes has no files stored
        else {
            System.out.println("No files on this node, waiting for replicated files.");
            String filename = receiveMessage();
            receiveFile(filename);
        }

    }

    //public static void updateLog() {
            //String logPath = path + "\\received\\log";

    //}

    public static ArrayList<String> scanFiles() {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            ArrayList<String> fileList = new ArrayList<>();

            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

            //add file to arraylist
            for (String x : result) {
              fileList.add(x);
            }

            //return arraylist
            return fileList;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void receiveFile(String receivePath) throws IOException {
        //extract filename from path
        String filename = receivePath.replace("\\", " ");
        String[] a = filename.split("nodeFiles");
        filename = a[1];

        ServerSocket s = new ServerSocket(9996);
        //wait
        Socket socket = s.accept();

        //receive file
        byte[] byteArray = new byte[6022386];
        InputStream is = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream(path+ "\\received\\" +filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(byteArray, 0, byteArray.length);
        int current = bytesRead;

        do {
            bytesRead = is.read(byteArray, current, (byteArray.length - current));
            if (bytesRead >= 0) current += bytesRead;
        } while (bytesRead > -1);

        bos.write(byteArray, 0, current);
        bos.flush();
        fos.close();
        bos.close();
        s.close();

    }

    public static void sendFile(String replicatedIP, String file) throws IOException {
        Socket s = new Socket(replicatedIP, 9996); //send file to replicated node

        File sendFile = new File(file);   //java File not object of class File
        byte[] byteArray = new byte[(int)sendFile.length()];

        FileInputStream fis = new FileInputStream(sendFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(byteArray, 0, byteArray.length);
        OutputStream os = s.getOutputStream();
        os.write(byteArray, 0, byteArray.length);
        os.flush();
        os.close();
        bis.close();
        fis.close();
        s.close();
    }

    public static void sendMessage(String message) throws IOException {
        Socket s = new Socket(("127.0.0.1"), 9999); //IP-address of server, port of server
        //get output stream from socket
        OutputStream outputStream = s.getOutputStream();
        //create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        //write the message we want to send
        dataOutputStream.writeUTF(message);
        //send the message
        dataOutputStream.flush();
        //close output stream
        dataOutputStream.close();
        //close socket
        s.close();
    }

    public static String receiveMessage() throws IOException {
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
