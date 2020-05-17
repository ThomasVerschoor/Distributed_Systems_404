package distributed.yproject.server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class TCPHandler extends Thread{

    private static Socket socket;
    private static InetAddress hostAddress;
    private static boolean running;

    public TCPHandler(Socket socket, InetAddress inetAddress) {
        TCPHandler.socket = socket;
        TCPHandler.hostAddress = inetAddress;
        running = true;
    }

    @Override
    public void run() {
        System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"] Created thread voor TCP communication with: "+hostAddress);
        if (running){
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DataInputStream dataInputStream = new DataInputStream(Objects.requireNonNull(inputStream));
            String message = null;
            try {
                message = dataInputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"] [TCP]" + hostAddress + " packet recieved: " + message);
            send(message);
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            shutdown();
        }
    }

    private void send(String message) {
        FileHandler.addFile(message,NodeHandler.nodes);
        String ID = NodeHandler.getPrevious(FileHandler.getHash(message));//TEST
        String IP = NodeHandler.nodes.get(ID);//TEST
        //String IP = FileHandler.getIP(message,NodeHandler.nodes); PREVIOUS
        System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"] The replicated node of file " +FileHandler.getHash(message)+ " is node with IP: " +IP+ ".");
        System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"] Sending TCP: ["+hostAddress+"]: "+IP);
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        try {
            dataOutputStream.writeUTF(IP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        System.out.println("["+TCPHandler.currentThread().getId()+" | "+TCPHandler.currentThread().getName()+"]Shutting down thread");
        System.out.println(" ");
        running = false;
    }
}
