package example.persistence

import static db.Tables.*

import db.tables.records.AuthorRecord
import db.tables.records.TagRecord
import example.model.Author
import example.model.Post
import example.model.Tag
import org.jooq.DSLContext

import javax.inject.Inject
import javax.inject.Provider

class PostRepository {

    private final Provider<DSLContext> dslContextProvider

    @Inject
    PostRepository(Provider<DSLContext> dslContextProvider) {
        this.dslContextProvider = dslContextProvider
    }

    List<Post> findByTag(String tag) {
        def records = dsl().selectFrom(POST.join(POST_TAG).onKey().join(TAG).onKey().join(AUTHOR).onKey())
            .where(TAG.VALUE.eq(tag))
            .fetch()

        records.collect {
            def postRecord = it.into(POST)
            def authorRecord = it.into(AUTHOR)

            Post.builder()
                .id(postRecord.id)
                .title(postRecord.title)
                .author(toAuthorEntity(authorRecord))
                .tags(findTagsForPostId(postRecord.id))
                .build()
        }
    }

    Set<Tag> findTagsForPostId(String postId) {
        dsl().selectFrom(POST_TAG.join(TAG).onKey())
            .where(POST_TAG.POST_ID.eq(postId))
            .fetch()
            .into(TAG)
            .map(this.&toTagEntity)
    }

    private Author toAuthorEntity(AuthorRecord record) {
        Author.builder()
            .id(record.id)
            .firstName(record.firstName)
            .lastName(record.lastName)
            .build()
    }

    private Tag toTagEntity(TagRecord record) {
        new Tag(record.id, record.value)
    }

    private DSLContext dsl() {
        dslContextProvider.get()
    }

}
