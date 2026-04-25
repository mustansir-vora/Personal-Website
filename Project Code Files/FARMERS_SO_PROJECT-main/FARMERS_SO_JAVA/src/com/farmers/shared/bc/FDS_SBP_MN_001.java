package com.farmers.shared.bc;

import java.util.ArrayList;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FDS_SBP_MN_001 extends DecisionElementBase 
{
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String returnValue = (String) data.getElementData("FDS_SBP_MN_001_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"FDS_SBP_MN_001"+" :: Menu Value : "+returnValue);
			data.setSessionData("FDS_SBP_MN_001_VALUE", returnValue);
			if(returnValue != null && !returnValue.equals("")) {
				//strExitState = returnValue;

				if(returnValue.equalsIgnoreCase(Constants.LOOKUP_ANOTHER_POLICY)) {
					strExitState = Constants.LOOKUP_ANOTHER_POLICY;
				}
				else if(returnValue.equalsIgnoreCase(Constants.BILLING_QUESTIONS)) {
					createMSPKey(caa, data, "SBP_MN_001", "BILLING QUESTIONS");
					strExitState = Constants.BILLING_QUESTIONS;
				}
				else if(returnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
					createMSPKey(caa, data, "SBP_MN_001", "REPRESENTATIVE");
					strExitState = Constants.REPRENSTATIVE;
				}
				else if(returnValue.equalsIgnoreCase(Constants.MAINMENU)) {
					strExitState = Constants.MAINMENU;
				}
				else if(returnValue.equalsIgnoreCase(Constants.LIENHOLDER)) {
					createMSPKey(caa, data, "SBP_MN_001", "LIENHOLDER");
					data.setSessionData(Constants.S_CALLLER_TYPE,"03");
					strExitState = Constants.LIENHOLDER;
				}
				else if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				}
				else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				}
			}

		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SBP_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SBP_MN_001 :: "+strExitState);
		return strExitState;

	}

	private void createMSPKey(CommonAPIAccess caa, DecisionElementData data, String menuID, String menuValue) {
		try {

			String strBU = (String) data.getSessionData("S_UNIQUE_BU");
			String strCategory = (String) data.getSessionData(Constants.S_CATEGORY);
			String strCategoryFlag = "N";
			data.addToLog(data.getCurrentElement(), "Value of S_CATEGORY : " + strCategory);
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

			if(strFMSTAARP!=null && strCategory!=null && strFMSTAARP.contains(strCategory)) {
				strBU = Constants.BU_FOREMOST_AARP;
				strCategoryFlag = "Y";
			}else if(strFMSTAGENT!=null && strCategory!=null && strFMSTAGENT.contains(strCategory)) {
				strBU = Constants.BU_FOREMOST_AGENT;
				strCategoryFlag = "Y";
			}else if(strFMSTCUST!=null && strCategory!=null && strFMSTCUST.contains(strCategory)) {
				strBU = Constants.BU_FOREMOST_CUST;
				strCategoryFlag = "Y";
			}else if(strFMSTUSAA!=null && strCategory!=null && strFMSTUSAA.contains(strCategory)) {
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
				String mspKey = strBU+":"+menuID+":"+menuValue;
				data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
				data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);
			}

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in GET_SAHM_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
	}

}