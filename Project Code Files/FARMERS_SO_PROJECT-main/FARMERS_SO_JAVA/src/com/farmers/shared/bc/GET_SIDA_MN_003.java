package com.farmers.shared.bc;

import java.util.HashMap;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.google.gson.stream.JsonReader;

public class GET_SIDA_MN_003  extends DecisionElementBase {

	@SuppressWarnings("unchecked")
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
	
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String returnValue = (String) data.getElementData("SIDA_MN_003_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SIDA_MN_003"+" :: Menu Value : "+returnValue);
			data.setSessionData(Constants.SIDA_MN_003_VALUE, returnValue);
			
			if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) strExitState = Constants.NOINPUT;
			else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) strExitState = Constants.NOMATCH;
			else strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_003 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SIDA_MN_003 :: "+strExitState);
		
		try {
			HashMap<String, PolicyBean> policyDetailsMap = (HashMap<String, PolicyBean>) data.getSessionData(Constants.POLICY_DETAILS_FOR_MN3);
			int i = 1;
			String prompt="";
			String vxml1=" ",vxml2=" ",vxml3=" ",vxml4=" ",vxml5=" ";
			String lang = (String) data.getSessionData("S_PREF_LANG");
			
			for (String key1 : policyDetailsMap.keySet()) {
				data.addToLog("Policy number :", key1);
				
				//if case for spanish added- Kirthik
				if(lang.equalsIgnoreCase("SP")) {
					if(i==1) {
						prompt = "Puedes decir uno para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
					}else if(i==2) {
						prompt = prompt+" Di dos para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
					}else if(i==3) {
						prompt = prompt+" Di tres para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
					}else if(i==4) {
						prompt= prompt+" Di cuatro para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
					}else if(i==5) {
						prompt= prompt+" Di cinco para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
					}
					
				}else {
					if(i==1) {
						prompt = "You can say one for policy ending in "+spellChar(key1.substring(key1.length()-4));	
					}else if(i==2) {
						prompt = prompt+" say two for policy ending in "+spellChar(key1.substring(key1.length()-4));	
					}else if(i==3) {
						prompt = prompt+" say three for policy ending in "+spellChar(key1.substring(key1.length()-4));	
					}else if(i==4) {
						prompt= prompt+" say four for policy ending in "+spellChar(key1.substring(key1.length()-4));	
					}else if(i==5) {
						prompt= prompt+" say five for policy ending in "+spellChar(key1.substring(key1.length()-4));	
					}
					
				}	
				i++;
				
			}
			prompt = "\""+prompt+"\"";
			//prompt = prompt.replaceAll(" ", ".");

			
			data.setSessionData(Constants.VXMLParam1, prompt);
			data.setSessionData(Constants.VXMLParam2, "NA");
			data.setSessionData(Constants.VXMLParam3, "NA");
			data.setSessionData(Constants.VXMLParam4, "NA");
			data.setSessionData(Constants.VXMLParam5, "NA");
			
			data.addToLog(data.getCurrentElement(),"Policies Dynamic Product Type Prompts = "+prompt.toString());
			data.addToLog(data.getCurrentElement(),"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
			data.addToLog(data.getCurrentElement(),"VXMLPARAM2 = "+data.getSessionData(Constants.VXMLParam2));
			data.addToLog(data.getCurrentElement(),"VXMLPARAM3 = "+data.getSessionData(Constants.VXMLParam3));
			data.addToLog(data.getCurrentElement(),"VXMLPARAM4 = "+data.getSessionData(Constants.VXMLParam4));
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_003 :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	
	}
	
	public String spellChar(String number) { 
		String returnString =number;
		if(number!=null) {
		char[] spellChar = number.toCharArray();
		returnString = "";
		for (char c : spellChar) {
			returnString = returnString +" "+c;
		}
		
		}
		return returnString;
	}
	
}
