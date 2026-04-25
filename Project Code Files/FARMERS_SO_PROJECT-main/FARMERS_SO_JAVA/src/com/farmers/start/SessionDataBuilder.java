package com.farmers.start;
import java.util.Enumeration;
import java.util.Properties;

import com.audium.server.global.ApplicationStartAPI;
import com.audium.server.session.CallStartAPI;
import com.farmers.util.Constants;
/**
 * The SessionBuilder Class is called CallStart method,
 * Read all the Properties from ivrconfig.properties
 * and set into Session ;
 */

public class SessionDataBuilder {
	ApplicationStartAPI  appStartAPI = null;
	String strElementName = "SessionDataBuilder";
	private Properties ivrConfigProp;
	Enumeration propertyNames;
	String strKey ="";
	String strValue="";

	/**
	 * Method to set the call specific information into session
	 *
	 */
	public void setToSessionData(CallStartAPI callStartAPI,String strIVRconfigName)
	{

		try{

			ivrConfigProp=(Properties)callStartAPI.getApplicationAPI().getApplicationData(strIVRconfigName);
			propertyNames =ivrConfigProp.propertyNames();

			while (propertyNames.hasMoreElements()) 
			{
				strKey = ((String) propertyNames.nextElement()).trim();
				strValue = ivrConfigProp.getProperty(strKey).trim();
				if(strKey.startsWith(Constants.SessionVarNameStartwith))
				{
					callStartAPI.setSessionData(strKey,strValue);						
				}
			}
			callStartAPI.addToLog(strElementName,"Global session variables set into session Successfully!");
		}catch(Exception e){
			callStartAPI.addToLog(strElementName,"Exception Occured while setting the session"+e.getMessage());
		}finally{
			propertyNames = null;			
		}
	}

	public void setToSessionDataAsIs(CallStartAPI callStartAPI,String strIVRconfigName)
	{

		try{

			ivrConfigProp=(Properties)callStartAPI.getApplicationAPI().getApplicationData(strIVRconfigName);
			propertyNames =ivrConfigProp.propertyNames();

			while (propertyNames.hasMoreElements()) 
			{
				strKey = ((String) propertyNames.nextElement()).trim();
				strValue = ivrConfigProp.getProperty(strKey).trim();
				callStartAPI.setSessionData(strKey,strValue);						
			}
			callStartAPI.addToLog(strElementName,"Global session variables set into session Successfully!");
		}catch(Exception e){
			callStartAPI.addToLog(strElementName,"Exception Occured while setting the session"+e.getMessage());
		}finally{
			propertyNames = null;			
		}
	}
}
