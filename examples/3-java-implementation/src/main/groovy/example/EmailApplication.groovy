package example

import example.config.Configuration
import example.config.SmtpConfiguration
import example.email.SimpleJavaMailEmailSender
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import org.simplejavamail.mailer.MailerBuilder

class EmailApplication extends Application<Configuration> {
    @Override
    void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(createEmailSendResource(configuration.smtp))
    }

    private EmailSendResource createEmailSendResource(SmtpConfiguration smtpConfiguration) {
        def mailer = MailerBuilder.withSMTPServer(smtpConfiguration.host, smtpConfiguration.port).buildMailer()

        new EmailSendResource(new SimpleJavaMailEmailSender(mailer))
    }
}
