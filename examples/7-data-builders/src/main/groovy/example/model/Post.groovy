package example.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.builder.Builder

@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false)
class Post implements Serializable {

    final String id
    final String title
    final Author author
    final Set<Tag> tags

    @Builder
    Post(
        @JsonProperty('id') String id,
        @JsonProperty('title') String title,
        @JsonProperty('author') Author author,
        @JsonProperty('tags') Set<Tag> tags
    ) {
        this.id = id
        this.title = title
        this.author = author
        this.tags = tags.asImmutable()
    }

}
