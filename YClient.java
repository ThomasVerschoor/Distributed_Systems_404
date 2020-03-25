import java.io.*;
import java.net.*;
import java.util.Scanner;

public class YClient {
    public static void main(String[] args) throws IOException{
        try{
            System.out.println("gib server ip: ");
            Scanner scanny = new Scanner(System.in);
            String ip = scanny.nextLine();    //Dit zoekt voor de host ip...
            System.out.println("\n gib port nr: ");
            int port = scanny.nextInt();
            System.out.println("\n");
            Socket socket = new Socket(ip,port);   //Connectie met server op poort (...) ...
            scanny.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}