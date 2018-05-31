package example.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false)
class Tag implements Serializable{

    final String id
    final String value

    Tag(@JsonProperty('id') String id, @JsonProperty('value') String value) {
        this.id = id
        this.value = value
    }
}
