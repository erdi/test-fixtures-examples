import io.dropwizard.Configuration
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import spock.lang.Specification

class HelloWorldSpec extends Specification {

    @Rule
    DropwizardAppRule<Configuration> applicationUnderTest = new DropwizardAppRule<>(HelloWorldApplication, randomPortConfiguration())

    def 'application responds with Hello world!'() {
        when:
        def response = applicationUnderTest.client()
            .target("http://localhost:${applicationUnderTest.localPort}/")
            .request()
            .get()

        then:
        response.readEntity(String) == 'Hello World!'
    }

    Configuration randomPortConfiguration() {
        def configuration = new Configuration()
        configuration.serverFactory.applicationConnectors.first().port = 0
        configuration.serverFactory.adminConnectors.first().port = 0
        configuration
    }

}
