import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPReplicatedFileReceiveHandler extends Thread{

    private int port;
    private String filePath;
    private boolean running = true;
    private ServerSocket serverSocket;
    private Logger logger;

    public TCPReplicatedFileReceiveHandler(int port, Logger logger){
        this.logger = logger;
        this.port = port;
        this.logger.log(Level.INFO,"[" + TCPReplicatedFileReceiveHandler.currentThread().getId() + " | " + TCPReplicatedFileReceiveHandler.currentThread().getName() + "] Starting TCPReplicatedFile listener on port: "+port);
        filePath = System.getProperty("user.dir");
        //TODO: Linux - Windows change
        //filePath = filePath.concat("\\receivedFiles");    //Windows
        filePath = filePath.concat("/receivedFiles");   //Linux
        try {
            this.logger.log(Level.INFO,"[" + TCPReplicatedFileReceiveHandler.currentThread().getId() + " | " + TCPReplicatedFileReceiveHandler.currentThread().getName() + "] Trying to create TCPReplicatedFileReceive Socket...");
            serverSocket = new ServerSocket(port);
            this.logger.log(Level.INFO,"[" + TCPReplicatedFileReceiveHandler.currentThread().getId() + " | " + TCPReplicatedFileReceiveHandler.currentThread().getName() + "] Done creating TCPReplicatedFileReceive Socket...");
        } catch (IOException e) {
            e.printStackTrace();
            this.logger.log(Level.SEVERE,"[" + TCPReplicatedFileReceiveHandler.currentThread().getId() + " | " + TCPReplicatedFileReceiveHandler.currentThread().getName() + "] Something went wrong trying to create TCPReplicatedFileReceive Socket");
        }
    }

    @Override
    public void run() {
        logger.log(Level.INFO,"[" + TCPReplicatedFileReceiveHandler.currentThread().getId() + " | " + TCPReplicatedFileReceiveHandler.currentThread().getName() + "] ID and name of TCPReplicatedFileReceive Thread");
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                logger.log(Level.INFO,"[" + TCPReplicatedFileReceiveHandler.currentThread().getId() + " | " + TCPReplicatedFileReceiveHandler.currentThread().getName() + "] TCPReplicatedFileReceive connection detected!");
                logger.log(Level.INFO,"[" + TCPReplicatedFileReceiveHandler.currentThread().getId() + " | " + TCPReplicatedFileReceiveHandler.currentThread().getName() + "] Requesting thread to handle receive file and its log file");
                TCPGetReplicatedFile TCPGetReplicated = new TCPGetReplicatedFile(socket, filePath, logger);
                TCPGetReplicated.start();
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Level.SEVERE,"[" + TCPReplicatedFileReceiveHandler.currentThread().getId() + " | " + TCPReplicatedFileReceiveHandler.currentThread().getName() + "] Something went wrong while waiting for TCPReplicatedFileReceive connection, it could be that client exited and socket was closed!");
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
