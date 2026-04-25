package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FMOM_MN_004_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		String strExitStateTemp = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FMOM_MN_004_Call", "Return_Value");
			data.addToLog(currElementName, "FMOM_MN_004_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("FMOM_MN_004_VALUE", strReturnValue);
			
			if (null != strReturnValue) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NO_INPUT;
					strExitStateTemp = Constants.NO_INPUT;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
					strExitStateTemp = Constants.NO_MATCH;
				} else if (strReturnValue.equalsIgnoreCase(Constants.MOBILE_HOME)) {
					strExitState = Constants.MOBILE_HOME;
					strExitStateTemp = Constants.MOBILE_HOME;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "MOBILE HOME");
				} else if (strReturnValue.contains("Dwelling")) {
					strExitState = Constants.SPECIALTY_DWELLING;
					strExitStateTemp = Constants.SPECIALTY_DWELLING;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "SPECIALITY DWELLING");
				} else if (strReturnValue.equalsIgnoreCase(Constants.LAND_LORD)) {
					strExitState = Constants.SPECIALTY_DWELLING;
					strExitStateTemp = Constants.SPECIALTY_DWELLING;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "SPECIALITY DWELLING");
				} else if (strReturnValue.equalsIgnoreCase("Vacant Home")) {
					strExitState = Constants.SPECIALTY_DWELLING;
					strExitStateTemp = Constants.SPECIALTY_DWELLING;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "VACANT HOME");
				} else if (strReturnValue.equalsIgnoreCase(Constants.SEASONAL_HOME)) {
					strExitState = Constants.SPECIALTY_DWELLING;
					strExitStateTemp = Constants.SPECIALTY_DWELLING;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "SEASONAL HOME");
				} else if (strReturnValue.equalsIgnoreCase(Constants.MOTORCYCLE)) {
					strExitState = Constants.MOTORCYCLE;
					strExitStateTemp = Constants.MOTORCYCLE;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "MOTORCYCLE");
				} else if (strReturnValue.equalsIgnoreCase(Constants.MOTOR_HOME)) {
					strExitState = Constants.MOTOR_HOME;
					strExitStateTemp = Constants.MOTOR_HOME;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "MOTOR HOME");
				} else if (strReturnValue.equalsIgnoreCase(Constants.TRAVEL_TRAILER)) {
					strExitState = Constants.MOTOR_HOME;
					strExitStateTemp = Constants.MOTOR_HOME;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "TRAVEL TRAILER");
				} else if (strReturnValue.contains(Constants.BOAT) || strReturnValue.contains(Constants.WATERCRAFT)) {
					strExitState = Constants.BOAT_OR_WATERCRAFT;
					strExitStateTemp = Constants.BOAT_OR_WATERCRAFT;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "WATERCRAFT");
				} else if (strReturnValue.equalsIgnoreCase(Constants.OFF_ROAD_VEHICLE)) {
					strExitState = Constants.MOTORCYCLE;
					strExitStateTemp = Constants.MOTORCYCLE;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "OFF ROAD VEHICLE");
				} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					strExitStateTemp = Constants.REPRESENTATIVE;
					caa.createMSPKey(caa, data, "FMOM_MN_004", "REPRESENTATIVE");
				} else {
					strExitState = Constants.ER;
					strExitStateTemp = Constants.ER;
					data.addToLog(currElementName, "FMOM_MN_004 if the value from GDF is null :: " + strReturnValue);
				}
			} else {
				strExitState = Constants.ER;
				strExitStateTemp = Constants.ER;
				data.addToLog(currElementName, "FMOM_MN_004 if the value from GDF is null :: " + strReturnValue);
			}
			data.setSessionData("S_FMOM_MN_004_RETURN_VALUE", strExitStateTemp);
			data.addToLog(currElementName, "S_FMOM_MN_004_RETURN_VALUE :: " + strExitStateTemp);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FMOM_MN_004_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FMOM_MN_004_VALUE :: " + strExitState);

//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("FMOM_MN_004_" + menuExsitState)
//				&& !((String) data.getSessionData("FMOM_MN_004_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("FMOM_MN_004_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for FMOM_MN_004: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":FMOM_MN_004:" + menuExsitState);
//		data.addToLog(currElementName,
//				"S_MENU_SELECTION_KEY FMOM_MN_004: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}