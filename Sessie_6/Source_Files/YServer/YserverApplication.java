package distributed.yproject.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class YserverApplication {

    private static final int recievePort = 4501;
    private static final int sendPort = 4500;
    private static final int TCPMessageRecievePort = 5501;
    private static final int multicastPort = 3456;
    private static final String multicastAddress = "225.6.7.8";
    private static boolean running = true;

    public static void main(String[] args){
        System.out.println("----------------------------------------------------");
        //NodeHandler.addNode("Mathijs","/192.168.12.1");
        //NodeHandler.addNode("Bilal","/192.168.12.2");
        //NodeHandler.addNode("Jorre","/192.168.12.3");
        //NodeHandler.addNode("Thomas","/192.168.12.4");
        UDPServer UDP = new UDPServer(recievePort,sendPort);
        UDP.start();
        UDPMultiServer UDPM = new UDPMultiServer(multicastPort,multicastAddress,sendPort);
        UDPM.start();
        TCPServer TCP = new TCPServer(TCPMessageRecievePort);
        TCP.start();
        System.out.println("starting REST server...");
        System.out.println("----------------------------------------------------");
        ConfigurableApplicationContext ctx = SpringApplication.run(YserverApplication.class, args);
        System.out.println(" ");
        Scanner scanny = new Scanner(System.in);
        while(running){
            String input = scanny.nextLine();
            switch(input){
                case "Exit":
                    System.out.println("----------------------------------------------------");
                    System.out.println("Stopping UDP server...");
                    UDP.shutdown();
                    System.out.println("Stopping UDPMulti server...");
                    UDPM.shutdown();
                    System.out.println("Stopping RESTServer...");
                    ctx.close();
                    running = false;
                    System.out.println("Stopping YServer...");
                    System.out.println("----------------------------------------------------");
                    break;

                case "help":
                    System.out.println("----------------------------------------------------");
                    System.out.println("Available commands: ");
                    System.out.println("Exit <This Exits YServer>");
                    System.out.println("help <This shows all available commands>");
                    System.out.println("----------------------------------------------------");
                    System.out.println(" ");
                    break;
            }
        }
        System.exit(0);
    }
}