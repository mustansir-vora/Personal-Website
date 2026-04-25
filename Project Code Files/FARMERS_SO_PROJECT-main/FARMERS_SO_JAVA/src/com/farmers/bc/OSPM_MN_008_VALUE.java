package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class OSPM_MN_008_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		String strExitStateTemp = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("OSPM_MN_008_Call", "Return_Value");
			data.addToLog(currElementName, "OSPM_MN_008_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("OSPM_MN_008_VALUE", strReturnValue);

			if (null != strReturnValue) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NO_INPUT;
					strExitStateTemp = Constants.NO_INPUT;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
					strExitStateTemp = Constants.NO_MATCH;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "REPRESENTATIVE");
				} else if (strReturnValue.equalsIgnoreCase(Constants.MOBILE_HOME)) {
					strExitState = Constants.MOBILE_HOME;
					strExitStateTemp = Constants.MOBILE_HOME;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "MOBILE HOME");
				} else if (strReturnValue.contains("Dwelling")) {
					strExitState = Constants.SPECIALTY_DWELLING;
					strExitStateTemp = Constants.SPECIALTY_DWELLING;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "SPECIALTY DWELLING");
				} else if (strReturnValue.equalsIgnoreCase(Constants.LAND_LORD)) {
					strExitState = Constants.SPECIALTY_DWELLING;
					strExitStateTemp = Constants.SPECIALTY_DWELLING;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "SPECIALTY DWELLING");
				} else if (strReturnValue.equalsIgnoreCase("Vacant Home")) {
					strExitState = Constants.SPECIALTY_DWELLING;
					strExitStateTemp = Constants.SPECIALTY_DWELLING;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "SPECIALTY DWELLING");
				} else if (strReturnValue.equalsIgnoreCase(Constants.SEASONAL_HOME)) {
					strExitState = Constants.SPECIALTY_DWELLING;
					strExitStateTemp = Constants.SPECIALTY_DWELLING;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "SPECIALTY DWELLING");
				} else if (strReturnValue.equalsIgnoreCase(Constants.MOTORCYCLE)) {
					strExitState = Constants.MOTORCYCLE;
					strExitStateTemp = Constants.MOTORCYCLE;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "MOTOR CYCLE OFF ROAD");
				} else if (strReturnValue.equalsIgnoreCase(Constants.OFF_ROAD_VEHICLE)) {
					strExitState = Constants.MOTORCYCLE;
					strExitStateTemp = Constants.MOTORCYCLE;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "MOTOR CYCLE OFF ROAD");
				} else if (strReturnValue.equalsIgnoreCase(Constants.MOTOR_HOME)) {
					strExitState = Constants.MOTOR_HOME;
					strExitStateTemp = Constants.MOTOR_HOME;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "MOTOR HOME TRAVEL");
				} else if (strReturnValue.equalsIgnoreCase(Constants.TRAVEL_TRAILER)) {
					strExitState = Constants.MOTOR_HOME;
					strExitStateTemp = Constants.MOTOR_HOME;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "MOTOR HOME TRAVEL");
				} else if (strReturnValue.contains(Constants.BOAT) || strReturnValue.contains(Constants.WATERCRAFT)) {
					strExitState = Constants.BOAT_OR_WATERCRAFT;
					strExitStateTemp = Constants.BOAT_OR_WATERCRAFT;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "BOAT WATERCRAFT");
				} else if (strReturnValue.equalsIgnoreCase(Constants.AUTO)) {
					strExitState = Constants.AUTO;
					strExitStateTemp = Constants.AUTO;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "AUTO");
				} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					strExitStateTemp = Constants.REPRESENTATIVE;
					caa.createMSPKey(caa, data, "OSPM_MN_008", "REPRESENTATIVE");
				} else {
					strExitState = Constants.ER;
					strExitStateTemp = Constants.ER;
					data.addToLog(currElementName,
							"OSPM_MN_008 if the value from GDF intent is ER :: " + strReturnValue);
				}
			} else {
				strExitState = Constants.ER;
				strExitStateTemp = Constants.ER;
				data.addToLog(currElementName, "OSPM_MN_008 if the value from GDF is null :: " + strReturnValue);
			}

			data.setSessionData("S_OSPM_MN_008_RETURN_VALUE", strExitStateTemp);
			data.addToLog(currElementName, "S_OSPM_MN_008_RETURN_VALUE :: " + strExitStateTemp);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in OSPM_MN_008_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "OSPM_MN_008_VALUE :: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("OSPM_MN_008_" + menuExsitState)
		//				&& !((String) data.getSessionData("OSPM_MN_008_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("OSPM_MN_008_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for OSPM_MN_008: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":OSPM_MN_008:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY OSPM_MN_008: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}