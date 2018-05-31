package example

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @RequestMapping("/")
    String index() {
        'Hello World!'
    }
}
