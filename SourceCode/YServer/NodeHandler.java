package distributed.yproject.server;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NodeHandler {

    public static ConcurrentHashMap<Integer, String> nodes = new ConcurrentHashMap<Integer, String>();

    public static void updateXML() {
        System.out.println("Updating XML file");
        XML.main(nodes);
    }

    public static void addNode(String name, String ip) {
        System.out.println("Adding node with <name | IP>: "+"<"+name+" | "+ip+">");
        Node temp = new Node(name, ip);
        System.out.println("ID chosen for "+name+": "+temp.getID());
        nodes.put(temp.getID(), temp.getIP());
        System.out.println("Map: "+nodes);
        updateXML();
    }

    public static void removeNode(String ip) {
        System.out.println("Removing "+ip+" from hashMap");
        for (Map.Entry<Integer, String> entry : nodes.entrySet()) {
            if (ip.equals(entry.getValue())) {
                nodes.remove(entry.getKey());
            }
        }
        updateXML();
    }

    public static boolean checkNode(String ip){
        System.out.println("Checking if "+ip+" is already part of the active users");
        Iterator<Map.Entry<Integer, String> > iterator = nodes.entrySet().iterator();
        boolean isPresent = false;
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if (ip.equals(entry.getValue())) {
                isPresent = true;
            }
        }
        return isPresent;
    }

    public static String getKey(String ip){
        int key = -1;
        Iterator<Map.Entry<Integer, String> > iterator = nodes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if (ip.equals(entry.getValue())) {
                key = entry.getKey();
            }
        }
        if (key == -1)
            return "Something went wrong at getKey in nodeHandler";
        else
            return String.valueOf(key);
    }

    public static String getKeyWithName(String name){
        int key = -1;
        Iterator<Map.Entry<Integer, String> > iterator = nodes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if (name.equals(entry.getValue())) {
                key = entry.getKey();
            }
        }
        if (key == -1)
            return "Something went wrong at getKey in nodeHandler";
        else
            return String.valueOf(key);
    }

    public static String getPrevious(String key) {
        int temp = -2147483647;
        int check = Integer.parseInt(key);
        Iterator<Map.Entry<Integer, String> > iterator = nodes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if (check > entry.getKey())
                if (temp < entry.getKey())
                    temp = entry.getKey();
        }
        if (temp == -2147483647){
            temp = NodeHandler.getBiggest();
        }
        return String.valueOf(temp);
    }

    private static int getBiggest() {
        int temp = -2147483647;
        Iterator<Map.Entry<Integer, String>> iterator = nodes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if (entry.getKey() > temp)
                temp = entry.getKey();
        }
        return temp;
    }

    public static String getNext(String key) {
        int temp = 2147483647;
        int check = Integer.parseInt(key);
        Iterator<Map.Entry<Integer, String> > iterator = nodes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if (check < entry.getKey())
                if (temp > entry.getKey())
                    temp = entry.getKey();
        }
        if (temp == 2147483647)
            temp = NodeHandler.getSmallest();
        return String.valueOf(temp);
    }

    private static int getSmallest() {
        int temp = 2147483647;
        Iterator<Map.Entry<Integer, String>> iterator = nodes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if (entry.getKey() < temp)
                temp = entry.getKey();
        }

        return temp;
    }

    public static String getIP(String id) {
        int key = Integer.parseInt(id);
        return nodes.get(key);
    }
}