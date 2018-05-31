package example

import static db.Tables.TODO
import static java.util.UUID.randomUUID
import static javax.ws.rs.core.Response.Status.OK

import example.config.DbConfiguration
import example.model.Todo
import org.jooq.DSLContext
import spock.lang.AutoCleanup
import spock.lang.Specification

class TodoSpec extends Specification {

    private final static String JDBC_URL = 'jdbc:hsqldb:mem:test'
    private final static String JDBC_DRIVER = 'org.hsqldb.jdbc.JDBCDriver'
    private final static String JDBC_USER = 'sa'
    private final static String JDBC_PASSWORD = ''

    @AutoCleanup
    RemoteControlledApplicationUnderTest applicationUnderTest = new RemoteControlledApplicationUnderTest().withConfig {
        db = new DbConfiguration(
            jdbcUrl: JDBC_URL,
            driverClassName: JDBC_DRIVER,
            username: JDBC_USER,
            password: JDBC_PASSWORD
        )
    }

    @AutoCleanup
    DatabaseCleaner databaseCleaner = new DatabaseCleaner(applicationUnderTest)

    def 'can retrieve todos via api which are stored in the db'() {
        given:
        def todo = new Todo(id: randomUUID(), title: 'buy milk')

        and:
        applicationUnderTest.remoteControl().exec {
            get(DSLContext).insertInto(TODO)
                .set(TODO.ID, todo.id)
                .set(TODO.TITLE, todo.title)
                .set(TODO.COMPLETED, todo.completed)
                .execute()
        }

        when:
        def getResponse = applicationUnderTest.client()
            .target("http://localhost:$applicationUnderTest.localPort")
            .path("todo/$todo.id")
            .request()
            .get()

        then:
        getResponse.statusInfo == OK
        getResponse.readEntity(Todo) == todo
    }

}
