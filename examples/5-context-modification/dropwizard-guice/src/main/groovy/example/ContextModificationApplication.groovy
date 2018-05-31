package example

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.util.Modules
import example.config.Configuration
import example.dbbootstrap.DatabaseInitializer
import example.guice.ContextModificationApplicationModule
import io.dropwizard.Application
import io.dropwizard.setup.Environment

class ContextModificationApplication extends Application<Configuration> {

    @Override
    void run(Configuration configuration, Environment environment) throws Exception {
        def injector = injector(configuration)

        initDb(configuration, injector)

        environment.jersey().register(injector.getInstance(EmailSendingResource))
    }

    static void main(String[] args) {
        new ContextModificationApplication().run(args)
    }

    private void initDb(Configuration configuration, Injector injector) {
        getClass().classLoader.loadClass(configuration.db.driverClassName)
        injector.getInstance(DatabaseInitializer).init()
    }

    private Injector injector(Configuration configuration) {
        def applicationModule = new ContextModificationApplicationModule(configuration)

        def module = configuration.overridingModules.inject(applicationModule) { Module effective, Module override ->
            Modules.override(effective).with(override)
        }

        Guice.createInjector(module)
    }

}
