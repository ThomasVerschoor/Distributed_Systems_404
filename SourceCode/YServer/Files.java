package distributed.yproject.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import static java.lang.StrictMath.abs;

public class Files {

    private String filename;
    private final int replicationID;
    private final int hash;
    private final int nodeHash;

    public Files(String filename,int nodeHash, ConcurrentHashMap<Integer, String> nodes){
        this.filename = filename;
        this.hash = hashCode(); //key van file Hashmap
        this.nodeHash = nodeHash;   //key van owner
        this.replicationID = setNodeID(nodes); //Key van replication node voor hashMap
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int setNodeID(ConcurrentHashMap<Integer, String> nodes) {
        ArrayList<Integer> array = new ArrayList<Integer>();
        ArrayList<Integer> array2 = new ArrayList<>();
        int temp2 = 0;
        int temp3 = 0;

        for (ConcurrentHashMap.Entry<Integer, String> entry : nodes.entrySet()) {
            int key = entry.getKey();

            array2.add(key);

            //determine node with biggest hash
            /*if (key > temp2) {
                temp2 = key;
            }*/
            //determine nodes with hash smaller than file hash
            if (key < hash) {
                array.add(key);
            }
        }
        //Sort array
        Collections.sort(array);
        Collections.sort(array2);

        //biggest hash
        temp2 = array2.get(array2.size()-1);
        //second biggest hash
        temp3 = array2.get(array2.size()-2);

        //return right ID
        if (array.size() == 0 && temp2 != nodeHash)
            return temp2;
        else if (array.size() == 0 && temp2 == nodeHash) {
            return temp3;
        }
        else {
            temp3 = array.get(array.size()-1);
            if (array.size() == 1 && temp3 == nodeHash)
                return temp2;
            else if (temp3 == nodeHash)
                return array.get(array.size()-2);
            else
                return temp3;
        }
    }

    public String getFilename() {
        return filename;
    }

    public int getHash() {
        return hash;
    }

    public int getReplicationID() {
        return replicationID;
    }

    @Override
    public int hashCode(){
        long max = 2147483647;
        long min = -2147483647;
        double result = (filename.hashCode()+max)*(327680d/(max+abs(min)));
        return (int) result;
    }
}