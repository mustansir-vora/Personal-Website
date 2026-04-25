package com.farmers.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

import com.audium.server.AudiumException;
import com.audium.server.logger.events.EventException;
import com.audium.server.proxy.StartCallInterface;
import com.audium.server.session.CallStartAPI;
import com.farmers.util.Constants;
import com.farmers.util.GlobalsCommon;
import com.farmers.util.SequenceNumber;

public class CallStart implements StartCallInterface {

	@Override
	public void onStartCall(CallStartAPI callStartAPI) throws AudiumException {
		String strVXMLIP =Constants.EmptyString;
		String strDNIS = Constants.NA;
		String strCLI = Constants.NA;
		String strCCallId = Constants.EmptyString;
		String strOANI = Constants.EmptyString;
		
		long lCallStart = System.currentTimeMillis();
		try {
			setDefaultAudioPath(callStartAPI);
			setToSessionData(callStartAPI);
			callStartAPI.setSessionData(Constants.S_CALL_ID, callStartAPI.getSessionData(Constants.ICM_CALLID));
			callStartAPI.setSessionData(Constants.S_ICMID, callStartAPI.getSessionData(Constants.ICM_CALLID));
			
			//Get VXML IP Address
			try {
				InetAddress thisIp =InetAddress.getLocalHost();
				strVXMLIP = thisIp.getHostAddress();
			}catch (UnknownHostException e) {
				callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Exception occurred while retrieving VXML IP Address. Exception: " + e.getMessage());
				strVXMLIP = Constants.NA;
			}
			
			try{
				strDNIS = callStartAPI.getSessionData(Constants.ICM_DNIS).toString();
				if(strDNIS==null)
				{
					strDNIS = Constants.NA;
				}
			}
			catch(Exception e){
				strDNIS = Constants.NA;	
			}
			
			try{
				strCLI = callStartAPI.getSessionData(Constants.ICM_ANI).toString();
			}
			catch(Exception e){
				strCLI = Constants.NA;	
			}
			if(strCLI.length()>10) {
				strCLI=strCLI.substring(strCLI.length()-10, strCLI.length());
			}
			
			try
			{
				strCCallId = (String)callStartAPI.getSessionData(Constants.ICM_CCALLID);
				String strCCallIdArr[] = strCCallId.split("-");
				String strRCKD = strCCallIdArr[0];
				String strRCK = strCCallIdArr[1];
				callStartAPI.setSessionData("S_RCKEY",strRCKD);
				callStartAPI.setSessionData("S_RCDAY",strRCK);

				callStartAPI.addToLog("","S_RCKEY="+strRCKD+ "  S_RCDAY= "+strRCK);
			}
			catch(Exception e){
				strCCallId = Constants.NA;
			}
			
			try
			{
				strOANI = (String)callStartAPI.getSessionData(Constants.ICM_OANI);
				if(strOANI==null || "".equalsIgnoreCase(strOANI)) {
					strOANI=strCLI;
				}

			}
			catch(Exception e){
			}
			
			SequenceNumber objSequenceNumber = new SequenceNumber();
			callStartAPI.setSessionData(Constants.S_ORIGINAL_ANI, strOANI);
			callStartAPI.setSessionData(Constants.S_SEQUENCE_COUNTER, objSequenceNumber);
			callStartAPI.setSessionData(Constants.S_ANI, strCLI);
			callStartAPI.setSessionData(Constants.S_FINAL_ANI, strCLI);
			callStartAPI.setSessionData(Constants.S_DNIS, strDNIS);
			callStartAPI.setSessionData(Constants.S_ORIGINAL_DNIS, strDNIS);
			callStartAPI.setSessionData(Constants.S_FINAL_DNIS, strDNIS);
			callStartAPI.setSessionData(Constants.S_VXML_SERVER_IP, strVXMLIP);
			callStartAPI.setSessionData(Constants.S_CALLSTART_TIME, "" + lCallStart);
		} catch (Exception e) {
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), GlobalsCommon.getExceptionTrace(e)); 
		}
	}
	
	private void setDefaultAudioPath(CallStartAPI callStartAPI) {
		try {
			Properties vrmMappingProp = (Properties) callStartAPI.getApplicationAPI().getApplicationData(Constants.A_IVR_PROP);
			if(callStartAPI.getApplicationAPI().getApplicationData(Constants.A_IVR_PROP) != null && vrmMappingProp.containsKey(Constants.S_AUDIO_PATH)) {
				String audioPath = vrmMappingProp.getProperty(Constants.S_AUDIO_PATH);
				callStartAPI.setDefaultAudioPath(audioPath.replace(Constants.REPLACE_IP, GlobalsCommon.getCurrentSysIP()));
			}
			
		} catch (Exception e) {
			callStartAPI.addToLog(callStartAPI.getCurrentElement(), GlobalsCommon.getExceptionTrace(e)); 
		}
	}
	
	/**
	 * Method to read from properties 
	 * Get the defaultValue in case of properties value is empty or null
	 * Set to Session Data
	 */
	public void setToSessionData(CallStartAPI callStartAPI) throws EventException 
	{
		Properties ivrProperties = (Properties) callStartAPI.getApplicationAPI().getApplicationData(Constants.A_IVR_PROP);
		Enumeration propertyNames = ivrProperties.propertyNames(); 
		String strKey ="";
		String strValue="";
		try{
			while (propertyNames.hasMoreElements()) {
				strKey = ((String) propertyNames.nextElement()).trim();
				strValue = ivrProperties.getProperty(strKey).trim();
				if(strKey.startsWith("S_"))
				{
					callStartAPI.setSessionData(strKey, strValue);
					callStartAPI.addToLog(callStartAPI.getCurrentElement(), "Setting session key : "+strKey+" value : "+strValue);
				}
			}
		}

		catch(Exception e){
			throw new EventException("Set Property file into ApplicationData. Ex:" + e); 
		}
	}

}
