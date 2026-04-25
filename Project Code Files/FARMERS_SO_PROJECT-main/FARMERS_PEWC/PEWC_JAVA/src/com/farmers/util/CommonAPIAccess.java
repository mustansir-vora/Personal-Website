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


	



}
