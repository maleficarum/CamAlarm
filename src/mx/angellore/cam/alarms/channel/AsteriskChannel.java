/**
 * 
 */
package mx.angellore.cam.alarms.channel;

import java.io.IOException;

import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;

import com.solab.alarms.AlarmChannel;

/**
 * @author angellore
 *
 */
public class AsteriskChannel implements AlarmChannel {
	
	private ManagerConnection managerConnection;

	public AsteriskChannel(String h, String m, String p) {
		ManagerConnectionFactory factory = new ManagerConnectionFactory(h,m,p);
        this.managerConnection = factory.createManagerConnection();		
	}
	
	public int getMinResendInterval() {
		return 1000 * 60 * 5;
	}

	public void send(String arg0, String arg1) {
		OriginateAction originateAction = new OriginateAction();
        ManagerResponse originateResponse = null;

        originateAction.setChannel("SIP/John");
        originateAction.setContext("default");
        originateAction.setExten("1300");
        originateAction.setPriority(new Integer(1));
        originateAction.setTimeout(new Integer(30000));

        // connect to Asterisk and log in
        try {
			managerConnection.login();
	        originateResponse = managerConnection.sendAction(originateAction, 30000);
	        
			System.out.println(originateResponse.getResponse());
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
		//Do nothing
	}

}
