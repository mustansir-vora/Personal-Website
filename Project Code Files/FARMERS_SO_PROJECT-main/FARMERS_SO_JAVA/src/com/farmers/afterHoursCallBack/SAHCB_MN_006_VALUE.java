package com.farmers.afterHoursCallBack;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SAHCB_MN_006_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String stateName = (String)data.getSessionData(Constants.S_STATENAME);
		 String statezone= getZoneIdFromState(stateName);
		data.addToLog(currElementName, "S_STATENAME :"+data.getSessionData(Constants.S_STATENAME));
		String strTimeZone=getTimeZoneFromState(stateName);
		data.addToLog(currElementName, "The State "+stateName+" Time Zone is::"+strTimeZone+ " And the Zone ID is ::"+statezone);
		data.setSessionData(Constants.S_TIMEZONE, strTimeZone);
		try {
			String strReturnValue = (String) data.getElementData("SAHCB_MN_006_Call","Return_Value");
			data.setSessionData("SAHCB_MN_006_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				
				ZonedDateTime callback = getCallbackTime("morning",statezone);
				String strcallback = callback.toInstant().toString();
				data.addToLog(currElementName, "The CallBack Time which is in Default ::"+strcallback);
				data.setSessionData(Constants.S_CALLBACK_TIME, strcallback);
				data.setSessionData(Constants.S_CALLBACK_TYPE, "Default");
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				
				ZonedDateTime callback = getCallbackTime("morning", statezone);
				String strcallback = callback.toInstant().toString();
				data.addToLog(currElementName, "The CallBack Time which is in Default ::"+strcallback);
				data.setSessionData(Constants.S_CALLBACK_TIME, strcallback);
				data.setSessionData(Constants.S_CALLBACK_TYPE, "Default");
				strExitState = Constants.NOMATCH;
			}

			else if(strReturnValue.equalsIgnoreCase("Morning")) {
				
				ZonedDateTime callback = getCallbackTime("morning", statezone);
				String strcallback = callback.toInstant().toString();
				data.addToLog(currElementName, "The CallBack Time which is in Morning ::"+strcallback);
				data.setSessionData(Constants.S_CALLBACK_TIME, strcallback);
				data.setSessionData(Constants.S_CALLBACK_TYPE, "Specific");
				strExitState = "done";
			}  else if(strReturnValue.equalsIgnoreCase("Afternoon")) {
				
				ZonedDateTime callback = getCallbackTime("afternoon", statezone);
				String strcallback = callback.toInstant().toString();
				data.addToLog(currElementName, "The CallBack Time which is in Afternoon ::"+strcallback);
				data.setSessionData(Constants.S_CALLBACK_TIME, strcallback);
				data.setSessionData(Constants.S_CALLBACK_TYPE, "Specific");
				strExitState = "done";
			} 
			else if(strReturnValue.equalsIgnoreCase("Evening")) {
				
				ZonedDateTime callback = getCallbackTime("evening", statezone);
				String strcallback = callback.toInstant().toString();
				data.addToLog(currElementName, "The CallBack Time which is in Evening ::"+strcallback);
				data.setSessionData(Constants.S_CALLBACK_TIME, strcallback);
				data.setSessionData(Constants.S_CALLBACK_TYPE, "Specific");
				strExitState = "done";
			}
			else {
				strExitState = Constants.ER;
				data.setSessionData("SAHCB_MN_006_VALUE", "ER");
			} 
			data.addToLog(currElementName,"SAHCB_MN_006_VALUE :: "+strExitState);
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in SAHCB_MN_006_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}



	//get Time Zone
	private  static String getTimeZoneFromState(String state) {
		String zoneId;

		switch (state.toLowerCase()) {
		// Eastern Time Zone
		case "connecticut": case "delaware": case "florida":
		case "georgia": case "maine": case "maryland":
		case "massachusetts": case "new hampshire": case "new jersey":
		case "new york": case "north carolina": case "ohio":
		case "pennsylvania": case "rhode island": case "south carolina":
		case "vermont": case "virginia": case "west virginia":
		case "michigan": case "indiana": case "kentucky":
			zoneId = "America/New_York";
			break;

			// Central Time Zone
		case "alabama": case "arkansas": case "illinois":
		case "iowa": case "louisiana": case "minnesota":
		case "mississippi": case "missouri": case "oklahoma":
		case "texas": case "wisconsin": case "tennessee":
		case "north dakota": case "south dakota": case "nebraska":
		case "kansas": case "kentucky (western)": case "florida (western)":
			zoneId = "America/Chicago";
			break;

			// Mountain Time Zone
		case "arizona": case "colorado": case "idaho":
		case "montana": case "new mexico": case "utah":
		case "wyoming":
			zoneId = "America/Denver";
			break;

			// Pacific Time Zone
		case "california": case "nevada": case "oregon": case "washington":
			zoneId = "America/Los_Angeles";
			break;

			// Alaska Time Zone
		case "alaska":
			zoneId = "America/Anchorage";
			break;

			// Hawaii Time Zone
		case "hawaii":
			zoneId = "Pacific/Honolulu";
			break;

		default:
			return "Unknown Time Zone for state: " + state;
		}

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of(zoneId));
		return now.format(DateTimeFormatter.ofPattern("z", Locale.ENGLISH));
	}
	
	//get  ZoneID 
		private  static String getZoneIdFromState(String state) {
			String zoneId;

			switch (state.toLowerCase()) {
			// Eastern Time Zone
			case "connecticut": case "delaware": case "florida":
			case "georgia": case "maine": case "maryland":
			case "massachusetts": case "new hampshire": case "new jersey":
			case "new york": case "north carolina": case "ohio":
			case "pennsylvania": case "rhode island": case "south carolina":
			case "vermont": case "virginia": case "west virginia":
			case "michigan": case "indiana": case "kentucky":
				zoneId = "America/New_York";
				break;

				// Central Time Zone
			case "alabama": case "arkansas": case "illinois":
			case "iowa": case "louisiana": case "minnesota":
			case "mississippi": case "missouri": case "oklahoma":
			case "texas": case "wisconsin": case "tennessee":
			case "north dakota": case "south dakota": case "nebraska":
			case "kansas": case "kentucky (western)": case "florida (western)":
				zoneId = "America/Chicago";
				break;

				// Mountain Time Zone
			case "arizona": case "colorado": case "idaho":
			case "montana": case "new mexico": case "utah":
			case "wyoming":
				zoneId = "America/Denver";
				break;

				// Pacific Time Zone
			case "california": case "nevada": case "oregon": case "washington":
				zoneId = "America/Los_Angeles";
				break;

				// Alaska Time Zone
			case "alaska":
				zoneId = "America/Anchorage";
				break;

				// Hawaii Time Zone
			case "hawaii":
				zoneId = "Pacific/Honolulu";
				break;

			default:
				return "Unknown Time Zone for state: " + state;
			}

			
			return zoneId ;
		}
	public static ZonedDateTime getCallbackTime(String input, String zoneIdStr) {
		ZoneId zoneId = ZoneId.of(zoneIdStr);

		// Define fixed callback times
		LocalTime morning = LocalTime.of(9, 0);   // 9 AM
		LocalTime afternoon = LocalTime.of(12, 0); // 12 PM
		LocalTime evening = LocalTime.of(16, 0);  // 4 PM

		// Current date & time in given time zone
		ZonedDateTime now = ZonedDateTime.now(zoneId);
		LocalDate today = now.toLocalDate();
		LocalDate tomorrow = today.plusDays(1);

		// Pick user preference
		LocalTime chosenTime;
		switch (input.toLowerCase(Locale.ROOT)) {
		case "morning":   chosenTime = morning; break;
		case "afternoon": chosenTime = afternoon; break;
		case "evening":   chosenTime = evening; break;
		default:   throw new IllegalArgumentException("Invalid input. Use: morning, afternoon, or evening."); // default
		}

		// If chosen time already passed today → schedule tomorrow
		LocalDate callbackDate = now.toLocalTime().isAfter(chosenTime) ? tomorrow : today;

		return ZonedDateTime.of(callbackDate, chosenTime, zoneId);
	}

}
