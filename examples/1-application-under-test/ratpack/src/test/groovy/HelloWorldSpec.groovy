import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Specification

class HelloWorldSpec extends Specification {

    @AutoCleanup
    GroovyRatpackMainApplicationUnderTest applicationUnderTest = new GroovyRatpackMainApplicationUnderTest()

    TestHttpClient httpClient = applicationUnderTest.httpClient

    def 'application responds with Hello world!'() {
        expect:
        httpClient.getText('/') == 'Hello World!'
    }

}
