package distributed.yproject.server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

import static java.lang.StrictMath.abs;

public class TCPHandler extends Thread{

    private Socket socket;
    private InetAddress hostAddress;
    private boolean running;
    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public TCPHandler(Socket socket, InetAddress inetAddress) {
        this.socket = socket;
        this.hostAddress = inetAddress;
        running = true;
    }

    @Override
    public void run() {
        System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"] Created thread voor TCP communication with: "+hostAddress);
        if (running){
            inputStream = null;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dataInputStream = new DataInputStream(Objects.requireNonNull(inputStream));
            String message = null;
            try {
                message = dataInputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"] [TCP]" + hostAddress + " packet recieved: " + message);
            send(message);
            shutdown();
        }
    }

    private void send(String message) {
        //message = hostName,fileName
        int index = message.indexOf(",");
        String hostName = message.substring(0,index);
        String fileName = message.substring(index+1);
        FileHandler.addFile(fileName,hashCode(hostName),NodeHandler.nodes);
        String IP = NodeHandler.nodes.get(FileHandler.getReplicationID(fileName));
        System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"] The replicated node of file " +FileHandler.getHash(fileName)+ " is node with IP: " +IP+ ".");
        System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"] Sending TCP: ["+hostAddress+"]: "+IP);
        outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataOutputStream = new DataOutputStream(outputStream);
        try {
            dataOutputStream.writeUTF(IP);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"]Shutting down thread");
        System.out.println(" ");
        try {
            dataInputStream.close();
            dataOutputStream.close();
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
    }

    public int hashCode(String name) {
        long max = 2147483647;
        long min = -2147483647;

        double result = (name.hashCode()+max)*(327680d/(max+abs(min)));

        return (int) result;
    }

}
