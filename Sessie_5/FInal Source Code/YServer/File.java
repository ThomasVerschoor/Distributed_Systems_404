package distributed.yserver;

import java.util.concurrent.ConcurrentHashMap;

import static java.lang.StrictMath.abs;

public class File {

    private String filename;
    private int nodeID;
    private int hash;

    public File(String filename, ConcurrentHashMap<Integer, String> nodes){
        this.filename = filename;
        this.hash = hashCode(); //key van file Hashmap
        this.nodeID = setNodeID(nodes); //Key van nodes hashMap
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int setNodeID(ConcurrentHashMap<Integer, String> nodes) {
        int array[] = new int[nodes.size()];
        int i = 0;
        int temp = 1000000;
        int temp2 = 0;

        for (ConcurrentHashMap.Entry<Integer, String> entry : nodes.entrySet()) {
            int key = entry.getKey();
            if (key > temp2) {
                temp2 = entry.getKey();
            }
            if (key < hash) {
                array[i] = entry.getKey();
                i++;
            }
        }

        for (int j = 0; j < array.length; j++) {
            if (hash - array[j] < temp){
                temp = array[j];
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