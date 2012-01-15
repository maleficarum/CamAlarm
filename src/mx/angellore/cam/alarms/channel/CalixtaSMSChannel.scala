/**
 * 
 */
package mx.angellore.cam.alarms.channel

import com.solab.alarms.AlarmChannel
import scala.reflect.BeanProperty
import com.auronix.calixta.sms.SMSGateway
import org.slf4j.LoggerFactory

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : CalixtaSMSChannel.scala , v 1.0 19/09/2011 angellore $
 */
class CalixtaSMSChannel(destListName:String) extends AlarmChannel {
  
	@BeanProperty
	var minResendInterval:Int = 5000
	val smsg = new SMSGateway();
	val logger = LoggerFactory.getLogger(getClass())
	
	def send (arg0:String, arg1:String) = {
		val rcode = smsg.sendMessageToList(destListName, arg1);
		logger.info("Rcode {}", rcode)
	}
	
	def shutdown = {
	
	}  

}