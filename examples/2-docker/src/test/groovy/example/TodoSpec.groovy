package example

import static io.dropwizard.util.Duration.seconds
import static java.util.UUID.randomUUID
import static javax.ws.rs.core.Response.Status.*

import example.TodoApplication
import example.config.Configuration
import example.config.DbConfiguration
import example.model.Todo
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.client.JerseyClientConfiguration
import io.dropwizard.testing.DropwizardTestSupport
import org.junit.ClassRule
import org.testcontainers.containers.MSSQLServerContainer
import org.testcontainers.containers.MSSQLServerContainerProvider
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation

class TodoSpec extends Specification {

    @Shared
    @ClassRule
    MSSQLServerContainer mssqlServerContainer = new MSSQLServerContainerProvider().newInstance()

    @Shared
    @AutoCleanup('after')
    DropwizardTestSupport<Configuration> applicationUnderTest

    @Shared
    Client client

    def setupSpec() {
        def configuration = new Configuration(
            db: new DbConfiguration(
                driverClassName: mssqlServerContainer.driverClassName,
                jdbcUrl: mssqlServerContainer.jdbcUrl,
                username: mssqlServerContainer.username,
                password: mssqlServerContainer.password,
            )
        )
        applicationUnderTest = new DropwizardTestSupport<>(TodoApplication, configuration)
        applicationUnderTest.before()
        client = new JerseyClientBuilder(applicationUnderTest.environment)
            .using(new JerseyClientConfiguration(timeout: seconds(10)))
            .build("test client")
    }

    def 'not found status is returned for non-existing todos'() {
        when:
        def response = path("todo/${randomUUID()}").get()

        then:
        response.statusInfo == NOT_FOUND
    }

    def 'can create and retrieve todos'() {
        given:
        def todo = new Todo(title: 'buy milk')

        when:
        def createResponse = path("todo")
            .post(Entity.json(todo))

        then:
        createResponse.statusInfo == CREATED

        when:
        def getResponse = client.target(createResponse.location).request().get()

        then:
        getResponse.statusInfo == OK
        getResponse.readEntity(Todo) == todo.copyWith(id: createResponse.readEntity(Todo).id)
    }

    Invocation.Builder path(String path) {
        client.target("http://localhost:${applicationUnderTest.localPort}/$path").request()
    }

}
