
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPFileSendHandler extends Thread{

    private InetAddress sendAddress;
    private String fileName;
    private String hostName;
    private String path;
    private int port;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private FileInputStream fileInputStream;
    private BufferedInputStream bufferedInputStream;
    private Logger logger;

    public TCPFileSendHandler(String received, String filename, String hostName, int tcpFileSendPort, String filePath, Logger logger) {
        this.logger = logger;
        fileName = filename;
        String temp = received.substring(1);
        try {
            sendAddress = InetAddress.getByName(temp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.hostName = hostName;
        port = tcpFileSendPort;
        String workingDirectory = System.getProperty("user.dir");
        //TODO: Linux - Windows change
       //String filesLocation = workingDirectory+"\\nodeFiles";   //Windows
        String filesLocation = workingDirectory+"/nodeFiles";   //Linux
        //path = filesLocation+"\\"+filePath; //Windows
        path = filesLocation+"/"+filePath;  //Linux
    }

    public void run(){
        logger.log(Level.INFO,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] ID and name of TCPFileSend Thread");
        logger.log(Level.INFO,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Created thread to send replicated file '"+fileName+"' located at: '"+path+"' to: "+sendAddress);
        socket = null;
        outputStream = null;
        inputStream = null;
        dataOutputStream = null;
        dataInputStream = null;
        fileInputStream = null;
        bufferedInputStream = null;
        File file = new File(path);
        byte[] byteArray = new byte[(int)file.length()];
        String received = null;
        try {
            logger.log(Level.INFO,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Trying to initialize I/O Streams to send file...");
            socket = new Socket(sendAddress,port);
            outputStream = Objects.requireNonNull(socket).getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            inputStream = Objects.requireNonNull(socket).getInputStream();
            dataInputStream = new DataInputStream(Objects.requireNonNull(inputStream));
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            logger.log(Level.INFO,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Done trying to initialize I/O Streams to send file...");
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Something went wrong trying to initialize I/O Streams to send file");
        }
        try{
            Objects.requireNonNull(dataOutputStream).writeUTF(hostName+","+fileName+","+byteArray.length);
            logger.log(Level.INFO,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Sending hostname: "+hostName+", filename: "+fileName+" and filesize: "+byteArray.length);
            dataOutputStream.flush();
            received = Objects.requireNonNull(dataInputStream).readUTF();
            logger.log(Level.INFO,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Received: "+received);
            if (received.equals("ACK")){
                Objects.requireNonNull(bufferedInputStream).read(byteArray,0,byteArray.length);
                logger.log(Level.INFO,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Sending file "+ fileName);
                dataOutputStream.write(byteArray,0,byteArray.length);
                dataOutputStream.flush();
                received = Objects.requireNonNull(dataInputStream).readUTF();
                logger.log(Level.INFO,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Received: "+received);
                if (received.equals("ACK")) {
                    logger.log(Level.INFO, "[" + TCPFileSendHandler.currentThread().getId() + " | " + TCPFileSendHandler.currentThread().getName() + "] Node sends ACK, received file correctly...");
                    System.out.println("file: "+fileName+" sent correctly to : "+sendAddress);
                }
                else if (received.equals("NACK"))
                    logger.log(Level.SEVERE,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Node sends NACK, received file incorrectly...");
                else
                    logger.log(Level.SEVERE,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Node did not send anything back, something went wrong");
            }else
                logger.log(Level.SEVERE,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] node does not ACK hostName and FileName packet, something went wrong!");
            close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Something went wrong sending the actual file");
        }
    }

    private void close() {
        try {
            logger.log(Level.INFO,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Shutting down getFile Thread, cleaning up I/O streams and closing socket...");
            dataInputStream.close();
            dataOutputStream.close();
            outputStream.close();
            inputStream.close();
            fileInputStream.close();
            bufferedInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,"["+TCPFileSendHandler.currentThread().getId()+" | "+TCPFileSendHandler.currentThread().getName()+"] Something went wrong cleaning up I/O streams and closing socket");
        }
    }
}
