package example

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

import com.github.tomakehurst.wiremock.WireMockServer
import example.RestApiApplication
import example.config.Configuration
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import spock.lang.AutoCleanup
import spock.lang.Specification

class RestApiSpec extends Specification {

    @AutoCleanup('stop')
    WireMockServer wireMock = createWireMockServer()

    Configuration configuration = new Configuration(
        greetingServiceUrl: "http://localhost:${wireMock.port()}"
    )

    @Rule
    DropwizardAppRule<Configuration> applicationUnderTest = new DropwizardAppRule<>(RestApiApplication, configuration)

    def "greeting is retrieved from the remote service"() {
        given:
        def greeting = 'Hello World!'

        and:
        wireMock.stubFor(
            get(urlEqualTo('/greet'))
                .willReturn(aResponse().withBody(greeting))
        )

        when:
        def response = applicationUnderTest.client()
            .target("http://localhost:$applicationUnderTest.localPort")
            .path("greet-using-remote-service")
            .request()
            .get()

        then:
        response.readEntity(String) == greeting
    }

    WireMockServer createWireMockServer() {
        def wireMockServer = new WireMockServer(wireMockConfig().dynamicPort())
        wireMockServer.start()
        wireMockServer
    }

}
