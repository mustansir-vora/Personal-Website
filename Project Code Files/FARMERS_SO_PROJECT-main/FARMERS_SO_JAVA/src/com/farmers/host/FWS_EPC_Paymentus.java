package com.farmers.host;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.FWSEPCPaymentus_Post;
import com.farmers.FarmersAPI_NP.FWSEPCPaymentus_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;


public class FWS_EPC_Paymentus extends DecisionElementBase {

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
			if(data.getSessionData(Constants.S_FWS_EPC_PAYMENTUSS_URL)!=null && data.getSessionData(Constants.S_CONN_TIMEOUT)!=null && data.getSessionData(Constants.S_READ_TIMEOUT)!=null){
				String url = (String) data.getSessionData(Constants.S_FWS_EPC_PAYMENTUSS_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String operation="SALE";
				String id = Constants.EmptyString;
				if(null != data.getSessionData("S_EPC_PAYMENT_POLICYNUM")) {
					id = (String)data.getSessionData("S_EPC_PAYMENT_POLICYNUM");
					data.addToLog(currElementName, "S_EPC_PAYMENT_POLICYNUM :: " + id);
				} else {
					id=(String)data.getSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO);
				}
				data.addToLog(currElementName, "FWS_EPC_Paymentus  id :: " + id);
				if(url.contains(Constants.S_CALLID)) url= url.replace(Constants.S_CALLID, id);
				String authtoken=(String)data.getSessionData(Constants.S_PAYOR_ZIP_CODE);
				String languagepreference=(String)data.getSessionData(Constants.S_PREF_LANG);
				// START : Earlier passing "en-us" & "es-us" but business requested to pass language as "en" or "es" hence modified here
				
				if(languagepreference.equalsIgnoreCase(Constants.EN)) {
					languagepreference = "en"; 
					data.addToLog(currElementName, "languagepreference ::... " + languagepreference);
				}
				else {
					languagepreference = "es";
					data.addToLog(currElementName, "languagepreference ::... " + languagepreference);
				}
				// END : Earlier passing "en-us" & "es-us" but business requested to pass language as "en" or "es" hence modified here
				String brandcode="FWS";
				
				String billingaccountnumber = ((String)data.getSessionData("S_FWS_POLICY_BILLING_ACCT_NO") != null) ? ((String)data.getSessionData("S_FWS_POLICY_BILLING_ACCT_NO")) : Constants.EmptyString;
				
				String firstname =((String)data.getSessionData(Constants.S_FIRST_NAME) != null) ? ((String)data.getSessionData(Constants.S_FIRST_NAME)) : Constants.EmptyString;
				String lastname=((String)data.getSessionData(Constants.S_LAST_NAME) != null) ? ((String)data.getSessionData(Constants.S_LAST_NAME)) : Constants.EmptyString;
				long phonenumber;
				if(null != data.getSessionData(Constants.S_FINAL_ANI)) phonenumber =  Long.parseLong((String)data.getSessionData(Constants.S_FINAL_ANI));
				else phonenumber = Long.parseLong((String)data.getSessionData(Constants.S_ANI));
				String email = ((String)data.getSessionData(Constants.S_EMAIL) != null) ? ((String)data.getSessionData(Constants.S_EMAIL)) : Constants.EmptyString;
				String line1= ((String)data.getSessionData(Constants.S_LINE1) != null) ? ((String)data.getSessionData(Constants.S_LINE1)) : Constants.EmptyString;
				String state = ((String)data.getSessionData(Constants.S_POLICY_STATE) != null) ? ((String)data.getSessionData(Constants.S_POLICY_STATE)) : Constants.EmptyString;
				String zipcode=(String)data.getSessionData(Constants.S_PAYOR_ZIP_CODE);
				String city=((String)data.getSessionData(Constants.S_CITY) != null) ? ((String)data.getSessionData(Constants.S_CITY)) : Constants.EmptyString;
				String country=((String)data.getSessionData(Constants.S_COUNTRY) != null) ? ((String)data.getSessionData(Constants.S_COUNTRY)) : Constants.EmptyString;

				double balancedueamount = ((String)data.getSessionData("S_Current_Balance") != null) ? Double.parseDouble(((String)data.getSessionData("S_Current_Balance"))) : 0.0d;
				String balance = formatWithTwoDecimals(balancedueamount);
				double finalBalance = Double.parseDouble(balance);
				
				String policysource = (String) data.getSessionData("S_POLICY_SOURCE") != null ? (String) data.getSessionData("S_POLICY_SOURCE") : Constants.EmptyString;
				if (policysource.equalsIgnoreCase("ARS")) {
					policysource = "FWSARS";
					//String substr = id.substring(Math.max(0,id.length()-9));
					//id=substr;
					id = id.length() >= 9 ? id.substring(id.length() - 9) : id;
					data.addToLog(currElementName, "ID ::... " + id); //Receiving 11 digits from backend system but passing 9 digits as per business requirement

				}
				else {
					policysource = "FWSA360";
				}
				data.addToLog(currElementName, "Policy Source for FWS EPC PaymentUS :: " + policysource);
				String existingcustomerind = "N";
				String policynumber = ((String)data.getSessionData("S_FWS_EPC_POLICY_NUM") != null) ? ((String)data.getSessionData("S_FWS_EPC_POLICY_NUM")) : Constants.EmptyString;
				String policyeffectivedate = ((String)data.getSessionData(Constants.S_FWS_POLICY_EFF_DATE) != null) ? ((String)data.getSessionData(Constants.S_FWS_POLICY_EFF_DATE)) : Constants.EmptyString;
				String policystate = ((String)data.getSessionData(Constants.S_POLICY_STATE) != null) ? ((String)data.getSessionData(Constants.S_POLICY_STATE)) : Constants.EmptyString;
				String policyinputtypecd = ((String)data.getSessionData("S_PAYMENT_LEVEL_CODE") != null) ? ((String)data.getSessionData("S_PAYMENT_LEVEL_CODE")) : Constants.EmptyString;
				String producerrolecode = ((String)data.getSessionData("S_FWS_PRODUCER_ROLE_CODE") != null) ? ((String)data.getSessionData("S_FWS_PRODUCER_ROLE_CODE")) : Constants.EmptyString;
				String callroutingindicator = ((String)data.getSessionData("S_FWS_CALL_ROUTING_INDICATOR") != null) ? ((String)data.getSessionData("S_FWS_CALL_ROUTING_INDICATOR")) : Constants.EmptyString;
				String iapolicyindicator = ((String)data.getSessionData(Constants.S_IA_POLICY_INDICATOR) != null) ? ((String)data.getSessionData(Constants.S_IA_POLICY_INDICATOR)) : Constants.EmptyString;
				String gpc = ((String)data.getSessionData("S_FWS_GPC") != null) ? ((String)data.getSessionData("S_FWS_GPC")) : Constants.EmptyString;
				String companyproductcode = ((String)data.getSessionData(Constants.S_FWS_POLICY_LOB) != null) ? ((String)data.getSessionData(Constants.S_FWS_POLICY_LOB)) : Constants.EmptyString;
				String servicelevels = ((String)data.getSessionData("S_FWS_SERVICE_LEVEL") != null) ? ((String)data.getSessionData("S_FWS_SERVICE_LEVEL")) : Constants.EmptyString;
				String combopackageindicator = ((String)data.getSessionData("S_FWS_COMBO_PACKAGE_INDICATOR") != null) ? ((String)data.getSessionData("S_FWS_COMBO_PACKAGE_INDICATOR")) : Constants.EmptyString;
				String paymentsitecd = billingaccountnumber != null && billingaccountnumber.length() > 2 ? billingaccountnumber.substring(0, 2) : Constants.EmptyString;
				String internalpolicynumber = ((String)data.getSessionData(Constants.S_FWS_INT_POLICY_NO) != null) ? ((String)data.getSessionData(Constants.S_FWS_INT_POLICY_NO)) : Constants.EmptyString;
				String internalpolicyversion = ((String)data.getSessionData("S_FWS_INTERNAL_POLICY_VERSION") != null) ? ((String)data.getSessionData("S_FWS_INTERNAL_POLICY_VERSION")) : Constants.EmptyString;

				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				FWSEPCPaymentus_Post obj = new FWSEPCPaymentus_Post();
				//Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				FWSEPCPaymentus_NP_Post objNP = new FWSEPCPaymentus_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_FWS_EPC_PAYMENTUSS_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, callerId, operation, id, authtoken, languagepreference, brandcode, firstname, lastname,  phonenumber, email, 
							line1, state, zipcode, city,  country, finalBalance, policysource, existingcustomerind, policynumber, policyeffectivedate, policystate, policyinputtypecd, 
							producerrolecode, callroutingindicator, iapolicyindicator, gpc, companyproductcode, servicelevels, combopackageindicator, paymentsitecd, 
							internalpolicynumber, internalpolicyversion, conTimeout, readTimeout,context, region);
				}else {
					region="PROD";
				    resp = obj.start(url, callerId, operation, id, authtoken, languagepreference, brandcode, firstname, lastname,  phonenumber, email, 
							line1, state, zipcode, city,  country, finalBalance, policysource, existingcustomerind, policynumber, policyeffectivedate, policystate, policyinputtypecd, 
							producerrolecode, callroutingindicator, iapolicyindicator, gpc, companyproductcode, servicelevels, combopackageindicator, paymentsitecd, 
							internalpolicynumber, internalpolicyversion, conTimeout, readTimeout,context);
				}
				//Non prod changes-Priya

				data.addToLog(currElementName, " API response  :"+resp);	
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));

				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "FWS_EPC_Paymentus API Response Success :: Response Code :: "+(int) resp.get(Constants.RESPONSE_CODE));
						strRespBody = "SUCCESS";
						//START Balaji K- CS1327384 - Farmers Insurance  US  Call Outcome for Payment US Transfers
						data.setSessionData("S_PAYMENTUS_CALLOUTCOME", "Y");
						//END Balaji K-CS1327384 - Farmers Insurance  US  Call Outcome for Payment US Transfers
						strExitState = Constants.SU;
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}		
				}
			}
		} catch (Exception e) {

			data.addToLog(currElementName,"Exception in  EPC_PaymentusGroup API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName," FWS_EPC_Paymentus API by PolicyNumber", strReqBody,region, (String) data.getSessionData(Constants.S_FWS_EPC_PAYMENTUSS_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for FWS_EPC_Paymentus API call by Policy Number  :: "+e);
			caa.printStackTrace(e);
		}


		if(strExitState.equalsIgnoreCase(Constants.SU)) {
			//			data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPC Return call PAYMENT_SUBMIT_FAILED");
			caa.createMSPKeyEPCTransferBackFromPaymentUS(caa, data, "EPC_PAYMENTUS", "API FAILURE");
		} else {
			//			data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPC_PAYMENTUS:API FAILURE");
			caa.createMSPKeyEPCTransferBackFromPaymentUS(caa, data, "EPC_PAYMENTUS", "WARHEAD ENABLED");
		}
		data.addToLog(currElementName,"S_MENU_SELCTION_KEY for  EPCUS_HOST_001  : FWS_EPC_Paymentus :: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));

		return strExitState;

	}
	
	
	public static String formatWithTwoDecimals(double number) {
	    // Round to two decimal places
	    double roundedNumber = Math.round(number * 100.0) / 100.0;
	    
	    // Format the rounded number as a string with two decimal places
	    return String.format("%.2f", roundedNumber);
	  }


	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	public static void main(String[] args) {
		String name = "Mustansir";
		System.out.println("Length :: " + name.length());
		
		name = name.substring(name.length() - 5, name.length());
		System.out.println("Name after substringing :: " + name);
	}
}

