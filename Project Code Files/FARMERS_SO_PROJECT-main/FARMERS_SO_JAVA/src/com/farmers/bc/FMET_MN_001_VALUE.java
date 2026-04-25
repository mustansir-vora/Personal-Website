package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.PolicyNumManupulation;

public class  FMET_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FMET_MN_001_call","Return_Value");
			data.addToLog(currElementName, " FMET_MN_001_call : Return_Value :: "+strReturnValue);
			String regex = "(.)*(\\d)(.)*";      
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				//            	data.setSessionData(Constants.S_POLICY_NUM, strReturnValue);
				//            	data.setSessionData(Constants.S_IS_POLICYNUM_PROVIDED, Constants.TRUE);
				PolicyNumManupulation objPolicyNumManupulation = new PolicyNumManupulation();
				String alteredPolicy = objPolicyNumManupulation.policyNumManupulation(strReturnValue, data);
				data.addToLog(currElementName, "Menu ID : "+"FMET_MN_001"+" :: Altered : "+alteredPolicy);
				data.setSessionData(Constants.S_POLICY_NUM, alteredPolicy);
				data.setSessionData(Constants.S_IS_POLICYNUM_PROVIDED, Constants.TRUE);
				strExitState = Constants.VALID;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FMET_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}

		data.addToLog(currElementName," FMET_MN_001_VALUE :: "+strExitState);
		data.setSessionData(Constants.S_INTENET, strExitState);

//		String menuExsitState = strExitState;
//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData(" FMET_MN_001_"+menuExsitState) && !((String)data.getSessionData(" FMET_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData(" FMET_MN_001_"+menuExsitState);
//		data.addToLog(currElementName, "Value of  FMET_MN_001 returned from Session : "+data.getSessionData(Constants. FMET_MN_001_VALUE));
//		data.addToLog(currElementName, "Final Value of Menu Exit State for  FMET_MN_001: "+menuExsitState);
//		if(null != menuExsitState && !menuExsitState.isEmpty()) data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+": FMET_MN_001:"+menuExsitState);

		return strExitState;
	}
}