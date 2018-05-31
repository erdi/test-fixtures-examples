package example

import com.google.inject.Guice
import com.google.inject.Injector
import example.config.Configuration
import example.dbbootstrap.DatabaseInitializer
import example.guice.DataBuildersApplicationModule
import example.remotecontrol.RemoteControlServlet
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import org.eclipse.jetty.servlet.ServletHolder

class DataBuildersApplication extends Application<Configuration> {
    @Override
    void run(Configuration configuration, Environment environment) throws Exception {
        def injector = initializeInjector(configuration)

        injector.getInstance(DatabaseInitializer).init()

        environment.jersey().register(injector.getInstance(PostResource))

        registerRemoteControlServlet(configuration, environment, injector)
    }

    private Injector initializeInjector(Configuration configuration) {
        Guice.createInjector(new DataBuildersApplicationModule(configuration))
    }

    private void registerRemoteControlServlet(Configuration configuration, Environment environment, Injector injector) {
        if (configuration.remoteControlEnabled) {
            def remoteControlServlet = new RemoteControlServlet(injector)
            environment.applicationContext.addServlet(new ServletHolder(remoteControlServlet), "/remote-control")
        }
    }
}
