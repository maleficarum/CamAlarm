/**
 * 
 */
package mx.angellore.cam.alarms.channel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.solab.alarms.AlarmChannel;

/**
 * @author angellore
 *
 */
public class AsteriskChannel implements AlarmChannel {

	private File src;
	private File dst;
	
	public AsteriskChannel(String a, String b) {
		src = new File(a);
		dst = new File(b);
	}
	
	public int getMinResendInterval() {
		return 1000 * 60 * 5;
	}

	public void send(String arg0, String arg1) {
		if(src.exists()) {
			try {
				FileUtils.copyFileToDirectory(src, dst);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void shutdown() {
		//Do nothing
	}

}
