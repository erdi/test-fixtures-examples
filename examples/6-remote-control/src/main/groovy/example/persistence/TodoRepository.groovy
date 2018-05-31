package example.persistence

import static db.Tables.TODO

import example.model.Todo
import org.jooq.DSLContext

import javax.inject.Inject
import javax.inject.Provider

class TodoRepository {

    private final Provider<DSLContext> dslContextProvider

    @Inject
    TodoRepository(Provider<DSLContext> dslContextProvider) {
        this.dslContextProvider = dslContextProvider
    }

    Optional<Todo> findById(String id) {
        def record = dsl().selectFrom(TODO)
            .where(TODO.ID.eq(id))
            .fetchOne()

        Optional.ofNullable(record).map {
            new Todo(id: it.id, title: it.title, completed: it.completed)
        }
    }

    private DSLContext dsl() {
        dslContextProvider.get()
    }
}
