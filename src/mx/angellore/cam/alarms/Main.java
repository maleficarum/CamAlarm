/**
 * 
 */
package mx.angellore.cam.alarms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

	private ApplicationContext ctx = null;
	private AlarmSender sender = null;
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private String hostname;

	public Main() {
		ctx = new ClassPathXmlApplicationContext("mx/angellore/cam/alarms/applicationContext.xml");
		sender = (AlarmSender) ctx.getBean("alarmSender");


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
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String cmd = in.readLine();

				sender.sendAlarm(hostname,cmd);

				cmd = null;
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}

	public static void main(String[] args) {
		new Main();
	}

}
