import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class YServer {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give the server socket: ");
        int chosenSocket = scanner.nextInt();
        ServerSocket serverSocket = new ServerSocket(chosenSocket);
        while (true){
            Socket socket = null;
            try {
                socket = serverSocket.accept(); //Nieuwe connectie...
                System.out.println("A new client is connected: " + socket);
                InetAddress connectedAdress = socket.getInetAddress();
                System.out.println("With IP : " + connectedAdress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}