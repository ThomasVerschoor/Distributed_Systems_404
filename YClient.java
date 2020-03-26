import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class YClient {
    public static void main(String[] args) throws IOException{
        try{
            System.out.println("give the IP of the NameServer: ");
            Scanner scanny = new Scanner(System.in);
            String ip = scanny.nextLine();
            System.out.println("\n give the port nr of the NameServer: ");
            int port = Integer.parseInt(scanny.nextLine());
            System.out.println("\n");
            Socket socket = new Socket(ip,port);   //Connectie met server op poort (...) ...
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());  //Input
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());  //Output
            System.out.println(dataIn.readUTF());
            String toSend = scanny.nextLine();
            if (toSend.equals("Exit")){ //Als men Exit typt zal men de connectie sluiten
                System.out.println("Closing this connection: "+ socket);
                socket.close();
                System.out.println("Connection closed");
            }
            dataOut.writeUTF(toSend);
            System.out.println(dataIn.readUTF());
            scanny.close();
            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}