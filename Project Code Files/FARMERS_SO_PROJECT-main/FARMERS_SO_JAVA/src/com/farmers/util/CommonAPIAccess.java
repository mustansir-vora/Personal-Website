package com.farmers.util;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import com.audium.server.global.ApplicationAPIBase;
import com.audium.server.session.APIBase;
import com.audium.server.session.ActionElementData;
import com.audium.server.session.DecisionElementData;


public class CommonAPIAccess {
	public static final int DEBUG=0;
	public static final int INFO=1;
	public static final int WARN=2;
	public static final int ERROR=3;


	APIBase apiBase = null;
	ApplicationAPIBase apiBase1 = null;
	Properties propConfig=null;
	String strConfigPath = Constants.CONFIG_PATH;

	int intLogLevel = DEBUG; //by default, DEBUG

	private CommonAPIAccess(APIBase apiBase)
	{
		this.apiBase = apiBase;
		String strLogLevel = (String)getFromAppData(Constants.AppVarName_ActivityLogLevel, "DEBUG");
		//System.out.println("strLogLevel="+strLogLevel);

		if("DEBUG".equalsIgnoreCase(strLogLevel)){
			intLogLevel = DEBUG;
		}else if("INFO".equalsIgnoreCase(strLogLevel)){
			intLogLevel = INFO;
		}else if("WARN".equalsIgnoreCase(strLogLevel)){
			intLogLevel = WARN;
		}else if("ERROR".equalsIgnoreCase(strLogLevel)){
			intLogLevel = ERROR;
		}else{
			intLogLevel = DEBUG; //default log level
		}
	}

	private CommonAPIAccess(ApplicationAPIBase apiBase1)
	{
		this.apiBase1 = apiBase1;
		String strLogLevel = (String)apiBase1.getApplicationData(Constants.AppVarName_ActivityLogLevel);
		if("DEBUG".equalsIgnoreCase(strLogLevel)){
			intLogLevel = DEBUG;
		}else if("INFO".equalsIgnoreCase(strLogLevel)){
			intLogLevel = INFO;
		}else if("WARN".equalsIgnoreCase(strLogLevel)){
			intLogLevel = WARN;
		}else if("ERROR".equalsIgnoreCase(strLogLevel)){
			intLogLevel = ERROR;
		}else{
			intLogLevel = DEBUG; //default log level
		}
	}
	public static CommonAPIAccess getInstance(APIBase apiBase){

		return new CommonAPIAccess(apiBase);
	}
	public static CommonAPIAccess getInstance(ApplicationAPIBase apiBase1){

		return new CommonAPIAccess(apiBase1);
	}

	public void printToLog(String pStrMessage, int pIntLogLevel){

		if(pIntLogLevel>=this.intLogLevel){
			try{
				apiBase.addToLog(apiBase.getCurrentElement(), pStrMessage);
			}catch(Exception e){
				//System.out.println("PrintTolog Method Exception.");
				e.printStackTrace();
			}
		}
	}

	public void printToLog(StringBuffer pSBMessage, int pIntLogLevel){
		printToLog(pSBMessage.toString(),pIntLogLevel);
	}

	public void printToLog(String pStrMessage){
		printToLog(pStrMessage, DEBUG); //by default DEBUG level would set
	}
	public void printToLog(StringBuffer pSBMessage){
		printToLog(pSBMessage.toString(), DEBUG); //by default DEBUG level would set
	}
	public void setToSession(String strName,Object objValue){
		try{
			if("".equals(apiBase.getSessionId()) || apiBase.getSessionId()==null){
				apiBase.getApplicationAPI().setApplicationData(strName, objValue);
			}else{
				apiBase.setSessionData(strName,objValue);
			}
		}catch(Exception e){
			printToLog(new StringBuffer("Exception occurred while setting ").append(strName).append(" in session. Ex:").append(e.getMessage()).toString(),INFO);
			// need to implement logger
		}
	}
	public Object getFromSession(String pStrName, Object objDefaultValue){
		Object objOutput=new Object();
		StringBuffer sb = new StringBuffer();
		try{
			if("".equals(apiBase.getSessionId()) || apiBase.getSessionId()==null){
				objOutput=apiBase.getApplicationAPI().getApplicationData(pStrName);
				if(objOutput==null){
					//	printToLog(new StringBuffer("NULL is returned while reading ").append(pStrName).append(" from app data. Hence default value is set.").toString(),INFO);
					printToLog(sb.append("NULL is returned while reading ").append(pStrName).append(" from app data. Hence default value is set.").toString(), INFO);
					objOutput = objDefaultValue;
				}
			}else{
				objOutput=apiBase.getSessionData(pStrName);
				if(objOutput==null){
					//	printToLog(new StringBuffer("NULL is returned while reading ").append(pStrName).append(" from session. Hence default value is set.").toString(),INFO);
					printToLog(sb.append("NULL is returned while reading ").append(pStrName).append(" from session. Hence default value is set.").toString(), INFO);
					objOutput = objDefaultValue;
				}
			}
		}catch(Exception e){
			// printToLog(new StringBuffer("Exception occurred while reading ").append(pStrName).append(" from session. Ex:").append(e.getMessage()).toString(),INFO);
			printToLog(sb.append("Exception occurred while reading ").append(pStrName).append(" from session. Ex:").append(e.getMessage()).toString(), INFO);
			objOutput = objDefaultValue;
		}
		finally{
			sb = null;
		}

		return objOutput;
	}

	public Object getFromAppData(String pStrName, Object objDefaultValue){

		Object objOutput=new Object();
		StringBuffer sb = new StringBuffer();
		try{
			objOutput=apiBase.getApplicationAPI().getApplicationData(pStrName);
			if(objOutput==null){
				sb.append("NULL is returned while reading ").append(pStrName).append(" from app data. Hence default value is set.");
				//printToLog(new StringBuffer("NULL is returned while reading ").append(pStrName).append(" from app data. Hence default value is set.").toString(),INFO);
				printToLog(sb.toString(), INFO);
				objOutput = objDefaultValue;
			}
		}catch(Exception e){
			sb.append("Exception occurred while reading ").append(pStrName).append(" from app data. Ex:").append(e.getMessage());
			printToLog(sb.toString(), INFO);
			//printToLog(new StringBuffer("Exception occurred while reading ").append(pStrName).append(" from app data. Ex:").append(e.getMessage()).toString(),INFO);
			objOutput = objDefaultValue;
		}
		finally{
			sb = null;
		}

		return objOutput;
	}

	public APIBase getApiBase() {
		return apiBase;
	}

	public void printStackTrace(Exception e)
	{   
		try{
			apiBase.addToLog(apiBase.getCurrentElement(),"Caught exception: "+e.getMessage());
			StringWriter stw = new java.io.StringWriter();
			PrintWriter pw = new java.io.PrintWriter(stw);
			e.printStackTrace(pw);                            
			apiBase.addToLog(apiBase.getCurrentElement(),stw.toString());
			try{
				stw.close();
				pw.close();	
			}catch(Exception e1){
				apiBase.addToLog(apiBase.getCurrentElement(), "Exception while closing String Writer & PrintWriter"+e1.getMessage());
			}	
		}catch(Exception e2){
			apiBase.addToLog(apiBase.getCurrentElement(), "Exception while Printing stacktrace"+e2.getMessage());
		}

	}


	public String getCurrentTime()
	{

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
		String strCurrentTime=sdf.format(new Date());
		return strCurrentTime;
	}

	public String readPropertyFileValue(String pStrPropertyObjectName,String Key)
	{
		String configValue=null;
		propConfig=(Properties)apiBase.getApplicationAPI().getApplicationData(pStrPropertyObjectName);
		try{
			configValue=propConfig.getProperty(Key);
		}catch (Exception e) {
			printToLog(new StringBuffer("Searching key variable not able in repective property file :").append(Key).toString());
			configValue=Constants.NA;
		}
		return configValue;
	}

	public void setDefaultAudioPath(String pStrLang,String strMediaIP){

		StringBuffer sb = new StringBuffer();
		//String strMediaIP = (String)getFromAppData(Constants.AppVarName_MEDIA_SERVER_IP,Constants.EmptyString);
		String strMediaPort = (String)getFromAppData(Constants.AppVarName_MEDIA_SERVER_PORT,Constants.EmptyString);
		String strMediaPath = (String)getFromAppData(Constants.AppVarName_PROMPTS_PATH,Constants.EmptyString);
		String strMediaConnectionMode=(String)getFromAppData(Constants.AppVarName_MEDIA_CONNECTION_MODE,Constants.EmptyString);

		//Forming mediapath
		String strMediaAppPath = null;
		String strMediaSysPath = null;

		try{
			//strMediaAppPath = strMediaConnectionMode+"://"+strMediaIP+":"+strMediaPort+strMediaPath+pStrLang+"/"+Constants.AppFolder+"/";
			//strMediaSysPath = strMediaConnectionMode+"://"+strMediaIP+":"+strMediaPort+strMediaPath +pStrLang+"/"+Constants.SysFolder+"/";

			strMediaAppPath = strMediaConnectionMode+"://"+strMediaPath+pStrLang+"/"+Constants.AppFolder+"/";
			strMediaSysPath = strMediaConnectionMode+"://"+strMediaPath +pStrLang+"/"+Constants.SysFolder+"/";

			setToSession(Constants.S_MediaAppPath,strMediaAppPath);	
			setToSession(Constants.S_MediaSysPath,strMediaSysPath);	

			apiBase.setDefaultAudioPath(strMediaAppPath);
			sb.append("Default Audio Path ->").append(apiBase.getDefaultAudioPath()).append("std prompt path ").append(strMediaSysPath);
		}
		finally{
			printToLog(sb.toString(), CommonAPIAccess.DEBUG);
			sb = null;
		}
	}
	public void setDefaultAudioPath(String pStrLang){

		StringBuffer sb = new StringBuffer();
		String strMediaIP = (String)getFromSession(Constants.S_VXML_SERVER_IP,Constants.EmptyString);
		String strMediaPort = (String)getFromAppData(Constants.AppVarName_MEDIA_SERVER_PORT,Constants.EmptyString);
		String strMediaPath = (String)getFromAppData(Constants.AppVarName_PROMPTS_PATH,Constants.EmptyString);
		String strMediaConnectionMode=(String)getFromAppData(Constants.AppVarName_MEDIA_CONNECTION_MODE,Constants.EmptyString);

		//Forming mediapath
		String strMediaAppPath = null;
		String strMediaSysPath = null;

		try{
			strMediaAppPath = strMediaConnectionMode+"://"+strMediaIP+":"+strMediaPort+strMediaPath+"/"+pStrLang+"/"+Constants.AppFolder+"/";
			strMediaSysPath = strMediaConnectionMode+"://"+strMediaIP+":"+strMediaPort+strMediaPath +"/"+pStrLang+"/"+Constants.SysFolder+"/";

			setToSession(Constants.S_MediaAppPath,strMediaAppPath);	
			setToSession(Constants.S_MediaSysPath,strMediaSysPath);	

			apiBase.setDefaultAudioPath(strMediaAppPath);
			sb.append("Default Audio Path ->").append(apiBase.getDefaultAudioPath()).append("std prompt path ").append(strMediaSysPath);
		}
		finally{
			printToLog(sb.toString(), CommonAPIAccess.DEBUG);
			sb = null;
		}
	}

	public String readHostReqFileValue(String strObjectName)
	{
		String strReqValue=Constants.NA;

		try{
			strReqValue = (String)apiBase.getApplicationAPI().getApplicationData(strObjectName);
		}catch (Exception e) {
			printToLog(new StringBuffer("Searching "+strObjectName+" variable not able in App data :").toString());
			strReqValue=Constants.NA;
		}
		return strReqValue;
	}


	public static Date getCurrentDateInCST() {
		Instant currTimeUTC = Instant.now();
		ZonedDateTime centralTime = currTimeUTC.atZone(ZoneId.of("America/Chicago"));
		Date dateObj = Date.from(centralTime.toInstant());
		return dateObj;
	}

	public static Date getCurrentDateInEST() {
		Instant currTimeUTC = Instant.now();
		ZonedDateTime centralTime = currTimeUTC.atZone(ZoneId.of("America/New_York"));
		Date dateObj = Date.from(centralTime.toInstant());
		return dateObj;
	}

	public static Date getCurrentDateInMST() {
		Instant currTimeUTC = Instant.now();
		ZonedDateTime centralTime = currTimeUTC.atZone(ZoneId.of("America/Phoenix"));
		Date dateObj = Date.from(centralTime.toInstant());
		return dateObj;
	}

	public static Date getCurrentDateInHST() {
		Instant currTimeUTC = Instant.now();
		ZonedDateTime centralTime = currTimeUTC.atZone(ZoneId.of("US/Hawaii"));
		Date dateObj = Date.from(centralTime.toInstant());
		return dateObj;
	}


	public void createMSPKey(CommonAPIAccess caa, DecisionElementData data, String menuID, String menuValue) {

		try {

			String strBU = (String)data.getSessionData(Constants.S_BU);
			String strCategory = (String)data.getSessionData(Constants.S_CATEGORY);
			String strCategoryFlag = "N";
			ArrayList<String>  strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");
			ArrayList<String>  strEmpSrv = (ArrayList<String>) data.getSessionData("EMP_SERV_LIST");
			ArrayList<String>  strFMSTAARP = (ArrayList<String>) data.getSessionData("FMST_AARP_LOB_LIST");
			ArrayList<String>  strFMSTAGENT = (ArrayList<String>) data.getSessionData("FMST_AGENT_LOB_LIST");
			ArrayList<String>  strFMSTCUST = (ArrayList<String>) data.getSessionData("FMST_CUST_LOB_LIST");
			ArrayList<String>  strFMSTUSAA = (ArrayList<String>) data.getSessionData("FMST_USAA_LOB_LIST");
			ArrayList<String>  str21stHawaii = (ArrayList<String>) data.getSessionData("21st_HAWAII_LOB_LIST");

			data.addToLog(data.getCurrentElement(), " Current BU : strBU :: "+strBU);
			data.addToLog(data.getCurrentElement(), " Current Category : strCategory :: "+strCategory);
			data.addToLog(data.getCurrentElement(), " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(data.getCurrentElement(), " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(data.getCurrentElement(), " A_FOREMOST_LOB : "+strForemostCode);
			data.addToLog(data.getCurrentElement(), " A_FWS_LOB : "+strFWSCode);
			data.addToLog(data.getCurrentElement(), " A_21ST_LOB : "+str21stCode);
			data.addToLog(data.getCurrentElement(), " EMP_SERV_LIST : "+strEmpSrv);
			data.addToLog(data.getCurrentElement(), " FMST_AARP_LOB_LIST : "+strFMSTAARP);
			data.addToLog(data.getCurrentElement(), " FMST_AGENT_LOB_LIST : "+strFMSTAGENT);
			data.addToLog(data.getCurrentElement(), " FMST_CUST_LOB_LIST : "+strFMSTCUST);
			data.addToLog(data.getCurrentElement(), " FMST_USAA_LOB_LIST : "+strFMSTUSAA);
			data.addToLog(data.getCurrentElement(), " 21st_HAWAII_LOB_LIST : "+str21stHawaii);

			String state = (String) data.getSessionData("S_STATENAME");
			data.addToLog(data.getCurrentElement(), "Value of S_STATENAME : "+state);
			String tmpStrBU = Constants.EmptyString;
			
			if(((strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) || (str21stCode!=null && strBU!=null && str21stCode.contains(strBU))) && "Hawaii".equalsIgnoreCase(state)) {
				tmpStrBU = Constants.BU_BW_HI;
			} else if(strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
				strBU = Constants.BU_BW;
			} else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				strBU = Constants.BU_FARMERS;
			} else if(strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				strBU = Constants.BU_FOREMOST;
			} else if(strFWSCode!=null && strBU!=null && (strFWSCode.contains(strBU) || strBU.contains(Constants.FWS))) {
				strBU = Constants.BU_FWS;
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				strBU = Constants.BU_21ST;
			}else if(strEmpSrv!=null && strBU!=null && strEmpSrv.contains(strBU)) {
				strBU = Constants.BU_EMP_SRV;
			} else {
				//strBU = "BWHI";
			}

			String mspKey = Constants.EmptyString;
			if(!Constants.EmptyString.equals(tmpStrBU)) {
				mspKey = tmpStrBU+":"+menuID+":"+menuValue;
			} else {
				mspKey = strBU+":"+menuID+":"+menuValue;
			}
			
			data.setSessionData("S_UNIQUE_BU", strBU);
			data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
			data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);

			data.addToLog(data.getCurrentElement(), "Value of S_LAST_5_MSP_VALUES :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));

			Queue<String> lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue :: " + lastFiveMSPValues);
			if (lastFiveMSPValues == null) {
				lastFiveMSPValues = new LinkedList<>();
			}

			lastFiveMSPValues.offer(mspKey);

			while (lastFiveMSPValues.size() > 5) {
				lastFiveMSPValues.poll();
			}

			data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);

			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue After Operation :: " + lastFiveMSPValues);

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in SMM_MN_003 :: "+e);
			caa.printStackTrace(e);
		}
	}
	
	public void createMSPKey_FNWL(CommonAPIAccess caa, DecisionElementData data, String menuID, String menuValue) {
		
		try {
			String strBU = Constants.FNWL;
			String mspKey = Constants.EmptyString;
			
			mspKey = strBU+":"+menuID+":"+menuValue;
			data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
			data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);
			
			data.addToLog(data.getCurrentElement(), "Value of S_LAST_5_MSP_VALUES :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));
			
			Queue<String> lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue :: " + lastFiveMSPValues);
			if (lastFiveMSPValues == null) {
				lastFiveMSPValues = new LinkedList<>();
			}

			lastFiveMSPValues.offer(mspKey);

			while (lastFiveMSPValues.size() > 5) {
				lastFiveMSPValues.poll();
			}

			data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);

			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue After Operation :: " + lastFiveMSPValues);
			
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in createMSPKey_FNWL method :: "+e);
			caa.printStackTrace(e);
		}
		
	}

	public void createMSPKeyTransferBack(CommonAPIAccess caa, DecisionElementData data, String menuID, String menuValue) {

		try {

			String strBU = (String)data.getSessionData(Constants.S_BU);
			String strCategory = (String)data.getSessionData(Constants.S_CATEGORY);
			String strCategoryBU = "";
			ArrayList<String>  strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strEmpSrv = (ArrayList<String>) data.getSessionData("EMP_SERV_LIST");
			ArrayList<String>  strFMSTAARP = (ArrayList<String>) data.getSessionData("FMST_AARP_LOB_LIST");
			ArrayList<String>  strFMSTAGENT = (ArrayList<String>) data.getSessionData("FMST_AGENT_LOB_LIST");
			ArrayList<String>  strFMSTCUST = (ArrayList<String>) data.getSessionData("FMST_CUST_LOB_LIST");
			ArrayList<String>  strFMSTUSAA = (ArrayList<String>) data.getSessionData("FMST_USAA_LOB_LIST");
			ArrayList<String>  str21stHawaii = (ArrayList<String>) data.getSessionData("21st_HAWAII_LOB_LIST");
			ArrayList<String>  strFISC = (ArrayList<String>) data.getSessionData("FISC_LIST");
			ArrayList<String>  strSpecialty = (ArrayList<String>) data.getSessionData("SPECIALTY_LIST");

			data.addToLog(data.getCurrentElement(), " Current BU : strBU :: "+strBU);
			data.addToLog(data.getCurrentElement(), " Current Category : strCategory :: "+strCategory);
			data.addToLog(data.getCurrentElement(), " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(data.getCurrentElement(), " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(data.getCurrentElement(), " A_FOREMOST_LOB : "+strForemostCode);
			data.addToLog(data.getCurrentElement(), " EMP_SERV_LIST : "+strEmpSrv);
			data.addToLog(data.getCurrentElement(), " FMST_AARP_LOB_LIST : "+strFMSTAARP);
			data.addToLog(data.getCurrentElement(), " FMST_AGENT_LOB_LIST : "+strFMSTAGENT);
			data.addToLog(data.getCurrentElement(), " FMST_CUST_LOB_LIST : "+strFMSTCUST);
			data.addToLog(data.getCurrentElement(), " FMST_USAA_LOB_LIST : "+strFMSTUSAA);
			data.addToLog(data.getCurrentElement(), " 21st_HAWAII_LOB_LIST : "+str21stHawaii);
			data.addToLog(data.getCurrentElement(), " FISC_LIST : "+strFISC);
			data.addToLog(data.getCurrentElement(), " SPECIALTY_LIST : "+strSpecialty);


			if(strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
				strBU = Constants.BU_BW;
			} else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				strBU = Constants.BU_FARMERS;
			} else if(strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				strBU = Constants.BU_FOREMOST;
			}else if(strEmpSrv!=null && strBU!=null && strEmpSrv.contains(strBU)) {
				strBU = Constants.BU_EMP_SRV;
			} else {
				//strBU = "BWHI";
			}

			if(strFMSTAARP!=null && strCategory!=null && strFMSTAARP.contains(strCategory)) {
				strCategoryBU = "AARP";
			}else if(strSpecialty!=null && strCategory!=null && strSpecialty.contains(strCategory)) {
				strCategoryBU = "SPECIALTY";
			}else if(strFMSTUSAA!=null && strCategory!=null && strFMSTUSAA.contains(strCategory)) {
				strCategoryBU = "USAA";
			}else if(strFISC!=null && strCategory!=null && strFISC.contains(strCategory)) {
				strCategoryBU = "FISC";
			}else if(strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
				strCategoryBU = "BW";
			}

			String mspKey = strBU+":"+menuID+":"+strCategoryBU+"_"+menuValue;
			data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
			data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in createMSPKeyTransferBack :: "+e);
			caa.printStackTrace(e);
		}
	}

	public void createMSPKeyFWSTransferBackFromPaymentUS(CommonAPIAccess caa, DecisionElementData data, String menuID, String menuValue) {

		try {
			String mspKey = "FWS"+":"+menuID+":"+menuValue;
			data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
			data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);

			data.addToLog(data.getCurrentElement(), "Value of S_LAST_5_MSP_VALUES :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));

			Queue<String> lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue :: " + lastFiveMSPValues);
			if (lastFiveMSPValues == null) {
				lastFiveMSPValues = new LinkedList<>();
			}

			lastFiveMSPValues.offer(mspKey);

			while (lastFiveMSPValues.size() > 5) {
				lastFiveMSPValues.poll();
			}

			data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);

			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue After Operation :: " + lastFiveMSPValues);

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in SMM_MN_003 :: "+e);
			caa.printStackTrace(e);
		}
	}

	public void createMSPKey_NLU(CommonAPIAccess caa, ActionElementData data, String menuID, String menuValue) {

		try {

			String strBU = (String)data.getSessionData(Constants.S_BU);
			String strCategory = (String)data.getSessionData(Constants.S_CATEGORY);
			String strCategoryFlag = "N";
			ArrayList<String>  strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");
			ArrayList<String>  strEmpSrv = (ArrayList<String>) data.getSessionData("EMP_SERV_LIST");
			ArrayList<String>  strFMSTAARP = (ArrayList<String>) data.getSessionData("FMST_AARP_LOB_LIST");
			ArrayList<String>  strFMSTAGENT = (ArrayList<String>) data.getSessionData("FMST_AGENT_LOB_LIST");
			ArrayList<String>  strFMSTCUST = (ArrayList<String>) data.getSessionData("FMST_CUST_LOB_LIST");
			ArrayList<String>  strFMSTUSAA = (ArrayList<String>) data.getSessionData("FMST_USAA_LOB_LIST");
			ArrayList<String>  str21stHawaii = (ArrayList<String>) data.getSessionData("21st_HAWAII_LOB_LIST");

			data.addToLog(data.getCurrentElement(), " Current BU : strBU :: "+strBU);
			data.addToLog(data.getCurrentElement(), " Current Category : strCategory :: "+strCategory);
			data.addToLog(data.getCurrentElement(), " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(data.getCurrentElement(), " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(data.getCurrentElement(), " A_FOREMOST_LOB : "+strForemostCode);
			data.addToLog(data.getCurrentElement(), " A_FWS_LOB : "+strFWSCode);
			data.addToLog(data.getCurrentElement(), " A_21ST_LOB : "+str21stCode);
			data.addToLog(data.getCurrentElement(), " EMP_SERV_LIST : "+strEmpSrv);
			data.addToLog(data.getCurrentElement(), " FMST_AARP_LOB_LIST : "+strFMSTAARP);
			data.addToLog(data.getCurrentElement(), " FMST_AGENT_LOB_LIST : "+strFMSTAGENT);
			data.addToLog(data.getCurrentElement(), " FMST_CUST_LOB_LIST : "+strFMSTCUST);
			data.addToLog(data.getCurrentElement(), " FMST_USAA_LOB_LIST : "+strFMSTUSAA);
			data.addToLog(data.getCurrentElement(), " 21st_HAWAII_LOB_LIST : "+str21stHawaii);


			if(strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
				strBU = Constants.BU_BW;
			} else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				strBU = Constants.BU_FARMERS;
			} else if(strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				strBU = Constants.BU_FOREMOST;
			} else if(strFWSCode!=null && strBU!=null && (strFWSCode.contains(strBU) || strBU.contains(Constants.FWS))) {
				strBU = Constants.BU_FWS;
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				strBU = Constants.BU_21ST;
			}else if(strEmpSrv!=null && strBU!=null && strEmpSrv.contains(strBU)) {
				strBU = Constants.BU_EMP_SRV;
			} else {
				//strBU = "BWHI";
			}

			String mspKey = strBU+":"+menuID+":"+menuValue;
			data.setSessionData("S_UNIQUE_BU", strBU);
			data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
			data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);

			data.addToLog(data.getCurrentElement(), "Value of S_LAST_5_MSP_VALUES :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));

			Queue<String> lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue :: " + lastFiveMSPValues);
			if (lastFiveMSPValues == null) {
				lastFiveMSPValues = new LinkedList<>();
			}

			lastFiveMSPValues.offer(mspKey);

			while (lastFiveMSPValues.size() > 5) {
				lastFiveMSPValues.poll();
			}

			data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);

			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue After Operation :: " + lastFiveMSPValues);

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in SMM_MN_003 :: "+e);
			caa.printStackTrace(e);
		}
	}

	public void createMSPKeyEPCTransferBackFromPaymentUS(CommonAPIAccess caa, DecisionElementData data, String menuID, String menuValue) {

		try {
			String strBU = (String)data.getSessionData(Constants.S_BU);
			String strCategory = (String) data.getSessionData(Constants.S_CATEGORY);
			String strCategoryFlag = "N";
			String EPCBrand = (String) data.getSessionData("S_EPC_BRAND_LABEL") != null ? (String) data.getSessionData("S_EPC_BRAND_LABEL") : Constants.EmptyString;
			String mspKey="";
			data.addToLog(data.getCurrentElement(), "Value of S_BU : " + strBU + "Value of S_CATEGORY : " + strCategory);
			
			if (null != data.getSessionData("S_FLAG_FOREMOST_BU") && data.getSessionData("S_FLAG_FOREMOST_BU").toString().equalsIgnoreCase(Constants.STRING_YES) && EPCBrand.toUpperCase().contains("BW")) {
				EPCBrand = "Foremost";
			}
			else if (null != data.getSessionData("S_FLAG_BW_BU") && data.getSessionData("S_FLAG_BW_BU").toString().equalsIgnoreCase(Constants.STRING_YES) && EPCBrand.toUpperCase().contains("FOREMOST")) {
				EPCBrand = "BW";
			}
			else if ((null != data.getSessionData("S_FLAG_FDS_BU") && data.getSessionData("S_FLAG_FDS_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) || (null != data.getSessionData("S_FLAG_FWS_BU") && data.getSessionData("S_FLAG_FWS_BU").toString().equalsIgnoreCase(Constants.STRING_YES))) {
				EPCBrand = "";
			}

			if(("Foremost_FISC".equalsIgnoreCase(EPCBrand) || "SPECIALITY FISC".equalsIgnoreCase(strBU) || strBU.contains("FISC") || strBU.contains("fisc") || "FM-NM-FISC".equalsIgnoreCase(strCategory) || "FM-FISC".equalsIgnoreCase(strCategory) || "FM-FISC-XFR-SVC".equalsIgnoreCase(strCategory) || "FM-FISC-XFR-SLS".equalsIgnoreCase(strCategory) || "FM-FISC-BP-VF".equalsIgnoreCase(strCategory) || "FM-FISC-MAP".equalsIgnoreCase(strCategory) || "Foremost_FISC".equalsIgnoreCase(EPCBrand))) {
				strBU="SPECIALITY FISC";
				strCategoryFlag = "Y";
			}else if("Foremost_Specialty_Service".equalsIgnoreCase(EPCBrand) || "SPECIALITY SERVICE".equalsIgnoreCase(strBU) || strBU.contains("SERVICE") || strBU.contains("service") || "FM-SRV-ONLY".equalsIgnoreCase(strCategory)) {
				strBU="SPECIALITY SERVICE";
				strCategoryFlag = "Y";
			}else if(EPCBrand.equalsIgnoreCase("BW_FSA") || "BW FSA".equalsIgnoreCase(strBU)) {
				strBU="BWFSA";
				strCategoryFlag = "Y";
			}else if(EPCBrand.equalsIgnoreCase("BW_COMMERCIAL")) {
				strBU="BWCOMMERCIAL";
				strCategoryFlag = "Y";
			}else if(EPCBrand.equalsIgnoreCase("BW_USAA") || "BW USAA".equalsIgnoreCase(strBU) || "BW USAA".equalsIgnoreCase(strCategory)) {
				strBU="BWUSAA";
				strCategoryFlag = "Y";
			}else if(EPCBrand.equalsIgnoreCase("BW") || "Bristol West".equalsIgnoreCase(strBU) || "Foremost Auto".equalsIgnoreCase(strBU) || "FM_AUTO".equalsIgnoreCase(strBU) || "BW_PROD".equalsIgnoreCase(strBU)
					|| "Bristol West".equalsIgnoreCase(strCategory) || "Foremost Auto".equalsIgnoreCase(strCategory) || "FM_AUTO".equalsIgnoreCase(strCategory) || "BW_PROD".equalsIgnoreCase(strCategory)) {
				strBU="BW";
				strCategoryFlag = "Y";
			}else if("FDS".equalsIgnoreCase(strBU) || "FDS".equalsIgnoreCase(strCategory)) {
				strBU="FDS";
				strCategoryFlag = "Y";
			}else if("FDS SPN".equalsIgnoreCase(strBU) || "FDS SPN".equalsIgnoreCase(strCategory)) {
				strBU="FDSSPN";
				strCategoryFlag = "Y";
			}else if("Foremost_AARP".equalsIgnoreCase(EPCBrand) || "FM-AARP".equalsIgnoreCase(strCategory) || "FM-CSSP-AARP".equalsIgnoreCase(strCategory) || "FM-AARP-SLS".equalsIgnoreCase(strCategory) || "FM-AARP-XFR-SLS".equalsIgnoreCase(strCategory) ||
					"FM-AARP-XFR-SVC".equalsIgnoreCase(strCategory) || "FM-AARP-BP-VF".equalsIgnoreCase(strCategory) || "FM-AARP-MAP".equalsIgnoreCase(strCategory)) {
				strBU="FOREMOSTAARP";
				strCategoryFlag = "Y";
			}else if("Foremost_USAA".equalsIgnoreCase(EPCBrand) || "FM-USAA".equalsIgnoreCase(strCategory) || "FM-USAA-LH".equalsIgnoreCase(strCategory) || "FM-CSSP-USAA".equalsIgnoreCase(strCategory) || "FM-USAA-XFR-SVC".equalsIgnoreCase(strCategory) ||
					"FM-USAA-XFR-SLS".equalsIgnoreCase(strCategory) || "FM-USAA-BP-VF".equalsIgnoreCase(strCategory) || "FM-USAA-MAP".equalsIgnoreCase(strCategory) || "FM-USAA-SLS".equalsIgnoreCase(strCategory)) {
				strBU="FOREMOSTUSAA";
				strCategoryFlag = "Y";
			}else {
					caa.createMSPKey(caa, data, menuID, menuValue);
			}

			if(null != strCategoryFlag && !strCategoryFlag.isEmpty() && "Y".equalsIgnoreCase(strCategoryFlag)) {
				mspKey = strBU+":"+menuID+":"+menuValue;
				data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
				data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);
			}

			data.addToLog(data.getCurrentElement(), "Value of S_LAST_5_MSP_VALUES :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));

			Queue<String> lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue :: " + lastFiveMSPValues);
			if (lastFiveMSPValues == null) {
				lastFiveMSPValues = new LinkedList<>();
			}

			lastFiveMSPValues.offer(mspKey);

			while (lastFiveMSPValues.size() > 5) {
				lastFiveMSPValues.poll();
			}

			data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);

			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue After Operation :: " + lastFiveMSPValues);

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in SMM_MN_003 :: "+e);
			caa.printStackTrace(e);
		}
	}

}
