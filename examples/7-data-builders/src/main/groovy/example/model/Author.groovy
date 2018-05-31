package example.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.builder.Builder

@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false)
class Author implements Serializable {

    final String id
    final String firstName
    final String lastName

    @Builder
    Author(
        @JsonProperty('id') String id,
        @JsonProperty('firstName') String firstName,
        @JsonProperty('lastName') String lastName
    ) {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
    }
}
