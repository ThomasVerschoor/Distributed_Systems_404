package distributed.yproject.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread{

    private int recievePort;
    private boolean running = true;
    private ServerSocket serverSocket;

    public TCPServer(int recievePort){
        this.recievePort = recievePort;
        System.out.println("Starting TCP server listening on port "+this.recievePort);
    }

    @Override
    public void run() {
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(recievePort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (running) {
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

    public void shutdown(){
        if (serverSocket.isClosed())
            running = false;
        else{
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            running = false;
        }
    }
}