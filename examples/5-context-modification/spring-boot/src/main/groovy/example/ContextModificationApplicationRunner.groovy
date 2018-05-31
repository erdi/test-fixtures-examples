package example

import example.dbbootstrap.DatabaseInitializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class ContextModificationApplicationRunner implements ApplicationRunner {

    private final DatabaseInitializer databaseInitializer

    @Autowired
    ContextModificationApplicationRunner(DatabaseInitializer databaseInitializer) {
        this.databaseInitializer = databaseInitializer
    }

    @Override
    void run(ApplicationArguments args) throws Exception {
        databaseInitializer.init()
    }
}
