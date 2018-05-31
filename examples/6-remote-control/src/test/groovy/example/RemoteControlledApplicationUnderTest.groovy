package example

import static groovy.lang.Closure.DELEGATE_FIRST
import static io.dropwizard.util.Duration.seconds

import example.config.Configuration
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.client.JerseyClientConfiguration
import io.dropwizard.testing.DropwizardTestSupport
import io.remotecontrol.transport.http.HttpTransport

import javax.ws.rs.client.Client
import java.util.function.Consumer

class RemoteControlledApplicationUnderTest implements AutoCloseable {

    private DropwizardTestSupport<Configuration> dropwizardTestSupport

    private Client client

    private final List<Consumer<Configuration>> configSpecs = []

    RemoteControlledApplicationUnderTest() {
        withConfig {
            remoteControlEnabled = true
        }
    }

    RemoteControlledApplicationUnderTest withConfig(@DelegatesTo(value = Configuration, strategy = DELEGATE_FIRST) Closure<?> configSpec) {
        ensureNotStarted()
        configSpecs << (this.&runWithDelegateFirst.curry(configSpec) as Consumer<Configuration>)
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
            dropwizardTestSupport = new DropwizardTestSupport<>(RemoteControlledApplication, buildConfiguration())
            dropwizardTestSupport.before()
        }
    }

    private void ensureNotStarted() {
        if (dropwizardTestSupport) {
            throw new RuntimeException('Application configuration cannot be modified after it has been started')
        }
    }

    private <T> T runWithDelegateFirst(Closure<T> self, Object delegate) {
        Closure<T> clone = (Closure<T>) self.clone()
        clone.delegate = delegate
        clone.resolveStrategy = DELEGATE_FIRST
        clone.call(delegate)
    }

    RemoteControl remoteControl() {
        def remoteControlEndpoint = "http://localhost:$localPort/remote-control"
        def backing = new io.remotecontrol.groovy.client.RemoteControl(new HttpTransport(remoteControlEndpoint))
        new RemoteControl(backing)
    }
}
