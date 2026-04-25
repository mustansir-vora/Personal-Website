package com.farmers.shared.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SIDA_MN_002 extends DecisionElementBase 
{
	 static int MAX_VALID_YR = 9999; 
	 static int MIN_VALID_YR = 1800; 


	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
		
		
		

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SIDA_MN_002_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SIDA_MN_002_Call"+" :: Menu Value : "+strReturnValue);
			data.setSessionData(Constants.SIDA_MN_002_VALUE, strReturnValue);
			
			String regex = "(.)*(\\d)(.)*";      
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				data.setSessionData(Constants.S_TELEPHONENUMBER, strReturnValue);
				data.setSessionData(Constants.S_POLICY_NUM, null);
				strExitState = Constants.VALID;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
				strExitState = Constants.REPRENSTATIVE;
				data.setSessionData("LAST_MENU_NAME_REP", "SIDA_MN_002");
			} else if(strReturnValue.equalsIgnoreCase(Constants.KYCBA_MN_001_Dontknow)) {	
				strExitState = Constants.DONTHAVE;
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in GET_SIDA_MN_002 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"GET_SIDA_MN_002 : strExitState :: "+strExitState);
		data.setSessionData("SIDA_MN_002_ExitState", strExitState);
		return strExitState;
	
	
		
		
		
		
		
		
	 /* 	String yy = "";
    	String mm = "";
    	String DD = "";
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			
			
			String returnValue = (String) data.getElementData("SIDA_MN_002_Call","Return_Value");

			
			data.addToLog(currElementName, "Menu ID : "+"SIDA_MN_002"+" :: Menu Value : "+returnValue);
			data.addToLog("S_TELEPHONUMBER", returnValue);
			data.setSessionData(Constants.SIDA_MN_002_VALUE, returnValue);
			if(returnValue != null && !returnValue.equals("")) 
			{
					data.addToLog("S_MENU_SELECTION_KEY", ((String)data.getSessionData("S_BU"))+"_SIDA_MN_002_"+returnValue);	

				if(returnValue.length()==10||returnValue.length()==9||returnValue.length()>=1)
				{
					data.setSessionData("S_MENU_SELECTION_KEY", ((String)data.getSessionData("S_BU"))+"_SIDA_MN_001_"+returnValue);
					data.setSessionData("S_TELEPHONUMBER", returnValue);
					strExitState = "number valid";
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
				else
				{
					//number Invalid
					
					data.setSessionData("S_TELEPHONUMBER", returnValue);
					strExitState = "number Invalid";
					
				}
				
			}
			
		
			 else 
			{
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.setSessionData("S_MENU_SELECTION_KEY", ((String)data.getSessionData("S_BU"))+"_SIDA_MN_002_"+"Exception");
			data.addToLog(currElementName,"Exception in SIDA_MN_002 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SIDA_MN_002 :: "+strExitState);
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("SIDA_MN_002_"+menuExsitState) && !((String)data.getSessionData("SIDA_MN_002_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("SIDA_MN_002_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for SIDA_MN_002: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":SIDA_MN_002:"+menuExsitState);
		
		
		return strExitState;
	*/
	}
	
	/* static boolean isLeap(int year) 
	    { 
	        return (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)); 
	    } 
	    static boolean isValidDate(int y,int m, int d) 
	    { 

	        if (y > MAX_VALID_YR || y < MIN_VALID_YR)
	        {
	            return false; 
	        }if (m < 1 || m > 12)
	        {
	            return false; 
	        }
	        if (d < 1 || d > 31)
	        {
	            return false; 
	        }
	            if (m == 2)  
	        { 
	            if (isLeap(y))
	            {
	            return (d <= 29); 
	            }
	            else
	            {
	                return (d <= 28); 
	        }
	    }
	  
	        if (m == 4 || m == 6 || m == 9 || m == 11)
	        {
	        	   return (d <= 30); 	
	        }
	         
	  
	        return true; 
	    } 
	    */
	
}