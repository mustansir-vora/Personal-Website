package com.farmers.bc;

import java.util.LinkedList;
import java.util.Queue;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CMMF_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CMMF_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " CMMF_MN_001_VALUE : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CMMF_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CMMF_MN_001_MakeAPayment)) {
				strExitState = Constants.CMMF_MN_001_MakeAPayment;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CMMF_MN_001_AccInfo)) {
				strExitState = Constants.CMMF_MN_001_AccInfo;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CMMF_MN_001_MailingAddress)) {
				strExitState = Constants.CMMF_MN_001_MailingAddress;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CMMF_MN_001_WebsiteInfo)) {
				strExitState = Constants.CMMF_MN_001_WebsiteInfo;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CMMF_MN_001_FarmersRealTImeMailing)) {
				strExitState = Constants.CMMF_MN_001_FarmersRealTImeMailing;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CMMF_MN_001_OtherBilling)) {
				strExitState = Constants.CMMF_MN_001_OtherBilling;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			} 
			else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CMMF_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CMMF_MN_001_VALUE :: "+strExitState);
		data.setSessionData(Constants.S_INTENT, strExitState);
		
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CMMF_MN_001_"+menuExsitState) && !((String)data.getSessionData("CMMF_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CMMF_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Value of CMMF_MN_001 returned from Session : "+data.getSessionData(Constants.CMMF_MN_001_Make_a_Payment));
		data.addToLog(currElementName, "Final Value of Menu Exit State for CMMF_MN_001: "+menuExsitState);
		if(null != menuExsitState && !menuExsitState.isEmpty()) {
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, "COMMERCIAL"+":CMMF_MN_001:"+menuExsitState);
		String mspKey =(String) data.getSessionData(Constants.S_MENU_SELCTION_KEY);
		
		data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);

		data.addToLog(data.getCurrentElement(), "Value of S_LAST_5_MSP_VALUES :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));

		Queue<String> lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
		data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue :: " + lastFiveMSPValues);
		if (lastFiveMSPValues == null) {
			lastFiveMSPValues = new LinkedList<>();
		}

		lastFiveMSPValues.offer(mspKey);

		while (lastFiveMSPValues.size() > 5) {
			lastFiveMSPValues.poll();
		}

		data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);

		data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue After Operation :: " + lastFiveMSPValues);
		}
		return strExitState;
	}
}