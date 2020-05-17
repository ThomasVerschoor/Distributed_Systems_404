import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class TCPFileSendHandler extends Thread{

    private static InetAddress sendAddress;
    private static String fileName;
    private static String hostName;
    private static String path;
    private static int port;

    public TCPFileSendHandler(String received, String filename, String hostName, int tcpFileSendPort, String filePath) {
        fileName = filename;
        String temp = received.substring(1);
        try {
            sendAddress = InetAddress.getByName(temp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        TCPFileSendHandler.hostName = hostName;
        port = tcpFileSendPort;
        path = filePath;
    }

    public void run(){
        System.out.println("["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Created thread to send replicated file '"+fileName+"' to: "+hostName);
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        File file = new File(path);
        byte[] byteArray = new byte[(int)file.length()];
        String received = null;
        try {
            socket = new Socket(sendAddress,port);
            outputStream = Objects.requireNonNull(socket).getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            inputStream = Objects.requireNonNull(socket).getInputStream();
            dataInputStream = new DataInputStream(Objects.requireNonNull(inputStream));
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            Objects.requireNonNull(dataOutputStream).writeUTF(hostName+","+fileName);
            System.out.println("["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Sending: "+hostName+","+fileName);
            dataOutputStream.flush();
            received = Objects.requireNonNull(dataInputStream).readUTF();
            if (received.equals("ACK")){
                System.out.println("["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Received: "+received);
                Objects.requireNonNull(bufferedInputStream).read(byteArray,0,byteArray.length);
                outputStream.write(byteArray,0,byteArray.length);
                outputStream.flush();
                received = Objects.requireNonNull(dataInputStream).readUTF();
                if (received.equals("ACK")){
                    System.out.println("["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"Node sends ACK, received file correctly...");
                }
                else if (received.equals("NACK")){
                    System.out.println("["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"Node sends NACK, received file incorrectly...");
                }
            }else
                System.out.println("node does not ACK hostName and FileName packet, something went wrong!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+" Closing socket and cleaning up I/O streams");
            outputStream.close();
            bufferedInputStream.close();
            fileInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
