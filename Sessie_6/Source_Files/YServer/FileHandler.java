package distributed.yproject.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileHandler {

    public static ConcurrentHashMap<Integer, Files> files = new ConcurrentHashMap<Integer, Files>();

    public static void addFile(String name,int nodeHash, ConcurrentHashMap<Integer, String> nodes){
        Files temp = new Files(name,nodeHash,nodes);
        files.put(temp.getHash(),temp);
        System.out.println("Adding file to files hashMap: "+name+" with ID: "+temp.getHash());
    }

    public static void removeFile(String name){
        System.out.println("Removing "+name+" from files hashMap");
        for (Map.Entry<Integer, Files> entry : files.entrySet()) {
            if (name.equals(entry.getValue().getFilename())) {
                files.remove(entry.getKey());
            }
        }
    }

    public static String getIP (String name,ConcurrentHashMap<Integer, String> nodes){
        int id = -1;
        for (Map.Entry<Integer, Files> entry : files.entrySet()) {
            if (name.equals(entry.getValue().getFilename())) {
                id = entry.getValue().getReplicationID();
            }
        }
        if (id == -1){
            return "Something went wrong at getIP in fileHandler";
        }else
            return nodes.get(id);
    }

    public static String getHash(String name){
        int hash = -1;
        for (Map.Entry<Integer, Files> entry : files.entrySet()) {
            if (name.equals(entry.getValue().getFilename())) {
                hash = entry.getValue().getHash();
            }
        }
        if (hash == -1)
            return "Something went wrong at getHash in fileHandler";
        else
            return String.valueOf(hash);
    }

    public static int getReplicationID(String name){
        int id = -1;
        for (Map.Entry<Integer, Files> entry : files.entrySet()) {
            if (name.equals(entry.getValue().getFilename())) {
                id = entry.getValue().getReplicationID();
            }
        }
        return id;
    }
}