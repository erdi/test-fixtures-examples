package example

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification

@SpringBootTest(webEnvironment = RANDOM_PORT)
class HelloWorldSpec extends Specification {

    @Autowired
    TestRestTemplate httpClient

    def 'application responds with Hello world!'() {
        when:
        def response = httpClient.getForEntity('/', String)

        then:
        response.body == 'Hello World!'
    }

}
