package mx.angellore.cam.alarms.timers

import mx.angellore.cam.alarms.commands.ICommand
import org.slf4j.LoggerFactory
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.TEXT

/**
 * Created with IntelliJ IDEA.
 * User: angellore
 * Date: 23/07/12
 * Time: 21:35
 * To change this template use File | Settings | File Templates.
 */
class ChangeStatusCommand  implements GroovyInterceptable {

    def logger = LoggerFactory.getLogger(getClass())
    def http

    def ChangeStatusCommand(clientUrl) {
        http = new HTTPBuilder(clientUrl)
        logger.info("Connecting to ${clientUrl}")
    }

    def invokeMethod(String method, args) {
        /*if(args.length >= 1) {
            if(args[0] == "STARTUP") {
                logger.info("Notificando inicio de aplicacion")

                def closure = {
                    http.request(GET,TEXT) { req ->
                        uri.path = '/pingCam'
                        uri.query = [ camname: args.length == 2 ? args[1] : "unknow"]

                        response.success = { resp, reader ->

                        }
                        http.handler.failure = { resp ->
                            println "Unexpected failure: ${resp.statusLine}"
                        }
                    }
                }

                closure.call()
                new PingServer(closure)
            }*/
        null
    }
}
