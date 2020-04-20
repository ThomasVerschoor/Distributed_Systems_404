import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Date;

// The Java class will be hosted at the URI path "/helloworld"
/*@Path("/helloworld/{name}")
public class HelloBilal {
    @PathParam("name")
    private String name;

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("text/plain")
    public String getClichedMessage() {
        // Return some cliched textual content
        return "Hello "+name;
    }
}*/
@Path("/helloworld")
public class HelloBilal {

    @GET
    public String doGreet() {
        return "Hello Stranger, the time is " + new Date();
    }


    @Path("/{name}")
    @GET
    public String doSayHello(@PathParam("name") String name) {
        return "Hello there " + name;
    }
}