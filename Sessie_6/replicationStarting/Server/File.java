import java.util.HashMap;

import static java.lang.StrictMath.abs;

public class File {

    private String filename;
    private int hash;
    private int nodeID;

    public File(String filename, HashMap<Integer, String> nodes){
        this.filename = filename;
        this.hash = hashCode();
        this.nodeID = setNodeID(nodes);
    }

    public int setNodeID(HashMap<Integer, String> nodes) {
        int array[] = new int[nodes.size()];
        int i = 0;
        int temp = 1000000;
        int temp2 = 0;
        int temp3 = 0;

        for (HashMap.Entry<Integer, String> entry : nodes.entrySet()) {
            int key = entry.getKey();

            //System.out.println("hash: " +hash);
            //System.out.println("key: " +key);

            //determine node with biggest hash
            if (key > temp2) {
                temp2 = key;
            }

            //determine nodes with hash smaller than file hash
            if (key < hash) {
                array[i] = key;
                //System.out.println("array:" +i+ " " +array[i]);
                i++;
            }
            //System.out.println(" ");
        }

        //determine node with smallest difference between it's hash and file hash
        for (int j = 0; j < array.length; j++) {
            //System.out.println("element: " +j);
            //System.out.println("value: " +array[j]);
            if ((hash - array[j]) < temp){
                //System.out.println("array length: " +array.length);
                //System.out.println("diff: " +(hash - array[j]));
                temp = (hash - array[j]);
                temp3 = array[j];
                //System.out.println("temp: " +temp3);
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

    @Override
    public int hashCode(){
        long max = 2147483647;
        long min = -2147483647;

        double result = (filename.hashCode()+max)*(327680d/(max+abs(min)));

        return (int) result;
    }

    public int getNodeID() {
        return nodeID;
    }

    public int getHash() {
        return hash;
    }

}
