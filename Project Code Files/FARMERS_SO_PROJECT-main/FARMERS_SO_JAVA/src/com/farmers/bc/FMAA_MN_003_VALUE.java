package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FMAA_MN_003_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {

				String strReturnValue = (String) data.getElementData("FMAA_MN_003_Call", "Return_Value");
				data.addToLog(currElementName, "FMAA_MN_003_Call : Return_Value :: " + strReturnValue);

				if (null != strReturnValue) {
					if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
						strExitState = Constants.NO_INPUT;
					} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
						strExitState = Constants.NO_MATCH;
					} else if (strReturnValue.equalsIgnoreCase(Constants.MOBILE_HOME)) {
						strExitState = Constants.MOBILE_HOME;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "MOBILE HOME");
					} else if (strReturnValue.contains("Dwelling")) {
						strExitState = Constants.SPECIALTY_DWELLING;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "SPECIALITY DWELLING");
					} else if (strReturnValue.equalsIgnoreCase(Constants.LAND_LORD)) {
						strExitState = Constants.SPECIALTY_DWELLING;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "SPECIALITY DWELLING");
					} else if (strReturnValue.equalsIgnoreCase("Vacant Home")) {
						strExitState = Constants.SPECIALTY_DWELLING;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "SPECIALITY DWELLING");
					} else if (strReturnValue.equalsIgnoreCase(Constants.SEASONAL_HOME)) {
						strExitState = Constants.SPECIALTY_DWELLING;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "SPECIALITY DWELLING");
					} else if (strReturnValue.equalsIgnoreCase(Constants.MOTORCYCLE)) {
						strExitState = Constants.MOTORCYCLE;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "MOTORCYCLE");
					} else if (strReturnValue.equalsIgnoreCase(Constants.MOTOR_HOME)) {
						strExitState = Constants.MOTOR_HOME;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "MOTOR HOME");
					} else if (strReturnValue.equalsIgnoreCase(Constants.TRAVEL_TRAILER)) {
						strExitState = Constants.MOTOR_HOME;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "TRAVEL TRAILER");
					} else if (strReturnValue.equalsIgnoreCase(Constants.OFF_ROAD_VEHICLE)) {
						strExitState = Constants.OFF_ROAD_VEHICLE;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "OFF ROAD VEHICLE");
					} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
						strExitState = Constants.REPRESENTATIVE;
						caa.createMSPKey(caa, data, "FMAA_MN_003", "REPRESENTATIVE");
					}
				} else {
					strExitState = Constants.ER;
					data.addToLog(currElementName, "FMAA_MN_003 if the value from GDF is null :: " + strReturnValue);
				}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FMAA_MN_003_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FMAA_MN_003_VALUE :: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("FMAA_MN_003_" + menuExsitState)
		//				&& !((String) data.getSessionData("FMAA_MN_003_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("FMAA_MN_003_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FMAA_MN_003: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":FMAA_MN_003:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY FMAA_MN_003: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}