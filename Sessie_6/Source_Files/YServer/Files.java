package distributed.yproject.server;

import java.util.concurrent.ConcurrentHashMap;
import static java.lang.StrictMath.abs;

public class Files {

    private String filename;
    private int nodeID;
    private int hash;

    public Files(String filename, ConcurrentHashMap<Integer, String> nodes){
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
        int temp3 = 0;

        for (ConcurrentHashMap.Entry<Integer, String> entry : nodes.entrySet()) {
            int key = entry.getKey();
            //determine node with biggest hash
            if (key > temp2) {
                temp2 = key;
            }
            //determine nodes with hash smaller than file hash
            if (key < hash) {
                array[i] = key;
                i++;
            }
        }
        //determine node with smallest difference between it's hash and file hash
        for (int j = 0; j < array.length; j++) {
            if ((hash - array[j]) < temp){
                temp = (hash - array[j]);
                temp3 = array[j];
            }
        }
        //if no nodes are smaller, return biggest node
        if (temp3 == 0){
            return temp2;
        }
        else {
            return temp3;
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