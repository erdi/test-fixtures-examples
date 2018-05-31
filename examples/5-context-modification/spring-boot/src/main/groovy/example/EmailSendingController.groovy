package example

import example.email.EmailSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping('/send-email/{address}')
class EmailSendingController {

    private final EmailSender emailSender
    private final SentEmailRepository sentEmailRepository

    @Autowired
    EmailSendingController(EmailSender emailSender, SentEmailRepository sentEmailRepository) {
        this.emailSender = emailSender
        this.sentEmailRepository = sentEmailRepository
    }

    @PostMapping
    ResponseEntity<?> sendEmail(@PathVariable('address') String address) {
        emailSender.sendTo(address)
        sentEmailRepository.add(address)
        ResponseEntity.noContent().build()
    }

}
