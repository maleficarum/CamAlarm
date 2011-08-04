package mx.angellore.cam.alarms.channel;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mx.angellore.cam.alarms.commands.ICommand;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.solab.alarms.AlarmChannel;

/**
 * 
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : GtalkChannel.java , v 1.0 26/07/2011 angellore $
 */
public class GtalkChannel implements AlarmChannel, MessageListener {

	@Resource(name="commands")
	private Map<String, ICommand> commands;

	private XMPPConnection connection = null;
	private List<String> destinos ;

	public GtalkChannel(String u, String p, List<String> dest) {
		destinos = dest;
		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		connection = new XMPPConnection(config);


		try {
			connection.connect();			
			connection.login(u, p);
			
			
			
			connection.getChatManager().addChatListener(
				    new ChatManagerListener() {
				        public void chatCreated(Chat chat, boolean createdLocally) {
				                chat.addMessageListener(GtalkChannel.this);
				        }
				    });			
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public int getMinResendInterval() {
		return 1000 * 60 * 5;
	}

	public void send(String arg0, String arg1) {
		for(String d : destinos) {
			try {
				Chat chat = connection.getChatManager().createChat(d, this);
				chat.sendMessage(String.format("%s : %s", arg0, arg1));
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}

	public void shutdown() {
		connection.disconnect();
	}

	public void processMessage(Chat arg0, Message arg1) {
		if(arg1.getBody() == null) {
			return ;
		}
		ICommand command = commands == null ? null : commands.get(arg1.getBody());
		try {
			if(command != null) {

				String r = command.execute(new Object[] {});
				arg0.sendMessage(r);

			} else {
				arg0.sendMessage("No existe comando " + arg1.getBody());
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

}
