package example.remotecontrol

import com.google.inject.Injector
import io.remotecontrol.groovy.server.ClosureCommandRunner
import io.remotecontrol.result.impl.DefaultResultFactory
import io.remotecontrol.server.MultiTypeReceiver
import io.remotecontrol.server.Receiver

class RemoteControlServlet extends io.remotecontrol.transport.http.RemoteControlServlet {

    private final Injector injector

    RemoteControlServlet(Injector injector) {
        this.injector = injector
    }

    @Override
    protected Receiver createReceiver() {
        def loader = getClass().classLoader
        def contextFactory = { chain -> new RemoteControlDelegate(injector) }
        def runner = new ClosureCommandRunner(loader, contextFactory, new DefaultResultFactory())
        new MultiTypeReceiver(loader, runner)
    }

}
