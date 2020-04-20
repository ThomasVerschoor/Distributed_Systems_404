package com.company;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
	// write your code here

        HashMap<Integer, String> nodes = new HashMap<Integer, String>();

        //make new node
        Node node1 = new Node("bilal", "143.129.39.106");
        Node node2 = new Node("mathijs", "143.129.39.107");
        Node node3 = new Node("liam", "143.129.39.108");
        Node node4 = new Node("thomas", "143.129.39.109");
        Node node5 = new Node("jorre", "143.129.39.111");

        //add node to map
        nodes.put(node1.getID(), node1.getIP());
        nodes.put(node2.getID(), node2.getIP());
        nodes.put(node3.getID(), node3.getIP());
        nodes.put(node4.getID(), node4.getIP());
        nodes.put(node5.getID(), node5.getIP());

        //make XML-file
        XML.main(nodes);

        //make file
        File file1 = new File("foto1.jpg", nodes);
        File file2 = new File("foto22.jpg", nodes);
        File file3 = new File("foto300.jpg", nodes);
        File file4 = new File("foto400.jpg", nodes);

        System.out.println("Het bestand staat op node " +file1.getNodeID()+ " met IP-adres " +nodes.get(file1.getNodeID()));
        System.out.println("Het bestand staat op node " +file2.getNodeID()+ " met IP-adres " +nodes.get(file2.getNodeID()));
        System.out.println("Het bestand staat op node " +file3.getNodeID()+ " met IP-adres " +nodes.get(file3.getNodeID()));
        System.out.println("Het bestand staat op node " +file4.getNodeID()+ " met IP-adres " +nodes.get(file4.getNodeID()));

        System.out.println(nodes);

    }
}
