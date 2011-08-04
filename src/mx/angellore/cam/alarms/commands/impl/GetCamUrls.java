/**
 * 
 */
package mx.angellore.cam.alarms.commands.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Message;

import mx.angellore.cam.alarms.commands.ICommand;

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : GetCamUrls.java , v 1.0 04/08/2011 angellore $
 */
public class GetCamUrls implements ICommand {
	
	private final ICommand ipCmd = new GetIP();
	private InetAddress addr = null;
	
	public GetCamUrls() {
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public List<String> execute(Object... args) {
		List<String> responses = new ArrayList<String>();
		
		List<String> resp = ipCmd.execute(args);
		
		if(resp.size() == 1) {
			String ip = resp.get(0);
			responses.add(String.format("http://%s:1981", ip));
			responses.add(String.format("http://%s:1982", ip));
			
			if(addr != null) {
				responses.add(String.format("http://%s:1981", new String(addr.getHostAddress())));
				responses.add(String.format("http://%s:1982", new String(addr.getHostAddress())));	
			}
		}
		
		return responses;
	}

}
