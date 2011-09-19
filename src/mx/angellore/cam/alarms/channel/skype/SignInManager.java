/**
 * 
 */
package mx.angellore.cam.alarms.channel.skype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skype.api.Account;


/**
 * SignIng Manager. Based on skype examples.
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : SignInManager.java , v 1.0 19/09/2011 angellore $
 */
public class SignInManager {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
  	 * Request polling interval (milliseconds).
  	 * 
  	 * @since 1.0
  	 */
    public static final int DELAY_INTERVAL = 1000;   // Equivalent to 1 second.

    /**
  	 * Request polling limit (iterations).
  	 * Results in a maximum total delay of <code>DELAY_CNT * DELAY_INTERVAL</code>
  	 * <em>milliseconds</em> before giving up and failing.
  	 * 
  	 * @since 1.0
  	 */
    public static final int DELAY_CNT = 45;
    
    /**
  	 * Delay interval prior to logout (milliseconds).
  	 * <br /><br />
  	 * Timing issue w/ some early versions of SkypeKit runtime - "immediate" logout
  	 * frequently causes the runtime to reflect an erroneous logout reason of
  	 * Account.LOGOUTREASON.APP_ID_FAILURE, so wait a few seconds... 
  	 * 
  	 * @since 1.0
  	 */
    public static final int LOGOUT_DELAY = (3 * 1000);   // Equivalent to 3 seconds.

	/**
	 * Common SkypeKit tutorial login processing.
	 * <ul>
	 *   <li>populates the session's Account instance</li>
	 *   <li>writes message to the console indicating success/failure/timeout</li>
	 *   <li>writes stack trace if I/O error setting up the transport!</li>
	 * </ul>
	 * 
	 * @param mySession
	 *	Partially initialized session instance providing access to this sessions's Skype object.
	 * 
	 * @return
	 *   <ul>
	 * 	   <li>true: success; {@link com.skype.tutorial.util.MySession#myAccount} populated</li>
	 *	   <li>false: failure</li>
	 *   </ul>
	 * 
	 * @since 1.0
	 */
	public boolean Login(SkypeSession mySession, String myAccountPword) {

		if (mySession.isLoggedIn()) {
			// Already logged in...
			logger.info("{} already logged in! (IP Addr {}:{}){}", new Object[] { mySession.getAccountName(), SkypeSession.IP_ADDR, SkypeSession.PORT_NUM });
			return true;
		}

		// Issue login request
		mySession.getAccount().LoginWithPassword(myAccountPword, false, true);
		

		int i = 0;
		while ((i < DELAY_CNT) && ((!mySession.isLoggedIn()) && (!mySession.isLoggedOut()))) {
			try {
				System.out.println("DECT " + DELAY_CNT + " " + mySession.isLoggedIn() + " - " + mySession.loginStatus);
				Thread.sleep(DELAY_INTERVAL);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}

		if (i < DELAY_CNT) {
			if (mySession.isLoggedIn()) {
				// Successful Login
				logger.info("{} already logged in! (IP Addr {}:{}){}", new Object[] { mySession.getAccountName(), SkypeSession.IP_ADDR, SkypeSession.PORT_NUM });
				return true;
			} else {
				logger.info("{} failed logged in! (IP Addr {}:{}){}", new Object[] { mySession.getAccountName(), SkypeSession.IP_ADDR, SkypeSession.PORT_NUM });
				return false;
			}
		} else {
			logger.info("{} timeout (IP Addr {}:{}){}", new Object[] { mySession.getAccountName(), SkypeSession.IP_ADDR, SkypeSession.PORT_NUM });
			return false;
		}
	}
	

	/**
	 * Common SkypeKit tutorial logout processing.
	 * <ul>
	 *   <li>writes message to the console indicating success/failure/timeout</li>
	 *   <li>writes stack trace if I/O error setting up the transport!</li>
	 * </ul>
	 * 
	 * Delays the logout by a few seconds to ensure that the SkypeKit runtime has fully settled in
	 * if the interval between sign-in and sign-out is really, really short (such as exists in
	 * {@link com.skype.tutorial.step1.Tutorial_1}). We don't want to see
  	 * Account.LOGOUTREASON.APP_ID_FAILURE unless our AppToken is truly bogus! 
	 * 
	 * @param myTutorialTag
	 * 	Invoker's {@link #MY_CLASS_TAG}.
	 * @param mySession
	 * 	Populated session object providing access to the invoker's
	 *  Skype and Account objects.
	 *  
	 * @see #LOGOUT_DELAY
	 * 
	 * @since 1.0
	 */
	public void Logout(SkypeSession mySession) {
		
		// Give the runtime a chance to catch its breath if it needs to...
		try {
			Thread.sleep(LOGOUT_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (mySession.isLoggedOut()) {
			logger.info("{} already logged out! (IP Addr {}:{}){}", new Object[] { mySession.getAccountName(), SkypeSession.IP_ADDR, SkypeSession.PORT_NUM });
			return;
		}

		// Issue logout request
		mySession.getAccount().Logout(false);

		int i = 0;
		while ((i < DELAY_CNT) && (!mySession.isLoggedOut())) {
			try {
				Thread.sleep(DELAY_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

		if (i < DELAY_CNT) {
			// Successful Logout
			logger.info("{} already logged out (IP Addr {}:{}){}", new Object[] { mySession.getAccountName(), SkypeSession.IP_ADDR, SkypeSession.PORT_NUM });
		} else {
			logger.info("{} timeout for log out (IP Addr {}:{}){}", new Object[] { mySession.getAccountName(), SkypeSession.IP_ADDR, SkypeSession.PORT_NUM });
		}
	}


	/**
	 * <em>Dynamically</em> determines if an Account is signed in.
	 * <br /><br />
	 * Specifically, this involves querying the DB to determine if the
	 * Account's status property reflects Account.STATUS.LOGGED_IN.
	 * For mobile devices, such activity can adversely affect battery life.
	 * <br /><br />
	 * <em>Only</em> a current status of <code>Account.STATUS.LOGGED_IN</code>
	 * returns true.
	 * <b><i>"Not logged in" is not the same as "Logged out"!</i></b>
	 * 
	 * @param myAccount
	 *  The target Account. 
	 *  
	 * @return
	 * <ul>
	 *   <li>true: currently signed in</li>
	 *   <li>false: currently signed out <em>or</em> target Account is null</li>
	 * </ul>
	 * 
	 * @see com.skype.tutorial.util.MySession#isLoggedIn()
	 * 
	 * @since 1.0
	 */
	public static boolean isLoggedIn(Account myAccount) {
		
		if (myAccount != null) {
			Account.STATUS accountStatus = Account.STATUS.get(myAccount.GetIntProperty(Account.PROPERTY.status));

			if (accountStatus == Account.STATUS.LOGGED_IN) {
				return (true);
			}
		}
		return (false);
	}
}
