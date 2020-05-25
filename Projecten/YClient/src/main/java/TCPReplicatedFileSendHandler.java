import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPReplicatedFileSendHandler extends Thread{

    private String destinationIP;
    private InetAddress destinationAddress;
    private String replicatedFileName;
    private String replicatedLogFileName;
    private Logger logger;
    private String path;
    private String hostName;
    private int destinationPort;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private FileInputStream replicatedFileInputStream;
    private FileInputStream replicatedLogFileInputStream;
    private BufferedInputStream bufferedInputStream1;
    private BufferedInputStream bufferedInputStream2;

    public TCPReplicatedFileSendHandler(int tcpReplicatedFileSendPort, String ip, String file, String s, String hostName, Logger logger) {
        destinationIP = ip;
        destinationIP = destinationIP.substring(1);
        replicatedFileName = file;
        replicatedLogFileName = s;
        this.hostName = hostName;
        destinationPort = tcpReplicatedFileSendPort;
        try {
            destinationAddress = InetAddress.getByName(destinationIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.logger = logger;
        String workingDirectory = System.getProperty("user.dir");
        //TODO: Windows - Linux
        path = workingDirectory+"\\receivedFiles\\";    //For windows
        //path = workingDirectory+"/receivedFiles/";  //For linux;
    }

    public void run(){
        logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] ID and name of TCPFileSend Thread");
        logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Created thread to send replicated file '"+replicatedFileName+"' and his log file '"+replicatedLogFileName+"' to: "+destinationAddress);
        socket = null;
        outputStream = null;
        inputStream = null;
        dataOutputStream = null;
        dataInputStream = null;
        replicatedFileInputStream = null;
        replicatedLogFileInputStream = null;
        bufferedInputStream1 = null;
        bufferedInputStream2 = null;
        File replicatedFile = new File(path+replicatedFileName);
        File replicatedLogFile = new File(path+replicatedLogFileName);
        byte[] fileArray = new byte[(int)replicatedFile.length()];
        byte[] logFileArray = new byte[(int)replicatedLogFile.length()];
        String received = null;
        try {
            logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Trying to initialize I/O Streams to send replicated file and it's log file...");
            socket = new Socket(destinationAddress,destinationPort);
            outputStream = Objects.requireNonNull(socket).getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            inputStream = Objects.requireNonNull(socket).getInputStream();
            dataInputStream = new DataInputStream(Objects.requireNonNull(inputStream));
            replicatedFileInputStream = new FileInputStream(replicatedFile);
            replicatedLogFileInputStream = new FileInputStream(replicatedLogFile);
            bufferedInputStream1 = new BufferedInputStream(replicatedFileInputStream);
            bufferedInputStream2 = new BufferedInputStream(replicatedLogFileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Something went wrong trying to initialize I/O Streams to send replicated file and it's log file");
        }
        try {
            //eerst hostName, fileName, fileSize, logFileName en logFileSize doorsturen
            //ACK
            //daarna file doorsturen
            //ACK
            //daarna logFile doorsturen
            //ACK
            Objects.requireNonNull(dataOutputStream).writeUTF(hostName+","+replicatedFileName+","+fileArray.length+","+replicatedLogFileName+","+logFileArray.length);
            System.out.println(hostName +" "+ replicatedFileName +" "+ fileArray.length + " " + replicatedLogFileName +" "+ logFileArray.length);
            logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Sending hostname: "+hostName+", filename: "+replicatedFileName+", filesize: "+replicatedFile.length()+", logFileName: " +replicatedLogFileName+", logFileSize: "+replicatedLogFileName.length());
            dataOutputStream.flush();
            received = Objects.requireNonNull(dataInputStream).readUTF();
            logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Received: "+received);
            if (received.equals("ACK")){
                Objects.requireNonNull(bufferedInputStream1).read(fileArray,0,fileArray.length);
                logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Node sends ACK, received hostName, fileName, fileSize, logFileName and logFileSize correctly");
                logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Sending file : "+replicatedFileName);
                dataOutputStream.write(fileArray,0,fileArray.length);
                dataOutputStream.flush();
                received = Objects.requireNonNull(dataInputStream).readUTF();
                logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Received: "+received);
                if (received.equals("ACK")){
                    Objects.requireNonNull(bufferedInputStream2).read(logFileArray, 0, logFileArray.length);
                    logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Node sends ACK, received replicated file "+replicatedFileName+" correctly");
                    dataOutputStream.write(logFileArray,0,logFileArray.length);
                    dataOutputStream.flush();
                    received = dataInputStream.readUTF();
                    logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Received: "+received);
                    if (received.equals("ACK")) {
                        logger.log(Level.INFO, "[" + TCPReplicatedFileSendHandler.currentThread().getId() + " | " + TCPReplicatedFileSendHandler.currentThread().getName() + "] Node sends ACK, received replicated log file " + replicatedLogFileName + " correctly");
                    }
                    else{
                        logger.log(Level.SEVERE,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Node sends NACK on replicated log file: "+replicatedLogFileName);
                        close();
                    }
                }else{
                    logger.log(Level.SEVERE,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Node sends NACK on replicated file: "+replicatedFileName);
                    close();
                }
            }else
                logger.log(Level.SEVERE, "[" + TCPReplicatedFileSendHandler.currentThread().getId() + " | " + TCPReplicatedFileSendHandler.currentThread().getName() + "] Node sends NACK on hostName, fileName, fileSize, logFileName and logFileSize");
            close();

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "[" + TCPReplicatedFileSendHandler.currentThread().getId() + " | " + TCPReplicatedFileSendHandler.currentThread().getName() + "] Something went wrong during the process of transfering the replicated file and its log");
        }
    }

    private void close() {
        try {
            logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Shutting down sendReplicatedFile Thread, cleaning up I/O streams and closing socket...");
            dataInputStream.close();
            dataOutputStream.close();
            outputStream.close();
            inputStream.close();
            replicatedFileInputStream.close();
            replicatedLogFileInputStream.close();
            bufferedInputStream1.close();
            bufferedInputStream2.close();
            socket.close();
            logger.log(Level.INFO,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Done trying to initialize I/O Streams to send ...");
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,"["+TCPReplicatedFileSendHandler.currentThread().getId()+" | "+TCPReplicatedFileSendHandler.currentThread().getName()+"] Something went wrong cleaning up I/O streams and closing socket in sendReplicatedFileThread");
        }
    }

}
