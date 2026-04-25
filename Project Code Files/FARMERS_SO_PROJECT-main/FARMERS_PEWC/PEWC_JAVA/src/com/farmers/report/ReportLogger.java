package com.farmers.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import org.json.XML;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.global.LoggerApplicationAPI;
import com.audium.server.logger.ApplicationLoggerBase;
import com.audium.server.logger.LoggerPlugin;
import com.audium.server.logger.events.ApplicationEvent;
import com.audium.server.logger.events.ElementEnterEvent;
import com.audium.server.logger.events.ElementExitEvent;
import com.audium.server.logger.events.EndEvent;
import com.audium.server.logger.events.EventException;
import com.audium.server.logger.events.HoteventEvent;
import com.audium.server.session.LoggerAPI;
import com.farmers.AdminAPI.InsertCallDataToQueue;
import com.farmers.util.Constants;
import com.farmers.util.GlobalsCommon;
import com.farmers.util.SequenceNumber;



public class ReportLogger extends ApplicationLoggerBase implements LoggerPlugin {


	private static final LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	private static Logger logger = LogManager.getLogger(ReportLogger.class.getName());
	
	FileInputStream fin;


	private static final String SPEECH_MENU = "SPEECH_MENU";
	private static final String MENU = "MENU";
	private static final String AUDIO = "AUDIO";
	private static final String MENU_DTMF_PATH = "MENU_DTMF_PATH";
	private static final String EMPTY = "EMPTY";
	private static final String OTHERS = "OTHERS";

	private static final String HOT_EVENT = "HE"; //stores a boolean value in scratch data


	private static final String CALL_END_STARTED = "CALL_END_STARTED";
	private static final String CURRENT_ELEMENT = "CE";
	private static final String LAST_ACCESS_MENU = "LM";
	private static final String PARENT_MENU_ID ="PAR_ID";
	private static final String HE_ELEMENTS = "HEE";
	private static final String LAST_ACCESS_MENU_OPTION = "LMO";

	private static final String DONE = "done";
	private static final String MAX_NOMATCH = "max_nomatch";
	private static final String MAX_NOINPUT = "max_noinput";
	private static final String MAX_ERROR = "max_error";

	private static final String CALL_DATA = "CALL_DATA";
	private static final String CUR_MENU = "CUR_MENU";

	private static final String ELEMENT_ENTRY_TIMESTAMP = "ELEMENT_ENTRY_TIMESTAMP";
	private static final String ELEMENT_EXIT_TIMESTAMP = "ELEMENT_EXIT_TIMESTAMP";
	
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;

	private String strCVPVXMLServerIP = "";
	private String strXMLFilePath = "C:\\Servion\\PEWC_CVP\\ReportData\\";
	private String strAppName = "";

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT);

	String hostUrl=null;
	URL url =null;
	JSONObject jsonRequest=null;
	String strRequest =null;
	JSONParser jParser = null;
	JSONObject jObjectResponse =null;


	@SuppressWarnings("unchecked")
	public void initialize(File configFile, LoggerApplicationAPI api) throws EventException {
		eventsToListenFor.add(END_EVENT_ID);
		eventsToListenFor.add(ELEMENT_ENTER_EVENT_ID);
		eventsToListenFor.add(ELEMENT_EXIT_EVENT_ID);
		eventsToListenFor.add(HOTEVENT_EVENT_ID);
		doAppStart();

	}

	public void destroy(LoggerApplicationAPI api) {
		logger.info("ReportLogger->destroy() method is called");
	}

	public void log(ApplicationEvent event) throws EventException {

		String eventID = event.getID();

		if (eventID.equals(ELEMENT_ENTER_EVENT_ID)) {
			ElementEnterEvent theEvent = (ElementEnterEvent) event;
			doElementEnter(theEvent);
		}else if (eventID.equals(ELEMENT_EXIT_EVENT_ID)) {
			ElementExitEvent theEvent = (ElementExitEvent) event;
			doElementExit(theEvent);
		} else if (eventID.equals(HOTEVENT_EVENT_ID)){
			HoteventEvent theEvent = (HoteventEvent) event;
			doHotEvent(theEvent);
		} else if (eventID.equals(END_EVENT_ID)) {
			EndEvent theEvent = (EndEvent) event;
			try{
				doEnd(theEvent);
			}catch(Exception ex){
				logger.error("Exception : "+ex.getMessage()+" :: "+ex);
			}
		}
	}

	private void doAppStart() throws EventException{

		strAppName = getApplicationName();
		logger = LogManager.getLogger(ReportLogger.class);

		try 
		{
			File file = new File(new StringBuffer(Constants.LOG4J_CONFIG_PATH).toString());
            context.setConfigLocation(file.toURI());
            logger.info(new StringBuffer(" ReportLogger - logger4J Config file loaded successfully from ").append(Constants.LOG4J_CONFIG_PATH).toString());
		} 
		catch (Exception fie) 
		{
			System.out.println(new StringBuffer("ReportLogger-logger4J Config file load error. Ex:").append(fie.getMessage()).toString());
		} 
		finally 
		{
			if(fin!=null)
			{
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fin = null;
			}
		}
		logger.debug("********************* FARMERS_SO_CVP ReportLogger logger ****************************");
		
		
		try
		{
			InetAddress serveraddr = InetAddress.getLocalHost();
			strCVPVXMLServerIP  = serveraddr.getHostAddress();
			logger.info("VXML Server IP:" + strCVPVXMLServerIP);
		}catch ( UnknownHostException e ){
			logger.error("VXML Server IP could not be captured");
		}

	}

	private void doElementEnter(ElementEnterEvent pLocalEvent){
		LoggerAPI localLoggerAPI = pLocalEvent.getLoggerAPI();

		String strSessionID = localLoggerAPI.getSessionId();
		String strCurElementName = pLocalEvent.getElementName();

		try{
			localLoggerAPI.setLoggerScratchData(CURRENT_ELEMENT,strCurElementName);
		}catch(Exception ex){
			logger.error("Error while setting CURRENT_ELEMENT. Ex:" + ex);
		}

		try{
			localLoggerAPI.setLoggerScratchData(ELEMENT_ENTRY_TIMESTAMP,pLocalEvent.getTimestamp());
		}catch(Exception ex){
			logger.error("Error while setting ELEMENT_ENTRY_TIMESTAMP. Ex:" + ex);
		}
		if(getElementType(strCurElementName).equals(AUDIO)){
			logger.error("Prompt type identified" );
			doElementEnterForAudio(pLocalEvent,strSessionID,strCurElementName);
		}
		else if(getElementType(strCurElementName).equals(MENU)){
			doElementEnterForMenu(pLocalEvent,strSessionID,strCurElementName);
		}else if(getElementType(strCurElementName).equals(SPEECH_MENU)) {
			doElementEnterForSpeechMenu(pLocalEvent,strSessionID,strCurElementName);
		}
		
		//Setting IVR path details into session only for Menu, prompt, host, admin API
		if((strCurElementName.contains("_MN_") && !strCurElementName.contains("_VALUE")) || strCurElementName.contains("_PA_") || strCurElementName.contains("_HOST_") || strCurElementName.contains("_ADM_")) {
			localLoggerAPI.setLoggerScratchData(Constants.S_IVRPATH,strCurElementName);
		}
	}
	
	private void doElementEnterForAudio(ElementEnterEvent pEvent, String pStrSessionID, String pStrCurElementName){
		LoggerAPI localLoggerAPI = pEvent.getLoggerAPI();
		Announcement tmpAnnouncement = new Announcement(); 
		CallData tmpCD = null;

		boolean isHotEventFired=false;
		String strServParentName = (String)localLoggerAPI.getLoggerScratchData(PARENT_MENU_ID);

		if(strServParentName==null || strServParentName.equals("")){
			strServParentName = "";
		}
		
		if(pStrCurElementName.contains(".")) {
			String[] pStrCurElementNameArr = (Pattern.compile("\\.").split(pStrCurElementName));
			pStrCurElementName = pStrCurElementNameArr[pStrCurElementNameArr.length-1];
		}
		logger.info("Entered into Audio Element:" + pStrCurElementName);


		try{
			isHotEventFired = getBooleanFromScratchData(localLoggerAPI,HOT_EVENT);
			logger.info("isHotEventFired:" + isHotEventFired);
		}catch(Exception ex){
			isHotEventFired=false;
			logger.error("Exception occurred while retrieve HOT_EVENT value");
		}
		//Setting the HotEvent flag as false in scratch data 
		//to allow other services to add to the Service path
		localLoggerAPI.removeLoggerScratchData(HOT_EVENT);

		//set Announcement details
		SequenceNumber sequenceNumber=(SequenceNumber)localLoggerAPI.getSessionData(Constants.S_SEQUENCE_COUNTER);
		tmpAnnouncement.setSequenceNum(sequenceNumber.get());
		localLoggerAPI.setLoggerScratchData(Constants.S_SEQUENCE_COUNTER,sequenceNumber);
		tmpAnnouncement.setStrAnnouncementId(pStrCurElementName);
		tmpAnnouncement.setCalAnnouncementDateTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
		tmpCD = getCallDataObject(localLoggerAPI);
		tmpCD.addAnnouncement(tmpAnnouncement);
		localLoggerAPI.setLoggerScratchData(CALL_DATA,tmpCD);
		logger.info("Call Data object is added to scratch data for Announcement");
	}

	private void doElementEnterForMenu(ElementEnterEvent pEvent, String pStrSessionID, String pStrCurElementName){
		LoggerAPI localLoggerAPI = pEvent.getLoggerAPI();
		Menu tmpMenu = new Menu();

		if(pStrCurElementName.contains(".")) {
			String[] pStrCurElementNameArr = (Pattern.compile("\\.").split(pStrCurElementName));
			pStrCurElementName = pStrCurElementNameArr[pStrCurElementNameArr.length-1];
		}

		logger.info("Entered into Menu Element:" + pStrCurElementName);
		/** Last Access Menu**/
		localLoggerAPI.setLoggerScratchData(LAST_ACCESS_MENU,pStrCurElementName);
		
		/** Menu ID & Menu Accessed Time **/
		SequenceNumber sequenceNumber=(SequenceNumber)localLoggerAPI.getSessionData(Constants.S_SEQUENCE_COUNTER);
		tmpMenu.setSequenceNum(sequenceNumber.get());
		localLoggerAPI.setLoggerScratchData(Constants.S_SEQUENCE_COUNTER,sequenceNumber);
		tmpMenu.setStrMenuId(pStrCurElementName);
		tmpMenu.setCalMenuStartTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
		String strMenuCode=(String)localLoggerAPI.getSessionData("S_MENU_CODE");
		tmpMenu.setStrMenuCode(strMenuCode);
		localLoggerAPI.setLoggerScratchData(CUR_MENU,tmpMenu);
	}
	
	private void doElementEnterForSpeechMenu(ElementEnterEvent pEvent, String pStrSessionID, String pStrCurElementName){
		LoggerAPI localLoggerAPI = pEvent.getLoggerAPI();
		Menu tmpMenu = new Menu();

		if(pStrCurElementName.contains(".")) {
			String[] pStrCurElementNameArr = (Pattern.compile("\\.").split(pStrCurElementName));
			pStrCurElementName = pStrCurElementNameArr[pStrCurElementNameArr.length-1];
		}

		logger.info("Entered into Menu Element:" + pStrCurElementName);
		/** Last Access Menu**/
		localLoggerAPI.setLoggerScratchData(LAST_ACCESS_MENU,pStrCurElementName);
		
		/** Menu ID & Menu Accessed Time **/
		SequenceNumber sequenceNumber=(SequenceNumber)localLoggerAPI.getSessionData(Constants.S_SEQUENCE_COUNTER);
		tmpMenu.setSequenceNum(sequenceNumber.get());
		localLoggerAPI.setLoggerScratchData(Constants.S_SEQUENCE_COUNTER,sequenceNumber);
		tmpMenu.setStrMenuId(pStrCurElementName);
		tmpMenu.setCalMenuStartTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
		localLoggerAPI.setLoggerScratchData(CUR_MENU,tmpMenu);
	}

	private void doElementExit(ElementExitEvent pLocalEvent){
		CallData tmpCD = null;
		Menu tmpMenu = null;
		LoggerAPI localLoggerAPI = pLocalEvent.getLoggerAPI();

		String strCurDTMF = null;
		String strCurElementName = "";
		String strCurExitState = "";
		String strElementType = "";

		try{
			strCurElementName = pLocalEvent.getElementName();
			localLoggerAPI.setLoggerScratchData(ELEMENT_EXIT_TIMESTAMP,pLocalEvent.getTimestamp());
		}catch(Exception ex){
			strCurElementName = "";
			logger.error("Exception while getting Current Element : "+ex);
		}
		logger.info("Current Element Name: " + strCurElementName);
		strElementType = getElementExitType(strCurElementName);
		logger.info("Element Type: " + strElementType);
		if(strElementType.equals(MENU)){

			try{
				strCurExitState = pLocalEvent.getExitState();
				logger.info("ExitState @ " + strCurElementName + ":" + strCurExitState);
				if(strCurExitState==null){
					strCurExitState="";
				}

				logger.info("Exit State: " + strCurExitState);

				if(strCurExitState.equals(MAX_NOMATCH) || strCurExitState.equals(Constants.NOMATCH)){
					strCurDTMF="NM";
				}else if(strCurExitState.equals(MAX_NOINPUT)  || strCurExitState.equals(Constants.NOINPUT)){
					strCurDTMF="NI";
				}
				else if(strCurExitState.equals(MAX_ERROR) || strCurExitState.equals(Constants.ER) ){
					strCurDTMF="ME";
				}else if(strCurExitState.equals(DONE)){
					String strSecureLoggingOn = localLoggerAPI.getElementData(strCurElementName,"isSecured");

					if("Y".equals(strSecureLoggingOn))
					{
						strCurDTMF ="SECURED_DATA";	
					}
					else{
						logger.info("Entered into Done: " + strCurExitState);
						
						String actualMenuID = "";
						if(strCurElementName.contains(".")) {
							String[] pStrCurElementNameArr = (Pattern.compile("\\.").split(strCurElementName));
							actualMenuID = pStrCurElementNameArr[pStrCurElementNameArr.length-1];
						}else {
							actualMenuID=strCurElementName;
						}
						logger.info("strCurElementName: " + strCurElementName+" :: actualMenuID : "+actualMenuID);
						strCurDTMF = localLoggerAPI.getElementData(actualMenuID,"value");
						logger.info("strCurDTMF in Done: " + strCurDTMF);
						strCurDTMF = strCurDTMF.trim();
					}

				}else{
					strCurDTMF = "";
				}
				logger.info("curDTMF: " + strCurDTMF);

			}catch(Exception parseEx){
				logger.info("Exception thrown: " );
				strCurDTMF = "";


			}
			localLoggerAPI.setLoggerScratchData(LAST_ACCESS_MENU_OPTION,strCurDTMF);
			tmpMenu = getCurrentMenuObject(localLoggerAPI);
			tmpMenu.setStrMenuOption(strCurDTMF);
			tmpMenu.setCalMenuEndTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
			tmpCD = getCallDataObject(localLoggerAPI);
			tmpCD.addMenu(tmpMenu);
			localLoggerAPI.setLoggerScratchData(CALL_DATA,tmpCD);
			localLoggerAPI.removeLoggerScratchData(CUR_MENU);
			logger.info("Call Data object is added to scratch data for Menu");


		}else if(strElementType.equals(SPEECH_MENU)) {

			try{
				strCurExitState = pLocalEvent.getExitState();
				logger.info("Speech ExitState @ " + strCurElementName + ":" + strCurExitState);
				if(strCurExitState==null){
					strCurExitState="";
				}
				
				tmpMenu = getCurrentMenuObject(localLoggerAPI);
				logger.info("Exit State: " + strCurExitState);
				logger.info("Entered into Done: " + strCurExitState);
						
				String actualMenuID = tmpMenu.getStrMenuId();
				
				logger.info("strCurElementName: " + strCurElementName+" :: actualMenuID : "+actualMenuID);
				strCurDTMF = localLoggerAPI.getElementData(tmpMenu.getStrMenuId(),"Return_Value");
				if(strCurDTMF!=null && strCurDTMF.length()==16) {
					try{strCurDTMF = strCurDTMF.substring(0,4) + "*********" + strCurDTMF.substring(strCurDTMF.length()-4,strCurDTMF.length());}catch(Exception e) {}
				}
				logger.info("strCurDTMF in Done: " + strCurDTMF);
				//strCurDTMF = strCurDTMF.trim();
				
				if(strCurDTMF==null) {
					if(strCurElementName.contains(".")) {
						String[] pStrCurElementNameArr = (Pattern.compile("\\.").split(strCurElementName));
						String nodetemp = pStrCurElementNameArr[pStrCurElementNameArr.length-1];
						strCurDTMF = (String) localLoggerAPI.getSessionData(nodetemp);
					} else {
						strCurDTMF = strCurExitState;
					}
					
				}
				logger.info("strCurDTMF in Done with alternative value : " + strCurDTMF);
				localLoggerAPI.setLoggerScratchData(LAST_ACCESS_MENU_OPTION,strCurDTMF);
				
				tmpMenu.setStrMenuOption(strCurDTMF);
				tmpMenu.setCalMenuEndTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
				tmpCD = getCallDataObject(localLoggerAPI);
				tmpCD.addMenu(tmpMenu);
				localLoggerAPI.setLoggerScratchData(CALL_DATA,tmpCD);
				localLoggerAPI.removeLoggerScratchData(CUR_MENU);
				logger.info("Call Data object is added to scratch data for Menu");

			}catch(Exception parseEx){
				logger.info("Exception thrown: " );
				strCurDTMF = "";


			}
			
		
		}


	}


	private void doHotEvent(HoteventEvent pLocalEvent){

		LoggerAPI localLoggerAPI = pLocalEvent.getLoggerAPI();
		StringBuffer sbTmp = null;

		String strCurElementName = (String)localLoggerAPI.getLoggerScratchData(CURRENT_ELEMENT);
		String strCurHEElements = "";

		logger.info("HotEvent @ '" + strCurElementName + "'");

		try{
			strCurHEElements = (String)localLoggerAPI.getLoggerScratchData(HE_ELEMENTS);
			if(strCurHEElements==null){
				strCurHEElements = "";
			}
		}catch(Exception ex){
			strCurHEElements = "";
		}

		try{
			localLoggerAPI.setLoggerScratchData(HOT_EVENT,true);
			sbTmp = new StringBuffer(70);
			sbTmp.append(strCurHEElements);
			if(strCurHEElements.equals("")){
				sbTmp = sbTmp.append("HotEvents:'");
			}else{
				sbTmp = sbTmp.append(", '");
			}
			sbTmp = sbTmp.append(strCurElementName);
			sbTmp = sbTmp.append("'");
			localLoggerAPI.setLoggerScratchData(HE_ELEMENTS,sbTmp.toString());
			logger.info("HE Elements:" + sbTmp.toString());
		}catch(Exception ex){
			logger.error("Exception while updating HE Elements. Ex:" + ex.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	private void doEnd(EndEvent pEvent) throws Exception{
		LoggerAPI localLoggerAPI = pEvent.getLoggerAPI();
		String strCallEndReason = "";
		String strSessionID = localLoggerAPI.getSessionId();
		String strIsQueuedCall			=	getSessionValue(pEvent,strSessionID,"S_IS_QUEUED_CALL"); 	// Call ID- GUID
		logger.info("strIsQueuedCall="+strIsQueuedCall);

		if("Y".equalsIgnoreCase(strIsQueuedCall))
		{
			logger.info(" No logging as it is a queued call");
		}
		else
		{
			String strHowCallEnded = "";
			String strCallEndTime = "";



			if(getBooleanFromScratchData(localLoggerAPI,CALL_END_STARTED)){
				throw new Exception("Call End is already called for this session");
			}else{
				localLoggerAPI.setLoggerScratchData(CALL_END_STARTED,"Y");
			}

			//block to capture 'How Call Ended'
			try{
				strHowCallEnded = pEvent.getHowCallEnded().toUpperCase();
			}catch(Exception e){
				strHowCallEnded = "";
			}
			logger.info("Call End time Reason :"+strHowCallEnded);
			//block to capture 'Call end Reason (like Timeout, Error etc.'
			try{
				strCallEndReason = pEvent.getCallResult().toUpperCase();
			}catch(Exception e){
				strCallEndReason = "";
			}


			strCallEndTime = getDate("yyyy-MM-dd HH:mm:ss",pEvent.getTimestamp().getTime());
			logger.info("Call End time has been taken from the Call End Event Timestamp");

			String strXML = new String("");

			String strStartTimeStamp = getDate("yyyyMMddHHmmssSSS",localLoggerAPI.getStartDate());
			String strCallStartTime = getDate("yyyy-MM-dd HH:mm:ss",localLoggerAPI.getStartDate());


			String strCurElementName = localLoggerAPI.getCurrentElement();
			String strDNIS = localLoggerAPI.getDnis();

			//boolean isHotEventFired=false;

			logger.info("End Event started");

			String strCallID = "";
			String strICMCallID ="";
			String strVXMLServerIP = "";
			String strEndType = "";
			String strCallDisposition ="";
			String strErrReason = "";
			String strCLI = "";
			String strLangCode			=	"";
			String strAgentTransfer		=	"";
			String strTransCode ="";
			String strRCKey="";
			String strRCDay="";
			String strRCSeq="";
			ArrayList listHostDetails = new ArrayList();

			logger.info("End Event started");

			//	Reading the Session variables
			strCallID			=	getSessionValue(pEvent,strSessionID,Constants.S_CALLID);
			strICMCallID		=	getSessionValue(pEvent,strSessionID,Constants.S_ICMID);
			strVXMLServerIP		=	getSessionValue(pEvent,strSessionID,Constants.S_VXML_SERVER_IP);
			strDNIS				=	getSessionValue(pEvent,strSessionID,Constants.S_DNIS);
			strCLI				=	getSessionValue(pEvent,strSessionID,Constants.S_ANI);
			strLangCode			=	getSessionValue(pEvent,strSessionID,Constants.S_PREF_LANG);
			String strAgent 	=   getSessionValue(pEvent,strSessionID,Constants.S_AGENT);
			String strANIGroup 	=   getSessionValue(pEvent,strSessionID,Constants.S_ANI_GROUP);
			String strBrand 	=   getSessionValue(pEvent,strSessionID,Constants.S_BRAND);
			String strCallerAuth =  getSessionValue(pEvent,strSessionID,Constants.S_KYC_AUTHENTICATED);
			String strCallOutcome=  getSessionValue(pEvent,strSessionID,Constants.S_CALL_OUTCOME);
			String strCatCode=  getSessionValue(pEvent,strSessionID,Constants.S_CAT_CODE);
			String strClaimNumber=  "NA";
			String finalDistination=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_DESTNUM);
			String callDuration=  getSessionValue(pEvent,strSessionID,Constants.S_CALL_DURATION);
			String finalANI=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_ANI);
			String finalCategory=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_CATEGORY);
			String finalDnis=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_DNIS);
			
			String finalDnisGroup=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_DNIS_GROUP);
			String finalIntent=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_INTENT);
			logger.info("before removing special Characters :: " + finalIntent );
			finalIntent = removeSpecialCharacter(finalIntent);
			logger.info("After removing special Characters :: " + finalIntent );
			String finalLang=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_LANG);
			String finalLOB=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_LOB);
			String stateGroup=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_STATE_GROUP);
			
			String holdON=  getSessionValue(pEvent,strSessionID,Constants.S_HOLD_ON);
			String hoursOfOperation=  getSessionValue(pEvent,strSessionID,Constants.S_CALLCENTER_OPEN_CLOSED);
			String strNoInput=  getSessionValue(pEvent,strSessionID,Constants.S_NO_INPUT);
			String strNoMatch=  getSessionValue(pEvent,strSessionID,Constants.S_NO_MATCH);
			String originalAni=  getSessionValue(pEvent,strSessionID,Constants.S_ORIGINAL_ANI);
			String originalcat=  getSessionValue(pEvent,strSessionID,Constants.S_ORIGINAL_CATEGORY);
			String originalDNIS=  getSessionValue(pEvent,strSessionID,Constants.S_ORIGINAL_DNIS);
			String originalDNISGroup=  getSessionValue(pEvent,strSessionID,Constants.S_ORIGINAL_DNIS_GROUP);
			String strIntent=  getSessionValue(pEvent,strSessionID,Constants.S_INTENT);
			String originalLang=  getSessionValue(pEvent,strSessionID,Constants.S_ORIGINAL_LANGUAGE);
			String originalLOB=  getSessionValue(pEvent,strSessionID,Constants.S_ORIGINAL_LOB);
			String originalStateGroup=  getSessionValue(pEvent,strSessionID,Constants.S_ORIGINAL_STATE_GROUP);
			String phoneType=  getSessionValue(pEvent,strSessionID,Constants.S_PHONE_TYPE);
			String policyNumber=  getSessionValue(pEvent,strSessionID,Constants.S_POLICY_NUM);
			strRCKey=  getSessionValue(pEvent,strSessionID,Constants.S_RCKEY)+"-"+getSessionValue(pEvent,strSessionID,"S_RCDAY");
			String repeat=  getSessionValue(pEvent,strSessionID,Constants.S_REPEAT);
			String routeTransferAction=  getSessionValue(pEvent,strSessionID,Constants.S_ROUTING_TRANSFER_ACTION);
			String finaldestination=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_DESTNUM);
			String routingKey=  getSessionValue(pEvent,strSessionID,Constants.S_ROUTING_KEY);
			String stateName=  getSessionValue(pEvent,strSessionID,Constants.S_STATENAME);
			String transferCode=  getSessionValue(pEvent,strSessionID,Constants.S_MENU_SELCTION_KEY);
			String transferReason=  getSessionValue(pEvent,strSessionID,Constants.S_TRANSFER_REASON);
			strAgentTransfer=getSessionValue(pEvent,strSessionID,"S_TRANSFER_FLAG");
			
			if(transferCode!=null) {
				transferCode= transferCode.replaceAll("[^a-zA-Z0-9]","");
			}
			
			logger.info("strCallID : " +strCallID);
			logger.info("strICMCallID : " +strICMCallID);
			logger.info("strVXMLServerIP : " +strVXMLServerIP);
			logger.info("strDNIS : " +strDNIS);
			logger.info("strCLI : " +strCLI);
			logger.info("strLangCode : " +strLangCode);
			logger.info("strAgent : " +strAgent);
			logger.info("strANIGroup : " +strANIGroup);
			logger.info("strBrand : " +strBrand);
			logger.info("strCallerAuth : " +strCallerAuth);
			logger.info("strCallOutcome : " +strCallOutcome);
			logger.info("strCatCode : " +strCatCode);
			logger.info("strClaimNumber : " +strClaimNumber);
			logger.info("finalDistination : " +finalDistination);
			logger.info("callDuration : " +callDuration);
			logger.info("finalANI : " +finalANI);
			logger.info("finalCategory : " +finalCategory);
			logger.info("finalDnisGroup : " +finalDnisGroup);
			logger.info("finalIntent : " +finalIntent);
			logger.info("finalLang : " +finalLang);
			logger.info("finalLOB : " +finalLOB);
			logger.info("stateGroup : " +stateGroup);
			logger.info("finalDnis : " +finalDnis);
			logger.info("holdON : " +holdON);
			logger.info("hoursOfOperation : " +hoursOfOperation);
			logger.info("lastFunctionName : " +getFromScratchData(localLoggerAPI,LAST_ACCESS_MENU));
			logger.info("lastFunctionResult : " +getFromScratchData(localLoggerAPI,LAST_ACCESS_MENU_OPTION));
			logger.info("strNoInput : " +strNoInput);
			logger.info("strNoMatch : " +strNoMatch);
			logger.info("originalAni : " +originalAni);
			logger.info("originalcat : " +originalcat);
			logger.info("originalDNIS : " +originalDNIS);
			logger.info("originalDNISGroup : " +originalDNISGroup);
			logger.info("strIntent : " +strIntent);
			logger.info("originalLang : " +originalLang);
			logger.info("originalLOB : " +originalLOB);
			logger.info("originalStateGroup : " +originalStateGroup);
			logger.info("phoneType : " +phoneType);
			logger.info("policyNumber : " +policyNumber);
			logger.info("rckey : " +strRCKey);
			logger.info("repeat : " +repeat);
			logger.info("routeTransferAction : " +routeTransferAction);
			logger.info("finaldestination : " +finaldestination);
			logger.info("routingKey : " +routingKey);
			logger.info("stateName : " +stateName);
			logger.info("transferCode : " +transferCode);
			logger.info("transferReason : " +transferReason);
			
			
			// To get host details
			try{
				listHostDetails= getSessionDataForHost(pEvent,strSessionID,Constants.S_HOST_DETAILS_LIST);
				logger.info("Report Logger - No. of Host Interactions: " +listHostDetails.size());
			}catch(Exception e){
				logger.error("Exception while retrieving HOST INTERACTION DETAILS: " + e.getMessage());
			}



			logger.info("session vars are captured");

			if("Y".equals(strAgentTransfer))
			{
				strHowCallEnded = "TRANSFER";
			}
			
			if("APP_SESSION_COMPLETE".equals(strHowCallEnded) && !("Y".equals(strAgentTransfer)))
			{
				strHowCallEnded = "IVR DISCONNECT";

			}
			logger.info("strHowCallEnded :" + strHowCallEnded);

			//Error Reason manipulation
			if("ERROR".equals(strCallEndReason) || "TIMEOUT".equals(strCallEndReason)){
				String strTmpErrReason = strCallEndReason + " @ '" + strCurElementName + "'";
				if("".equals(strErrReason)){
					strErrReason = strTmpErrReason;
				}else{
					strErrReason = strErrReason + " | " + strTmpErrReason;
				}
			}

			logger.info("Error Reason:" + strErrReason);
			if(transferReason==null || "".equals(transferReason)) {
				if(strErrReason!=null && (strErrReason.contains("ERROR") ||strErrReason.contains("error"))) {
					transferReason = "Call transfer due to host failure";
				}else {
					transferReason = "Normal call transfer";
				}
			}
			
			if("Y".equalsIgnoreCase(getSessionValue(pEvent,strSessionID,"S_DR"))) {
				transferReason = "DR transfer due to IVR failure";
			}

			/** Call Duration **/
			long lCallStartTime = Long.parseLong((String) getSessionValue(pEvent,strSessionID,Constants.S_CALLSTART_TIME));
			long lCallEnd = System.currentTimeMillis();
			long lDuration = lCallEnd - lCallStartTime;
			logger.info( "CallDuration: " + lDuration);
			long lSeconds = lDuration / 1000;
			long lMinutes = lDuration / (60 * 1000);
			long lHours = lDuration / (60 * 60 * 1000);
			callDuration = lHours+ ":" + lMinutes + ":" + lSeconds;
			logger.info( "CallDuration(H:M:S): " + lHours+ ":" + lMinutes + ":" + lSeconds);
			/************ Calling Set methods of CallData ***************/
			
			CallData cd = getCallDataObject(localLoggerAPI);
			
			cd.setStrICMCallID(strICMCallID);
			cd.setICMCallID(strICMCallID);
			cd.setOriginalDNIS(strDNIS);
			cd.setOriginalANI(strCLI);
			cd.setCallStartTime(strCallStartTime);
			cd.setCallEndTime(strCallEndTime);		
			cd.setFinalLang(strLangCode);
			cd.setLastFunctionName(getFromScratchData(localLoggerAPI,LAST_ACCESS_MENU));
			cd.setLastFunctionResult(getFromScratchData(localLoggerAPI,LAST_ACCESS_MENU_OPTION));
			cd.setAgent(strAgentTransfer);
			cd.setTransferCode(strTransCode);
			cd.setDispositionCode(strHowCallEnded);
			cd.setErrReason(strErrReason);
			cd.setListHostDetails(listHostDetails);
			cd.setAgent(strAgent);
			cd.setAniGroup(strANIGroup);
			cd.setBrand(strBrand);
			cd.setCallOutcome(strHowCallEnded);
			cd.setCallerAUTH(strCallerAuth);
			cd.setCatCode(strCatCode);
			cd.setClaimNumber(strClaimNumber);
			cd.setDestination(finalDistination);
			cd.setDuration(callDuration);
			cd.setFinalANI(finalANI);
			cd.setFinalCategory(finalCategory);
			cd.setFinalDNIS(finalDnis);
			cd.setFinalDNISGroup(finalDnisGroup);
			cd.setFinalIntent(finalIntent);
			cd.setFinalLang(finalLang);
			cd.setFinalLOB(finalLOB);
			cd.setFinalStateGroup(stateGroup);
			cd.setHoldOn(holdON);
			cd.setHoursOfOperation(hoursOfOperation);
			cd.setNoInput(strNoInput);
			cd.setNoMatch(strNoMatch);
			cd.setOriginalANI(originalAni);
			cd.setOriginalCategory(originalcat);
			cd.setOriginalDNIS(originalDNIS);
			cd.setOriginalDNISGroup(originalDNISGroup);
			cd.setOriginalIntent(strIntent);
			cd.setOriginalLang(originalLang);
			cd.setOriginalLOB(originalLOB);
			cd.setOriginalStateGroup(originalStateGroup);
			cd.setPhoneType(phoneType);
			cd.setPolicyNumber(policyNumber);
			cd.setStrRCKey(strRCKey);
			cd.setRepeat(repeat);
			cd.setRouteAction(routeTransferAction);
			cd.setRouteDestination(finaldestination);
			cd.setRouteRule(routingKey);
			cd.setState(stateName);
			cd.setTransferCode(transferCode);
			cd.setTransferReason(transferReason);
			
			
			
			/** **/

			logger.info("Call Data variables set");

			try{
				strXML = cd.getXML(pEvent);
				
				
				org.json.JSONObject xmlJSONObj = XML.toJSONObject(strXML,true);
	            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
	            
	            logger.info("CALLDATA JSON packet **** : "+jsonPrettyPrintString);
	   
				hostUrl=getSessionValue(pEvent,strSessionID,"S_REPORT_URL");
				int intReadTimeout=Integer.parseInt(getSessionValue(pEvent,strSessionID,"S_READ_TIMEOUT"));
				int intConnectionTimeout=Integer.parseInt(getSessionValue(pEvent,strSessionID,"S_CON_TIMEOUT"));
				JSONObject objJSONReportRequest = new JSONObject();
				JSONParser objJSONParser = new JSONParser();
				
				logger.info("CALLDATA Parser **** ");
				objJSONReportRequest = (JSONObject) objJSONParser.parse(jsonPrettyPrintString);
				logger.info("CALLDATA intReadTimeout **** : "+intReadTimeout);
				logger.info("CALLDATA hostUrl **** : "+hostUrl);
				logger.info("CALLDATA strSessionID **** : "+strSessionID);
				logger.info("CALLDATA intConnectionTimeout **** : "+intConnectionTimeout);
				LoggerContext context = (LoggerContext)pEvent.getLoggerApplicationAPI().getApplicationData("LOGGER");
				
				InsertCallDataToQueue objInsertCallDataToQueue = new InsertCallDataToQueue();
				jObjectResponse = objInsertCallDataToQueue.start(hostUrl, strSessionID, objJSONReportRequest, intReadTimeout, intConnectionTimeout, context);
				
				if(jObjectResponse!=null)
				{
					
					if("200".equalsIgnoreCase(""+(int)jObjectResponse.get("responseCode")))
					{
						logger.info("Successfull logging");
					}
					else
					{
						String reportpath = (String)pEvent.getLoggerApplicationAPI().getApplicationData(Constants.A_REPORT_DATA_PATH);
						if(reportpath!=null) {
							strXMLFilePath=reportpath;
						}
						logger.info(" logging Failed. Hence writing to flat file in "+strXMLFilePath);
						writeXMLToFile(strSessionID,jsonPrettyPrintString);
					}
				}


				} catch (Exception e) {
					logger.info(" logging Failed due to exception. Hence writing to flat file "+GlobalsCommon.getExceptionTrace(e));
					writeXMLToFile(strSessionID,strXML);
				}
			}
		}
			

	private CallData getCallDataObject(LoggerAPI pLoggerAPI){
		CallData returnCD = null;
		try{
			returnCD = (CallData)pLoggerAPI.getLoggerScratchData(CALL_DATA);
			if(returnCD==null){
				logger.info("The CallData object is null.  Hence new object is getting created");
				returnCD = new CallData(strAppName,pLoggerAPI.getSessionId(),logger);
			}
		}catch(Exception ex){
			returnCD = new CallData(strAppName,pLoggerAPI.getSessionId(),logger);
		}
		return returnCD;
	}

	private Menu getCurrentMenuObject(LoggerAPI pLoggerAPI){
		Menu returnMenu = null;
		try{
			returnMenu = (Menu)pLoggerAPI.getLoggerScratchData(CUR_MENU);
			if(returnMenu==null){
				logger.info("The Current Menu object is null.  Hence new object is getting created");
				returnMenu = new Menu();
			}
		}catch(Exception ex){
			returnMenu = new Menu();
		}
		return returnMenu;
	}

	private String getSessionValue(EndEvent pEvent, String pStrSessionID, String pStrSessionVarName){
		String strRetValue = "";
		try{
			strRetValue = (String)pEvent.getLoggerAPI().getSessionData(pStrSessionVarName);
			if(strRetValue==null){
				strRetValue = "";
			}
			logger.info(pStrSessionVarName + ":" + strRetValue);
		}catch(Exception e){
			logger.error("Failed to get Session Var[" + pStrSessionVarName + "]");
			strRetValue = "";
		}
		return strRetValue;
	}

	private LinkedList getSessionData(EndEvent pEvent, String pStrSessionID, String pStrSessionVarName)
	{
		LinkedList listRetValue = new LinkedList();
		try{
			listRetValue = (LinkedList)pEvent.getLoggerAPI().getSessionData(pStrSessionVarName);
			if(listRetValue==null){
				listRetValue = new LinkedList();
			}
			logger.info(pStrSessionVarName + ":" + listRetValue);
		}catch(Exception e){
			logger.error("Failed to get Session Var[" + pStrSessionVarName + "]");
			//strRetValue = "";
		}
		return listRetValue;
	}
	
	private ArrayList getSessionDataForHost(EndEvent pEvent, String pStrSessionID, String pStrSessionVarName)
	{
		ArrayList listRetValue = new ArrayList();
		try{
			listRetValue = (ArrayList)pEvent.getLoggerAPI().getSessionData(pStrSessionVarName);
			if(listRetValue==null){
				listRetValue = new ArrayList();
			}
			logger.info(pStrSessionVarName + ":" + listRetValue);
		}catch(Exception e){
			logger.error("Failed to get Session Var[" + pStrSessionVarName + "]");
			//strRetValue = "";
		}
		return listRetValue;
	}


	private boolean getBooleanFromScratchData(LoggerAPI pLoggerAPI, String pStrKey){
		boolean boolRetValue = false;
		try{
			if(pLoggerAPI.getLoggerScratchData(pStrKey)==null){
				boolRetValue = false;
			}else{
				boolRetValue = true;
			}
		}catch(Exception ex){
			boolRetValue = false;
		}
		return boolRetValue;
	}

	private String getFromScratchData(LoggerAPI pLoggerAPI, String pStrKey){
		String strRetValue = "";
		try{
			strRetValue = (String)pLoggerAPI.getLoggerScratchData(pStrKey);
			if(strRetValue==null){
				strRetValue = "";
			}
		}catch(Exception ex){
			strRetValue = "";
		}
		return strRetValue;
	}
	private void addToMenuDTMF(LoggerAPI pLoggerAPI, String pStrToBeAdded, char pChrDelimiter){
		StringBuffer sbTmp = null;
		String strTmp = null;

		try{
			strTmp = getFromScratchData(pLoggerAPI,MENU_DTMF_PATH);
			if(strTmp.equals("")){
				pLoggerAPI.setLoggerScratchData(MENU_DTMF_PATH,pStrToBeAdded);
			}else{
				sbTmp = new StringBuffer(500);
				sbTmp.append(strTmp);
				sbTmp.append(pChrDelimiter);
				sbTmp.append(pStrToBeAdded);
				pLoggerAPI.setLoggerScratchData(MENU_DTMF_PATH,sbTmp.toString());
			}
		}catch(Exception ex){
			logger.error("Exception occurred while adding to Menu DTMF path during HE. Ex:" + ex.getMessage());
		}
	}


	private String getDate(String pStrFormat, Date pDate){
		DateFormat fmt = null;
		if(pStrFormat.equals("dd/MM/yyyy HH:mm:ss")){
			fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		}
		if(pStrFormat.equals("MM/dd/yyyy HH:mm:ss")){
			fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		}
		if(pStrFormat.equals("yyyyMMddHHmmssSSS")){
			fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		}if(pStrFormat.equals("yyyy-MM-dd HH:mm:ss")) {
			fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}

		return fmt.format(pDate);
	}


	private String getElementType(String pStrElementName){
		int intElementLength = 0;

		if(pStrElementName==null || pStrElementName.equals("")){
			return EMPTY;
		}

		intElementLength = pStrElementName.length();

		if(intElementLength <=0){
			return EMPTY;
		}
		if(pStrElementName.contains("_PA_")){
			return AUDIO;
		}
		else if(pStrElementName.endsWith("_DM") || pStrElementName.endsWith("_DC")  || (pStrElementName.contains("_MN") && !pStrElementName.contains("_VALUE") && !pStrElementName.contains("_Call")) ){
			logger.info("XML DTMF Menu:" + pStrElementName);
			return MENU;
		}else if(pStrElementName.endsWith("_Call") && (pStrElementName.contains("_MN") && !pStrElementName.contains("_VALUE")) ){
			logger.info("XML SPEECH Menu:" + pStrElementName);
			return SPEECH_MENU;
		}
		return OTHERS;
	}
	
	private String getElementExitType(String pStrElementName){
		int intElementLength = 0;

		if(pStrElementName==null || pStrElementName.equals("")){
			return EMPTY;
		}

		intElementLength = pStrElementName.length();

		if(intElementLength <=0){
			return EMPTY;
		}
		if(pStrElementName.contains("_PA_")){
			return AUDIO;
		}
		else if(pStrElementName.contains("_MN") && !pStrElementName.contains("_VALUE") && !pStrElementName.contains("_Call")){
			logger.info("XML:" + pStrElementName);
			return MENU;
		}else if(pStrElementName.contains("_MN") && pStrElementName.contains("_VALUE")){
			logger.info("XML:" + pStrElementName);
			return SPEECH_MENU;
		}


		return OTHERS;
	}
	
	private void writeXMLToFile(String pStrIVRKey, String pStrXMLString){

		FileOutputStream fos = null;
		PrintWriter pwXML = null;
		String url =null;
		String callID = pStrIVRKey;
		try{
			logger.info("XML from calldata--->:" + pStrXMLString);
			
			fos = new FileOutputStream(strXMLFilePath + pStrIVRKey + ".json");
			logger.info("New FileOutputStream (" + strXMLFilePath + pStrIVRKey + ".XML" + " is created");
			pwXML = new PrintWriter(fos);
			pwXML.println(pStrXMLString);
		}catch(Exception e){
			logger.error("Exception : "+e.getMessage());
		} finally {
			if(fos!=null){
				try{
					fos.flush();
					if(pwXML!=null){
						pwXML.flush();
					}
					fos.close();
				}catch(Exception e){
					logger.error("Exception : "+e.getMessage());
				}
			}
			fos = null;

			if(pwXML!=null){
				try{
					pwXML.close();
				}catch(Exception e){
					logger.error("Exception : "+e.getMessage());
				}
			}
			pwXML = null;
			logger.info("pwXML is set to NULL");
		}
	}
	
	private String removeSpecialCharacter(String strSpecialCharsToRemove) {
		if(strSpecialCharsToRemove!=null) {
			strSpecialCharsToRemove= strSpecialCharsToRemove.replaceAll("[^a-zA-Z0-9]","");
		}
		return strSpecialCharsToRemove;
	}
	

}
