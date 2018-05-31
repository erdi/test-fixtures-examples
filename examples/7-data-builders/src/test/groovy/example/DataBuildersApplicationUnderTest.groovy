package example

import static groovy.lang.Closure.DELEGATE_FIRST
import static io.dropwizard.util.Duration.seconds

import example.config.Configuration
import example.util.ClosureUtil
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.client.JerseyClientConfiguration
import io.dropwizard.testing.DropwizardTestSupport
import io.remotecontrol.transport.http.HttpTransport

import javax.ws.rs.client.Client
import java.util.function.Consumer

class DataBuildersApplicationUnderTest implements AutoCloseable {

    private DropwizardTestSupport<Configuration> dropwizardTestSupport

    private Client client

    private final List<Consumer<Configuration>> configSpecs = []

    DataBuildersApplicationUnderTest() {
        withConfig {
            remoteControlEnabled = true
        }
    }

    DataBuildersApplicationUnderTest withConfig(@DelegatesTo(value = Configuration, strategy = DELEGATE_FIRST) Closure<?> configSpec) {
        ensureNotStarted()
        configSpecs << (ClosureUtil.&runWithDelegateFirst.curry(configSpec) as Consumer<Configuration>)
        this
    }

    int getLocalPort() {
        ensureStarted()
        dropwizardTestSupport.localPort
    }

    @Override
    void close() throws Exception {
        client?.close()
        dropwizardTestSupport.after()
    }

    Client client() {
        ensureStarted()
        if (!client) {
            client = new JerseyClientBuilder(dropwizardTestSupport.environment)
                .using(new JerseyClientConfiguration(timeout: seconds(3)))
                .build("test client")
        }
        client
    }

    private Configuration buildConfiguration() {
        def configuration = new Configuration()
        configSpecs*.accept(configuration)
        configuration
    }

    private void ensureStarted() {
        if (!dropwizardTestSupport) {
            dropwizardTestSupport = new DropwizardTestSupport<>(DataBuildersApplication, buildConfiguration())
            dropwizardTestSupport.before()
        }
    }

    private void ensureNotStarted() {
        if (dropwizardTestSupport) {
            throw new RuntimeException('Application configuration cannot be modified after it has been started')
        }
    }

    RemoteControl remoteControl() {
        def remoteControlEndpoint = "http://localhost:$localPort/remote-control"
        def backing = new io.remotecontrol.groovy.client.RemoteControl(new HttpTransport(remoteControlEndpoint))
        new RemoteControl(backing)
    }
}
