package example.model

import groovy.transform.Immutable

@Immutable(copyWith = true)
class Todo {

    String id
    String title
    boolean completed

}
