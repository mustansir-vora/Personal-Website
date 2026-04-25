package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class OSPM_MN_006_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		String strExitStateTemp = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {

			String authCheck = (String) data.getSessionData("IS_CALLED_SHARED_ID_AUTH");

			if (authCheck != null && "TRUE".equalsIgnoreCase(authCheck)) {

				String productCode = (String) data.getSessionData("POLICY_PRODUCT_CODE");
				String productType = (String) data.getSessionData(Constants.FINAL_PRODUCTTYPE);
				String subProductType = (String) data.getSessionData("POLICY_TYPE");

				if (productCode != null && !productCode.isEmpty()) {

					if (productCode.equals("077") || productCode.equals("255") || productCode.equals("77")) {
						strExitState = Constants.MOTOR_HOME;
						strExitStateTemp = Constants.MOTOR_HOME;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "MOTOR HOME TRAVEL");
					} else if ((productCode.equals("105") || productCode.equals("103") || productCode.equals("107")
							|| productCode.equals("106") || productCode.equals("672") || productCode.equals("444"))) {
						strExitState = Constants.MOBILE_HOME;
						strExitStateTemp = Constants.MOBILE_HOME;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "MOBILE HOME");
					} else if (productCode.equals("381")) {
						strExitState = Constants.SPECIALTY_DWELLING;
						strExitStateTemp = Constants.SPECIALTY_DWELLING;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "SPECIALTY DWELLING");
					} else if (productCode.equals("276")) {
						strExitState = Constants.MOTORCYCLE;
						strExitStateTemp = Constants.MOTORCYCLE;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "MOTOR CYCLE OFF ROAD");
					} else if (productCode.equals("601") || productCode.equals("602")) {
						strExitState = Constants.BOAT_OR_WATERCRAFT;
						strExitStateTemp = Constants.BOAT_OR_WATERCRAFT;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "BOAT WATERCRAFT");
					} else {
						strExitState = Constants.ER;
						strExitStateTemp = Constants.ER;
					}

					data.setSessionData("S_OSPM_MN_006_RETURN_VALUE", strExitStateTemp);
					data.addToLog(currElementName, "S_OSPM_MN_006_RETURN_VALUE :: " + strExitStateTemp);
				}
				else if(productType!=null && !productType.isEmpty()) {
					if (productType.equalsIgnoreCase("AUTO")) {
						strExitState = Constants.AUTO;
						strExitStateTemp = Constants.AUTO;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "AUTO");	
					}
					else if (productType.equalsIgnoreCase("HOME")) {
						data.addToLog(currElementName, "No DNIS set for Home Product - take default dnis " );
					}
					else if (productType.equalsIgnoreCase("MR")) {
						strExitState = Constants.BOAT_OR_WATERCRAFT;
						strExitStateTemp = Constants.BOAT_OR_WATERCRAFT;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "BOAT WATERCRAFT");	
					}
					else if (productType.equalsIgnoreCase("RV")) {
						if(subProductType!=null && !subProductType.isEmpty()) {
							if(subProductType.toLowerCase().equalsIgnoreCase("motor home policy") || subProductType.toLowerCase().equalsIgnoreCase("Motor Home") || subProductType.toLowerCase().equalsIgnoreCase("TX Commercial Mobile Home Liability Policy")) {
								strExitState = Constants.MOTOR_HOME;
								strExitStateTemp = Constants.MOTOR_HOME;
								caa.createMSPKey(caa, data, "OSPM_MN_006", "MOTOR HOME TRAVEL");
							}else if(subProductType.toLowerCase().equalsIgnoreCase("motorcycle policy") || subProductType.toLowerCase().equalsIgnoreCase("Motorcycle and Off Road Vehicle") 
									|| subProductType.toLowerCase().equalsIgnoreCase("Travel Trailer") || subProductType.toLowerCase().equalsIgnoreCase("Motorcycle")) {
								strExitState = Constants.MOTORCYCLE;
								strExitStateTemp = Constants.MOTORCYCLE;
								caa.createMSPKey(caa, data, "OSPM_MN_006", "MOTOR CYCLE OFF ROAD");
							}else {
								data.addToLog(currElementName, "No Sub product type set for RV - Product " );
							}
						}
					}
					else if (productType.equalsIgnoreCase("SP")) {
						if(subProductType!=null && !subProductType.isEmpty()) {
							if(subProductType.toLowerCase().equalsIgnoreCase("specialty dwelling") || subProductType.toLowerCase().equalsIgnoreCase("Specialty Dwelling")) {
								strExitState = Constants.SPECIALTY_DWELLING;
								strExitStateTemp = Constants.SPECIALTY_DWELLING;
								caa.createMSPKey(caa, data, "OSPM_MN_006", "SPECIALTY DWELLING");
							}
							else if(subProductType.toLowerCase().equalsIgnoreCase("mobile home policy") || subProductType.toLowerCase().equalsIgnoreCase("Mobile Home") || subProductType.toLowerCase().equalsIgnoreCase("Commercial Rental Mobile Home") || subProductType.toLowerCase().equalsIgnoreCase("TX Mobile Home")) {
								strExitState = Constants.MOBILE_HOME;
								strExitStateTemp = Constants.MOBILE_HOME;
								caa.createMSPKey(caa, data, "OSPM_MN_006", "MOBILE HOME");
							}else {
								data.addToLog(currElementName, "No Sub product type set for SP - Product " );
							}
						}

					}
					else if (productType.equalsIgnoreCase("UMBRELLA")) {
						data.addToLog(currElementName, "No DNIS set for Umbrella Product - take default dnis " );
					}else {
						data.addToLog(currElementName, "No Product Type Mapped " );
					}
				}else {
					data.addToLog(currElementName, "No product or policy set during authentication" );
				}
			} else {

				String strReturnValue = (String) data.getElementData("OSPM_MN_006_Call", "Return_Value");
				data.addToLog(currElementName, "OSPM_MN_006_Call : Return_Value :: " + strReturnValue);
				data.setSessionData("OSPM_MN_006_VALUE", strReturnValue);

				if (null != strReturnValue) {
					if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
						strExitState = Constants.NO_INPUT;
						strExitStateTemp = Constants.NO_INPUT;
					} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
						strExitState = Constants.NO_MATCH;
						strExitStateTemp = Constants.NO_MATCH;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "REPRESENTATIVE");;
					} else if (strReturnValue.equalsIgnoreCase(Constants.MOBILE_HOME)) {
						strExitState = Constants.MOBILE_HOME;
						strExitStateTemp = Constants.MOBILE_HOME;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "MOBILE HOME");
					} else if (strReturnValue.contains("Dwelling")) {
						strExitState = Constants.SPECIALTY_DWELLING;
						strExitStateTemp = Constants.SPECIALTY_DWELLING;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "SPECIALTY DWELLING");
					} else if (strReturnValue.equalsIgnoreCase(Constants.LAND_LORD)) {
						strExitState = Constants.SPECIALTY_DWELLING;
						strExitStateTemp = Constants.SPECIALTY_DWELLING;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "SPECIALTY DWELLING");
					} else if (strReturnValue.equalsIgnoreCase("Vacant Home")) {
						strExitState = Constants.SPECIALTY_DWELLING;
						strExitStateTemp = Constants.SPECIALTY_DWELLING;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "SPECIALTY DWELLING");
					} else if (strReturnValue.equalsIgnoreCase(Constants.SEASONAL_HOME)) {
						strExitState = Constants.SPECIALTY_DWELLING;
						strExitStateTemp = Constants.SPECIALTY_DWELLING;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "SPECIALTY DWELLING");
					} else if (strReturnValue.equalsIgnoreCase(Constants.MOTORCYCLE)) {
						strExitState = Constants.MOTORCYCLE;
						strExitStateTemp = Constants.MOTORCYCLE;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "MOTOR CYCLE OFF ROAD");
					} else if (strReturnValue.equalsIgnoreCase(Constants.OFF_ROAD_VEHICLE)) {
						strExitState = Constants.MOTORCYCLE;
						strExitStateTemp = Constants.MOTORCYCLE;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "MOTOR CYCLE OFF ROAD");
					} else if (strReturnValue.equalsIgnoreCase(Constants.MOTOR_HOME)) {
						strExitState = Constants.MOTOR_HOME;
						strExitStateTemp = Constants.MOTOR_HOME;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "MOTOR HOME TRAVEL");
					} else if (strReturnValue.equalsIgnoreCase(Constants.TRAVEL_TRAILER)) {
						strExitState = Constants.MOTOR_HOME;
						strExitStateTemp = Constants.MOTOR_HOME;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "MOTOR HOME TRAVEL");
					} else if (strReturnValue.contains(Constants.BOAT)
							|| strReturnValue.contains(Constants.WATERCRAFT)) {
						strExitState = Constants.BOAT_OR_WATERCRAFT;
						strExitStateTemp = Constants.BOAT_OR_WATERCRAFT;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "BOAT WATERCRAFT");
					} else if (strReturnValue.equalsIgnoreCase(Constants.AUTO)) {
						strExitState = Constants.AUTO;
						strExitStateTemp = Constants.AUTO;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "AUTO");
					} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
						strExitState = Constants.REPRESENTATIVE;
						strExitStateTemp = Constants.REPRESENTATIVE;
						caa.createMSPKey(caa, data, "OSPM_MN_006", "REPRESENTATIVE");
					} else {
						strExitState = Constants.ER;
						strExitStateTemp = Constants.ER;
						data.addToLog(currElementName,
								"OSPM_MN_006 if the value from GDF intent is ER :: " + strReturnValue);
					}
				} else {
					strExitState = Constants.ER;
					strExitStateTemp = Constants.ER;
					data.addToLog(currElementName, "OSPM_MN_006 if the value from GDF is null :: " + strReturnValue);
				}

			}
			data.setSessionData("S_OSPM_MN_006_RETURN_VALUE", strExitStateTemp);
			data.addToLog(currElementName, "S_OSPM_MN_006_RETURN_VALUE :: " + strExitStateTemp);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in OSPM_MN_006_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "OSPM_MN_006_VALUE :: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("OSPM_MN_006_" + menuExsitState)
		//				&& !((String) data.getSessionData("OSPM_MN_006_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("OSPM_MN_006_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for OSPM_MN_006: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":OSPM_MN_006:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY OSPM_MN_006: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}