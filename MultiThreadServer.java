import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(30028); //Poort van Server
        while(true){
            Socket socket = null;
            try {
                socket = serverSocket.accept(); //ALS er een neiuwe connecie naar ServerSocket komt steken we in socket de socket van de Host die connecteerd
                System.out.println("A new client is connected: " + socket);
                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                System.out.println("Assigning new thread for this client");
                Thread thread = new MultiThreadClientHandler(socket,dataIn,dataOut);   //We maken een nieuwe thread aan voor elke client die joinen
                thread.start();                                             // met daarin een runnende Client handler die onze request zal regelen.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
