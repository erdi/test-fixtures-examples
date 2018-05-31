package example.guice

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides
import com.google.inject.Singleton
import com.zaxxer.hikari.HikariDataSource
import example.config.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration

import javax.sql.DataSource

class RemoteControlledApplicationModule implements Module {

    private final Configuration configuration

    RemoteControlledApplicationModule(Configuration configuration) {
        this.configuration = configuration
    }

    @Override
    void configure(Binder binder) {
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
}
