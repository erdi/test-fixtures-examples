package example.dbbootstrap

import liquibase.Contexts
import liquibase.Liquibase
import liquibase.database.Database
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

import javax.inject.Inject
import javax.sql.DataSource

class DatabaseInitializer {

    private final DataSource dataSource

    @Inject
    DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource
    }

    void init() {
        Database database
        try {
            database = createDatabase()
            Liquibase liquibase = new Liquibase('master-changelog.yaml', new ClassLoaderResourceAccessor(), database)
            liquibase.update(new Contexts())
        } finally {
            database?.close()
        }
    }

    private Database createDatabase() {
        DatabaseFactory.instance.findCorrectDatabaseImplementation(createJdbcConnection())
    }

    private JdbcConnection createJdbcConnection() {
        new JdbcConnection(dataSource.connection)
    }

}
