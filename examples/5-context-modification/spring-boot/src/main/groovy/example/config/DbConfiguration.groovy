package example.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties('db')
class DbConfiguration {

    String driverClassName
    String jdbcUrl
    String username
    String password

}
