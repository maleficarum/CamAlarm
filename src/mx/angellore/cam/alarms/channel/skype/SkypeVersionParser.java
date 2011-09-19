/**
 * 
 */
package mx.angellore.cam.alarms.channel.skype;

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : SkypeVersionParser.java , v 1.0 19/09/2011 angellore $
 */
public class SkypeVersionParser {
	/**
	 * The SkypeKit version string, as returned by com.skype.api.Skype.GetVersionString.
	 * 
	 * @since 1.0
	 */
	protected static String versionStr;

	/**
	 * Number of components in the version number string.
	 * 
	 * @since 1.0
	 */
	protected static final int versionNumCnt = 4;

	/**
	 * Parsed array of the individual component version numbers.
	 * 
	 * @since 1.0
	 */
	protected static String[] versionNums = new String[versionNumCnt];
	

	/**
	 * Tutorial constructor.
	 * <br /><br />
	 * Obtain the skypeKit version string, for example:
	 * <pre>
	 * 2.0/windows-x86-skypekit-novideo_3.1.0.2689_125068
	 * </pre>
	 * and:
	 * <ul>
	 *   <li>store it in {@link #versionStr}</li>
	 *   <li>split it at the underscores, yielding "2.0/windows-x86-skypekit-novideo",
	 *   	"3.1.0.2689", and "125068"</li>
	 *   <li>split the version number segment at the periods, yielding "3", "1", "0", and "2689"</li>
	 *   <li>store the <i>number</i> of version number components in {@link #versionNumCnt}</li>
	 *   <li>store the actual version number components in {@link #versionNums}</li>
	 * </ul>
	 * 
	 * @since 1.0
	 */
	public SkypeVersionParser(SkypeObject mySkype) {
		String[] versionParts;
		
		SkypeVersionParser.versionStr = mySkype.GetVersionString();
		
		if (SkypeVersionParser.versionStr.length() > 1) {
		    versionParts = SkypeVersionParser.versionStr.split("_");
		
		    SkypeVersionParser.versionNums = versionParts[1].split("\\.", SkypeVersionParser.versionNumCnt);
        }
	}

	
	/**
	 * Obtain the <em>complete</em> unparsed version string exactly as as returned by com.skype.api.Skype.GetVersionString
	 * 
	 * @return
	 * 	The unparsed SkypeKit version string.
	 * 
	 * @since 1.0
	 */
	public String getVersionStr() {
		return (SkypeVersionParser.versionStr);
	}

	
	/**
	 * Obtain the <em>major</em> SkypeKit version number.
	 * 
	 * @return
	 * 	The major SkypeKit version number, for example,</br />
	 * 	&nbsp;&nbsp;&nbsp;&nbsp;<code>2.0/windows-x86-skypekit-novideo_3.1.0.2689_125068</code>
	 *  <br />
	 * 	would return 3.
	 * 
	 * @since 1.0
	 */
	public int getMajorVersion() {
		if (SkypeVersionParser.versionStr.length() > 0) {
		   return (Integer.parseInt(SkypeVersionParser.versionNums[0]));
		} else {
			return 0;
		}

	}

	/**
	 * Obtain the <em>minor</em> SkypeKit version number.
	 * 
	 * @return
	 * 	The minor SkypeKit version number, for example,</br />
	 * 	&nbsp;&nbsp;&nbsp;&nbsp;<code>2.0/windows-x86-skypekit-novideo_3.1.0.2689_125068</code>
	 *  <br />
	 * 	would return 1 (one).
	 * 
	 * @since 1.0
	 */
	public int getMinorVersion() {
		if (SkypeVersionParser.versionStr.length() > 0) {
		   return (Integer.parseInt(SkypeVersionParser.versionNums[1]));
		} else {
			return 0;
		}
	}
	
	/**
	 * Obtain the <em>patch</em> SkypeKit version number.
	 * 
	 * @return
	 * 	The patch SkypeKit version number, for example,</br />
	 * 	&nbsp;&nbsp;&nbsp;&nbsp;<code>2.0/windows-x86-skypekit-novideo_3.1.0.2689_125068</code>
	 *  <br />
	 * 	would return 0 (zero).
	 * 
	 * @since 1.0
	 */
	public int getPatchVersion() {
		if (SkypeVersionParser.versionStr.length() > 0) {
			return (Integer.parseInt(SkypeVersionParser.versionNums[2]));
		} else {
			return 0;
		}
	}
}
