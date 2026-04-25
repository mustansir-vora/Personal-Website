package com.farmers.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.CommercialBillingSummary_Get;
import com.farmers.FarmersAPI_NP.CommercialBillingSummary_NP_Get;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;


public class CommPayments_Billing extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

		String exitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
        
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)

		try {
			data.setSessionData("S_IS_RTB_ACCOUNT","FALSE");
			if(data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY)!=null && data.getSessionData(Constants.S_CONN_TIMEOUT)!=null && data.getSessionData(Constants.S_READ_TIMEOUT)!=null){
				String url = (String) data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY);
				String policyNum = (String)data.getSessionData(Constants.S_POLICY_NUM);
				if(url.contains(Constants.S_URL_POLICYNUM)) url = url.replace(Constants.S_URL_POLICYNUM, policyNum);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				CommercialBillingSummary_Get obj = new CommercialBillingSummary_Get();
				//Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				CommercialBillingSummary_NP_Get objNP = new CommercialBillingSummary_NP_Get();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_COMMERCIAL_BILLING_SUMMARY;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url,callerId,conTimeout,readTimeout, context, region);
				}else {
					region="PROD";
				    resp = obj.start(url,callerId,conTimeout,readTimeout, context);
				}
				//Non prod changes-Priya
				data.addToLog(currElementName, " API response  :"+resp);	
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));

				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY) && resp.get(Constants.RESPONSE_BODY) != null) {
						data.addToLog(currElementName, "Set Commercial Billing Summary Lookup API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						apiResponseManupulation(data, caa, currElementName, strRespBody);
						exitState = Constants.SU;

					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}		
				}
			}
		} catch (Exception e) {

			data.addToLog(currElementName,"Exception in  Commercial Billing Summary Lookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName," Commercial Billing Summary LookupAPI by PolicyNumber", strReqBody,region, (String) data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY));
			objHostDetails.endHostReport(data,strRespBody , exitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for  Commercial Billing Summary Lookup API call by Policy Number  :: "+e);
			caa.printStackTrace(e);
		}

		data.setSessionData("S_Commercial_API","True");

		String hostMSPEndKey = Constants.EmptyString;
		String isPaymentBlocked = (String) data.getSessionData("S_IS_PAYMENT_BLOCKED");
		String isRTBAccount = (String) data.getSessionData("S_IS_RTB_ACCOUNT");
		data.addToLog(currElementName,"Value of S_IS_PAYMENT_BLOCKED is : "+isPaymentBlocked+ "and Value of S_IS_RTB_ACCOUNT is : "+isRTBAccount);
		//if(null!=isPaymentBlocked && !isPaymentBlocked.isEmpty() && isPaymentBlocked.equalsIgnoreCase(Constants.TRUE)) hostMSPEndKey = Constants.PAYMENT_BLOCKED;
		if(null != isRTBAccount && !isRTBAccount.isEmpty () && isRTBAccount.equalsIgnoreCase(Constants.TRUE)) hostMSPEndKey = Constants.ACTIVE_RTB_ACCOUNT;
		if(null != hostMSPEndKey && !hostMSPEndKey.isEmpty()) data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CPF_HOST_001:"+hostMSPEndKey);
		data.addToLog(currElementName,"S_MENU_SELCTION_KEY for  CPF_HOST_001 :: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));

		return exitState;

	}



	private void apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject billingSummary = (JSONObject) resp.get("billingSummary");
			JSONArray accounts = (JSONArray) billingSummary.get("accounts");
			for (Object accountObj : accounts) {
				JSONObject account = (JSONObject) accountObj;
				String strAccNum = (String)account.get("accountNumber");
				if(strAccNum.contains("-")) strAccNum =strAccNum.split("\\-")[0];
				data.setSessionData(Constants.S_ACCNUM, strAccNum);
				String stopCode = (String) account.get("stopCode");
				data.setSessionData("S_STOPCODE", stopCode);
				if(stopCode.equalsIgnoreCase("BBR")) data.setSessionData("S_IS_RTB_ACCOUNT","TRUE");
				else data.setSessionData("S_IS_RTB_ACCOUNT","FALSE");
				String payorZipCode = (String) account.get("payorZipCode");
				data.setSessionData("S_PAYOR_ZIP_CODE", payorZipCode);

				String ivrChnlBlockInd = (String) account.get("ivrChnlBlockInd");
				data.setSessionData("S_IVR_CHNL_BLOCK_IND", ivrChnlBlockInd);
				if(ivrChnlBlockInd.isEmpty()) {
					data.setSessionData("S_IS_ivrChnlBlockInd","TRUE");
				}
				else {
					data.setSessionData("S_IS_ivrChnlBlockInd","FALSE");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
	}
}

