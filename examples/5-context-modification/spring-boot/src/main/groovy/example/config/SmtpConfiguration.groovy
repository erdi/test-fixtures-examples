package example.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties('smtp')
class SmtpConfiguration {

    String host
    int port

}
