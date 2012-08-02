package mx.com.maleficarum.alarms.server

import java.net.ServerSocket
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

/**
 * Created with IntelliJ IDEA.
 * User: angellore
 * Date: 31/07/12
 * Time: 10:00
 * To change this template use File | Settings | File Templates.
 */
class CamHTTPSever implements GroovyInterceptable {

    def server
    def runnable
    def port
    def logger = LoggerFactory.getLogger(CamHTTPSever.class)
    def ctx
    def sender

    Object invokeMethod(String methodName, ApplicationContext args) {
        ctx = args
        sender = ctx.getBean("alarmSender")
        server = new ServerSocket(port?.toInteger())

        runnable = new Runnable() {

            void run() {
                while(true) {
                    try {
                        server.accept() { socket ->
                                socket.withStreams { input, output ->
                                    def reader = input.newReader()
                                    def buffer = reader.readLine()

                                    if(buffer =~ /alarma/) {
                                        def a = buffer.split("=")[1].split("HTTP")[0]
                                        logger.info("Sending alarma ${a}")
                                        sender.sendAlarm(a,"FATAL")
                                    }

                                    output << "ok\n"
                                }
                            socket.close()
                        }
                    } catch(Exception e) {
                         e.printStackTrace()
                    }
                }
            }

        }

        logger.info("Started http server on port ${port}")
        new Thread(runnable).start()

        null
    }
}
