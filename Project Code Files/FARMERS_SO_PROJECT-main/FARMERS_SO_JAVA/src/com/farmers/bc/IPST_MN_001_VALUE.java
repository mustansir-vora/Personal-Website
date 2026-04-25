package com.farmers.bc;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class IPST_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER; 
		String strReturnValue = Constants.EmptyString;
		String strConfigState = Constants.EmptyString;
		String strStateCode = Constants.EmptyString;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		HashMap<String, String> stateMap = null;
		try {
			stateMap = new HashMap<>();
			strReturnValue = (String) data.getElementData("IPST_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " IPST_MN_001_VALUE : Return_Value :: "+strReturnValue);
			strConfigState = (String) data.getSessionData("S_CONFIG_STATE_CODE");
			data.addToLog(currElementName, "Config State Code :: " + strConfigState);
			data.setSessionData("IPST_MN_001_VALUE", strReturnValue);
			
            if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else {
				for(String strObject : strConfigState.split("\\|")) {
            		data.addToLog(currElementName, "State Code Object :: " + strObject);
        			stateMap.put(strObject.split(":")[0], strObject.split(":")[1]);
        		}
            	data.addToLog(currElementName, "State Code Map :: " + stateMap);
            	if(stateMap.containsKey(strReturnValue)) {
            		strStateCode = stateMap.get(strReturnValue);
            		data.addToLog(currElementName, "State Code :: "+strStateCode);
            		data.setSessionData("S_STATE_CODE", strStateCode);
            		data.setSessionData("S_STATE_NAME", strReturnValue);
            		data.setSessionData(Constants.S_STATENAME, strReturnValue);
        			data.setSessionData(Constants.S_STATECODE, strStateCode);
            		strExitState = Constants.SU;
            	}else {
            		strExitState = Constants.NOMATCH;
            		data.addToLog(currElementName, "Caller's State is not added in the config in the param of S_CONFIG_STATE_CODE");
            	}
			}
		}catch(Exception e) {
			data.addToLog(currElementName,"Exception in IPST_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	public static void main(String[] args) {
		String str = "Texas-Mountain:TX"+"|"+"Wyoming:WY"+"|"+"Wisconsin:WI"+"|"+"West Virginia:WV"+"|"+"Washington:WA"+"|"+"Virginia:VA"+"|"+"Vermont:VT"+"|"+"Utah:UT"+"|"+"Toll-Free:TF"+"|"+"Texas:TX"+"|"+"Tennessee:TN"+"|"+"South Dakota:SD"+"|"+"South Carolina:SC"+"|"+"Rhode Island:RI"+"|"+"Pennsylvania:PA"+"|"+"Oregon:OR"+"|"+"Oklahoma:OK"+"|"+"Ohio:OH"+"|"+"North Dakota:ND"+"|"+"North Carolina:NC"+"|"+"New York:NY"+"|"+"New Mexico:NM"+"|"+"New Jersey:NJ"+"|"+"New Hampshire:NH"+"|"+"Nevada:NV"+"|"+"NebraskaN:E"+"|"+"Montana:MT"+"|"+"Missouri:MO"+"|"+"Mississippi:MS"+"|"+"Minnesota:MN"+"|"+"Michigan:MI"+"|"+"Massachusetts:MA"+"|"+"Maryland:MD"+"|"+"Maine:ME"+"|"+"Louisiana:LA"+"|"+"Kentucky:KY"+"|"+"Kansas:KS"+"|"+"Iowa:IA"+"|"+"Indiana:IN"+"|"+"Illinois:IL"+"|"+"Idaho:ID"+"|"+"Hawaii:HI"+"|"+"Georgia:GA"+"|"+"Florida:FL"+"|"+"District Of Columbia:DC"+"|"+"Delaware:DE"+"|"+"Connecticut:CT"+"|"+"Colorado:CO"+"|"+"California:CA"+"|"+"Arkansas:AR"+"|"+"Arizona:AZ"+"|"+"All:ALL"+"|"+"Alaska:AK"+"|"+"Alabama:AL";
		int i =0;
		HashMap<String, String> stateMap = new HashMap<>();
		for(String strObject : str.split("\\|")) {
			System.out.println( i++ + "  : "+ "String Object :: " + strObject);
			stateMap.put(strObject.split(":")[0], strObject.split(":")[1]);
		}
		System.out.println("HashMap :: " + stateMap);
		
		
		
		String strtest = "New Virgina%%%%";
		String regex = "(.)*(\\d)(.)*";      
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(strtest);
        if(matcher.matches()) {
        	System.out.println("Matched");
        }else {
        	System.out.println("Matched");
        }
	}
	
}