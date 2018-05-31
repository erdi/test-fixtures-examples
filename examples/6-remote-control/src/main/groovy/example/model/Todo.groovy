package example.model

import groovy.transform.Immutable

@Immutable(copyWith = true)
class Todo implements Serializable {

    String id
    String title
    boolean completed

}
