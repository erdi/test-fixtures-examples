package example

import static db.Tables.*

import org.jooq.DSLContext

class DatabaseCleaner implements AutoCloseable {

    private final DataBuildersApplicationUnderTest remoteControlledApplicationUnderTest

    DatabaseCleaner(DataBuildersApplicationUnderTest remoteControlledApplicationUnderTest) {
        this.remoteControlledApplicationUnderTest = remoteControlledApplicationUnderTest
    }

    @Override
    void close() throws Exception {
        remoteControlledApplicationUnderTest.remoteControl().exec {
            def context = get(DSLContext)
            [POST_TAG, POST, TAG, AUTHOR].each {
                context.deleteFrom(it).execute()
            }
        }
    }
}
