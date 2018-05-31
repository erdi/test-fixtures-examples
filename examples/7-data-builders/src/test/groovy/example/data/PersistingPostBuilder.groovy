package example.data

import static db.Tables.*
import static example.util.ClosureUtil.runWithDelegateFirst
import static groovy.lang.Closure.DELEGATE_FIRST
import static java.util.UUID.randomUUID

import example.DataBuildersApplicationUnderTest
import example.model.Post
import example.model.Tag
import org.jooq.DSLContext

class PersistingPostBuilder {

    private final DataBuildersApplicationUnderTest applicationUnderTest

    PersistingPostBuilder(DataBuildersApplicationUnderTest applicationUnderTest) {
        this.applicationUnderTest = applicationUnderTest
    }

    Post post(@DelegatesTo(value = PersistingPostBuilderDelegate, strategy = DELEGATE_FIRST) Closure<?> specification) {
        def delegate = new PersistingPostBuilderDelegate(applicationUnderTest)
        runWithDelegateFirst(specification, delegate)

        def post = delegate.post.build()
        persist(post)
        post
    }

    private void persist(Post post) {
        applicationUnderTest.remoteControl().exec {
            def dsl = get(DSLContext)

            dsl.insertInto(POST)
                .set(POST.ID, post.id)
                .set(POST.TITLE, post.title)
                .set(POST.AUTHOR_ID, post.author.id)
                .execute()

            def tagsInsert = dsl.insertQuery(POST_TAG)
            post.tags.each { tag ->
                tagsInsert.newRecord()
                tagsInsert.addValue(POST_TAG.ID, randomUUID().toString())
                tagsInsert.addValue(POST_TAG.POST_ID, post.id)
                tagsInsert.addValue(POST_TAG.TAG_ID, tag.id)
            }
            tagsInsert.execute()
        }
    }

    static class PersistingPostBuilderDelegate {
        @Delegate
        private final Post.PostBuilder post = Post.builder()
            .id(randomUUID().toString())
            .tags([] as Set)

        private final DataBuildersApplicationUnderTest applicationUnderTest

        PersistingPostBuilderDelegate(DataBuildersApplicationUnderTest applicationUnderTest) {
            this.applicationUnderTest = applicationUnderTest
        }

        void tags(String... tags) {
            post.tags(tags.collect(this.&findOrCreateTag).toSet())
        }

        void author(@DelegatesTo(value = PersistingAuthorBuilder.PersistingAuthorBuilderDelegate, strategy = DELEGATE_FIRST) Closure<?> specification) {
            def builtAuthor = new PersistingAuthorBuilder(applicationUnderTest).author(specification)
            post.author(builtAuthor)
        }

        private Tag findOrCreateTag(String tag) {
            applicationUnderTest.remoteControl().exec {
                def dsl = get(DSLContext)

                def id = dsl.selectFrom(TAG)
                    .where(TAG.VALUE.eq(tag))
                    .fetchOne(TAG.ID)

                if (!id) {
                    id = randomUUID().toString()
                    dsl.insertInto(TAG)
                        .set(TAG.ID, id)
                        .set(TAG.VALUE, tag)
                        .execute()
                }

                new Tag(id, tag)
            }
        }
    }

}
