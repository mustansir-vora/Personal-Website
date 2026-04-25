package com.farmers.adminHost;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetWarheadTableByKey;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.TimezoneCheck;

public class WarheadTableByKey extends DecisionElementBase{
	
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
		if(data.getSessionData(Constants.S_WARHEAD_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_WARHEAD_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String strAPIName = (String) data.getSessionData(Constants.S_API_NAME);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			GetWarheadTableByKey obj = new GetWarheadTableByKey();
			JSONObject resp = obj.start(url, callerId, Constants.tenantid, strAPIName, conTimeout, readTimeout, context);
			data.addToLog(currElementName, "AccLinkAniLookup �PI response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
					data.addToLog(currElementName, "Set WarheadTableByKey  Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				} else {
					strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
				}
				
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in WarheadTableByKey call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"WarheadTableByKey", strReqBody,"", (String) data.getSessionData(Constants.S_WARHEAD_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for WarheadTableByKey call  :: "+e);
			caa.printStackTrace(e);
		}
	return strExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			if(resArr.size() == 0 ) return Constants.ER;
			JSONObject warheadObj = (JSONObject) resArr.get(0);
			String strIsAPIDisabled = (String)warheadObj.get("disable");
			if(null != strIsAPIDisabled && strIsAPIDisabled.equalsIgnoreCase("1")) strIsAPIDisabled = Constants.TRUE;
			else strIsAPIDisabled = Constants.FALSE;
			data.setSessionData(Constants.S_IS_API_DISABLED, strIsAPIDisabled);
			data.addToLog(currElementName, "S_IS_API_DISABLED : "+data.getSessionData(Constants.S_IS_API_DISABLED));
			JSONArray warheadannouncementrecordingArr = (JSONArray)warheadObj.get("warheadannouncementrecording");
			if(null != warheadannouncementrecordingArr && warheadannouncementrecordingArr.size() > 0 ) {
				JSONObject warheadAnnRecordingArrObj = (JSONObject) warheadannouncementrecordingArr.get(0);
				if(data.getSessionData(Constants.S_PREF_LANG).equals(Constants.EN)) {
					if(!((String)warheadAnnRecordingArrObj.get("audioenglishwavpath")).equals("") && ((String)warheadAnnRecordingArrObj.get("audioenglishwavpath")).contains(".wav")) {
						String wavFile = (String)warheadAnnRecordingArrObj.get("audioenglishwavpath"); 
						if(wavFile.contains("\\")) {
							String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
							wavFile = wavFileArr[wavFileArr.length-1];
						}
						data.setSessionData(Constants.WH_PA_001_WAV, wavFile);
					}
					if(!((String)warheadAnnRecordingArrObj.get("audioenglishtext")).equals(""))	data.setSessionData(Constants.WH_PA_001_TTS, warheadAnnRecordingArrObj.get("audioenglishtext"));
					data.addToLog(currElementName, "EN :: audioenglishwavpath : "+data.getSessionData(Constants.WH_PA_001_WAV)+" :: audioenglishtext : "+data.getSessionData(Constants.WH_PA_001_TTS));
				} else {
					if(!((String)warheadAnnRecordingArrObj.get("audiospanishwavpath")).equals("") && ((String)warheadAnnRecordingArrObj.get("audiospanishwavpath")).contains(".wav")) {
						String wavFile = (String)warheadAnnRecordingArrObj.get("audiospanishwavpath"); 
						if(wavFile.contains("\\")) {
							String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
							wavFile = wavFileArr[wavFileArr.length-1];
						}
						data.setSessionData(Constants.WH_PA_001_WAV, wavFile);
					}
					if(!((String)warheadAnnRecordingArrObj.get("audiospanishtext")).equals("")){
						data.setSessionData(Constants.WH_PA_001_TTS, warheadAnnRecordingArrObj.get("audiospanishtext"));
					}
					data.addToLog(currElementName, "SP:: audiospanishtext : "+data.getSessionData(Constants.WH_PA_001_WAV)+" :: audiospanishtext : "+data.getSessionData(Constants.WH_PA_001_TTS));
				}
			}
			JSONObject dayObj = null;
			boolean isOpen = false;
			if(strIsAPIDisabled.equalsIgnoreCase(Constants.FALSE)) {
				Map<String,String> exceptionMap =  (HashMap<String,String>) isWarheadDSOpenOverride(warheadObj, "downtimescheduleexceptiondayoverrides", data, caa);
				if(exceptionMap!=null && Constants.TRUE.equalsIgnoreCase(exceptionMap.get("OverRaidFlag"))) {
					String timewindow = exceptionMap.get("timeWindow");
					String timeZone = exceptionMap.get("timeZone");
					if(timewindow.contains(".")) timewindow = timewindow.replaceAll("\\.", ":");
					isOpen = TimezoneCheck.checkwithinTime(timewindow, timeZone, data);
					data.addToLog(currElementName, "S_WARHEAD_OPEN_CLOSED by exception : " + isOpen);
					if(isOpen) data.setSessionData(Constants.S_WARHEAD_OPEN_CLOSED, Constants.S_OPEN);	
				} else if(null != warheadObj.get("downtimeschedulewindow")) {
					JSONArray downtimeschedulewindowArr = (JSONArray)warheadObj.get("downtimeschedulewindow");
					if(downtimeschedulewindowArr != null && downtimeschedulewindowArr.size() > 0 ) {
						JSONObject timetableObj = (JSONObject) downtimeschedulewindowArr.get(0);
						ZonedDateTime nowInCentral = ZonedDateTime.now(ZoneId.of("America/Chicago"));
						// Extract the day of the week
				        DayOfWeek dayOfWeek = nowInCentral.getDayOfWeek();
				        // Print the day of the week in full text format (e.g., "Monday", "Tuesday", ...)
				        // Locale.ENGLISH is used to ensure the output is in English. You can adjust the locale as needed.
				        String dayOfWeekText = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
				        dayOfWeekText = dayOfWeekText.toLowerCase();
				        data.addToLog(data.getCurrentElement(), "Day of week in Central : " + dayOfWeekText);
				        dayObj = (JSONObject)timetableObj.get(dayOfWeekText);
						data.addToLog(currElementName, "dayObj : "+dayObj);
						String timewindow = (String)dayObj.get("timewindow");
						if(timewindow.contains(".")) timewindow = timewindow.replaceAll("\\.", ":");
						isOpen = TimezoneCheck.checkwithinTime(timewindow, "CT", data);
						data.addToLog(currElementName, "S_WARHEAD_OPEN_CLOSED : " + isOpen);
						if(isOpen) data.setSessionData(Constants.S_WARHEAD_OPEN_CLOSED, Constants.S_OPEN);	
					}
				} else{
					data.addToLog(currElementName, "downtimeschedulewindow Object is null ");
				}
				data.addToLog(currElementName, "S_WARHEAD_OPEN_CLOSED : "+data.getSessionData(Constants.S_WARHEAD_OPEN_CLOSED));
			}
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	private Map<String,String> isWarheadDSOpenOverride(JSONObject warheadObj, String strOverrideTime, DecisionElementData data, CommonAPIAccess caa) {		
		Map<String,String> retList = new HashMap<String,String>();
		String ccOverrideFlag = Constants.FALSE;
		String timeWindow = Constants.EmptyString;
		String timeZone = Constants.EmptyString;
		data.addToLog(data.getCurrentElement(), "Down time check for exception over rides : " + strOverrideTime);
		if (warheadObj.containsKey(strOverrideTime)) {
			JSONArray exceptiondayOverrideArray = (JSONArray) warheadObj.get(strOverrideTime);
			if(exceptiondayOverrideArray != null) {
				if(exceptiondayOverrideArray.size() > 0) {
					for (@SuppressWarnings("rawtypes")
					Iterator iterator = exceptiondayOverrideArray.iterator(); iterator.hasNext();) {
						JSONObject overrideObject = (JSONObject) iterator.next();
						String dateString = (String)overrideObject.get("date");
						// Parse the date string to a LocalDateTime
				        LocalDateTime dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				        // Convert LocalDateTime to LocalDate
				        LocalDate date = dateTime.toLocalDate();
				        data.addToLog(data.getCurrentElement(), "Override date returned : " + date);
				        // Get the current date
				        ZonedDateTime nowInCentral = ZonedDateTime.now(ZoneId.of("America/Chicago"));
				        LocalDate today = nowInCentral.toLocalDate();
				        data.addToLog(data.getCurrentElement(), "current day : " + today);
				        // Compare the dates
				        if(date.equals(today)) {
				        	data.addToLog(data.getCurrentElement(), "Override day matching for : " + strOverrideTime);
				        	JSONObject overrideTimeWindowObj = (JSONObject)overrideObject.get("timewindow");
				        	timeWindow = (String)overrideTimeWindowObj.get("timewindow");
				        	timeZone = (String)overrideTimeWindowObj.get("timezone");
				        	data.addToLog(data.getCurrentElement(), "Override time window : " + timeWindow);
				        	data.addToLog(data.getCurrentElement(), "Override timezone : " + timeZone);
				        	ccOverrideFlag = Constants.TRUE;
				        	//breaking for loop
				        	break;
				        } else {
				        	data.addToLog(data.getCurrentElement(), "Override day not matching for : " + strOverrideTime);
				        }
					}
				} else {
					data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + strOverrideTime);
				}
			} else {
				data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + strOverrideTime);
			}
		}
		retList.put("OverRaidFlag",ccOverrideFlag);
		retList.put("timeWindow",timeWindow);
		retList.put("timeZone",timeZone);
		data.addToLog(data.getCurrentElement(), "Override return list : " + retList);
		return retList;
	}
}
