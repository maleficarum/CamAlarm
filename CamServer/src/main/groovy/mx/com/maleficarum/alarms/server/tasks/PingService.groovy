package mx.com.maleficarum.alarms.server.tasks

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import groovyx.net.http.HTTPBuilder

/**
 * Makes a ping to any given source in jalarms config
 * @author Oscar I. Hernandez Ventura
 */
class PingService implements GroovyInterceptable {

    def alarmSender
    def logger = LoggerFactory.getLogger(PingService.class)
    def interval
    def http

    def PingService(url) {
        http = new HTTPBuilder(url)
        logger.info("Connecting to ${url}")
    }

    Object invokeMethod(String methodName, args) {
        def executor = Executors.newSingleThreadScheduledExecutor()

        def closure = {
            http.request(GET,TEXT) { req ->
                uri.path = '/pingCam'
                uri.query = [ camname: args["hostname"]]

                response.success = { resp, reader ->

                }
                http.handler.failure = { resp ->
                    println "Unexpected failure: ${resp.statusLine}"
                }
            }
        }

        def task = new Runnable() {

            void run() {
                alarmSender.sendAlarm("PING ${new Date()}", "PING")
                closure.call()
            }
        }

        logger.info("Starting ping task every ${interval} seconds")
        executor.schedule(task, interval.toLong(), TimeUnit.SECONDS)

    }

}
