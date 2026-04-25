package com.farmers.host;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.Afni_Post;
import com.farmers.FarmersAPI.Ciscosrmcti_Post;
import com.farmers.FarmersAPI.FWSsrmcti_Post;
import com.farmers.FarmersAPI_NP.Afni_NP_Post;
import com.farmers.FarmersAPI_NP.Ciscosrmcti_NP_Post;
import com.farmers.FarmersAPI_NP.FWSsrmcti_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class AFNI_API extends DecisionElementBase {

	
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			
			if(checkFWSBU(data)) {
				FWSCRM(currElementName, data);
			}else {
				ciscoSRMAPI(currElementName, data);
			}
			
			
		if(data.getSessionData(Constants.S_AFNI_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_AFNI_URL);
			String callid=((String)data.getSessionData(Constants.S_CGUID)!=null)?((String)data.getSessionData(Constants.S_CGUID)) : Constants.EmptyString;
			String callerani = ((String) data.getSessionData(Constants.S_ANI)!=null)?((String)data.getSessionData(Constants.S_ANI)) : Constants.EmptyString;
			//String policynumber = ((String) data.getSessionData(Constants.S_POLICY_NUM)!=null)?((String)data.getSessionData(Constants.S_POLICY_NUM)) : Constants.EmptyString;
			String ivrcallid = ((String) data.getSessionData(Constants.S_CGUID)!=null)?((String)data.getSessionData(Constants.S_CGUID)) : Constants.EmptyString;
			String accountnumber = ((String) data.getSessionData(Constants.S_ACCOUNT_NUMBER) != null) ? ((String) data.getSessionData(Constants.S_ACCOUNT_NUMBER)) : Constants.EmptyString;
			String originaldnis = ((String) data.getSessionData(Constants.S_ORIGINAL_DNIS) != null) ? ((String) data.getSessionData(Constants.S_ORIGINAL_DNIS)) : Constants.EmptyString;
			String originaldnisdescription = ((String) data.getSessionData(Constants.S_ORIGINAL_DNIS_DESCRIPTION) != null) ? ((String) data.getSessionData(Constants.S_ORIGINAL_DNIS_DESCRIPTION)) : Constants.EmptyString;
			String zipcode = ((String) data.getSessionData(Constants.S_ZIP_CODE) != null) ? ((String) data.getSessionData(Constants.S_ZIP_CODE)) : Constants.EmptyString;
			String quotenumber = ((String) data.getSessionData(Constants.S_QUOTE_NUMBER) != null) ? ((String) data.getSessionData(Constants.S_QUOTE_NUMBER)) : Constants.EmptyString;
			String state = ((String) data.getSessionData(Constants.S_STATE) != null) ? ((String) data.getSessionData(Constants.S_STATE)) : Constants.EmptyString;
			String policynumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
			String FWSLob = (String) data.getSessionData("S_FWS_POLICY_LOB");
			String FWSPolicyType = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
			String FWS_BU_FLAG = (String) data.getSessionData("S_FLAG_FWS_BU");
			if (null != FWSPolicyType && null != FWS_BU_FLAG && FWS_BU_FLAG.equalsIgnoreCase(Constants.STRING_YES) && FWSPolicyType.equalsIgnoreCase("Met360")) {
				boolean startsWithAlphabets = Character.isLetter(policynumber.charAt(0));
				if (!startsWithAlphabets) {
					policynumber = FWSLob+policynumber;
				}
			}
			int conntimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			Afni_Post obj= new Afni_Post();
			//Non prod changes-Priya
			data.addToLog("API URL: ", url);
			String prefix = "https://api-np-ss.farmersinsurance.com"; 
			String UAT_FLAG="";
	        if(url.startsWith(prefix)) {
	        	UAT_FLAG="YES";
	        }
			Afni_NP_Post objNP = new Afni_NP_Post();
			JSONObject resp=null ;
			if("YES".equalsIgnoreCase(UAT_FLAG)) {
				String Key =Constants.S_AFNI_URL;
				 region = regionDetails.get(Key);
				data.addToLog("Region for UAT endpoint: ", region);
				resp = objNP.start(url, callid, callerani, policynumber, accountnumber, originaldnis, originaldnisdescription, zipcode, quotenumber, state,  conntimeout, readTimeout,context, region);
			}else {
				region="PROD";
			    resp = obj.start(url, callid, callerani, policynumber, accountnumber, originaldnis, originaldnisdescription, zipcode, quotenumber, state,  conntimeout, readTimeout,context);
			}
			//Non prod changes-Priya
			strRespBody = resp.toString();
			data.addToLog(currElementName, "AfniAPI response  :"+resp);
			
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				strRespBody = resp.toString();
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					StrExitState = Constants.SU;
				}
			}
			
			
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in AfniAPI call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"AfniAPI", strReqBody, region,(String) data.getSessionData(Constants.S_AFNI_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for AfniAPI call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}
	
	public boolean checkFWSBU (DecisionElementData data) {
		boolean fwsBS = false;
		String currElementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			
		String strBU = (String) data.getSessionData(Constants.S_BU);
		

		ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
		data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
		data.addToLog(currElementName, " S_BU : "+strBU);
		

		if(strFWSCode!=null && strBU!=null && strFWSCode.contains(strBU)) {
			fwsBS = true;
		} else {
			fwsBS = false;
		}
		data.addToLog(currElementName, " fwsBS : "+fwsBS);
		
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception while forming host details for SIDA_HOST_001  :: "+e);
		caa.printStackTrace(e);
	}
		return fwsBS;
	}
	
	
	public String FWSCRM(String currElementName, DecisionElementData data) {

		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
		if(data.getSessionData(Constants.S_FWS_SRMCTI_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_FWS_SRMCTI_URL);
			String callid=((String)data.getSessionData(Constants.S_CGUID)!=null)?((String)data.getSessionData(Constants.S_CGUID)) : Constants.EmptyString;
			String callerani = ((String) data.getSessionData(Constants.S_ANI)!=null)?((String)data.getSessionData(Constants.S_ANI)) : Constants.EmptyString;
			String destinationapp = ((String) data.getSessionData(Constants.S_DESTINATION_APP)!=null)?((String)data.getSessionData(Constants.S_DESTINATION_APP)) : "SRM";
			String ivrcallid = ((String) data.getSessionData(Constants.S_CGUID)!=null)?((String)data.getSessionData(Constants.S_CGUID)) : Constants.EmptyString;
			String poptype = ((String) data.getSessionData(Constants.S_POPUP_TYPE)!=null)?((String)data.getSessionData(Constants.S_POPUP_TYPE)) : "01";
			String calledinto = ((String) data.getSessionData(Constants.S_DNIS)!=null)?((String)data.getSessionData(Constants.S_DNIS)) : Constants.EmptyString;
			String calledintime = ((String) data.getSessionData(Constants.S_CALLED_INTIME)!=null)?((String)data.getSessionData(Constants.S_CALLED_INTIME)) : Constants.EmptyString;
			String tollfreedescription = ((String) data.getSessionData(Constants.S_TOLLFREE_DESCRIPTION)!=null)?((String)data.getSessionData(Constants.S_TOLLFREE_DESCRIPTION)) : Constants.EmptyString;
			
			String language = ((String) data.getSessionData(Constants.S_PREF_LANG)!=null)?((String)data.getSessionData(Constants.S_PREF_LANG)) : Constants.EmptyString;
			String an1 = ((String) data.getSessionData(Constants.S_AN1)!=null)?((String)data.getSessionData(Constants.S_AN1)) : Constants.EmptyString;
			String upn1 = ((String) data.getSessionData(Constants.S_UPN1)!=null)?((String)data.getSessionData(Constants.S_UPN1)) : Constants.EmptyString;
			String transferreason = ((String) data.getSessionData(Constants.S_TRANFER_REASON)!=null)?((String)data.getSessionData(Constants.S_TRANFER_REASON)) : Constants.EmptyString;
			String intent = ((String) data.getSessionData(Constants.S_INTENT)!=null)?((String)data.getSessionData(Constants.S_INTENT)) : Constants.EmptyString;
			
			String brand = "FWS";
			boolean validpolicynumber = false;
			if ( null != data.getSessionData("IS_CALLED_SHARED_ID_AUTH") && data.getSessionData("IS_CALLED_SHARED_ID_AUTH").toString().equalsIgnoreCase("TRUE")) {
				validpolicynumber = true;
			}
			else {
				validpolicynumber = false;
			}
			String callertype = ((String) data.getSessionData(Constants.S_CALLLER_TYPE)!=null)?((String)data.getSessionData(Constants.S_CALLLER_TYPE)) : "01";
			String tollfreenumber = ((String) data.getSessionData(Constants.S_TOLLFREE_NUM)!=null)?((String)data.getSessionData(Constants.S_TOLLFREE_NUM)) : Constants.EmptyString;
			String callerinput = ((String) data.getSessionData(Constants.S_POLICY_NUM)!=null)?((String)data.getSessionData(Constants.S_POLICY_NUM)) : Constants.EmptyString;
			String FWSLob = (String) data.getSessionData("S_FWS_POLICY_LOB");
			String FWSPolicyType = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
			String FWS_BU_FLAG = (String) data.getSessionData("S_FLAG_FWS_BU");
			if (null != FWSPolicyType && null != FWS_BU_FLAG && FWS_BU_FLAG.equalsIgnoreCase(Constants.STRING_YES) && FWSPolicyType.equalsIgnoreCase("Met360")) {
				boolean startsWithAlphabets = Character.isLetter(callerinput.charAt(0));
				if (!startsWithAlphabets) {
					callerinput = FWSLob+callerinput;
				}
			}
			String callerrisklevel = ((String) data.getSessionData(Constants.S_TI_SCORE)!=null)?((String)data.getSessionData(Constants.S_TI_SCORE)) : Constants.EmptyString;
			
			if(Constants.HIGH.equalsIgnoreCase(callerrisklevel)) {
				callerrisklevel = Constants.LOW;
			}else {
				callerrisklevel = Constants.MEDIUM;
			}
			
			if(Constants.EN.equalsIgnoreCase(language)) {
				language = "en-us";
			}else {
				language = "es-us";
			}
			
//Caller Verification//
			
			String CCAI_Agent_FirstName =(String) data.getSessionData(Constants.CCAI_Agent_FirstName) != null ? (String) data.getSessionData(Constants.CCAI_Agent_FirstName) : "NA";
			String CCAI_Agent_LastName =(String) data.getSessionData(Constants.CCAI_Agent_LastName)!= null ? (String) data.getSessionData(Constants.CCAI_Agent_LastName) : "NA";
			String CCAI_Agent_State =(String) data.getSessionData(Constants.CCAI_Agent_State) != null ? (String) data.getSessionData(Constants.CCAI_Agent_State) : "NA";
			String CCAI_Agent_Code_Producer_Code =(String) data.getSessionData(Constants.CCAI_Agent_Code_Producer_Code) != null ? (String) data.getSessionData(Constants.CCAI_Agent_Code_Producer_Code) : "NA";
			String CCAI_Agency_Name =(String) data.getSessionData(Constants.CCAI_Agency_Name) != null ? (String) data.getSessionData(Constants.CCAI_Agency_Name) : "NA";
			String CCAI_Agent_Verified =(String) data.getSessionData(Constants.CCAI_Agent_Verified) != null ? (String) data.getSessionData(Constants.CCAI_Agent_Verified) : "NA";
			String CCAI_Agent_Policy_Number =(String) data.getSessionData(Constants.CCAI_Agent_Policy_Number) != null ? (String) data.getSessionData(Constants.CCAI_Agent_Policy_Number) : "NA";
			String CCAI_Customer_FirstName =(String) data.getSessionData(Constants.CCAI_Customer_FirstName) != null ? (String) data.getSessionData(Constants.CCAI_Customer_FirstName) : "NA";
			String CCAI_Customer_LastName =(String) data.getSessionData(Constants.CCAI_Customer_LastName) != null ? (String) data.getSessionData(Constants.CCAI_Customer_LastName) : "NA";;
			String CCAI_Customer_SSN_Last_4 =(String) data.getSessionData(Constants.CCAI_Customer_SSN_Last_4) != null ? (String) data.getSessionData(Constants.CCAI_Customer_SSN_Last_4) : "NA";
			String CCAI_Customer_DOB =(String) data.getSessionData(Constants.CCAI_Customer_DOB) != null ? (String) data.getSessionData(Constants.CCAI_Customer_DOB) : "NA";
			String CCAI_Customer_DLNumber =(String) data.getSessionData(Constants.CCAI_Customer_DLNumber) != null ? (String) data.getSessionData(Constants.CCAI_Customer_DLNumber) : "NA";
			String CCAI_Customer_Address_Street_1 =(String) data.getSessionData(Constants.CCAI_Customer_Address_Street_1) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_Street_1) : "NA";
			String CCAI_Customer_Address_Street_2 =(String) data.getSessionData(Constants.CCAI_Customer_Address_Street_2) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_Street_2) : "NA";
			String CCAI_Customer_Address_City =(String) data.getSessionData(Constants.CCAI_Customer_Address_City) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_City) : "NA";
			String CCAI_Customer_Address_State =(String) data.getSessionData(Constants.CCAI_Customer_Address_State) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_State) : "NA";
			String CCAI_Customer_Address_ZipCode =(String) data.getSessionData(Constants.CCAI_Customer_Address_ZipCode) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_ZipCode) : "NA";
			String CCAI_Customer_Address_Country =(String) data.getSessionData(Constants.CCAI_Customer_Address_Country) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_Country) : "NA";
			String CCAI_Customer_Address_Type =(String) data.getSessionData(Constants.CCAI_Customer_Address_Type) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_Type) : "NA";
			String CCAI_Customer_Policy_Number =(String) data.getSessionData(Constants.CCAI_Customer_Policy_Number) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Policy_Number) : "NA";
			String CCAI_Customer_BillingAccount_Number =(String) data.getSessionData(Constants.CCAI_Customer_BillingAccount_Number) != null ? (String) data.getSessionData(Constants.CCAI_Customer_BillingAccount_Number) : "NA";
			String CCAI_Customer_Verified =(String) data.getSessionData(Constants.CCAI_Customer_Verified) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Verified) : "NA";
			String CCAI_Customer_ECN =(String) data.getSessionData(Constants.CCAI_Customer_ECN) != null ? (String) data.getSessionData(Constants.CCAI_Customer_ECN) : "NA";
			
			//Caller Verification//
			
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readtimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			FWSsrmcti_Post obj = new FWSsrmcti_Post();
			//JSONObject resp =  (JSONObject) obj.start(url, ivrcallid,  callerani, destinationapp, tollfreenumber, callerinput, poptype, calledinto, calledintime, tollfreedescription, 
					//callertype, language, an1, upn1, transferreason, intent , brand , validpolicynumber, callerrisklevel, conTimeout, readtimeout, context);
                FWSsrmcti_NP_Post objNP = new FWSsrmcti_NP_Post();
			
              //Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
			JSONObject resp=null ;
			if("YES".equalsIgnoreCase(UAT_FLAG)) {
				data.addToLog("S_FWS_SRMCTI_URL: ", url);
				String Key =Constants.S_FWS_SRMCTI_URL;
				region = regionDetails.get(Key);
				data.addToLog("Region for UAT endpoint: ", region);
				resp = (JSONObject) objNP.start(url, ivrcallid,  callerani, destinationapp, tollfreenumber, callerinput, poptype, calledinto, calledintime, tollfreedescription, 
						callertype, language, an1, upn1, transferreason, intent , brand , validpolicynumber, callerrisklevel, conTimeout, readtimeout, context,
						CCAI_Agent_FirstName,
						CCAI_Agent_LastName,
						CCAI_Agent_State,
						CCAI_Agent_Code_Producer_Code,
						CCAI_Agency_Name,
						CCAI_Agent_Verified,
						CCAI_Agent_Policy_Number,
						CCAI_Customer_FirstName,
						CCAI_Customer_LastName,
						CCAI_Customer_SSN_Last_4,
						CCAI_Customer_DOB,
						CCAI_Customer_DLNumber,
						CCAI_Customer_Address_Street_1,
						CCAI_Customer_Address_Street_2,
						CCAI_Customer_Address_City,
						CCAI_Customer_Address_State,
						CCAI_Customer_Address_ZipCode,
						CCAI_Customer_Address_Country,
						CCAI_Customer_Address_Type,
						CCAI_Customer_Policy_Number,
						CCAI_Customer_BillingAccount_Number,
						CCAI_Customer_Verified,
						CCAI_Customer_ECN,region);
			}else {
				region="PROD";
			    resp = (JSONObject) obj.start(url, ivrcallid,  callerani, destinationapp, tollfreenumber, callerinput, poptype, calledinto, calledintime, tollfreedescription, 
						callertype, language, an1, upn1, transferreason, intent , brand , validpolicynumber, callerrisklevel, conTimeout, readtimeout, context,
						CCAI_Agent_FirstName,
						CCAI_Agent_LastName,
						CCAI_Agent_State,
						CCAI_Agent_Code_Producer_Code,
						CCAI_Agency_Name,
						CCAI_Agent_Verified,
						CCAI_Agent_Policy_Number,
						CCAI_Customer_FirstName,
						CCAI_Customer_LastName,
						CCAI_Customer_SSN_Last_4,
						CCAI_Customer_DOB,
						CCAI_Customer_DLNumber,
						CCAI_Customer_Address_Street_1,
						CCAI_Customer_Address_Street_2,
						CCAI_Customer_Address_City,
						CCAI_Customer_Address_State,
						CCAI_Customer_Address_ZipCode,
						CCAI_Customer_Address_Country,
						CCAI_Customer_Address_Type,
						CCAI_Customer_Policy_Number,
						CCAI_Customer_BillingAccount_Number,
						CCAI_Customer_Verified,
						CCAI_Customer_ECN);
			}
			
			strRespBody = resp.toString();
			data.addToLog(currElementName, "FWS_SRMCTI_API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				strRespBody = resp.toString();
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					StrExitState = Constants.SU;
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in FWS_SRMCTI_API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"FWS_SRMCTI_API", strReqBody,region, (String) data.getSessionData(Constants.S_FWS_SRMCTI_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for FWS_SRMCTI_API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	
	}
	
	public String ciscoSRMAPI(String currElementName, DecisionElementData data)  {

		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
		if(data.getSessionData(Constants.S_CISCO_SRMCTI_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null
				&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_CISCO_SRMCTI_URL);
			String ivrcallid = ((String) data.getSessionData(Constants.S_CGUID)!=null)?((String)data.getSessionData(Constants.S_CGUID)) : Constants.EmptyString;
			String callid=((String)data.getSessionData(Constants.S_CGUID)!=null)?((String)data.getSessionData(Constants.S_CGUID)) : Constants.EmptyString;
			if(url.contains(Constants.S_CALLID)) url = url.replace(Constants.S_CALLID, ivrcallid);
			String callerani = ((String) data.getSessionData(Constants.S_ANI)!=null)?((String)data.getSessionData(Constants.S_ANI)) : Constants.EmptyString;
			String destinationapp = ((String) data.getSessionData(Constants.S_DESTINATION_APP)!=null)?((String)data.getSessionData(Constants.S_DESTINATION_APP)) : "SRM";
			String tollfreenumber = ((String) data.getSessionData(Constants.S_TOLLFREE_NUM)!=null)?((String)data.getSessionData(Constants.S_TOLLFREE_NUM)) : Constants.EmptyString;
			String callerinput = ((String) data.getSessionData(Constants.S_POLICY_NUM)!=null)?((String)data.getSessionData(Constants.S_POLICY_NUM)) : Constants.EmptyString;
			String poptype = ((String) data.getSessionData(Constants.S_POPUP_TYPE)!=null)?((String)data.getSessionData(Constants.S_POPUP_TYPE)) : "01";
			String calledinto = ((String) data.getSessionData(Constants.S_DNIS)!=null)?((String)data.getSessionData(Constants.S_DNIS)) : Constants.EmptyString;
			String calledintime = ((String) data.getSessionData(Constants.S_CALLED_INTIME)!=null)?((String)data.getSessionData(Constants.S_CALLED_INTIME)) : Constants.EmptyString;
			String tollfreedescription = ((String) data.getSessionData(Constants.S_TOLLFREE_DESCRIPTION)!=null)?((String)data.getSessionData(Constants.S_TOLLFREE_DESCRIPTION)) : Constants.EmptyString;
			
			String language = ((String) data.getSessionData(Constants.S_PREF_LANG)!=null)?((String)data.getSessionData(Constants.S_PREF_LANG)) : Constants.EmptyString;
			String an1 = ((String) data.getSessionData(Constants.S_AN1)!=null)?((String)data.getSessionData(Constants.S_AN1)) : Constants.EmptyString;
			String upn1 = ((String) data.getSessionData(Constants.S_UPN1)!=null)?((String)data.getSessionData(Constants.S_UPN1)) : Constants.EmptyString;
			String transferreason = ((String) data.getSessionData(Constants.S_TRANFER_REASON)!=null)?((String)data.getSessionData(Constants.S_TRANFER_REASON)) : Constants.EmptyString;
			String intent = ((String) data.getSessionData(Constants.S_INTENT)!=null)?((String)data.getSessionData(Constants.S_INTENT)) : Constants.EmptyString;
//Caller Verification//
			
			String CCAI_Agent_FirstName =(String) data.getSessionData(Constants.CCAI_Agent_FirstName) != null ? (String) data.getSessionData(Constants.CCAI_Agent_FirstName) : "NA";
			String CCAI_Agent_LastName =(String) data.getSessionData(Constants.CCAI_Agent_LastName)!= null ? (String) data.getSessionData(Constants.CCAI_Agent_LastName) : "NA";
			String CCAI_Agent_State =(String) data.getSessionData(Constants.CCAI_Agent_State) != null ? (String) data.getSessionData(Constants.CCAI_Agent_State) : "NA";
			String CCAI_Agent_Code_Producer_Code =(String) data.getSessionData(Constants.CCAI_Agent_Code_Producer_Code) != null ? (String) data.getSessionData(Constants.CCAI_Agent_Code_Producer_Code) : "NA";
			String CCAI_Agency_Name =(String) data.getSessionData(Constants.CCAI_Agency_Name) != null ? (String) data.getSessionData(Constants.CCAI_Agency_Name) : "NA";
			String CCAI_Agent_Verified =(String) data.getSessionData(Constants.CCAI_Agent_Verified) != null ? (String) data.getSessionData(Constants.CCAI_Agent_Verified) : "NA";
			String CCAI_Agent_Policy_Number =(String) data.getSessionData(Constants.CCAI_Agent_Policy_Number) != null ? (String) data.getSessionData(Constants.CCAI_Agent_Policy_Number) : "NA";
			String CCAI_Customer_FirstName =(String) data.getSessionData(Constants.CCAI_Customer_FirstName) != null ? (String) data.getSessionData(Constants.CCAI_Customer_FirstName) : "NA";
			String CCAI_Customer_LastName =(String) data.getSessionData(Constants.CCAI_Customer_LastName) != null ? (String) data.getSessionData(Constants.CCAI_Customer_LastName) : "NA";;
			String CCAI_Customer_SSN_Last_4 =(String) data.getSessionData(Constants.CCAI_Customer_SSN_Last_4) != null ? (String) data.getSessionData(Constants.CCAI_Customer_SSN_Last_4) : "NA";
			String CCAI_Customer_DOB =(String) data.getSessionData(Constants.CCAI_Customer_DOB) != null ? (String) data.getSessionData(Constants.CCAI_Customer_DOB) : "NA";
			String CCAI_Customer_DLNumber =(String) data.getSessionData(Constants.CCAI_Customer_DLNumber) != null ? (String) data.getSessionData(Constants.CCAI_Customer_DLNumber) : "NA";
			String CCAI_Customer_Address_Street_1 =(String) data.getSessionData(Constants.CCAI_Customer_Address_Street_1) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_Street_1) : "NA";
			String CCAI_Customer_Address_Street_2 =(String) data.getSessionData(Constants.CCAI_Customer_Address_Street_2) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_Street_2) : "NA";
			String CCAI_Customer_Address_City =(String) data.getSessionData(Constants.CCAI_Customer_Address_City) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_City) : "NA";
			String CCAI_Customer_Address_State =(String) data.getSessionData(Constants.CCAI_Customer_Address_State) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_State) : "NA";
			String CCAI_Customer_Address_ZipCode =(String) data.getSessionData(Constants.CCAI_Customer_Address_ZipCode) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_ZipCode) : "NA";
			String CCAI_Customer_Address_Country =(String) data.getSessionData(Constants.CCAI_Customer_Address_Country) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_Country) : "NA";
			String CCAI_Customer_Address_Type =(String) data.getSessionData(Constants.CCAI_Customer_Address_Type) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Address_Type) : "NA";
			String CCAI_Customer_Policy_Number =(String) data.getSessionData(Constants.CCAI_Customer_Policy_Number) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Policy_Number) : "NA";
			String CCAI_Customer_BillingAccount_Number =(String) data.getSessionData(Constants.CCAI_Customer_BillingAccount_Number) != null ? (String) data.getSessionData(Constants.CCAI_Customer_BillingAccount_Number) : "NA";
			String CCAI_Customer_Verified =(String) data.getSessionData(Constants.CCAI_Customer_Verified) != null ? (String) data.getSessionData(Constants.CCAI_Customer_Verified) : "NA";
			String CCAI_Customer_ECN =(String) data.getSessionData(Constants.CCAI_Customer_ECN) != null ? (String) data.getSessionData(Constants.CCAI_Customer_ECN) : "NA";
			
			//Caller Verification//
			/** Brand **/
			String brand = Constants.EmptyString;
			if (null != data.getSessionData("S_FLAG_FDS_BU") && data.getSessionData("S_FLAG_FDS_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) {
				brand = "FDS";
			}
			
			
			boolean validpolicynumber = false;
			if ( null != data.getSessionData("IS_CALLED_SHARED_ID_AUTH") && data.getSessionData("IS_CALLED_SHARED_ID_AUTH").toString().equalsIgnoreCase("TRUE")) {
				validpolicynumber = true;
			}
			else {
				validpolicynumber = false;
			}
			
			String callertype = ((String) data.getSessionData(Constants.S_CALLLER_TYPE)!=null)?((String)data.getSessionData(Constants.S_CALLLER_TYPE)) : "01";
			
			
			
			String callerrisklevel = ((String) data.getSessionData(Constants.S_TI_SCORE)!=null)?((String)data.getSessionData(Constants.S_TI_SCORE)) : Constants.EmptyString;
			
			if(Constants.HIGH.equalsIgnoreCase(callerrisklevel)) {
				callerrisklevel = Constants.LOW;
			}else {
				callerrisklevel = Constants.MEDIUM;
			}
			
			if(Constants.EN.equalsIgnoreCase(language)) {
				language = "en-us";
			}else {
				language = "es-us";
			}
			
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			Ciscosrmcti_Post obj = new Ciscosrmcti_Post();
			
			//START- Non prod changes-Priya
			//Non prod changes-Priya
			data.addToLog("API URL: ", url);
			String prefix = "https://api-np-ss.farmersinsurance.com"; 
			String UAT_FLAG="";
	        if(url.startsWith(prefix)) {
	        	UAT_FLAG="YES";
	        }
			
			Ciscosrmcti_NP_Post objNP = new Ciscosrmcti_NP_Post();
			JSONObject resp=null ;
			if("YES".equalsIgnoreCase(UAT_FLAG)) {
				data.addToLog("S_CISCO_SRMCTI_URL: ", url);
				String Key =Constants.S_CISCO_SRMCTI_URL;
				region = regionDetails.get(Key);
				data.addToLog("Region for UAT endpoint: ", region);
				resp =  (JSONObject) objNP.start(url, callid,  callerani, destinationapp, tollfreenumber, callerinput, poptype, calledinto, calledintime,
						tollfreedescription, callertype, language, an1, upn1, transferreason, intent , brand , validpolicynumber, 
						callerrisklevel, conTimeout, readTimeout, context , CCAI_Agent_FirstName,
						CCAI_Agent_LastName,
						CCAI_Agent_State,
						CCAI_Agent_Code_Producer_Code,
						CCAI_Agency_Name,
						CCAI_Agent_Verified,
						CCAI_Agent_Policy_Number,
						CCAI_Customer_FirstName,
						CCAI_Customer_LastName,
						CCAI_Customer_SSN_Last_4,
						CCAI_Customer_DOB,
						CCAI_Customer_DLNumber,
						CCAI_Customer_Address_Street_1,
						CCAI_Customer_Address_Street_2,
						CCAI_Customer_Address_City,
						CCAI_Customer_Address_State,
						CCAI_Customer_Address_ZipCode,
						CCAI_Customer_Address_Country,
						CCAI_Customer_Address_Type,
						CCAI_Customer_Policy_Number,
						CCAI_Customer_BillingAccount_Number,
						CCAI_Customer_Verified,
						CCAI_Customer_ECN,
						region
						
						);
			}else {
				region="PROD";
			    resp = (JSONObject) obj.start(url, callid,  callerani, destinationapp, tollfreenumber, callerinput, poptype, calledinto, calledintime,
						tollfreedescription, callertype, language, an1, upn1, transferreason, intent , brand , validpolicynumber, 
						callerrisklevel, conTimeout, readTimeout, context , CCAI_Agent_FirstName,
						CCAI_Agent_LastName,
						CCAI_Agent_State,
						CCAI_Agent_Code_Producer_Code,
						CCAI_Agency_Name,
						CCAI_Agent_Verified,
						CCAI_Agent_Policy_Number,
						CCAI_Customer_FirstName,
						CCAI_Customer_LastName,
						CCAI_Customer_SSN_Last_4,
						CCAI_Customer_DOB,
						CCAI_Customer_DLNumber,
						CCAI_Customer_Address_Street_1,
						CCAI_Customer_Address_Street_2,
						CCAI_Customer_Address_City,
						CCAI_Customer_Address_State,
						CCAI_Customer_Address_ZipCode,
						CCAI_Customer_Address_Country,
						CCAI_Customer_Address_Type,
						CCAI_Customer_Policy_Number,
						CCAI_Customer_BillingAccount_Number,
						CCAI_Customer_Verified,
						CCAI_Customer_ECN
						
						
						);
			}
			
			data.addToLog(currElementName, "AFNI_API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				strRespBody = resp.toString();
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					StrExitState = Constants.SU;
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in AFNI_API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"AFNI_API", strReqBody, region,(String) data.getSessionData(Constants.S_CISCO_SRMCTI_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for CiscosrmctiAPI call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		
	return StrExitState;
	
	}

}