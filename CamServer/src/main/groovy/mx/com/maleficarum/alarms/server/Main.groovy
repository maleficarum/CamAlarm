package mx.com.maleficarum.alarms.server

import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Date: 30/07/12 22:46
 * @author Oscar I. Hernandez
 */
class Main {

    def ctx

    public Main(hostname) {
        ctx = new ClassPathXmlApplicationContext(["spring-config.xml"] as String[])
        def interceptables = ctx.getBeansOfType(GroovyInterceptable.class)

        interceptables.each { k, v ->
            v.invokeMethod(hostname, ctx)
        }

        ctx.getBean("alarmSender").sendAlarm("Prueba", "PING")
    }

    static void main(args) {
        new Main(args.length == 0 ? "Dev" : args[0])
    }

}
