package example

import example.config.DbConfiguration
import example.data.PersistingPostBuilder
import example.model.Post
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.ws.rs.core.GenericType

class DataBuildersSpec extends Specification {

    private final static String JDBC_URL = 'jdbc:hsqldb:mem:test'
    private final static String JDBC_DRIVER = 'org.hsqldb.jdbc.JDBCDriver'
    private final static String JDBC_USER = 'sa'
    private final static String JDBC_PASSWORD = ''

    @AutoCleanup
    DataBuildersApplicationUnderTest applicationUnderTest = new DataBuildersApplicationUnderTest().withConfig {
        db = new DbConfiguration(
            jdbcUrl: JDBC_URL,
            driverClassName: JDBC_DRIVER,
            username: JDBC_USER,
            password: JDBC_PASSWORD
        )
    }

    @AutoCleanup
    DatabaseCleaner databaseCleaner = new DatabaseCleaner(applicationUnderTest)

    @Delegate(parameterAnnotations = true)
    PersistingPostBuilder postBuilder = new PersistingPostBuilder(applicationUnderTest)

    def 'can retrieve posts via api which are bootstrapped in the db using builders'() {
        given:
        def groovyPost = post {
            title('Groovy is GR8')
            tags('groovy')
            author {
                firstName('Marcin')
                lastName('Erdmann')
            }
        }

        def gradlePost = post {
            title('Build happiness')
            tags('groovy', 'gradle')
            author {
                firstName('Luke')
                lastName('Daley')
            }
        }

        expect:
        requestPostsTaggedWith('gradle') == [gradlePost]

        when:
        def groovyPosts = requestPostsTaggedWith('groovy')

        then:
        groovyPosts.size() == 2
        groovyPosts.containsAll([groovyPost, gradlePost])
    }

    List<Post> requestPostsTaggedWith(String tag) {
        applicationUnderTest.client()
            .target("http://localhost:$applicationUnderTest.localPort")
            .path('post')
            .queryParam('tag', tag)
            .request()
            .get()
            .readEntity(new GenericType<List<Post>>() {})
    }

}
