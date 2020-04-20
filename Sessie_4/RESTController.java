package distributed.yserver;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Semaphore;


@RestController
public class RESTController {

    Semaphore addUser = new Semaphore(1);
    Semaphore exit = new Semaphore(1);
    Semaphore addFile = new Semaphore(1);

    @RequestMapping("/exit")
    public String exit() throws InterruptedException {
        exit.acquire();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if(!hashTest(request.getRemoteAddr())){
            exit.release();
            return "New user detected!\nTo add yourself as a user use the following command: /addUser/<name>\n";
        }
        nodeHandler.removeNode(request.getRemoteAddr());
        exit.release();
        return "User succesfully deleted";
    }

    @PutMapping("/addUser/{name}")
    public String addUser(@PathVariable("name")String name) throws InterruptedException {
        addUser.acquire();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if(hashTest(request.getRemoteAddr())){
            addUser.release();
            return "You already are active, no need to add yourself again.";
        }
        nodeHandler.addNode(name,request.getRemoteAddr());
        addUser.release();
        return "User succesfully added, you can use all REST functionality now";
    }

    @PutMapping("/addFile/{fileName}")
    public String addFile(@PathVariable("fileName")String fileName) throws InterruptedException {
        addFile.acquire();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if(!hashTest(request.getRemoteAddr())){
            addFile.release();
            return "New user detected!\nTo add yourself as a user use the following command: /addUser/<name>\n";
        }
        fileHandler.addFile(fileName,nodeHandler.nodes);
        addFile.release();
        return "File "+fileName+" succesfully added with Hash: "+fileHandler.getHash(fileName);
    }

    @GetMapping("/getMap")
    public String getMap(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        System.out.println(nodeHandler.getKey(request.getRemoteAddr())+" requests nodes hashMap ");
        if(!hashTest(request.getRemoteAddr())){
            return "New user detected!\nTo add yourself as a user use the following command: /addUser/<name>\n";
        }
        return "Current active users: "+String.valueOf(nodeHandler.nodes);
    }

    @GetMapping("/getFileMap")
    public String getFileMap(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        System.out.println(nodeHandler.getKey(request.getRemoteAddr())+" requests files hashMap ");
        if(!hashTest(request.getRemoteAddr())){
            return "New user detected!\nTo add yourself as a user use the following command: /addUser/<name>\n";
        }
        return "Current active files: "+String.valueOf(fileHandler.files);
    }

    @GetMapping("/getFileOwner/{fileName}")
    public String getFileOwner(@PathVariable("fileName")String fileName){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        System.out.println(nodeHandler.getKey(request.getRemoteAddr())+" requests file ownership of file: "+fileName);
        if(!hashTest(request.getRemoteAddr())){
            return "New user detected!\nTo add yourself as a user use the following command: /addUser/<name>\n";
        }
        return"file located at IP: "+fileHandler.getIP(fileName,nodeHandler.nodes);
    }

    private boolean hashTest(String ip) {
        boolean testPassed = false;
        if(nodeHandler.checkNode(ip))  //returns true when already in hashMap
            testPassed = true;
        return testPassed;
    }
}