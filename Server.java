package com.company;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public Server(int port) throws IOException {
        int number, temp;

        ServerSocket s1 = new ServerSocket(port);
        Socket ss = s1.accept();
        Scanner sc = new Scanner(ss.getInputStream());
        number = sc.nextInt();

        temp = number*2;

        PrintStream p = new PrintStream(ss.getOutputStream());
        p.println(temp);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);

    }
}
