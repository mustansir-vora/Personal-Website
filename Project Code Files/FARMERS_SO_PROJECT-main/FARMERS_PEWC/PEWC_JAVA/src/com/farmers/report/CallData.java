package com.farmers.report;

import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.audium.server.logger.events.EndEvent;


public class CallData implements Serializable{

	private static final long serialVersionUID = 923456789;

	private transient Logger logger = null;
	private transient PrintWriter pw = null;

	/*Static final CONSTANTS - Log4J log levels*/
	private static final int DEBUG = 1; 
	private static final int INFO = 2; 
	private static final int WARN = 3; 
	private static final int ERROR = 4; 
	private static final int FATAL = 5;
	private static final String YES = "Y";

	private static final String NONE = "N";

	private ArrayList arrMenu = new ArrayList();
	private ArrayList arrAnnouncement = new ArrayList();
	private ArrayList arrHostBean = new ArrayList();

	private String strSessionID = "";
	private String originalDNIS= "";
	private String finalDNIS= "";
	private String originalDNISGroup= "";
	private String finalDNISGroup= "";
	private String originalCategory= "";
	private String finalCategory= "";
	private String originalLOB= "";
	private String finalLOB= "";
	private String originalANI= "";
	private String finalANI= "";
	private String callerAUTH= "";
	private String originalIntent= "";
	private String finalIntent= "";
	private String originalStateGroup= "";
	private String finalStateGroup= "";
	private String originalLang= "";
	private String finalLang= "";
	private String hoursOfOperation= "";
	private String transferCode= "";
	private String transferReason= "";
	private String transferNode= "";
	private String aniGroup= "";
	private String callOutcome= "";
	private String catCode= "";
	private String routeRule= "";
	private String routeAction= "";
	private String routeDestination= "";
	private String phoneType= "";
	private String testcaseID= "";
	private String duration= "";
	private String state= "";
	private String lastFunctionName= "";
	private String lastFunctionResult= "";
	private String claimNumber= "";
	private String policyNumber= "";
	private String brand= "";
	private String dispositionCode= "";
	private String errReason= "";
	private String ivrTraversalPath= "";
	private String noMatch= "";
	private String noInput= "";
	private String Agent= "";
	private String HoldOn= "";
	private String Repeat= "";
	private String strCallEndTime="";
	private String strCallStartTime="";
	private String strICMCallID="";
	private String strRCKey="";
	private String destination="";
	private String strErrReason="";
	private String strHEElements="";
	
	private String dummyHost = "<HOST_DETAIL><SEQUENCE_NUM>0</SEQUENCE_NUM><HOST_ID></HOST_ID><HOST_METHOD></HOST_METHOD><HOST_TYPE></HOST_TYPE>"
			+ "<HOST_STARTTIME></HOST_STARTTIME><HOST_IN_PARAMS></HOST_IN_PARAMS><HOST_OUT_PARAMS></HOST_OUT_PARAMS>"
			+ "<HOST_RESPONSE></HOST_RESPONSE><HOST_ERROR_CODE></HOST_ERROR_CODE><HOST_FAILURE_REASON></HOST_FAILURE_REASON>"
			+ "<HOST_ENDTIME></HOST_ENDTIME><HOST_COUNTER>0</HOST_COUNTER><HOST_RESERVE1></HOST_RESERVE1><HOST_RESERVE2></HOST_RESERVE2><HOST_RESERVE3></HOST_RESERVE3><HOST_RESERVE4></HOST_RESERVE4></HOST_DETAIL>";
		
	private String dummyAnno = "<ANNOUNCEMENTDETAIL><SEQUENCE_NUM>0</SEQUENCE_NUM><ANNOUNCEMENT_ID></ANNOUNCEMENT_ID><ANNOUNCEMENT_STARTTIME></ANNOUNCEMENT_STARTTIME><ANNOUNCEMENT_ENDTIME></ANNOUNCEMENT_ENDTIME><ANNOUNCEMENT_COUNTER>0</ANNOUNCEMENT_COUNTER><ANNOUNCEMENT_RESERVE1></ANNOUNCEMENT_RESERVE1><ANNOUNCEMENT_RESERVE2></ANNOUNCEMENT_RESERVE2><ANNOUNCEMENT_RESERVE3></ANNOUNCEMENT_RESERVE3><ANNOUNCEMENT_RESERVE4></ANNOUNCEMENT_RESERVE4></ANNOUNCEMENTDETAIL>";
	private String dummyMenu = "<MENU_DETAIL><SEQUENCE_NUM>0</SEQUENCE_NUM><MENU_ID></MENU_ID><MENU_STARTTIME></MENU_STARTTIME><MENU_ENDTIME></MENU_ENDTIME><MENU_OPTION></MENU_OPTION><MENU_COUNTER>0</MENU_COUNTER><MENU_RESERVE1></MENU_RESERVE1><MENU_RESERVE2></MENU_RESERVE2><MENU_RESERVE3></MENU_RESERVE3><MENU_RESERVE4></MENU_RESERVE4></MENU_DETAIL>";
	
	private ArrayList listHostDetails = new ArrayList();
	StringBuffer sbIVRPath = new StringBuffer(2000);
	/*
	 * JSONObject callData = new JSONObject(); JSONObject callInfo = new
	 * JSONObject(); JSONObject menuInfo = new JSONObject(); JSONObject annoInfo =
	 * new JSONObject(); JSONObject annoInfo = new JSONObject();
	 */
	
	public CallData(String pStrAppName, String pStrSessionID, Logger logger){
		this.logger = logger;
		strSessionID = pStrSessionID;
	}

	public void addMenu(Menu tmpMenu){
		arrMenu.add(tmpMenu);
	}
	
	protected void addAnnouncement(Announcement tmpAnnouncement){
		arrAnnouncement.add(tmpAnnouncement);
	}
	
	protected void addHost(HostBean tmpHostBean){
		arrHostBean.add(tmpHostBean);
	}


	@SuppressWarnings("rawtypes")
	protected String getXML(EndEvent pEvent){
		int intNoOfMenus = 0;
		int intNoOfAnnouncement = 0;
		int intNoOfHostDetails = 0;

		String strComboKey = strSessionID + "_" + strCallStartTime;

		StringBuffer sbTmp = new StringBuffer(6000); //declared to store around 6000 chars (an educated guess)
		StringBuffer sbAnnouncementsXML = new StringBuffer(2000);
		StringBuffer sbMenusXML = new StringBuffer(2000);
		StringBuffer sbHostXML = new StringBuffer(2000);
		StringBuffer sbMenuPath = new StringBuffer(150);

		//Announcement details formation
		try{
			intNoOfAnnouncement = arrAnnouncement.size();
			logger.info("No of Announcement:" + intNoOfAnnouncement);

		}catch(Exception ex){
			intNoOfMenus = 0;
		}
		
		for(int i=0;i<intNoOfAnnouncement;i++){
			String strAnnouncementTime		= 	"";
			String strAnnouncementId			= 	"";
			int sequenceNum = 0;
			
			Announcement objAnnouncement = (Announcement)arrAnnouncement.get(i);

			try{
				sequenceNum = objAnnouncement.getSequenceNum();	
			}catch(Exception e){
				sequenceNum = 0;
			}
			
			try{
				strAnnouncementId = objAnnouncement.getStrAnnouncementId();
			}catch(Exception e){
				strAnnouncementId = "";
			}
			logger.info("strMenuId:" + strAnnouncementId);
			ivrPathFormation(strAnnouncementId);
			
			try{
				strAnnouncementTime = objAnnouncement.getCalAnnouncementDateTime();
			}catch(Exception e){
				strAnnouncementTime = "";
			}
			
			
			
			if(!strAnnouncementId.equals("")){
				sbAnnouncementsXML.append("<ANNOUNCEMENTDETAIL><SEQUENCE_NUM>");
				sbAnnouncementsXML.append(sequenceNum);
				sbAnnouncementsXML.append("</SEQUENCE_NUM><ANNOUNCEMENT_ID>");
				sbAnnouncementsXML.append(strAnnouncementId);
				sbAnnouncementsXML.append("</ANNOUNCEMENT_ID><ANNOUNCEMENT_STARTTIME>");
				sbAnnouncementsXML.append(strAnnouncementTime);
				sbAnnouncementsXML.append("</ANNOUNCEMENT_STARTTIME><ANNOUNCEMENT_ENDTIME>");
				sbAnnouncementsXML.append(strAnnouncementTime);
				sbAnnouncementsXML.append("</ANNOUNCEMENT_ENDTIME><ANNOUNCEMENT_COUNTER>");
				sbAnnouncementsXML.append(""+(i+1));
				sbAnnouncementsXML.append("</ANNOUNCEMENT_COUNTER>");
				sbAnnouncementsXML.append("<ANNOUNCEMENT_RESERVE1></ANNOUNCEMENT_RESERVE1><ANNOUNCEMENT_RESERVE2></ANNOUNCEMENT_RESERVE2><ANNOUNCEMENT_RESERVE3></ANNOUNCEMENT_RESERVE3><ANNOUNCEMENT_RESERVE4></ANNOUNCEMENT_RESERVE4>");
				sbAnnouncementsXML.append("</ANNOUNCEMENTDETAIL>");

			}	
		}
		if(intNoOfAnnouncement>=1) {
			sbAnnouncementsXML.append(dummyAnno);
		}
		
		logger.info("sbAnnouncementsXML : "+sbAnnouncementsXML.toString());
		//Menu details formation
		try{
			intNoOfMenus = arrMenu.size();
			logger.info("No of Menus:" + intNoOfMenus);

		}catch(Exception ex){
			intNoOfMenus = 0;
		}
		
		for(int i=0;i<intNoOfMenus;i++){
			String strMenuStartTime		= 	"";
			String strMenuEndTime		= 	"";
			String strMenuId 			= 	"";
			String strMenuOption 		= 	"";
			int sequenceNum = 0;
			
			Menu objMenu = (Menu)arrMenu.get(i);

			try{
				sequenceNum = objMenu.getSequenceNum();	
			}catch(Exception e){
				sequenceNum = 0;
			}
			
			try{
				strMenuId = objMenu.getStrMenuId();
				if(strMenuId.contains("_Call")) {
					strMenuId = (Pattern.compile("\\_Call").split(strMenuId))[0];
				}
			}catch(Exception e){
				strMenuId = "";
			}

			logger.info("strMenuId:" + strMenuId);
			ivrPathFormation(strMenuId);

			try{
				strMenuStartTime = objMenu.getCalMenuStartTime();
			}catch(Exception e){
				strMenuStartTime = "";
			}
			try{
				strMenuEndTime = objMenu.getCalMenuEndTime();
			}catch(Exception e){
				strMenuEndTime = "";
			}

			try{
				strMenuOption = objMenu.getStrMenuOption();
				if(strMenuOption==null) {
					strMenuOption="NA";
				}
			}catch(Exception e){
				strMenuOption = "";
			}

			if(!strMenuId.equals("") && !strMenuStartTime.equals("") ){
				if(i!=0){
					sbMenuPath.append('|');
				}
				sbMenuPath.append(strMenuId);
				
				sbMenusXML.append("<MENU_DETAIL><SEQUENCE_NUM>");
				sbMenusXML.append(sequenceNum);
				sbMenusXML.append("</SEQUENCE_NUM><MENU_ID>");
				sbMenusXML.append(strMenuId);
				sbMenusXML.append("</MENU_ID><MENU_STARTTIME>");
				sbMenusXML.append(strMenuStartTime);
				sbMenusXML.append("</MENU_STARTTIME><MENU_ENDTIME>");
				sbMenusXML.append(strMenuEndTime);
				sbMenusXML.append("</MENU_ENDTIME><MENU_OPTION>");
				sbMenusXML.append(strMenuOption);
				sbMenusXML.append("</MENU_OPTION><MENU_COUNTER>");
				sbMenusXML.append(""+(i+1));
				sbMenusXML.append("</MENU_COUNTER>");
				sbMenusXML.append("<MENU_RESERVE1></MENU_RESERVE1><MENU_RESERVE2></MENU_RESERVE2><MENU_RESERVE3></MENU_RESERVE3><MENU_RESERVE4></MENU_RESERVE4>");
				sbMenusXML.append("</MENU_DETAIL>");


			}	
			
		}
		if(intNoOfMenus>=1) {
			sbMenusXML.append(dummyMenu);
		}

		//Host details formation
		try {
			logger.info("*** HOST INTERACTIONS DETAILS PREPARATION STARTED ***" );
			@SuppressWarnings("unchecked")
			ArrayList<HostBean> listHostInteractions = (ArrayList<HostBean>) getListHostDetails();

			try{
				intNoOfHostDetails = listHostInteractions.size();
				logger.info("No.of Host Interactions: " +intNoOfHostDetails);
			}catch(Exception e){
				intNoOfHostDetails = 0;
			}
			logger.info("HOST detail for loop starting : "+listHostInteractions);
			for(int i=0; i<intNoOfHostDetails; i++)
			{
				logger.info("Host obj : "+listHostInteractions.get(i));
				logger.info("Host obj : "+listHostInteractions.get(i));
				
				HostBean objHostInteractions =  listHostInteractions.get(i);
				logger.info("HOST detail objHostInteractions : "+objHostInteractions );
				String strHostType = "";
				int sequenceNum = 0;
				String strHostId = "";
				String strHostMethod = "";
				String strHostInParams = "";
				String strHostOutParams = "";
				String strHostResponse = "";
				String strHostStartTime = "";
				String strHostEndTime = "";
				String strHostErrorCode="";
				String strHostErrorDesc="";
					
				try{
					sequenceNum = objHostInteractions.getSequenceNum();	
				}catch(Exception e){
					sequenceNum = 0;
				}
				
				try{
					strHostId = objHostInteractions.getHostID();		
				}catch(Exception e){
					strHostId = "";
				}
				ivrPathFormation(strHostId);
				try{
					strHostMethod = objHostInteractions.getHostMethod();		
				}catch(Exception e){
					strHostMethod = "";
				}
				try{
					strHostType = objHostInteractions.getHostType();		
				}catch(Exception e){
					strHostType = "";
				}
				try{
					strHostInParams = objHostInteractions.getHostInParams();	
				}catch(Exception e){
					strHostInParams = "";
				}
				try{
					strHostOutParams = objHostInteractions.getHostOutParams();		
				}catch(Exception e){
					strHostOutParams = "";
				}
				try{
					strHostResponse = objHostInteractions.getHostResponse();	
				}catch(Exception e){
					strHostResponse = "";
				}
				try{
					strHostStartTime = objHostInteractions.getHostAccStartTime();	
				}catch(Exception e){
					strHostStartTime = "";
				}
				try{
					strHostEndTime = objHostInteractions.getHostAccEndTime();
				}catch(Exception e){
					strHostEndTime = "";
				}
				try{
					strHostErrorCode = objHostInteractions.getHostErrCode();	
				}catch(Exception e){
					strHostErrorCode = "";
				}
				try{
					strHostErrorDesc = objHostInteractions.getHostErrDesc();
				}catch(Exception e){
					strHostErrorDesc = "";
				}
				
				logger.info("HOST detail formation start**********8" );
				sbHostXML.append("<HOST_DETAIL><SEQUENCE_NUM>");
				sbHostXML.append(sequenceNum);
				sbHostXML.append("</SEQUENCE_NUM><HOST_ID>");
				sbHostXML.append(strHostId);
				sbHostXML.append("</HOST_ID><HOST_METHOD>");
				sbHostXML.append(strHostMethod);
				sbHostXML.append("</HOST_METHOD><HOST_TYPE>");
				sbHostXML.append(strHostType);
				sbHostXML.append("</HOST_TYPE><HOST_STARTTIME>");
				sbHostXML.append(strHostStartTime);
				sbHostXML.append("</HOST_STARTTIME><HOST_IN_PARAMS>");
				sbHostXML.append("<![CDATA["+strHostInParams+"]]>");
				sbHostXML.append("</HOST_IN_PARAMS><HOST_OUT_PARAMS>");
				sbHostXML.append("<![CDATA["+strHostOutParams+"]]>");
				sbHostXML.append("</HOST_OUT_PARAMS><HOST_RESPONSE>");
				sbHostXML.append(strHostResponse);
				sbHostXML.append("</HOST_RESPONSE><HOST_ERROR_CODE>");
				sbHostXML.append(strHostErrorCode);
				sbHostXML.append("</HOST_ERROR_CODE><HOST_FAILURE_REASON>");
				sbHostXML.append(strHostErrorDesc);
				sbHostXML.append("</HOST_FAILURE_REASON><HOST_ENDTIME>");
				sbHostXML.append(strHostEndTime);
				sbHostXML.append("</HOST_ENDTIME><HOST_COUNTER>");
				sbHostXML.append(" "+(i+1));
				//sbHostXML.append(strHostTrackId);
				sbHostXML.append("</HOST_COUNTER><HOST_RESERVE1></HOST_RESERVE1><HOST_RESERVE2></HOST_RESERVE2><HOST_RESERVE3></HOST_RESERVE3><HOST_RESERVE4></HOST_RESERVE4></HOST_DETAIL>");
			}
			
			if(intNoOfHostDetails>=1) {
				sbHostXML.append(dummyHost);
			}
					
			logger.info("*** HOST INTERACTIONS DETAILS PREPARATION FINISHED ***" );
		} catch (Exception e) {
			logger.error("Exception while preparing HOST INTERACTIONS: " +e.getMessage());
			writeStackTrace(strSessionID, e);
		}
		


		sbTmp.append("<IVRREPORTDATA>");
		sbTmp.append("<CALLINFO>");
		/** TENANT_ID **/
		sbTmp.append("<TENANT_ID>");
		sbTmp.append(1);
		sbTmp.append("</TENANT_ID>");
		
		/** Call ID**/
		sbTmp.append("<CALL_ID>");
		sbTmp.append(strICMCallID);
		sbTmp.append("</CALL_ID>");

		/** SESSION ID**/
		sbTmp.append("<SESSION_ID>");
		sbTmp.append(strSessionID);
		sbTmp.append("</SESSION_ID>");
		
		/** RCK_RCKD **/
		sbTmp.append("<RCK_RCKD>");
		sbTmp.append(strRCKey);
		sbTmp.append("</RCK_RCKD>");
		
		sbTmp.append("<AGENT>");
		sbTmp.append(Agent);
		sbTmp.append("</AGENT>");
		sbTmp.append("<HOLD_ON>");
		sbTmp.append(HoldOn);
		sbTmp.append("</HOLD_ON>");
		sbTmp.append("<REPEAT>");
		sbTmp.append(Repeat);
		sbTmp.append("</REPEAT>");
		
		sbTmp.append("<LAST_FUNCTION_NAME>");
		sbTmp.append(lastFunctionName);
		sbTmp.append("</LAST_FUNCTION_NAME>");
		sbTmp.append("<LAST_FUNCTION_RESULT>");
		sbTmp.append(lastFunctionResult);
		sbTmp.append("</LAST_FUNCTION_RESULT>");
		sbTmp.append("<CLAIM_NUMBER>");
		sbTmp.append(claimNumber);
		sbTmp.append("</CLAIM_NUMBER>");
		sbTmp.append("<POLICY_NUMBER>");
		sbTmp.append(policyNumber);
		sbTmp.append("</POLICY_NUMBER>");
		sbTmp.append("<BRAND>");
		sbTmp.append(brand);
		sbTmp.append("</BRAND>");
		sbTmp.append("<DISPOSITION_CODE>");
		sbTmp.append(dispositionCode);
		sbTmp.append("</DISPOSITION_CODE>");
		sbTmp.append("<NO_MATCH>");
		sbTmp.append(noMatch);
		sbTmp.append("</NO_MATCH>");
		sbTmp.append("<NO_INPUT>");
		sbTmp.append(noInput);
		sbTmp.append("</NO_INPUT>");
		
		sbTmp.append("<TRANSFER_REASON>");
		sbTmp.append(transferReason);
		sbTmp.append("</TRANSFER_REASON>");
		sbTmp.append("<TRANSFER_NODE>");
		sbTmp.append(transferNode);
		sbTmp.append("</TRANSFER_NODE>");
		sbTmp.append("<ANI_GROUP>");
		sbTmp.append(aniGroup);
		sbTmp.append("</ANI_GROUP>");
		sbTmp.append("<CALL_OUTCOME>");
		sbTmp.append(callOutcome);
		sbTmp.append("</CALL_OUTCOME>");
		sbTmp.append("<CAT_CODE>");
		sbTmp.append(catCode);
		sbTmp.append("</CAT_CODE>");
		sbTmp.append("<ROUTE_RULE>");
		sbTmp.append(routeRule);
		sbTmp.append("</ROUTE_RULE>");
		sbTmp.append("<ROUTE_ACTION>");
		sbTmp.append(routeAction);
		sbTmp.append("</ROUTE_ACTION>");
		sbTmp.append("<ROUTE_DESTINATION>");
		sbTmp.append(routeDestination);
		sbTmp.append("</ROUTE_DESTINATION>");
		sbTmp.append("<PHONE_TYPE>");
		sbTmp.append(phoneType);
		sbTmp.append("</PHONE_TYPE>");
		sbTmp.append("<TESTCASE_ID>");
		sbTmp.append(testcaseID);
		sbTmp.append("</TESTCASE_ID>");
		sbTmp.append("<DURATION>");
		sbTmp.append(duration);
		sbTmp.append("</DURATION>");
		sbTmp.append("<DESTINATION>");
		sbTmp.append(destination);
		sbTmp.append("</DESTINATION>");
		sbTmp.append("<STATE>");
		sbTmp.append(state);
		sbTmp.append("</STATE>");
		sbTmp.append("<ORIGINAL_STATE_GROUP>");
		sbTmp.append(originalStateGroup);
		sbTmp.append("</ORIGINAL_STATE_GROUP>");
		sbTmp.append("<FINAL_STATE_GROUP>");
		sbTmp.append(finalStateGroup);
		sbTmp.append("</FINAL_STATE_GROUP>");
		sbTmp.append("<ORIGINAL_LANGUAGE>");
		sbTmp.append(originalLang);
		sbTmp.append("</ORIGINAL_LANGUAGE>");
		sbTmp.append("<FINAL_LANGUAGE>");
		sbTmp.append(finalLang);
		sbTmp.append("</FINAL_LANGUAGE>");
		sbTmp.append("<HOURS_OF_OPERATION>");
		sbTmp.append(hoursOfOperation);
		sbTmp.append("</HOURS_OF_OPERATION>");
		sbTmp.append("<TRANSFER_CODE>");
		sbTmp.append(transferCode);
		sbTmp.append("</TRANSFER_CODE>");

		sbTmp.append("<ORIGINAL_DNIS>");
		sbTmp.append(originalDNIS);
		sbTmp.append("</ORIGINAL_DNIS><FINAL_DNIS>");
		sbTmp.append(finalDNIS);
		sbTmp.append("</FINAL_DNIS><ORIGINAL_DNIS_GROUP>");
		sbTmp.append(originalDNISGroup);
		sbTmp.append("</ORIGINAL_DNIS_GROUP><START_TIME>");
		sbTmp.append(strCallStartTime);
		sbTmp.append("</START_TIME><END_TIME>");
		sbTmp.append(strCallEndTime);
		sbTmp.append("</END_TIME><FINAL_DNIS_GROUP>");
		sbTmp.append(finalDNISGroup);
		sbTmp.append("</FINAL_DNIS_GROUP><IVR_TRAVERSAL_PATH>");
		sbTmp.append(sbIVRPath);
		sbTmp.append("</IVR_TRAVERSAL_PATH><ORIGINAL_CATEGORY>");
		sbTmp.append(originalCategory);
		sbTmp.append("</ORIGINAL_CATEGORY><FINAL_CATEGORY>");
		sbTmp.append(finalCategory);
		sbTmp.append("</FINAL_CATEGORY><ORIGINAL_LOB>");
		sbTmp.append(originalLOB);
		sbTmp.append("</ORIGINAL_LOB><FINAL_LOB>");
		sbTmp.append(finalLOB);		
		sbTmp.append("</FINAL_LOB><ORIGINAL_ANI>");
		sbTmp.append(originalANI);
		sbTmp.append("</ORIGINAL_ANI><FINAL_ANI>");
		sbTmp.append(finalANI);
		sbTmp.append("</FINAL_ANI><CALLER_AUTH>");
		sbTmp.append(callerAUTH);
		sbTmp.append("</CALLER_AUTH><ORIGINAL_INTENT>");
		sbTmp.append(originalIntent);
		sbTmp.append("</ORIGINAL_INTENT><FINAL_INTENT>");
		sbTmp.append(finalIntent);
		sbTmp.append("</FINAL_INTENT><ERR_REASON>");

		sbTmp.append(strErrReason);
		if(!"".equals(strErrReason) && !"".equals(strHEElements)){
			sbTmp.append('|');
		}
		sbTmp.append(strHEElements);
		sbTmp.append("</ERR_REASON><RESERVE1></RESERVE1><RESERVE2></RESERVE2><RESERVE3></RESERVE3><RESERVE4></RESERVE4></CALLINFO>");
		logger.info("IVR element is done");
		sbTmp.append(sbAnnouncementsXML.toString());
		sbTmp.append(sbMenusXML.toString());
		logger.info("MENUS element is done");
		// Host details to be updated
		sbTmp.append(sbHostXML.toString());
		sbTmp.append("</IVRREPORTDATA>");
		logger.info("XML FROM CallData class : " + sbTmp.toString());			
		return sbTmp.toString();
	}



	private String getDate(String pStrFormat, Date pDate){
		DateFormat fmt = null;
		if("dd/MM/yyyy HH:mm:ss".equalsIgnoreCase(pStrFormat)){
			fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		}
		if("yyyyMMddHHmmssSSS".equalsIgnoreCase(pStrFormat)){
			fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		}

		return fmt.format(pDate);
	}

	protected void writeStackTrace(String pStrComboKey, Exception pException){
		try{
			if (pStrComboKey == null){
				pStrComboKey = "0000000000000"; //13 zeroes
			}else{
				if (pStrComboKey.equals("")){
					pStrComboKey = "0000000000000"; //13 zeroes
				}
			}
			pw.flush();
			pw.println("CALL ID: " + pStrComboKey);
			pException.printStackTrace(pw);
			pw.flush();
		}catch(Exception e){
			logger.error("Error while print the stack trace to the error file. Ex:" + e.getMessage());
		}
	}

	
	public void setICMCallID(String strICMCallID) {
		this.strICMCallID = strICMCallID;
	}


	public void setCallStartTime(String strCallStartTime) {
		this.strCallStartTime = strCallStartTime;
	}

	public void setCallEndTime(String strCallEndTime) {
		this.strCallEndTime = strCallEndTime;
	}  

	@SuppressWarnings("rawtypes")
	public void setListHostDetails(ArrayList listHostDetails) {
		this.listHostDetails = listHostDetails;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList getListHostDetails() {
		return listHostDetails;
	}

	public StringBuffer getSbIVRPath() {
		return sbIVRPath;
	}

	public void setSbIVRPath(StringBuffer sbIVRPath) {
		this.sbIVRPath = sbIVRPath;
	}
	
	public void ivrPathFormation(String strElementId) {
		if(sbIVRPath.length() == 0) sbIVRPath.append(strElementId);
		else sbIVRPath.append("|"+strElementId);
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getStrSessionID() {
		return strSessionID;
	}

	public void setStrSessionID(String strSessionID) {
		this.strSessionID = strSessionID;
	}

	public String getOriginalDNIS() {
		return originalDNIS;
	}

	public void setOriginalDNIS(String originalDNIS) {
		this.originalDNIS = originalDNIS;
	}

	public String getFinalDNIS() {
		return finalDNIS;
	}

	public void setFinalDNIS(String finalDNIS) {
		this.finalDNIS = finalDNIS;
	}

	public String getOriginalDNISGroup() {
		return originalDNISGroup;
	}

	public void setOriginalDNISGroup(String originalDNISGroup) {
		this.originalDNISGroup = originalDNISGroup;
	}

	public String getFinalDNISGroup() {
		return finalDNISGroup;
	}

	public void setFinalDNISGroup(String finalDNISGroup) {
		this.finalDNISGroup = finalDNISGroup;
	}

	public String getOriginalCategory() {
		return originalCategory;
	}

	public void setOriginalCategory(String originalCategory) {
		this.originalCategory = originalCategory;
	}

	public String getFinalCategory() {
		return finalCategory;
	}

	public void setFinalCategory(String finalCategory) {
		this.finalCategory = finalCategory;
	}

	public String getOriginalLOB() {
		return originalLOB;
	}

	public void setOriginalLOB(String originalLOB) {
		this.originalLOB = originalLOB;
	}

	public String getFinalLOB() {
		return finalLOB;
	}

	public void setFinalLOB(String finalLOB) {
		this.finalLOB = finalLOB;
	}

	public String getOriginalANI() {
		return originalANI;
	}

	public void setOriginalANI(String originalANI) {
		this.originalANI = originalANI;
	}

	public String getFinalANI() {
		return finalANI;
	}

	public void setFinalANI(String finalANI) {
		this.finalANI = finalANI;
	}

	public String getCallerAUTH() {
		return callerAUTH;
	}

	public void setCallerAUTH(String callerAUTH) {
		this.callerAUTH = callerAUTH;
	}

	public String getOriginalIntent() {
		return originalIntent;
	}

	public void setOriginalIntent(String originalIntent) {
		this.originalIntent = originalIntent;
	}

	public String getFinalIntent() {
		return finalIntent;
	}

	public void setFinalIntent(String finalIntent) {
		this.finalIntent = finalIntent;
	}

	public String getOriginalStateGroup() {
		return originalStateGroup;
	}

	public void setOriginalStateGroup(String originalStateGroup) {
		this.originalStateGroup = originalStateGroup;
	}

	public String getFinalStateGroup() {
		return finalStateGroup;
	}

	public void setFinalStateGroup(String finalStateGroup) {
		this.finalStateGroup = finalStateGroup;
	}

	public String getOriginalLang() {
		return originalLang;
	}

	public void setOriginalLang(String originalLang) {
		this.originalLang = originalLang;
	}

	public String getFinalLang() {
		return finalLang;
	}

	public void setFinalLang(String finalLang) {
		this.finalLang = finalLang;
	}

	public String getHoursOfOperation() {
		return hoursOfOperation;
	}

	public void setHoursOfOperation(String hoursOfOperation) {
		this.hoursOfOperation = hoursOfOperation;
	}

	public String getTransferCode() {
		return transferCode;
	}

	public void setTransferCode(String transferCode) {
		this.transferCode = transferCode;
	}

	public String getTransferReason() {
		return transferReason;
	}

	public void setTransferReason(String transferReason) {
		this.transferReason = transferReason;
	}

	public String getTransferNode() {
		return transferNode;
	}

	public void setTransferNode(String transferNode) {
		this.transferNode = transferNode;
	}

	public String getAniGroup() {
		return aniGroup;
	}

	public void setAniGroup(String aniGroup) {
		this.aniGroup = aniGroup;
	}

	public String getCallOutcome() {
		return callOutcome;
	}

	public void setCallOutcome(String callOutcome) {
		this.callOutcome = callOutcome;
	}

	public String getCatCode() {
		return catCode;
	}

	public void setCatCode(String catCode) {
		this.catCode = catCode;
	}

	public String getRouteRule() {
		return routeRule;
	}

	public void setRouteRule(String routeRule) {
		this.routeRule = routeRule;
	}

	public String getRouteAction() {
		return routeAction;
	}

	public void setRouteAction(String routeAction) {
		this.routeAction = routeAction;
	}

	public String getRouteDestination() {
		return routeDestination;
	}

	public void setRouteDestination(String routeDestination) {
		this.routeDestination = routeDestination;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getTestcaseID() {
		return testcaseID;
	}

	public void setTestcaseID(String testcaseID) {
		this.testcaseID = testcaseID;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLastFunctionName() {
		return lastFunctionName;
	}

	public void setLastFunctionName(String lastFunctionName) {
		this.lastFunctionName = lastFunctionName;
	}

	public String getLastFunctionResult() {
		return lastFunctionResult;
	}

	public void setLastFunctionResult(String lastFunctionResult) {
		this.lastFunctionResult = lastFunctionResult;
	}

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDispositionCode() {
		return dispositionCode;
	}

	public void setDispositionCode(String dispositionCode) {
		this.dispositionCode = dispositionCode;
	}

	public String getErrReason() {
		return errReason;
	}

	public void setErrReason(String errReason) {
		this.errReason = errReason;
	}

	public String getIvrTraversalPath() {
		return ivrTraversalPath;
	}

	public void setIvrTraversalPath(String ivrTraversalPath) {
		this.ivrTraversalPath = ivrTraversalPath;
	}

	public String getNoMatch() {
		return noMatch;
	}

	public void setNoMatch(String noMatch) {
		this.noMatch = noMatch;
	}

	public String getNoInput() {
		return noInput;
	}

	public void setNoInput(String noInput) {
		this.noInput = noInput;
	}

	public String getAgent() {
		return Agent;
	}

	public void setAgent(String agent) {
		Agent = agent;
	}

	public String getHoldOn() {
		return HoldOn;
	}

	public void setHoldOn(String holdOn) {
		HoldOn = holdOn;
	}

	public String getRepeat() {
		return Repeat;
	}

	public void setRepeat(String repeat) {
		Repeat = repeat;
	}

	public String getStrCallEndTime() {
		return strCallEndTime;
	}

	public void setStrCallEndTime(String strCallEndTime) {
		this.strCallEndTime = strCallEndTime;
	}

	public String getStrICMCallID() {
		return strICMCallID;
	}

	public void setStrICMCallID(String strICMCallID) {
		this.strICMCallID = strICMCallID;
	}

	public String getStrRCKey() {
		return strRCKey;
	}

	public void setStrRCKey(String strRCKey) {
		this.strRCKey = strRCKey;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getStrErrReason() {
		return strErrReason;
	}

	public void setStrErrReason(String strErrReason) {
		this.strErrReason = strErrReason;
	}

	public String getStrHEElements() {
		return strHEElements;
	}

	public void setStrHEElements(String strHEElements) {
		this.strHEElements = strHEElements;
	}

	
	
}
