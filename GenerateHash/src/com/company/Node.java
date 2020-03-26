package com.company;

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
        int hash = hostname.hashCode();

        if (hash > 0) {
            return (int) (hostname.hashCode() % 32768);
        }
        else {
            return (int) (hostname.hashCode() % 32768 * (-1));
        }
    }
}
