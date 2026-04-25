package com.farmers.shared.bc;

import java.util.HashMap;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Take1stPolicy extends DecisionElementBase {

	@SuppressWarnings("unchecked")
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = (HashMap<String, HashMap<String, PolicyBean>>) data.getSessionData(Constants.S_POLICYDETAILS_MAP);
			for (String key : productPolicyMap.keySet()) {
				HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap.get(key);
				for (String key1 : policyDetailsMap.keySet()) {
					PolicyBean obj = (PolicyBean) policyDetailsMap.get(key1);
					data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
					data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
					data.setSessionData("DOB_CHECK","YES");
					data.setSessionData("ZIP_CHECK","YES");
					
					data.setSessionData(Constants.S_POLICY_NUM, key1);
					data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
					//data.setSessionData("POLICY_SYMBOL", key1);
					data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
					if(null != data.getSessionData("S_FLAG_FOREMOST_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FOREMOST_BU"))) {
						data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrPolicyProductCode()+key1);
					} else if(null != data.getSessionData("S_FLAG_FWS_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FWS_BU"))) {
						data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
					}
					strExitState = Constants.SU;;
					break;
				}
			}
			//DOB BYPASS CHANGE
			if(null==data.getSessionData(Constants.S_API_DOB) || data.getSessionData(Constants.S_API_DOB).toString().isEmpty()) {
				data.setSessionData("DOB_CHECK", "NO");
				data.addToLog("DOB_CHECK", data.getSessionData("DOB_CHECK").toString());
			}
			if(null==data.getSessionData(Constants.S_API_ZIP) || data.getSessionData(Constants.S_API_ZIP).toString().isEmpty()) {
				data.setSessionData("ZIP_CHECK", "NO");
				data.addToLog("ZIP_CHECK", data.getSessionData("ZIP_CHECK").toString());
			}
			
		//DOB BYPASS CHANGE
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in Take1stPolicy :: "+e);
			caa.printStackTrace(e);
		}

		return strExitState;
	}

}
