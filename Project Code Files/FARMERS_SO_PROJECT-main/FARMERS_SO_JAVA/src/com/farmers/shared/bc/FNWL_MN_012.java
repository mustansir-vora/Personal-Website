package com.farmers.shared.bc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FNWL.PolicyBean_via_PCN;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_012 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			String returnValue = (String) data.getElementData("FNWL_MN_012_Call", "Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_012 :: Menu Value : " + returnValue);
			data.setSessionData(Constants.FNWL_MN_012_VALUE, returnValue);

			if (null != returnValue && Constants.EmptyString != returnValue) {

				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_012", "NOMATCH");
				} else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_012", "NOINPUT");
				} else if (returnValue.equalsIgnoreCase(Constants.DONTHAVE)) {
					strExitState = Constants.DONTHAVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_012", "DONT_HAVE");
				} else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_012", "REPRESENTATIVE");
				} 
				else {
					Boolean isValidInput = checkInput(returnValue, data, caa, currElementName);
					
					if (isValidInput) {
						strExitState = Constants.SU;
						data.setSessionData(Constants.FNWL_USER_ENTERED_ZIP, returnValue);
					}
					
					compareUserAndPolicyZIP(returnValue,data,caa,currElementName);
					
				}
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_MN_012 :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_012_VALUE :: "+strExitState);
		
		return strExitState;
	}

	private Boolean checkInput(String returnValue, DecisionElementData data, CommonAPIAccess caa,String currElementName) {
		Boolean isValid = false;
		
		try {
			Pattern pattern = Pattern.compile("^[0-9]{5}$");
			isValid = pattern.matcher(returnValue).matches();
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_012 CheckInput Method :: "+e);
			caa.printStackTrace(e);
		}
		return isValid;
	}
	
	private void compareUserAndPolicyZIP(String returnValue, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		try {
			
			HashMap<String, PolicyBean_via_PCN> policyMapviaPCN = (HashMap<String, PolicyBean_via_PCN>) data.getSessionData(Constants.FNWL_POLICYMAP_VIA_PCN);
			data.addToLog(currElementName, "Policy Map :: " + policyMapviaPCN);
			String policyNum = (String) data.getSessionData(Constants.FNWL_POLICY_NUM);
			HashMap<String, PolicyBean_via_PCN> finalPolicyMap = new HashMap<String, PolicyBean_via_PCN>();
			int x = 0;
			
			if (null != policyMapviaPCN && policyMapviaPCN.size() > 0) {
				
				for (Map.Entry<String, PolicyBean_via_PCN> entry : policyMapviaPCN.entrySet()) {
					PolicyBean_via_PCN policyObj = entry.getValue();
					data.addToLog(currElementName, "Iterating through Policy Map via PCN");
					
					if (policyObj.getPolicyNumber().equalsIgnoreCase(policyNum)) {
						String policyZIP = null != policyObj.getZip() && policyObj.getZip().length() > 5 ? policyObj.getZip().substring(0, 5) : policyObj.getZip();
						
						if(null != policyZIP && policyZIP.equals(returnValue)) {
							data.addToLog(currElementName, "User Entered ZIP :: " + returnValue + " :: matches the API ZIP :: " + policyZIP);
							data.setSessionData(Constants.FNWL_ZIP_MATCHED, Constants.TRUE);
							x = x + 1;
							finalPolicyMap.put("POLICY" + String.valueOf(x), policyObj);
							data.setSessionData(Constants.FNWL_POLICYMAP_VIA_PCN, finalPolicyMap);
						}else {
							if (null != data.getSessionData(Constants.FNWL_ZIP_MATCHED) && Constants.TRUE.equalsIgnoreCase((String) data.getSessionData(Constants.FNWL_ZIP_MATCHED)) ) {
								data.addToLog(currElementName, "ZIP is already matched :: Keeping ZIP matched flag TRUE");
							}
							else{
								data.addToLog(currElementName, "User Entered ZIP :: " + returnValue + " :: does not match the API ZIP + " + policyZIP);
								data.setSessionData(Constants.FNWL_ZIP_MATCHED, Constants.FALSE);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_012 compareUserAndPolicyZIP Method :: "+e);
			caa.printStackTrace(e);
		}
		
	}


}
