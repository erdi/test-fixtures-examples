package example.data

import static db.Tables.AUTHOR
import static example.util.ClosureUtil.runWithDelegateFirst
import static groovy.lang.Closure.DELEGATE_FIRST
import static java.util.UUID.randomUUID

import example.DataBuildersApplicationUnderTest
import example.model.Author
import org.jooq.DSLContext

class PersistingAuthorBuilder {

    private final DataBuildersApplicationUnderTest applicationUnderTest

    PersistingAuthorBuilder(DataBuildersApplicationUnderTest applicationUnderTest) {
        this.applicationUnderTest = applicationUnderTest
    }

    Author author(@DelegatesTo(value = PersistingAuthorBuilderDelegate, strategy = DELEGATE_FIRST) Closure<?> specification) {
        def delegate = new PersistingAuthorBuilderDelegate()
        runWithDelegateFirst(specification, delegate)

        def author = delegate.author.build()
        persist(author)
        author
    }

    private void persist(Author author) {
        applicationUnderTest.remoteControl().exec {
            get(DSLContext).insertInto(AUTHOR)
                .set(AUTHOR.ID, author.id)
                .set(AUTHOR.FIRST_NAME, author.firstName)
                .set(AUTHOR.LAST_NAME, author.lastName)
                .execute()
        }
    }

    static class PersistingAuthorBuilderDelegate {
        @Delegate
        private final Author.AuthorBuilder author = Author.builder()
            .id(randomUUID().toString())
    }
}
