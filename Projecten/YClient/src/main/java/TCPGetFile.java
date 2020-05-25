import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPGetFile extends Thread{

    private Socket socket;
    private String path;
    private boolean running = true;
    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private FileOutputStream fileOutputStream;
    private BufferedOutputStream bufferedOutputStream;
    private Logger logger;

    public TCPGetFile(Socket serverSocket, String path, Logger logger) {
        socket = serverSocket;
        this.path = path;
        this.logger = logger;
    }

    public void run(){
        logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] ID and name of TCPGetFile Thread");
        if (running) {
            logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Created thread to receive replicated file");
            byte[] byteArray = new byte[0];
            inputStream = null;
            outputStream = null;
            dataInputStream = null;
            dataOutputStream = null;
            fileOutputStream = null;
            bufferedOutputStream = null;
            String hostName = null;
            String fileName = null;
            int fileSize = 0;
            //INITIALIZE STREAMS
            try {
                logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Trying to initialize I/O Streams...");
                inputStream = socket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
                outputStream = socket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);
                logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Done trying to initialize I/O Streams...");
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.SEVERE,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Something went wrong trying to initialize I/O Streams...");
            }
            //GET HOSTNAME/FILENAME/FILESIZE
            try {
                logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Trying to receive hostname/filename/filesize");
                String received = Objects.requireNonNull(dataInputStream).readUTF();    //hostName,fileName,fileSize
                int index = received.indexOf(",");
                hostName = received.substring(0, index);
                String temp = received.substring(index + 1);
                index = temp.indexOf(",");
                fileName = temp.substring(0,index);
                fileSize = Integer.parseInt(temp.substring(index+1));
                byteArray = new byte[fileSize];
                if (!fileName.equals(null) && !hostName.equals(null)) {
                    logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Succesfully got hostname: " + hostName + " ,filename: " + fileName + " and filesize: "+fileSize+" ,sending ACK");
                    Objects.requireNonNull(dataOutputStream).writeUTF("ACK");   //SEND ACK
                } else {
                    logger.log(Level.SEVERE,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Something went wrong reading fileName and hostName...");
                    logger.log(Level.SEVERE,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Received fileName: " + fileName);
                    logger.log(Level.SEVERE,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Received hostName: " + hostName);
                    Objects.requireNonNull(dataOutputStream).writeUTF("NACK");  //SEND NACK | something went wrong
                }
                dataOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.SEVERE,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Something went wrong trying to receive hostname/filename/filesize");
            }
            //GET FILE
            try {
                if (!fileName.equals(null) && !hostName.equals(null)) {
                    logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] receiving file " + fileName);
                    //TODO: Linux - Windows change
                    //fileOutputStream = new FileOutputStream(path+"/"+fileName);  //linux
                    fileOutputStream = new FileOutputStream(path+"\\"+fileName);  //windows
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    int x;
                    while (fileSize > 0 && (x = inputStream.read(byteArray,0,(int)Math.min(byteArray.length, fileSize))) != -1){
                        bufferedOutputStream.write(byteArray,0,x);
                        fileSize -= x;
                    }
                    logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Done reading "+fileName+", writing to receivedFile location");
                    bufferedOutputStream.flush();
                    if (!(Arrays.toString(byteArray)).equals(null)) {
                        dataOutputStream.writeUTF("ACK");
                        logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Succesfully received file " + fileName + " ,sending ACK");
                        System.out.println("Replicated file successfully downloaded: "+fileName);
                        PrintWriter printWriter = null;
                        try{
                            logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Trying to create log file for downloaded file: "+fileName);
                            int index = fileName.indexOf(".");
                            String fileLogName = fileName.substring(0,index);
                            //TODO: Linux - Windows change
                            printWriter = new PrintWriter(new File(path+"\\"+fileLogName+".log")); //For Windows
                            //printWriter = new PrintWriter(new File(path+"/"+fileLogName+".log")); //For Linux
                            printWriter.println("Owner of the file: "+YClient.hostName);
                            printWriter.println("Download location: "+hostName);
                        } catch (FileNotFoundException e) {
                            logger.log(Level.SEVERE,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Something went wrong trying to create a log file for file: "+fileName);
                            e.printStackTrace();
                        }finally {
                            logger.log(Level.INFO,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Successfully created log file for downloaded file: "+fileName);
                            System.out.println("Generated log file for downloaded file: "+fileName);
                            if (printWriter!=null)
                                printWriter.close();
                        }
                    } else {
                        logger.log(Level.SEVERE,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Something went wrong reading " + fileName + " ,sending NACK");
                        logger.log(Level.SEVERE,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] ByteArray: " + Arrays.toString(byteArray));
                        dataOutputStream.writeUTF("NACK");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.SEVERE,"[" + TCPGetFile.currentThread().getId() + " | " + TCPGetFile.currentThread().getName() + "] Something went wrong trying to receive the actual file");
            }
        }
        exit();
    }

    public void exit() {
        logger.log(Level.INFO,"["+TCPGetFile.currentThread().getId()+" | "+TCPGetFile.currentThread().getName()+"] Shutting down getFile Thread, cleaning up I/O streams and closing socket...");
        try {
            socket.close();
            inputStream.close();
            outputStream.close();
            dataInputStream.close();
            dataOutputStream.close();
            fileOutputStream.close();
            bufferedOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,"["+TCPGetFile.currentThread().getId()+" | "+TCPGetFile.currentThread().getName()+"] Something went wrong shutting down getFile Thread");
        }
        running = false;
    }
}