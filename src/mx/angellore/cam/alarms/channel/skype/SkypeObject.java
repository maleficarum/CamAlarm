/**
 * 
 */
package mx.angellore.cam.alarms.channel.skype;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skype.api.Skype;

/**
 * 
 * @author Oscar Ivan Hernandez Ventura [ oscar at angellore dot mx]
 *
 * @version $ Id : SkypeObject.java , v 1.0 19/09/2011 angellore $
 */
public class SkypeObject extends Skype {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * Datagram stream ID, used by Tutorial 11.
	 * 
	 * @since 1.0
	 */
	public String streamName = new String("");

	/**
	 * Assigns active input and output devices from among those available.
	 * Notifies user regarding the name of the selected devices or whether the request failed.
	 * <em>Both</em> devices must exist for the request to succeed.
	 * 
	 * @param micIdx
	 * 	Index into the array of available recording devices of the requested input device.
	 * @param spkrIdx
	 * 	Index into the array of available playback devices of the requested output device.
	 * 
	 * @return
	 * <ul>
	 *   <li>true: success</li>
	 *   <li>false: failure</li>
	 * </ul>
	 * 
	 * @see com.skype.api.Skype#GetAvailableRecordingDevices()
	 * @see com.skype.api.Skype#GetAvailableOutputDevices()
	 * 
	 * @since 1.0
	 */
	public boolean SetupAudioDevices(int micIdx, int spkrIdx) {
		boolean	passFail = true;	// Ever the optimist, assume success!

		Skype.GetAvailableRecordingDevicesResult inputDevices = this.GetAvailableRecordingDevices();
		Skype.GetAvailableOutputDevicesResult outputDevices = this.GetAvailableOutputDevices();

		if (micIdx > (inputDevices.handleList.length + 1)) {

			passFail = false;
		}

		if (spkrIdx > (outputDevices.handleList.length + 1)) {
			logger.info("Invalid speaker device no. ({}) passed", spkrIdx);
			passFail = false;
		}

		if (passFail) {
			logger.info("Setting mic to {} ({}){}", inputDevices.nameList[micIdx], inputDevices.productIdList[micIdx]);
			logger.info("Setting speakers to {}  ({}){}", outputDevices.nameList[spkrIdx], outputDevices.productIdList[spkrIdx]);
			
			this.SelectSoundDevices(inputDevices.handleList[micIdx], outputDevices.handleList[spkrIdx], outputDevices.handleList[spkrIdx]);
			this.SetSpeakerVolume(100);
		}

		return (passFail);
	}


	/**
	 * Normalizes a phone number and indicates that operation's success/failure.
	 * <br /><br />
	 * Determines the country code dialing prefix through {@link com.skype.api.Skype#GetISOCountryInfo()}
	 * by matching the default Locale country with an entry in the
	 * {@link com.skype.api.Skype.GetISOCountryInfoResult#countryCodeList}.
	 * Writes a message to the console indicating success/failure reason.
	 * 
	 * @param pstn
	 * 	Phone number to normalize.
	 * 
	 * @return
	 *   The normalization result, which includes:
	 *   <ul>
	 *     <li>an Enum instance detailing success/failure reason.</li>
	 *     <li>the normalized string (success) or error message string (failure)</li>
	 *   </ul>
	 * 
	 * @see com.skype.api.Skype#NormalizePSTNWithCountry(String, int)
	 * @see com.skype.api.Skype#GetISOCountryInfo()
	 * 
	 * @since 1.0
	 */
	public Skype.NormalizeIdentityResult GetNormalizationStr(String pstn) {
		Skype.NormalizeIdentityResult nrmlResultReturn = new NormalizeIdentityResult();

		Skype.GetISOCountryInfoResult isoInfo = this.GetISOCountryInfo();
		int availCountryCodes = isoInfo.countryCodeList.length;
		int isoInfoIdx;
		String ourCountryCode = Locale.getDefault().getCountry();
		for (isoInfoIdx = 0; isoInfoIdx < availCountryCodes; isoInfoIdx++) {
			if (ourCountryCode.equalsIgnoreCase(isoInfo.countryCodeList[isoInfoIdx])) {
				break;
			}
		}
		if (isoInfoIdx >= availCountryCodes) {
			nrmlResultReturn.result = Skype.NORMALIZERESULT.IDENTITY_EMPTY; // Anything but IDENTITY_OK...
			nrmlResultReturn.normalized = "Couldn't match Locale!";
			logger.info("%s: Error! Couldn't match Locale {} in Skype.GetISOCountryInfo results", ourCountryCode);
			return (nrmlResultReturn);
		}
		logger.info("%n%s ISOInfo match (%d of %d):%n\tCode: %s%n\tDialExample: %s%n\tName: %s%n\tPrefix: %s%nLocale: %s%n%n", new Object[] { (isoInfoIdx + 1),
				this.GetISOCountryInfo().countryCodeList.length,
				this.GetISOCountryInfo().countryCodeList[isoInfoIdx],
				this.GetISOCountryInfo().countryDialExampleList[isoInfoIdx],
				this.GetISOCountryInfo().countryNameList[isoInfoIdx],
				this.GetISOCountryInfo().countryPrefixList[isoInfoIdx],
				Locale.getDefault().getCountry() });

		Skype.NormalizePSTNWithCountryResult nrmlResult = this.NormalizePSTNWithCountry(pstn, isoInfo.countryPrefixList[isoInfoIdx]);

		switch (nrmlResult.result) {
		case IDENTITY_OK:
			nrmlResultReturn.normalized = nrmlResult.normalized;
			break;
		case IDENTITY_EMPTY:
			nrmlResultReturn.normalized = "Identity input was empty";
			break;
		case IDENTITY_TOO_LONG:
			nrmlResultReturn.normalized = "Identity string too long";
			break;
		case IDENTITY_CONTAINS_INVALID_CHAR:
			nrmlResultReturn.normalized = "Invalid character(s) found in identity string";
			break;
		case PSTN_NUMBER_TOO_SHORT:
			nrmlResultReturn.normalized = "PSTN number too short";
			break;
		case PSTN_NUMBER_HAS_INVALID_PREFIX:
			nrmlResultReturn.normalized = "Invalid character(s) found in PSTN prefix";
			break;
		case SKYPENAME_STARTS_WITH_NONALPHA :
			nrmlResultReturn.normalized = "Skype Name string starts with non-alphanumeric character";
			break;
		case SKYPENAME_SHORTER_THAN_6_CHARS:
			nrmlResultReturn.normalized = "Skype Name too short";
			break;
		default:
			nrmlResultReturn.normalized = "Cannot determine Skype.NORMALIZATION ?!?";
			break;
		}

		if (nrmlResult.result != Skype.NORMALIZERESULT.IDENTITY_OK) {
			logger.info("Error! Raw PSTN: %s - Normalized PSTN: %s.%n", pstn, nrmlResultReturn.normalized);
		} else {
			logger.info("%s: Raw PSTN: %s / Normalized PSTN: %s.%n", pstn, nrmlResultReturn.normalized);
		}

		nrmlResultReturn.result = nrmlResult.result;
		return nrmlResultReturn;
	}

}
