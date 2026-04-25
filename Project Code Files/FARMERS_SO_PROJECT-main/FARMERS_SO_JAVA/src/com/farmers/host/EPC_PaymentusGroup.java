package com.farmers.host;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.EPCPaymentusGroup_Post;
import com.farmers.FarmersAPI_NP.EPCPaymentusGroup_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;


public class EPC_PaymentusGroup extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

		String strExitState = Constants.ER;
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

			data.addToLog(currElementName, "URL :: " + data.getSessionData(Constants.S_EPC_PAYMENTUS_GROUP_URL) + " , Conn Time Out :: " + data.getSessionData(Constants.S_CONN_TIMEOUT) +" , Read Time Out ::" + data.getSessionData(Constants.S_READ_TIMEOUT));

			if(null != data.getSessionData(Constants.S_EPC_PAYMENTUS_GROUP_URL) && null!= data.getSessionData(Constants.S_CONN_TIMEOUT) && null!= data.getSessionData(Constants.S_READ_TIMEOUT)){
				String url = (String) data.getSessionData(Constants.S_EPC_PAYMENTUS_GROUP_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String operation="SALE";
				String id = (String)data.getSessionData("S_EPC_PAYMENT_POLICYNUM");
				data.addToLog(currElementName, "ID value :: "+id);
				String authtoken=(String)caa.getFromSession(Constants.S_PAYOR_ZIP_CODE, "");
				String languagepreference=(String)data.getSessionData(Constants.S_PREF_LANG);
				if(url.contains(Constants.S_POLICY_NUM)) url= url.replace(Constants.S_POLICY_NUM, id);
				data.addToLog(currElementName, "URL :: " + url);
				data.addToLog(currElementName, "callerid :: "+data.getSessionData(Constants.S_CALLID)+" ,operation :: "+operation +" ,id :: "+id+" ,authtoken:: "+authtoken+" ,Language Preference ::"+data.getSessionData(Constants.S_PREF_LANG));

				if(languagepreference.equalsIgnoreCase(Constants.EN)) languagepreference = "en";
				else languagepreference = "es";
				
				data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
				/** Brand **/
				String brandcode = (String)data.getSessionData(Constants.S_BU);
				if(brandcode.contains("commercial")) {
					brandcode = "BI";
				}
				else if (null != data.getSessionData("S_FLAG_FDS_BU") && data.getSessionData("S_FLAG_FDS_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) {
					brandcode = "FDS";
				}
				else if (null != data.getSessionData("S_FLAG_BW_BU") && data.getSessionData("S_FLAG_BW_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) {
					brandcode = "BW";
				}
				else if(null != data.getSessionData("S_FLAG_FOREMOST_BU") && data.getSessionData("S_FLAG_FOREMOST_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) {
					brandcode = "FM";
				}
				
				data.addToLog(currElementName,"Brandcode::"+brandcode);
				
				String firstname =((String)data.getSessionData(Constants.S_FIRST_NAME) != null) ? ((String)data.getSessionData(Constants.S_FIRST_NAME)) : Constants.EmptyString;
				String lastname=((String)data.getSessionData(Constants.S_LAST_NAME) != null) ? ((String)data.getSessionData(Constants.S_LAST_NAME)) : Constants.EmptyString;
				long phonenumber;
				if(null != data.getSessionData(Constants.S_FINAL_ANI)) phonenumber =  Long.parseLong((String)data.getSessionData(Constants.S_FINAL_ANI));
				else phonenumber = Long.parseLong((String)data.getSessionData(Constants.S_ANI));
				String email = ((String)data.getSessionData(Constants.S_EMAIL) != null) ? ((String)data.getSessionData(Constants.S_EMAIL)) : Constants.EmptyString;
				String line1= ((String)data.getSessionData(Constants.S_LINE1) != null) ? ((String)data.getSessionData(Constants.S_LINE1)) : Constants.EmptyString;
				String state = ((String)data.getSessionData(Constants.S_STATE) != null) ? ((String)data.getSessionData(Constants.S_STATE)) : Constants.EmptyString;
				String zipcode=(String)caa.getFromSession(Constants.S_PAYOR_ZIP_CODE, "");
				String city=((String)data.getSessionData(Constants.S_CITY) != null) ? ((String)data.getSessionData(Constants.S_CITY)) : Constants.EmptyString;
				String country=((String)data.getSessionData(Constants.S_COUNTRY) != null) ? ((String)data.getSessionData(Constants.S_COUNTRY)) : Constants.EmptyString;
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				data.addToLog(currElementName, "firstname::"+firstname+",lastname::"+lastname+",phonenumber::"+phonenumber+",email::"+email+",line1::"+line1+",state::"+state+",zipcode::"+zipcode+",city::"+city+",country::"+country+",connectiontimeout::"+conTimeout+",readtimeout::"+readTimeout);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				EPCPaymentusGroup_Post obj = new EPCPaymentusGroup_Post();
				//START-- UAT ENV CHANGES(PRIYA,SHAIK)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				EPCPaymentusGroup_NP_Post objNP=new EPCPaymentusGroup_NP_Post();
				JSONObject resp=null;
				if("YES".equalsIgnoreCase(UAT_FLAG))
				{
					String Key=Constants.S_EPC_PAYMENTUS_GROUP_URL;
					 region=regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ",region);
					resp=objNP.start(url, callerId, operation, id, authtoken, languagepreference, brandcode, firstname, lastname, phonenumber, email, line1, state, zipcode, city, country, conTimeout, readTimeout, context,region);
					
				}
				else
				{
					region="PROD";
					resp=obj.start(url, callerId, operation, id, authtoken, languagepreference, brandcode, firstname, lastname, phonenumber, email, line1, state, zipcode, city, country, conTimeout, readTimeout, context);


				}
				//UAT ENV CHANGE END(SHAIK,PRIYA
				data.addToLog(currElementName, " API response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));

				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Set EPC_PaymentusGroup API Response into session with the key name of "+currElementName+Constants._RESP);
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strRespBody = (String)resp.get(Constants.RESPONSE_MSG);
						//START Balaji K- CS1327384 - Farmers Insurance  US  Call Outcome for Payment US Transfers
						data.setSessionData("S_PAYMENTUS_CALLOUTCOME", "Y");
						data.setSessionData("S_PAYMENTUS_CALLOUTCOME", "Y");
						//END Balaji K-CS1327384 - Farmers Insurance  US  Call Outcome for Payment US Transfers
						strExitState = Constants.SU;
					} else {
						if(resp.containsKey(Constants.RESPONSE_MSG)) strRespBody = (String)resp.get(Constants.RESPONSE_MSG);
					}		
				}
			}
		} catch (Exception e) {

			data.addToLog(currElementName,"Exception in  EPC_PaymentusGroup API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName," EPC_PaymentusGroup API by PolicyNumber", strReqBody,region, (String) data.getSessionData(Constants.S_EPC_PAYMENTUS_GROUP_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for EPC_PaymentusGroup API call by Policy Number  :: "+e);
			caa.printStackTrace(e);
		}

		data.setSessionData("S_Commercial_API","True");

		if(strExitState.equalsIgnoreCase(Constants.SU)) {
			//			data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPC Return call PAYMENT_SUBMIT_FAILED");
			caa.createMSPKeyEPCTransferBackFromPaymentUS(caa, data, "EPC_PAYMENTUS", "API FAILURE");
		}else {
			//			data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPC Return call PAYMENT_SUBMIT_FAILED");	
			caa.createMSPKeyEPCTransferBackFromPaymentUS(caa, data, "EPC_PAYMENTUS", "API FAILURE");
		}

		//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPC_PAYMENTUS:API FAILURE");
		data.addToLog(currElementName,"S_MENU_SELCTION_KEY for  EPCUS_HOST_002  : EPC_PaymentusGroup :: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));

		return strExitState;

	}

}

