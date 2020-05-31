package distributed.yproject.server;

import org.springframework.web.bind.annotation.*;

@RestController
public class RESTController {

    @GetMapping("/getMap")
    public String getMap(){
        System.out.println("Someone requests nodes hashMap ");
        return "Current active users: "+NodeHandler.nodes;
    }

    @GetMapping("/getIP/{ID}")
    public String getIP(@PathVariable("ID") String id){
        System.out.println("A node requests ip from node: "+id);
        //return NodeHandler.nodes.get(id);
        System.out.println("Ip of node " +id+ " is " +NodeHandler.getIP(id));
        return NodeHandler.getIP(id);
    }

    @GetMapping("/getPreviousNode/{ID}")
    public String getPreviousNode(@PathVariable("ID") String id){
        System.out.println("A node requests previous node of node: "+id);
        System.out.println("Previous node of " +id+ " is " +NodeHandler.getPrevious(id));
        return String.valueOf(NodeHandler.getPrevious(id));
    }
}