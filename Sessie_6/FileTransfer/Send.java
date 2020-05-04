import java.io.*;
import java.net.Socket;

public class Send {

    public static void main(String[] args) throws IOException {
        // write your code here
        Socket s = new Socket("127.0.0.1", 9996); //send file to replicated node

        File sendFile = new File("C:\\Users\\mathi\\Desktop\\nodeFiles\\aaaaaaa.txt");   //java File not object of class File
        byte[] byteArray = new byte[(int)sendFile.length()];

        FileInputStream fis = new FileInputStream(sendFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(byteArray, 0, byteArray.length);
        OutputStream os = s.getOutputStream();
        os.write(byteArray, 0, byteArray.length);
        os.flush();
        os.close();
        bis.close();
        fis.close();
        s.close();
    }

}
