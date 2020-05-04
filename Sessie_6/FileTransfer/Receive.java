import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Receive {
    public static void main(String[] args) throws IOException {
        // write your code here
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ServerSocket s = null;

        try{
        s = new ServerSocket(9996);
        Socket socket = s.accept();

        //receive file
        byte[] byteArray = new byte[6022386];
        InputStream is = socket.getInputStream();
        fos = new FileOutputStream("C:\\Users\\mathi\\Desktop\\nodeFiles\\received\\aaaaaaa.txt");
        bos = new BufferedOutputStream(fos);
        bytesRead = is.read(byteArray, 0, byteArray.length);
        current = bytesRead;

        do {
            bytesRead = is.read(byteArray, current, (byteArray.length - current));
            if (bytesRead >= 0) current += bytesRead;
        } while (bytesRead > -1);

        bos.write(byteArray, 0, current);
        bos.flush();
    }
    finally {
        if (fos != null) fos.close();
        if (bos != null) bos.close();
        if (s != null) s.close();
    }
    }
}
