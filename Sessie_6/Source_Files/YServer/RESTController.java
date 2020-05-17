package distributed.yproject.server;

import org.springframework.web.bind.annotation.*;

@RestController
public class RESTController {

    @GetMapping("/getMap")
    public String getMap(){
        System.out.println("Someone requests nodes hashMap ");
        return "Current active users: "+NodeHandler.nodes;
    }
}