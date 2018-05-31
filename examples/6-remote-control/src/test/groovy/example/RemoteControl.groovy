package example

import static groovy.lang.Closure.DELEGATE_FIRST

import example.remotecontrol.RemoteControlDelegate

class RemoteControl {

    private final io.remotecontrol.groovy.client.RemoteControl backing

    RemoteControl(io.remotecontrol.groovy.client.RemoteControl backing) {
        this.backing = backing
    }

    public <T> T exec(@DelegatesTo(value = RemoteControlDelegate, strategy = DELEGATE_FIRST) Closure<T> command) {
        backing.exec(command)
    }

}
