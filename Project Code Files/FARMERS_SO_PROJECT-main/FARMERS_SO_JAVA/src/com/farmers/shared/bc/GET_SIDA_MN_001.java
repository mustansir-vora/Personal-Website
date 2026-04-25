package com.farmers.shared.bc;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.PolicyNumManupulation;

public class GET_SIDA_MN_001 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			
			String strBU = (String) data.getSessionData(Constants.S_BU);
			
			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");
			
			data.addToLog(currElementName, " PolicyLookup : strBU :: "+strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A__LOB : "+strFWSCode);
			data.addToLog(currElementName, " rFOREMOST_LOB_LIST :: "+strForemostCode);
			
			if(strBristolCode!=null && null != strBU && strBristolCode.contains(strBU)) {
				data.addToLog(currElementName, "BU is matched with Bristol LOB");
				data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
			} else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				//S_FLAG_FDS_BU
				data.addToLog(currElementName, "BU is matched with Farmers LOB");
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
			} else if(strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				data.addToLog(currElementName, "BU is matched with Foremost LOB");
				data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
			} else if(strFWSCode!=null && strBU!=null && strFWSCode.contains(strBU)) {
				data.addToLog(currElementName, "BU is matched with FWS LOB");
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				data.addToLog(currElementName, "BU is matched with 21st LOB");
				data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
			} else {
				data.addToLog(currElementName, "no BU is matched with LOB or brands check");
			}
			
			data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU")+ " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
			
			String SpecialtyCollectionFlag = (String) data.getSessionData("SPECIALTY_COLLECTION_FLAG");
			String strReturnValue = Constants.EmptyString;
			if (null != SpecialtyCollectionFlag && "TRUE".equalsIgnoreCase(SpecialtyCollectionFlag)) {
				strReturnValue = (String) data.getElementData("SPECIALTY_SIDA_MN_001_Call","Return_Value");
			}
			else {
				strReturnValue = (String) data.getElementData("SIDA_MN_001_Call","Return_Value");
			}
			
			data.addToLog(currElementName, "Menu ID : "+"SIDA_MN_001"+" :: Menu Value : "+strReturnValue);
			data.setSessionData(Constants.SIDA_MN_001_VALUE, strReturnValue);

			String regex = "(.)*(\\d)(.)*";      
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				PolicyNumManupulation objPolicyNumManupulation = new PolicyNumManupulation();
				String alteredPolicy = objPolicyNumManupulation.policyNumManupulation(strReturnValue, data);
				data.addToLog(currElementName, "Menu ID : "+"SIDA_MN_001"+" :: Altered : "+alteredPolicy);
				data.setSessionData(Constants.S_POLICY_NUM, alteredPolicy);
				data.setSessionData(Constants.S_POLICY_NUM, alteredPolicy);
				data.setSessionData(Constants.S_CALLER_INPUT, alteredPolicy);
				data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_POLICYNUM);
				strExitState = Constants.VALID;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
				strExitState = Constants.REPRENSTATIVE;
				if (null != SpecialtyCollectionFlag && "TRUE".equalsIgnoreCase(SpecialtyCollectionFlag)) {
					data.setSessionData("LAST_MENU_NAME_REP", "SPECIALTY_SIDA_MN_001");
				}
				else {
					data.setSessionData("LAST_MENU_NAME_REP", "SIDA_MN_001");
				}
			} else if(strReturnValue.equalsIgnoreCase(Constants.DONTHAVE)) {	
				strExitState = Constants.DONTHAVE;
				data.setSessionData("DontHaveItPrompt", "Let's try this another way");
			}else if(strReturnValue.equalsIgnoreCase("Hold on")) {	
				strExitState = "Hold on";
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in GET_SIDA_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"GET_SIDA_MN_001 : strExitState :: "+strExitState);
		data.setSessionData("SIDA_MN_001_ExitState", strExitState);
		return strExitState;

	}
}
