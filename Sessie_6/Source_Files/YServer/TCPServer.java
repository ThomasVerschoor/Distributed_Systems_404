package distributed.yproject.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread{

    private static int recievePort;
    private static boolean running = true;

    public TCPServer(int recievePort){
        TCPServer.recievePort = recievePort;
        System.out.println("Starting TCP server listening on port "+TCPServer.recievePort);
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(TCPServer.recievePort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (TCPServer.running) {
            if (serverSocket != null) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    System.out.println(" ");
                    TCPHandler TCPH = new TCPHandler(socket,socket.getInetAddress());
                    TCPH.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}