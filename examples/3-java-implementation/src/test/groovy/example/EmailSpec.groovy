package example

import static com.icegreen.greenmail.util.ServerSetup.PROTOCOL_SMTP
import static javax.ws.rs.core.MediaType.TEXT_PLAIN
import static org.simplejavamail.converter.EmailConverter.mimeMessageToEmail

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import example.config.Configuration
import example.config.SmtpConfiguration
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.ws.rs.client.Entity

class EmailSpec extends Specification {

    @AutoCleanup('stop')
    GreenMail greenMail = createGreenMailServer()

    Configuration configuration = new Configuration(
        smtp: new SmtpConfiguration(
            host: 'localhost',
            port: greenMail.smtp.port
        )
    )

    @Rule
    DropwizardAppRule<Configuration> applicationUnderTest = new DropwizardAppRule<>(EmailApplication, configuration)

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
        greenMail.receivedMessages.size() == 1

        and:
        with(mimeMessageToEmail(greenMail.receivedMessages[0])) {
            fromRecipient.address == 'bob@example.com'
            recipients*.address == [recipient]
            subject == 'Hello World!'
            plainText == 'This is a hello world email.'
        }

    }

    GreenMail createGreenMailServer() {
        def setup = new ServerSetup(0, 'localhost', PROTOCOL_SMTP)

        def server = new GreenMail(setup)
        server.start()
        server
    }

}
