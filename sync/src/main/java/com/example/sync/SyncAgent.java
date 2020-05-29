package com.example.sync;

import jade.core.Agent;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.StrictMath.abs;
public class SyncAgent extends Agent {
    private static Logger logger;
    private ArrayList<String> list=new ArrayList<>();
    public void setList(){
        try (Stream<Path> walk = Files.walk(Paths.get("C:\\nodeFiles"))) {
            List<String> result = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            //add file to arraylist
            list.addAll(result);
            System.out.println(list);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void setup() {
        System.out.println("Hello world! I'm an agent!");
        System.out.println("My local name is " + getAID().getLocalName());
        System.out.println("My GUID is " + getAID().getName());
        System.out.println("My addresses are " + String.join(",", getAID().getAddressesArray()));
        setList();
        int hostHash=hashCode("bilal");
        Iterator it = list.iterator();
        try {
            String maps= sendGET("getMap", "127.0.0.1");
            System.out.println(maps);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(it.hasNext()){
            String fileName=it.next().toString();
           if(hashCode(fileName)>hostHash){
               System.out.println(fileName+" is in the correct node");
           }else{
               System.out.println(fileName+" is in the wrong node");
           }
        }
    }

    private static String sendGET(String command, String address) throws IOException {

       // logger.log(Level.INFO,"[MAIN] Sending HTTP GET to YServer: "+"http://" + address + ":420/" + command);
        URL url = new URL("http://" + address + ":420/" + command);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
            return response.toString();
        } else {
            System.out.println("GET request failed");
            return "GET request failed";
        }
    }
    public static int hashCode(String name) {
        long max = 2147483647;
        long min = -2147483647;

        double result = (name.hashCode() + max) * (327680d / (max + abs(min)));

        return (int) result;
    }
}
