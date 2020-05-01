package com.company;

import java.net.*;
import java.io.*;
import java.util.*;

public class YServer {

  /*  static void Startup(){
    }
    static void Running(){
    }
    static void Exit(){
    }
    */

    public static void main(String[] args) throws Exception{


        //Startup();      //launch at startup multicast
        //Running();      //commands
        //Exit();         //send to server

        System.setProperty("java.net.preferIPv4Stack", "true");     //ipv4 gebruiken
        try{
            InetAddress group = InetAddress.getByName("225.6.7.8");
            MulticastSocket socket = new MulticastSocket(); //create new socket
            String message = "UDP Multicasting test!";
            System.out.println("message sent");

            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), group, 3456);
            socket.send(packet);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
