import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPFileReceiveHandler extends  Thread {

    private int port;
    private String path;
    private boolean running = true;
    private ServerSocket serverSocket;
    private Logger logger;

    public TCPFileReceiveHandler(int tcpFileReceivePort, Logger logger) {
        this.logger = logger;
        this.logger.log(Level.INFO,"Starting TCPFile listener on port: "+tcpFileReceivePort);
        port = tcpFileReceivePort;
        path = System.getProperty("user.dir");
        //TODO: Linux - Windows change
        //path = path.concat("\\receivedFiles");    //Windows
        path = path.concat("/receivedFiles");   //Linux
        try {
            this.logger.log(Level.INFO,"[" + TCPFileReceiveHandler.currentThread().getId() + " | " + TCPFileReceiveHandler.currentThread().getName() + "] Trying to create TCPFileReceive Socket...");
            serverSocket = new ServerSocket(port);
            this.logger.log(Level.INFO,"[" + TCPFileReceiveHandler.currentThread().getId() + " | " + TCPFileReceiveHandler.currentThread().getName() + "] Done creating TCPFileReceive Socket...");
        } catch (IOException e) {
            e.printStackTrace();
            this.logger.log(Level.SEVERE,"[" + TCPFileReceiveHandler.currentThread().getId() + " | " + TCPFileReceiveHandler.currentThread().getName() + "] Something went wrong trying to create TCPFileReceive Socket");
        }
    }

    @Override
    public void run(){
        logger.log(Level.INFO,"[" + TCPFileReceiveHandler.currentThread().getId() + " | " + TCPFileReceiveHandler.currentThread().getName() + "] ID and name of TCPFileReceive Thread");
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                logger.log(Level.INFO,"[" + TCPFileReceiveHandler.currentThread().getId() + " | " + TCPFileReceiveHandler.currentThread().getName() + "] TCPFileReceive connection detected!");
                logger.log(Level.INFO,"[" + TCPFileReceiveHandler.currentThread().getId() + " | " + TCPFileReceiveHandler.currentThread().getName() + "] Requesting thread to handle receive file");
                TCPGetFile TCPGet = new TCPGetFile(socket, path, logger);
                TCPGet.start();
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Level.SEVERE,"[" + TCPFileReceiveHandler.currentThread().getId() + " | " + TCPFileReceiveHandler.currentThread().getName() + "] Something went wrong while waiting for TCPFileReceive connection, it could be that client exited and socket was closed!");
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
