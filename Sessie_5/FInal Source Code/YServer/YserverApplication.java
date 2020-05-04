package distributed.yserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class YserverApplication {

    private static final int recievePort = 4501;
    private static final int sendPort = 4500;
    private static final int multicastPort = 3456;
    private static final String multicastAddress = "225.6.7.8";
    private static boolean running = true;

    public static void main(String[] args){
        UDPServer UDP = new UDPServer(recievePort,sendPort);
        UDP.start();
        UDPMultiServer UDPM = new UDPMultiServer(multicastPort,multicastAddress,sendPort);
        UDPM.start();
        System.out.println("starting REST server...");
        ConfigurableApplicationContext ctx = SpringApplication.run(YserverApplication.class, args);
        Scanner scanny = new Scanner(System.in);
        while(running){
            String input = scanny.nextLine();
            switch(input){
                case "Exit":
                    System.out.println("Stopping UDP server...");
                    UDP.shutdown();
                    System.out.println("Stopping UDPMulti server...");
                    UDPM.shutdown();
                    System.out.println("Stopping RESTServer...");
                    ctx.close();
                    running = false;
                    System.out.println("Stopping YServer...");
                    break;

                case "help":
                    System.out.println("Available commands: ");
                    System.out.println("Exit <This Exits YServer>");
                    System.out.println("help <This shows all available commands>");
                    break;
            }
        }
        System.exit(0);
    }

}
