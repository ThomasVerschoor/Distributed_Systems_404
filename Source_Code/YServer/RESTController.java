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
    public String getIP(@PathVariable("ID") int id){
        System.out.println("A node requests ip from node: "+id);
        return NodeHandler.nodes.get(id);
    }

    @GetMapping("/getPreviousNode/{ID}")
    public String getPreviousNode(@PathVariable("ID") String id){
        System.out.println("A node requests previous node of node: "+id);
        return String.valueOf(NodeHandler.getPrevious(id));
    }
}