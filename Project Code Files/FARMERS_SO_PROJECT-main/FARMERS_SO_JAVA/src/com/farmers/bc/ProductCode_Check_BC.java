package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class ProductCode_Check_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {

			String productCode = (String) data.getSessionData("POLICY_PRODUCT_CODE");

			if (productCode != null && !productCode.isEmpty()) {
				strExitState = checkProductCode(currElementName, data, productCode);
				data.addToLog(currElementName, "ProductCode_Check_BC :: the product code is not null :: " + productCode);
				data.addToLog(currElementName, "Exit State After Checking Product Code :: " + strExitState);
			} 
			else {
				strExitState = Constants.STRING_NO;
				data.addToLog(currElementName, "ProductCode_Check_BC :: the product code is null :: " + productCode);
				data.addToLog(currElementName, "Exit State After Checking Product Code :: " + strExitState);
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in ProductCode_Check_BC :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "ProductCode_Check_BC :: " + strExitState);

		return strExitState;
	}
	
	public String checkProductCode(String currElementName, DecisionElementData data, String productCode) {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		
		try {
			if (productCode.equalsIgnoreCase("077")) {
				strExitState = setMSP(currElementName, data, "TRAVEL TRAILER");
			}
			else if (productCode.equalsIgnoreCase("103") || productCode.equalsIgnoreCase("105") || productCode.equalsIgnoreCase("106") || productCode.equalsIgnoreCase("107") || productCode.equalsIgnoreCase("444")) {
				strExitState = setMSP(currElementName, data, "MOBILE HOME");
			}
			else if (productCode.equalsIgnoreCase("255")) {
				strExitState = setMSP(currElementName, data, "MOTOR HOME");
			}
			else if (productCode.equalsIgnoreCase("276")) {
				strExitState = setMSP(currElementName, data, "MOTORCYCLE");
			}
			else if (productCode.equalsIgnoreCase("381")) {
				strExitState = setMSP(currElementName, data, "SPECIALITY DWELLING");
			}
			else if (productCode.equalsIgnoreCase("601") || productCode.equalsIgnoreCase("602")) {
				strExitState = setMSP(currElementName, data, "REPRESENTATIVE");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in checkProductCode Method in ProductCode_Check_BC Class :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	public String setMSP(String currElementName, DecisionElementData data, String productType) {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			if (currElementName.equalsIgnoreCase("PRODUCT_CODE_CHECK")) {
				//data.setSessionData(Constants.S_MENU_SELECTION_KEY, "Foremost:FMAA_MN_003:"+productType);
				caa.createMSPKey(caa, data, "FMAA_MN_003", productType);
				data.addToLog(currElementName, "Menu :: Existing Policy :: " + productType);
				strExitState = Constants.STRING_YES;
			}
			else if (currElementName.equalsIgnoreCase("PRODUCT_CODE_CHECK_1")) {
				//data.setSessionData(Constants.S_MENU_SELECTION_KEY, "Foremost:FMAA_MN_002:"+productType);
				caa.createMSPKey(caa, data, "FMAA_MN_002", productType);
				data.addToLog(currElementName, "Menu :: Other Billing Questions :: " + productType);
				strExitState = Constants.STRING_YES;
			}
			else if (currElementName.equalsIgnoreCase("PRODUCT_CODE_CHECK_2")) {
				//data.setSessionData(Constants.S_MENU_SELECTION_KEY, "Foremost:FMAA_MN_004:"+productType);
				caa.createMSPKey(caa, data, "FMAA_MN_004", productType);
				data.addToLog(currElementName, "Menu :: Quotes :: " + productType);
				strExitState = Constants.STRING_YES;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in setMSP Method in ProductCode_Check_BC Class :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
}