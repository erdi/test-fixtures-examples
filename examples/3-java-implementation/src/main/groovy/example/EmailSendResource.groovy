package example

import example.email.EmailSender

import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response

@Path('/send-email')
class EmailSendResource {

    private final EmailSender emailSender

    EmailSendResource(EmailSender emailSender) {
        this.emailSender = emailSender
    }

    @POST
    @Path('{address}')
    Response send(@PathParam('address') String address) {
        emailSender.sendTo(address)
    }

}
