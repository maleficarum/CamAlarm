/**
 * 
 */
package mx.angellore.cam.alarms.commands.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import mx.angellore.cam.alarms.commands.ICommand;

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : GetIP.java , v 1.0 04/08/2011 angellore $
 */
public class GetIP implements ICommand {

	private final String WHATISMYIP = "http://automation.whatismyip.com/n09230945.asp";
	
	public String execute(Object... args) {
		String result = null;
		try {
			URL url = new URL(WHATISMYIP);
			InputStream in = url.openStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			result = reader.readLine();
			
			reader.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
