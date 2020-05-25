import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPGetReplicatedFile extends Thread{

    private Socket socket;
    private String filePath;
    private boolean running = true;
    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private FileOutputStream fileOutputStream;
    private BufferedOutputStream bufferedOutputStream;
    private FileOutputStream logFileOutputStream;
    private BufferedOutputStream logBufferedOutputStream;
    private Logger logger;

    public TCPGetReplicatedFile(Socket socket, String filePath, Logger logger) {
        this.socket = socket;
        this.filePath = filePath;
        this.logger = logger;
    }

    public void run(){
        logger.log(Level.INFO,"[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] ID and name of TCPGetReplicatedFile Thread");
        if (running) {
            logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Created thread to receive replicated file and its log");
            byte[] byteArray = new byte[0];
            byte[] logByteArray = new byte[0];
            inputStream = null;
            outputStream = null;
            dataInputStream = null;
            dataOutputStream = null;
            fileOutputStream = null;
            bufferedOutputStream = null;
            logFileOutputStream = null;
            logBufferedOutputStream = null;
            String hostName = null;
            String fileName = null;
            int fileSize = 0;
            String logFileName = null;
            int logFileSize = 0;
            //INITIALIZE STREAMS
            try {
                logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Trying to initialize I/O Streams...");
                inputStream = socket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
                outputStream = socket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);
                logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Done trying to initialize I/O Streams...");
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Something went wrong trying to initialize I/O Streams...");
            }
            //GET HOSTNAME/FILENAME/FILESIZE/LOGFILENAME/LOGFILESIZE
            try {
                logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Trying to receive hostname/filename/filesize/logFileName/logFileSize");
                String received = Objects.requireNonNull(dataInputStream).readUTF();    //hostName,fileName,fileSize,logFileName,logFileSize
                int index = received.indexOf(",");
                hostName = received.substring(0, index);    //hostName
                String temp = received.substring(index + 1);
                index = temp.indexOf(",");
                fileName = temp.substring(0, index); //fileName
                temp = temp.substring(index + 1);
                index = temp.indexOf(",");
                fileSize = Integer.parseInt(temp.substring(0, index)); //fileSize
                temp = temp.substring(index + 1);
                index = temp.indexOf(",");
                logFileName = temp.substring(0, index);
                logFileSize = Integer.parseInt(temp.substring(index + 1));
                byteArray = new byte[fileSize];
                logByteArray = new byte[logFileSize];
                if (!(fileName.equals(null)) && !(hostName.equals(null)) && !(fileSize == 0) && !(logFileName.equals(null)) && !(logFileSize == 0)) {
                    logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Succesfully got hostname: " + hostName + " ,filename: " + fileName + " ,filesize: " + fileSize + ",logFileName: " + logFileName + ",logFileSize: " + logFileSize + " ,sending ACK");
                    Objects.requireNonNull(dataOutputStream).writeUTF("ACK");   //SEND ACK
                } else {
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Something went wrong reading fileName, hostName, fileSize, logFileName and logFileSize...");
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Received fileName: " + fileName);
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Received hostName: " + hostName);
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Received fileSize: " + fileSize);
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Received logFileName: " + logFileName);
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Received logFileSize: " + logFileSize);
                    Objects.requireNonNull(dataOutputStream).writeUTF("NACK");  //SEND NACK | something went wrong
                }
                dataOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Something went wrong trying to receive hostname/filename/filesize/logFileName/logFileSize");
            }
            //GET FILE
            try {
                logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] receiving file " + fileName);
                //TODO: Linux - Windows change
                //fileOutputStream = new FileOutputStream(path+"/"+fileName);  //linux
                fileOutputStream = new FileOutputStream(filePath + "\\" + fileName);  //windows
                bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                int x;
                while (fileSize > 0 && (x = inputStream.read(byteArray, 0, (int) Math.min(byteArray.length, fileSize))) != -1) {
                    bufferedOutputStream.write(byteArray, 0, x);
                    fileSize -= x;
                }
                logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Done reading " + fileName + ", writing to receivedFile location");
                bufferedOutputStream.flush();
                if (!(Arrays.toString(byteArray)).equals(null)) {
                    dataOutputStream.writeUTF("ACK");
                    dataOutputStream.flush();
                    logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Succesfully received file " + fileName + " ,sending ACK");
                    System.out.println("Replicated file successfully downloaded: " + fileName);
                } else {
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Something went wrong reading " + fileName + " ,sending NACK");
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] ByteArray: " + Arrays.toString(byteArray));
                    dataOutputStream.writeUTF("NACK");
                    dataOutputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Something went wrong trying to receive the actual file");
            }
            //GET LOGFILE
            try {
                logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] receiving log file " + logFileName);
                //TODO: Linux - Windows change
                //fileOutputStream = new FileOutputStream(logFilePath+"/"+logFileName);  //linux
                logFileOutputStream = new FileOutputStream(filePath + "\\" + logFileName);  //windows
                logBufferedOutputStream = new BufferedOutputStream(logFileOutputStream);
                int x;
                while (logFileSize > 0 && (x = inputStream.read(logByteArray, 0, (int) Math.min(logByteArray.length, logFileSize))) != -1) {
                    logBufferedOutputStream.write(logByteArray, 0, x);
                    logFileSize -= x;
                }
                logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Done reading " + logFileName + ", writing to receivedFile location");
                logBufferedOutputStream.flush();
                if (!(Arrays.toString(logByteArray)).equals(null)) {
                    dataOutputStream.writeUTF("ACK");
                    dataOutputStream.flush();
                    logger.log(Level.INFO, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Succesfully received file " + logFileName + " ,sending ACK");
                    System.out.println("Replicated LogFile successfully downloaded: " + logFileName);
                } else {
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Something went wrong reading " + logFileName + " ,sending NACK");
                    logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] ByteArray: " + Arrays.toString(logByteArray));
                    dataOutputStream.writeUTF("NACK");
                    dataOutputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, "[" + TCPGetReplicatedFile.currentThread().getId() + " | " + TCPGetReplicatedFile.currentThread().getName() + "] Something went wrong trying to receive the actual file");
            }
            exit();
        }
    }

    public void exit() {
        logger.log(Level.INFO,"["+TCPGetReplicatedFile.currentThread().getId()+" | "+TCPGetReplicatedFile.currentThread().getName()+"] Shutting down getReplicatedFile Thread, cleaning up I/O streams and closing socket...");
        try {
            socket.close();
            inputStream.close();
            outputStream.close();
            dataInputStream.close();
            dataOutputStream.close();
            fileOutputStream.close();
            bufferedOutputStream.close();
            logFileOutputStream.close();
            logBufferedOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,"["+TCPGetReplicatedFile.currentThread().getId()+" | "+TCPGetReplicatedFile.currentThread().getName()+"] Something went wrong shutting down getReplicatedFile Thread");
        }
        running = false;
    }
}
