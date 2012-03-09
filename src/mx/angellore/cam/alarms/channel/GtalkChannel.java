package mx.angellore.cam.alarms.channel;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mx.angellore.cam.alarms.commands.ICommand;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import com.solab.alarms.AlarmChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : GtalkChannel.java , v 1.0 26/07/2011 angellore $
 */
public class GtalkChannel implements AlarmChannel, MessageListener, ConnectionListener {

	@Resource(name="commands")
	private Map<String, ICommand> commands;

	private XMPPConnection connection = null;
	private List<String> destinos ;
	private int minResendInterval = 1000 * 60 * 5;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String user ;
    private String password ;

	public GtalkChannel(String u, String p, List<String> dest) {
		destinos = dest;
        user = u;
        password = p;
        logger.info("Inicializado ....");
        connect();
	}

    private void connect() {
        logger.info("Conectando a gmail ...");
        ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
        connection = new XMPPConnection(config);


        try {
            connection.connect();
            connection.login(user, password);

            connection.getChatManager().addChatListener(
                    new ChatManagerListener() {
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            chat.addMessageListener(GtalkChannel.this);
                        }
                    });
            connection.addConnectionListener(this);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

	public int getMinResendInterval() {
		return minResendInterval;
	}

	public void setMinResendInterval(int minResendInterval) {
		this.minResendInterval = minResendInterval;
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
		ICommand command = commands == null ? null : commands.get(arg1.getBody().toUpperCase());
		try {
			if(command != null) {

				List<String> r = command.execute(new Object[] {});
				if(r != null) {
					for(String rr : r) {
						arg0.sendMessage(rr);
					}
				}

			} else {
				arg0.sendMessage("No existe comando " + arg1.getBody());
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void connectionClosed() {
        logger.warn("Conexion cerrada. Reconectando ...");
        connect();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        logger.warn("Se produjo un error. Reconectando ...", e);
        connect();
    }

    @Override
    public void reconnectingIn(int i) {
        logger.warn("Reconectando " + i);
    }

    @Override
    public void reconnectionSuccessful() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reconnectionFailed(Exception e) {
        logger.error("No se pudo reconectar. Intentando de nuevo ...", e);
        connect();
    }
}
