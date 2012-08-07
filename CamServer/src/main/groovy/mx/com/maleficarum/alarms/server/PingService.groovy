package mx.com.maleficarum.alarms.server

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory

/**
 * Makes a ping to any given source in jalarms config
 * @author Oscar I. Hernandez Ventura
 */
class PingService implements GroovyInterceptable {

    def alarmSender
    def logger = LoggerFactory.getLogger(PingService.class)
    def interval

    Object invokeMethod(String methodName, args) {
        def executor = Executors.newSingleThreadScheduledExecutor()
        def task = new Runnable() {

            void run() {
                  alarmSender.sendAlarm("PING ${new Date()}", "PING")
            }
        }

        logger.info("Starting ping task every ${interval} seconds")
        executor.schedule(task, interval.toLong(), TimeUnit.SECONDS)

    }

}
