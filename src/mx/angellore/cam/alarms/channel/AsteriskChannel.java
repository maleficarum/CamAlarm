/**
 * 
 */
package mx.angellore.cam.alarms.channel;

import java.io.IOException;
import java.util.Map;

import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;

import com.solab.alarms.AlarmChannel;

/**
 * @author angellore
 *
 */
public class AsteriskChannel implements AlarmChannel {
	
	private ManagerConnection managerConnection;
	private Map<String, String> config;

	public AsteriskChannel(String h, String m, String p) {
		ManagerConnectionFactory factory = new ManagerConnectionFactory(h,m,p);
        this.managerConnection = factory.createManagerConnection();		
	}
	
	public int getMinResendInterval() {
		return 1000 * 60 * 5;
	}

	public void send(String arg0, String arg1) {
		OriginateAction originateAction = new OriginateAction();

        originateAction.setChannel(config.get("CHANNEL"));
        originateAction.setContext(config.get("CONTEXT"));
        originateAction.setExten(config.get("EXTENSION"));
        originateAction.setPriority(new Integer(1));
        originateAction.setTimeout(10000l);
        originateAction.setCallerId(config.get("CALLERID"));

        // connect to Asterisk and log in
        try {
			managerConnection.login();
	        managerConnection.sendAction(originateAction, 30000);
	        managerConnection.logoff();			        
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AuthenticationFailedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

	}

	public void shutdown() {

	}
	
	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

}
