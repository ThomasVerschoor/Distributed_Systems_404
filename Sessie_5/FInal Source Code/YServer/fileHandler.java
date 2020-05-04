package distributed.yserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class fileHandler {

    public static ConcurrentHashMap<Integer, File> files = new ConcurrentHashMap<Integer, File>();

    public static void addFile(String name, ConcurrentHashMap<Integer, String> nodes){
        System.out.println("Adding file to files hashMap:"+name);
        File temp = new File(name,nodes);
        files.put(temp.getHash(),temp);
        System.out.println("Id generated: "+temp.getHash());
    }

    public static void removeFile(String name){
        System.out.println("Removing "+name+" from files hashMap");
        for (Map.Entry<Integer, File> entry : files.entrySet()) {
            if (name.equals(entry.getValue().getFilename())) {
                files.remove(entry.getKey());
            }
        }
    }

    public static String getIP (String name,ConcurrentHashMap<Integer, String> nodes){
        int id = -1;
        for (Map.Entry<Integer, File> entry : files.entrySet()) {
            if (name.equals(entry.getValue().getFilename())) {
                id = entry.getValue().getNodeID();
            }
        }
        if (id == -1){
            return "Something went wrong at getIP in fileHandler";
        }else
            return nodes.get(id);
    }

    public static String getHash(String name){
        int hash = -1;
        for (Map.Entry<Integer, File> entry : files.entrySet()) {
            if (name.equals(entry.getValue().getFilename())) {
                hash = entry.getValue().getHash();
            }
        }
        if (hash == -1)
            return "Something went wrong at getHash in fileHandler";
        else
            return String.valueOf(hash);
    }
}
