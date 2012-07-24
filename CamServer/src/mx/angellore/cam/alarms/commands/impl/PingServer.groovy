package mx.angellore.cam.alarms.commands.impl

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 *
 * @author Oscar I. Hernandez
 */
class PingServer {

    def executor = Executors.newSingleThreadScheduledExecutor();

    def PingServer(closure) {
        def pingTask = new Runnable() {

            void run() {
                println("CORRIENDO PINGER")
                closure.call()
            }
        }

        executor.scheduleWithFixedDelay(pingTask, 6, 6, TimeUnit.HOURS);
    }

}
