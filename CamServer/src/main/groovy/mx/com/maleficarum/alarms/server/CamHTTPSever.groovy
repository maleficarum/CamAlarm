package mx.com.maleficarum.alarms.server

import java.net.ServerSocket
import org.slf4j.LoggerFactory

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

    Object invokeMethod(String methodName, args) {
        server = new ServerSocket(port?.toInteger())
        runnable = new Runnable() {

            void run() {
                while(true) {
                    server.accept() { socket ->
                            socket.withStreams { input, output ->
                                try {
                                    input.eachLine() { line ->
                                        println line
                                    }
                                } catch (GroovyRuntimeException b) { println(b) }
                                output.withWriter { writer ->
                                    writer << "HTTP/1.1 200 OK\n"
                                    writer << "Content-Type: text/html\n\n"
                                    writer << "Hello World!"
                                }
                                output.flush()
                            }
                        socket.close()
                    }
                }
            }

        }

        logger.info("Started http server on port ${port}")
        new Thread(runnable).start()

        null
    }
}
