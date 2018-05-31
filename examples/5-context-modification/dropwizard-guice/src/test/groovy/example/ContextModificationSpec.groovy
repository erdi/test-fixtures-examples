package example

import static javax.ws.rs.core.MediaType.TEXT_PLAIN

import example.config.DbConfiguration
import example.email.EmailSender
import groovy.sql.Sql
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.ws.rs.client.Entity

class ContextModificationSpec extends Specification {

    private final static String JDBC_URL = 'jdbc:hsqldb:mem:test'
    private final static String JDBC_DRIVER = 'org.hsqldb.jdbc.JDBCDriver'
    private final static String JDBC_USER = 'sa'
    private final static String JDBC_PASSWORD = ''

    @AutoCleanup
    ContextModificationApplicationUnderTest applicationUnderTest = new ContextModificationApplicationUnderTest().withConfig {
        db = new DbConfiguration(
            jdbcUrl: JDBC_URL,
            driverClassName: JDBC_DRIVER,
            username: JDBC_USER,
            password: JDBC_PASSWORD
        )
    }.withBindings {
        bind(EmailSender).to(NoopEmailSender)
    }

    def 'emails are sent to the address which is posted'() {
        given:
        def recipient = 'alice@example.com'

        when:
        applicationUnderTest.client()
            .target("http://localhost:$applicationUnderTest.localPort")
            .path("send-email/$recipient")
            .request()
            .post(Entity.entity(null, TEXT_PLAIN))

        then:
        addresseesInDb() == [recipient]
    }

    List<String> addresseesInDb() {
        def sql = Sql.newInstance(JDBC_URL, JDBC_USER, JDBC_PASSWORD, JDBC_DRIVER)
        def addresses = sql.rows('select ADDRESS from DEFAULT_SCHEMA.SENT_EMAIL')*.get('ADDRESS')
        sql.close()
        addresses
    }

}
