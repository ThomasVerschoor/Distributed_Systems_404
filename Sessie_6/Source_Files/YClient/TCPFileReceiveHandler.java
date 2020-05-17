import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPFileReceiveHandler extends  Thread {

    private static int port;
    private static String path;
    private static boolean running = true;

    public TCPFileReceiveHandler(int tcpFileReceivePort) {
        port = tcpFileReceivePort;
        path = System.getProperty("user.dir");
    }

    @Override
    public void run(){
        path = path.concat("\\nodeFiles");
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            TCPGetFile TCPGet = new TCPGetFile(socket,path);
            TCPGet.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit(){
        running = false;
    }
}
