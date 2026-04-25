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
 
public class CIDA_HOST_001 extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		String url = Constants.EmptyString;
		String finalurl = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
 
		try {
			if(data.getSessionData(Constants.S_CONN_TIMEOUT)!=null && data.getSessionData(Constants.S_READ_TIMEOUT)!=null){
				String policyNum = (String)data.getSessionData(Constants.S_POLICY_NUM);
				String accNum = (String)data.getSessionData(Constants.S_ACCNUM);
				
				if(null != policyNum && !policyNum.isEmpty()) {
					url = (String)data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY_POLICYNUM_URL);
					if(url.contains(Constants.S_URL_POLICYNUM)) finalurl = url.replace(Constants.S_URL_POLICYNUM, policyNum);
					data.addToLog(currElementName,"POLICYNUM API URL : "+ finalurl);
				} else {
					url = (String)data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY_ACCNUM_URL);
					if(url.contains(Constants.S_URL_ACCNUM)) finalurl = url.replace(Constants.S_URL_ACCNUM, accNum);
					data.addToLog(currElementName,"ACCNUM API URL : "+ finalurl);
				}
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				CommercialBillingSummary_Get obj = new CommercialBillingSummary_Get();
				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				CommercialBillingSummary_NP_Get objNP = new CommercialBillingSummary_NP_Get();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_COMMERCIAL_BILLING_SUMMARY_POLICYNUM_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(finalurl,callerId,conTimeout,readTimeout, context, region);
				}else {
					region="PROD";
				    resp = obj.start(finalurl,callerId,conTimeout,readTimeout, context);
				}
              //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
				data.addToLog(currElementName, " API response  :"+resp);	
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
 
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set Commercial Billing Summary Lookup API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
					}else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();	
					}
					data.setSessionData(Constants.S_COMMERCIAL_BILLING_JSON,strRespBody);
				}
				
				
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in  Commercial Billing Summary Lookup API call  :: "+e);
			caa.printStackTrace(e);
		}
 
		try {
			
			objHostDetails.startHostReport(currElementName," Commercial Billing Summary LookupAPI by AccountNumber/Policynumber", strReqBody,region, url);
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for  Commercial Billing Summary Lookup API call by Account Number  :: "+e);
			caa.printStackTrace(e);
		}
 
		data.setSessionData("S_Commercial_API","True");
 
		return strExitState;
	}
 
 
 
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
        try {
            JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
            data.addToLog(currElementName, "Value of Response Body : " + resp);
            JSONObject billingSummary = (JSONObject) resp.get("billingSummary");
            data.addToLog(currElementName, "Value of billingSummary : " + billingSummary);
            JSONArray accounts = (JSONArray) billingSummary.get("accounts");
            data.addToLog(currElementName, "Value of accounts : " + accounts);
            int noOfAccounts = 0;
            for (Object accountObj : accounts) {
            	
                JSONObject account = (JSONObject) accountObj;
                data.addToLog(currElementName, "Value of Account" + account);
                String retrievedaccno = (String) account.get("accountNumber");
                data.addToLog(currElementName, "Value of retrievedaccno  : " + retrievedaccno);
                
                // Extract the account number from the retrievedaccno string
                String accno = retrievedaccno.split("-")[0];
                
                data.addToLog(currElementName, "Value of AccNumber After Extraction  : " + accno);
                data.setSessionData(Constants.S_API_ACCNUM, accno);
                if(accno != null && !accno.isEmpty()) noOfAccounts = noOfAccounts+1;
                String stopCode = (String) account.get("stopCode");
                data.setSessionData("S_STOPCODE", stopCode);
                if (stopCode.equalsIgnoreCase("BBR")) data.setSessionData("S_IS_RTB_ACCOUNT", "TRUE");
                else data.setSessionData("S_IS_RTB_ACCOUNT", "FALSE");

                String payorZipCode = (String) account.get("payorZipCode");
                data.setSessionData("S_PAYOR_ZIP_CODE", payorZipCode);
            }
            
            String isPolicyNumberProvided = (String) data.getSessionData(Constants.S_IS_POLICYNUM_PROVIDED);
            String isAccNumberProvided = (String) data.getSessionData(Constants.S_IS_ACCNO_PROVIDED);
            
            if(noOfAccounts == 0 && isPolicyNumberProvided != null && isPolicyNumberProvided.equalsIgnoreCase(Constants.TRUE)) {
            	strExitState=Constants.CIDA_HOST_001_NoAccFound_PolicyNumberAsked_Resp;
            } else if(noOfAccounts == 0) {
            	strExitState=Constants.CIDA_HOST_001_NoAccFound_Resp;
            } else if(noOfAccounts == 1 && isPolicyNumberProvided != null && isPolicyNumberProvided.equalsIgnoreCase(Constants.TRUE)) {
            	strExitState=Constants.CIDA_HOST_001_AccountfoundbyPolicyNumber_Resp;
            	data.setSessionData(Constants.VXMLParam1, (String)data.getSessionData(Constants.S_API_ACCNUM));
            	data.setSessionData(Constants.VXMLParam2, "NA");
            	data.setSessionData(Constants.VXMLParam3, "NA");
            	data.setSessionData(Constants.VXMLParam4, "NA");
            	data.addToLog(currElementName, "CIDA_HOST_001 : VXMLParam1 : "+data.getSessionData(Constants.VXMLParam1));
            	
            	/** Screen pop up **/
            	data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_POLICYNUM);
            	data.setSessionData(Constants.S_CALLER_INPUT,(String)data.getSessionData(Constants.S_POLICY_NUM));
            	data.setSessionData(Constants.S_VALID_POLICY_NUM,true);
            } else if(noOfAccounts == 1 && isAccNumberProvided != null && isAccNumberProvided.equalsIgnoreCase(Constants.TRUE)) {
            	/** Screen pop up **/
            	data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_ACCOUNTNUM);
            	data.setSessionData(Constants.S_CALLER_INPUT,(String)data.getSessionData(Constants.S_ACCNUM));
            	strExitState=Constants.CIDA_HOST_001_AccountfoundbyAccNumber_Resp;
            }else if(noOfAccounts > 1) {
            	strExitState=Constants.CIDA_HOST_001_GreaterThan1Acc_Resp;
            } else {
            	strExitState = Constants.ER;
            }
        } catch (Exception e) {
            data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
            caa.printStackTrace(e);
        }
        return strExitState;
    }
 
}