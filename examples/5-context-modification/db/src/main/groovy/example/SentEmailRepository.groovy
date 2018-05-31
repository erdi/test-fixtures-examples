package example

import static db.Tables.SENT_EMAIL

import org.jooq.DSLContext

import javax.inject.Inject
import javax.inject.Provider

class SentEmailRepository {

    private final Provider<DSLContext> dslContextProvider

    @Inject
    SentEmailRepository(Provider<DSLContext> dslContextProvider) {
        this.dslContextProvider = dslContextProvider
    }

    void add(String address) {
        def id = UUID.randomUUID().toString()

        dsl().insertInto(SENT_EMAIL)
            .set(SENT_EMAIL.ID, id)
            .set(SENT_EMAIL.ADDRESS, address)
            .execute()
    }

    DSLContext dsl() {
        dslContextProvider.get()
    }

}
