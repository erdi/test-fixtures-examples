package example

import static db.Tables.SENT_EMAIL
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

import example.email.EmailSender
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = [
    'db.driverClassName=org.hsqldb.jdbc.JDBCDriver',
    'db.jdbcUrl=jdbc:hsqldb:mem:test',
    'db.username=sa',
    'db.password='
])
class ContextModificationSpec extends Specification {

    @Autowired
    TestRestTemplate httpClient

    @Autowired
    DSLContext dsl

    def 'sending email writes addressee email to the db'() {
        given:
        def addressee = 'alice@example.com'

        when:
        httpClient.postForLocation("/send-email/$addressee", null)

        then:
        addresseesInDb() == [addressee]
    }

    List<String> addresseesInDb() {
        dsl.selectFrom(SENT_EMAIL).fetch(SENT_EMAIL.ADDRESS)
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        @Primary
        EmailSender noopEmailSender() {
            new NoopEmailSender()
        }
    }

}
