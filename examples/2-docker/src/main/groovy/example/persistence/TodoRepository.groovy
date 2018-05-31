package example.persistence

import static db.Tables.TODO

import example.model.Todo

class TodoRepository {

    @Delegate
    private final DslContextFactory dslContextFactory

    TodoRepository(DslContextFactory dslContextFactory) {
        this.dslContextFactory = dslContextFactory
    }

    Todo add(Todo todo) {
        def id = UUID.randomUUID().toString()

        dsl().insertInto(TODO)
            .set(TODO.ID, id)
            .set(TODO.TITLE, todo.title)
            .set(TODO.COMPLETED, todo.completed)
            .execute()

        todo.copyWith(id: id)
    }

    Optional<Todo> findById(String id) {
        def record = dsl().selectFrom(TODO)
            .where(TODO.ID.eq(id))
            .fetchOne()

        Optional.ofNullable(record).map {
            new Todo(id: it.id, title: it.title, completed: it.completed)
        }
    }

}
