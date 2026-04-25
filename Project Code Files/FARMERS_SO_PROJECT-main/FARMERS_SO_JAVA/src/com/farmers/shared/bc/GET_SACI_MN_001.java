package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SACI_MN_001 extends DecisionElementBase 
{



	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
	
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			
//Representative,Claims,Billing and Payments,ID Cards,Agent Information
			
			String returnValue = (String) data.getElementData("SACI_MN_001_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SAHM_MN_002"+" :: Menu Value : "+returnValue);
			data.setSessionData(Constants.SACI_MN_001_VALUE, returnValue);
			if(returnValue != null && !returnValue.equals("")) 
			{
					data.addToLog("S_MENU_SELECTION_KEY", ((String)data.getSessionData("S_BU"))+"_SAHM_MN_002_"+returnValue.toUpperCase());
				
				if(returnValue.equalsIgnoreCase(Constants.MAINMENU))
				{
					strExitState = Constants.MAINMENU;
					
				}
				else if(returnValue.equalsIgnoreCase(Constants.REPRENSTATIVE) || returnValue.equalsIgnoreCase("Transfer") || returnValue.equalsIgnoreCase(Constants.AGENT))
				{
					
					strExitState = Constants.REPRENSTATIVE;
				}
				else if(returnValue.equalsIgnoreCase(Constants.AGENTINFORMATIONS))
				{
					strExitState = Constants.AGENTINFORMATIONS;
					
				}
				else if(returnValue.equalsIgnoreCase(Constants.POLICYSTATUS))
				{
					strExitState = Constants.POLICYSTATUS;
					
				}
				else if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) 
				{
				
						strExitState = Constants.NOINPUT;
				} else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) 
				{
					strExitState = Constants.NOMATCH;
				} else if(returnValue.equalsIgnoreCase(Constants.STRING_YES)) 
				{
					strExitState = Constants.STRING_YES;
				}  else if(returnValue.equalsIgnoreCase(Constants.STRING_NO))
				{
					strExitState = Constants.STRING_NO;
				}
			}
			 else 
			{
				data.addToLog("S_MENU_SELECTION_KEY", ((String)data.getSessionData("S_BU"))+"_SAHM_MN_002_"+returnValue);
				strExitState = Constants.ER;
			}
 
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SAHM_MN_002 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SACI_MN_001 :: "+strExitState);
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("SACI_MN_001_"+menuExsitState) && !((String)data.getSessionData("SACI_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("SACI_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for SACI_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":SACI_MN_001:"+menuExsitState);
		return strExitState;
	}
	
}

