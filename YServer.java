import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class YServer {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give the server socket: ");
        int chosenSocket = Integer.parseInt(scanner.nextLine());
        ServerSocket serverSocket = new ServerSocket(chosenSocket);
        while (true){
            Socket socket = null;
            try {
                socket = serverSocket.accept(); //Nieuwe connectie...
                System.out.println("A new client is connected: " + socket + " With IP: "+ socket.getInetAddress());
                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                System.out.println("Starting iniThread for this client");
                Thread thread = new YiniClient(socket,dataIn,dataOut);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class YiniClient extends Thread{
    final DataInputStream dataIn;
    final DataOutputStream dataOut;
    final Socket socket;

    public YiniClient(Socket socket, DataInputStream dataIn, DataOutputStream dataOut){
        this.dataIn = dataIn;
        this.dataOut = dataOut;
        this.socket = socket;
    }

    @Override
    public void run(){
        String received;
        try{
            dataOut.writeUTF("Enter the name you would liek to use for this node: ");
            received = dataIn.readUTF();
            if (received.equals("Exit")){
                System.out.println("Client " + this.socket + " sends exit...");
                System.out.println("Closing this connection.");
                this.socket.close();
                System.out.println("Connection closed");
            }
            System.out.println("Node has chosen" + received + "as name, executing hashing function");
            //Hashing met naam dat gegeven is moet hier gebruiken map(received)
            dataOut.writeUTF(received+" has been set succesfully as name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.dataOut.close();
            this.dataIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}