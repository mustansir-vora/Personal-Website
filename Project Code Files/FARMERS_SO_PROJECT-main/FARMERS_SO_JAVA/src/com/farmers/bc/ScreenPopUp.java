package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class ScreenPopUp  extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String screenPopUp = (String) data.getSessionData(Constants.S_SCREEN_POPUP_KEY);
			//String strBU = (String) data.getSessionData(Constants.S_BU);
			//String mappingKey =  strBU+"_"+screenPopUp;
			
			String strCiscoSRM = (String)data.getApplicationAPI().getApplicationData(Constants.A_CISCO_SRM_CTI_API);
			String strKM2 = (String)data.getApplicationAPI().getApplicationData(Constants.A_KM2_API);
			String strClearHarbor = (String)data.getApplicationAPI().getApplicationData(Constants.A_CLEAR_HARBOR);
			String strFWSCode = (String)data.getApplicationAPI().getApplicationData(Constants.A_FWS_SRM_CTI);
			String strCommercial = (String)data.getApplicationAPI().getApplicationData(Constants.A_COMM_SRM_CTI);
			String strAPEX = (String)data.getApplicationAPI().getApplicationData(Constants.A_APEX);
			String strAFNI = (String)data.getApplicationAPI().getApplicationData(Constants.A_AFNI);
			
			data.addToLog("screenPopUp Key :", screenPopUp);
			data.addToLog("strCiscoSRM :", strCiscoSRM);
			data.addToLog("strKM2:", strKM2);
			data.addToLog("strClearHarbor", strClearHarbor);
			data.addToLog("strFWSCode", strFWSCode);
			data.addToLog("strCommercial", strCommercial);
			data.addToLog("strAPEX", strAPEX);
			data.addToLog("strAFNI", strAFNI);
			String brand ="";
			if(strCiscoSRM!=null &&screenPopUp!=null&& strCiscoSRM.contains(screenPopUp)) {
				brand = GetBrand(strCiscoSRM, screenPopUp);
				strExitState = Constants.A_CISCO_SRM_CTI_API;
			} else if(strKM2!=null &&screenPopUp!=null&& strKM2.contains(screenPopUp)) {
				brand = GetBrand(strKM2, screenPopUp);
				strExitState = Constants.A_KM2_API;
			} else if(strClearHarbor!=null &&screenPopUp!=null&& strClearHarbor.contains(screenPopUp)) {
				brand = GetBrand(strClearHarbor, screenPopUp);
				strExitState = Constants.A_CLEAR_HARBOR;
			} else if(strFWSCode!=null &&screenPopUp!=null&& strFWSCode.contains(screenPopUp)) {
				brand = GetBrand(strFWSCode, screenPopUp);
				strExitState = Constants.A_FWS_SRM_CTI;
			} else if(strCommercial!=null &&screenPopUp!=null&& strCommercial.contains(screenPopUp)) {
				brand = GetBrand(strCommercial, screenPopUp);
				strExitState = Constants.A_COMM_SRM_CTI;
			} else if(strAPEX!=null &&screenPopUp!=null&& strAPEX.contains(screenPopUp)) {
				brand = GetBrand(strAPEX, screenPopUp);
				strExitState = Constants.A_APEX;
			} else if(strAFNI!=null &&screenPopUp!=null&& strAFNI.contains(screenPopUp)) {
				brand = GetBrand(strAFNI, screenPopUp);
				strExitState = Constants.A_AFNI;
			}
			data.setSessionData(Constants.S_BRAND,brand);
			data.addToLog("brand :", brand);
			data.addToLog("Exit State :", strExitState);
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in ScreenPopUp :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"ScreenPopUp :: "+strExitState);
		return strExitState;
	}
	
public String GetBrand (String completeList,String screenPOP) {
	String brand = "";
	String[] completeArr = completeList.split(",");
	for (String brandandPOP : completeArr) {
		String[] brandandPOPArr= brandandPOP.split("-");
		if(screenPOP.equalsIgnoreCase(brandandPOPArr[0])) {
			brand = brandandPOPArr[1];
			break;
		}
	}
	
	return brand;
	
}

public static void main(String[] args) {
	ScreenPopUp oo = new ScreenPopUp();
	System.out.println(oo.GetBrand("Farmers AFNI-SO,Farmers AFNI Sales-SO,FWS AFNI-FWS", "FWS AFNI"));
}
}
