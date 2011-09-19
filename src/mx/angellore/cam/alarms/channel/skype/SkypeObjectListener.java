/**
 * 
 */
package mx.angellore.cam.alarms.channel.skype;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skype.api.Account;
import com.skype.api.Account.AccountListener;
import com.skype.api.Contact;
import com.skype.api.Contact.ContactListener;
import com.skype.api.ContactGroup;
import com.skype.api.Conversation;
import com.skype.api.Conversation.ConversationListener;
import com.skype.api.Conversation.LIST_TYPE;
import com.skype.api.Message;
import com.skype.api.Message.MessageListener;
import com.skype.api.Participant;
import com.skype.api.Participant.DTMF;
import com.skype.api.Participant.ParticipantListener;
import com.skype.api.Skype;
import com.skype.api.Skype.PROXYTYPE;
import com.skype.api.SkypeObject;
import com.skype.api.Sms;
import com.skype.api.Sms.PROPERTY;
import com.skype.api.Sms.SmsListener;
import com.skype.api.Skype.SkypeListener;

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : SkypeListener.java , v 1.0 19/09/2011 angellore $
 */
public class SkypeObjectListener implements AccountListener, SkypeListener, ContactListener,  ConversationListener,  MessageListener, ParticipantListener, SmsListener {

	private SkypeSession session ;
	private Conversation activeConversation = null;
	private Participant[] activeConversationParticipants = null;
	public boolean appConnected = false;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public SkypeObjectListener(SkypeSession session) {
		this.session = session;
		registerAllListeners();    	
	}

	public void registerListener ( int modid ) {
		session.getSkype().RegisterListener( modid, this );
	} 

	public void unRegisterListener ( int modid ) {
		session.getSkype().UnRegisterListener(modid, this);
	}	

	public void registerAllListeners () {
		registerListener( Account.moduleID());
		registerListener( Contact.moduleID());
		registerListener( Conversation.moduleID());
		registerListener( Message.moduleID());
		registerListener( Participant.moduleID());
		registerListener( Sms.moduleID());
		registerListener( Skype.getmoduleid());
	}

	public void unRegisterAllListeners () {
		unRegisterListener( Account.moduleID());
		unRegisterListener( Contact.moduleID());
		unRegisterListener( Conversation.moduleID());
		unRegisterListener( Message.moduleID());
		unRegisterListener( Participant.moduleID());
		unRegisterListener( Sms.moduleID());
		unRegisterListener( Skype.getmoduleid());
	}	

	public void OnPropertyChange(SkypeObject obj, PROPERTY prop, Object value) {
		System.out.println("Cambio " + prop + " a " + value);
	}

	public void OnPropertyChange(SkypeObject obj, com.skype.api.Participant.PROPERTY prop, Object value) {

	}

	public void OnIncomingDTMF(SkypeObject obj, DTMF dtmf) {


	}

	public void OnPropertyChange(SkypeObject obj, com.skype.api.Message.PROPERTY prop, Object value) {


	}

	public void OnPropertyChange(SkypeObject obj, com.skype.api.Conversation.PROPERTY prop, Object value) {


	}

	public void OnParticipantListChange(SkypeObject obj) {


	}

	public void OnMessage(SkypeObject obj, Message message) {
		Message.TYPE msgType = Message.TYPE.get(message.GetIntProperty(Message.PROPERTY.type));
		if (msgType == Message.TYPE.POSTED_TEXT) {
			Integer conversationID = message.GetIntProperty(Message.PROPERTY.convo_guid);
			Conversation conversation = session.getSkype().GetConversationByIdentity(conversationID.toString());
			String msgAuthor = message.GetStrProperty(Message.PROPERTY.author);
			String msgBody = message.GetStrProperty(Message.PROPERTY.body_xml);

			if (!msgAuthor.equals(session.getAccountName())) {
				Integer msgTimeStamp = new Integer(message.GetIntProperty(Message.PROPERTY.timestamp));
				Date dateTimeStamp = new Date((msgTimeStamp.longValue() * 1000L));
				DateFormat targetDateFmt = DateFormat.getDateTimeInstance();
				logger.info("posted message {} {} {}", new Object [] {targetDateFmt.format(dateTimeStamp), msgAuthor, msgBody});
				Calendar targetDate = Calendar.getInstance();
				conversation.PostText((targetDateFmt.format(targetDate.getTime()) + ": This is an automated reply"), false);
			}
		} else {
			logger.info("Ignoring ConversationListener.OnMessage of type {}",msgType.toString());
		}
	}

	public void OnSpawnConference(SkypeObject obj, Conversation spawned) {


	}

	public void OnPropertyChange(SkypeObject obj, com.skype.api.Contact.PROPERTY prop, Object value) {


	}

	public void OnNewCustomContactGroup(ContactGroup group) {


	}

	public void OnContactOnlineAppearance(Contact contact) {


	}

	public void OnContactGoneOffline(Contact contact) {


	}

	public void OnConversationListChange(Conversation conversation, LIST_TYPE type, boolean added) {
		
	}

	public void OnMessage(Message message, boolean changesInboxTimestamp, Message supersedesHistoryMessage, Conversation conversation) {
		Message.TYPE msgType = Message.TYPE.get(message.GetIntProperty(Message.PROPERTY.type));

        if (msgType == Message.TYPE.POSTED_TEXT) {
            String msgAuthor = message.GetStrProperty(Message.PROPERTY.author);
            String msgBody = message.GetStrProperty(Message.PROPERTY.body_xml);
            if (!msgAuthor.equals(session.getAccountName())) {
                // Get timestamp -- it's in seconds, and the Date constructor needs milliseconds!
            	Integer msgTimeStamp = new Integer(message.GetIntProperty(Message.PROPERTY.timestamp));
                Date dateTimeStamp = new Date((msgTimeStamp.longValue() * 1000L));
            	DateFormat targetDateFmt = DateFormat.getDateTimeInstance();
            	logger.info("posted message {} {} {}", new Object[] { targetDateFmt.format(dateTimeStamp), msgAuthor, msgBody });
            	Calendar targetDate = Calendar.getInstance();
            	conversation.PostText((targetDateFmt.format(targetDate.getTime()) + ": This is an automated reply"), false);
            }
        } else {
			logger.info("Ignoring SkypeListener.OnMessage of type {}", msgType.toString());
		}
	}

	public void OnAvailableVideoDeviceListChange() {


	}

	public void OnAvailableDeviceListChange() {


	}

	public void OnNrgLevelsChange() {


	}

	public void OnProxyAuthFailure(PROXYTYPE type) {


	}

	public void OnPropertyChange(SkypeObject obj,com.skype.api.Account.PROPERTY prop, Object value) {
	    if (prop == Account.PROPERTY.status) {
			Account.STATUS accountStatus = Account.STATUS.get(session.getAccount().GetIntProperty(Account.PROPERTY.status));
			session.setLoginStatus(accountStatus);
	    } else if (prop == Account.PROPERTY.logoutreason) {
 	    	Account.LOGOUTREASON logoutReason = Account.LOGOUTREASON.get(session.getAccount().GetIntProperty(Account.PROPERTY.logoutreason));
	    }
	}

}
