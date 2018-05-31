package example

import static javax.ws.rs.core.MediaType.APPLICATION_JSON

import example.model.Post
import example.persistence.PostRepository

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam

@Path('/post')
@Produces(APPLICATION_JSON)
class PostResource {

    private final PostRepository postRepository

    @Inject
    PostResource(PostRepository postRepository) {
        this.postRepository = postRepository
    }

    @GET
    List<Post> findPosts(@QueryParam('tag') String tag) {
        postRepository.findByTag(tag)
    }

}
