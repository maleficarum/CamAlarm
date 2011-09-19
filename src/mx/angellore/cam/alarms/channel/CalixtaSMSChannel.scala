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
class CalixtaSMSChannel extends AlarmChannel {
  
	@BeanProperty
	var minResendInterval:Int = 0
	val smsg = new SMSGateway();
	
	def send (arg0:String, arg1:String) = {
		val csv = "NOMBRE,TELEFONO\r\nMike,ABCD"
		val resp = smsg.sendCSVString(csv,"Hola {$NOMBRE$}, saludos desde Java.");
		println(resp)
	}
	
	def shutdown = {
	
	}  

}