import java.io.*;
import java.net.*;
import java.util.Scanner;

public class YClient {
    static Socket socket;
    public static void main(String[] args) throws IOException{
        System.out.println("give server ip: ");
        Scanner scanny = new Scanner(System.in);
        String ip = scanny.nextLine();
        System.out.println("\n give port nr: ");
        int port = Integer.parseInt(scanny.nextLine());
        socket = new Socket(ip,port);
        DataInputStream dataIn = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
        try{
            System.out.println(dataIn.readUTF());
            String toSend = scanny.nextLine();
            dataOut.writeUTF(toSend);
            if (!toSend.equals("Exit")) {
                System.out.println("Name: " + toSend);
                System.out.println("Sending...");
                System.out.println(dataIn.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while(true){
                System.out.println(dataIn.readUTF());   //UTF is een manier van codering om bytes do or te streamen...
                String toSend = scanny.nextLine();
                dataOut.writeUTF(toSend);
                System.out.println("Sending: "+toSend);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}