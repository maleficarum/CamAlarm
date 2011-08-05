/**
 * 
 */
package mx.angellore.cam.alarms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.solab.alarms.AlarmSender;

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : Main.java , v 1.0 26/07/2011 angellore $
 */
public class Main {

	private static final byte[] RESPONSE = "HTTP/1.1 200 OK\r\nContent-Length: 4\r\nConnection: close\r\nContent-Type: text/plain\r\n\r\nDONE".getBytes();
	private ApplicationContext ctx = null;
	private AlarmSender sender = null;
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private String hostname;

	public Main(String h) {
		ctx = new ClassPathXmlApplicationContext("mx/angellore/cam/alarms/applicationContext.xml");
		sender = (AlarmSender) ctx.getBean("alarmSender");
		hostname = h;
	}

	public void start() {

		try {
			ServerSocket server = new ServerSocket(9090);
			String cmd = "";

			while(!cmd.toUpperCase().equals("EXIT")) {
				Socket socket = server.accept();

				executor.execute(new Reader(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class Reader implements Runnable {

		private Socket s ;
		
		public Reader(Socket s) {
			this.s = s;
		}

		public void run() {

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
				
                String linea = reader.readLine();
                
                if (linea.startsWith("POST")) {
                        int largo = 0;
                        while (linea.length() > 0) {
                                linea = reader.readLine();
                                if (linea.startsWith("Content-Length: ")) {
                                        largo = Integer.parseInt(linea.substring(16));
                                }
                        }
                        if (largo > 0) {
                                char[] buf = new char[largo];
                                reader.read(buf);
                                linea = new String(buf);
                        } else {
                                linea = reader.readLine();
                        }
                } else if (linea.startsWith("GET")) {
                        int pos = linea.indexOf('?');
                        int p2 = linea.lastIndexOf(" HTTP/");
                        if (pos > 0 && p2 > 20) {
                                linea = linea.substring(pos + 1, p2);
                        } else {
                                linea = null;
                        }
                } else {
                        linea = null;
                }

                s.getOutputStream().write(RESPONSE);
                s.getOutputStream().flush();
                s.close();		
                
                sender.sendAlarm(hostname, linea);
				
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}

	public static void main(String[] args) {
		new Main("").start();
	}

}
