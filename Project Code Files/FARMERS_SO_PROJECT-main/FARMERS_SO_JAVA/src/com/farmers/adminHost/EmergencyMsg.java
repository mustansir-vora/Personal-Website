package com.farmers.adminHost;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetEmergencyAnnouncementByBusinessObjects;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*EM_ADM_002*/
public class EmergencyMsg extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
		if(data.getSessionData(Constants.S_EMERGENCY_DETAILS_URL) != null &&  data.getSessionData(Constants.S_DNIS) != null
				&& data.getSessionData(Constants.S_DNISGROUP) != null && data.getSessionData(Constants.S_CATEGORY) != null  && data.getSessionData(Constants.S_LOB) != null 
				&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String)data.getSessionData(Constants.S_EMERGENCY_DETAILS_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String dnis = (String) data.getSessionData(Constants.S_DNIS);
			String dnisgroup = (String) data.getSessionData(Constants.S_DNISGROUP);
			String category = (String) data.getSessionData(Constants.S_CATEGORY);
			String lob = (String) data.getSessionData(Constants.S_LOB);;
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			GetEmergencyAnnouncementByBusinessObjects test = new GetEmergencyAnnouncementByBusinessObjects();
			JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, dnis, dnisgroup, lob, category, conTimeout, readTimeout, context);
			strRespBody = resp.toString();
			data.addToLog(currElementName, "GetEmergencyAnnouncementByBusinessObjects API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					data.addToLog(currElementName, "Set EM_ADM_002  GetEmergencyAnnouncementByBusinessObjects API Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in GetEmergencyAnnouncementByBusinessObjects API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"GetEmergencyAnnouncementByBusinessObjects API", strReqBody,"", (String)data.getSessionData(Constants.S_EMERGENCY_DETAILS_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetEmergencyAnnouncementByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String StrExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			JSONObject emrgObj = null;
			
			if(resArr == null || resArr.size() == 0 ) return Constants.ER;
			
			if(resArr.size() == 0 ) {
				return Constants.ER;
			} else if(resArr.size() == 1) {
				emrgObj = (JSONObject)resArr.get(0);
			} else {
				@SuppressWarnings("unchecked")
				ArrayList<String> stategroup = (ArrayList<String>)data.getSessionData(Constants.S_STATE_GROUP);
				data.addToLog(currElementName,"Area based on state group :"+stategroup);
				//eliminate state groups
				@SuppressWarnings("unchecked")
				List<JSONObject> list = (List<JSONObject>) StreamSupport.stream(resArr.spliterator(), false).map(e -> (JSONObject) e).collect(Collectors.toList());
				List<JSONObject> tempList1 = new ArrayList<JSONObject>(routingSortingStateGroup(list, Constants.S_STATE_GROUP, Constants.StateGroup, data));
				
			
				List<JSONObject> tempList2 = routingSortingLang(tempList1, Constants.S_FINAL_LANG, Constants.Language, data);
				
				if (tempList2.size() > 0) {
					data.addToLog(currElementName, "Set to break loop at more than zero");
					data.addToLog(currElementName, "TempList : " + tempList2);
					emrgObj = (JSONObject) tempList2.get(0);
				} else {
					emrgObj = null;
				}
				data.addToLog(currElementName,"mainObj based on state group : " + emrgObj);
			}
			
			/*
			JSONObject emrgObj = (JSONObject) resArr.get(0);
			if(emrgObj.get("action") != null && !((String)emrgObj.get("action")).isEmpty()) data.setSessionData(Constants.S_EMERGENCY_ACTION, (String)emrgObj.get("action"));
			
			if(emrgObj.get("stategroup") != null && !((String)emrgObj.get("stategroup")).isEmpty()) {
				ArrayList<String> stateGroup = new ArrayList<String>();
				stateGroup.add(""+emrgObj.get("stategroup"));
				//data.setSessionData(Constants.S_STATE_GROUP, stateGroup);
			}*/
			
			
			JSONArray recordingArr = (JSONArray)emrgObj.get("recording");
			if(recordingArr == null || recordingArr.size() == 0 ) return Constants.ER;
			
			
			JSONObject recordingObj = (JSONObject)recordingArr.get(0);
			data.setSessionData(Constants.S_EMERGENCY_ENABLED, Constants.STRING_YES);
			if(data.getSessionData(Constants.S_FINAL_LANG).equals(Constants.English)) {
				if(!((String)recordingObj.get("audioenglishwavpath")).equals("") && ((String)recordingObj.get("audioenglishwavpath")).contains(".wav")) {
					String wavFile = (String)recordingObj.get("audioenglishwavpath"); 
					if(wavFile.contains("\\")) {
						String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
						wavFile = wavFileArr[wavFileArr.length-1];
					}
					data.setSessionData(Constants.EM_PA_002_WAV, wavFile);
				}
				if(!((String)recordingObj.get("audioenglishtext")).equals(""))	data.setSessionData(Constants.EM_PA_002_TTS, recordingObj.get("audioenglishtext"));
				data.addToLog(currElementName, "EN :: audioenglishwavpath : "+data.getSessionData(Constants.EM_PA_002_WAV)+" :: audioenglishtext : "+data.getSessionData(Constants.EM_PA_002_TTS));
			} else {
				if(!((String)recordingObj.get("audiospanishwavpath")).equals("") && ((String)recordingObj.get("audiospanishwavpath")).contains(".wav")) {
					String wavFile = (String)recordingObj.get("audiospanishwavpath"); 
					if(wavFile.contains("\\")) {
						String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
						wavFile = wavFileArr[wavFileArr.length-1];
					}
					data.setSessionData(Constants.EM_PA_002_WAV, wavFile);
				}
				if(!((String)recordingObj.get("audiospanishtext")).equals("")) data.setSessionData(Constants.EM_PA_002_TTS, recordingObj.get("audiospanishtext"));
				data.addToLog(currElementName, "SP:: audiospanishwavpath : "+data.getSessionData(Constants.EM_PA_002_WAV)+" :: audiospanishtext : "+data.getSessionData(Constants.EM_PA_002_TTS));
			}
			
			StrExitState = Constants.SU;
			data.setSessionData(Constants.S_IS_MAIN_TABLE_CALLED, Constants.STRING_YES);
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}
	
	@SuppressWarnings("unused")
	private List<JSONObject> routingSortingLang(List<JSONObject> reqList, String sessionKey, String routingRespKey,
			DecisionElementData data) {
		data.addToLog(data.getCurrentElement(), "Routing reqList size before sorting with " + sessionKey + " is : " + reqList.size());
		List<JSONObject> respList = new ArrayList<JSONObject>();
		//if (reqList.size() == 1)
			//return reqList;
		String sessKey = (String) data.getSessionData(sessionKey);
		data.addToLog(data.getCurrentElement(), sessionKey + " value from session : " + sessKey);
		for (JSONObject tempObj : reqList) {
			if (Constants.EmptyString.equalsIgnoreCase(tempObj.get(routingRespKey).toString()) && sessKey.equals(Constants.English)) {
				data.addToLog(data.getCurrentElement(), "Adding empty lang entry");
				respList.add(tempObj);
			} else {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(sessKey)) {
					respList.add(tempObj);
				}
			}
		}
		data.addToLog(data.getCurrentElement(),"Routing resp list size post sorting with " + sessionKey + " : " + respList.size());
		return respList;
	}
	
	@SuppressWarnings("unused")
	private Set<JSONObject> routingSortingStateGroup(List<JSONObject> reqList, String sessionKey,String routingRespKey, DecisionElementData data) {
		data.addToLog(data.getCurrentElement(),"Routing reqList size before sorting with " + sessionKey + " is : " + reqList.size());
		Set<JSONObject> respSet = new HashSet<JSONObject>();
		Set<JSONObject> respSetEmpty = new HashSet<JSONObject>();
		//if (reqList.size() == 1)
			//return reqList;

		try {
			@SuppressWarnings("unchecked")
			List<String> stateGroups = (List<String>) data.getSessionData(sessionKey);
			if (stateGroups != null) {
				data.addToLog(data.getCurrentElement(), "State groups : " + stateGroups);		
				for (Iterator<String> iterator = stateGroups.iterator(); iterator.hasNext();) {
					String stateGroupName = iterator.next();
					data.addToLog(data.getCurrentElement(), "State group for check : " + stateGroupName);
					for (JSONObject tempObj : reqList) {
						if(tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.EmptyString)) {
							data.addToLog(data.getCurrentElement(), "Empty state populated");
							respSetEmpty.add(tempObj);
						} else {
							if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(stateGroupName)) {
								data.addToLog(data.getCurrentElement(), "State group matched and added : " + stateGroupName);
								respSet.add(tempObj);
							}	
						}
					}
				}
			} else {
				data.addToLog(data.getCurrentElement(), "State group is null");
				for (JSONObject tempObj : reqList) {
					if(Constants.EmptyString.equalsIgnoreCase(tempObj.get(routingRespKey).toString())) {
						data.addToLog(data.getCurrentElement(), "Empty state group added to resp");
						respSet.add(tempObj);
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in State group sorting ");
			if (respSet.size() == 0) {
				return new HashSet<JSONObject>(reqList);
			} else {
				return respSet;
			}
		}
		if(respSet.isEmpty()) {
			respSet = respSetEmpty;
		}
		
		data.addToLog(data.getCurrentElement(), "Routing resp list size post sorting with " + sessionKey + " : " + respSet.size());
		return respSet;
	}

}
