package com.company;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        System.out.println("gib me port nr: ");
        Scanner sc = new Scanner(System.in);
        int port = sc.nextInt();
        System.out.println("\n now gib address: ");
        String ip = sc.nextLine();
        DatagramSocket ds = new DatagramSocket(port);


        byte[] b1=new byte[1024];
        DatagramPacket dp = new DatagramPacket(b1, b1.length);
        ds.receive(dp);
        String str = new String(dp.getData(),0,dp.getLength());
        int num = Integer.parseInt(str.trim());
        int result = num*num;
        Socket s = new Socket(ip, port);
        byte[] b2 = String.valueOf(result).getBytes();
        DatagramPacket dp1 = new DatagramPacket(b2, b2.length, s.getInetAddress(), s.getPort());
        ds.send(dp1);


    }


}
