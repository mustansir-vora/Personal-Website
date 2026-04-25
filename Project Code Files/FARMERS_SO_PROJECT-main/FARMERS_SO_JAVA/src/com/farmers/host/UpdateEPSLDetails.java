package com.farmers.host;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.UpdateEmployeeServiceLineDetails;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class UpdateEPSLDetails extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		JSONObject reportesl_PTO = null;
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String strIEX_ID = Constants.EmptyString;
		String loginid=Constants.EmptyString;
		String leaveType=Constants.EmptyString;
		String leavefullorpartial=Constants.EmptyString;
		String leavestartdatetime=Constants.EmptyString;
		String leaveenddatetime=Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			loginid=(String)data.getSessionData(Constants.S_AGENTID);
			strIEX_ID = (String) data.getSessionData(Constants.S_IEXUSERID);
			leaveType = (String) data.getElementData("EPSL_MN_005_Call","Return_Value");
			leavefullorpartial=(String) data.getElementData("EPSL_MN_006_Call","Return_Value");
			data.addToLog(currElementName, "ESPL_MN_005 Value :: " + leaveType + " :: ESPL_MN_006_Value :: " + leavefullorpartial);

			if(leavefullorpartial.equalsIgnoreCase("Partial Day")) {
				String strReturnValue1=(String) data.getSessionData(Constants.S_LEAVESTARTTIME);
				String strReturnValue2=(String) data.getSessionData(Constants.S_LEAVEENDTIME);
				data.addToLog(currElementName, "Date Start Time :: "+ strReturnValue1 + " :: Date End Time :: " + strReturnValue2);
				leavestartdatetime=getcurrentdatetime(strReturnValue1,data);
				leaveenddatetime=getcurrentdatetime(strReturnValue2, data);
			}
			if(leavefullorpartial.equalsIgnoreCase("Full Day"))
			{
//				LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalDateTime.MIN.toLocalTime());			       
//				LocalDateTime endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalDateTime.MAX.toLocalTime());
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//				String formattedDate1 = dateFormat.format(startTime);
//				String formattedDate2 = dateFormat.format(endTime);
//				data.addToLog(currElementName, "Date Start Time :: "+formattedDate1 + " :: Date End Time :: " + formattedDate2);
//				leavestartdatetime=currentdatetime(formattedDate1,data);
//				leaveenddatetime=currentdatetime(formattedDate2, data);
				LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalDateTime.MIN.toLocalTime());
		        LocalDateTime endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalDateTime.MAX.toLocalTime());
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		        String startString = startTime.format(formatter);
		        String endString = endTime.format(formatter);
				data.addToLog(currElementName, "Date Start Time :: "+startString + " :: Date End Time :: " + endString);
				leavestartdatetime=startString;
				leaveenddatetime=endString;

			}
			if(data.getSessionData(Constants.S_UPDATE_EPSL_DETAILS_URL)  != null  &&  data.getSessionData(Constants.S_AGENTID)!=null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url=(String)data.getSessionData(Constants.S_UPDATE_EPSL_DETAILS_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				loginid=(String)data.getSessionData(Constants.S_AGENTID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				UpdateEmployeeServiceLineDetails test=new UpdateEmployeeServiceLineDetails();
				JSONObject resp=(JSONObject) test.start(url, callerId,loginid,Constants.S_LEAVEAPPLIED_1, conTimeout, readTimeout, context);
				strRespBody = resp.toString();
				data.addToLog(currElementName, "UpdateEPSLDetails API response  :"+resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set EPSL_HOST_002  UpdateEPSLDetails Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
					}
					else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}

			}
			reportesl_PTO = new JSONObject();
			reportesl_PTO.put("AgentID", loginid);
			reportesl_PTO.put("IEX_ID", strIEX_ID);
			reportesl_PTO.put("LeaveType", leaveType);
			reportesl_PTO.put("LeaveFullOrPartial", leavefullorpartial);
			reportesl_PTO.put("LeaveStartDateTime",leavestartdatetime);
			reportesl_PTO.put("LeaveEndDateTime",leaveenddatetime);
			data.addToLog(data.getCurrentElement(), "Report ESL_PTO :: " + reportesl_PTO);
		}	
		catch(Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for UpdateEPSLDetails API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"UpdateEPSLDetails API", strReqBody, region,(String)data.getSessionData(Constants.S_UPDATE_EPSL_DETAILS_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			
			
			SetHostDetails objHostDetails1 = new SetHostDetails(caa);
			objHostDetails.setinitalValue();
			
			objHostDetails1.startHostReport(currElementName,"ReportESL_PTO_REQUEST", reportesl_PTO.toJSONString(),region, "");
			objHostDetails1.endHostReport(data,strRespBody , "SU", "200","");
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for UpdateEPSLDetails API call  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {	
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			boolean result = (boolean) resp.get("res");
			if(!result)strExitState = Constants.ER;
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
/*	public static String currentdatetime(String str, DecisionElementData data) {
		data.addToLog(data.getCurrentElement(), "String value for this function :: " + str);
		String[] timeParts = str.split(":");
		int hours = Integer.parseInt(timeParts[0]);
		int minutes = Integer.parseInt(timeParts[1]);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,hours);
		calendar.set(Calendar.MINUTE, minutes);
		Date currentCustomStartDate1 = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String formattedDate = dateFormat.format(currentCustomStartDate1);
		return formattedDate;
	}	*/
	
	
	public static String getcurrentdatetime(String str, DecisionElementData data) {
		  data.addToLog(data.getCurrentElement(), "String value for this function :: " + str);
		  System.out.println("String value for this function :: " + str);
		
		  // Parse time string (format HH:mm)
		  String[] timeParts = str.split(":");
		  int hours = Integer.parseInt(timeParts[0]);
		  int minutes = Integer.parseInt(timeParts[1]);

		  // Create a new Calendar for CST with time from parsed string
		  Calendar cstCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")); // America/Chicago is considered CST Time
		  cstCalendar.set(Calendar.YEAR, cstCalendar.get(Calendar.YEAR));
		  cstCalendar.set(Calendar.MONTH, cstCalendar.get(Calendar.MONTH));
		  cstCalendar.set(Calendar.DAY_OF_MONTH, cstCalendar.get(Calendar.DAY_OF_MONTH));
		  cstCalendar.set(Calendar.HOUR_OF_DAY, hours);
		  cstCalendar.set(Calendar.MINUTE, minutes);
		  cstCalendar.set(Calendar.SECOND, cstCalendar.get(Calendar.SECOND));

		  //Convert in format (yyyy-MM-dd'T'HH:mm:ss)
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		  dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago")); // Adjust ZoneId for specific CST location
		  String formattedDate = dateFormat.format(cstCalendar.getTime());

		  return formattedDate;
		}
}
