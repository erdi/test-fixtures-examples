package example.dbbootstrap

import com.zaxxer.hikari.HikariDataSource

class FileHsqlDbBootstrapper {

    static void main(String[] args) {
        HikariDataSource dataSource = createDataSource(args[0], args[1])
        new DatabaseInitializer(dataSource).init()
    }

    private static HikariDataSource createDataSource(String dbDirectoryPath, String dbName) {
        FileHsqlDbBootstrapper.classLoader.loadClass('org.hsqldb.jdbc.JDBCDriver')
        def dataSource = new HikariDataSource(
            jdbcUrl: "jdbc:hsqldb:file:$dbDirectoryPath/$dbName;shutdown=true;hsqldb.write_delay=false",
            username: 'sa',
            password: '',
            maximumPoolSize: 2,
            autoCommit: true
        )
        dataSource
    }

}
