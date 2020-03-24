import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MultiThreadClient {
    public static void main(String[] args) throws IOException{
        try{
            Scanner scanny = new Scanner(System.in);
            InetAddress ip = InetAddress.getByName("localhost");    //Dit zoekt voor de host ip...
            Socket socket = new Socket(ip,30028);   //Connectie met server op poort (...) ...
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());  //Input
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());  //Output
            while(true){    //Deze loop zal info tussen client en client handler uitwisselen...
                System.out.println(dataIn.readUTF());   //UTF is een manier van codering om bytes do or te streamen...
                String toSend = scanny.nextLine();
                System.out.println("Number to send: " + toSend);
                dataOut.writeUTF(toSend);
                System.out.println("Sending: "+dataOut);
                if (toSend.equals("Exit")){ //Als men Exit typt zal men de connectie sluiten
                    System.out.println("Closing this connection: "+ socket);
                    socket.close();
                    System.out.println("Connection closed");
                    break;
                }
                String recieved = dataIn.readUTF();
                System.out.println(recieved);
            }
            scanny.close();
            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
