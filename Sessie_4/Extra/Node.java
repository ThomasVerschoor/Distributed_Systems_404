package com.company;

import static java.lang.StrictMath.abs;

public class Node {

    private String hostname;
    private int ID;
    private String IP;

    public Node(String hostname, String IP){
        this.hostname = hostname;
        this.ID = hashCode();
        this.IP = IP;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getHostname() {
        return hostname;
    }

    public int getID() {
        return ID;
    }

    public String getIP() {
        return IP;
    }

    @Override
    public int hashCode() {
        long max = 2147483647;
        long min = -2147483647;

        double result = (hostname.hashCode()+max)*(327680d/(max+abs(min)));

        return (int) result;
    }
}
