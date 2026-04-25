package com.farmers.start;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.core.lookup.MapLookup;

import com.audium.server.AudiumException;
import com.audium.server.global.ApplicationStartAPI;
import com.audium.server.proxy.StartCallInterface;
import com.audium.server.session.CallStartAPI;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.SequenceNumber;

public class CallStart implements StartCallInterface{

	String strMediaURL=Constants.EmptyString;
	String strMediaAppPath=Constants.EmptyString;
	String strMediaSysPath =Constants.EmptyString;
	String strElementName = Constants.EmptyString;
	String strCVPCallID = Constants.EmptyString;
	String strICMCallID =Constants.EmptyString;
	String strSessionId = Constants.EmptyString;
	String strSkillgroup=Constants.EmptyString;

	String strBackToIVRLang=Constants.EmptyString;
	String strBackToIVRTransferCode=Constants.EmptyString;
	String strAgentNotAvailableFlag=Constants.EmptyString;

	String strDNIS = Constants.NA;
	String strANI = Constants.EmptyString;
	String strCallType=Constants.NA;
	String strVXMLIP =Constants.EmptyString;
	String strAPPID = Constants.EmptyString;
	String strCLI = Constants.NA;
	String strCallStartDate = Constants.EmptyString;
	String strICMCall=Constants.EmptyString;
	String strOANI = Constants.EmptyString;
	String strOCallId = Constants.EmptyString;
	String strCCallId = Constants.EmptyString;
	String strTTID = Constants.EmptyString;
	String strTIDV = Constants.EmptyString;
	String strTI = Constants.EmptyString;
	String strBE = Constants.EmptyString;
	String strCS = Constants.EmptyString;
	String strCGUID = Constants.EmptyString;
	String strAreaCode = Constants.EmptyString;
	String strICM_BU = Constants.EmptyString;
	
	//CS1348016 - All BU's - Onboarding Line Routing
	String stronBoardingEligibleDays=Constants.EmptyString;
	
	HashMap<String, String> headerDetails = new HashMap<String, String>();

	ApplicationStartAPI applicationStartAPI=null;
	CommonAPIAccess caa=null;

	public void onStartCall(CallStartAPI callStartAPI) throws AudiumException
	{

		strElementName = callStartAPI.getCurrentElement();
		caa=CommonAPIAccess.getInstance(callStartAPI);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		SimpleDateFormat datePopFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strStartTime = dateFormat.format(new Date());
		String strPopTime = datePopFormat.format(new Date()).replace(" ", "T");
		long lCallStart = System.currentTimeMillis();
		// loading application session variables into session data
		SessionDataBuilder sessionBuilder = new SessionDataBuilder();
		sessionBuilder.setToSessionData(callStartAPI,Constants.APP_LEVEL_IVRCONFIG_PROP_OBJECT);
		//		sessionBuilder.setToSessionData(callStartAPI,Constants.APP_LEVEL_MENUCONFIG_PROP_OBJECT);
		sessionBuilder.setToSessionDataAsIs(callStartAPI,Constants.APP_LEVEL_MENU_SELECTION_PROP_OBJECT);
		StringBuffer stringBuffer=null;

		callStartAPI.setSessionData(Constants.S_PREF_LANG, Constants.EN);
		callStartAPI.setSessionData(Constants.S_FINAL_LANG, Constants.English);
//		Caller Verification Change by Venkatesh K M -Start
		
		
		callStartAPI.setSessionData(Constants.S_CV_GDF_CONNECTOR_FLAG, Constants.NO);
		
		
		callStartAPI.setSessionData(Constants.S_KYC_AUTHENTICATED_CCAI, Constants.STRING_NO);
		
		
		callStartAPI.setSessionData(Constants.S_REP_HANDLING_COUNTER,"0");

//		Caller Verification Change by Venkatesh K M -End
		
		//Error Handling Venkatesh K M
		
		// Utterence Handling
		
		callStartAPI.setSessionData("S_CALLER_UTTERANCE","INITIAL");
		
		callStartAPI.setSessionData(Constants.S_TRANSFER_ENTER, Constants.NO);
        
		//CS1191070_IA Premier Agent changes
		callStartAPI.setSessionData("KYCAF_HOST_003_FLAG", "N");
        //CS1191070_IA Premier Agent changes
		
		//CS1271390: Model based automated Farmers Customer retention call routing (US861200/OIR#10920) - HLCF Design Draft
		callStartAPI.setSessionData("S_CALL_FROM_KYC", "N");
		
		//GDF Connectivity issue
		callStartAPI.setSessionData("S_HOTEVENT", "N");
		callStartAPI.setSessionData("S_HOTEVENT_TRANSFER", "N");
		callStartAPI.setSessionData("S_HOTEVENT_RETRY_COUNT", "1st");
		
		//CS1336023
		callStartAPI.setSessionData("S_CANCEL_POLICY_INTENT", "N");
		
		//CS1349586 ,Farmers | US | Specialty IVR Flow
				callStartAPI.setSessionData("CA_CALLER", Constants.N_FLAG);
		
		try
		{
			stringBuffer=new StringBuffer();
			strICMCallID =(String)callStartAPI.getSessionData(Constants.ICM_CALLID);
		}
		catch(Exception e){
			stringBuffer.append("Exception occurred while retrieving ICM call id. Exception: "+e.getMessage());
			strICMCallID = Constants.NA;
		}
		caa.setToSession(Constants.S_ICMID,strICMCallID);



		strSessionId = callStartAPI.getSessionId();

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
			stringBuffer.append("Exception occurred while retrieving CCall id. Exception: "+e.getMessage());
			strCCallId = Constants.NA;
		}

		//Get VXML IP Address
		try 
		{
			InetAddress thisIp =InetAddress.getLocalHost();
			strVXMLIP = thisIp.getHostAddress();
		}catch (UnknownHostException e) {

			stringBuffer.append("Exception occurred while retrieving VXML IP Address. Exception: " + e.getMessage());
			strVXMLIP = Constants.NA;
		}

		try
		{
			strCLI = callStartAPI.getSessionData(Constants.ICM_ANI).toString();
			caa.printToLog(new StringBuffer("strANI:").append(strCLI),CommonAPIAccess.INFO);
		}
		catch(Exception e){
			stringBuffer.append("Exception occurred while retrieving CLI. Exception: ").append(e.getMessage());
			strCLI = Constants.NA;	
		}


		try
		{
			strDNIS = callStartAPI.getSessionData(Constants.ICM_DNIS).toString();
			caa.printToLog(new StringBuffer("strDNIS:").append(strDNIS),CommonAPIAccess.INFO);
			if(strDNIS==null)
			{
				stringBuffer.append("DNIS value not able to received from ICM. ");
				strDNIS = Constants.NA;
			}
		}
		catch(Exception e){
			stringBuffer.append("Exception occurred while retrieving DNIS. Exception: ").append(e.getMessage());
			strDNIS = Constants.NA;	
		}

		if(strCLI.length()>10) {
			strCLI=strCLI.substring(strCLI.length()-10, strCLI.length());
		}

		try
		{
			strAreaCode = strCLI.substring(0, 3);
			caa.printToLog(new StringBuffer("strAreaCode:").append(strAreaCode),CommonAPIAccess.INFO);
		}
		catch(Exception e){
			stringBuffer.append("Exception occurred while retrieving strAreaCode. Exception: ").append(e.getMessage());
			strAreaCode = Constants.NA;	
		}

		try
		{
			strOANI = (String)callStartAPI.getSessionData(Constants.ICM_OANI);
			strOCallId = (String)callStartAPI.getSessionData(Constants.ICM_OCALLID);
			strTTID = (String)callStartAPI.getSessionData(Constants.ICM_TTID);
			strTIDV = (String)callStartAPI.getSessionData(Constants.ICM_TIDV);
			strTI = (String)callStartAPI.getSessionData(Constants.ICM_TI);
			strBE = (String)callStartAPI.getSessionData(Constants.ICM_BE);
			strCS = (String)callStartAPI.getSessionData(Constants.ICM_CS);
			strCGUID = (String)callStartAPI.getSessionData(Constants.ICM_CGUID);
			strICM_BU = (String)callStartAPI.getSessionData(Constants.ICM_BU);
			String strSFID =(String)callStartAPI.getSessionData("SFID");

			if(strOANI==null || "".equalsIgnoreCase(strOANI)) {
				strOANI=strCLI;
			}

			caa.printToLog("strOANI :"+strOANI);
			caa.printToLog("strOCallId :"+strOCallId);
			caa.printToLog("strTTID :"+strTTID);
			caa.printToLog("strTIDV :"+strTIDV);
			caa.printToLog("strTI :"+strTI);
			caa.printToLog("strBE :"+strBE);
			caa.printToLog("strCS :"+strCS);
			caa.printToLog("strICM_BU :"+strICM_BU);
			caa.printToLog("strCGUID :"+strCGUID);
			caa.printToLog("strSFID :"+strSFID);

		}
		catch(Exception e){
			stringBuffer.append("Exception occurred while retrieving DNIS. Exception: ").append(e.getMessage());
		}



		caa.setToSession(Constants.S_ANI, strCLI);
		SequenceNumber objSequenceNumber = new SequenceNumber();
		caa.setToSession(Constants.S_SEQUENCE_COUNTER, objSequenceNumber);
		//caa.setToSession(Constants.S_ANI, "6092852994");
		caa.setToSession(Constants.S_ORIGINAL_ANI, strOANI);
		caa.setToSession(Constants.S_FINAL_ANI, strCLI);
		caa.setToSession(Constants.S_DNIS, strDNIS);
		caa.setToSession(Constants.S_ORIGINAL_DNIS, strDNIS);
		caa.setToSession(Constants.S_FINAL_DNIS, strDNIS);
		caa.setToSession(Constants.S_CALLID, strICMCallID);
		caa.setToSession(Constants.S_VXML_SERVER_IP, strVXMLIP);
		caa.setToSession(Constants.S_AGENT_TRANSFER, Constants.N_FLAG);
		caa.setToSession(Constants.S_CALLSTART_TIME, "" + lCallStart);
		caa.setToSession(Constants.S_SESSION_ID, strSessionId);
		caa.setToSession(Constants.S_CALLSTART_DATE_TIME,strStartTime);
		caa.setToSession(Constants.S_CALLED_INTIME,strPopTime);
		caa.setToSession(Constants.S_IVRPATH,Constants.EmptyString);
		caa.setToSession(Constants.S_PREF_LANG,Constants.EN);
		caa.setToSession(Constants.S_OANI,strOANI);
		caa.setToSession(Constants.S_OCALLID,strOCallId);
		caa.setToSession(Constants.S_CCALLID,strCCallId);
		caa.setToSession(Constants.S_TTID,strTTID);
		caa.setToSession(Constants.S_TIDV,strTIDV);
		caa.setToSession(Constants.S_TI,strTI);
		caa.setToSession(Constants.S_BE,strBE);
		caa.setToSession(Constants.S_CS,strCS);
		caa.setToSession(Constants.S_CGUID,strCGUID);
		caa.setToSession(Constants.S_SPEECH_MN_NM_TRUE_FLAG,"N");
		caa.setToSession(Constants.S_AREACODE,strAreaCode);
		caa.setToSession(Constants.S_BU_ID,strICM_BU);
		caa.setToSession(Constants.AppTagTraversal,Constants.NA);
		caa.setToSession(Constants.S_KYC_AUTHENTICATED,"NO");
		caa.setToSession(Constants.S_CALLCENTER_OPEN_CLOSED,"NA");
		caa.setToSession(Constants.S_DR,"N");
		caa.setToSession(Constants.S_ESPL_AGENT_MATCH_TRIES,"0");
		caa.setToSession("IS_FROM_FAS_CALINIT_NLU", "N");
		caa.setToSession("S_FLAG_BW_BU", "NO");
		caa.setToSession("S_FLAG_FDS_BU", "NO");
		caa.setToSession("S_AGENT_OPTED_NO", "N");

		caa.setToSession("REP_TRIES", 0);
		caa.setToSession(Constants.FNWL_CALLER_TYPE, "03");
		caa.setToSession("FNWL_AUTH", "N");

		/**
		 * Shared Id Auth - LOB Configuration
		 * 
		 */


		String strBristolCode = (String)callStartAPI.getApplicationAPI().getApplicationData("A_BRISTOL_LOB");
		String strFarmersCode = (String)callStartAPI.getApplicationAPI().getApplicationData("A_FARMERS_LOB");
		String strForemostCode = (String)callStartAPI.getApplicationAPI().getApplicationData("A_FOREMOST_LOB");
		String strFWSCode = (String)callStartAPI.getApplicationAPI().getApplicationData("A_FWS_LOB");
		String str21stCode = (String)callStartAPI.getApplicationAPI().getApplicationData("A_21ST_LOB");
		String strEmpSrv = (String)callStartAPI.getApplicationAPI().getApplicationData("A_EMP_SERV_LOB");
		String strForemostAARP = (String)callStartAPI.getApplicationAPI().getApplicationData("A_FOREMOST_AARP_CATEGORY");
		String strForemostAgent = (String)callStartAPI.getApplicationAPI().getApplicationData("A_FOREMOST_AGENT_CATEGORY");
		String strForemostCust = (String)callStartAPI.getApplicationAPI().getApplicationData("A_FOREMOST_CUST_CATEGORY");
		String strForemostUSAA = (String)callStartAPI.getApplicationAPI().getApplicationData("A_FOREMOST_USAA_CATEGORY");
		String strF21stHawaii = (String)callStartAPI.getApplicationAPI().getApplicationData("A_21ST_HAWAII_CATEGORY");
		String strFISC = (String)callStartAPI.getApplicationAPI().getApplicationData("A_FISC_CATEGORY");
		String strSpecialty = (String)callStartAPI.getApplicationAPI().getApplicationData("A_SPECIALTY_CATEGORY");
		String strIDCardLOB = (String)callStartAPI.getApplicationAPI().getApplicationData("A_IDCARD_LOB_LIST");
		String strBPLOB = (String)callStartAPI.getApplicationAPI().getApplicationData("A_B&P_LOB_LIST");
		String strMAPLOB = (String)callStartAPI.getApplicationAPI().getApplicationData("A_MAP_LOB_LIST");
		String strAgentInfLOB = (String)callStartAPI.getApplicationAPI().getApplicationData("A_AGENTINF_LOB_LIST");
		String strMailAddLOB = (String)callStartAPI.getApplicationAPI().getApplicationData("A_MAILADD_LOB_LIST");
		String strCovFAQLOB = (String)callStartAPI.getApplicationAPI().getApplicationData("A_COVFAQ_LOB_LIST");
		String strEmpSerLineLOB = (String)callStartAPI.getApplicationAPI().getApplicationData("A_EMPSERLINE_LOB_LIST");
		String strFNWLLOB = (String)callStartAPI.getApplicationAPI().getApplicationData("A_FNWL_LOB_LIST");

		callStartAPI.addToLog("CallStart", "Bristol Code from config :: " + strBristolCode + " :: Farmers COde :: " + strFarmersCode + " :: strForemostCode :: " 
				+ strForemostCode + " :: strFWSCode :: " + strFWSCode + " :: str21stCode :: " + str21stCode + " :: strEmpSrv :: "+strEmpSrv + " :: strForemostAARP :: " 
				+ strForemostAARP + " :: strForemostAgent :: " + strForemostAgent + " :: strForemostCust :: " + strForemostCust + " :: strForemostUSAA :: " 
				+ strForemostUSAA + " :: strF21stHawaii :: " + strF21stHawaii + " :: strFISC :: " + strFISC + " :: strSpecialty :: " + strSpecialty);

		String[] arrBristolCode = null;
		if(null != strBristolCode && strBristolCode.contains(",")) {
			arrBristolCode = strBristolCode.split(",");
		}else {
			arrBristolCode = new String [1];
			arrBristolCode[0] = strBristolCode;
		}

		String[] arrFarmersCode = null;
		if(null != strFarmersCode && strFarmersCode.contains(",")) {
			arrFarmersCode = strFarmersCode.split(",");
		}else {
			arrFarmersCode = new String [1];
			arrFarmersCode[0] = strFarmersCode;
		}

		String[] arrForemostCode = null;
		if(null != strForemostCode && strForemostCode.contains(",")) {
			arrForemostCode = strForemostCode.split(",");
		}else {
			arrForemostCode = new String [1];
			arrForemostCode[0] = strForemostCode;
		}

		String[] arrFWSCode = null;
		if(null != strFWSCode && strFWSCode.contains(",")) {
			arrFWSCode = strFWSCode.split(",");
		}else {
			arrFWSCode = new String [1];
			arrFWSCode[0] = strFWSCode;
		}

		String[] arr21stCode = null;
		if(null != str21stCode && str21stCode.contains(",")) {
			arr21stCode = str21stCode.split(",");
		}else {
			arr21stCode = new String [1];
			arr21stCode[0] = str21stCode;
		}

		String[] arrEMPCode = null;
		if(null != strEmpSrv && strEmpSrv.contains(",")) {
			arrEMPCode = strEmpSrv.split(",");
		}else {
			arrEMPCode = new String [1];
			arrEMPCode[0] = strEmpSrv;
		}

		String[] arrFMSTAARP = null;
		if(null != strForemostAARP && strForemostAARP.contains(",")) {
			arrFMSTAARP = strForemostAARP.split(",");
		}else {
			arrFMSTAARP = new String [1];
			arrFMSTAARP[0] = strForemostAARP;
		}

		String[] arrFMSTAgent = null;
		if(null != strForemostAgent && strForemostAgent.contains(",")) {
			arrFMSTAgent = strForemostAgent.split(",");
		}else {
			arrFMSTAgent = new String [1];
			arrFMSTAgent[0] = strForemostAgent;
		}

		String[] arrFMSTCust = null;
		if(null != strForemostCust && strForemostCust.contains(",")) {
			arrFMSTCust = strForemostCust.split(",");
		}else {
			arrFMSTCust = new String [1];
			arrFMSTCust[0] = strForemostCust;
		}

		String[] arrFMSTUSAA = null;
		if(null != strForemostUSAA && strForemostUSAA.contains(",")) {
			arrFMSTUSAA = strForemostUSAA.split(",");
		}else {
			arrFMSTUSAA = new String [1];
			arrFMSTUSAA[0] = strForemostUSAA;
		}

		String[] arr21stHwaii = null;
		if(null != strF21stHawaii && strF21stHawaii.contains(",")) {
			arr21stHwaii = strF21stHawaii.split(",");
		}else {
			arr21stHwaii = new String [1];
			arr21stHwaii[0] = strF21stHawaii;
		}

		String[] arrFISC = null;
		if(null != strFISC && strFISC.contains(",")) {
			arrFISC = strFISC.split(",");
		}else {
			arrFISC = new String [1];
			arrFISC[0] = strFISC;
		}

		String[] arrSpecialty = null;
		if(null != strSpecialty && strSpecialty.contains(",")) {
			arrSpecialty = strSpecialty.split(",");
		}else {
			arrSpecialty = new String [1];
			arrSpecialty[0] = strSpecialty;
		}
		
		String[] arrIDCardLOB = null;
		if(null != strIDCardLOB && strIDCardLOB.contains(",")) {
			arrIDCardLOB = strIDCardLOB.split(",");
		}else {
			arrIDCardLOB = new String [1];
			arrIDCardLOB[0] = strIDCardLOB;
		}
		
		String[] arrBPLOB = null;
		if(null != strBPLOB && strBPLOB.contains(",")) {
			arrBPLOB = strBPLOB.split(",");
		}else {
			arrBPLOB = new String [1];
			arrBPLOB[0] = strBPLOB;
		}
		
		String[] arrMAPLOB = null;
		if(null != strMAPLOB && strMAPLOB.contains(",")) {
			arrMAPLOB = strMAPLOB.split(",");
		}else {
			arrMAPLOB = new String [1];
			arrMAPLOB[0] = strMAPLOB;
		}
		
		String[] arrAgentInfLOB = null;
		if(null != strAgentInfLOB && strAgentInfLOB.contains(",")) {
			arrAgentInfLOB = strAgentInfLOB.split(",");
		}else {
			arrAgentInfLOB = new String [1];
			arrAgentInfLOB[0] = strAgentInfLOB;
		}
		
		String[] arrMailAddLOB = null;
		if(null != strMailAddLOB && strMailAddLOB.contains(",")) {
			arrMailAddLOB = strMailAddLOB.split(",");
		}else {
			arrMailAddLOB = new String [1];
			arrMailAddLOB[0] = strMailAddLOB;
		}
		
		String[] arrCovFAQLOB = null;
		if(null != strCovFAQLOB && strCovFAQLOB.contains(",")) {
			arrCovFAQLOB = strCovFAQLOB.split(",");
		}else {
			arrCovFAQLOB = new String [1];
			arrCovFAQLOB[0] = strCovFAQLOB;
		}
		
		String[] arrEmpSerLineLOB = null;
		if(null != strCovFAQLOB && strEmpSerLineLOB.contains(",")) {
			arrEmpSerLineLOB = strEmpSerLineLOB.split(",");
		}else {
			arrEmpSerLineLOB = new String [1];
			arrEmpSerLineLOB[0] = strEmpSerLineLOB;
		}
		
		String[] arrFNWLLOB = null;
		if(null != strFNWLLOB && strFNWLLOB.contains(",")) {
			arrFNWLLOB = strFNWLLOB.split(",");
		}else {
			arrFNWLLOB = new String [1];
			arrFNWLLOB[0] = strFNWLLOB;
		}
		
		ArrayList<String> foremost_Lob = null,bristolLob = null, farmerLob = null, fwsLob = null, list21stLob = null, empLob=null, fmstAARP=null, fmstAgent=null, fmstCust=null, fmstUSAA=null, list21stHawaii=null, fisc=null, specialty=null, IDcardLOB = null, BPLOB = null, MAPLOB = null, AgentInfLOB = null, MailAddLOB = null, CovFAQLOB = null, EmpSerLineLOB = null, FNWLLOB = null;

		foremost_Lob = new ArrayList<String>();
		for (String lobVal : arrForemostCode) {
			foremost_Lob.add(lobVal);
		}

		bristolLob = new ArrayList<String>();
		for (String lobVal : arrBristolCode) {
			bristolLob.add(lobVal);
		}

		farmerLob = new ArrayList<String>();
		for (String lobVal : arrFarmersCode) {
			farmerLob.add(lobVal);
		}

		fwsLob = new ArrayList<String>();
		for (String lobVal : arrFWSCode) {
			fwsLob.add(lobVal);
		}

		list21stLob = new ArrayList<String>();
		for (String lobVal : arr21stCode) {
			list21stLob.add(lobVal);
		}

		empLob = new ArrayList<String>();
		for (String lobVal : arrEMPCode) {
			empLob.add(lobVal);
		}

		fmstAARP = new ArrayList<String>();
		for (String lobVal : arrFMSTAARP) {
			fmstAARP.add(lobVal);
		}

		fmstAgent = new ArrayList<String>();
		for (String lobVal : arrFMSTAgent) {
			fmstAgent.add(lobVal);
		}

		fmstCust = new ArrayList<String>();
		for (String lobVal : arrFMSTCust) {
			fmstCust.add(lobVal);
		}

		fmstUSAA = new ArrayList<String>();
		for (String lobVal : arrFMSTUSAA) {
			fmstUSAA.add(lobVal);
		}

		list21stHawaii = new ArrayList<String>();
		for (String lobVal : arr21stHwaii) {
			list21stHawaii.add(lobVal);
		}

		fisc = new ArrayList<String>();
		for (String lobVal : arrFISC) {
			fisc.add(lobVal);
		}

		specialty = new ArrayList<String>();
		for (String lobVal : arrSpecialty) {
			specialty.add(lobVal);
		}
		
		IDcardLOB = new ArrayList<String>();
		for(String lobVal : arrIDCardLOB) {
			IDcardLOB.add(lobVal);
		}
		
		BPLOB = new ArrayList<String>();
		for(String lobVal : arrBPLOB) {
			BPLOB.add(lobVal);
		}
		
		MAPLOB = new ArrayList<String>();
		for(String lobVal : arrMAPLOB) {
			MAPLOB.add(lobVal);
		}
		
		AgentInfLOB = new ArrayList<>();
		for (String lobVal : arrAgentInfLOB) {
			AgentInfLOB.add(lobVal);
		}
		
		MailAddLOB = new ArrayList<>();
		for (String lobVal : arrMailAddLOB) {
			MailAddLOB.add(lobVal);
		}
		
		CovFAQLOB = new ArrayList<>();
		for (String lobVal : arrCovFAQLOB) {
			CovFAQLOB.add(lobVal);
		}
		
		EmpSerLineLOB = new ArrayList<>();
		for (String lobVal : arrEmpSerLineLOB) {
			EmpSerLineLOB.add(lobVal);
		}
		
		FNWLLOB = new ArrayList<>();
		for (String lobVal : arrFNWLLOB) {
			FNWLLOB.add(lobVal);
		}

		callStartAPI.addToLog("CallStart", "List :: Foremost :: "+ foremost_Lob + " :: Bristol LOB :: " + bristolLob + " :: Farmers LOB :: " + farmerLob + 
				" :: fws LOB :: " + fwsLob + " :: 21st LOB :: " + list21stLob + " :: empLob :: " + empLob + " :: fmstAARP :: " + fmstAARP + " :: fmstAgent :: " + fmstAgent + 
				" :: fmstCust :: " + fmstCust + " :: fmstUSAA :: " + fmstUSAA + " :: list21stHawaii :: " + list21stHawaii + " :: fisc :: " + fisc + " :: specialty :: " + specialty);

		caa.setToSession("FOREMOST_LOB_LIST", foremost_Lob);
		caa.setToSession("BRISTOL_LOB_LIST", bristolLob);
		caa.setToSession("FARMER_LOB_LIST", farmerLob);
		caa.setToSession("FWS_LOB_LIST", fwsLob);
		caa.setToSession("21st_LOB_LIST", list21stLob);
		caa.setToSession("EMP_SERV_LIST", empLob);
		caa.setToSession("FMST_AARP_LOB_LIST", fmstAARP);
		caa.setToSession("FMST_AGENT_LOB_LIST", fmstAgent);
		caa.setToSession("FMST_CUST_LOB_LIST", fmstCust);
		caa.setToSession("FMST_USAA_LOB_LIST", fmstUSAA);
		caa.setToSession("21st_HAWAII_LOB_LIST", list21stHawaii);
		caa.setToSession("FISC_LIST", fisc);
		caa.setToSession("SPECIALTY_LIST", specialty);
		caa.setToSession("IS_CLARIFIER_ALREADY_INVOKED", "N");
		caa.setToSession("IDCARD_LOB_LIST", IDcardLOB);
		caa.setToSession("B&P_LOB_LIST", BPLOB);
		caa.setToSession("MAP_LOB_LIST", MAPLOB);
		caa.setToSession("AGENTINF_LOB_LIST", AgentInfLOB);
		caa.setToSession("MAILADD_LOB_LIST", MailAddLOB);
		caa.setToSession("COVFAQ_LOB_LIST", CovFAQLOB);
		caa.setToSession("EMPSERLINE_LOB_LIST", EmpSerLineLOB);
		caa.setToSession("FNWL_LOB_LIST", FNWLLOB);
		callStartAPI.setSessionData(Constants.S_CALLLER_TYPE,"01");
		

		//After hour rules for HOOP
		String strHoopBristolCode = (String)callStartAPI.getApplicationAPI().getApplicationData("A_HOOP_BRISTOL_LOB");
		String strHoopFarmersCode = (String)callStartAPI.getApplicationAPI().getApplicationData("A_HOOP_FARMERS_LOB");
		String strHoopFWSCode = (String)callStartAPI.getApplicationAPI().getApplicationData("A_HOOP_FWS_LOB");
		String strHoop21stCode = (String)callStartAPI.getApplicationAPI().getApplicationData("A_HOOP_21ST_LOB");

		callStartAPI.addToLog("CallStart", "strHoopBristolCode from config :: " + strHoopBristolCode + " :: strHoopFarmersCode :: " + strHoopFarmersCode + " :: strHoopFWSCode :: " + strFWSCode + " :: str21stCode :: " + str21stCode + " :: strEmpSrv :: "+strEmpSrv + " :: strForemostAARP :: " 
				+ strHoopFWSCode + " :: strHoop21stCode :: " + strHoop21stCode);

		String[] arrHoopBristolCode = null;
		if(null != strHoopBristolCode && strHoopBristolCode.contains(",")) {
			arrHoopBristolCode = strHoopBristolCode.split(",");
		}else {
			arrHoopBristolCode = new String [1];
			arrHoopBristolCode[0] = strHoopBristolCode;
		}

		String[] arrHoopFarmersCode = null;
		if(null != strHoopFarmersCode && strHoopFarmersCode.contains(",")) {
			arrHoopFarmersCode = strHoopFarmersCode.split(",");
		}else {
			arrHoopFarmersCode = new String [1];
			arrHoopFarmersCode[0] = strHoopFarmersCode;
		}

		String[] arrHoopFWSCode = null;
		if(null != strHoopFWSCode && strHoopFWSCode.contains(",")) {
			arrHoopFWSCode = strHoopFWSCode.split(",");
		}else {
			arrHoopFWSCode = new String [1];
			arrHoopFWSCode[0] = strHoopFWSCode;
		}

		String[] arrHoop21stCode = null;
		if(null != strHoop21stCode && strHoop21stCode.contains(",")) {
			arrHoop21stCode = strHoop21stCode.split(",");
		}else {
			arrHoop21stCode = new String [1];
			arrHoop21stCode[0] = strHoop21stCode;
		}

		ArrayList<String> listFarmersHoopLob = null,listBristolHoopLob = null, listFwsHoopLob = null, listHoop21stLob = null;

		listBristolHoopLob = new ArrayList<String>();
		for (String lobVal : arrHoopBristolCode) {
			listBristolHoopLob.add(lobVal);
		}

		listFarmersHoopLob = new ArrayList<String>();
		for (String lobVal : arrHoopFarmersCode) {
			listFarmersHoopLob.add(lobVal);
		}

		listFwsHoopLob = new ArrayList<String>();
		for (String lobVal : arrHoopFWSCode) {
			listFwsHoopLob.add(lobVal);
		}

		listHoop21stLob = new ArrayList<String>();
		for (String lobVal : arrHoop21stCode) {
			listHoop21stLob.add(lobVal);
		}

		callStartAPI.addToLog("CallStart", "HOOP List ::  Bristol HOOP LOB :: " + listBristolHoopLob + " :: Farmers HOOP LOB :: " + listFarmersHoopLob + 
				" :: fws HOOP LOB :: " + listFwsHoopLob + " :: 21st HOOP LOB :: " + listHoop21stLob);

		caa.setToSession("S_HOOP_BRISTOL_LOB_LIST", listBristolHoopLob);
		caa.setToSession("S_HOOP_FARMER_LOB_LIST", listFarmersHoopLob);
		caa.setToSession("S_HOOP_FWS_LOB_LIST", listFwsHoopLob);
		caa.setToSession("S_HOOP_21st_LOB_LIST", listHoop21stLob);

		//Set audio path with default lang
		caa.setDefaultAudioPath(((String)callStartAPI.getSessionData(Constants.S_PREF_LANG)),strVXMLIP);		

		//Write to Log for Call Start Info 
		stringBuffer.append( "Call Start Time: ").append(strStartTime).append(" | LCallStart: ").append(lCallStart).append(" | DNIS: ").append(strDNIS).append(" | CLI: ").append(strCLI)
		.append(" | ICM CallId: ").append(strICMCallID).append(" | VXML IP: ").append(strVXMLIP).append(" | SessionId ID: ").append(strSessionId).append(" | Lang ID: ").append(Constants.EN).toString();
		caa.printToLog(stringBuffer);
		
		callStartAPI.setSessionData("S_HEADER_DETAILS", headerDetails);
		//caa.setToSession("S_HEADER_DETAILS", headerDetails);
		
		//MDM FWS Phone number lookup enable/disable flag, default set "N"
		caa.getFromSession(Constants.S_MDM_PHONENO_LOOKUP_FLAG, callStartAPI.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_FLAG) != null
				? String.valueOf(callStartAPI.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_FLAG)) :"N");
		
		//CS1240953 - ID and Auth Simplify VIA Enable Default Flag
		callStartAPI.setSessionData(Constants.S_NEW_VIA_FLAG, Constants.FALSE);
		//CS1240953 - ID and Auth Simplify Caller auth  default Flag
		callStartAPI.setSessionData("S_CALLER_AUTH", "Auth_Not_Required");
		//NEW VIA MDM Phone No Collection default flag
		callStartAPI.setSessionData("S_PHONE_NUMBER_RESULT", "N");
		// NEW VIA MDM Result Flag for After Hours Treatment
		callStartAPI.setSessionData("S_NEW_MDM_RESULT","FALSE");
		
		//CS1348016 - All BU's - Onboarding Line Routing
		String stronBoardingEligibleDays=(String) caa.getFromSession(Constants.S_ONBOARDING_ELIGIBLE_DAYS, callStartAPI.getSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS) != null
				? String.valueOf(callStartAPI.getSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS)) :"60");
		callStartAPI.setSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS, stronBoardingEligibleDays);
		callStartAPI.addToLog("The OnBoarding Eligible Days configured in Config File::", stronBoardingEligibleDays);
		callStartAPI.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
		callStartAPI.setSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG, Constants.N_FLAG);
		
	}

	public static void main(String[] args) {
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			System.out.println("System IP Address : " +
					(localhost.getHostAddress()).trim());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception occurred while retrieving IP address : "+e.getMessage());
		}
	}

}
