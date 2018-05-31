package example

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.client.Client

@Path('/greet-using-remote-service')
class RemoteServiceGreetingResource {

    private final String greetingServiceUrl
    private final Client client

    RemoteServiceGreetingResource(String greetingServiceUrl, Client client) {
        this.greetingServiceUrl = greetingServiceUrl
        this.client = client
    }

    @GET
    String greet() {
        client.target(greetingServiceUrl)
            .path('greet')
            .request()
            .get()
            .readEntity(String)
    }

}
