package com.farmers.bc;


import java.util.ArrayList;

import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

/**
 * This class is called by the decision element
 * for setting the Menu Options in case of the Menu
 */
public class DecisionBC extends DecisionElementBase
{

	CommonAPIAccess caa;
	@SuppressWarnings("unchecked")
	public String doDecision(String name, DecisionElementData data)	throws AudiumException
	{
		String strExitState = Constants.EmptyString;
		String strElementName = Constants.EmptyString;
		String strCallerOption = Constants.EmptyString;
		String strMenuName = Constants.EmptyString;
		ArrayList<String> al=null;
		String strMenuCode=null;
		caa=CommonAPIAccess.getInstance(data);

		strElementName = data.getCurrentElement();
		strExitState=data.getExitStateHistory().get(data.getExitStateHistory().size()-1);
		caa.printToLog(new StringBuffer("CurrentExistState= ").append(strExitState), CommonAPIAccess.INFO);
		al=(ArrayList<String>) caa.getFromSession(Constants.S_AL_MENU_TRAVERSED, null);
		if(al==null)
		{
			al=new ArrayList<String>();
			caa.printToLog(new StringBuffer("Menu traversal List is null. So creating one ").append(al), CommonAPIAccess.INFO);
		}
		else
		{
			caa.printToLog(new StringBuffer("Menu traversal List= ").append(al), CommonAPIAccess.INFO);
		}
		strMenuCode=(String) caa.getFromSession(Constants.S_MENU_CODE, Constants.NA);
		al.add(strMenuCode);
		caa.setToSession(Constants.S_AL_MENU_TRAVERSED, al);
		caa.printToLog(new StringBuffer("Menu traversal List after adding current menu= ").append(al), CommonAPIAccess.INFO);
		if(strExitState.equalsIgnoreCase(Constants.MAX_ERROR))
		{
			caa.printToLog(new StringBuffer("Exitstate : ").append(strExitState), CommonAPIAccess.INFO);
			strExitState = Constants.MAX_ERROR;
			
		}
		
		strMenuName= data.getElementHistory().get(data.getElementHistory().size()-2);
		data.addToLog(name, "strMenuName : "+strMenuName);
		if(strMenuName.contains(".")) {
			String[] strMenuNameArr = strMenuName.split("\\.");
			strMenuName = strMenuNameArr[strMenuNameArr.length - 1];
		}
		strCallerOption = data.getElementData(strMenuName,"value");		
		caa.printToLog(new StringBuffer("strMenuName2 ").append(strMenuName).append("strCallerOption : ").append(strCallerOption), CommonAPIAccess.INFO);
		//Performing an extra validation to retrieve the DTMF key press
	/*	if("".equals(strCallerOption) || strCallerOption==null ){
			strMenuName= data.getElementHistory().get(data.getElementHistory().size()-3);	
			strCallerOption = data.getElementData(strMenuName,"value");
		}
		caa.printToLog(new StringBuffer("CurrentMenuName= ").append(strMenuName).append(" |CallerOption= ").append(strCallerOption), CommonAPIAccess.INFO);
		*/
		if(Constants.DTMF_KEY_PRESS_0.equals(strCallerOption)){
			strExitState = Constants.DTMF_KEY_PRESS_0;			
		}
		else if (Constants.DTMF_KEY_PRESS_1.equals(strCallerOption)){
			strExitState = Constants.DTMF_KEY_PRESS_1;
		}
		else if (Constants.DTMF_KEY_PRESS_2.equals(strCallerOption)){	
			strExitState = Constants.DTMF_KEY_PRESS_2;
		}
		else if (Constants.DTMF_KEY_PRESS_3.equals(strCallerOption)){
			strExitState = Constants.DTMF_KEY_PRESS_3;
		}
		else if (Constants.DTMF_KEY_PRESS_4.equals(strCallerOption)){
			strExitState = Constants.DTMF_KEY_PRESS_4;
		}
		else if (Constants.DTMF_KEY_PRESS_5.equals(strCallerOption)){
			strExitState = Constants.DTMF_KEY_PRESS_5;
		}
		else if (Constants.DTMF_KEY_PRESS_6.equals(strCallerOption)){
			strExitState = Constants.DTMF_KEY_PRESS_6;
		}
		else if (Constants.DTMF_KEY_PRESS_7.equals(strCallerOption)){
			strExitState = Constants.DTMF_KEY_PRESS_7;
		}
		else if (Constants.DTMF_KEY_PRESS_8.equals(strCallerOption)){
			strExitState = Constants.DTMF_KEY_PRESS_8;
		}
		else if (Constants.DTMF_KEY_PRESS_9.equals(strCallerOption)){
			data.setSessionData(Constants.S_FINAL_LANG, Constants.Spanish);
			strExitState = Constants.DTMF_KEY_PRESS_9;
		}
		else if (Constants.DTMF_KEY_PRESS_STAR.equals(strCallerOption)){
			strExitState = Constants.EXIT_STATE_STAR;
		}else if (Constants.DTMF_KEY_PRESS_HASH.equals(strCallerOption)){
			strExitState=Constants.DTMF_KEY_PRESS_HASH;
		}

		/*
		String menuExsitState = strExitState;
		
		if(menuExsitState.equalsIgnoreCase(Constants.MAX_NOINPUT)) {
			menuExsitState = Constants.NOINPUT;
			strExitState = Constants.NOINPUT;
		}
		else if(menuExsitState.equalsIgnoreCase(Constants.MAX_NOMATCH) || menuExsitState.equalsIgnoreCase(Constants.MAXCOMBINEDTRIES) || menuExsitState.equalsIgnoreCase(Constants.MAX_ERROR)) {
			menuExsitState = Constants.NOMATCH;
			strExitState = Constants.NOMATCH;
		}
		if(null != (String)data.getSessionData(strMenuName+"_"+menuExsitState) && !((String)data.getSessionData(strMenuName+"_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData(strMenuName+"_"+menuExsitState);
		data.addToLog(name, "Final Value of Menu Exit State for "+strMenuName+" is :"+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":"+strMenuName+":"+menuExsitState);
		*/
		caa.printToLog(new StringBuffer("Exitstate : ").append(strExitState), CommonAPIAccess.INFO);
		return strExitState;
		
		
	}
	

}