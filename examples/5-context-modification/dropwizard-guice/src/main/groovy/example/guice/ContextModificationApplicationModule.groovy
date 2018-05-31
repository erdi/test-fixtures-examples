package example.guice

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides
import com.google.inject.Singleton
import com.zaxxer.hikari.HikariDataSource
import example.config.Configuration
import example.email.EmailSender
import example.email.SimpleJavaMailEmailSender
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.simplejavamail.mailer.Mailer
import org.simplejavamail.mailer.MailerBuilder

import javax.sql.DataSource

class ContextModificationApplicationModule implements Module {

    private final Configuration configuration

    ContextModificationApplicationModule(Configuration configuration) {
        this.configuration = configuration
    }

    @Override
    void configure(Binder binder) {
        binder.bind(EmailSender).to(SimpleJavaMailEmailSender)
    }

    @Provides
    @Singleton
    DataSource dataSource() {
        new HikariDataSource(
            jdbcUrl: configuration.db.jdbcUrl,
            username: configuration.db.username,
            password: configuration.db.password,
            autoCommit: true
        )
    }

    @Provides
    DSLContext dslContext(DataSource dataSource) {
        DSL.using(new DefaultConfiguration().set(dataSource))
    }

    @Provides
    Mailer mailer() {
        MailerBuilder.withSMTPServer(configuration.smtp.host, configuration.smtp.port).buildMailer()
    }
}
