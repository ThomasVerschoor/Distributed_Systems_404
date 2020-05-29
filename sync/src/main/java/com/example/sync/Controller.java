package com.example.sync;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.instrument.Instrumentation;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
public class Controller {

    ArrayList<String> list = new ArrayList<>();
    Semaphore addUser = new Semaphore(1);
    Semaphore exit = new Semaphore(1);
    Semaphore addFile = new Semaphore(1);
    boolean check=false;

    //methode met agent
    @PostConstruct
    public void startAgent() throws IOException, StaleProxyException{
     //   while (true) {
     //       if (check) {
                Runtime rt = Runtime.instance();
                Profile p = new ProfileImpl();
                p.setParameter(Profile.MAIN_HOST, "localhost");
                ContainerController cc = rt.createMainContainer(p);
                AgentController ac;
                ac = cc.createNewAgent("SyncAgent", "com.example.sync.SyncAgent", null);
                ac.start();
                ac.suspend();
                check = false;
     //       }
     //   }
    }

    @PutMapping("/addFile/{fileName}")
    public String addFile(@PathVariable("fileName")String fileName) throws InterruptedException {
        addFile.acquire();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        list.add(fileName);
        addFile.release();
        return "File ";
    }

    @PutMapping("/setCheck/")
    public void setCheck() {
        check=true;
    }



    @GetMapping("/getFiles")
    public String getMap(){
        System.out.println("getting files");
        //HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        //System.out.println(nodeHandler.getKey(request.getRemoteAddr())+" requests nodes hashMap ");

        return "Current active users: ";
    }


}