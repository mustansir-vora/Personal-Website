package com.farmers.shared.bc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FNWL.PolicyBean_via_PCN;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_013 extends DecisionElementBase {
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			String returnValue = (String) data.getElementData("FNWL_MN_013_Call", "Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_013 :: Menu Value : " + returnValue);
			data.setSessionData(Constants.FNWL_MN_013_VALUE, returnValue);

			if (null != returnValue && Constants.EmptyString != returnValue) {

				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_013", "NOMATCH");
				} else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_013", "NOINPUT");
				} else if (returnValue.equalsIgnoreCase(Constants.DONTHAVE)) {
					strExitState = Constants.DONTHAVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_013", "DONT_HAVE");
				} else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_013", "REPRESENTATIVE");
				} 
				else {
					Boolean isValidInput = checkInput(returnValue, data, caa, currElementName);
					
					if (isValidInput) {
						strExitState = Constants.SU;
						data.setSessionData(Constants.FNWL_USER_ENTERED_SSN, returnValue);
					}
					
					compareUserAndPolicySSN(returnValue, data, caa, currElementName);
					
				}
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_MN_013 :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_013_VALUE :: "+strExitState);
		
		return strExitState;
	}

	private Boolean checkInput(String returnValue, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
			
		Boolean isValid = false;
		
		try {
			Pattern pattern = Pattern.compile("^[0-9]{4}$");
			isValid = pattern.matcher(returnValue).matches();
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_013 CheckInput Method :: "+e);
			caa.printStackTrace(e);
		}
		return isValid;
	}
	
	private void compareUserAndPolicySSN(String returnValue, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		try {
			
			HashMap<String, PolicyBean_via_PCN> policyMapviaPCN = (HashMap<String, PolicyBean_via_PCN>) data.getSessionData(Constants.FNWL_POLICYMAP_VIA_PCN);
			data.addToLog(currElementName, "Policy Map :: " + policyMapviaPCN);
			String policynum = null != (String) data.getSessionData(Constants.FNWL_POLICY_NUM) ? (String) data.getSessionData(Constants.FNWL_POLICY_NUM) : Constants.EmptyString;
			HashMap<String, PolicyBean_via_PCN> finalPolicyMap = new HashMap<String, PolicyBean_via_PCN>();
			int x = 0;
			
			if (null != policyMapviaPCN && policyMapviaPCN.size() > 0) {
				
				for (Map.Entry<String, PolicyBean_via_PCN> entry : policyMapviaPCN.entrySet()) {
					PolicyBean_via_PCN policyDetails = entry.getValue();
					data.addToLog(currElementName, "Iterating through Policy Map via PCN");
					
					if (policyDetails.getPolicyNumber().equalsIgnoreCase(policynum)) {
						
						if ((null != policyDetails.getSsn() && !Constants.EmptyString.equalsIgnoreCase(policyDetails.getSsn()) && policyDetails.getSsn().substring(policyDetails.getSsn().length() - 4).equalsIgnoreCase(returnValue)) || (null != policyDetails.getTaxID() && !Constants.EmptyString.equalsIgnoreCase(policyDetails.getTaxID()) && policyDetails.getTaxID().substring(policyDetails.getTaxID().length() - 4).equalsIgnoreCase(returnValue))) {
							data.addToLog(currElementName, "User Entered SSN :: " + returnValue + " :: matches the API SSN/TaxID :: Policy SSN :: " + policyDetails.getSsn() + " :: Policy TaxID :: " + policyDetails.getTaxID());
							data.setSessionData(Constants.FNWL_SSN_MATCHED, Constants.TRUE);
							x = x + 1;
							finalPolicyMap.put("POLICY" + String.valueOf(x), policyDetails);
							data.setSessionData(Constants.FNWL_POLICYMAP_VIA_PCN, finalPolicyMap);
						} else {
							if (null != data.getSessionData(Constants.FNWL_SSN_MATCHED) && Constants.TRUE.equalsIgnoreCase((String) data.getSessionData(Constants.FNWL_SSN_MATCHED)) ) {
								data.addToLog(currElementName, "SSN is already matched :: Keeping SSN matched flag TRUE");
							}
							else{
								data.addToLog(currElementName, "User Entered SSN :: " + returnValue + " :: does not match the API SSN/TaxID");
								data.setSessionData(Constants.FNWL_SSN_MATCHED, Constants.FALSE);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_013 compareUserAndPolicySSN Method :: "+e);
			caa.printStackTrace(e);
		}
		
		
	}

}
