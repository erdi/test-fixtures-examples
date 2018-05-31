package example.util

import static groovy.lang.Closure.DELEGATE_FIRST

class ClosureUtil {

    static <T> T runWithDelegateFirst(Closure<T> self, Object delegate) {
        Closure<T> clone = (Closure<T>) self.clone()
        clone.delegate = delegate
        clone.resolveStrategy = DELEGATE_FIRST
        clone.call(delegate)
    }

}
