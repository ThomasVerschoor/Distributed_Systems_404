import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(30028); //Poort van Server
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept(); //ALS er een neiuwe connecie naar ServerSocket komt steken we in socket de socket van de Host die connecteerd
                System.out.println("A new client is connected: " + socket);
                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                System.out.println("Assigning new thread for this client");
                Thread thread = new MultiThreadClientHandler(socket, dataIn, dataOut);   //We maken een nieuwe thread aan voor elke client die joinen
                thread.start();                                             // met daarin een runnende Client handler die onze request zal regelen.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class MultiThreadClientHandler extends Thread{
    final DataInputStream dataIn;
    final DataOutputStream dataOut;
    final Socket socket;

    public MultiThreadClientHandler(Socket socket, DataInputStream dataIn, DataOutputStream dataOut) {    //Elke keer als er een nieuwe client wil verbinden
        this.dataIn = dataIn;                                                           // maken we een nieuwe instantie van ClientHandler
        this.dataOut = dataOut;
        this.socket = socket;
    }

    @Override
    public void run(){
        String toReturn;
        String received;
        while(true){
            try{
                dataOut.writeUTF("Enter a number: ");
                received = dataIn.readUTF();
                if(received.equals("Exit")){
                    System.out.println("Client " + this.socket + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.socket.close();
                    System.out.println("Connection closed");
                    break;
                }
                int recievedNumber = Integer.parseInt(received);
                int calculatedNumber = recievedNumber*2;
                toReturn = Integer.toString(calculatedNumber);
                dataOut.writeUTF(toReturn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try{
            this.dataIn.close();
            this.dataOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}