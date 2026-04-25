package com.farmers.fws.fgia;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GetSelectedMenuOption extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String selectedOption = "";
			String strMenuName = data.getElementHistory().get(data.getElementHistory().size()-2);
			String strMenuExit = data.getExitStateHistory().get(data.getExitStateHistory().size()-1);
			if(strMenuName.contains(".")) {
				strMenuName = strMenuName.split("\\.")[1];
			}
			if (strMenuExit.equalsIgnoreCase(Constants.DONE)) {
				strExitState = data.getElementData(strMenuName, "value");
				if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_001) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = Constants.FWSARC_PWD_RESET;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = Constants.FWSARS_POLICIES;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = Constants.FWSARS_NEW_BUSINESS;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
					selectedOption = Constants.FWSARC_AGENT_360_POLICIES;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_4)) {
					selectedOption = Constants.FWSARC_AGENT_360_NEW_BUSINESS;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_5)) {
					/*
					 * if(data.getSessionData(Constants.S_PAK_II_OPEN) != null) { String pakOpen =
					 * (String) data.getSessionData(Constants.S_PAK_II_OPEN);
					 * if(pakOpen.equalsIgnoreCase("Open")) { selectedOption =
					 * Constants.FWSARC_PAK_II_OPEN; }else { selectedOption =
					 * Constants.FWSARC_PAK_II_CLOSED; } }
					 */
					selectedOption = Constants.FWSARC_PAK_II_OPEN;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_6)) {
					selectedOption = "UNDERWRITING APPROVAL";
				}
				// Above mentioned extended DTMF 7 & 8 is added as new options for FWSARC Main Menu (FWSARC_MN_002) by business Team
				
				else if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_7)) {
					selectedOption = Constants.FWSARC_PWRESET; 
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_8)) {
					selectedOption = Constants.FWSARC_CLAIMS; 
				}
				
				//START : FWS Commission Flow
				else if(strMenuName.equalsIgnoreCase(Constants.FWSC_MN_001) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = Constants.FWSC_COMMISSION; 
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSC_MN_001) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = Constants.FWSC_INDEPENDENT_AGENT;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSC_MN_001) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
					selectedOption = Constants.FWSC_PARTY_EXTENSION;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSCM_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = Constants.FWSCM_BROKER_INQUIRY;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSCM_MN_002) && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = Constants.FWSCM_BROKER_ISSUE;
				}
				//END : FWS Commission Flow
				
				//FGIA
				else if(strMenuName.equalsIgnoreCase("FGIA_MN_001") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = "PARTY EXTENSION";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_001") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "CUSTOMER";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_001") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
					selectedOption = "AGENT";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_002") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = "SALES";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_002") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "SERVICE";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_003") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = "PERSONAL LINE POLICY";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_003") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "COMMERCIAL";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_003") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
					selectedOption = "FLOOD POLICY";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_004") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = "PERSONAL LINE POLICY";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_004") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "COMMERCIAL";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_004") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
					selectedOption = "FLOOD";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_005") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "SAFECO";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_005") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
					selectedOption = "STATE AUTO";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_005") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_4)) {
					selectedOption = "TRAVELERS";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_005") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_5)) {
					selectedOption = "KEMPER";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_005") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_6)) {
					selectedOption = "HARTFORD";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_005") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_7)) {
					selectedOption = "ALL OTHER";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_006") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = "AUTO";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_006") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "HOME";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_007") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = "PROGRESSIVE";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_007") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "HARTFORD";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_007") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
					selectedOption = "FARMERS";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_007") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_4)) {
					selectedOption = "LIBERTY MUTUAL";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_007") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_5)) {
					selectedOption = "ALL OTHER";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_008") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = "SALES";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_008") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "SERVICE";
					
					//CS1200081 :Farmers Insurance | US | FGIA Commissions Prompt
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_008") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_6)) {
					selectedOption = "COMMISIONS";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_009") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = "PERSONAL LINE POLICY";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_009") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "COMMERCIAL";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_009") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
					selectedOption = "FLOOD POLICY";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_010") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
					selectedOption = "PERSONAL LINE POLICY";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_010") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
					selectedOption = "COMMERCIAL";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_010") && strExitState.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
					selectedOption = "FLOOD POLICY";
				}
				
			}else{
				strExitState = strMenuExit;
				data.addToLog(currElementName, "Please check the exit state of menu \""+strMenuName+"\" and exist state \""+strMenuExit+"\". It should be only as done");
				if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_001) && strExitState.equalsIgnoreCase(Constants.MAX_NOINPUT)) {
					selectedOption = Constants.FWSARC_NO_INPUT;
				}else if(strMenuName.equalsIgnoreCase(Constants.FWSARC_MN_001) && strExitState.equalsIgnoreCase(Constants.MAX_NOMATCH)) {
					selectedOption = Constants.FWSARC_NO_MATCH;
				
				}
				//START : FWS Commission Flow
				else if(strMenuName.equalsIgnoreCase(Constants.FWSCM_MN_002)) {
					selectedOption = Constants.FWSCM_BROKER_INQUIRY;
				}
				//END : FWS Commission Flow
				
				else if(strMenuName.equalsIgnoreCase("FGIA_MN_004")) {
					selectedOption = "NO SELECTION";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_005")) {
					selectedOption = "NO SELECTION";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_006")) {
					selectedOption = "NO SELECTION";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_007")) {
					selectedOption = "NO SELECTION";
				}else if(strMenuName.equalsIgnoreCase("FGIA_MN_008")) {
					selectedOption = "NO SELECTION";
				}else {
					selectedOption = strExitState;
				}
			}
			
			data.addToLog(currElementName, "Customer choosen the option \""+strExitState+"\" in the menuID "+strMenuName+"\"");

			//String menuSelectionKey = ((String)data.getSessionData("S_BU"))+":"+strMenuName+":"+selectedOption;
			if(selectedOption != null && !selectedOption.equals("")) {
				String menuSelectionKey = "FWS:"+strMenuName+":"+selectedOption;
				//menuSelectionKey = "COMMERCIAL:CMMF_MN_001:REAL TIME BILLING";
				data.addToLog(data.getCurrentElement(), "MSP : "+menuSelectionKey);
				data.setSessionData(Constants.S_MENU_SELCTION_KEY, menuSelectionKey);
			}
			
			
			
			

		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in GET_FWSARC_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
