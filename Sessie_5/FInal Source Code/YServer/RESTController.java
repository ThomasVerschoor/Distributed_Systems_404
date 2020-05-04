package distributed.yserver;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.util.concurrent.Semaphore;


@RestController
public class RESTController {

    Semaphore addFile = new Semaphore(1);

    @PutMapping("/addFile/{fileName}")
    public String addFile(@PathVariable("fileName")String fileName) throws InterruptedException {
        addFile.acquire();
        fileHandler.addFile(fileName,nodeHandler.nodes);
        addFile.release();
        return "File "+fileName+" succesfully added with Hash: "+fileHandler.getHash(fileName);
    }

    @GetMapping("/getMap")
    public String getMap(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        System.out.println("Someone requests nodes hashMap ");
        return "Current active users: "+String.valueOf(nodeHandler.nodes);
    }

    @GetMapping("/getFileMap")
    public String getFileMap(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        System.out.println("Someone requests files hashMap ");
        return "Current active files: "+String.valueOf(fileHandler.files);
    }


    @GetMapping("/getFileOwner/{fileName}")
    public String getFileOwner(@PathVariable("fileName")String fileName){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        System.out.println("Someone requests file ownership of file: "+fileName);
        return"file located at IP: "+fileHandler.getIP(fileName,nodeHandler.nodes);
    }
}