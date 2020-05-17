import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class TCPGetFile extends Thread{

    private static Socket socket;
    private static String path;

    public TCPGetFile(Socket serverSocket, String path) {
        TCPGetFile.socket = serverSocket;
        TCPGetFile.path = path;
    }

    public void run(){
        System.out.println("["+TCPGetFile.currentThread().getId()+" | "+TCPGetFile.currentThread().getName()+"] Created thread to receive replicated file");
        byte[] byteArray = new byte[6022386];
        InputStream inputStream = null;
        OutputStream outputStream = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        String hostName = null;
        String fileName = null;
        //INITIALIZE STREAMS
        try{
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //GET HOSTNAME/FILENAME
        try {
            String received = Objects.requireNonNull(dataInputStream).readUTF();    //hostName,fileName
            int index = received.indexOf(",");
            hostName = received.substring(0,index);
            fileName = received.substring(index+1);
            if (!fileName.equals(null) && !hostName.equals(null)){
                Objects.requireNonNull(dataOutputStream).writeUTF("ACK");   //SEND ACK
                dataOutputStream.flush();
            }else {
                System.out.println("["+TCPGetFile.currentThread().getId()+" | "+TCPGetFile.currentThread().getName()+"] Something went wrong reading fileName and hostName...");
                System.out.println("["+TCPGetFile.currentThread().getId()+" | "+TCPGetFile.currentThread().getName()+"] Received fileName: "+fileName);
                System.out.println("["+TCPGetFile.currentThread().getId()+" | "+TCPGetFile.currentThread().getName()+"] Received hostName: "+hostName);
                Objects.requireNonNull(dataOutputStream).writeUTF("NACK");  //SEND NACK | something went wrong
                dataOutputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //GET FILE
        try {
            if (!fileName.equals(null) && !hostName.equals(null)) {
                fileOutputStream = new FileOutputStream(path+"\\received\\"+fileName);
                bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                int bytesRead = inputStream.read(byteArray,0,byteArray.length);
                int current = bytesRead;
                do{
                    bytesRead = inputStream.read(byteArray, current, (byteArray.length - current));
                    if (bytesRead >= 0) current += bytesRead;
                }while (bytesRead > -1);
                bufferedOutputStream.write(byteArray,0,current);
                bufferedOutputStream.flush();
                fileOutputStream.flush();
                fileOutputStream.close();
                bufferedOutputStream.close();
                if (!(Arrays.toString(byteArray)).equals(null))
                    dataOutputStream.writeUTF("ACK");
                else {
                    System.out.println("["+TCPGetFile.currentThread().getId()+" | "+TCPGetFile.currentThread().getName()+"] Something went wrong reading the file...");
                    System.out.println("["+TCPGetFile.currentThread().getId()+" | "+TCPGetFile.currentThread().getName()+"] byteArray: "+ Arrays.toString(byteArray));
                    dataOutputStream.writeUTF("NACK");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}