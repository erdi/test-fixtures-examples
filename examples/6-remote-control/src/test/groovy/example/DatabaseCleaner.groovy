package example

import static db.Tables.TODO

import org.jooq.DSLContext

class DatabaseCleaner implements AutoCloseable {

    private final RemoteControlledApplicationUnderTest remoteControlledApplicationUnderTest

    DatabaseCleaner(RemoteControlledApplicationUnderTest remoteControlledApplicationUnderTest) {
        this.remoteControlledApplicationUnderTest = remoteControlledApplicationUnderTest
    }

    @Override
    void close() throws Exception {
        remoteControlledApplicationUnderTest.remoteControl().exec {
            get(DSLContext).deleteFrom(TODO).execute()
        }
    }
}
