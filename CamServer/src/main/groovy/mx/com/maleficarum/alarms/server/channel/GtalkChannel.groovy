package mx.com.maleficarum.alarms.server.channel

import com.solab.alarms.AlarmChannel
import org.jivesoftware.smack.*
import org.jivesoftware.smack.packet.Message

import com.solab.alarms.AlarmChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Date: 03/08/12 20:11
 * @author Oscar I. Hernandez
 */
public class GtalkChannel implements AlarmChannel, MessageListener, ConnectionListener {

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
            println("Envidnao ${arg0} ${arg1} a ${d}")
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
        try {
            arg0.sendMessage(arg1.getBody())
        } catch (XMPPException e) {
            e.printStackTrace()
        }
    }

    public void connectionClosed() {
        logger.warn("Conexion cerrada. Reconectando ...");
        connect();
    }

    public void connectionClosedOnError(Exception e) {
        logger.warn("Se produjo un error. Reconectando ...", e);
        connect();
    }

    public void reconnectingIn(int i) {
        logger.warn("Reconectando " + i);
    }

    public void reconnectionSuccessful() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reconnectionFailed(Exception e) {
        logger.error("No se pudo reconectar. Intentando de nuevo ...", e);
        connect();
    }
}
