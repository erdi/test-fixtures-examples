package example

import com.zaxxer.hikari.HikariDataSource
import example.config.Configuration
import example.config.DbConfiguration
import example.dbbootstrap.DatabaseInitializer
import example.persistence.DslContextFactory
import example.persistence.TodoRepository
import io.dropwizard.Application
import io.dropwizard.setup.Environment

import javax.sql.DataSource

class TodoApplication extends Application<Configuration> {

    @Override
    void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(createTodoResource(configuration.db))
    }

    static void main(String[] args) {
        new TodoApplication().run(args)
    }

    private TodoResource createTodoResource(DbConfiguration dbConfiguration) {
        def dataSource = createDataSource(dbConfiguration)
        initializeDb(dataSource)

        def dslContextFactory = new DslContextFactory(dataSource)
        def repository = new TodoRepository(dslContextFactory)

        new TodoResource(repository)
    }

    private void initializeDb(DataSource dataSource) {
        new DatabaseInitializer(dataSource).init()
    }

    private DataSource createDataSource(DbConfiguration dbConfiguration) {
        getClass().classLoader.loadClass(dbConfiguration.driverClassName)
        new HikariDataSource(
            jdbcUrl: dbConfiguration.jdbcUrl,
            username: dbConfiguration.username,
            password: dbConfiguration.password,
            autoCommit: true
        )
    }

}
