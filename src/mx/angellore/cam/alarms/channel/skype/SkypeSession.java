/**
 * 
 */
package mx.angellore.cam.alarms.channel.skype;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skype.api.Account;
import com.skype.ipc.TCPSocketTransport;
import com.skype.ipc.TLSServerTransport;
import com.skype.ipc.Transport;


/**
 * Skype session 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : SkypeSession.java , v 1.0 19/09/2011 angellore $
 */
public class SkypeSession {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private SignInManager signInManager = new SignInManager();
    private String accountName ;

    /**
	 * Skype instance for this tutorial session.
	 * 
	 * @see com.skype.tutorial.util.MySkype
	 * @see com.skype.api.Skype
	 * 
	 * @since 1.0
	 */
    private SkypeObject skype = null;

    /**
	 * SkypeKit version number parse instance for this tutorial session.
	 * <br /><br />
	 * Do <em>not</em> attempt to instantiate this instance until <em>after initializing</em>
	 * {@link #mySkype}!
	 * 
	 * @see com.skype.tutorial.util.ParseSkypeKitVersion
	 * 
	 * @since 1.0
	 */
    private SkypeVersionParser skypeVersionParser = null;
    
    /**
	 * Account instance for this tutorial session.
	 * Set on successful login, <i>not</i> during session creation!
	 * 
	 * @see com.skype.api.Account
	 * 
	 * @since 1.0
	 */
    private Account myAccount = null;


    /**
	 * Cached status of this session's associated Account.
	 * <br /><br />
	 * Initialized to <code>Account.STATUS.CONNECTING_TO_P2P</code> (initial login process state);
	 * updated by Account onPropertyChange handler. We don't initialize it to
	 * <code>Account.STATUS.LOGGED_OUT</code> so we can catch a failed login,
	 * for example, due to <code>Account.LOGOUTREASON.INCORRECT_PASSWORD</code> 
	 * 
	 * @see com.skype.tutorial.util.JavaTutorialListeners#OnPropertyChange(SkypeObject, com.skype.api.Account.PROPERTY, Object)
	 * @see com.skype.api.Account
	 * 
	 * @since 1.2
	 */
    public Account.STATUS loginStatus = Account.STATUS.CONNECTING_TO_P2P;
    
    /**
	 * Callbacks/event handlers for this tutorial session.
	 * 
	 * @since 1.0
	 */
    private SkypeObjectListener skypeObjectListener = null;

	/**
	 * Server IP Address.
	 * 
	 * @since 1.0
	 */
    public static final String IP_ADDR = "127.0.0.1";

    /**
	 * Server Port.
	 * <br /><br />
	 * If you modify this compiled-in default, you will need to start the matching SkypeKit runtime with option:<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;<code>-p <em>9999</em></code><br />
	 * where <code>-p <em>9999</em></code> reflects this value. 
	 * 
	 * @since 1.0
	 */
    public static final int PORT_NUM = 8963;

    public Transport myTransport = new TCPSocketTransport(IP_ADDR, PORT_NUM); 									  
    public TLSServerTransport myTLSTransport; 									  

		
    /**
	 * Creates <em>most</em> everything needed for a tutorial session; the Account instance is populated during sign-in. 
	 * 
	 * @param tutorialTag
	 *  The tutorial's class name. If null or the empty string, default it to <code>T_TAG_DFLT</code>.
	 * @param accountName
	 *  The <em>name</em> of the account to use for this tutorial. If null or the empty string,
	 *  <em>fail</em> by throwing a RuntimeException indicating that fact.
	 * @param myAppKeyPairMgr
	 * 	AppKeyPairMgr instance containing the certificate and key extracted from this tutorial's
	 *  associated PEM file.
	 * 
	 * @return
	 * <ul>
	 *   <li>true: session initialized</li>
	 *   <li>false: session initialization failed due to:
	 *   	<ul>
	 *   		<li>no or empty account name</li>
	 *   		<li>com.skype.api.Skype.Init failed - most likely from an invalid AppKeyPair</li>
	 *   		<li>could not obtain an Account instance</li>
	 *   	</ul>
	 *   </li>
	 * </ul>
	 *  
	 * @see com.skype.tutorial.util.SignInMgr
	 * 
	 * @since 1.0
	 */
	public boolean doCreateSession(String accountName, SkypeKeyPairManager myAppKeyPairMgr) {

		if ((accountName != null) && (accountName.length() != 0)) {
			this.accountName = new String(accountName);
		} else {
			throw new RuntimeException(": Cannot initialize session instance - no account name!");
		}
		
		// Set up our session with the SkypeKit runtime...
		// Note that most of the Skype methods - including static methods and GetVersionString - will
		// fail and/or throw an exception if invoked prior to successful initialization!
		skype = new SkypeObject();
        myTLSTransport = new TLSServerTransport(myTransport,
        						myAppKeyPairMgr.getAppKeyPairCertificate(),
        						myAppKeyPairMgr.getAppKeyPairPrivateKey());		

		try {
			skype.Init(myTLSTransport);
			skypeVersionParser = new SkypeVersionParser(skype);
			
	 		logger.info("Initialized SkypeObject instance - version = {} ({}.{}.{})", new Object[] {
	 					skypeVersionParser.getVersionStr(),
	 					skypeVersionParser.getMajorVersion(),
						skypeVersionParser.getMinorVersion(),
						skypeVersionParser.getPatchVersion() });
		} catch (IOException e1) {
			e1.printStackTrace();
			return (false);
		}
		
		skypeObjectListener = new SkypeObjectListener(this);
		
		// Get the Account
		if ((myAccount = skype.GetAccount(accountName)) == null) {
			logger.info("%s: Could not get Account for %s!%n", accountName);
			return (false);
		}
		
		logger.info("Got Account for {}", accountName);
		return (true);
	}

	
	/**
	 * Specifically, this involves:
	 * <ol>
	 *   <li>Un-registering the listeners</li>
	 *   <li>Disconnecting the transport</li>
	 *   <li>"Closing" our Skype instance, which terminates the SkypeKit runtime</li> 
	 * </ol> 
	 * @see com.skype.api.Skype#Close()
	 * 
	 * @since 1.0
	 */
	public void doTearDownSession() {

		try {
        	if (skypeObjectListener != null) {
        		skypeObjectListener.unRegisterAllListeners();
        		skypeObjectListener = null;
        	}
        	// closing Skype also disconnects the transport
        	if (skype != null) {
        		skype.Close();
        		skype = null;
        	}
		} catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
	}

	/**
	 * Retrieves the current login status of this session's Account.
	 * @return
	 * 	Cached login status of this session's Account.
	 * 
	 * @see com.skype.tutorial.util.JavaTutorialListeners#OnPropertyChange(SkypeObject, com.skype.api.Account.PROPERTY, Object)
	 * 
	 * @since 1.0
	 */
	public Account.STATUS getLoginStatus() {
		return (this.loginStatus); 
	}

	/**
	 * Establishes the login status of this session's Account.
	 * @param loginStatus
	 * 	Reported login status of this session's Account.
	 * 
	 * @see com.skype.tutorial.util.JavaTutorialListeners#OnPropertyChange(SkypeObject, com.skype.api.Account.PROPERTY, Object)
	 * 
	 * @since 1.0
	 */
	public void setLoginStatus(Account.STATUS loginStatus) {
		this.loginStatus = loginStatus;
		return;
	}

	/**
	 * Determines if an Account is signed in.
	 * <br /><br />
	 * Specifically, this involves examining the last cached value for
	 * the associated Account's status property. Caching the status avoids
	 * having to query the DB. For mobile devices, WiFi-connected laptops
	 * running on battery power, and so forth this typically avoids expending
	 * battery charge to transmit the server request.
	 * <br /><br />
 	 * Essentially, <em>only</em> a current status of <code>Account.STATUS.LOGGED_IN</code>
 	 * returns true.
	 * <b><i>"Not logged in" is not the same as "Logged out"!</i></b>
	 * 
	 * @return
	 * <ul>
	 *   <li>true: currently signed in</li>
	 *   <li>false: currently signed out <em>or</em> target Account is null</li>
	 * </ul>
	 * 
	 * @see com.skype.tutorial.util.SignInMgr#isLoggedIn(Account)
	 * 
	 * @since 1.0
	 */
	public boolean isLoggedIn() {
		if (this.loginStatus == Account.STATUS.LOGGED_IN) {
			return (true);
		}
		return (false);
	}


	/**
	 * Determines if an Account is signed out.
	 * <br /><br />
	 * Specifically, this involves examining the last cached value for
	 * the associated Account's status property. Caching the status avoids
	 * having to query the DB. For mobile devices, WiFi-connected laptops
	 * running on battery power, and so forth this typically avoids expending
	 * battery charge to transmit the server request.
	 * <br /><br />
 	 * Essentially, <em>only</em> a current status of <code>Account.STATUS.LOGGED_OUT</code>
 	 * returns true.
	 * <b><i>"Not logged out" is not the same as "Logged in"!</i></b>
	 * 
	 * @return
	 * <ul>
	 *   <li>true: currently signed in</li>
	 *   <li>false: currently signed out <em>or</em> target Account is null</li>
	 * </ul>
	 * 
	 * @see com.skype.tutorial.util.SignInMgr#isLoggedIn(Account)
	 * 
	 * @since 1.0
	 */
	public boolean isLoggedOut() {
		if (this.loginStatus == Account.STATUS.LOGGED_OUT) {
			return (true);
		}
		return (false);
	}


	public SignInManager getSignInManager() {
		return signInManager;
	}


	public SkypeObject getSkype() {
		return skype;
	}

	public String getAccountName() {
		return accountName;
	}

	public SkypeVersionParser getSkypeVersionParser() {
		return skypeVersionParser;
	}

	public Account getAccount() {
		return myAccount;
	}

}
