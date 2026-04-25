package com.farmers.shared.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_FSLS_MN_001 extends DecisionElementBase 
{


	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String returnValue = (String) data.getElementData("FSLS_MN_001_Call","Return_Value");

			data.addToLog(currElementName, "Menu ID : "+"FSLS_MN_001"+" :: Menu Value : "+returnValue);
			if(returnValue != null && !returnValue.equals("")) 
			{
				if(returnValue.equalsIgnoreCase(Constants.RVV)) 
				{
					strExitState = Constants.RVV;
					caa.createMSPKey(caa, data, "FSLS_MN_001", "RV");
					data.setSessionData("FSLS_MN_001_EXITSTATE", Constants.RVV);
					data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+data.getSessionData("FSLS_MN_001_EXITSTATE"));
				} 
				else if(returnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) 
				{
					strExitState = Constants.REPRENSTATIVE;
					caa.createMSPKey(caa, data, "FSLS_MN_001", "REPRESENTATIVE");
					data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+data.getSessionData("FSLS_MN_001_EXITSTATE"));
				}  
				else if(returnValue.equalsIgnoreCase(Constants.AUTO))
				{
					strExitState = Constants.AUTO;
					caa.createMSPKey(caa, data, "FSLS_MN_001", "AUTO");
					data.setSessionData("FSLS_MN_001_EXITSTATE", Constants.AUTO);
					data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+data.getSessionData("FSLS_MN_001_EXITSTATE"));
				}
				else if(returnValue.equalsIgnoreCase(Constants.HOMEOWNERS) || returnValue.equalsIgnoreCase("Homeowners") || returnValue.equalsIgnoreCase(Constants.HOME)) 
				{
					strExitState = Constants.HOMEOWNERS;
					caa.createMSPKey(caa, data, "FSLS_MN_001", "Homeowners");
					data.setSessionData("FSLS_MN_001_EXITSTATE", "Homeowners");
					data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+data.getSessionData("FSLS_MN_001_EXITSTATE"));
				}
				else if(returnValue.equalsIgnoreCase(Constants.MOTORCYCLE)) 
				{
					strExitState = Constants.MOTORCYCLE;
					caa.createMSPKey(caa, data, "FSLS_MN_001", "MOTOR CYCLE");
					data.setSessionData("FSLS_MN_001_EXITSTATE", Constants.MOTORCYCLE);
					data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+data.getSessionData("FSLS_MN_001_EXITSTATE"));
				} 
				else if(returnValue.equalsIgnoreCase(Constants.WATERCRAFT)) 
				{	
					strExitState = Constants.WATERCRAFT;
					caa.createMSPKey(caa, data, "FSLS_MN_001", "WATER CRAFT");
					data.setSessionData("FSLS_MN_001_EXITSTATE", Constants.WATERCRAFT);
					data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+data.getSessionData("FSLS_MN_001_EXITSTATE"));
				} 
				else if(returnValue.equalsIgnoreCase(Constants.SOMETHINGELSE) || returnValue.trim().equalsIgnoreCase("Something Else")) 
				{
					strExitState = Constants.SOMETHINGELSE;
					caa.createMSPKey(caa, data, "FSLS_MN_001", "SOMETHING ELSE");
					data.setSessionData("FSLS_MN_001_EXITSTATE", Constants.SOMETHINGELSE);
					data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+data.getSessionData("FSLS_MN_001_EXITSTATE"));
				} 
				else if(returnValue.equalsIgnoreCase(Constants.NOINPUT))
				{
					strExitState = Constants.NOINPUT;
					caa.createMSPKey(caa, data, "FSLS_MN_001", "NO SELECTION");
					data.setSessionData("FSLS_MN_001_EXITSTATE", Constants.NO_SELECTION);
					data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+data.getSessionData("FSLS_MN_001_EXITSTATE"));
				}
				else if(returnValue.equalsIgnoreCase(Constants.NOMATCH))
				{
					strExitState = Constants.NOMATCH;		
					caa.createMSPKey(caa, data, "FSLS_MN_001", "NO SELECTION");
					data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+strExitState);
				}	
			}

			else 
			{
				strExitState = Constants.ER;
				data.addToLog(currElementName, "EXITSTATE FSLS_MN_001 :: "+strExitState);
			}
		}

		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FSLS_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FSLS_MN_001 :: "+strExitState);
		//		String menuExsitState = strExitState;
		//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if(null != (String)data.getSessionData("FSLS_MN_001_"+menuExsitState) && !((String)data.getSessionData("FSLS_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("FSLS_MN_001_"+menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FSLS_MN_001: "+menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FSLS_MN_001:"+menuExsitState);
		//		data.addToLog(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FSLS_MN_001:"+menuExsitState);

		return strExitState;

	}

}

