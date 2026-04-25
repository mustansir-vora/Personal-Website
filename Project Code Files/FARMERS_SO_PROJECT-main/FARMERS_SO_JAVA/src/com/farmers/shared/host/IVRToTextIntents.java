package com.farmers.shared.host;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.servion.farmers.IVR2TextDecider;


public class IVRToTextIntents extends DecisionElementBase {


	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		try {
			data.addToLog(currElementName, "Entered");
			data.addToLog(currElementName, "S_IVR2TEXT_INTENT_URL"+(String) data.getSessionData(Constants.S_IVR2TEXT_INTENT_URL));
			if(data.getSessionData(Constants.S_IVR2TEXT_INTENT_URL) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_IVR2TEXT_INTENT_URL);
				String intent = (String) data.getSessionData(Constants.APPTAG);
				String callid =  (String) data.getSessionData(Constants.S_CALLID);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog(currElementName, "wsurl : "+wsurl);
				data.addToLog(currElementName, "intent : "+intent);

				Lookupcall lookups = new Lookupcall();
				org.json.simple.JSONObject resp = lookups.GetIVRTOTEXTINTENT(wsurl, callid, Constants.tenantid, intent, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout),context);
				data.addToLog(currElementName, "IVRToTextIntents API response  :"+resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set IVRToTextIntents  Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody, intent);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch(Exception e) {
			data.addToLog(currElementName,"Exception in IVRToTextIntents API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName,"IVRToTextIntents", strReqBody,"", (String) data.getSessionData(Constants.S_IVR2TEXT_INTENT_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for IVRToTextIntents API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;

	}

	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, String intent) {
		String strExitState = Constants.ER, lob = Constants.EmptyString, ivr2textBU = Constants.EmptyString, timeAndAllocation = Constants.EmptyString, isDisable = Constants.EmptyString;
		String timeWindow = Constants.EmptyString,allocation = Constants.EmptyString, strIVR2TextBUVal = Constants.EmptyString;
		Map<String, ArrayList<String>> mapTimeAllocation = null;
		ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
		ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
		boolean isWithinTimePeriod = false, isWithinAllocation = false;
		try {
			lob = (String) data.getSessionData(Constants.S_BU);
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			data.addToLog(currElementName,"IVRTOTEXT Admin API resp size :"+resArr.size());
			if(null == resArr || resArr.size() == 0) {
				return Constants.ER;
			}else {
				if(resArr.size()==1) {
					JSONObject	tempObj = (JSONObject) resArr.get(0);
					ivr2textBU = (String) tempObj.get("businessunit");
					if(strBristolCode.contains(lob)) {
						strIVR2TextBUVal = "Bristol West";
					}else if(strFarmersCode.contains(lob)) {
						strIVR2TextBUVal = "Farmers";
					}else {
						// LOB doesnt match
					}
					data.addToLog(currElementName,"LOB :"+ lob + " | " + "ivr2textBU :  " + ivr2textBU + " | " + " strIVR2TextBUVal : " + strIVR2TextBUVal);

					//lob - bristol west ivr2text - farmers 1 , bristol west 2
					if((ivr2textBU.equalsIgnoreCase(strIVR2TextBUVal))) {
						if(tempObj.containsKey("isdisable") && null != tempObj.get("isdisable")) {
							isDisable = (String) tempObj.get("isdisable");
							data.addToLog(currElementName,"Is Disable : " + isDisable);
							if(null != isDisable && isDisable.equalsIgnoreCase("1"))  return Constants.SU;
							timeAndAllocation = (String) tempObj.get("allocation");
							mapTimeAllocation = getTimeAllocation(data, caa, currElementName, timeAndAllocation);
							data.addToLog(currElementName,"Map of TimePeriod and Allocation : " + mapTimeAllocation);
							/**
							 * Check Time Period
							 */
							ArrayList<String> getTimeAllocation;
							ZoneId objZoneId = ZoneId.of("America/Chicago");
							ZonedDateTime objZonedDateTime = ZonedDateTime.now(objZoneId);
							LocalDate date  = objZonedDateTime.toLocalDate();
							int dayWeek = date.getDayOfWeek().getValue();

							data.addToLog(currElementName,"Current Day : " + date.getDayOfWeek().getValue());
							data.addToLog(currElementName,"Day of the Week - Formatted FULL :: "
									+ date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));

							String dayofWeekName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()).substring(0,3).toUpperCase();
							if(mapTimeAllocation!=null) {
								getTimeAllocation = new ArrayList<String>();
								getTimeAllocation = mapTimeAllocation.get(dayofWeekName);
								timeWindow = getTimeAllocation.get(0);
								allocation = getTimeAllocation.get(1);
								data.addToLog(currElementName,"Time Window : " + timeWindow + " | " + "Allocation : " + allocation);
							}else {
								data.addToLog(currElementName,"Map Allocation is null or empty ");
							}


							//Check is current time within time period

							isWithinTimePeriod = checkTimePeriod(data, caa, currElementName, timeWindow);
							data.addToLog(currElementName,"Is Within time window: " + isWithinTimePeriod );

							//Check is with in Allocation
							IVR2TextDecider decider = new IVR2TextDecider();
							isWithinAllocation = decider.offerIvr2Text(intent, allocation, data);

							data.addToLog(currElementName,"Is Within time Allocation: " + isWithinAllocation );

							if(null != isDisable && isDisable.equalsIgnoreCase("0") && isWithinTimePeriod && isWithinAllocation) {
								data.setSessionData(Constants.IS_ELIGIBLE_INTENT, Constants.STRING_YES);
							}
							//if(!(((String)tempObj.get("isdisable")).equals("1"))) data.setSessionData(Constants.IS_ELIGIBLE_INTENT, Constants.STRING_YES);
						}
						strExitState = Constants.SU;
					}else {
						data.addToLog(currElementName,"IVRTOTEXT Business not mapped to Bristol or Farmers :");
					}

					data.addToLog(currElementName, " IS_ELIGIBLE_INTENT : "+data.getSessionData(Constants.IS_ELIGIBLE_INTENT));

				}else if(resArr.size()==2) {
					if(strBristolCode.contains(lob)) {
						strIVR2TextBUVal = "Bristol West";
					}else if(strFarmersCode.contains(lob)) {
						strIVR2TextBUVal = "Farmers";
					}else {
						// LOB doesnt match
					}
					data.addToLog(currElementName,"LOB :"+ lob + " | " + "ivr2textBU :  " + ivr2textBU + " | " + " strIVR2TextBUVal : " + strIVR2TextBUVal);

					for (int i = 0; i < resArr.size(); i++) {
						JSONObject	tempObj = (JSONObject) resArr.get(i);
						ivr2textBU = (String) tempObj.get("businessunit");

						data.addToLog(currElementName, " IVR to Text BU : " + i + " : " + ivr2textBU);
						if(ivr2textBU.equalsIgnoreCase(strIVR2TextBUVal)) {
							if(tempObj.containsKey("isdisable") && null != tempObj.get("isdisable")) {
								isDisable = (String) tempObj.get("isdisable");
								data.addToLog(currElementName,"Is Disable : " + isDisable);
								if(null != isDisable && isDisable.equalsIgnoreCase("1"))  return Constants.SU;
								timeAndAllocation = (String) tempObj.get("allocation");
								mapTimeAllocation = getTimeAllocation(data, caa, currElementName, timeAndAllocation);
								data.addToLog(currElementName,"Map of TimePeriod and Allocation : " + mapTimeAllocation);
								/**
								 * Check Time Period
								 */
								ArrayList<String> getTimeAllocation;
								ZoneId objZoneId = ZoneId.of("America/Chicago");
								ZonedDateTime objZonedDateTime = ZonedDateTime.now(objZoneId);
								LocalDate date  = objZonedDateTime.toLocalDate();
								int dayWeek = date.getDayOfWeek().getValue();

								data.addToLog(currElementName,"Current Day : " + date.getDayOfWeek().getValue());
								data.addToLog(currElementName,"Day of the Week - Formatted FULL :: "
										+ date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
								
								String dayofWeekName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()).substring(0,3).toUpperCase();
								if(mapTimeAllocation!=null) {
									getTimeAllocation = new ArrayList<String>();
									getTimeAllocation = mapTimeAllocation.get(dayofWeekName);
									timeWindow = getTimeAllocation.get(0);
									allocation = getTimeAllocation.get(1);
									data.addToLog(currElementName,"Time Window : " + timeWindow + " | " + "Allocation : " + allocation);
								}else {
									data.addToLog(currElementName,"Map Allocation is null or empty ");
								}

								//Check is current time within time period
								isWithinTimePeriod = checkTimePeriod(data, caa, currElementName, timeWindow);
								data.addToLog(currElementName,"Is Within time window: " + isWithinTimePeriod );

								//Check is with in Allocation
								IVR2TextDecider decider = new IVR2TextDecider();
								isWithinAllocation = decider.offerIvr2Text(intent, allocation, data);

								data.addToLog(currElementName,"Is Within time Allocation: " + isWithinAllocation );

								if(null != isDisable && isDisable.equalsIgnoreCase("0") && isWithinTimePeriod && isWithinAllocation) {
									data.setSessionData(Constants.IS_ELIGIBLE_INTENT, Constants.STRING_YES);
								}
								//if(!(((String)tempObj.get("isdisable")).equals("1"))) data.setSessionData(Constants.IS_ELIGIBLE_INTENT, Constants.STRING_YES);
							}
							strExitState = Constants.SU;
							break;
						}else {
							data.addToLog(currElementName,"IVRTOTEXT Business not mapped to Bristol or Farmers :");
						}
						data.addToLog(currElementName, " IS_ELIGIBLE_INTENT : "+data.getSessionData(Constants.IS_ELIGIBLE_INTENT));
					}
				}
			}

		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in IVRToTextIntents apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private Map<String, ArrayList<String>> getTimeAllocation(DecisionElementData data,CommonAPIAccess caa, String currElementName, String timetable){
		String timeWindow = null, allocation = null;
		HashMap<String,ArrayList<String>> map = new HashMap<String, ArrayList<String>>();;
		try {
			String[] dayList = timetable.split("\\|");
			String[] timetableList;
			ArrayList<String> timeAllocation;
			for (String day : dayList) {
				data.addToLog("Time Table with allocation : ", day);
				timetableList = day.split(" ");
				timeAllocation = new ArrayList<String>();
				timeAllocation.add(timetableList[1]);
				timeAllocation.add(timetableList[2].replace("%", ""));
				map.put(timetableList[0].toUpperCase(), timeAllocation);
			}
		}catch (Exception e) {
			data.addToLog(currElementName,"Exception in IVRToTextIntents Get Time Allocation method  :: "+e);
			caa.printStackTrace(e);
		}
		return map;

	}

	private boolean checkTimePeriod(DecisionElementData data,CommonAPIAccess caa, String currElementName, String timeWindow){

		boolean retValue = false;
		try {
			if(timeWindow!=null && !"".equalsIgnoreCase(timeWindow)) {
				LocalTime startTime = LocalTime.of(Integer.parseInt(timeWindow.split("\\-")[0].split("\\:")[0]), Integer.parseInt(timeWindow.split("\\-")[0].split("\\:")[1]));
				LocalTime endTime = LocalTime.of(Integer.parseInt(timeWindow.split("\\-")[1].split("\\:")[0]), Integer.parseInt(timeWindow.split("\\-")[1].split("\\:")[1]));

				ZoneId objZoneId = ZoneId.of("America/Chicago");
				ZonedDateTime objZonedDateTime = ZonedDateTime.now(objZoneId);

				LocalTime timeNow = objZonedDateTime.toLocalTime();

				data.addToLog(currElementName,"Time Window pa ssed : " + timeWindow);
				data.addToLog(currElementName,"Current Time : " + timeNow);

				retValue = (
						(timeNow.isAfter(startTime) || timeNow.equals(startTime)) && 
						(timeNow.isBefore(endTime) || timeNow.equals(endTime)));

				data.addToLog(currElementName,"Is Current time is within the time period : " + retValue);
			}

		}catch (Exception e) {
			data.addToLog(currElementName,"Exception in IVRToTextIntents Check TimePeriod method  :: "+e);
			caa.printStackTrace(e);
		}
		return retValue;

	}
}
