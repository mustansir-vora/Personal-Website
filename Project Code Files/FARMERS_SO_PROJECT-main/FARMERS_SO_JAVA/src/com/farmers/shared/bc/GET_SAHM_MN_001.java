package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SAHM_MN_001 extends DecisionElementBase 
{



	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String returnValue = (String) data.getElementData("SAHM_MN_001_Call","Return_Value");
			data.addToLog("Menu ID :",returnValue);
			data.setSessionData(Constants.SAHM_MN_001_VALUE, returnValue);
			if(returnValue != null && !returnValue.equals("")) 
			{
				if(returnValue.equalsIgnoreCase(Constants.GENERALPOLICY))
				{
					strExitState = Constants.GENERALPOLICY;	
					data.setSessionData(Constants.S_INTENT, returnValue);
				}
				else if(returnValue.equalsIgnoreCase(Constants.REPRENSTATIVE))
				{
					strExitState = Constants.REPRENSTATIVE;
				}
				else if(returnValue.equalsIgnoreCase(Constants.BILLINGANDPAYMENTS))
				{
					strExitState = Constants.BILLINGANDPAYMENTS;
					data.setSessionData(Constants.S_INTENT, returnValue);
					data.setSessionData("INTENT", "BILLING AND PAYMENTS");
				}
				else if(returnValue.equalsIgnoreCase(Constants.IDCARDS))
				{
					strExitState = Constants.IDCARDS;
					data.setSessionData(Constants.S_INTENT, returnValue);
				}
				else if(returnValue.equalsIgnoreCase(Constants.POLICYSTATUS))
				{
					strExitState = Constants.POLICYSTATUS;
					data.setSessionData(Constants.S_INTENT, returnValue);
				}
				else if(returnValue.equalsIgnoreCase(Constants.CLAIMS))
				{
					strExitState = Constants.CLAIMS;
					caa.createMSPKey(caa, data, "SAHM_MN_001", "CLAIMS");
					data.setSessionData(Constants.S_INTENT, returnValue);
				}	
				else if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) 
				{
					strExitState = Constants.NOINPUT;
				} else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) 
				{
					strExitState = Constants.NOMATCH;
				}
			}
			else 
			{
				data.addToLog("S_MENU_SELECTION_KEY", ((String)data.getSessionData("S_BU"))+"_SAHM_MN_001_"+returnValue);
				strExitState = Constants.ER;
			}

		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SAHM_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
		
		

	/*	data.addToLog(currElementName,"SAHM_MN_001 :: "+strExitState);
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("SAHM_MN_001_"+menuExsitState) && !((String)data.getSessionData("SAHM_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("SAHM_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for SAHM_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":SAHM_MN_001:"+menuExsitState);
		*/
		return strExitState;

	}

}

