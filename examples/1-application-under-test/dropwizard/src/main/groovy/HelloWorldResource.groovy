import static javax.ws.rs.core.MediaType.TEXT_PLAIN

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/")
@Produces(TEXT_PLAIN)
class HelloWorldResource {

    @GET
    String sayHello() {
        'Hello World!'
    }

}
