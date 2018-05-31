package example

import com.google.inject.Guice
import com.google.inject.Injector
import example.config.Configuration
import example.dbbootstrap.DatabaseInitializer
import example.guice.RemoteControlledApplicationModule
import example.remotecontrol.RemoteControlServlet
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import org.eclipse.jetty.servlet.ServletHolder

class RemoteControlledApplication extends Application<Configuration> {
    @Override
    void run(Configuration configuration, Environment environment) throws Exception {
        def injector = initializeInjector(configuration)

        injector.getInstance(DatabaseInitializer).init()

        environment.jersey().register(injector.getInstance(TodoResource))

        registerRemoteControlServlet(configuration, environment, injector)
    }

    static void main(String[] args) {
        new RemoteControlledApplication().run(args)
    }

    private Injector initializeInjector(Configuration configuration) {
        Guice.createInjector(new RemoteControlledApplicationModule(configuration))
    }

    private void registerRemoteControlServlet(Configuration configuration, Environment environment, Injector injector) {
        if (configuration.remoteControlEnabled) {
            def remoteControlServlet = new RemoteControlServlet(injector)
            environment.applicationContext.addServlet(new ServletHolder(remoteControlServlet), "/remote-control")
        }
    }
}
