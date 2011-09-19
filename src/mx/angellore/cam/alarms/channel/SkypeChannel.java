/**
 * 
 */
package mx.angellore.cam.alarms.channel;

import java.util.List;

import mx.angellore.cam.alarms.channel.skype.SkypeKeyPairManager;
import mx.angellore.cam.alarms.channel.skype.SkypeSession;

import com.skype.api.Conversation;
import com.skype.api.Sms;
import com.skype.api.Sms.SetSMSBodyResult;
import com.solab.alarms.AlarmChannel;
/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : SkypeChannel.java , v 1.0 19/09/2011 angellore $
 */
public class SkypeChannel implements AlarmChannel {
	
    private static SkypeKeyPairManager keyPairManager = new SkypeKeyPairManager();
    private static SkypeSession skypeSession = new SkypeSession();
    private List<String> destinos ;
    public SkypeChannel(String keyLocation, String username, String password, List<String> destinos) {
    	this.destinos = destinos;
    	
    	if(!keyPairManager.setAppKeyPairFromFile(keyLocation)) {
    		throw new IllegalArgumentException("Unable to lad PEM file from location");
    	}
    	
    	if(!skypeSession.doCreateSession(username, keyPairManager)) {
    		throw new IllegalArgumentException("Unable to create Skype session");
    	}
    	
    	if(!skypeSession.getSignInManager().Login(skypeSession, password)) {
    		throw new IllegalArgumentException(String.format("Unable to login : %s", "")); 
    	}
    }

	public int getMinResendInterval() {
		return 0;
	}

	public void send(String arg0, String arg1) {
		String[] dest = destinos.toArray(new String[destinos.size()]);
		String body = String.format("%s%s", arg0, arg1);
		
    	Sms sms = skypeSession.getSkype().CreateOutgoingSms();
    	sms.SetBody(body);
    	
    	if(sms.SetTargets(dest)) {
    		Conversation conversation = skypeSession.getSkype().GetConversationByParticipants(dest, true, false);
    		int i = 0;
    		while(i < 10 && conversation == null) {
        		i++;
        		try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}    		
    		conversation.PostSMS(sms, body);    		
    	}
	}

	public void shutdown() {
		if (skypeSession != null) {
			skypeSession.getSignInManager().Logout(skypeSession);
			skypeSession.doTearDownSession();
		}
	}

}
