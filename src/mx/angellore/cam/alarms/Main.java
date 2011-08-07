/**
 * 
 */
package mx.angellore.cam.alarms;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.solab.alarms.AlarmSender;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : Main.java , v 1.0 26/07/2011 angellore $
 */
public class Main {

	private ApplicationContext ctx = null;
	private AlarmSender sender = null;
	private String hostname;

	public Main(String h) {
		ctx = new ClassPathXmlApplicationContext("mx/angellore/cam/alarms/applicationContext.xml");
		sender = (AlarmSender) ctx.getBean("alarmSender");
		hostname = h;
	}

	public void start() {

		try {
			InetSocketAddress addr = new InetSocketAddress(8080);
			HttpServer server = HttpServer.create(addr, 0);

			server.createContext("/alarm", new AlarmRequestHandler());
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class AlarmRequestHandler implements HttpHandler {
		
		public void handle(HttpExchange exchange) throws IOException {
			String requestMethod = exchange.getRequestMethod();
			if (requestMethod.equalsIgnoreCase("GET")) {
				Headers responseHeaders = exchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "text/plain");
				exchange.sendResponseHeaders(200, 0);

				URI requestedUri = exchange.getRequestURI();
				String query = requestedUri.getRawQuery();

				OutputStream responseBody = exchange.getResponseBody();

				Map<String, String> args = getQueryString(query);
				
				sender.sendAlarm(hostname, args.get("alarm"));
				
				responseBody.write("done".getBytes());

				responseBody.close();
			}
		}
		
		private Map<String, String> getQueryString(String qs) {
			Map<String, String> map = new HashMap<String, String>();
			String[] params = null;
			
			if(qs.indexOf("&") > 0) {
				params = qs.split("&");
			} else {
				params = new String[1];
				params[0] = qs;
			}
			
			for(String p : params) {
				if(p.indexOf("=") > 0) {
					String[] pair = p.split("=");
					map.put(pair[0], pair[1]);
				}
			}
			
			return map;
		}
	}

	public static void main(String[] args) {
		new Main("").start();
	}

}
