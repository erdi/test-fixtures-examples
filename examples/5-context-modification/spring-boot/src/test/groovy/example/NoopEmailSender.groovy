package example

import example.email.EmailSender
import groovy.util.logging.Slf4j

@Slf4j
class NoopEmailSender implements EmailSender {

    @Override
    void sendTo(String address) {
        log.info("Not sending email to $address")
    }

}
