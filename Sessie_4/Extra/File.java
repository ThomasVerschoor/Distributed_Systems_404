package com.company;

import java.util.HashMap;

import static java.lang.StrictMath.abs;

public class File {

    private String filename;
    private int nodeID;
    private int hash;

    public File(String filename, HashMap<Integer, String> nodes){
        this.filename = filename;
        this.hash = hashCode();
        this.nodeID = setNodeID(nodes);
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int setNodeID(HashMap<Integer, String> nodes) {
        int array[] = new int[nodes.size()];
        int i = 0;
        int temp = 1000000;
        int temp2 = 0;

        for (HashMap.Entry<Integer, String> entry : nodes.entrySet()) {

            //System.out.println("hash: " +hash);
            //System.out.println("key: " +entry.getKey());

            temp2 = entry.getKey();

            if (entry.getKey() < hash) {
                array[i] = entry.getKey();
                //System.out.println("array:" +i+ " " +array[i]);
                i++;
            }
        }

            for (int j = 0; j < array.length; j++) {
                if (hash - array[j] < temp){
                    temp = array[j];
                    //System.out.println("temp: " +temp);
                }
            }

            if (temp == 0){
                return temp2;
            }
            else {
                return temp;
            }
    }

    public String getFilename() {
        return filename;
    }

    public int getHash() {
        return hash;
    }

    public int getNodeID() {
        return nodeID;
    }

    @Override
    public int hashCode(){
        long max = 2147483647;
        long min = -2147483647;

        double result = (filename.hashCode()+max)*(327680d/(max+abs(min)));

        return (int) result;
    }
}
