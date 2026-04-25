package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SPPO_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		String strExitStateTemp = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SPPO_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "SPPO_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("SPPO_MN_001_VALUE", strReturnValue);

			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
				strExitStateTemp = Constants.NOINPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
				strExitStateTemp = Constants.NOMATCH;
				caa.createMSPKey(caa, data, "SPPO_MN_001", "OTHER");
			} else if (strReturnValue.equalsIgnoreCase(Constants.MOBILE_HOME)) {
				strExitState = Constants.MOBILE_HOME;
				strExitStateTemp = Constants.MOBILE_HOME;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "MOBILE HOME");
			} else if (strReturnValue.equalsIgnoreCase(Constants.SPECIALTY_DWELLING)) {
				strExitState = Constants.SPECIALTY_DWELLING;
				strExitStateTemp = Constants.SPECIALTY_DWELLING;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "SPECIALTY DWELLING");
			} else if (strReturnValue.equalsIgnoreCase(Constants.LAND_LORD)) {
				strExitState = Constants.LAND_LORD;
				strExitStateTemp = Constants.LAND_LORD;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "SPECIALTY DWELLING");
			} else if (strReturnValue.equalsIgnoreCase("Vacant Home")) {
				strExitState = "Vacant Home";
				strExitStateTemp = "Vacant Home";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "SPECIALTY DWELLING");
			} else if (strReturnValue.equalsIgnoreCase(Constants.SEASONAL_HOME)) {
				strExitState = Constants.SEASONAL_HOME;
				strExitStateTemp = Constants.SEASONAL_HOME;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "SPECIALTY DWELLING");
			} else if (strReturnValue.equalsIgnoreCase(Constants.MOTORCYCLE)) {
				strExitState = Constants.MOTORCYCLE;
				strExitStateTemp = Constants.MOTORCYCLE;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "MOTOR CYCLE OFFROAD");
			} else if (strReturnValue.equalsIgnoreCase(Constants.OFF_ROAD_VEHICLE)) {
				strExitState = Constants.OFF_ROAD_VEHICLE;
				strExitStateTemp = Constants.OFF_ROAD_VEHICLE;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "MOTOR CYCLE OFFROAD");
			} else if (strReturnValue.equalsIgnoreCase(Constants.MOTOR_HOME)) {
				strExitState = Constants.MOTOR_HOME;
				strExitStateTemp = Constants.MOTOR_HOME;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "MOTOR HOME TRAVELLER");
			} else if (strReturnValue.equalsIgnoreCase(Constants.TRAVEL_TRAILER)) {
				strExitState = Constants.TRAVEL_TRAILER;
				strExitStateTemp = Constants.TRAVEL_TRAILER;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "MOTOR HOME TRAVELLER");
			} else if (strReturnValue.equalsIgnoreCase(Constants.BOAT)) {
				strExitState = Constants.BOAT_OR_WATERCRAFT;
				strExitStateTemp = Constants.BOAT_OR_WATERCRAFT;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "BOAT WATERCRAFT");
			} else if (strReturnValue.equalsIgnoreCase(Constants.WATERCRAFT)) {
				strExitState = Constants.BOAT_OR_WATERCRAFT;
				strExitStateTemp = Constants.BOAT_OR_WATERCRAFT;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SPPO_MN_001", "BOAT WATERCRAFT");
			}else if (strReturnValue.equalsIgnoreCase("Other")) {
				strExitState = "Other";
				strExitStateTemp = "Other";
				caa.createMSPKey(caa, data, "SPPO_MN_001", "OTHER");
			}
			//			else if (strReturnValue.equalsIgnoreCase(Constants.DEFAULT)) {
			//				strExitState = Constants.DEFAULT;
			//				strExitStateTemp = Constants.DEFAULT;
			//			} else if (strReturnValue.equalsIgnoreCase(Constants.OTHER)) {
			//				strExitState = Constants.DEFAULT;
			//				strExitStateTemp = Constants.DEFAULT;
			//			} 
			else {
				strExitState = Constants.ER;
				strExitStateTemp = Constants.ER;
			}

			data.setSessionData("S_SPPO_MN_001_RETURN_VALUE", strExitStateTemp);
			data.addToLog(currElementName, "S_SPPO_MN_001_RETURN_VALUE :: " + strExitStateTemp);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SPPO_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "SPPO_MN_001_VALUE :: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("SPPO_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("SPPO_MN_001_" + menuExsitState)).isEmpty()) {
		//			data.addToLog(currElementName, "S_BU in SPPO_MN_001: " + (String) data.getSessionData("S_BU"));
		//			if (null != ((String) data.getSessionData(Constants.S_BU))) {
		//
		//				menuExsitState = (String) data.getSessionData("SPPO_MN_001_" + menuExsitState);
		//				data.addToLog(currElementName, "Final Value of Menu Exit State for SPPO_MN_001: " + menuExsitState);
		//				data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//						data.getSessionData(Constants.S_BU) + ":SPPO_MN_001:" + menuExsitState);
		//				data.addToLog(currElementName,
		//						"S_MENU_SELECTION_KEY SPPO_MN_001: " + (String) data.getSessionData("S_MENU_SELECTION_KEY"));
		//			}
		//		}
		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("SPPO_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("SPPO_MN_001_" + menuExsitState)).isEmpty()) {
		//			menuExsitState = (String) data.getSessionData("SPPO_MN_001_" + menuExsitState);
		//			data.addToLog(currElementName, "Final Value of Menu Exit State for SPPO_MN_001: " + menuExsitState);
		//			data.addToLog(currElementName, "S_BU in SPPO_MN_001: " + (String) data.getSessionData("S_BU"));
		//			if (null != ((String) data.getSessionData(Constants.S_BU))) {
		//				if (null != menuExsitState && !menuExsitState.isEmpty())
		//					data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//							data.getSessionData(Constants.S_BU) + ":SPPO_MN_001:" + menuExsitState);
		//				String menuSelectionKey = (String) data.getSessionData("S_MENU_SELCTION_KEY");
		//				data.addToLog(currElementName, "S_MENU_SELECTION_KEY SPPO_MN_001: " + menuSelectionKey);
		//			}
		//		}

		return strExitState;
	}
}