package com.company;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
	// write your code here

        HashMap<Integer, String> nodes = new HashMap<Integer, String>();
        HashMap<Integer, String> file = new HashMap<Integer, String>();

        Node node1 = new Node("host1", "143.129.39.106");
        Node node2 = new Node("host2", "143.129.39.107");
        Node node3 = new Node("host3", "143.129.39.108");
        Node node4 = new Node("host4", "143.129.39.109");

        nodes.put(node1.getID(), node1.getIP());
        nodes.put(node2.getID(), node2.getIP());
        nodes.put(node3.getID(), node3.getIP());
        nodes.put(node4.getID(), node4.getIP());

        System.out.println(nodes);
        System.out.println(nodes.get(16266));

        nodes.remove(16267);

        System.out.println(nodes);
    }
}
