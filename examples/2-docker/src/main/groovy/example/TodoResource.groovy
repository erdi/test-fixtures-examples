package example

import static javax.ws.rs.core.MediaType.APPLICATION_JSON

import example.model.Todo
import example.persistence.TodoRepository

import javax.ws.rs.*
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

@Path("/todo")
@Produces(APPLICATION_JSON)
class TodoResource {

    private final TodoRepository todoRepository

    TodoResource(TodoRepository todoRepository) {
        this.todoRepository = todoRepository
    }

    @POST
    Response create(Todo todo) {
        def stored = todoRepository.add(todo)

        def uri = UriBuilder.fromResource(TodoResource).path(TodoResource, 'get').build(stored.id)
        Response.created(uri)
            .entity(stored)
            .build()
    }

    @GET
    @Path('{id}')
    Optional<Todo> get(@PathParam('id') String id) {
        todoRepository.findById(id)
    }

}
