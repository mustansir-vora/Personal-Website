package com.farmers.shared.bc;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FNWL.PolicyBean_via_ANI;
import com.farmers.bean.FNWL.PolicyBean_via_PCN;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.servion.platform.utilities.ReturnVal;

public class FNWL_MN_011 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			String returnValue = (String) data.getElementData("FNWL_MN_011_Call", "Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_011 :: Menu Value : " + returnValue);
			data.setSessionData(Constants.FNWL_MN_011_VALUE, returnValue);

			if (null != returnValue && Constants.EmptyString != returnValue) {

				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_011", "NOMATCH");
				} else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_011", "NOINPUT");
				} else if (returnValue.equalsIgnoreCase(Constants.DONTHAVE)) {
					strExitState = Constants.DONTHAVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_011", "DONT_HAVE");
				} else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_011", "REPRESENTATIVE");
				} 
				else {
					strExitState = Constants.SU;
					data.setSessionData(Constants.FNWL_USER_ENTERED_DOB, returnValue);
					isDOBValid(data, caa, currElementName, returnValue);
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_MN_011 :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_011_VALUE :: "+strExitState);
		
		return strExitState;
	}
	
	public void isDOBValid(DecisionElementData data, CommonAPIAccess caa, String currElementName, String DOB) {
		
		try {
			HashMap<String, PolicyBean_via_PCN> policyMapviaPCN = (HashMap<String, PolicyBean_via_PCN>) data.getSessionData(Constants.FNWL_POLICYMAP_VIA_PCN);
			data.addToLog(currElementName, "Policy Map :: " + policyMapviaPCN);
			String policynum = (String) data.getSessionData(Constants.FNWL_POLICY_NUM);
			HashMap<String, PolicyBean_via_PCN> finalPolicyMap = new HashMap<String, PolicyBean_via_PCN>();
			int x = 0;
			
			if (null != policyMapviaPCN && policyMapviaPCN.size() > 0) {
				
				for (Map.Entry<String, PolicyBean_via_PCN> entry : policyMapviaPCN.entrySet()) {
					PolicyBean_via_PCN policyDet = entry.getValue();
					data.addToLog(currElementName, "Iterating through Policy Map via PCN");
					if (policyDet.getPolicyNumber().equalsIgnoreCase(policynum)) {
						
						SimpleDateFormat policysdf = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat inputsdf = new SimpleDateFormat("MM-dd-yyyy");
						
						String policyDOB = null != policyDet.getDateOfBirth() && !Constants.EmptyString.equalsIgnoreCase(policyDet.getDateOfBirth()) ? inputsdf.format(policysdf.parse(policyDet.getDateOfBirth())) : Constants.EmptyString;
						
						if (DOB.equalsIgnoreCase(policyDOB)) {
							data.addToLog(currElementName, "User entered DOB :: " + DOB + " :: matched with Policy DOB :: " + policyDOB);
							data.setSessionData(Constants.FNWL_DOB_MATCHED, Constants.TRUE);
							x = x + 1;
							finalPolicyMap.put("POLICY" + String.valueOf(x), policyDet);
							data.setSessionData(Constants.FNWL_POLICYMAP_VIA_PCN, finalPolicyMap);
						}
						else{
							if (null != data.getSessionData(Constants.FNWL_DOB_MATCHED) && Constants.TRUE.equalsIgnoreCase((String) data.getSessionData(Constants.FNWL_DOB_MATCHED)) ) {
								data.addToLog(currElementName, "DOB is already matched :: Keeping DOB matched flag TRUE");
							}
							else{
								data.setSessionData(Constants.FNWL_DOB_MATCHED, Constants.FALSE);
								data.addToLog(currElementName, "User entered DOB :: " + DOB + " :: did not match with Policy DOB :: " + policyDOB);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_MN_011 while checking if DOB is valid :: " + e);
			caa.printStackTrace(e);
		}
		
	}

}
