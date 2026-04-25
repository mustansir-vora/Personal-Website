package com.farmers.start;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.proxy.EndCallInterface;
import com.audium.server.session.CallEndAPI;
import com.audium.server.session.DecisionElementData;
import com.farmers.AdminAPI.GetBusinessObjectsByDnisKey;
import com.farmers.AdminAPI.GetMenuSelectionPropertyTableByKey;
import com.farmers.VerintAPI.VerintPauseResume;
import com.farmers.report.CallData;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/**
 * The On End Call Class is called if the caller hung up, the application hung
 * up on the caller, the session was invalidated, or the sesion timed out on its
 * own (due to some error).
 */

public class CallEnd  implements EndCallInterface {

	/**
	 * All On End Call classes must implement this method. Use the passed
	 * CallEndAPI class to get useful information. Making changes here will
	 * do nothing as the call has already ended.
	 */

	String strElementName ="";
	String strCallEndDate = "";
	String strLang = "";
	String strCallResult = "";
	String strCallEndReason = "";
	String strSelfService = "";

	DateFormat ctiDateFormat = null;
	String strCTIDateFormat = "dd/MM/yyyy HH:mm:ss";
	
	HashMap<String, String> headerDetails = null;

	@SuppressWarnings("unchecked")
	public void onEndCall(CallEndAPI callEndAPI) throws AudiumException 
	{
		String strCallId = "";
		String strDNIS = "";
		String strCLI = "";
		long lCallStartTime = 0L;

		strElementName = this.getClass().getSimpleName();
		StringBuffer sbLog = new StringBuffer();
		CommonAPIAccess caa = null;	
		String strCallerInput="";
		caa = CommonAPIAccess.getInstance(callEndAPI);
		
			
		//Contain Module values set into CVP Reporting server header
		/*
		try {
			headerDetails = (HashMap<String, String>) callEndAPI.getSessionData("S_HEADER_DETAILS");
			callEndAPI.addToLog("headerDetails size : ", ""+headerDetails.size());
			if(headerDetails != null) {
				callEndAPI.addToLog("S_ContainableCount", headerDetails.get("S_ContainableCount") != null ? (headerDetails.get("S_ContainableCount")) : "0");
				callEndAPI.addToLog("S_ContainedCount", headerDetails.get("S_ContainedCount") != null ? (headerDetails.get("S_ContainedCount")) : "0");
				callEndAPI.addToLog("S_PartiallyContainedCount", headerDetails.get("S_PartiallyContainedCount") != null ? (headerDetails.get("S_PartiallyContainedCount")) : "0");
			} else {
				callEndAPI.addToLog("callEndAPI", "headerDetails are null");
			}
		} catch (Exception e) {
			caa.printStackTrace(e);
		}
		*/
		
		//Setting Call_Outcome, Transfer Reason Reason values here. These values are being formed in report logger hence copying the same logic here.
		String call_Outcome = Constants.EmptyString;
		String transfer_Reason = Constants.EmptyString;
		String agent_Transfer_Flag = Constants.EmptyString;
		String containableCount = Constants.EmptyString;
		String containedCount = Constants.EmptyString;
		String partiallyContainedCount = Constants.EmptyString;
		String iaDisconnectFlag = Constants.EmptyString;
		String str_callEndReason = Constants.EmptyString;
		String strCurrentElementName = Constants.EmptyString;
		String strErrReason = Constants.EmptyString;
		String originalIntent = Constants.EmptyString;
		String finalIntent = Constants.EmptyString;
		String phoneType = "NA";
		String strStartTime = Constants.EmptyString;
		String strCallEndTime = Constants.EmptyString;
		String duration = Constants.EmptyString;
		String FailOut_Rep_Handling_Flag = Constants.EmptyString;
		String Rep_Handling_Flag = Constants.EmptyString;
		String strNLUFailureFlag=Constants.EmptyString;
		String NoInputFlag= Constants.EmptyString;
       //START:GDF Connectivity Issue retries changes
		String HotEventTransferFlag = Constants.EmptyString;
	    //End:GDF Connectivity Issue retries changes
		
		String paymentUsFlag="";
		String ivrtoText ="";
		try {
			
			String isMobileCaller = (String)callEndAPI.getSessionData(Constants.IS_MOBILE_CALLER);
			
			if (null != isMobileCaller && isMobileCaller.equalsIgnoreCase(Constants.STRING_YES)) {
				phoneType = "Mobile";
			}
			else if (null != isMobileCaller && isMobileCaller.equalsIgnoreCase(Constants.STRING_NO)) {
				phoneType = "Telephone";
			}
			
			headerDetails = (HashMap<String, String>) callEndAPI.getSessionData("S_HEADER_DETAILS");
			containableCount = null != headerDetails.get("S_ContainableCount") ? headerDetails.get("S_ContainableCount") : "0";
			containedCount = null != headerDetails.get("S_ContainedCount") ? headerDetails.get("S_ContainedCount") : "0";
			partiallyContainedCount = null != headerDetails.get("S_PartiallyContainedCount") ? headerDetails.get("S_PartiallyContainedCount") : "0";
			String lastfunctionresultRepcheck=null!=headerDetails.get("RPT_Last_Function_Result")? headerDetails.get("RPT_Last_Function_Result") : "NA";
			agent_Transfer_Flag = (String) callEndAPI.getSessionData("S_TRANSFER_FLAG");
			iaDisconnectFlag = (String) callEndAPI.getSessionData("IA_DISCONNECT_FLAG");
			FailOut_Rep_Handling_Flag= (String) callEndAPI.getSessionData(Constants.S_FAILOUT_REP_HANDLING_COUNTER_FLAG);
			Rep_Handling_Flag = (String) callEndAPI.getSessionData(Constants.S_REP_HANDLING_COUNTER_FLAG);
			//START:GDF Connectivity Issue retries changes
			HotEventTransferFlag = (String)callEndAPI.getSessionData("S_HOTEVENT_TRANSFER");
			//END:GDF Connectivity Issue retries changes
			if (null != iaDisconnectFlag && "TRUE".equalsIgnoreCase(iaDisconnectFlag) && Integer.parseInt(partiallyContainedCount) > 0) {
				partiallyContainedCount = String.valueOf(Integer.parseInt(partiallyContainedCount) - 1) ;
			}
			
			//Setting Call_Outcome here
			call_Outcome = callEndAPI.getHowCallEnded().toUpperCase();
			callEndAPI.addToLog("How Call Ended :: ", call_Outcome);
			callEndAPI.addToLog(Rep_Handling_Flag, "Rep_Handling_Flag : " + Rep_Handling_Flag);
			

			callEndAPI.addToLog(FailOut_Rep_Handling_Flag, "FailOut_Rep_Handling_Flag : " + FailOut_Rep_Handling_Flag);
			
			paymentUsFlag = (String)callEndAPI.getSessionData("S_PAYMENTUS_CALLOUTCOME");
			ivrtoText =  (String)callEndAPI.getSessionData("S_IVRTOTEXT_CALLOUTCOME");
			if("Y".equals(strNLUFailureFlag)) {
				
				 call_Outcome = "No Input Disconnect";
				 NoInputFlag="Y";
				}
			if("Y".equals(agent_Transfer_Flag))
			{
				call_Outcome = "TRANSFER";
			}
			
			if("Y".equals(agent_Transfer_Flag) && null != (String) callEndAPI.getSessionData("RLUS_FLAG") && "Y".equalsIgnoreCase((String) callEndAPI.getSessionData("RLUS_FLAG")))
			{
				call_Outcome = "RLUS Transfer";
			}
			
			if("Y".equals(agent_Transfer_Flag) && null != (String) callEndAPI.getSessionData("ZINNIA_FLAG") && "TRUE".equalsIgnoreCase((String) callEndAPI.getSessionData("ZINNIA_FLAG")))
			{
				call_Outcome = "ZINNIA Transfer";
			}
			
			if("Y".equals(agent_Transfer_Flag) && null != (String) callEndAPI.getSessionData("PAYMENTUS_FLAG") && "Y".equalsIgnoreCase((String) callEndAPI.getSessionData("PAYMENTUS_FLAG")))
			{
				call_Outcome = "PaymentUS Transfer";
			}
			
			if("Y".equals(agent_Transfer_Flag) && null != (String) callEndAPI.getSessionData("PAYMENTUS_FLAG") && "N".equalsIgnoreCase((String) callEndAPI.getSessionData("PAYMENTUS_FLAG")))
			{
				call_Outcome = "PaymentUS Transfer Fail";
			}
			
			if("APP_SESSION_COMPLETE".equals(call_Outcome) && !("Y".equals(agent_Transfer_Flag)))
			{
				call_Outcome = "IVR DISCONNECT";
			}
			//START - CS1240948 :: Set Call Outcome
			if (null != (String)callEndAPI.getSessionData("NoInput") && "3".equalsIgnoreCase((String)callEndAPI.getSessionData("NoInput")) && (call_Outcome.equalsIgnoreCase("IVR DISCONNECT") || call_Outcome.equalsIgnoreCase("DISCONNECT"))) {
				call_Outcome = "NO INPUT DISCONNECT";
				NoInputFlag="Y";
			}
			//End - CS1240948 :: Set Call Outcome
			
			if(("Y".equals(Rep_Handling_Flag) || ("Representative".equals(lastfunctionresultRepcheck)) || ("representative-request".equals(finalIntent))|| ("representativerequest".equals(finalIntent))) && ("Y".equals(agent_Transfer_Flag))) 
			{
				call_Outcome = "VOLUNTARY TRANSFER";
			}
			
			if(("Y".equals(FailOut_Rep_Handling_Flag) && ("Y".equals(agent_Transfer_Flag))))
			{
				call_Outcome = "ERROR TRANSFER";
			}
			
			if("Y".equals(HotEventTransferFlag))
			{
				call_Outcome = "ERROR TRANSFER";
			}
			
			if(null != iaDisconnectFlag && "TRUE".equalsIgnoreCase(iaDisconnectFlag) && (call_Outcome.equalsIgnoreCase("DISCONNECT") || call_Outcome.equalsIgnoreCase("IVR DISCONNECT"))) {
				call_Outcome = "IA DISCONNECT";
			}
			
			
			if (Integer.parseInt(containableCount) > 0 && Integer.parseInt(containedCount) > 0 && Integer.parseInt(containableCount) == Integer.parseInt(containedCount) && partiallyContainedCount.equalsIgnoreCase("0") && call_Outcome.equalsIgnoreCase("HANGUP")) {
				call_Outcome = "COMPLETED";
			}
			
			if (Integer.parseInt(containableCount) > 0 && Integer.parseInt(containedCount) > 0 && Integer.parseInt(containableCount) == Integer.parseInt(containedCount) && partiallyContainedCount.equalsIgnoreCase("0") && null != callEndAPI.getSessionData("EMP_SERV_COMPLETED") && "TRUE".equalsIgnoreCase((String) callEndAPI.getSessionData("EMP_SERV_COMPLETED"))) {
				
				call_Outcome = "COMPLETED";
			
			}
			//Start Balaji K- CS1327384 - Farmers Insurance  US  Call Outcome for Payment US Transfers
			if("Y".equals(paymentUsFlag) && !call_Outcome.equalsIgnoreCase("HANGUP")) {
				call_Outcome = "PAYMENTUS TRANSFER";
			}
			//End Balaji K-CS1327384 - Farmers Insurance  US  Call Outcome for Payment US Transfers
			if("Y".equals(ivrtoText) && !call_Outcome.equalsIgnoreCase("HANGUP")) {
				call_Outcome = "IVR TO TEXT";
			}
			//Setting Transfer_Reason here
			strCurrentElementName = callEndAPI.getCurrentElement();
			
			//Error Reason manipulation
			str_callEndReason = callEndAPI.getCallResult().toUpperCase();
			if("ERROR".equals(str_callEndReason) || "TIMEOUT".equals(str_callEndReason)){
				String strTmpErrReason = str_callEndReason + " @ '" + strCurrentElementName + "'";
				if("".equals(strErrReason)){
					strErrReason = strTmpErrReason;
				}else{
					strErrReason = strErrReason + " | " + strTmpErrReason;
				}
			}

			
			if(transfer_Reason==null || "".equals(transfer_Reason)) {
				if(strErrReason!=null && (strErrReason.contains("ERROR") ||strErrReason.contains("error"))) {
					transfer_Reason = "Call transfer due to host failure";
				}else if ("Y".equalsIgnoreCase(NoInputFlag)){
					transfer_Reason = "Disconnect";
				}
				//START:GDF Connectivity Issue retries changes
				else if("Y".equalsIgnoreCase(HotEventTransferFlag)) {
					transfer_Reason = "GDF RETRY FAILURE";
				}
				//END:GDF Connectivity Issue retries changes
				else {
					transfer_Reason = "Normal call transfer";
				}
			}
			
			if("Y".equalsIgnoreCase((String) callEndAPI.getSessionData("S_DR"))) {
				transfer_Reason = "DR transfer due to IVR failure";
			}
			
			//Setting original & final intent as App tag value if values are empty from session
			//originalIntent = (String) callEndAPI.getSessionData(Constants.APPTAG);
			originalIntent = (String) callEndAPI.getSessionData("NLCL_MN_001_VALUE");
			finalIntent = (String) callEndAPI.getSessionData(Constants.S_FINAL_INTENT);
			
			if (null == originalIntent || "".equalsIgnoreCase(originalIntent)) {
				originalIntent = (String) callEndAPI.getSessionData(Constants.S_INTENT);
			}
			if (null == finalIntent || "".equalsIgnoreCase(finalIntent)) {
				finalIntent = (String) callEndAPI.getSessionData(Constants.S_INTENT);
			}
			if (null == finalIntent || "".equalsIgnoreCase(finalIntent)) {
				finalIntent = (String) callEndAPI.getSessionData(Constants.APPTAG);
			}
			
			
			//Getting Start Time From Event & End time is current time. Calculating Difference using custom logic
			strStartTime = getDate("yyyy-MM-dd HH:mm:ss", callEndAPI.getStartDate());
			callEndAPI.addToLog("StartTime :: ", strStartTime);
			strStartTime = convertTimetoCT(strStartTime, callEndAPI);
			callEndAPI.addToLog("StartTime in CST :: ", strStartTime);
			strCallEndTime = getDate("yyyy-MM-dd HH:mm:ss", new Date());
			callEndAPI.addToLog("EndTime :: ", strCallEndTime);
			strCallEndTime = convertTimetoCT(strCallEndTime, callEndAPI);
			callEndAPI.addToLog("EndTime in CST :: ", strCallEndTime);
			
			duration = calculateDuration(strStartTime, strCallEndTime, callEndAPI);
			callEndAPI.addToLog("Duration in seconds :: ", duration);
		}
		catch (Exception e) {
			caa.printStackTrace(e);
		}
		
		//Setting header details in to vxml custom content for eGain reports
		try {
			callEndAPI.addToLog("RPT_Ivr_Traversal_Path",headerDetails.get("RPT_Ivr_Traversal_Path") != null ? headerDetails.get("RPT_Ivr_Traversal_Path") : "NA");
			callEndAPI.addToLog("RPT_Call_HeaderID",(String)callEndAPI.getSessionData(Constants.S_CALLID) + "_" + callEndAPI.getSessionId());
			callEndAPI.addToLog("RPT_CallID",(String)callEndAPI.getSessionData(Constants.S_CALLID));
			callEndAPI.addToLog("RPT_SessionID",callEndAPI.getSessionId());
			callEndAPI.addToLog("RPT_Original_DNIS",(String)callEndAPI.getSessionData(Constants.S_ORIGINAL_DNIS));
			callEndAPI.addToLog("RPT_Final_DNIS",(String)callEndAPI.getSessionData(Constants.S_FINAL_DNIS));
			callEndAPI.addToLog("RPT_Original_DNIS_Group",(String)callEndAPI.getSessionData(Constants.S_ORIGINAL_DNIS_GROUP));
			callEndAPI.addToLog("RPT_Final_DNIS_Group",(String)callEndAPI.getSessionData(Constants.S_FINAL_DNIS_GROUP));
			callEndAPI.addToLog("RPT_Original_Category",(String)callEndAPI.getSessionData(Constants.S_ORIGINAL_CATEGORY));
			callEndAPI.addToLog("RPT_Final_Category",(String)callEndAPI.getSessionData(Constants.S_FINAL_CATEGORY));
			callEndAPI.addToLog("RPT_Original_LOB",(String)callEndAPI.getSessionData(Constants.S_ORIGINAL_LOB));
			callEndAPI.addToLog("RPT_Final_LOB",(String)callEndAPI.getSessionData(Constants.S_FINAL_LOB));
			callEndAPI.addToLog("RPT_Original_ANI",(String)callEndAPI.getSessionData(Constants.S_ORIGINAL_ANI));
			callEndAPI.addToLog("RPT_Final_ANI",(String)callEndAPI.getSessionData(Constants.S_FINAL_ANI));
			callEndAPI.addToLog("RPT_ANI_Exists",(String)callEndAPI.getSessionData(Constants.S_ANI_EXIST));
			callEndAPI.addToLog("RPT_Original_Intent",originalIntent);
			callEndAPI.addToLog("RPT_Final_Intent",finalIntent);
			callEndAPI.addToLog("RPT_Original_State_Group",(String)callEndAPI.getSessionData(Constants.S_ORIGINAL_STATE_GROUP));
			callEndAPI.addToLog("RPT_Final_State_Group",(String)callEndAPI.getSessionData(Constants.S_FINAL_STATE_GROUP));
			callEndAPI.addToLog("RPT_Original_Language",(String)callEndAPI.getSessionData(Constants.S_ORIGINAL_LANGUAGE));
			callEndAPI.addToLog("RPT_Final_Language",(String)callEndAPI.getSessionData(Constants.S_FINAL_LANG));
			callEndAPI.addToLog("RPT_Hours_Of_Operation",(String)callEndAPI.getSessionData(Constants.S_CALLCENTER_OPEN_CLOSED));
			callEndAPI.addToLog("RPT_Start_Time",strStartTime);
			callEndAPI.addToLog("RPT_End_Time",strCallEndTime);
			callEndAPI.addToLog("RPT_Transfer_Reason",transfer_Reason);
			callEndAPI.addToLog("RPT_Call_Outcome",call_Outcome);
			callEndAPI.addToLog("RPT_Route_Rule",(String)callEndAPI.getSessionData(Constants.S_ROUTING_KEY));
			callEndAPI.addToLog("RPT_Route_Action",(String)callEndAPI.getSessionData(Constants.S_ROUTING_TRANSFER_ACTION));
			callEndAPI.addToLog("RPT_Route_Destination",(String)callEndAPI.getSessionData(Constants.S_FINAL_DESTNUM));
			callEndAPI.addToLog("RPT_Duration",duration);
			callEndAPI.addToLog("RPT_Transfer_Number",(String)callEndAPI.getSessionData(Constants.S_FINAL_DESTNUM));
			callEndAPI.addToLog("RPT_State",(String)callEndAPI.getSessionData(Constants.S_STATENAME));
			callEndAPI.addToLog("RPT_Last_Function_Name",headerDetails.get("RPT_Last_Function_Name") != null ? headerDetails.get("RPT_Last_Function_Name") : "NA");
			callEndAPI.addToLog("RPT_Last_Function_Result",headerDetails.get("RPT_Last_Function_Result") != null ? headerDetails.get("RPT_Last_Function_Result") : "NA");
			callEndAPI.addToLog("RPT_Phone_Type",phoneType);
			callEndAPI.addToLog("RPT_Policy_Source",(String)callEndAPI.getSessionData(Constants.S_POLICY_SOURCE));
			callEndAPI.addToLog("RPT_CAT_Code",(String)callEndAPI.getSessionData(Constants.S_CAT_CODE));
			callEndAPI.addToLog("RPT_Transfer_Node",(String)callEndAPI.getSessionData(Constants.S_MENU_SELECTION_KEY));
			callEndAPI.addToLog("RPT_Is_Fully_Authenticated",(String)callEndAPI.getSessionData(Constants.S_KYC_AUTHENTICATED));
			callEndAPI.addToLog("RPT_Ani_Group",(String)callEndAPI.getSessionData(Constants.S_ANI_GROUP));
			callEndAPI.addToLog("RPT_Policy_Number",(String)callEndAPI.getSessionData(Constants.S_POLICY_NUM));
			callEndAPI.addToLog("RPT_Brand",(String)callEndAPI.getSessionData(Constants.S_BU));
			callEndAPI.addToLog("RPT_Ivr_Traversal_Path",headerDetails.get("RPT_Ivr_Traversal_Path") != null ? headerDetails.get("RPT_Ivr_Traversal_Path") : "NA");
			callEndAPI.addToLog("RPT_ContainableCount",headerDetails.get("S_ContainableCount") != null ? headerDetails.get("S_ContainableCount") : "0");
			callEndAPI.addToLog("RPT_ContainedCount",headerDetails.get("S_ContainedCount") != null ? headerDetails.get("S_ContainedCount") : "0");
			callEndAPI.addToLog("RPT_PartiallyContainedCount",headerDetails.get("S_PartiallyContainedCount") != null ? headerDetails.get("S_PartiallyContainedCount") : "0");
			callEndAPI.addToLog("RPT_CallData1","NA");
			callEndAPI.addToLog("RPT_CallData2","NA");
			callEndAPI.addToLog("RPT_CallData3","NA");
			callEndAPI.addToLog("RPT_CallData4",(String)callEndAPI.getSessionData(Constants.S_POLICY_NUM));
			callEndAPI.addToLog("RPT_CallData5","NA");
			callEndAPI.addToLog("RPT_ANI_Match", null != callEndAPI.getSessionData("S_ANI_MATCH") ? (String) callEndAPI.getSessionData("S_ANI_MATCH") : "FALSE");
		} catch (Exception e) {
			caa.printStackTrace(e);
		}

		try
		{
			Object routingDone = callEndAPI.getSessionData("ROUTINGDONE"); 
			verintEnable(callEndAPI);
			String menuSelectionUrl = (String) callEndAPI.getSessionData("S_MSPTABLE_URL");
			String callerId = (String) callEndAPI.getSessionData(Constants.S_CALLID);
			strCallId = (String) callEndAPI.getSessionData(Constants.S_ICMID);
			strDNIS = (String) callEndAPI.getSessionData(Constants.S_DNIS);
			strCLI = (String) callEndAPI.getSessionData(Constants.S_ANI);
			lCallStartTime = Long.parseLong((String)callEndAPI.getSessionData(Constants.S_CALLSTART_TIME));
			int conTimeout = Integer.valueOf((String) callEndAPI.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String) callEndAPI.getSessionData(Constants.S_READ_TIMEOUT));
			String menuSelectionKey = (String) callEndAPI.getSessionData(Constants.S_MENU_SELCTION_KEY);
			LoggerContext context = (LoggerContext) callEndAPI.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			sbLog.append(" | CALLID: ").append(strCallId).append(" | DNIS: ").append(strDNIS).append(" | CLI:").append(strCLI).append(" | RoutingDone:").append(routingDone);

			/* Update to Report CallData Bean for Call End Info */ 
			ctiDateFormat = new SimpleDateFormat(strCTIDateFormat);
			strCallEndDate = ctiDateFormat.format(new Date());

			strCallEndReason= callEndAPI.getHowCallEnded().trim();
			callEndAPI.setSessionData(Constants.S_CALL_END_REASON, strCallEndReason);

			sbLog.append(" | Call End Date: " + strCallEndDate + " | Call End Reason: " + strCallEndReason + " | Call Result: " + strCallResult);
			callEndAPI.setSessionData(Constants.S_CALLEND_TIME, strCallEndDate);
			strCallerInput=(String)caa.getFromSession(Constants.S_CallerInput, Constants.NA);
			sbLog.append(" | strCallerInput: "+strCallerInput);
						
			if(Constants.HANGUP.equalsIgnoreCase(strCallEndReason)){ //just in case of Hangup event
				
				sbLog.append(" | Caller disconnect  " );
			}
			
			if(routingDone == null) {
				String finalDNis = mspTableLookup(menuSelectionUrl, callerId, Constants.tenantid, menuSelectionKey, conTimeout, readTimeout, context, callEndAPI);
					
				if(!finalDNis.equalsIgnoreCase(Constants.EmptyString)) {
					boolean isBOUpdated = updateBO(finalDNis, callEndAPI);
					if (isBOUpdated) {
						String dnis = (String) callEndAPI.getSessionData(Constants.S_FINAL_DNIS);
						String dnisgroup = (String) callEndAPI.getSessionData(Constants.S_FINAL_DNIS_GROUP); 
						String category = (String) callEndAPI.getSessionData(Constants.S_FINAL_CATEGORY);
						String lob = (String) callEndAPI.getSessionData(Constants.S_FINAL_LOB);
						callEndAPI.addToLog(strElementName, "Final BO objects updated, DNIS <" + dnis + ">, DNIS Group <" + dnisgroup + ">, Category <" + category + ">, LOB <" + lob + ">");		
					}
				} else {
					sbLog.append("MSP Entry Missing for : " + menuSelectionKey);
				}
			} else {
				sbLog.append("Routing Done. Skipping MSP");
			}
			
		}
		catch (Exception e) {
			caa.printStackTrace(e);
		}
		finally
		{
			caa=null;
			callEndAPI.addToLog(strElementName, sbLog.toString());
			long lCallEnd = System.currentTimeMillis();
			long lDuration = lCallEnd - lCallStartTime;
			callEndAPI.addToLog(strElementName, "CallDuration: " + lDuration);
			callEndAPI.setSessionData(Constants.S_CALL_DURATION, lDuration);
			long lSeconds = lDuration / 1000;
			long lMinutes = lDuration / (60 * 1000);
			long lHours = lDuration / (60 * 60 * 1000);
			callEndAPI.addToLog(strElementName, "CallDuration(H:M:S): " + lHours+ ":" + lMinutes + ":" + lSeconds);
		}
		
	}
	
private String mspTableLookup(String url, String callid, int tenantId, String mspKey, int conntimeout, int readtimeout, LoggerContext context, CallEndAPI data) throws AudiumException {
		
		String retDNIS = Constants.EmptyString;
		String strElementName  = data.getCurrentElement();
		GetMenuSelectionPropertyTableByKey objGetMenuSelectionPropertyTableByKey = new GetMenuSelectionPropertyTableByKey();
		
		JSONObject objResp = objGetMenuSelectionPropertyTableByKey.start(url, callid, tenantId, mspKey, conntimeout, readtimeout, context);
		JSONObject respBody = null;
		
		String strReqBody = Constants.EmptyString;
		
		if (objResp != null) {
			if (objResp.containsKey(Constants.REQUEST_BODY))
				strReqBody = objResp.get(Constants.REQUEST_BODY).toString();
			data.addToLog(strElementName, "Req Body : " + strReqBody);
			if (objResp.containsKey(Constants.RESPONSE_CODE) && (int) objResp.get(Constants.RESPONSE_CODE) == 200) {
				respBody = (JSONObject) objResp.get(Constants.RESPONSE_BODY);
				data.addToLog(strElementName, "Resp Body : " + respBody);
			}
		}
		
		if(respBody != null) {
			JSONArray resArray = (JSONArray) respBody.get("res");
			data.addToLog(strElementName, "MSP Result : " + objResp);
			if (resArray == null || resArray.size() == 0) {
				data.addToLog(strElementName, "MSP Array 0");
			} else {
				data.addToLog(strElementName, "MSP Result Array Size : " + resArray.size());
				JSONObject mspObject = (JSONObject) resArray.get(0);
				data.addToLog(strElementName, "MSP objects : " + mspObject);
				
				String dnis = (String)mspObject.get("dnis");
				String category = (String)mspObject.get("category");
				String nlintentrollup = (String)mspObject.get("nlintentrollup");
				String intent = (String)mspObject.get("intent");
				
				data.addToLog(strElementName, "MSP dnis : " + dnis);
				data.addToLog(strElementName, "MSP category : " + category);
				data.addToLog(strElementName, "MSP nlintentrollup : " + nlintentrollup);
				data.addToLog(strElementName, "MSP intent : " + intent);
				
				data.setSessionData(Constants.S_FINAL_DNIS, dnis);
				
				if(intent != null) {
					if(!intent.equalsIgnoreCase(Constants.EmptyString)) {
						data.setSessionData(Constants.S_FINAL_INTENT, intent);
					}
				}
				retDNIS = dnis;
				//data.setSessionData(Constants.S_INTENT, intent);
			}
		}
		return retDNIS;
	}
	
	private boolean updateBO(String mspDnis, CallEndAPI data) {
		
		boolean isBOUpdated = false;
		
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		data.addToLog(data.getCurrentElement(), "BusinessObjects started");
		
		try {
			if(data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String)data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog(data.getCurrentElement(), "strDnis  :"+mspDnis+ " :: API url : "+data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL)+ " :: conTimeout : "+conTimeout);
				GetBusinessObjectsByDnisKey objBusinessObjects = new GetBusinessObjectsByDnisKey();
				JSONObject resp =  (JSONObject) objBusinessObjects.start(url, callerId, Constants.tenantid, mspDnis , conTimeout, readTimeout,context);
				data.addToLog(data.getCurrentElement(), "GetBusinessObjectsByDnisKey API response  :"+resp);
				if(resp != null) {
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(data.getCurrentElement(), "" );
						
						JSONObject respObj = (JSONObject)resp.get(Constants.RESPONSE_BODY);
						JSONArray resArr = (JSONArray)respObj.get("res");
						
						if(resArr.size() == 1) {
							
							isBOUpdated = true;
							
							JSONObject obj = (JSONObject) resArr.get(0);
							
							if(obj.get("dnisgroup") != null) {
								if(!obj.get("dnisgroup").toString().equalsIgnoreCase(Constants.EmptyString)) {
									data.setSessionData(Constants.S_FINAL_DNIS_GROUP, obj.get("dnisgroup"));
								}
							}
							if(obj.get("category") != null) {
								if(!obj.get("category").toString().equalsIgnoreCase(Constants.EmptyString)) {
									data.setSessionData(Constants.S_FINAL_CATEGORY, obj.get("category"));
								}
							}
							if(obj.get("lineofbusiness") != null) {
								if(!obj.get("lineofbusiness").toString().equalsIgnoreCase(Constants.EmptyString)) {
									data.setSessionData(Constants.S_FINAL_LOB, obj.get("lineofbusiness"));
								}
							}
						}		
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in GetBusinessObjectsByDnisKey API call  :: "+e);
			caa.printStackTrace(e);
		}
		return isBOUpdated;
	}
	
	public String verintEnable(CallEndAPI data) {

		String StrExitState = "ER";
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String currElementName = data.getCurrentElement();
		String verintPaused = (String) data.getSessionData("VERINT_PAUSED");
		data.addToLog(currElementName, "Verint Paused : " + verintPaused);
		
		if("true".equalsIgnoreCase(verintPaused)) {
			try {
				//Verint start
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String verintUrl = (String) data.getSessionData("S_VERINT_URL");
			
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				data.addToLog(currElementName, "Verint URL : " + verintUrl);
				VerintPauseResume objVerintPauseResume = new VerintPauseResume();
				data.addToLog(currElementName, "Verint 1 : ");
				boolean resp = objVerintPauseResume.verintPauseResume(verintUrl, "ResumeRecord", callerId, context, conTimeout, readTimeout);
				data.addToLog(currElementName, "Verint 2 : " + resp);
					
				if (resp) {
					StrExitState = "SU";
				}
				data.setSessionData("VERINT_PAUSED", "false");
			} catch (Exception e) {
				data.addToLog(currElementName,"Exception in recordingDisable call  :: "+e);
				caa.printStackTrace(e);
			}

		}
		
		return StrExitState;
	
	}
	
	private static String getDate(String pStrFormat, Date pDate){
		DateFormat fmt = null;
		if(pStrFormat.equals("dd/MM/yyyy HH:mm:ss")){
			fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		}
		if(pStrFormat.equals("MM/dd/yyyy HH:mm:ss")){
			fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		}
		if(pStrFormat.equals("yyyyMMddHHmmssSSS")){
			fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		}if(pStrFormat.equals("yyyy-MM-dd HH:mm:ss")) {
			fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}

		return fmt.format(pDate);
	}
	
	private static String calculateDuration(String startTime, String endTime, CallEndAPI data) {
		String currElementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		long seconds = 0;
		
		try {
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime callStartTime = LocalDateTime.parse(startTime, formatter);
			LocalDateTime callEndTime = LocalDateTime.parse(endTime, formatter);
			
			 Duration duration = Duration.between(callStartTime, callEndTime);
		     seconds = duration.getSeconds();
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in calculateDuration :: "+e);
			caa.printStackTrace(e);
		}
		
		return String.valueOf(seconds);
	}
	
	private String convertTimetoCT(String time, CallEndAPI data) {
		String cstDateTime = Constants.EmptyString;
		String currElementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime timeinLocalDateFormat = LocalDateTime.parse(time, formatter);
			
			ZoneId currentZone = ZoneId.systemDefault();
			ZonedDateTime defaultZoneddate = timeinLocalDateFormat.atZone(currentZone);
			
			ZoneId cstZone = ZoneId.of("America/Chicago");
			ZonedDateTime cstTime = defaultZoneddate.withZoneSameInstant(cstZone);
			
			cstDateTime = cstTime.format(formatter);
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in calculateDuration :: "+e);
			caa.printStackTrace(e);
		}
		return cstDateTime;
	}
}