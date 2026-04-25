package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SMM_MN_001 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
	
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String returnValue = Constants.EmptyString;
		try {	
			String prefLang = (String) data.getSessionData("S_PREF_LANG");
			
			if (null != prefLang && "SP".equalsIgnoreCase(prefLang)) {
				returnValue = (String) data.getElementData("SP_SMM_MN_001_Call","Return_Value");
			}
			else {
				returnValue = (String) data.getElementData("SMM_MN_001_Call","Return_Value");
			}
			
			data.setSessionData(Constants.SMM_MN_001_VALUE, returnValue);
			data.addToLog(currElementName, "Menu ID : "+"SMM_MN_001"+" :: Menu Value : "+returnValue);
			
			
			if(returnValue != null && !returnValue.equals("")) 
			{
//				data.addToLog("S_MENU_SELECTION_KEY", ((String)data.getSessionData("S_BU"))+"_SMM_MN_001_"+returnValue);
				if(returnValue.equalsIgnoreCase(Constants.BILLINGANDPAYMENTS))
				{
					strExitState = Constants.BILLINGANDPAYMENTS;
					data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");
					data.setSessionData("INTENT", "BILLING AND PAYMENTS");
					caa.createMSPKey(caa, data, "SMM_MN_001", "BILLINGS&PAYMENTS");
				} else if (returnValue.equalsIgnoreCase(Constants.IDCARDS))
				{
					strExitState = Constants.IDCARDS;
					caa.createMSPKey(caa, data, "SMM_MN_001", "IDCARDS");
				}
				else if (returnValue.equalsIgnoreCase(Constants.SALESQUOTES))	
				{
					strExitState = Constants.SALESQUOTES;
					caa.createMSPKey(caa, data, "SMM_MN_001", "SALES/QUOTES");
				}
				else if (returnValue.equalsIgnoreCase(Constants.AGENTINFORMATIONS))	
				{
					strExitState = Constants.AGENTINFORMATIONS;
					caa.createMSPKey(caa, data, "SMM_MN_001", "AGENTINFO/CONTACT AN AGENT");
				}
				else if (returnValue.equalsIgnoreCase(Constants.CLAIMS))	
				{
					strExitState = Constants.CLAIMS;
					caa.createMSPKey(caa, data, "SMM_MN_001", "CLAIMS");
				}
				else if (returnValue.equalsIgnoreCase(Constants.REPRENSTATIVE))	
				{
					strExitState = Constants.REPRENSTATIVE;
					caa.createMSPKey(caa, data, "SMM_MN_001", "REPRESENTATIVE");
				}
				else if (returnValue.equalsIgnoreCase(Constants.CANCELPOLICY))
				{
					strExitState = Constants.CANCELPOLICY;
					data.setSessionData("S_CANCEL_POLICY_INTENT", "Y");
					data.setSessionData("S_IA_CUSTOMER_CHECK", "Y");
					caa.createMSPKey(caa, data, "SMM_MN_001", "CANCELPOLICY");
				}
				else if (returnValue.equalsIgnoreCase(Constants.WEBSITEHELP))
				{
					strExitState = Constants.WEBSITEHELP;
					caa.createMSPKey(caa, data, "SMM_MN_001", "WEBSITEHELP/PASSWORDRESET");
				}else if(returnValue.equalsIgnoreCase(Constants.POLICY_QUESTIONS)) {
					strExitState =returnValue;
					data.setSessionData(Constants.SHARED_AUTH_RETURN, Constants.STRING_YES);
					caa.createMSPKey(caa, data, "SMM_MN_001", "POLICYCHANGES&QUESTIONS");
				}else if(returnValue.equalsIgnoreCase(Constants.POLICY_CHANGES)) {
					strExitState =Constants.POLICY_QUESTIONS;
					data.setSessionData(Constants.SHARED_AUTH_RETURN, Constants.STRING_YES);
					caa.createMSPKey(caa, data, "SMM_MN_001", "POLICYCHANGES&QUESTIONS");
				}
				else if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) 
				{
					caa.createMSPKey(caa, data, "SMM_MN_001", "REPRESENTATIVE");
					strExitState = Constants.NOINPUT;
				} else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) 
				{
					caa.createMSPKey(caa, data, "SMM_MN_001", "REPRESENTATIVE");
					strExitState = Constants.NOMATCH;
				}
			}
			else 
			{
					strExitState = Constants.ER;
			}
 
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SMM_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
//		String menuExsitState = strExitState;
//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("SMM_MN_001_"+menuExsitState) && !((String)data.getSessionData("SMM_MN_001_"+menuExsitState)).isEmpty())
//			menuExsitState = (String)data.getSessionData("SMM_MN_001_"+menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for SMM_MN_001: "+menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":SMM_MN_001:"+menuExsitState);
		return strExitState;
	
	}
	
}


