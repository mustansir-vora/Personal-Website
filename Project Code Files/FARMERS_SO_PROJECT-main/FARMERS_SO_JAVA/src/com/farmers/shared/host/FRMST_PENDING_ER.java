package com.farmers.shared.host;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FRMST_PENDING_ER extends DecisionElementBase {
	
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String exitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
			
		try {
			
			createMSPKey(caa, data, "FRMST_PENDING_MN_001", "REPRESENTATIVE");
		    exitState=Constants.SU;
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FRMST_PENDING_ER doDecision call  :: "+e);
			caa.printStackTrace(e);
		}
		return exitState;
	}
	
	private void createMSPKey(CommonAPIAccess caa, DecisionElementData data, String menuID, String menuValue) {
		try {

			String strBU = (String) data.getSessionData("S_UNIQUE_BU");
			String strCategory = (String) data.getSessionData(Constants.S_CATEGORY);
			String strCategoryFlag = "N";
			String strEPCBrand = (String) data.getSessionData("S_EPC_BRAND_LABEL");
			String mspKey="";
			
			data.addToLog(data.getCurrentElement(), "Value of S_CATEGORY : " + strCategory);
			data.addToLog(data.getCurrentElement(), "Value of S_EPC_BRAND_LABEL : " + strEPCBrand);
			
			ArrayList<String>  strFMSTAARP = (ArrayList<String>) data.getSessionData("FMST_AARP_LOB_LIST");
			ArrayList<String>  strFMSTAGENT = (ArrayList<String>) data.getSessionData("FMST_AGENT_LOB_LIST");
			ArrayList<String>  strFMSTCUST = (ArrayList<String>) data.getSessionData("FMST_CUST_LOB_LIST");
			ArrayList<String>  strFMSTUSAA = (ArrayList<String>) data.getSessionData("FMST_USAA_LOB_LIST");
			ArrayList<String>  str21stHawaii = (ArrayList<String>) data.getSessionData("21st_HAWAII_LOB_LIST");

			data.addToLog(data.getCurrentElement(), " FMST_AARP_LOB_LIST : "+strFMSTAARP);
			data.addToLog(data.getCurrentElement(), " FMST_AGENT_LOB_LIST : "+strFMSTAGENT);
			data.addToLog(data.getCurrentElement(), " FMST_CUST_LOB_LIST : "+strFMSTCUST);
			data.addToLog(data.getCurrentElement(), " FMST_USAA_LOB_LIST : "+strFMSTUSAA);
			data.addToLog(data.getCurrentElement(), " 21st_HAWAII_LOB_LIST : "+str21stHawaii);
			
			if (null != data.getSessionData("S_FLAG_FOREMOST_BU") && data.getSessionData("S_FLAG_FOREMOST_BU").toString().equalsIgnoreCase(Constants.STRING_YES) && strEPCBrand.toUpperCase().contains("BW")) {
				strEPCBrand = "Foremost";
			}
			else if (null != data.getSessionData("S_FLAG_BW_BU") && data.getSessionData("S_FLAG_BW_BU").toString().equalsIgnoreCase(Constants.STRING_YES) && strEPCBrand.toUpperCase().contains("FOREMOST")) {
				strEPCBrand = "BW";
			}

			if(strFMSTAARP!=null && ((strCategory!=null && strFMSTAARP.contains(strCategory)) || (strEPCBrand != null && strFMSTAARP.contains(strEPCBrand)))) {
				strBU = Constants.BU_FOREMOST_AARP;
				strCategoryFlag = "Y";
			}else if(strFMSTAGENT!=null && strCategory!=null && strFMSTAGENT.contains(strCategory)) {
				strBU = Constants.BU_FOREMOST_AGENT;
				strCategoryFlag = "Y";
			}else if(strFMSTCUST!=null && strCategory!=null && strFMSTCUST.contains(strCategory)) {
				strBU = Constants.BU_FOREMOST_CUST;
				strCategoryFlag = "Y";
			}else if(strFMSTUSAA!=null && ((strCategory!=null && strFMSTUSAA.contains(strCategory)) || (strEPCBrand != null && strFMSTUSAA.contains(strEPCBrand)))) {
				strBU = Constants.BU_FOREMOST_USAA;
				strCategoryFlag = "Y";
			}else if(str21stHawaii!=null && strCategory!=null && str21stHawaii.contains(strCategory)) {
				strBU = Constants.BU_21STHAWAII;
				strCategoryFlag = "Y";
			}
			else {
				caa.createMSPKey(caa, data, menuID, menuValue);
			}

			if(null != strCategoryFlag && !strCategoryFlag.isEmpty() && "Y".equalsIgnoreCase(strCategoryFlag)) {
				mspKey = strBU+":"+menuID+":"+menuValue;
				data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
				data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);
			}
			
			Queue<String> lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue :: " + lastFiveMSPValues);
			if (lastFiveMSPValues == null) {
				lastFiveMSPValues = new LinkedList<>();
			}

			lastFiveMSPValues.offer(mspKey);

			while (lastFiveMSPValues.size() > 5) {
				lastFiveMSPValues.poll();
			}

			data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);

			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue After Operation :: " + lastFiveMSPValues);

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in GET_SAHM_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
	}
	
}
