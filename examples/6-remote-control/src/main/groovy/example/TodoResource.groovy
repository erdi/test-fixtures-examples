package example

import static javax.ws.rs.core.MediaType.APPLICATION_JSON

import example.model.Todo
import example.persistence.TodoRepository

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("/todo")
@Produces(APPLICATION_JSON)
class TodoResource {

    private final TodoRepository todoRepository

    @Inject
    TodoResource(TodoRepository todoRepository) {
        this.todoRepository = todoRepository
    }

    @GET
    @Path('{id}')
    Optional<Todo> get(@PathParam('id') String id) {
        todoRepository.findById(id)
    }

}
