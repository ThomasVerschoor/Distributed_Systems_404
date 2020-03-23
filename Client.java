package com.company;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public Client (String address, int port) throws IOException {
        int number, temp;

        Scanner sc = new Scanner(System.in);
        Socket s = new Socket(address, port); //IP-address of server, port of server
        Scanner sc1 = new Scanner(s.getInputStream());

        System.out.println("Enter a number: ");
        number = sc.nextInt();

        PrintStream p = new PrintStream(s.getOutputStream());
        p.println(number);

        temp = sc1.nextInt();
        System.out.println(temp);
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 5000);
    }
}
