package example

import example.SentEmailRepository
import example.email.EmailSender

import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response

@Path('/send-email')
class EmailSendingResource {

    private final SentEmailRepository sentEmailRepository
    private final EmailSender emailSender

    @Inject
    EmailSendingResource(SentEmailRepository sentEmailRepository, EmailSender emailSender) {
        this.sentEmailRepository = sentEmailRepository
        this.emailSender = emailSender
    }

    @POST
    @Path('{address}')
    Response send(@PathParam('address') String address) {
        emailSender.sendTo(address)
        sentEmailRepository.add(address)
    }
}
