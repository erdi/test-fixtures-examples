package example

import com.zaxxer.hikari.HikariDataSource
import example.config.DbConfiguration
import example.config.SmtpConfiguration
import example.dbbootstrap.DatabaseInitializer
import example.email.SimpleJavaMailEmailSender
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.simplejavamail.mailer.Mailer
import org.simplejavamail.mailer.MailerBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.context.annotation.Scope
import org.springframework.core.type.AnnotationMetadata

import javax.sql.DataSource

@SpringBootApplication
@EnableAutoConfiguration(exclude = [LiquibaseAutoConfiguration, JooqAutoConfiguration])
@Import(ContextModificationApplicationBeanDefinitionRegistrar)
class ContextModificationApplication {

    static void main(String[] args) {
        SpringApplication.run(ContextModificationApplication, args)
    }

    @Bean
    DataSource dataSource(DbConfiguration dbConfiguration) {
        getClass().classLoader.loadClass(dbConfiguration.driverClassName)
        new HikariDataSource(
            jdbcUrl: dbConfiguration.jdbcUrl,
            username: dbConfiguration.username,
            password: dbConfiguration.password,
            autoCommit: true
        )
    }

    @Bean
    @Scope('prototype')
    DSLContext dslContext(DataSource dataSource) {
        DSL.using(new DefaultConfiguration().set(dataSource))
    }

    @Bean
    Mailer mailer(SmtpConfiguration smtpConfiguration) {
        MailerBuilder.withSMTPServer(smtpConfiguration.host, smtpConfiguration.port).buildMailer()
    }

    static class ContextModificationApplicationBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

        @Override
        void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            registerBeanDefinition(registry, SentEmailRepository)
            registerBeanDefinition(registry, DatabaseInitializer)
            registerBeanDefinition(registry, SimpleJavaMailEmailSender)
        }

        private registerBeanDefinition(BeanDefinitionRegistry registry, Class type) {
            def definition = new GenericBeanDefinition(beanClass: type)
            registry.registerBeanDefinition(type.simpleName.uncapitalize(), definition)
        }

    }
}
