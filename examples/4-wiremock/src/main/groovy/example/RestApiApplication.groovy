package example

import example.config.Configuration
import io.dropwizard.Application
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.setup.Environment

class RestApiApplication extends Application<Configuration> {

    @Override
    void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(createRemoteServiceGreetingResource(configuration, environment))
    }

    static void main(String[] args) {
        new RestApiApplication().run(args)
    }

    private RemoteServiceGreetingResource createRemoteServiceGreetingResource(Configuration configuration,
                                                                              Environment environment) {
        def client = new JerseyClientBuilder(environment).build('Greeting service')

        new RemoteServiceGreetingResource(configuration.greetingServiceUrl, client)
    }

}
