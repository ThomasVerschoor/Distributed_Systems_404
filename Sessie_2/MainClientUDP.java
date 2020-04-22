package com.company;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        System.out.println("gib me poortnr: ");

        Scanner sc = new Scanner(System.in);
        int port = sc.nextInt();
        System.out.println("\n now gib address: ");
        String ip = sc.nextLine();
        Socket socket = new Socket(ip, port);

        DatagramSocket ds = new DatagramSocket(port);

        byte[] b1=new byte[1024];
        DatagramPacket dp = new DatagramPacket(b1, b1.length);
        ds.receive(dp);
        String str = new String(dp.getData(),0,dp.getLength());
        int num = Integer.parseInt(str.trim());
        int result = num*num;

        InetAddress ia = socket.getInetAddress();
        byte[] b2 = String.valueOf(result).getBytes();
        DatagramPacket dp1 = new DatagramPacket(b2, b2.length, ia, port);
        ds.send(dp1);


    }


}
