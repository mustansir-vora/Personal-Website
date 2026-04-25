package com.farmers.start;

import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Properties;

import com.audium.server.AudiumException;
import com.audium.server.proxy.StartCallInterface;
import com.audium.server.session.CallStartAPI;
import com.farmers.util.Constants;

public class CallStart implements StartCallInterface{
	private Properties ivrConfigProp;
	Enumeration propertyNames;

	public void onStartCall(CallStartAPI callStartAPI) throws AudiumException {
		
		
		String originalANI = Constants.EmptyString; //Caller ANI
		String originalDNIS = Constants.EmptyString; //Caller DNIS
		String BUID = Constants.EmptyString; //Business Unit ID
		String Lang = Constants.EmptyString; 
		String CGUID = Constants.EmptyString; //Call GUID
		String ANI = Constants.EmptyString; //Spoofed ANI - Used for testing
		String DNIS = Constants.EmptyString; //Spoofed DNIS - Used for testing
		String CCallID = Constants.EmptyString; //Hyphen Delimited RCK-RCKD value
		String OCallID = Constants.EmptyString; //RCKRCKD value without any delimition
		String BE = Constants.EmptyString; //Business Entity Value
		String VXMLIP = Constants.EmptyString; //IP Address of VXML Server
		String voiceName = Constants.EmptyString; //Voice Name for initial invocation
		
		try {
			
			originalANI = (String) callStartAPI.getSessionData(Constants.getOriginalANI);
			originalDNIS = (String) callStartAPI.getSessionData(Constants.getOriginalDNIS);
			BUID = (String) callStartAPI.getSessionData(Constants.getBUID);
			CGUID = (String) callStartAPI.getSessionData(Constants.getCGUID);
			ANI = (String) callStartAPI.getSessionData(Constants.getOANI);
			DNIS = (String) callStartAPI.getSessionData(Constants.getODNIS);
			CCallID = (String) callStartAPI.getSessionData(Constants.getCCallID);
			OCallID = (String) callStartAPI.getSessionData(Constants.getOCallID);
			BE = (String) callStartAPI.getSessionData(Constants.getBE);
			Lang = (String) callStartAPI.getSessionData(Constants.getActiveLang);
			
			InetAddress thisIp =InetAddress.getLocalHost();
			VXMLIP = thisIp.getHostAddress();
			
			if (null != Lang && Lang.equalsIgnoreCase(Constants.SP)) {
				Lang = Constants.es_US;
				voiceName = Constants.ES_VoiceName;
			}
			else {
				Lang = Constants.en_US;
				voiceName = Constants.EN_VoiceName;
			}
			
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Original ANI :: " + originalANI);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Original DNIS :: " + originalDNIS);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "BUID :: " + BUID);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Language :: " + Lang);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "CGUID :: " + CGUID);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "OANI :: " + ANI);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "ODNIS :: " + DNIS);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "CCallID :: " + CCallID);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "OCallID :: " + OCallID);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "BE :: " + BE);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "VXML IP Address :: " + VXMLIP);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Updated Lang :: " + Lang);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Voice Name :: " + voiceName);
			
			//callStartAPI.setSessionData(Constants.OriginalANI, originalANI);
			//callStartAPI.setSessionData(Constants.OriginalDNIS, originalDNIS);
			//callStartAPI.setSessionData(Constants.BUID, BUID);
			callStartAPI.setSessionData(Constants.ActiveLang, Lang);
			//callStartAPI.setSessionData(Constants.CGUID, CGUID);
			//callStartAPI.setSessionData(Constants.OANI, ANI);
			//callStartAPI.setSessionData(Constants.ODNIS, DNIS);
			//callStartAPI.setSessionData(Constants.CCallID, CCallID);
			//callStartAPI.setSessionData(Constants.OCallID, OCallID);
			//callStartAPI.setSessionData(Constants.BE, BE);
			callStartAPI.setSessionData(Constants.VoiceName, voiceName);
			callStartAPI.setSessionData(Constants.DF_GUID, CGUID);
			callStartAPI.setSessionData(Constants.DF_BUID, BUID);
			callStartAPI.setSessionData(Constants.DF_ONPREMCALLID, OCallID);
			callStartAPI.setSessionData(Constants.DF_ORIGINALANI, originalANI);
			callStartAPI.setSessionData(Constants.DF_ORIGINALDNIS, originalDNIS);
			callStartAPI.setSessionData(Constants.DF_OANI, ANI);
			callStartAPI.setSessionData(Constants.DF_ODNIS, DNIS);
			callStartAPI.setSessionData(Constants.Lang, Lang);
			
			
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Session Variables set");
			
			setConfigToSessionData(callStartAPI,Constants.APP_LEVEL_IVRCONFIG_PROP_OBJECT);
			
			String start_event = (String) callStartAPI.getSessionData(Constants.S_START_EVENT_NAME);
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Start Event name fetched from Config :: " + start_event);
			callStartAPI.setSessionData(Constants.EventName, start_event);
			
			setDefaultAudioPath(Lang, VXMLIP, callStartAPI);
			
		} catch (Exception e) {
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Exception occurred while retrieving ICM variables :: " + e.getMessage());
		}
	}
	
	//Start - Set data starting with S_ in config into Session data
	public void setConfigToSessionData(CallStartAPI callStartAPI,String strIVRconfigName) {
		String strKey = Constants.EmptyString;
		String strValue = Constants.EmptyString;
		StringBuilder sb = new StringBuilder();
		
		try {
			
			ivrConfigProp=(Properties)callStartAPI.getApplicationAPI().getApplicationData(strIVRconfigName);
			propertyNames =ivrConfigProp.propertyNames();
			
			while (propertyNames.hasMoreElements()) 
			{
				strKey = ((String) propertyNames.nextElement()).trim();
				strValue = ivrConfigProp.getProperty(strKey).trim();
				
				if(strKey.startsWith(Constants.SessionVarNameStartwith))
				{
					callStartAPI.setSessionData(strKey,strValue);			
					sb.append("strkey --> ").append(strKey).append(" strValue --> ").append(strValue).append(" | ") ;
				}
			}
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Properties set into session at CallStart :: " + sb.toString());
		} catch (Exception e) {
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Exception Occured while setting the session variables at CallStart :: " + e.getMessage());
		}
	}
	
	public void setDefaultAudioPath(String LangCode,String MediaIP, CallStartAPI callStartAPI){

		StringBuffer sb = new StringBuffer();
		String appName = (String) callStartAPI.getApplicationAPI().getApplicationData(Constants.APP_LEVEL_APPLICATION_NAME);
		String strMediaConnectionMode=(String) callStartAPI.getApplicationAPI().getApplicationData(Constants.APP_LEVEL_MEDIA_CONNECTION_MODE);

		//Forming mediapath
		String strMediaAppPath = null;

		try{
			strMediaAppPath = strMediaConnectionMode + "://" + MediaIP + "/" + appName + "/" + LangCode + "/" + Constants.AppFolder + "/";
			callStartAPI.setSessionData(Constants.S_MediaAppPath,strMediaAppPath);	
			callStartAPI.setDefaultAudioPath(strMediaAppPath);
			sb.append("Default Audio Path ->").append(callStartAPI.getDefaultAudioPath());
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), sb.toString());
		}
		catch (Exception e) {
			callStartAPI.addToLog(callStartAPI.getCurrentElement()," : Unexpected error occurred : " + e.getMessage());
			e.printStackTrace();
		}
		finally{
		}
	}

}
