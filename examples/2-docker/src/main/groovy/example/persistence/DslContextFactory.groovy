package example.persistence

import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration

import javax.sql.DataSource

class DslContextFactory {

    private final DataSource dataSource

    DslContextFactory(DataSource dataSource) {
        this.dataSource = dataSource
    }

    DSLContext dsl() {
        DSL.using(new DefaultConfiguration().set(dataSource))
    }

}
