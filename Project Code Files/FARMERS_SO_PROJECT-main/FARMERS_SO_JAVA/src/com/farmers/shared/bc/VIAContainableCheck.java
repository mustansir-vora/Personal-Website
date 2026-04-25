package com.farmers.shared.bc;

import java.util.HashMap;

import com.audium.server.logger.events.EndEvent;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

public class VIAContainableCheck extends DecisionElementBase{

	@Override
	public String doDecision(String currentElementName, DecisionElementData data) throws Exception {
		// TODO Auto-generated method stub
		String strExitState="SU";
		String policyLookupResp="";
		if(null!=data.getSessionData("S_VIA_HOST_001_RESPONSE") && "".equalsIgnoreCase((String)data.getSessionData("S_VIA_HOST_001_RESPONSE"))) {
			policyLookupResp= (String)data.getSessionData("S_VIA_HOST_001_RESPONSE");
		}
		String containableCount = getContainValuesfromHeaderDetailsMap(data, "S_ContainableCount");
		HashMap<String, String> headerDetails = null;
		headerDetails = (HashMap<String, String>)data.getSessionData("S_HEADER_DETAILS");
        
		if(policyLookupResp.equalsIgnoreCase("ER")){ 			
		    if(containableCount.equalsIgnoreCase("0") ){
		    	strExitState= "NO";
		       }else {
		    	   strExitState= "YES";
		       }
		}
		return strExitState;
	}
	@SuppressWarnings("unchecked")
	private String getContainValuesfromHeaderDetailsMap(DecisionElementData data, String pStrSessionVarName) {
		String str = null;
		HashMap<String, String> headerDetails = null;
		try {
			headerDetails = (HashMap<String, String>) data.getSessionData("S_HEADER_DETAILS");
			str = (null != headerDetails.get(pStrSessionVarName)) ? headerDetails.get(pStrSessionVarName) : "0";
			data.addToLog(pStrSessionVarName,  str);
		}
		catch (Exception e) {
			data.addToLog("Failed to get value from S_HEADER_DETAILS hashmap :: Object key [", pStrSessionVarName);
		}
		return str;
	}
	

}
