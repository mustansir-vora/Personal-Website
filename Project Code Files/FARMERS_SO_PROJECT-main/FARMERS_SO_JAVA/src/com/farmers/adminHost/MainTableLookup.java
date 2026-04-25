package com.farmers.adminHost;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
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
import com.farmers.AdminAPI.GetMainTableByBusinessObjects;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.HotEventTransferHoursCheck;
import com.farmers.util.TimezoneCheck;

/*SANI_ADM_003 && EM_ADM_001*/
public class MainTableLookup extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		try {
			data.addToLog(currElementName, "MainTableLookup called**********");
			HotEventTransferHoursCheck eventTransferHoursCheck = new HotEventTransferHoursCheck();
			eventTransferHoursCheck.isWithInBusinessHours(data);
		}catch (Exception e) {
			data.addToLog(currElementName,"Exception in HotEventTransferHoursCheck call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		try {
			data.addToLog(currElementName, "MainTableLookup API start  ");
		if(data.getSessionData(Constants.S_MAIN_DETAILS_URL) != null &&  data.getSessionData(Constants.S_DNIS) != null
				&& data.getSessionData(Constants.S_DNISGROUP) != null && data.getSessionData(Constants.S_CATEGORY) != null  && data.getSessionData(Constants.S_LOB) != null 
				&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_MAIN_DETAILS_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String dnis = (String)data.getSessionData(Constants.S_DNIS);
			String dnisgroup = (String) data.getSessionData(Constants.S_DNISGROUP);
			String category = (String) data.getSessionData(Constants.S_CATEGORY);
			String lob = (String) data.getSessionData(Constants.S_LOB);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			GetMainTableByBusinessObjects test = new GetMainTableByBusinessObjects();
			JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, dnis, dnisgroup, lob, category, conTimeout, readTimeout, context);
			data.addToLog(currElementName, "GetMainTableByBusinessObjects API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					data.addToLog(currElementName, "Set SANI_ADM_003  GetMainTableByBusinessObjects API Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in GetMainTableByBusinessObjects API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"GetMainTableByBusinessObjects API", strReqBody,"", (String) data.getSessionData(Constants.S_MAIN_DETAILS_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode," ");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}
	
	@SuppressWarnings("deprecation")
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String StrExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			data.addToLog(currElementName,"mainObj size :"+resArr.size());
			
			JSONObject mainObj =null;
			ArrayList<String> stategroup = (ArrayList<String>)data.getSessionData(Constants.S_STATE_GROUP);
			data.addToLog(currElementName,"Area based on state group :"+stategroup);
			String stategroupMain= Constants.EmptyString;
			String originalStateGroup =Constants.EmptyString;
			try {
				boolean skip = false;
				
				if(resArr.size() == 0 ) return Constants.ER;
				if(resArr.size() >1 && stategroup!=null) {
					for(String stateGroupAreaCode: stategroup) {
						for (int i = 0; i < resArr.size(); i++) {
							JSONObject	tempObj = (JSONObject) resArr.get(i);
							stategroupMain= (String)tempObj.get("stategroup");
							if(stategroupMain !=null && stategroupMain.equalsIgnoreCase(stateGroupAreaCode)) {
								mainObj = tempObj;
								originalStateGroup =stategroupMain;
								skip = true;
								
								break;
							}else if("".equals(stategroupMain)) {
								mainObj = tempObj;
							}else {
								
							}
						}
						if(skip) {
							break;
						}
					}
				}
			} catch (Exception e) {
				data.addToLog(currElementName,"Exception in State group method  :: "+e);
				caa.printStackTrace(e);
			}
			
			data.addToLog(currElementName,"mainObj based on state group :"+mainObj);
			
			
			if(mainObj==null)mainObj = (JSONObject) resArr.get(0);
			if(mainObj.get("stategroup") != null && !((String)mainObj.get("stategroup")).isEmpty()) {
				ArrayList<String> stateGroup = new ArrayList<String>();
				stateGroup.add(""+mainObj.get("stategroup"));
				//data.setSessionData(Constants.S_STATE_GROUP, stateGroup);
				//data.setSessionData(Constants.S_ORIGINAL_STATE_GROUP, stateGroup);
				
			}else {
				if(stategroup!=null) {
					//data.setSessionData(Constants.S_STATE_GROUP, stategroup);
					//data.setSessionData(Constants.S_ORIGINAL_STATE_GROUP, stategroup.toString());
					
				}
			}
			
			if(mainObj.get("action") != null && !((String)mainObj.get("action")).isEmpty()) data.setSessionData("action", (String)mainObj.get("action"));
			if(mainObj.get("language") != null && !((String)mainObj.get("language")).isEmpty()) data.setSessionData("language", (String)mainObj.get("language"));
			if(mainObj.get("secondarylanguage") != null && !((String)mainObj.get("secondarylanguage")).isEmpty()  && (((String)mainObj.get("secondarylanguage")).equals("1"))) {
				data.setSessionData(Constants.S_SECONDARY_LANG, Constants.TRUE);
				if(((String) data.getSessionData("language")).equalsIgnoreCase(Constants.English)) 
					data.setSessionData(Constants.S_PREF_LANG, Constants.EN);
				else 
					data.setSessionData(Constants.S_PREF_LANG, Constants.SP);
			} else {
				data.setSessionData(Constants.S_SECONDARY_LANG, Constants.FALSE);
				if (((String) data.getSessionData("language")).equals(Constants.Spanish)) 
					data.setSessionData(Constants.S_PREF_LANG, Constants.SP);
				else 
					data.setSessionData(Constants.S_PREF_LANG, Constants.EN);
			}
			data.setSessionData(Constants.S_ORIGINAL_LANGUAGE, (String)data.getSessionData(Constants.S_PREF_LANG));
			data.addToLog(currElementName, "Admin API language : "+data.getSessionData(Constants.S_PREF_LANG));
			
			if(mainObj.get("livepersoncheck") != null && !((String)mainObj.get("livepersoncheck")).isEmpty() && (((String)mainObj.get("livepersoncheck")).equals("1")))
				data.setSessionData(Constants.S_LIVE_PERSON_CHECK_ENABLED, Constants.TRUE);
			if(mainObj.get("kyc") != null && !((String)mainObj.get("kyc")).isEmpty() && (((String)mainObj.get("kyc")).equals("1")))
				data.setSessionData(Constants.S_KYC_ENABLED, Constants.TRUE);
			if(mainObj.get("nlu") != null && !((String)mainObj.get("nlu")).isEmpty() && (((String)mainObj.get("nlu")).equals("1")))
				data.setSessionData(Constants.S_NLU_ENABLED, Constants.TRUE);
			//Start CS1240953 - General ID and Auth Simplification
			if(mainObj.get("viaauthentication") != null && !((String)mainObj.get("viaauthentication")).isEmpty() && (((String)mainObj.get("viaauthentication")).equals("1")))
				data.setSessionData(Constants.S_NEW_VIA_FLAG, Constants.TRUE);
			//End CS1240953 - General ID and Auth Simplification
			//Welcome prompt details
			if(null != mainObj.get("welcomemessage")) {
				JSONArray welcomemessageArr = (JSONArray)mainObj.get("welcomemessage");
				if(null != welcomemessageArr && welcomemessageArr.size() > 0 ) {
					
					String[] arrDetails = AudioFormation(welcomemessageArr,data);
					data.addToLog(currElementName,"finalWave :"+arrDetails[0]);
					data.addToLog(currElementName,"finalTTS :"+arrDetails[1]);
					
					data.setSessionData("EM_PA_001_WAV", arrDetails[0]);
					data.setSessionData("EM_PA_001_TTS", arrDetails[1]);
					
				}
			}
			
			//main menu details
			if(null != mainObj.get("mainmenu") && !"".equals(mainObj.get("mainmenu").toString())) {
				JSONArray mainMenuArr = (JSONArray)mainObj.get("mainmenu");
				if(null != mainMenuArr && mainMenuArr.size() > 0 ) {
					JSONObject mainMenuObj = (JSONObject) mainMenuArr.get(0);
					String action = (String)data.getSessionData("action");
					data.addToLog(currElementName, "Main table IVR action :"+action);
					if(action.equalsIgnoreCase(Constants.MainMenu_Direct_Transfer) || action.equalsIgnoreCase(Constants.MainMenu_Welcome_and_Transfer)||action.equalsIgnoreCase(Constants.MainMenu_Welcome_and_Transfer_With_ANI_Lookup)) {
						data.setSessionData(Constants.S_FIND_IVR_MAINFLOW, action);
						data.addToLog(currElementName, "Action: "+data.getSessionData(Constants.S_FIND_IVR_MAINFLOW));
					} else if (action.equalsIgnoreCase(Constants.MainMenu_IVR_Treatment)) {
						if(mainMenuObj.get("key") != null && !((String)mainMenuObj.get("key")).isEmpty()) data.setSessionData(Constants.S_FIND_IVR_MAINFLOW, (String)mainMenuObj.get("key"));
						if(mainMenuObj.get("producerline") != null && !((String)mainMenuObj.get("producerline")).isEmpty() && (((String)mainMenuObj.get("producerline")).equals("1"))) {
								data.setSessionData(Constants.S_PRODUCER_LINE, Constants.TRUE);
								data.setSessionData(Constants.S_CALLLER_TYPE,"02");
						}
//						Caller Verification Change by Venkatesh K M -Start
						if(mainMenuObj.get("callerverification") != null && !((String)mainMenuObj.get("callerverification")).isEmpty() && (((String)mainMenuObj.get("callerverification")).equals("1"))) {
							data.setSessionData(Constants.S_CALLER_VERIFICATION, Constants.YES);
							data.addToLog(currElementName, "S_CALLER_VERIFICATION :"+data.getSessionData(Constants.S_CALLER_VERIFICATION));
							
					} else {
						data.setSessionData(Constants.S_CALLER_VERIFICATION, Constants.NO);
						data.addToLog(currElementName, "S_CALLER_VERIFICATION :"+data.getSessionData(Constants.S_CALLER_VERIFICATION));
					}
						

//						Caller Verification Change by Venkatesh K M -End

						
					} else {
						data.setSessionData(Constants.S_FIND_IVR_MAINFLOW, Constants.MainMenu_Disconnect);
					}
				}else {
					String action = (String)mainObj.get("action");
					data.addToLog(currElementName, "Main table IVR action :"+action);
					if(action.equalsIgnoreCase(Constants.MainMenu_Direct_Transfer) || action.equalsIgnoreCase(Constants.MainMenu_Welcome_and_Transfer)) {
						data.setSessionData(Constants.S_FIND_IVR_MAINFLOW, action);
						data.addToLog(currElementName, "Action: "+data.getSessionData(Constants.S_FIND_IVR_MAINFLOW));
					}else {
						data.setSessionData(Constants.S_FIND_IVR_MAINFLOW, Constants.MainMenu_Disconnect);
					}
				}
			}else {
				String action = (String)mainObj.get("action");
				data.addToLog(currElementName, "Main table IVR action :"+action);
				if(action.equalsIgnoreCase(Constants.MainMenu_Direct_Transfer) || action.equalsIgnoreCase(Constants.MainMenu_Welcome_and_Transfer)||action.equalsIgnoreCase(Constants.MainMenu_Welcome_and_Transfer_With_ANI_Lookup)) {
					data.setSessionData(Constants.S_FIND_IVR_MAINFLOW, action);
					data.addToLog(currElementName, "Action: "+data.getSessionData(Constants.S_FIND_IVR_MAINFLOW));
				}else {
					data.setSessionData(Constants.S_FIND_IVR_MAINFLOW, Constants.MainMenu_Disconnect);
				}
			}
			data.addToLog(currElementName, "Main table S_FIND_IVR_MAINFLOW :"+data.getSessionData(Constants.S_FIND_IVR_MAINFLOW));

			
			JSONObject dayObj = null;
			boolean isOpen = true;
			Map<String,String> exceptionMap =  (HashMap<String,String>) isCCOpenOverride(mainObj, Constants.exceptiondayoverrides, data, caa);
			if(exceptionMap!=null && Constants.TRUE.equalsIgnoreCase(exceptionMap.get("OverRaidFlag"))) {
				String timewindow = exceptionMap.get("timeWindow");
				String timeZone = exceptionMap.get("timeZone");
				if(timewindow.contains(".")) timewindow = timewindow.replaceAll("\\.", ":");
				isOpen = TimezoneCheck.checkwithinTime(timewindow, timeZone, data);
				data.addToLog(currElementName, "Is callcenter Open by exception : " + isOpen);
			}else if(null != mainObj.get("timetable")) {
				JSONArray timetableArr = (JSONArray)mainObj.get("timetable");
				if(timetableArr != null && timetableArr.size() > 0 ) {
					JSONObject timetableObj = (JSONObject) timetableArr.get(0);
					
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
					data.addToLog(currElementName, "Is callcenter Open : " + isOpen);
				}
			}else{
				data.addToLog(currElementName, "timetable Object is null ");
			}
			
			data.addToLog(currElementName, "Checking holiday ");
			Map<String, String> holidayMap =  holidayCheck(mainObj, "holidaymessage", data, caa);
			String holidayFlag = (String)holidayMap.get("HolidayFlag");
			if(Constants.TRUE.equalsIgnoreCase(holidayFlag)) {
				isOpen = false;
				String audioText = "";
				String audioWav = "";
				if(Constants.SP.equalsIgnoreCase((String)data.getSessionData(Constants.S_PREF_LANG))){
					audioText = holidayMap.get("audiospanishtext");
					audioWav  = holidayMap.get("audiospanishwavpath");
					data.addToLog(currElementName, "audiospanishtext :"+audioText);
					data.addToLog(currElementName, "audiospanishwavpath :"+audioWav);
				}else {
					audioText = holidayMap.get("audioenglishtext");
					audioWav =  holidayMap.get("audioenglishwavpath");
					data.addToLog(currElementName, "audioenglishtext :"+audioText);
					data.addToLog(currElementName, "audioenglishwavpath :"+audioWav);
				}
				data.setSessionData(Constants.Entry_HOOP_PA_001_TTS, audioText);
				data.setSessionData(Constants.Entry_HOOP_PA_001_WAV, audioWav);
				data.setSessionData("HolidayFlag", holidayFlag);
			}
			
			
			data.addToLog(currElementName, "Call center Open flag : " + isOpen);
			if(isOpen) {
				data.setSessionData(Constants.S_CALLCENTER_OPEN_CLOSED, Constants.S_OPEN);
			} else {
				data.setSessionData(Constants.S_CALLCENTER_OPEN_CLOSED, Constants.CLOSED);
			}
			
			data.addToLog(currElementName, "S_CALLCENTER_OPEN_CLOSED : "+data.getSessionData(Constants.S_CALLCENTER_OPEN_CLOSED));
			
			//peeloff details
			if(null != mainObj.get("peeloff")) {
				JSONArray peeloffArr = (JSONArray)mainObj.get("peeloff");
				if(peeloffArr != null && peeloffArr.size() > 0 ) {
					data.setSessionData(Constants.S_PEEL_OFF_ENABLED, Constants.STRING_YES);
					JSONObject peeloffObj = (JSONObject) peeloffArr.get(0);
					data.setSessionData(Constants.S_PEEL_OFF_DNIS, peeloffObj.get("dnis"));
					if(!peeloffObj.get("audioenglishwavpath").equals("")) {
						String wavFile = (String)peeloffObj.get("audioenglishwavpath"); 
						data.addToLog(currElementName, "wavFile : "+wavFile);
						if(wavFile.contains("\\")) {
							String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
							wavFile = wavFileArr[wavFileArr.length-1];
						}
						data.setSessionData(Constants.EM_MN_001_WAV, wavFile);
					}
					if(!peeloffObj.get("audioenglishtext").equals("")){
						data.setSessionData(Constants.EM_MN_001_TTS, (String)peeloffObj.get("audioenglishtext"));
					}
					data.addToLog(currElementName, "EN peel off wav prompt : "+data.getSessionData(Constants.EM_MN_001_WAV)+" :: EN peel off TTS prompt : "+data.getSessionData(Constants.EM_MN_001_TTS));
				}
			}
			//Set audio path with Admin main API lang
			caa.setDefaultAudioPath((String)data.getSessionData(Constants.S_PREF_LANG),(String)data.getSessionData(Constants.S_VXML_SERVER_IP));
			
			StrExitState = Constants.SU;
			data.setSessionData(Constants.S_IS_MAIN_TABLE_CALLED, Constants.STRING_YES);
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}
	
	private JSONObject getOrderedJSON(JSONArray welcomemessageArr, int filecount,String prefLang) {
		JSONObject objJSONObject = null;
		
		for(int i=0; i<welcomemessageArr.size();i++) {
			JSONObject tempObj = (JSONObject) welcomemessageArr.get(i); 
			String key= (String)tempObj.get("key");
			
			if(key!=null && key.contains("_"+filecount) && !(key.contains("_1_"+filecount)) && key.endsWith("_"+filecount)) {
				objJSONObject = tempObj;
				break;
			}else {
				objJSONObject = (JSONObject) welcomemessageArr.get(0);
			}
		}
		return objJSONObject;
	}
	
	private String[] AudioFormation(JSONArray welcomemessageArr, DecisionElementData data) {
		String prefLang = (String)data.getSessionData(Constants.S_PREF_LANG);
		String finalWave="",finalTTS="";
		String currElementName=data.getCurrentElement();
		String[] arr = new String[2];
		for(int i=0; i<welcomemessageArr.size();i++) {
			int fileCount = i+1;
			JSONObject welcomemessageObj =getOrderedJSON(welcomemessageArr, fileCount,prefLang);
			data.addToLog(data.getCurrentElement(),"Count :"+fileCount);
			data.addToLog(data.getCurrentElement(),"key :"+welcomemessageObj.get("key"));
			
			if(Constants.EN.equals(prefLang)) {
				String wavFile = (String)welcomemessageObj.get("audioenglishwavpath"); 
				if(wavFile.contains("\\")) {
					String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
					wavFile = wavFileArr[wavFileArr.length-1];
				}
				finalWave= (finalWave.equals("") && i==0)?finalWave+ wavFile:finalWave+"|"+wavFile;
				finalTTS= (finalTTS.equals("") && i==0)?finalTTS+ (String)welcomemessageObj.get("audioenglishtext"):finalTTS+"|"+(String)welcomemessageObj.get("audioenglishtext");
				data.addToLog(currElementName, "EN:: audioenglishwavpath : "+finalWave+" :: audioenglishtext : "+finalTTS);
			} else {
				String wavFile = (String)welcomemessageObj.get("audiospanishwavpath"); 
				if(wavFile.contains("\\")) {
					String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
					wavFile = wavFileArr[wavFileArr.length-1];
				}
				finalWave= (finalWave.equals("") && i==0)?finalWave+ wavFile:finalWave+"|"+wavFile;
				finalTTS= (finalTTS.equals("") && i==0)?finalTTS+ (String)welcomemessageObj.get("audiospanishtext"):finalTTS+"|"+(String)welcomemessageObj.get("audiospanishtext");
				
				data.addToLog(currElementName, "SP:: audiospanishwavpath : "+finalWave+" :: audiospanishtext : "+finalTTS);
			}
			
		}
		arr[0]=finalWave;
		arr[1]=finalTTS;
		return arr;
	}
	
	private Map<String,String> isCCOpenOverride(JSONObject mainObj, String ccName, DecisionElementData data, CommonAPIAccess caa) {		
		Map<String,String> retList = new HashMap<String,String>();
		String ccOverrideFlag = Constants.FALSE;
		String timeWindow = Constants.EmptyString;
		String timeZone = Constants.EmptyString;
		data.addToLog(data.getCurrentElement(), "Time table name for check exception over rides : " + ccName);
		if (mainObj.containsKey(ccName)) {
			JSONArray exceptiondayOverrideArray = (JSONArray) mainObj.get(ccName);
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
				        	data.addToLog(data.getCurrentElement(), "Override day matching for : " + ccName);
				        	JSONObject overrideTimeWindowObj = (JSONObject)overrideObject.get("timewindow");
				        	timeWindow = (String)overrideTimeWindowObj.get("timewindow");
				        	timeZone = (String)overrideTimeWindowObj.get("timezone");
				        	data.addToLog(data.getCurrentElement(), "Override time window : " + timeWindow);
				        	data.addToLog(data.getCurrentElement(), "Override timezone : " + timeZone);
				        	ccOverrideFlag = Constants.TRUE;
				        	//breaking for loop
				        	break;
				        } else {
				        	data.addToLog(data.getCurrentElement(), "Override day not matching for : " + ccName);
				        }
					}
				} else {
					data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + ccName);
				}
			} else {
				data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + ccName);
			}
		}
		retList.put("OverRaidFlag",ccOverrideFlag);
		retList.put("timeWindow",timeWindow);
		retList.put("timeZone",timeZone);
		data.addToLog(data.getCurrentElement(), "Override return list : " + retList);
		return retList;
	}
	
	
	private Map<String,String> holidayCheck(JSONObject mainObj, String ccName, DecisionElementData data, CommonAPIAccess caa) {		
		Map<String,String> retList = new HashMap<String,String>();
		String ccOverrideFlag = Constants.FALSE;
		data.addToLog(data.getCurrentElement(), "Time table name for check holiday : " + ccName);
		
		if (mainObj.containsKey(ccName)) {
			JSONArray exceptiondayOverrideArray = (JSONArray) mainObj.get(ccName);
			if(exceptiondayOverrideArray != null) {
				if(exceptiondayOverrideArray.size() > 0) {
					for (@SuppressWarnings("rawtypes")
					Iterator iterator = exceptiondayOverrideArray.iterator(); iterator.hasNext();) {
						JSONObject overrideObject = (JSONObject) iterator.next();
						String startDateString = (String)overrideObject.get("startdatetime");
						String stopDateString = (String)overrideObject.get("stopdatetime");
						String latestartdatetime = (String)overrideObject.get("latestartdatetime");
						
						if(latestartdatetime!=null && !Constants.EmptyString.equals(latestartdatetime))
							startDateString=latestartdatetime;
						
						// Parse the date string to a LocalDateTime
				        LocalDateTime startDateTime = LocalDateTime.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				        LocalDateTime stopDateTime = LocalDateTime.parse(stopDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				        // Convert LocalDateTime to LocalDate
				        
				        data.addToLog(data.getCurrentElement(), "Override start date returned : " + startDateTime);
				        data.addToLog(data.getCurrentElement(), "Override end date returned : " + stopDateTime);
				        // Get the current date
				        ZonedDateTime nowInCentral = ZonedDateTime.now(ZoneId.of("America/Chicago"));
				        LocalDateTime today = nowInCentral.toLocalDateTime();
				        data.addToLog(data.getCurrentElement(), "current day : " + today);
				        // Compare the dates
				        if(today.isAfter(startDateTime) && today.isBefore(stopDateTime)) {
				        	data.addToLog(data.getCurrentElement(), "Today is Holiday ");
				        	JSONArray holidayrecordingsArr = (JSONArray)overrideObject.get("holidayrecordings");
				        	JSONObject holidayrecordingsObj = (JSONObject) holidayrecordingsArr.get(0);
				        	
				        	retList.put("audioenglishtext",""+holidayrecordingsObj.get("audioenglishtext"));
				    		retList.put("audioenglishwavpath",""+holidayrecordingsObj.get("audioenglishwavpath"));
				    		retList.put("audiospanishtext",""+holidayrecordingsObj.get("audiospanishtext"));
				    		retList.put("audiospanishwavpath",""+holidayrecordingsObj.get("audiospanishwavpath"));
				        	
				        	
				        	data.addToLog(data.getCurrentElement(), "audio english text : " + holidayrecordingsObj.get("audioenglishtext"));
				        	data.addToLog(data.getCurrentElement(), "audio english wav path : " + holidayrecordingsObj.get("audioenglishwavpath"));
				        	data.addToLog(data.getCurrentElement(), "audio spanish text : " + holidayrecordingsObj.get("audiospanishtext"));
				        	data.addToLog(data.getCurrentElement(), "audio spanish wav path : " + holidayrecordingsObj.get("audiospanishwavpath"));
				        	ccOverrideFlag = Constants.TRUE;
				        	//breaking for loop
				        	break;
				        } else {
				        	data.addToLog(data.getCurrentElement(), "Override day not matching for : " + ccName);
				        }
					}
				} else {
					data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + ccName);
				}
			} else {
				data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + ccName);
			}
		}
		retList.put("HolidayFlag",ccOverrideFlag);
		
		data.addToLog(data.getCurrentElement(), "Override return list : " + retList);
		return retList;
	}
	
	public static void main(String[] args) {
		  LocalDateTime startDateTime = LocalDateTime.parse("2024-03-27T23:30:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	      LocalDateTime stopDateTime = LocalDateTime.parse("2024-03-27T23:50:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	      ZonedDateTime nowInCentral = ZonedDateTime.now(ZoneId.of("America/Chicago"));
	      LocalDateTime today = nowInCentral.toLocalDateTime();
			System.out.println(startDateTime);
			System.out.println(stopDateTime);
			System.out.println(nowInCentral);
	      if(today.isAfter(startDateTime) && today.isBefore(stopDateTime)) {
	    	  System.out.println("TURE");
	      }else {
	    	  System.out.println("False");
	      }
	      
	      
	}
	
}