package example.remotecontrol

import com.google.inject.Injector

class RemoteControlDelegate {

    private final Injector injector

    RemoteControlDelegate(Injector injector) {
        this.injector = injector
    }

    public <T> T get(Class<T> type) {
        injector.getInstance(type)
    }

}
