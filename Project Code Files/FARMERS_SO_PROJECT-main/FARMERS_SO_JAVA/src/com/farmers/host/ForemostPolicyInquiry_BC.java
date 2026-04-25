package com.farmers.host;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.ForemostPolicyInquiry_Post;
import com.farmers.FarmersAPI_NP.ForemostPolicyInquiry_NP_Post;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;

public class ForemostPolicyInquiry_BC extends DecisionElementBase {
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)

		try {

			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

			data.addToLog(currElementName,
					"ForemostPolicy_Inquiry API Request body : url ::"
							+ (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) + " : CallID :: "
							+ (String) data.getSessionData(Constants.S_CALLID) + " : Policy Number :: "
							+ (String) data.getSessionData(Constants.S_POLICY_NUM) + " : Connection Timeout :: "
							+ (String) data.getSessionData(Constants.S_CONN_TIMEOUT) + " : Read timeout :: "
							+ (String) data.getSessionData(Constants.S_READ_TIMEOUT));

			if (null != data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) && null != data.getSessionData(Constants.S_CONN_TIMEOUT)
					&& null != data.getSessionData(Constants.S_READ_TIMEOUT)) {
				String url = (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				String policyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);// Will get from GDF Menu
				String policySource = Constants.S_POLICY_SOURCE_FOREMOST;// hard code as Foremost
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String sysDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
				String connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				ForemostPolicyInquiry_Post apiObj = new ForemostPolicyInquiry_Post();

				//Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				ForemostPolicyInquiry_NP_Post objNP = new ForemostPolicyInquiry_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_FOREMOST_POLICYINQUIIRY_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, policyNumber, sysDate, policySource,Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context, region);
				}else {
					region="PROD";
				    resp = apiObj.start(url, tid, policyNumber, sysDate, policySource,Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr),context);
				}
				//Non prod changes-Priya

				data.addToLog(currElementName,
						currElementName + " : ForemostPolicyInquiry_Post API response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));

				if (null != resp) {
					if (resp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
						data.addToLog(currElementName,
								"ForemostPolicy_Inquiry API Request body if response is not null :: " + strReqBody);
					}
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set " + currElementName
								+ " : ForemostPolicyInquiry_Post API Response into session with the key name of "
								+ currElementName + Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));

						StrExitState = Constants.SU;
						data.addToLog(currElementName,
								"ForemostPolicy_inquiry API strExitState if resposne is success :: " + StrExitState);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
						data.addToLog(currElementName,
								"ForemostPolicy_Inquiry API Response Message if response code is not 200 ::"
										+ strRespBody);
					}

				} else {
					data.addToLog(currElementName, "ForemostPolicy_Inquiry API Response is null" + resp);
				}
			}

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			data.addToLog(currElementName + "Exception in ForemostPolicy_Inquiry API :: ",
					e.getMessage() + " :: Full Exception  :: " + sw.toString());
		}

		try {
			objHostDetails.startHostReport(currElementName, "ForemostPolicy_Inquiry API", strReqBody, region,(String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
			objHostDetails.endHostReport(data, strRespBody,
					StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for " + currElementName
					+ "  ForemostPolicyInquiryAPI call  :: " + e);
			caa.printStackTrace(e);
		}

		data.addToLog(currElementName + "strExitState at last :: ", StrExitState);
		return StrExitState;
	}

}
