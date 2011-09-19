/**
 * 
 */
package mx.angellore.cam.alarms.channel

import com.solab.alarms.AlarmChannel
import scala.reflect.BeanProperty
import com.auronix.calixta.sms.SMSGateway

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : CalixtaSMSChannel.scala , v 1.0 19/09/2011 angellore $
 */
class CalixtaSMSChannel(destListName:String) extends AlarmChannel {
  
	@BeanProperty
	var minResendInterval:Int = 1000 * 10 * 5
	val smsg = new SMSGateway();
	
	def send (arg0:String, arg1:String) = {
		smsg.sendMessageToList(destListName, arg1);
	}
	
	def shutdown = {
	
	}  

}