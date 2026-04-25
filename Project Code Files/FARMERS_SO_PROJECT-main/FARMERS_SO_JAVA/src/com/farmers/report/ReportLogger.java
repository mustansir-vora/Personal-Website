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
import java.util.HashMap;
import java.util.LinkedList;
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
import com.farmers.util.SequenceNumber;
//*****************************************
import com.farmers.util.CommonAPIAccess;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.AccountLinkAniLookup_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.Constants;



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
	
	//CS1263594 - Add tenth, hundred, thousands - Arshath 
	private static final String LAST_ACCESS_ANNO = "LA";
	private static final String PARENT_MENU_ID ="PAR_ID";
	private static final String HE_ELEMENTS = "HEE";
	private static final String LAST_ACCESS_MENU_OPTION = "LMO";

	private static final String DONE = "done";
	private static final String MAX_NOMATCH = "max_nomatch";
	private static final String MAX_NOINPUT = "max_noinput";
	private static final String MAX_ERROR = "max_error";

	private static final String CALL_DATA = "CALL_DATA";
	private static final String CUR_MENU = "CUR_MENU";
	
	//CS1263594 - Add tenth, hundred, thousands - Arshath 
	private static final String CUR_ANN = "CUR_ANN";
	
	private static final String ELEMENT_ENTRY_TIMESTAMP = "ELEMENT_ENTRY_TIMESTAMP";
	private static final String ELEMENT_EXIT_TIMESTAMP = "ELEMENT_EXIT_TIMESTAMP";
	
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;

	private String strCVPVXMLServerIP = "";
	private String strXMLFilePath = "C:\\Servion\\FARMERS_SO_CVP\\ReportData\\";
	private String strAppName = "";
	
	ContainModule obj = null;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT);

	String hostUrl=null;
	URL url =null;
	JSONObject jsonRequest=null;
	String strRequest =null;
	JSONParser jParser = null;
	JSONObject jObjectResponse =null;
	
	HashMap<String, String> headerDetails = null;
	String strIVRpath = "";


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
			File file = new File(new StringBuffer(Constants.CONFIG_PATH).append(Constants.LOG4J_XML_FILE).toString());
            context.setConfigLocation(file.toURI());
            logger.info(new StringBuffer(" ReportLogger - logger4J Config file loaded successfully from ").append(Constants.CONFIG_PATH).toString());
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

	@SuppressWarnings("unchecked")
	private void doElementEnter(ElementEnterEvent pLocalEvent){
		LoggerAPI localLoggerAPI = pLocalEvent.getLoggerAPI();

		String strSessionID = localLoggerAPI.getSessionId();
		String strCurElementName = pLocalEvent.getElementName();
		
		try {
			headerDetails = (HashMap<String, String>) pLocalEvent.getLoggerAPI().getSessionData("S_HEADER_DETAILS");
			if(null != headerDetails.get("RPT_Ivr_Traversal_Path")) {
				strIVRpath = headerDetails.get("RPT_Ivr_Traversal_Path");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		

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
		
		if((strCurElementName.contains("_MN_") && !strCurElementName.contains("_VALUE")) || strCurElementName.contains("_PA_") || strCurElementName.contains("_HOST_") || strCurElementName.contains("_ADM_")) {
			if(strCurElementName.contains(".")) {
				String[] strCurElementNameArr = (Pattern.compile("\\.").split(strCurElementName));
				strCurElementName =strCurElementNameArr[strCurElementNameArr.length-1];
			}
			if(null != strIVRpath && !strIVRpath.isEmpty()) {
				strIVRpath = strIVRpath+"|"+strCurElementName;
			} else {
				strIVRpath = strCurElementName;
			}
			
			/*
			//Set S_ContainedCount details in to session for eGain reports
			try {
				String strContainedConfig = (String) pLocalEvent.getLoggerAPI().getSessionData("S_Contained_Config");
				if(strContainedConfig.contains(strCurElementName)) {
					int containedCount = headerDetails.get("S_ContainedCount") != null ? Integer.valueOf(headerDetails.get("S_ContainedCount")): 0;
					headerDetails.put("S_ContainedCount",String.valueOf(containedCount+1));
					logger.info("Updated Contained count after "+strCurElementName+" Audio : "+headerDetails.get("S_ContainedCount"));
				}
			} catch (Exception e) {
				logger.error("Exception while setting S_ContainedCount into session : "+e);
			}
			*/
			
		}
		//logger.info("strIVRpath : "+strIVRpath);
		headerDetails.put("RPT_Ivr_Traversal_Path", strIVRpath);
		localLoggerAPI.setLoggerScratchData("S_HEADER_DETAILS",headerDetails);
		
	
	}
	
	private void doElementEnterForAudio(ElementEnterEvent pEvent, String pStrSessionID, String pStrCurElementName){
		LoggerAPI localLoggerAPI = pEvent.getLoggerAPI();
		Announcement tmpAnnouncement = new Announcement(); 
		//CallData tmpCD = null;

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
		//tmpAnnouncement.setCalAnnouncementDateTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
		 tmpAnnouncement.setCalAnnouncementStartTime(simpleDateFormat.format(Calendar.getInstance().getTimeInMillis()));
		    logger.info("Announcement Start Time: " + tmpAnnouncement.getCalAnnouncementStartTime());
//		tmpCD = getCallDataObject(localLoggerAPI);
//		tmpCD.addAnnouncement(tmpAnnouncement);
		//localLoggerAPI.setLoggerScratchData(CALL_DATA,tmpCD);
		
		localLoggerAPI.setLoggerScratchData(CUR_ANN, tmpAnnouncement);
		logger.info("Call Data object is added to scratch data for Announcement");
	}

	@SuppressWarnings("unchecked")
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
		
		headerDetails = (HashMap<String, String>) pEvent.getLoggerAPI().getSessionData("S_HEADER_DETAILS");
		headerDetails.put("RPT_Last_Function_Name", pStrCurElementName);
		localLoggerAPI.setLoggerScratchData("S_HEADER_DETAILS",headerDetails);
		
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
	
	@SuppressWarnings("unchecked")
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
		headerDetails = (HashMap<String, String>) pEvent.getLoggerAPI().getSessionData("S_HEADER_DETAILS");
		headerDetails.put("RPT_Last_Function_Name", pStrCurElementName);
		localLoggerAPI.setLoggerScratchData("S_HEADER_DETAILS",headerDetails);
		/** Menu ID & Menu Accessed Time **/
		SequenceNumber sequenceNumber=(SequenceNumber)localLoggerAPI.getSessionData(Constants.S_SEQUENCE_COUNTER);
		tmpMenu.setSequenceNum(sequenceNumber.get());
		localLoggerAPI.setLoggerScratchData(Constants.S_SEQUENCE_COUNTER,sequenceNumber);
		tmpMenu.setStrMenuId(pStrCurElementName);
		tmpMenu.setCalMenuStartTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
		localLoggerAPI.setLoggerScratchData(CUR_MENU,tmpMenu);
	}

	@SuppressWarnings("unchecked")
	private void doElementExit(ElementExitEvent pLocalEvent){
		CallData tmpCD = null;
		Menu tmpMenu = null;
		LoggerAPI localLoggerAPI = pLocalEvent.getLoggerAPI();

		String strCurDTMF = null;
		String strCurAnno = null;
		Announcement tmpAnnouncement = null;
		String strCurElementName = "";
		String strCurExitState = "";
		String strElementType = "";
		
		//START - CS1240948 :: initialize Nomatch & NoInput Counters for each menu while exiting element
		String noMatch_Count = Constants.EmptyString;
		String noInput_Count = Constants.EmptyString;
		
		
		String htDTMFRetry = Constants.EmptyString;
		String htGDFRetry = Constants.EmptyString;
		
		try {
			noMatch_Count = (String) pLocalEvent.getLoggerAPI().getSessionData("NoMatch") != null ? (String) pLocalEvent.getLoggerAPI().getSessionData("NoMatch") : "0";
			noInput_Count = (String) pLocalEvent.getLoggerAPI().getSessionData("NoInput") != null ? (String) pLocalEvent.getLoggerAPI().getSessionData("NoInput") : "0";
			
		}
		catch (Exception e) {
			logger.error("Exception while fetching NoMatch & NoInput Counters :: "+e);
		}
		//END - CS1240948 :: initialize Nomatch & NoInput Counters for each menu while exiting element
     
		
		//Start:GDF Connectivity Issue retries changes
				try {
					htDTMFRetry = (String) pLocalEvent.getLoggerAPI().getSessionData("HOTEVENT_DTMF_RETRY") != null ? (String) pLocalEvent.getLoggerAPI().getSessionData("HOTEVENT_DTMF_RETRY") : "N";
					htGDFRetry = (String) pLocalEvent.getLoggerAPI().getSessionData("HOTEVENT_GDF_RETRY") != null ? (String) pLocalEvent.getLoggerAPI().getSessionData("HOTEVENT_GDF_RETRY") : "N";
					
				}
				catch (Exception e) {
					logger.error("Exception while fetching hotevent retry Counters :: "+e);
				}
				//END:GDF Connectivity Issue retries changes
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
		String[] splitElementName = Pattern.compile("\\.").split(strCurElementName);
		if (null == obj) {
			obj = new ContainModule();
		}
		
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
			headerDetails = (HashMap<String, String>) pLocalEvent.getLoggerAPI().getSessionData("S_HEADER_DETAILS");
			headerDetails.put("RPT_Last_Function_Result", strCurDTMF);
			localLoggerAPI.setLoggerScratchData("S_HEADER_DETAILS",headerDetails);
			
			tmpMenu = getCurrentMenuObject(localLoggerAPI);
			tmpMenu.setStrMenuOption(strCurDTMF);
			tmpMenu.setCalMenuEndTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
			
			//START - CS1240948 :: Set Nomatch, NoInput & combined tries Counters into Bean Object
			tmpMenu.setStrNo_Match(noMatch_Count);
			tmpMenu.setStrNo_Input(noInput_Count);
			tmpMenu.setStrCombined_Tries(String.valueOf(Integer.valueOf(noMatch_Count) + Integer.valueOf(noInput_Count)));
			//END - CS1240948 :: Set Nomatch, NoInput & combined tries Counters into Bean Object
			
			//START:GDF Connectivity Issue retries changes
			 if(null!= pLocalEvent.getLoggerAPI().getSessionData("S_HOTEVENT")&& "Y".equalsIgnoreCase((String)pLocalEvent.getLoggerAPI().getSessionData("S_HOTEVENT"))) {
				 if("Y".equalsIgnoreCase(htDTMFRetry)) tmpMenu.setStrHoteventRetry("DTMF Retry=1");
				 else if("Y".equalsIgnoreCase(htGDFRetry)) tmpMenu.setStrHoteventRetry("GDF Retry=1");
			 }else {
				 tmpMenu.setStrHoteventRetry(" ");
			 }
			//END:GDF Connectivity Issue retries changes
			 
			tmpCD = getCallDataObject(localLoggerAPI);
			tmpCD.addMenu(tmpMenu);
			localLoggerAPI.setLoggerScratchData(CALL_DATA,tmpCD);
			localLoggerAPI.removeLoggerScratchData(CUR_MENU);
			logger.info("Call Data object is added to scratch data for Menu");

			obj.reportingLogic(splitElementName[splitElementName.length-1], pLocalEvent, strCurDTMF);

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
					if(strCurDTMF!=null && strCurDTMF.length()==16 && strCurDTMF.matches("\\d+")) {
						try{strCurDTMF = strCurDTMF.substring(0,4) + "*********" + strCurDTMF.substring(strCurDTMF.length()-4,strCurDTMF.length());}catch(Exception e) {}
					}
					logger.info("strCurDTMF in Done: " + strCurDTMF);
					//strCurDTMF = strCurDTMF.trim();
					
					if(strCurDTMF==null) {
						if(strCurElementName.contains(".")) {
							String[] pStrCurElementNameArr = (Pattern.compile("\\.").split(strCurElementName));
							String nodetemp = pStrCurElementNameArr[pStrCurElementNameArr.length-1];
							strCurDTMF = (String) localLoggerAPI.getSessionData(nodetemp);
						}else if (null != (String)localLoggerAPI.getSessionData(strCurElementName)) {
							strCurDTMF = (String) localLoggerAPI.getSessionData(strCurElementName);
						}else {
							strCurDTMF = strCurExitState;
						}
						
					}
			
				logger.info("strCurDTMF in Done with alternative value : " + strCurDTMF);
				localLoggerAPI.setLoggerScratchData(LAST_ACCESS_MENU_OPTION,strCurDTMF);
				headerDetails = (HashMap<String, String>) pLocalEvent.getLoggerAPI().getSessionData("S_HEADER_DETAILS");
				headerDetails.put("RPT_Last_Function_Result", strCurDTMF);
				localLoggerAPI.setLoggerScratchData("S_HEADER_DETAILS",headerDetails);
				tmpMenu.setStrMenuOption(strCurDTMF);
				tmpMenu.setCalMenuEndTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
				
				//START - CS1240948 :: Set Nomatch, NoInput & combined tries Counters into Bean Object
				tmpMenu.setStrNo_Match(noMatch_Count);
				tmpMenu.setStrNo_Input(noInput_Count);
				tmpMenu.setStrCombined_Tries(String.valueOf(Integer.valueOf(noMatch_Count) + Integer.valueOf(noInput_Count)));
				//END - CS1240948 :: Set Nomatch, NoInput & combined tries Counters into Bean Object
				
				 // START - All Bu's Nlu Utterance
		        
				String userUtterance = (String) localLoggerAPI.getSessionData("S_CALLER_UTTERANCE");

				if (userUtterance != null && !userUtterance.isEmpty() &&
				    (actualMenuID.contains("NLCL_MN_001") || actualMenuID.contains("NLCL_MN_002"))) {

				    if (userUtterance.length() > 100) {
				        userUtterance = userUtterance.substring(0, 100);
				    }

				    tmpMenu.setStrNluMenuUtterance(userUtterance);
				}

		        // END - All Bu's Nlu Utterance
				
				//START:GDF Connectivity Issue retries changes
				 if("N".equalsIgnoreCase((String)pLocalEvent.getLoggerAPI().getSessionData("S_HOTEVENT_TRANSFER")) && "2nd".equalsIgnoreCase((String)pLocalEvent.getLoggerAPI().getSessionData("S_HOTEVENT_RETRY_COUNT"))) {
					 if("Y".equalsIgnoreCase(htDTMFRetry)) tmpMenu.setStrHoteventRetry("DTMF Retry=1");
					 else if("Y".equalsIgnoreCase(htGDFRetry)) tmpMenu.setStrHoteventRetry("GDF Retry=1");
				 }else if("Y".equalsIgnoreCase((String)pLocalEvent.getLoggerAPI().getSessionData("S_HOTEVENT_TRANSFER")) && "2nd".equalsIgnoreCase((String)pLocalEvent.getLoggerAPI().getSessionData("S_HOTEVENT_RETRY_COUNT"))) {
					 if("Y".equalsIgnoreCase(htGDFRetry)) tmpMenu.setStrHoteventRetry("GDF Retry=1");				
				 }else {
					 tmpMenu.setStrHoteventRetry(" ");
				 }
				//END:GDF Connectivity Issue retries changes
				tmpCD = getCallDataObject(localLoggerAPI);
				tmpCD.addMenu(tmpMenu);
				localLoggerAPI.setLoggerScratchData(CALL_DATA,tmpCD);
				localLoggerAPI.removeLoggerScratchData(CUR_MENU);
				logger.info("Call Data object is added to scratch data for Menu");

			}catch(Exception parseEx){
				logger.info("Exception thrown: " );
				strCurDTMF = "";


			}
			
			obj.reportingLogic(splitElementName[splitElementName.length-1], pLocalEvent, strCurDTMF);
		}//CS1263594 - Add tenth, hundred, thousands - Arshath -Start
		else if (strElementType.equals(AUDIO)) {
			
			try{
				strCurExitState = pLocalEvent.getExitState();
				logger.info("Announcement ExitState @ " + strCurElementName + ":" + strCurExitState);
				if(strCurExitState==null){
					strCurExitState="";
				}
				
				if (strCurExitState.equals(DONE)||strCurExitState.equals(MAX_NOINPUT)||strCurExitState.equals(MAX_NOMATCH)) {
	                logger.info("Entered into Done: " + strCurExitState);
	                //String actualAnnouncementID = "";

	                if(strCurAnno==null) {
						if(strCurElementName.contains(".")) {
							String[] pStrCurElementNameArr = (Pattern.compile("\\.").split(strCurElementName));
							String Annotemp = pStrCurElementNameArr[pStrCurElementNameArr.length-1];
							strCurAnno = (String) localLoggerAPI.getSessionData(Annotemp);
						}else if (null != (String)localLoggerAPI.getSessionData(strCurElementName)) {
							strCurAnno = (String) localLoggerAPI.getSessionData(strCurElementName);
						}else {
							strCurAnno = strCurExitState;
						}
						
	                strCurAnno = localLoggerAPI.getCurrentElement();
                    //logger.info("strCurElementName: " + strCurElementName + " :: actualAnnouncementID : " + actualAnnouncementID);
                    logger.info("strCurAnno in Done: " + strCurAnno);
				}
			
			}}
			
			catch(Exception parseAnn){
				logger.info("Exception thrown: " + parseAnn);	
			}
			logger.info("curAnnouncement: " + strCurAnno);
	        localLoggerAPI.setLoggerScratchData(LAST_ACCESS_ANNO,strCurAnno);
	                
	        // Retrieve announcement object
	        tmpAnnouncement = getCurrentAnnouncementObject(localLoggerAPI);
	        tmpAnnouncement.setStrAnnouncementId(strCurAnno);
	        tmpAnnouncement.setCalAnnouncementEndTime(simpleDateFormat.format(Calendar.getInstance().getTimeInMillis()));
	        logger.info("Announcement End Time: " + tmpAnnouncement.getCalAnnouncementEndTime());
	                
		    tmpCD = getCallDataObject(localLoggerAPI);
			tmpCD.addAnnouncement(tmpAnnouncement);
			localLoggerAPI.setLoggerScratchData(CALL_DATA,tmpCD);
			localLoggerAPI.removeLoggerScratchData(CUR_ANN);
			logger.info("Call Data object is added to scratch data for Announcement");	
				    
			obj.reportingLogic(splitElementName[splitElementName.length-1], pLocalEvent, "");		
		}	
		//CS1263594 - Add tenth, hundred, thousands- Arshath -End 
		else {
			obj.reportingLogic(splitElementName[splitElementName.length-1], pLocalEvent, "");
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
			String strCallerAuth = "";
			String newViaFlag = "";
			ArrayList listHostDetails = new ArrayList();
			
			//Caller verification Venkatesh K M
			String strCallerVerification="";
			// Balaji K- CS1327384 - Farmers Insurance  US  Call Outcome for Payment US Transfers
			String paymentUsFlag = "";
			//Balaji K - CS1327400 - Farmers Insurance  US Call Outcome for IVR to Text
			String ivrtoText ="";
			
			

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
			String strBrand 	=   getSessionValue(pEvent,strSessionID,Constants.S_BU);
			newViaFlag = getSessionValue(pEvent,strSessionID,"S_NEW_VIA_FLAG");
			paymentUsFlag = getSessionValue(pEvent,strSessionID,"S_PAYMENTUS_CALLOUTCOME");
			ivrtoText = getSessionValue(pEvent, strSessionID, "S_IVRTOTEXT_CALLOUTCOME");
			
			if(newViaFlag.equals(Constants.TRUE)) {
				strCallerAuth =getSessionValue(pEvent,strSessionID,"S_CALLER_AUTH");
			}else {
		     strCallerAuth =  getSessionValue(pEvent,strSessionID,Constants.S_KYC_AUTHENTICATED);
			}
			String strCallOutcome=  getSessionValue(pEvent,strSessionID,Constants.S_CALL_OUTCOME);
			String containableCount = getContainValuesfromHeaderDetailsMap(pEvent, "S_ContainableCount");
			String containedCount = getContainValuesfromHeaderDetailsMap(pEvent, "S_ContainedCount");
			String partiallyContainedCount = getContainValuesfromHeaderDetailsMap(pEvent, "S_PartiallyContainedCount"); 
			String iaDisconnectFlag = getSessionValue(pEvent,strSessionID,"IA_DISCONNECT_FLAG");
			if (null != iaDisconnectFlag && "TRUE".equalsIgnoreCase(iaDisconnectFlag) && Integer.parseInt(partiallyContainedCount) > 0) {
				partiallyContainedCount = String.valueOf(Integer.parseInt(partiallyContainedCount) - 1) ;
			}
			String strCatCode=  getSessionValue(pEvent,strSessionID,Constants.S_CAT_CODE);
			String strClaimNumber=  "NA";
			String finalDistination=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_DESTNUM);
			String callDuration=  getSessionValue(pEvent,strSessionID,Constants.S_CALL_DURATION);
			String finalANI=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_ANI);
			String finalCategory=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_CATEGORY);
			String finalDnis=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_DNIS);
			
			String finalDnisGroup=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_DNIS_GROUP);
			String finalIntent=  getSessionValue(pEvent,strSessionID,Constants.S_FINAL_INTENT);
			if (null == finalIntent || "".equalsIgnoreCase(finalIntent)) {
				finalIntent = getSessionValue(pEvent,strSessionID,Constants.S_INTENT);
			}
			if (null == finalIntent || "".equalsIgnoreCase(finalIntent)) {
				finalIntent = getSessionValue(pEvent,strSessionID,Constants.APPTAG);
			}
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
			//String strIntent=  getSessionValue(pEvent,strSessionID,Constants.APPTAG);
			String strIntent=  getSessionValue(pEvent,strSessionID,"NLCL_MN_001_VALUE");
			if (null == strIntent || "".equalsIgnoreCase(strIntent)) {
				strIntent = getSessionValue(pEvent,strSessionID,Constants.S_INTENT);
			}
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
			strCallerVerification=	getSessionValue(pEvent,strSessionID,Constants.S_CALLER_VERIFICATION);
			String strFailOutRepHandlingFlag= getSessionValue(pEvent,strSessionID,Constants.S_FAILOUT_REP_HANDLING_COUNTER_FLAG);
			String strRepHandlingFlag= getSessionValue(pEvent,strSessionID,Constants.S_REP_HANDLING_COUNTER_FLAG);
			String strNLUFailureFlag=getSessionValue(pEvent,strSessionID,"NLU_FAILURE_FLAG");
			String lastfunctionresultRepcheck=(String) localLoggerAPI.getLoggerScratchData(LAST_ACCESS_MENU_OPTION);
			String NoInputFlag="N";
			String HotEventTransferFlag = getSessionValue(pEvent,strSessionID,"S_HOTEVENT_TRANSFER");
			if(transferCode!=null) {
				transferCode= transferCode.replaceAll("[^a-zA-Z0-9]","");
			}
			
			if(routingKey!=null) {
				routingKey = removeampCharacter(routingKey);
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
			if("".equalsIgnoreCase(getFromScratchData(localLoggerAPI,LAST_ACCESS_MENU_OPTION))) {
				String lastFunName = getFromScratchData(localLoggerAPI,LAST_ACCESS_MENU);
				if(lastFunName.contains("Call")) {
					lastFunName = lastFunName.replace("Call", "VALUE");
					logger.info("lastFunctionResult : " +localLoggerAPI.getSessionData(lastFunName));
				}
			}
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
			logger.info("strCallerVerification : " +strCallerVerification);
			
			// To get host details
			try{
				listHostDetails= getSessionDataForHost(pEvent,strSessionID,Constants.S_HOST_DETAILS_LIST);
				logger.info("Report Logger - No. of Host Interactions: " +listHostDetails.size());
			}catch(Exception e){
				logger.error("Exception while retrieving HOST INTERACTION DETAILS: " + e.getMessage());
			}



			logger.info("session vars are captured");
            if("Y".equals(strNLUFailureFlag)) {
				
				strHowCallEnded = "No Input Disconnect";
				NoInputFlag="Y";
			}


			if("Y".equals(strAgentTransfer))
			{
				strHowCallEnded = "TRANSFER";
			}
			
			//FNWL CR- Reporting Call_Outcomes- Start
			if("Y".equals(strAgentTransfer) && null != (String) pEvent.getLoggerAPI().getSessionData("RLUS_FLAG") && "Y".equalsIgnoreCase((String) pEvent.getLoggerAPI().getSessionData("RLUS_FLAG")))
			{
				strHowCallEnded = "RLUS Transfer";
			}
			
			if("Y".equals(strAgentTransfer) && null != (String) pEvent.getLoggerAPI().getSessionData("ZINNIA_FLAG") && "TRUE".equalsIgnoreCase((String) pEvent.getLoggerAPI().getSessionData("ZINNIA_FLAG")))
			{
				strHowCallEnded = "ZINNIA Transfer";
			}
			
			if("Y".equals(strAgentTransfer) && null != (String) pEvent.getLoggerAPI().getSessionData("PAYMENTUS_FLAG") && "Y".equalsIgnoreCase((String) pEvent.getLoggerAPI().getSessionData("PAYMENTUS_FLAG")))
			{
				strHowCallEnded = "PaymentUS Transfer";
			}
			
			if("Y".equals(strAgentTransfer) && null != (String) pEvent.getLoggerAPI().getSessionData("PAYMENTUS_FLAG") && "N".equalsIgnoreCase((String) pEvent.getLoggerAPI().getSessionData("PAYMENTUS_FLAG")))
			{
				strHowCallEnded = "PaymentUS Transfer Fail";
			}
			//FNWL CR- Reporting Call_Outcomes- End

			if("APP_SESSION_COMPLETE".equals(strHowCallEnded) && !("Y".equals(strAgentTransfer)))
			{
				strHowCallEnded = "IVR DISCONNECT";

			}
			
			//START - CS1240948 :: Set Call Outcome
			if (null != (String)pEvent.getLoggerAPI().getSessionData("NoInput") && "3".equalsIgnoreCase((String)pEvent.getLoggerAPI().getSessionData("NoInput")) && (strHowCallEnded.equalsIgnoreCase("IVR DISCONNECT") || strHowCallEnded.equalsIgnoreCase("DISCONNECT"))) {
				strHowCallEnded = "NO INPUT DISCONNECT";
				NoInputFlag="Y";
			}
			//End - CS1240948 :: Set Call Outcome
			
			if(("Y".equals(strRepHandlingFlag) || ("Representative".equals(lastfunctionresultRepcheck)) || ("representative-request".equals(finalIntent))|| ("representativerequest".equals(finalIntent))) && ("Y".equals(strAgentTransfer))) 
			{
				strHowCallEnded = "VOLUNTARY TRANSFER";
			}
			
			if("Y".equals(strFailOutRepHandlingFlag) && ("Y".equals(strAgentTransfer)))
			{
				strHowCallEnded = "ERROR TRANSFER";
			}
			
			if("Y".equals(HotEventTransferFlag))
			{
				strHowCallEnded = "ERROR TRANSFER";
			}
			
			if(null != (String)pEvent.getLoggerAPI().getSessionData("IA_DISCONNECT_FLAG") && "TRUE".equalsIgnoreCase((String)pEvent.getLoggerAPI().getSessionData("IA_DISCONNECT_FLAG")) && (strHowCallEnded.equalsIgnoreCase("DISCONNECT") || strHowCallEnded.equalsIgnoreCase("IVR DISCONNECT"))) {
				
				strHowCallEnded = "IA DISCONNECT";
			
			}
			
			if (Integer.parseInt(containableCount) > 0 && Integer.parseInt(containedCount) > 0 && Integer.parseInt(containableCount) == Integer.parseInt(containedCount) && partiallyContainedCount.equalsIgnoreCase("0") && strHowCallEnded.equalsIgnoreCase("HANGUP")) {
			
				strHowCallEnded = "COMPLETED";
			
			}
			
			if (Integer.parseInt(containableCount) > 0 && Integer.parseInt(containedCount) > 0 && Integer.parseInt(containableCount) == Integer.parseInt(containedCount) && partiallyContainedCount.equalsIgnoreCase("0") && null != pEvent.getLoggerAPI().getSessionData("EMP_SERV_COMPLETED") && "TRUE".equalsIgnoreCase((String) pEvent.getLoggerAPI().getSessionData("EMP_SERV_COMPLETED"))) {
				
				strHowCallEnded = "COMPLETED";
			
			}
			
			//Start Balaji K- CS1327384 - Farmers Insurance  US  Call Outcome for Payment US Transfers
			if("Y".equals(paymentUsFlag) && !strHowCallEnded.equalsIgnoreCase("HANGUP")) {
				strHowCallEnded = "PAYMENTUS TRANSFER";
			}
			//End Balaji K-CS1327384 - Farmers Insurance  US  Call Outcome for Payment US Transfers
			if("Y".equals(ivrtoText) && !strHowCallEnded.equalsIgnoreCase("HANGUP")) {
				strHowCallEnded = "IVR TO TEXT";
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
				}else if("Y".equalsIgnoreCase(NoInputFlag)) {
					transferReason="Disconnect";
				}else if("Y".equalsIgnoreCase(HotEventTransferFlag)){
					transferReason = "GDF RETRY FAILURE";
				}
				else {
					transferReason = "Normal call transfer";
				}
			}
			
			if (null != (String)pEvent.getLoggerAPI().getSessionData("NoInput") && "3".equalsIgnoreCase((String)pEvent.getLoggerAPI().getSessionData("NoInput")) && (strHowCallEnded.equalsIgnoreCase("IVR DISCONNECT") || strHowCallEnded.equalsIgnoreCase("DISCONNECT"))) {
				transferReason = "Disconnect";
				
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
			logger.info("Test Logger ");
			cd.setstrContainableCount(containableCount);
			cd.setstrContainedCount(containedCount);
			cd.setstrPartiallyContainedCount(partiallyContainedCount);
			cd.setstrCallerVerification(strCallerVerification);	
			
			/** **/

			logger.info("Call Data variables set");
			String jsonPrettyPrintString=null;
			try{
				strXML = cd.getXML(pEvent);
				
				
				org.json.JSONObject xmlJSONObj = XML.toJSONObject(strXML,true);
	            jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
	            
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
				LoggerContext context = (LoggerContext)pEvent.getLoggerApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
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
						logger.error(" logging Failed. Hence writing to flat file in "+strXMLFilePath);
						writeXMLToFile(strSessionID,jsonPrettyPrintString);
					}
				 }
				} catch (Exception e) {
					logger.info("logging Failed due to exception. Hence writing to flat file"+e);
					if(jsonPrettyPrintString!=null && !"".equals(jsonPrettyPrintString)) {
						writeXMLToFile(strSessionID,jsonPrettyPrintString);
					}else {
						writeXMLToFile(strSessionID,strXML);
					}
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

	//CS1263594 - Add tenth, hundred, thousands - Arshath -Start
		private Announcement getCurrentAnnouncementObject(LoggerAPI pLoggerAPI) {
			Announcement returnAnn = null;
			
			try {
				returnAnn = (Announcement)pLoggerAPI.getLoggerScratchData(CUR_ANN);
				if(returnAnn==null){
					logger.info("The Current Menu object is null.  Hence new object is getting created");
					returnAnn = new Announcement();
				}
			} catch (Exception e) {
				returnAnn = new Announcement();
			}
			return returnAnn;
		}
		//CS1263594 - Add tenth, hundred, thousands - Arshath -End
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
    
	private String getSessionValueLog(ApplicationEvent Event, String pStrSessionID, String pStrSessionVarName){
		String strRetValue = "";
		try{
			strRetValue = (String)Event.getLoggerApplicationAPI().getApplicationData(pStrSessionVarName);
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
			if("".equalsIgnoreCase(strRetValue)) {
				String lastFunName = (String)pLoggerAPI.getLoggerScratchData(LAST_ACCESS_MENU);
				if(lastFunName.contains("Call")) {
					lastFunName = lastFunName.replace("Call", "VALUE");
					strRetValue = (String)pLoggerAPI.getSessionData(lastFunName);
				}
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
	private String removeampCharacter(String strSpecialCharsToRemove) {
		if(strSpecialCharsToRemove!=null) {
			strSpecialCharsToRemove= strSpecialCharsToRemove.replaceAll("&","");
		}
		return strSpecialCharsToRemove;
	}
	
	@SuppressWarnings("unchecked")
	private String getContainValuesfromHeaderDetailsMap(EndEvent pEvent, String pStrSessionVarName) {
		String str = null;
		
		try {
			headerDetails = (HashMap<String, String>) pEvent.getLoggerAPI().getSessionData("S_HEADER_DETAILS");
			logger.info("HeaderDetails Hashmap :: " + headerDetails);
			str = (null != headerDetails.get(pStrSessionVarName)) ? headerDetails.get(pStrSessionVarName) : "0";
			logger.info(pStrSessionVarName + ":" + str);
		}
		catch (Exception e) {
			logger.error("Failed to get value from S_HEADER_DETAILS hashmap :: Object key [" + pStrSessionVarName + "]");
		}
		return str;
	}
}
