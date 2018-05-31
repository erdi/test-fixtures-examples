package example.email

import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.Mailer

import javax.inject.Inject

class SimpleJavaMailEmailSender implements EmailSender {

    private final Mailer mailer

    @Inject
    SimpleJavaMailEmailSender(Mailer mailer) {
        this.mailer = mailer
    }

    @Override
    void sendTo(String address) {
        def email = EmailBuilder.startingBlank()
            .from('bob@example.com')
            .to(address)
            .withSubject('Hello World!')
            .withPlainText('This is a hello world email.')
            .buildEmail()

        mailer.sendMail(email)
    }

}
