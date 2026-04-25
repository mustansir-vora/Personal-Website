package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FGIA_MN_Common_VALUE extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		String selectedOption = Constants.EmptyString;
		
		try {
			String strReturnValue = (String) data.getElementData(currElementName.replace("_VALUE", "_Call"), "Return_Value");
			data.addToLog(currElementName, currElementName + " :: DTMF Menu Return Value :: " +strReturnValue);
			data.setSessionData(currElementName, strReturnValue);
			
			if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
				strExitState = Constants.DTMF_KEY_PRESS_1;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
				strExitState = Constants.DTMF_KEY_PRESS_2;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
				strExitState = Constants.DTMF_KEY_PRESS_3;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_4)) {
				strExitState = Constants.DTMF_KEY_PRESS_4;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_5)) {
				strExitState = Constants.DTMF_KEY_PRESS_5;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_6)) {
				strExitState = Constants.DTMF_KEY_PRESS_6;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_7)) {
				strExitState = Constants.DTMF_KEY_PRESS_7;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_8)) {
				strExitState = Constants.DTMF_KEY_PRESS_8;
			}
			if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_9)) {
				strExitState = Constants.DTMF_KEY_PRESS_9;
			}
			
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in " + currElementName + " Exit State Mapping :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			
			if (currElementName.equalsIgnoreCase("FGIA_MN_001_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "PARTY EXTENSION";
					break;
				case "2":
					selectedOption = "CUSTOMER";
					break;
				case "3":
					selectedOption = "AGENT";
					break;
				}
			}
			else if (currElementName.equalsIgnoreCase("FGIA_MN_002_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "SALES";
					break;

				case "2":
					selectedOption = "SERVICE";
					break;
				}
			}
			else if (currElementName.equalsIgnoreCase("FGIA_MN_003_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "PERSONAL LINE POLICY";
					break;
				
				case "2":
					selectedOption = "COMMERCIAL";
					break;
				
				case "3":
					selectedOption = "FLOOD POLICY";
					break;
				}
			}
			else if (currElementName.equalsIgnoreCase("FGIA_MN_004_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "PERSONAL LINE POLICY";
					break;
				
				case "2":
					selectedOption = "COMMERCIAL";
					break;
				
				case "3":
					selectedOption = "FLOOD";
					break;
				}
			}
			else if (currElementName.equalsIgnoreCase("FGIA_MN_005_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "PROGRESSIVE";
					break;
				
				case "2":
					selectedOption = "SAFECO";
					break;
				
				case "3":
					selectedOption = "STATE AUTO";
					break;
					
				case "4":
					selectedOption = "TRAVELERS";
					break;
					
				case "5":
					selectedOption = "KEMPER";
					break;
					
				case "6":
					selectedOption = "HARTFORD";
					break;
					
				case "7":
					selectedOption = "ALL OTHER";
					break;
				}
			}
			else if (currElementName.equalsIgnoreCase("FGIA_MN_006_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "AUTO";
					break;

				case "2":
					selectedOption = "HOME";
					break;
				}
			}
			else if (currElementName.equalsIgnoreCase("FGIA_MN_007_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "PROGRESSIVE";
					break;
					
				case "2":
					selectedOption = "HARTFORD";
					break;
					
				case "3":
					selectedOption = "FARMERS";
					break;
					
				case "4":
					selectedOption = "LIBERTY MUTUAL";
					break;
					
				case "5":
					selectedOption = "ALL OTHER";
					break;
				}
			}
			else if (currElementName.equalsIgnoreCase("FGIA_MN_008_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "SALES";
					break;
					
				case "2":
					selectedOption = "SERVICE";
					break;
					
				case "6":
					selectedOption = "COMMISIONS";
					break;
				}
			}
			else if (currElementName.equalsIgnoreCase("FGIA_MN_009_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "PERSONAL LINE POLICY";
					break;
					
				case "2":
					selectedOption = "COMMERCIAL";
					break;
					
				case "3":
					selectedOption = "FLOOD POLICY";
					break;
				}
			}
			else if (currElementName.equalsIgnoreCase("FGIA_MN_010_VALUE")) {
				switch (strExitState) {
				case "1":
					selectedOption = "PERSONAL LINE POLICY";
					break;
					
				case "2":
					selectedOption = "COMMERCIAL";
					break;
					
				case "3":
					selectedOption = "FLOOD POLICY";
					break;
				}
			}
			
			caa.createMSPKey(caa, data, currElementName.replace("_VALUE", ""), selectedOption);
			
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in " + currElementName + " Menu Selection Option Mapping :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

}
