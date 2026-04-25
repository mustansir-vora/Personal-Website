package com.farmers.afterHoursCallBack;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.CallBack_Segment_Post;
import com.farmers.FarmersAPI_NP.CallBack_Segment_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SendCallBackDataToSegment  extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String apiRespCode = Constants.EmptyString;
		String region=null;
		HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		try {
			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			if (data.getSessionData(Constants.S_CALLBACK_SEGMENT_URL) != null && data.getSessionData(Constants.S_CALLID) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_ANI) != null&& data.getSessionData(Constants.S_STATENAME) != null && data.getSessionData(Constants.S_TIMEZONE) != null && data.getSessionData(Constants.S_CALLBACK_TIME) != null && data.getSessionData(Constants.S_FIRSTNAME) != null && data.getSessionData(Constants.S_LASTNAME) != null  ) {
				String url = (String) data.getSessionData(Constants.S_CALLBACK_SEGMENT_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String phonenumber = (String) data.getSessionData(Constants.S_ANI);
				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT));
				data.addToLog(currElementName, "Segment API readtimeout ::" + readTimeout);
				String eventType="ivr";
				String callBackConsent="true";
				String statecode = (String) data.getSessionData(Constants.S_STATECODE);
				String timezone = (String) data.getSessionData(Constants.S_TIMEZONE);
				String callBackTime = (String) data.getSessionData(Constants.S_CALLBACK_TIME);
				String callbacktype = (String) data.getSessionData(Constants.S_CALLBACK_TYPE);
				String fname = (String) data.getSessionData(Constants.S_FIRSTNAME);
				String lname = (String) data.getSessionData(Constants.S_LASTNAME);

				CallBack_Segment_Post apiObj= new CallBack_Segment_Post();
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
				if(url.startsWith(prefix)) {
					UAT_FLAG="YES";
				}
				CallBack_Segment_NP_Post apiNpObj= new CallBack_Segment_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					data.addToLog("S_CALLBACK_SEGMENT_URL: ", url);
					String Key =Constants.S_CALLBACK_SEGMENT_URL;
					region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = apiNpObj.start(url, tid,conTimeout, readTimeout, eventType,phonenumber,statecode,timezone,callBackTime,callbacktype,callBackConsent,fname,lname,  context, region);
				}
				else {
					region="PROD";
					resp = apiObj.start(url, tid, conTimeout, readTimeout, eventType,phonenumber,statecode,timezone,callBackTime,callbacktype,callBackConsent,fname,lname, context);
				}
				data.addToLog(currElementName, "API response  :" + resp);
                
				strReqBody = (String) resp.get(Constants.REQUEST_BODY).toString();
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (resp != null) {

					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {

						data.setSessionData("S_CALLBACK_SEGMENT_CALLED", "TRUE");
						data.addToLog(currElementName, "Callback Segment API Response into session with the key name of " + currElementName + Constants._RESP);
						JSONObject strRespBodyTemp = (JSONObject) resp.get(Constants.RESPONSE_BODY);

						strRespBody = (String) resp.get(Constants.RESPONSE_BODY).toString();
						strReqBody = (String) resp.get(Constants.REQUEST_BODY).toString();

						if (!strRespBodyTemp.isEmpty()) {

							StrExitState =Constants.SU;
							//data.setSessionData("CALLBACKSEGMENTJSONRESP", strRespBody);

						}else {
							StrExitState = Constants.ER;
						}
						//data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}

			}
		}
		catch(Exception e) {
			data.addToLog(currElementName, "Exception in Segment API call  :: " + e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName, "SENDCALLBACKDATATOSEGMENT_BC", strReqBody,region, (String) data.getSessionData(Constants.S_CALLBACK_SEGMENT_URL));
			objHostDetails.endHostReport(data, strRespBody, StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for Send Callback Data to Segment API call  :: " + e);
			caa.printStackTrace(e);
		}
		return StrExitState;

	}
}
