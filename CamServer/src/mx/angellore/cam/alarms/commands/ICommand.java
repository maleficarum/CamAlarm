/**
 * 
 */
package mx.angellore.cam.alarms.commands;

import java.util.List;

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : ICommand.java , v 1.0 04/08/2011 angellore $
 */
public interface ICommand {
	
	public List<String> execute(Object...args);

}
