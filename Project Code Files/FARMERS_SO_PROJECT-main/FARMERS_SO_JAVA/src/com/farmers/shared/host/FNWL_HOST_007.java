package com.farmers.shared.host;


import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FNWL.PolicyBean_via_PCN;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_HOST_007 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		JSONObject RespBody = null;
		String apiRespCode = Constants.EmptyString;
		String region=null;
		
		try {
			
			HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
			
			if (null != data.getSessionData(Constants.S_EPC_PAYMENTUS_GROUP_URL) && null != data.getSessionData(Constants.S_READ_TIMEOUT)&& null != data.getSessionData(Constants.S_CONN_TIMEOUT)) {
				String url = (String) data.getSessionData(Constants.S_EPC_PAYMENTUS_GROUP_URL);
				String policyNumber = null != data.getSessionData(Constants.FNWL_POLICY_NUM) ? (String) data.getSessionData(Constants.FNWL_POLICY_NUM) : Constants.EmptyString;
				url = url.contains("S_POLICY_NUM") ? url.replace("S_POLICY_NUM", policyNumber) : url;
				String callID = (String) data.getSessionData(Constants.S_CALLID);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				HashMap<String, PolicyBean_via_PCN> policyMap = (HashMap<String, PolicyBean_via_PCN>) data.getSessionData(Constants.FNWL_POLICYMAP_VIA_PCN);
				data.addToLog(currElementName, "PolicyMap recieved from session :: " + policyMap);
				PolicyBean_via_PCN policyBean_PCN = null;
				
				for (HashMap.Entry<String, PolicyBean_via_PCN> entry : policyMap.entrySet()) {
					policyBean_PCN = entry.getValue();
					
					if (policyNumber.equalsIgnoreCase(policyBean_PCN.getPolicyNumber())) {
						data.addToLog(conntimeout, "Policy Bean found in Policy Map :: Bean Obj :: " + policyBean_PCN);
						break;
					}
				}
				
				String operation = Constants.FNWL_EPC_OPERATION;
				String authtoken = policyBean_PCN.getZip() == null || Constants.EmptyString.equalsIgnoreCase(policyBean_PCN.getZip()) ? (String) data.getSessionData(Constants.FNWL_MN_015_VALUE) : policyBean_PCN.getZip().substring(0, 5);
				String languagePref = null != data.getSessionData(Constants.S_PREF_LANG) ? (String)data.getSessionData(Constants.S_PREF_LANG) : Constants.EmptyString;
				if (languagePref.equalsIgnoreCase(Constants.EN)) languagePref = "en";
				else languagePref = "es";
				Double balanceDue = Constants.FNWL_BALANCE_DUE_AMOUNT;
				String policySource = Constants.FNWL;
				String existingCustomer = Constants.FNWL_Y;
				String firstName = null != policyBean_PCN.getFirstName() ? policyBean_PCN.getFirstName() : Constants.EmptyString;
				String lastName = null != policyBean_PCN.getLastName() ? policyBean_PCN.getLastName() : Constants.EmptyString;
				String phonenumber = (String) data.getSessionData(Constants.S_ANI);
				String line1 = null != policyBean_PCN.getAddressLine1() ? policyBean_PCN.getAddressLine1() : Constants.EmptyString;
				String state = null != policyBean_PCN.getState() ? policyBean_PCN.getState() : Constants.EmptyString;
				String zip = authtoken;
				String city = null != policyBean_PCN.getCity() ? policyBean_PCN.getCity() : Constants.EmptyString;
				String country = null != policyBean_PCN.getCountry() ? policyBean_PCN.getCountry() : Constants.EmptyString;
				String email = Constants.EmptyString;
				
				data.addToLog(currElementName, "url :: " + url);
				data.addToLog(currElementName, "Operation :: " + operation);
				data.addToLog(currElementName, "id :: " + policyNumber);
				data.addToLog(currElementName, "authToken :: " + authtoken);
				data.addToLog(currElementName, "lanuagePref :: " + languagePref);
				data.addToLog(currElementName, "balanceDue :: " + balanceDue);
				data.addToLog(currElementName, "policySource :: " + policySource);
				data.addToLog(currElementName, "existingCustomer :: " + existingCustomer);
				data.addToLog(currElementName, "firstname :: " + firstName);
				data.addToLog(currElementName, "lastname :: " + lastName);
				data.addToLog(currElementName, "phonenumber :: " + phonenumber);
				data.addToLog(currElementName, "line1 :: " + line1);
				data.addToLog(currElementName, "state :: " + state);
				data.addToLog(currElementName, "zip :: " + zip);
				data.addToLog(currElementName, "city :: " + city);
				data.addToLog(currElementName, "country :: " + country);
				
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
				
				if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
                if("YES".equalsIgnoreCase(UAT_FLAG)) {
                	
                region = regionDetails.get(Constants.S_EPC_PAYMENTUS_GROUP_URL);
                }
                else {
                	region="PROD";
                }
				
				Lookupcall FNWLEPCPaymentUSLookup = new Lookupcall();
				JSONObject resp = FNWLEPCPaymentUSLookup.FNWLEPCPaymentUS_Post(url, callID, operation, policyNumber, authtoken, languagePref, balanceDue, policySource, existingCustomer, firstName, lastName, Long.parseLong(phonenumber), email, line1, state, city, zip, country, region, UAT_FLAG, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context);
				
				if (null != resp && resp.containsKey(Constants.RESPONSE_CODE)) {
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				}
				
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						strRespBody = (String)resp.get(Constants.RESPONSE_MSG);
						strExitState = Constants.SU;
					} else {
						if(resp.containsKey(Constants.RESPONSE_MSG)) strRespBody = (String)resp.get(Constants.RESPONSE_MSG);
					}		
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_HOST_007 API Call :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName, "FNWL_PaymentTransfer", strReqBody, region, (String) data.getSessionData(Constants.S_FNWL_PAYMENTUS_URL));
			objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode, "");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for FNWL EPC PaymentUS API call  :: " + e);
			caa.printStackTrace(e);
		}
		
		if (strExitState.equalsIgnoreCase(Constants.SU)) {
			caa.createMSPKey_FNWL(caa, data, "EPC_PAYMENTUS", "API_SUCCESS");
			data.setSessionData("PAYMENTUS_FLAG", "Y");
		}
		else {
			caa.createMSPKey_FNWL(caa, data, "EPC_PAYMENTUS", "API_FAILURE");
			data.setSessionData("PAYMENTUS_FLAG", "N");
		}
		
		return strExitState;
	}

}
