package com.farmers.bc;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SSN_FEIN_Check extends DecisionElementBase {

	@SuppressWarnings("unchecked")
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.STRING_NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			ArrayList<JSONObject> policiesObj = (ArrayList<JSONObject>) data.getSessionData(Constants.S_POLICY_LIST);
			String str_MN_FEIN_SSN_VALUE = (String) data.getSessionData(Constants.S_MN_FEIN_SSN_VALUE);
			String policyNum = Constants.EmptyString;
			int noOfPolicies = 0;
			
			data.addToLog(currElementName, " S_POLICY_LIST : "+policiesObj);
			data.addToLog(currElementName, " S_MN_FEIN_SSN_VALUE : "+str_MN_FEIN_SSN_VALUE);
			
			for(JSONObject obj : policiesObj) {
				JSONObject insuredDetailsObj = (JSONObject) obj.get("insuredDetails");
				JSONArray feinArr = (JSONArray)insuredDetailsObj.get("FEIN");
				String strFEIN_SSN = (String)feinArr.get(0);
				if(str_MN_FEIN_SSN_VALUE.equalsIgnoreCase(strFEIN_SSN)) noOfPolicies = noOfPolicies+1;
					policyNum = (String)obj.get("policyNumber");
            	data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_POLICYNUM);
            	data.setSessionData(Constants.S_CALLER_INPUT,policyNum);
            	data.setSessionData(Constants.S_VALID_POLICY_NUM,true);
			}
			data.addToLog(currElementName, " FEIN_SSN_MATCHED with no of policies : "+noOfPolicies);
			if (noOfPolicies == 1) {
				data.setSessionData(Constants.S_IS_SINGLE_POLICIY, Constants.STRING_YES);
				data.setSessionData(Constants.S_FEIN_SSN_MATCHED, Constants.STRING_YES);
				data.setSessionData(Constants.S_POLICY_NUM, policyNum);
				StrExitState = Constants.STRING_YES;
			}else if(noOfPolicies >1) {
				data.setSessionData(Constants.S_FEIN_SSN_MATCHED, Constants.STRING_YES);
				data.setSessionData(Constants.S_IS_SINGLE_POLICIY, Constants.STRING_NO);
				StrExitState = Constants.STRING_YES;
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, " FEIN_SSN_MATCHED with API response :: "+(String)data.getSessionData(Constants.S_FEIN_SSN_MATCHED));
		return StrExitState;
	}

}
