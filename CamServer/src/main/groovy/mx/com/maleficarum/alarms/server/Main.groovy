package mx.com.maleficarum.alarms.server

import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Date: 30/07/12 22:46
 * @author Oscar I. Hernandez
 */
class Main {

    def ctx
    def hostname

    public Main(h) {
        hostname = h
        ctx = new ClassPathXmlApplicationContext(["spring-config.xml"] as String[])
        def interceptables = ctx.getBeansOfType(GroovyInterceptable.class)

        interceptables.each { k, v ->
            try {
                v.invokeMethod(hostname, ["context":ctx, "hostname":hostname])
            } catch(Exception e) {
                ctx.getBean("alarmSender").sendAlarm("Sistema iniciado ${hostname}", "ERROR     AX")
            }
        }

        ctx.getBean("alarmSender").sendAlarm("Sistema iniciado ${hostname}", "PING")
    }

    static void main(args) {
        new Main(args.length == 0 ? "Dev" : args[0])
    }

}
