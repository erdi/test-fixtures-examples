import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment

class HelloWorldApplication extends Application<Configuration> {

    @Override
    void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(HelloWorldResource)
    }

    static void main(String[] args) {
        new HelloWorldApplication().run(args)
    }
}
