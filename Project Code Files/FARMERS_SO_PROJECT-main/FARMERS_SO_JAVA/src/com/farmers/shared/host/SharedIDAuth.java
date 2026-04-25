package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.bean.SHAuthForemost.ForemostRoot;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.google.gson.Gson;

public class SharedIDAuth extends DecisionElementBase 


{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		try {
			
			return fwsPolicyLookupNew(strRespBody, strRespBody, data, caa, currElementName);
			/*
			StrExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
			return StrExitState;
			*/
				
		} catch (Exception e)
		{
			data.addToLog(currElementName,"Exception while forming host details for SIDA_HOST_002  :: "+e);
			caa.printStackTrace(e);
		}
	/*
		try {
			objHostDetails.startHostReport(currElementName,"fwsPolicyLookup", strReqBody);
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU);
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDA_HOST_001  PolicyInquiryAPI call  :: "+e);
			caa.printStackTrace(e);
		}
		*/
		return StrExitState;
	}

	private String AccountLinkANILookup(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName, String strExitState) 
	{
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try 
		{
			  if(data.getSessionData(Constants.S_ACCOUNTLINKANILOOKUPURL) != null 
				&& data.getSessionData(Constants.S_READ_TIMEOUT) != null 
				&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
				&& data.getSessionData(Constants.S_TELEPHONENUMBER) != null 
				&& data.getSessionData(Constants.S_ANI) != null 
				&& data.getSessionData(Constants.S_LOB) != null)
			{
				String wsurl = (String) data.getSessionData(Constants.S_ACCOUNTLINKANILOOKUPURL);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String telephonenumber = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				String  ani = (String) data.getSessionData(Constants.S_ANI);
				String  Brand = (String) data.getSessionData(Constants.S_LOB);
				String  tid = (String) data.getSessionData(Constants.S_TID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				//https://api-ss.farmersinsurance.com/plcyms/v3/policiesAndClaims?operation=searchByPhone
				data.addToLog("wsurl", wsurl);
				data.addToLog("ani", ani);
				data.addToLog("conntimeout", conntimeout);
				data.addToLog("readtimeout", readtimeout);
				Lookupcall lookups = new Lookupcall();
				//UAT CHANGE START(SHAIK,PRIYA)
				String region=null;
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					 region = regionDetails.get(Constants.S_ACCOUNTLINKANILOOKUPURL);
				}else {
					region="PROD";
				}
				org.json.simple.JSONObject responses = (JSONObject) lookups.GetAccountLinkAniLookup(wsurl,tid, telephonenumber, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout),context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
				//END
				if(responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strRespBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set ACElookupAPI API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						apiResponseManupulation_AccountLinkANILookup(data, caa, currElementName, strRespBody);
						strExitState = Constants.SU;
					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
					
				}
			}

		}catch(Exception ae)
		{
			data.addToLog(currElementName,"Exception in ACElookupAPI API call  :: "+ae);
			caa.printStackTrace(ae);
		}
		
		return strExitState;
		
	}
	private String PointPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName, String strExitState) 
	{
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        String region=null;
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
				String input = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				//https://api-ss.farmersinsurance.com/plcyms/v3/policies/21365978
				Lookupcall lookups = new Lookupcall();
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
				if("YES".equalsIgnoreCase(UAT_FLAG)){
				 region = regionDetails.get(Constants.S_POINT_POLICYINQUIIRY_URL);
				}else {
					region="PROD";
				}
				org.json.simple.JSONObject responses = lookups.Getpointpolicyinquiry(wsurl,input,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
				
				if(responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strRespBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_001 : PointPolicyInquiry �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_PointPolicyInquiry(data, caa, currElementName, strRespBody);
						
					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
					
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_HOST_001  PointPolicyInquiry API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String fwsPolicyLookup(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName, String strExitState) 
	{
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
 
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)	
		try {
			if(data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL);
				data.addToLog("wsurl", wsurl.toString());
				String policyContractNumbers = (String) data.getSessionData(Constants.S_POLICY_NUM);
				data.addToLog("policycontractnumber", policyContractNumbers);
				String billingAccountNumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				data.addToLog("billingaccountnumber", billingAccountNumber);
				String telephoneNumber = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				data.addToLog("telephonenumber", telephoneNumber);
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				data.addToLog("tid", tid.toString());
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
		
				//https://api-ss.farmersinsurance.com/plcyms/v3/policies?operation=searchByCriteria
				Lookupcall lookups = new Lookupcall();
				
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				org.json.simple.JSONObject responses = null;
				 String region=null;
                if("YES".equalsIgnoreCase(UAT_FLAG)) {
                region= regionDetails.get(Constants.S_FWS_POLICYLOOKUP_URL);
                }else {
                	region="";
                }

				if(policyContractNumbers != null && !policyContractNumbers.equals("") ) {
				 responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl,tid, policyContractNumbers,null,null,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
					
				}else if(billingAccountNumber != null && !billingAccountNumber.equals("")){
					 responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl,tid, null,billingAccountNumber,null,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
						
				}else {
					 responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl,tid, null,null,telephoneNumber,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
						
				}
				
//				
//				org.json.simple.JSONObject responses = lookups.GetFWSPolicyLookup(wsurl,tid, policycontractnumber, null,null,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context);
				data.addToLog("responses", responses.toString());

				if(responses != null) 
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strRespBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_001 : FWSPolicyLookup �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_FwsPolicyLookup(data, caa, currElementName, strRespBody);
						
					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
					
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_HOST_001  FWSPolicyLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String foremostPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName, String strExitState)
	{

		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try
		{
			if(data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_POLICY_NUM) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null)
			{
				String url = (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				String policynumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String systemDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
				String policysource = "Foremost";
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				//https://api-ss.farmersinsurance.com/plcyms/v3/policies?operation=searchByPolicySource
				Lookupcall lookups = new Lookupcall();
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				String region=null;
				//uat env change(shaik,priya)
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				region = regionDetails.get(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				}else {
					region="PROD";
				}
				org.json.simple.JSONObject responses = lookups.GetforemostPolicyInquiry(url,tid, policynumber, systemDate,policysource, Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				data.addToLog("responses", ""+responses);
				//JSONObject jsonobj = new JSONObject(responses);
				String HTTPStatusCode = (String) responses.get("responseCode");
				String HTTPRespMsg = (String) responses.get("responseMsg");
				
				if(responses != null)
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strRespBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) 
					{
						data.addToLog(currElementName, "Set SIDA_HOST_001 : ForemostPolicyInquiry �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strExitState =	apiResponseManupulation_ForemostPolicyInquiry(data, caa, currElementName, strRespBody);
					
					} else
					{
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
					
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_HOST_001  ForemostPolicyInquiry API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String mulesoftFarmerPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName, String strExitState) 
				{
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
				String policyContractNumbers = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String billingAccountNumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String telephoneNumber = (String) data.getSessionData(Constants.S_ANI);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_TID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				//https://api-ss.farmersinsurance.com/plcyms/v3/policies?operation=searchByCriteria
				Lookupcall lookups = new Lookupcall();
				
				  String region=null;
				  data.addToLog("API URL: ", wsurl);
					String prefix = "https://api-np-ss.farmersinsurance.com"; 
					String UAT_FLAG="";
			        if(wsurl.startsWith(prefix)) {
			        	UAT_FLAG="YES";
			        }
		             if("YES".equalsIgnoreCase(UAT_FLAG)) {
		              region = regionDetails.get(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
		             }else {
		            	 region="PROD";
		             }
						org.json.simple.JSONObject responses = null;
						if(policyContractNumbers != null && !policyContractNumbers.equals("") ) {
						 responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl,tid, policyContractNumbers,null,null,Integer.parseInt(conntimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
							
						}else if(billingAccountNumber != null && !billingAccountNumber.equals("")){
							 responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl,tid, null,billingAccountNumber,null,Integer.parseInt(conntimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
								
						}else {
							 responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl,tid, null,null,telephoneNumber,Integer.parseInt(conntimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
								
						}
					
					
					
					data.addToLog("responses", responses.toString());
				
				if(responses != null)
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strRespBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_MulesoftFarmerPolicyInquiry(data, caa, currElementName, strRespBody);
						
					} else 
					{
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
					
				}
			}
		} catch (Exception e)
		{
			data.addToLog(currElementName,"Exception in SIDA_HOST_001  MulesoftFarmerPolicyInquiry_Post API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
		

	}

	private String policyInquiry_RetriveInsurancePoliciesByParty(String strRespBody, String strRespBody2,
			DecisionElementData data, CommonAPIAccess caa, String currElementName, String strExitState) 
	{
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
	
		try {
			if(data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL) != null &&  data.getSessionData(Constants.S_TELE_PHONE_NUMBER) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null)
			{
				String wsurl = (String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
				String phoneNo = (String) data.getSessionData(Constants.S_PHONENUMBER);
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				Lookupcall lookups = new Lookupcall();
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				String region = null;
				 if("YES".equalsIgnoreCase(UAT_FLAG)) {
						region=regionDetails.get(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
				 }else {
					 region="PROD";
				 }
				org.json.simple.JSONObject responses = lookups.GetPolicyInquiry_RetrieveInsurance(wsurl,tid,phoneNo,Integer.parseInt(connTimeout),Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
				if(responses != null)
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strRespBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_RetriveInsurancePoliciesByParty(data, caa, currElementName, strRespBody);
					
					} else 
					{
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
					
				}
				

			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_HOST_001  RetrieveIntermediarySegmentationInfo API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String  apiResponseManupulation_MulesoftFarmerPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		
		String strExitState = Constants.ER;
		try {
			
			String Zip = "";
			String lob = null;
			String policynumber = null;
			String prompt = null;
			int rvprodtypecount = 0;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int mrprodtypecount = 0;
			int spprodtypecount = 0;
			boolean singleproducttype = false;
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray agentsArr = (JSONArray)resp.get("policies");
			JSONArray insuredDetailsArr = (JSONArray)(((JSONObject) agentsArr.get(0)).get("insuredDetails"));
			if(((JSONObject) insuredDetailsArr.get(0)).containsKey("birthDate"))
			{
				String birthDate = (String)((JSONObject) insuredDetailsArr.get(0)).get("birthDate");
				data.setSessionData("S_API_DOB", birthDate);
				data.addToLog("S_API_DOB", birthDate);
			}
			
			if(((JSONObject) insuredDetailsArr.get(0)).containsKey("addresses"))
			{
				JSONArray zipDetailsArr = (JSONArray)(((JSONObject) agentsArr.get(0)).get("addresses"));
				if(((JSONObject) zipDetailsArr.get(0)).containsKey("zip"))
				{
					Zip = (String)((JSONObject) zipDetailsArr.get(0)).get("zip");
					data.setSessionData("S_API_ZIP", Zip);
					data.addToLog("S_API_ZIP", Zip);
				}
				if (null != ((JSONObject) zipDetailsArr.get(0)).get("state"))  {
					data.setSessionData(Constants.S_POLICY_STATE_CODE, (String)((JSONObject) zipDetailsArr.get(0)).get("state"));
				}
				
			}
			
			if(agentsArr != null && agentsArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP, agentsArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();

				if(currElementName.equalsIgnoreCase("KYCMF_HOST_004") || currElementName.equalsIgnoreCase("KYCMF_HOST_003")) return Constants.SU;

				for(int i = 0; i < agentsArr.size(); i++) 
				{
					JSONObject policydata = (JSONObject) agentsArr.get(i);

					if (policydata.containsKey("lineOfBusiness")) {
						lob = (String) policydata.get("lineOfBusiness");

						if (productTypeCounts.containsKey(lob)) {
							productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						}
						else {
							productTypeCounts.put(lob, 1);
						}
					}
				}

				data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_RV)) {
					rvprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_RV);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_SP)) {
					spprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_SP);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_MR)) {
					mrprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_MR);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_HOME)) {
					homeprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_HOME);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_AUTO)) {
					autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_AUTO);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_UMBRELLA)) {
					umbrellaprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_UMBRELLA);
				}

				data.setSessionData(Constants.RV_PRODUCTTYPECOUNT_SHAUTH, rvprodtypecount);
				data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH, autoprodtypecount);
				data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_SHAUTH, homeprodtypecount);
				data.setSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_SHAUTH, umbrellaprodtypecount);
				data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_SHAUTH, mrprodtypecount);
				data.setSessionData(Constants.SP_PRODUCTTYPECOUNT_SHAUTH, spprodtypecount);

				data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH));
				data.addToLog(currElementName,"Home Product Type Count  = "+data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_SHAUTH));
				data.addToLog(currElementName,"Umbrella Product Type Count = "+data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_SHAUTH));
				data.addToLog(currElementName,"Boat Product Type Count  = "+data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_SHAUTH));
				data.addToLog(currElementName,"Specialty Dwelling or Mobile Home Product Type Count  = "+data.getSessionData(Constants.SP_PRODUCTTYPECOUNT_SHAUTH));

				singleproducttype = checkifsingleproducttype(rvprodtypecount, autoprodtypecount, homeprodtypecount, umbrellaprodtypecount, mrprodtypecount, spprodtypecount);
				data.addToLog(currElementName, "Check if single Product : "+singleproducttype);

				if (agentsArr.size() == 1)

					{

					if (lob.equalsIgnoreCase("AUTO")) {
						prompt = "Auto policy";
					}
					else if (lob.equalsIgnoreCase("HOME")) {
						prompt = "a Home policy";
					}
					else if (lob.equalsIgnoreCase("MR")) {
						prompt = "a Boat policy";
					}
					else if (lob.equalsIgnoreCase("RV")) {
						prompt = "a Motor Home or Motorcycle Policy";
					}
					else if (lob.equalsIgnoreCase("SP")) {
						prompt = "a Specialty Dwelling or Mobile Home Policy";
					}
					else if (lob.equalsIgnoreCase("UMBRELLA")) {
						prompt = "an Umbrella Policy";
					}
  
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto policy")) {
						prompt = "una póliza de auto";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = "una póliza de casa";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = "o una póliza de paraguas";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = "una póliza de barco";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
						prompt = "una casa rodante o póliza de motocicleta";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
						prompt = "una vivienda especial o póliza de casa móvil";
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					}
					
					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Single Policy Scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

					strExitState = Constants.SU;
				}

				else if (!singleproducttype) {

					if(autoprodtypecount >= 1) {
						prompt = "an Auto policy"; 
					}
					if (homeprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Home Policy";
						}else {
							prompt = "a Home Policy";
						}
					}
					if (mrprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Boat Policy";
						}else {
							prompt = "a Boat Policy";
						}
					}
					if (rvprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Motor Home or Motorcycle Policy";
						}else {
							prompt = "a Motor Home or Motorcycle Policy";
						}
					}
					if (spprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Specialty Dwelling or Mobile Home Policy";
						}else {
							prompt = "a Specialty Dwelling or Mobile Home Policy";
						}
					}
					if (umbrellaprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " or an Umbrella Policy";
						}else {
							prompt = "an Umbrella Policy";
						}
					}

					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						prompt = prompt.replace("an Auto policy","una póliza de auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy"," una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace("a Home Policy","una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy"," o una póliza de paraguas");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace(" a Boat Policy"," una póliza de barco");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace("a Boat Policy","una póliza de barco");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
						prompt = prompt.replace(" a Motor Home or Motorcycle Policy"," una casa rodante o póliza de motocicleta");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
						prompt = prompt.replace("a Motor Home or Motorcycle Policy","una casa rodante o póliza de motocicleta");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
						prompt = prompt.replace(" a Specialty Dwelling or Mobile Home Policy"," una vivienda especial o póliza de casa móvil");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
						prompt = prompt.replace("a Specialty Dwelling or Mobile Home Policy"," una vivienda especial o póliza de casa móvil");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an umbrella policy")) {
						prompt = prompt.replace("an Umbrella Policy"," una póliza de paraguas");
					}
					
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Multiple Product Types Scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

					strExitState = Constants.SU;
				}

				else if (agentsArr.size() > 1 && singleproducttype) {
					if (lob.equalsIgnoreCase("RV")) {
						prompt = "Motor Home or Motorcycle";
					}
					else if (lob.equalsIgnoreCase("AUTO")) {
						prompt = "Auto";
					}
					else if (lob.equalsIgnoreCase("HOME")) {
						prompt = "Home";
					}
					else if (lob.equalsIgnoreCase("UMBRELLA")) {
						prompt = "Umbrella";
					}
					else if (lob.equalsIgnoreCase("MR")) {
						prompt = "Boat";
					}
					else if (lob.equalsIgnoreCase("SP")) 
					{
						prompt = "Specialty Dwelling or Mobile Home";
					}	
					else if (lob.equalsIgnoreCase("F")) 
					{
						prompt = "Specialty Dwelling or Mobile Home";
					}	
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace("Auto"," auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace("Home"," casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella")) {
						prompt = prompt.replace("Umbrella"," paraguas");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat")) {
						prompt = prompt.replace("Boat"," barco");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("motor home or motorcycle")) {
						prompt = prompt.replace("Motor Home or Motorcycle"," una casa rodante o motocicleta");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home")) {
						prompt = prompt.replace("Specialty Dwelling or Mobile Home"," vivienda especial o casa móvil");
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Single Product Type Multiple Policy scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

					strExitState = Constants.SU;
				}
			}
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in AccLinkAniLookup method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState; 

	}
	
	private String apiResponseManupulation_ForemostPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) 
	{	
		String strExitState = Constants.ER;
		try 
		{
			String Zip="";
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray agentsArr = (JSONArray)resp.get("policies");
			String policySource = (String)((JSONObject) agentsArr.get(0)).get("policySource");
			String lob = null;
			String policynumber = null;
			String prompt = null;
			int rvprodtypecount = 0;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int mrprodtypecount = 0;
			int spprodtypecount = 0;	
			boolean singleproducttype = false;
			JSONArray insuredDetailsArr = (JSONArray)(((JSONObject) agentsArr.get(0)).get("insureds"));
			if(((JSONObject) insuredDetailsArr.get(0)).containsKey("birthDate"))
			{
				String birthDate = (String)((JSONObject) insuredDetailsArr.get(0)).get("birthDate");
				data.setSessionData("S_API_DOB", birthDate);
				data.addToLog("S_API_DOB", birthDate);
			}
			
			if(((JSONObject) insuredDetailsArr.get(0)).containsKey("addresses"))
			{
				JSONArray zipDetailsArr = (JSONArray)(((JSONObject) agentsArr.get(0)).get("addresses"));
				if(((JSONObject) zipDetailsArr.get(0)).containsKey("zip"))
				{
					Zip = (String)((JSONObject) zipDetailsArr.get(0)).get("zip");
					data.setSessionData("S_API_ZIP", Zip);
					data.addToLog("S_API_ZIP", Zip);
				}
				if (null != ((JSONObject) zipDetailsArr.get(0)).get("state"))  {
					data.setSessionData(Constants.S_POLICY_STATE_CODE, (String)((JSONObject) zipDetailsArr.get(0)).get("state"));
				}
			}
			String GSONLIB = resp.toString();
			Gson gsonobj = new Gson();
			ForemostRoot retrivalpart = gsonobj.fromJson(GSONLIB, ForemostRoot.class);
			List<com.farmers.bean.SHAuthForemost.Policy> FormePolcy = retrivalpart.getPolicies();
		
			
			if(agentsArr != null && agentsArr.size() > 0)
			{
				data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP, agentsArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();

				if(currElementName.equalsIgnoreCase("KYCMF_HOST_004") || currElementName.equalsIgnoreCase("KYCMF_HOST_003")) return Constants.SU;

				for(int i = 0; i < agentsArr.size(); i++) 
				{
					JSONObject policydata = (JSONObject) agentsArr.get(i);

					if (policydata.containsKey("lineOfBusiness")) {
						lob = (String) policydata.get("lineOfBusiness");

						if (productTypeCounts.containsKey(lob)) {
							productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						}
						else {
							productTypeCounts.put(lob, 1);
						}
					}
				
					data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);

					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_RV)) {
						rvprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_RV);
					}
					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_SP)) {
						spprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_SP);
					}
					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_MR)) {
						mrprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_MR);
					}
					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_HOME)) {
						homeprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_HOME);
					}
					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_AUTO)) {
						autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_AUTO);
					}
					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_UMBRELLA)) {
						umbrellaprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_UMBRELLA);
					}

					data.setSessionData(Constants.RV_PRODUCTTYPECOUNT_SHAUTH, rvprodtypecount);
					data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH, autoprodtypecount);
					data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_SHAUTH, homeprodtypecount);
					data.setSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_SHAUTH, umbrellaprodtypecount);
					data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_SHAUTH, mrprodtypecount);
					data.setSessionData(Constants.SP_PRODUCTTYPECOUNT_SHAUTH, spprodtypecount);

					data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH));
					data.addToLog(currElementName,"Home Product Type Count  = "+data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_SHAUTH));
					data.addToLog(currElementName,"Umbrella Product Type Count = "+data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_SHAUTH));
					data.addToLog(currElementName,"Boat Product Type Count  = "+data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_SHAUTH));
					data.addToLog(currElementName,"Specialty Dwelling or Mobile Home Product Type Count  = "+data.getSessionData(Constants.SP_PRODUCTTYPECOUNT_SHAUTH));

					singleproducttype = checkifsingleproducttype(rvprodtypecount, autoprodtypecount, homeprodtypecount, umbrellaprodtypecount, mrprodtypecount, spprodtypecount);
					data.addToLog(currElementName, "Check if single Product : "+singleproducttype);

					if (agentsArr.size() == 1)

						{

						if (lob.equalsIgnoreCase("AUTO")) {
							prompt = "Auto policy";
						}
						else if (lob.equalsIgnoreCase("HOME")) {
							prompt = "a Home policy";
						}
						else if (lob.equalsIgnoreCase("MR")) {
							prompt = "a Boat policy";
						}
						else if (lob.equalsIgnoreCase("RV")) {
							prompt = "a Motor Home or Motorcycle Policy";
						}
						else if (lob.equalsIgnoreCase("SP")) {
							prompt = "a Specialty Dwelling or Mobile Home Policy";
						}
						else if (lob.equalsIgnoreCase("UMBRELLA")) {
							prompt = "an Umbrella Policy";
						}
						
						String lang = (String) data.getSessionData("S_PREF_LANG");
						
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto policy")) {
							prompt = "póliza de auto";
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
							prompt = "una póliza de casa";
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
							prompt = "o una póliza de paraguas";
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
							prompt = "una póliza de barco";
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or mtorcycle policy")) {
							prompt = "una casa rodante o póliza de motocicleta";
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
							prompt = "una vivienda especial o póliza de casa móvil";
						}

						if (prompt.contains(" ")) {
							prompt = prompt.replaceAll(" ", ".");
						}  

						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
						data.addToLog(currElementName,"Single Policy Scenario");
						data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
						data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

						data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
						data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

						strExitState = Constants.SU;
					}

					else if (!singleproducttype) {

						if(autoprodtypecount >= 1) {
							prompt = "an Auto policy"; 
						}
						if (homeprodtypecount >= 1) {
							if (null != prompt) {
								prompt += " a Home Policy";
							}else {
								prompt = "a Home Policy";
							}
						}
						if (mrprodtypecount >= 1) {
							if (null != prompt) {
								prompt += " a Boat Policy";
							}else {
								prompt = "a Boat Policy";
							}
						}
						if (rvprodtypecount >= 1) {
							if (null != prompt) {
								prompt += " a Motor Home or Motorcycle Policy";
							}else {
								prompt = "a Motor Home or Motorcycle Policy";
							}
						}
						if (spprodtypecount >= 1) {
							if (null != prompt) {
								prompt += " a Specialty Dwelling or Mobile Home Policy";
							}else {
								prompt = "a Specialty Dwelling or Mobile Home Policy";
							}
						}
						if (umbrellaprodtypecount >= 1) {
							if (null != prompt) {
								prompt += " or an Umbrella Policy";
							}else {
								prompt = "an Umbrella Policy";
							}
						}
						
						String lang = (String) data.getSessionData("S_PREF_LANG");
						
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
							prompt = prompt.replace("an Auto policy","una póliza de auto");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
							prompt = prompt.replace(" a Home Policy"," una póliza de casa");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
							prompt = prompt.replace("a Home Policy","una póliza de casa");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
							prompt = prompt.replace(" or an Umbrella Policy"," o una póliza de paraguas");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an umbrella policy")) {
							prompt = prompt.replace("an Umbrella Policy","una póliza de paraguas");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
							prompt = prompt.replace(" a Boat Policy"," una póliza de barco");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
							prompt = prompt.replace("a Boat Policy","una póliza de barco");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
							prompt = prompt.replace(" a Motor Home or Motorcycle Policy"," una casa rodante o póliza de motocicleta");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
							prompt = prompt.replace("a Motor Home or Motorcycle Policy","una casa rodante o póliza de motocicleta");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
							prompt = prompt.replace(" a Specialty Dwelling or Mobile Home Policy"," una vivienda especial o póliza de casa móvil");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
							prompt = prompt.replace("a Specialty Dwelling or Mobile Home Policy"," una vivienda especial o póliza de casa móvil");
						}

						if (prompt.contains(" ")) {
							prompt = prompt.replaceAll(" ", ".");
						} 

						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
						data.addToLog(currElementName,"Multiple Product Types Scenario");
						data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
						data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

						data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
						data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

						strExitState = Constants.SU;
					}

					else if (agentsArr.size() > 1 && singleproducttype) {
						if (lob.equalsIgnoreCase("RV")) {
							prompt = "Motor Home or Motorcycle";
						}
						else if (lob.equalsIgnoreCase("AUTO")) {
							prompt = "Auto";
						}
						else if (lob.equalsIgnoreCase("HOME")) {
							prompt = "Home";
						}
						else if (lob.equalsIgnoreCase("UMBRELLA")) {
							prompt = "Umbrella";
						}
						else if (lob.equalsIgnoreCase("MR")) {
							prompt = "Boat";
						}
						else if (lob.equalsIgnoreCase("SP")) {
							prompt = "Specialty Dwelling or Mobile Home";
						}	
						
						String lang = (String) data.getSessionData("S_PREF_LANG");
						
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
							prompt = prompt.replace("Auto"," auto");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
							prompt = prompt.replace("Home"," casa");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella")) {
							prompt = prompt.replace("Umbrella"," paraguas");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat")) {
							prompt = prompt.replace("Boat"," barco");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle")) {
							prompt = prompt.replace("Motor Home or Motorcycle"," una casa rodante o motocicleta");
						}
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home")) {
							prompt = prompt.replace("Specialty Dwelling or Mobile Home"," una vivienda especial o casa móvil");
						}

						if (prompt.contains(" ")) {
							prompt = prompt.replaceAll(" ", ".");
						} 

						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
						data.addToLog(currElementName,"Single Product Type Multiple Policy scenario");
						data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
						data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

						data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
						data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

						strExitState = Constants.SU;
					}
				}
			}
				else {
					strExitState = Constants.ER;
				}
			} catch (Exception e) {
				data.addToLog(currElementName,"Exception in AccLinkAniLookup method  :: "+e);
				caa.printStackTrace(e);
			}
			return strExitState;
				
	}

	private String apiResponseManupulation_FwsPolicyLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody)
	{			
		String strExitState = Constants.ER;
		try {
			
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray policiesArr = (JSONArray)resp.get("policies");
		
			String lob = null;
			String prompt = Constants.EmptyString;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int mrprodtypecount = 0;
			boolean singleproducttype = false;
			
			if(policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String,PolicyBean>>();
				for(int i = 0; i < policiesArr.size(); i++) {
					PolicyBean beanObj = new PolicyBean();
					String strPolicyNum = Constants.EmptyString;
					JSONObject policyData = (JSONObject) policiesArr.get(i);
					
					if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
						lob = (String) policyData.get("lineOfBusiness");
						beanObj.setStrPolicyLOB(lob);
						if (productTypeCounts.containsKey(lob)) productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						else productTypeCounts.put(lob, 1);
					}
					String zip = "";
					if (policyData.containsKey("addresses") && null != policyData.get("addresses")) {
						JSONArray addressesArr = (JSONArray)policyData.get("addresses");
						JSONObject addressesObj = (JSONObject) addressesArr.get(0);
						if (addressesObj.containsKey("zip")) {
							zip = zip.equals("")?""+addressesObj.get("zip").toString().substring(0, 5) : zip+","+addressesObj.get("zip").toString().substring(0, 5);
							//zip = (String) addressesObj.get("zip");
						}
						beanObj.setStrZipCode(zip);
						if (addressesObj.containsKey("state")) beanObj.setStrPolicyState((String)addressesObj.get("state"));
					}
					if (policyData.containsKey("insuredDetails")) {
						
						String strDOB = "";
						JSONArray insuredDetailsArr = (JSONArray)policyData.get("insuredDetails");
						for(int k =0;k<insuredDetailsArr.size();k++) {
							JSONObject obj =(JSONObject)insuredDetailsArr.get(k);
							strDOB = (strDOB.equals(""))?""+obj.get("birthDate"):strDOB+","+obj.get("birthDate");
						}
						beanObj.setStrDOB(strDOB);
						
						JSONObject insuredDetailsObj =(JSONObject)insuredDetailsArr.get(0);
						if(insuredDetailsObj.containsKey("firstName") && null != insuredDetailsObj.get("firstName")) beanObj.setStrFirstName((String)insuredDetailsObj.get("firstName"));
						if(insuredDetailsObj.containsKey("lastName") && null != insuredDetailsObj.get("lastName")) beanObj.setStrLastName((String)insuredDetailsObj.get("lastName"));
					}
					if (policyData.containsKey("policyNumber") && null != policyData.get("policyNumber")) {
						strPolicyNum = (String) policyData.get("policyNumber");
						beanObj.setStrPolicyNum(strPolicyNum);
					}
					
					if(policyData.containsKey("policySource") && null != policyData.get("policySource")) beanObj.setStrPolicySource((String)policyData.get("policySource"));
					if(policyData.containsKey("policyState") && null != policyData.get("policyState")) beanObj.setStrPolicyState((String)policyData.get("policyState"));
					if(policyData.containsKey("suffix") && null != policyData.get("suffix")) beanObj.setStrPolicySuffix((String)policyData.get("suffix"));
					if(policyData.containsKey("effectiveDate") && null != policyData.get("effectiveDate")) beanObj.setStrEffectiveDate((String)policyData.get("effectiveDate"));
					if(policyData.containsKey("InternalPolicyVersion") && null != policyData.get("InternalPolicyVersion")) beanObj.setStrInternalPolicyNumber((String)policyData.get("InternalPolicyVersion"));
					if(policyData.containsKey("InternalPolicyNumber") && null != policyData.get("InternalPolicyNumber")) beanObj.setStrInternalPolicyVersion((String)policyData.get("InternalPolicyNumber"));
					if(policyData.containsKey("billingAccountNumber") && null != policyData.get("billingAccountNumber")) {
						String tmpBillingAccNum = (String)policyData.get("billingAccountNumber");
						if("ARS".equalsIgnoreCase(beanObj.getStrPolicySource()) && tmpBillingAccNum.length() > 9) {
							beanObj.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj.setStrBillingAccountNumber(tmpBillingAccNum.substring(2, tmpBillingAccNum.length()));
						} else {
							beanObj.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj.setStrBillingAccountNumber(tmpBillingAccNum.substring(0, tmpBillingAccNum.length()));
						}
					}
					
					String tmpKey = Constants.EmptyString;
					switch (lob) {
					case Constants.PRODUCTTYPE_Y:
						tmpKey = Constants.MR_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_H:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_F:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_A:
						tmpKey = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_U:
						tmpKey = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
						break;
					default:
						break;
					}
					if(productPolicyMap.containsKey(tmpKey)) {
						HashMap<String, PolicyBean> tmpPolicyDetails = productPolicyMap.get(tmpKey);
						tmpPolicyDetails.put(strPolicyNum, beanObj);
						productPolicyMap.put(tmpKey, tmpPolicyDetails);
					} else {
						HashMap<String, PolicyBean> policyDetails = new HashMap<String, PolicyBean>();
						policyDetails.put(strPolicyNum, beanObj);
						productPolicyMap.put(tmpKey, policyDetails);
					}
				}
				data.setSessionData(Constants.S_POLICYDETAILS_MAP, productPolicyMap);
				data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);
				data.addToLog(currElementName, "policyDetails Hashmap : "+productPolicyMap);

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_Y)) {
					mrprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_Y);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_H)) {
					homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_H);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_F)) {
					homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_F);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_A)) {
					autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_A);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_U)) {
					umbrellaprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_U);
				}

				data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC, autoprodtypecount);
				data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC, homeprodtypecount);
				data.setSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC, umbrellaprodtypecount);
				data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC, mrprodtypecount);
				

				data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Home Product Type Count  = "+data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Umbrella Product Type Count = "+data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Boat Product Type Count  = "+data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(0, autoprodtypecount, homeprodtypecount, umbrellaprodtypecount, mrprodtypecount, 0);
				data.addToLog(currElementName, "Check if single Product : "+singleproducttype);

				if (policiesArr.size() == 1) {
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+Constants.S_SINGLE_POLICY_FOUND);
					for (String key : productPolicyMap.keySet()) {
						HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap.get(key);
						for (String key1 : policyDetailsMap.keySet()) {
							PolicyBean obj = (PolicyBean) policyDetailsMap.get(key1);
							data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
							data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
							
							data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", lob);
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
						}
					}
					strExitState = Constants.SU;
				} else if (!singleproducttype) {
					if(autoprodtypecount >= 1) prompt = prompt+" an Auto policy"; 
					if (homeprodtypecount >= 1) prompt = prompt+" a Home Policy"; 
					if (mrprodtypecount >= 1) prompt = prompt+" a Boat Policy"; 
					if (umbrellaprodtypecount >= 1) prompt = prompt+" or an Umbrella Policy"; 
			
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						prompt = prompt.replace(" an Auto policy"," una póliza de auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy"," una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy"," o una póliza de paraguas");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace(" or an Umbrella Policy"," una póliza de barco");
					}
					
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Multiple Product Types Scenario");
					data.addToLog(currElementName,"Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
					
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));
					
					strExitState = Constants.SU;
				} else if (policiesArr.size() > 1 && singleproducttype) {
					String produtType = "";
					if(autoprodtypecount >= 1) {
						prompt = " Auto "; 
						produtType = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
					}
					else if (homeprodtypecount >= 1) {
						prompt = " Home "; 
						produtType = Constants.HOME_PRODUCTTYPECOUNT_KYC;
					}
					else if (umbrellaprodtypecount >= 1) {
						prompt = " an Umbrella ";
						produtType = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
					}
					else if(mrprodtypecount >= 1) {
						prompt = " Boat ";
						produtType = Constants.MR_PRODUCTTYPECOUNT_KYC;
					}
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace(" Auto "," auto ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace(" Home "," casa ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an umbrella")) {
						prompt = prompt.replace(" an Umbrella "," una paraguas ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat")) {
						prompt = prompt.replace(" Boat "," barco ");
					}
					
					HashMap<String, PolicyBean> policyDetailsMap = new HashMap<String, PolicyBean>();
					for (String key : productPolicyMap.keySet()) {
						policyDetailsMap = productPolicyMap.get(key);
					}
					data.setSessionData(Constants.POLICY_DETAILS_FOR_MN3, policyDetailsMap);
					
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 
					
					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
					
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));
					
					strExitState = Constants.SU;
				}
			} else {
				strExitState = Constants.ER;
			}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in AccLinkAniLookup method  :: "+e);
		caa.printStackTrace(e);
	}
	return strExitState;

}
		

	private String apiResponseManupulation_RetriveInsurancePoliciesByParty(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			
			//String strExitState = Constants.ER;
			String lob = null;
			String policynumber = null;
			String prompt = null;
			int rvprodtypecount = 0;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			boolean singleproducttype = false;
			
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			String GSONLIB = resp.toString();
			Gson gsonobj = new Gson();
			com.farmers.bean.SHAuthBWPHNOSearch.BWRoot retrivalpart = gsonobj.fromJson(GSONLIB, com.farmers.bean.SHAuthBWPHNOSearch.BWRoot.class);
			List<com.farmers.bean.SHAuthBWPHNOSearch.AutoPolicy> AUTOS = (List<com.farmers.bean.SHAuthBWPHNOSearch.AutoPolicy>) retrivalpart.getHousehold().getAutoPolicies();
			List<com.farmers.bean.SHAuthBWPHNOSearch.HomePolicy> HOME = (List<com.farmers.bean.SHAuthBWPHNOSearch.HomePolicy>) retrivalpart.getHousehold().getHomePolicies();
			JSONObject resps = (JSONObject) new JSONParser().parse(strRespBody);
			data.setSessionData("S_API_DOB", "");
			data.setSessionData("S_API_ZIP", "");
			
			JSONObject household = (JSONObject) resp.get("household");
			JSONArray autoPoliciesarr = (JSONArray)household.get("autoPolicies");
			JSONArray homePoliciesarr = (JSONArray)household.get("homePolicies");
			if(homePoliciesarr != null && homePoliciesarr.size() > 0) data.setSessionData(Constants.S_ACTIVE_CLAIM_AVAILABLE, Constants.STRING_YES); 
			
			if(autoPoliciesarr != null && autoPoliciesarr.size() > 0) 
			{
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, autoPoliciesarr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				if(currElementName.equalsIgnoreCase("SIDA_HOST_001") || currElementName.equalsIgnoreCase("SIDA_HOST_001")) return Constants.SU;
				data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP, autoPoliciesarr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				if(currElementName.equalsIgnoreCase("SIDA_HOST_001") || currElementName.equalsIgnoreCase("SIDA_HOST_001")) return Constants.SU;

				data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);
				autoprodtypecount = autoPoliciesarr.size();
				homeprodtypecount = homePoliciesarr.size();
				data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH, autoprodtypecount);
				data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_SHAUTH, homeprodtypecount);
				if(autoprodtypecount>1)
				{
					data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH, autoprodtypecount);
					
				}
				if(homeprodtypecount>1)
				{
					data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_SHAUTH, homeprodtypecount);
				}
				
				data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH));
				data.addToLog(currElementName,"Home Product Type Count  = "+data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_SHAUTH));
				lob="AUTO";
				singleproducttype = checkifsingleproducttype( autoprodtypecount, homeprodtypecount );
				data.addToLog(currElementName, "Check if single Product : "+singleproducttype);
				if (autoPoliciesarr.size() == 1)
				{

					if (lob.equalsIgnoreCase("AUTO"))
					{
						prompt = "Auto policy";
					}
					else if (lob.equalsIgnoreCase("HOME")) {
						prompt = "a Home policy";
					}
					else if (lob.equalsIgnoreCase("MR")) {
						prompt = "a Boat policy";
					}
					else if (lob.equalsIgnoreCase("RV")) {
						prompt = "a Motor Home or motorcycle policy";
					}
					else if (lob.equalsIgnoreCase("SP")) {
						prompt = "a Specialty Dwelling or Mobile Home Policy";
					}
					else if (lob.equalsIgnoreCase("UMBRELLA")) {
						prompt = "an Umbrella Policy";
					}
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto policy")) {
						prompt = "una póliza de auto";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = "una póliza de casa";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = "una póliza de barco";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
						prompt = "una casa rodante o póliza de motocicleta";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
						prompt = "una vivienda especial o póliza de casa móvil";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an umbrella policy")) {
						prompt = "una póliza de paraguas";
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					}  

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Single Policy Scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
					strExitState = Constants.SU;
				}

				else if (!singleproducttype) 
				{

					if(autoprodtypecount >= 1) 
					{
						prompt = "an Auto policy"; 
					}
					if (homeprodtypecount >= 1) 
					{
						if (null != prompt) 
						{
							prompt += " a Home Policy";
						}else 
						{
							prompt = "a Home Policy";
						}
					}
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						prompt = prompt.replace("an Auto policy"," una póliza de auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace("a Home Policy","una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy"," una póliza de casa");
					}

					if (prompt.contains(" "))
					{
						prompt = prompt.replaceAll(" ", ".");
					} 

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Multiple Product Types Scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
					strExitState = Constants.SU;
				}

				else if (autoPoliciesarr.size() > 1 && singleproducttype) 
				{
					if (lob.equalsIgnoreCase("RV")) {
						prompt = "Motor Home or Motorcycle";
					}
					else if (lob.equalsIgnoreCase("AUTO")) {
						prompt = "Auto";
					}
					else if (lob.equalsIgnoreCase("HOME")) {
						prompt = "Home";
					}
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace("Auto"," auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace("Home"," casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("motor home or motorcycle")) {
						prompt = prompt.replace("Motor Home or Motorcycle"," una casa rodante o motocicleta");
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Single Product Type Multiple Policy scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					if(AUTOS.get(0).getNamedInsured().getBirthDate()!=null)
					{
						String birthDate = AUTOS.get(0).getNamedInsured().getBirthDate();
						data.setSessionData("S_API_DOB", birthDate);
						data.addToLog("S_API_DOB", birthDate);
					}
					if(AUTOS.get(0).getInsuredVehicle().getGaragingAddress().getPostalCode()!=null)
					{
					    String Zip = AUTOS.get(0).getInsuredVehicle().getGaragingAddress().getPostalCode();
						data.setSessionData("S_API_ZIP", Zip);
						data.addToLog("S_API_ZIP", Zip);
					}
			
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

					
					strExitState = Constants.SU;
				}
				
			}
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) 
		{
			data.addToLog(currElementName,"Exception in AccLinkAniLookup method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;

}

	public boolean checkifsingleproducttype( int autoprodtypecount, int homeprodtypecount)
	{
		int nonZeroCount = 0;

		if (autoprodtypecount != 0) {
			nonZeroCount++;
		}
		if (homeprodtypecount != 0) {
			nonZeroCount++;
		}
		// Return true only if there's exactly one non-zero element
		return nonZeroCount == 1;
	}
	private String apiResponseManupulation_PointPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject resultsObj = (JSONObject) resp.get("policySummary");
			
			String lob = null;
			String policynumber = null;
			String prompt = null;
			int rvprodtypecount = 0;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int mrprodtypecount = 0;
			int spprodtypecount = 0;	
			boolean singleproducttype = false;
			
			if(resultsObj != null && resultsObj.size() > 0)
			{
				data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP, resultsObj.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();

				if(currElementName.equalsIgnoreCase("SIDA_HOST_001") || currElementName.equalsIgnoreCase("SIDA_HOST_001")) return Constants.SU;
				
				JSONArray address = (JSONArray)resultsObj.get("address");
				JSONArray driversarr = (JSONArray)resultsObj.get("drivers");
				
				if(driversarr !=null && ((JSONObject) driversarr.get(0)).containsKey("dateofbirth"))
				{
					String birthDate = (String)((JSONObject) driversarr.get(0)).get("dateofbirth");
					data.setSessionData("S_API_DOB", birthDate);
					data.addToLog("S_API_DOB", birthDate);
				}
				if(address !=null && ((JSONObject) address.get(0)).containsKey("zip"))
				{
					String Zip = (String)((JSONObject) address.get(0)).get("zip");
					data.setSessionData("S_API_ZIP", Zip);
					data.addToLog("S_API_ZIP", Zip);
					
				}
				
				

				for(int i = 0; i < resultsObj.size(); i++) 
				{
					JSONObject policydata = (JSONObject) resultsObj.get(i);

					if (policydata.containsKey("lob"))
					{
						lob = (String) policydata.get("lob");

						if (productTypeCounts.containsKey(lob)) {
							productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						}
						else {
							productTypeCounts.put(lob, 1);
						}
					}
				}
				
				data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_RV)) {
					rvprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_RV);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_SP)) {
					spprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_SP);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_MR)) {
					mrprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_MR);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_HOME)) {
					homeprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_HOME);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_AUTO)) {
					autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_AUTO);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_AUTOP)) {
					autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_AUTO);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_UMBRELLA)) {
					umbrellaprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_UMBRELLA);
				}
				data.setSessionData(Constants.RV_PRODUCTTYPECOUNT_SHAUTH, rvprodtypecount);
				data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH, autoprodtypecount);
				data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_SHAUTH, homeprodtypecount);
				data.setSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_SHAUTH, umbrellaprodtypecount);
				data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_SHAUTH, mrprodtypecount);
				data.setSessionData(Constants.SP_PRODUCTTYPECOUNT_SHAUTH, spprodtypecount);

				data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH));
				data.addToLog(currElementName,"Home Product Type Count  = "+data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_SHAUTH));
				data.addToLog(currElementName,"Umbrella Product Type Count = "+data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_SHAUTH));
				data.addToLog(currElementName,"Boat Product Type Count  = "+data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_SHAUTH));
				data.addToLog(currElementName,"Specialty Dwelling or Mobile Home Product Type Count  = "+data.getSessionData(Constants.SP_PRODUCTTYPECOUNT_SHAUTH));
				singleproducttype = checkifsingleproducttype(rvprodtypecount, autoprodtypecount, homeprodtypecount, umbrellaprodtypecount, mrprodtypecount, spprodtypecount);
				data.addToLog(currElementName, "Check if single Product : "+singleproducttype);

				if (resultsObj.size() == 1) {

					if (lob.equalsIgnoreCase("AUTO")) {
						prompt = "Auto policy";
					}
					else if (lob.equalsIgnoreCase("HOME")) {
						prompt = "a Home policy";
					}
					else if (lob.equalsIgnoreCase("MR")) {
						prompt = "a Boat policy";
					}
					else if (lob.equalsIgnoreCase("RV")) {
						prompt = "a Motor Home or Motorcycle Policy";
					}
					else if (lob.equalsIgnoreCase("SP")) {
						prompt = "a Specialty Dwelling or Mobile Home Policy";
					}
					else if (lob.equalsIgnoreCase("UMBRELLA")) {
						prompt = "an Umbrella Policy";
					}
					

					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto policy")) {
						prompt = "póliza de auto";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = "una póliza de casa";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = "una póliza de barco";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
						prompt = "una casa rodante o póliza de motocicleta";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
						prompt = "una vivienda especial o póliza de casa móvil";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an umbrella policy")) {
						prompt = "una póliza de paraguas";
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					}  

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Single Policy Scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

					strExitState = Constants.SU;
				}

				else if (!singleproducttype) {

					if(autoprodtypecount >= 1) {
						prompt = "an Auto policy"; 
					}
					if (homeprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Home Policy";
						}else {
							prompt = "a Home Policy";
						}
					}
					if (mrprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Boat Policy";
						}else {
							prompt = "a Boat Policy";
						}
					}
					if (rvprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Motor Home or Motorcycle Policy";
						}else {
							prompt = "a Motor Home or Motorcycle Policy";
						}
					}
					if (spprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Specialty Dwelling or Mobile Home Policy";
						}else {
							prompt = "a Specialty Dwelling or Mobile Home Policy";
						}
					}
					if (umbrellaprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " or an Umbrella Policy";
						}else {
							prompt = "an Umbrella Policy";
						}
					}
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						prompt = prompt.replace("an Auto policy"," una póliza de auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy"," una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace("a Home Policy","una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace(" a Boat Policy"," una póliza de barco");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace("a Boat Policy","una póliza de barco");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
						prompt = prompt.replace(" a Motor Home or Motorcycle Policy"," una casa rodante o póliza de motocicleta");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
						prompt = prompt.replace("a Motor Home or Motorcycle Policy","una casa rodante o póliza de motocicleta");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
						prompt = prompt.replace(" a Specialty Dwelling or Mobile Home Policy"," una vivienda especial o póliza de casa móvil");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
						prompt = prompt.replace("a Specialty Dwelling or Mobile Home Policy","una vivienda especial o póliza de casa móvil");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy"," o una póliza de paraguas");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace("an Umbrella Policy","una póliza de paraguas");
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Multiple Product Types Scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

					strExitState = Constants.SU;
				}

				else if (resultsObj.size() > 1 && singleproducttype) {
					if (lob.equalsIgnoreCase("RV")) {
						prompt = "Motor Home or Motorcycle";
					}
					else if (lob.equalsIgnoreCase("AUTO")) {
						prompt = "Auto";
					}
					else if (lob.equalsIgnoreCase("HOME")) {
						prompt = "Home";
					}
					else if (lob.equalsIgnoreCase("UMBRELLA")) {
						prompt = "Umbrella";
					}
					else if (lob.equalsIgnoreCase("MR")) {
						prompt = "Boat";
					}
					else if (lob.equalsIgnoreCase("SP")) {
						prompt = "Specialty Dwelling or Mobile Home";
					}	
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace("Auto"," auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace("Home"," casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat")) {
						prompt = prompt.replace("Boat"," barco");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("motor home or motorcycle")) {
						prompt = prompt.replace("Motor Home or Motorcycle"," una casa rodante o pmotocicleta");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("specialty dwelling or mobile home")) {
						prompt = prompt.replace("Specialty Dwelling or Mobile Home"," una vivienda especial o casa móvil");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella")) {
						prompt = prompt.replace("Umbrella"," paraguas");
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Single Product Type Multiple Policy scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));
					
					String policyProductCode = "A";	
					data.setSessionData("S_API_POLICYCODE_1", policyProductCode); //SingleFoundDoubleFoundTribleFound
					data.addToLog("S_API_POLICYCODE_1", policyProductCode);
					strExitState = Constants.SU;
				}
			}
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in AccLinkAniLookup method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;

				
				

			}
			

	public boolean checkifsingleproducttype(int rvprodtypecount, int autoprodtypecount, int homeprodtypecount, int umbrellaprodtypecount, int mrprodtypecount, int spprodtypecount) {
		int nonZeroCount = 0;
		if (rvprodtypecount != 0) {
			nonZeroCount++;
		}
		if (autoprodtypecount != 0) {
			nonZeroCount++;
		}
		if (homeprodtypecount != 0) {
			nonZeroCount++;
		}
		if (umbrellaprodtypecount != 0) {
			nonZeroCount++;
		}
		if (mrprodtypecount != 0) {
			nonZeroCount++;
		}
		if (spprodtypecount != 0) {
			nonZeroCount++;
		}
		// Return true only if there's exactly one non-zero element
		return nonZeroCount == 1;
	}
	private void apiResponseManupulation_AccountLinkANILookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject resp1 =(JSONObject) resp.get("responseBody");
			JSONArray agentsArr = (JSONArray)resp1.get("policies");
			JSONArray agentsArrs =(JSONArray)(((JSONObject) agentsArr.get(0)).get("addresses"));
			JSONArray insureddetails =(JSONArray)(((JSONObject) agentsArr.get(0)).get("insuredDetails"));
			String mdtDateOfBirth = (String)((JSONObject) insureddetails.get(0)).get("birthDate");
			String Zip = (String)((JSONObject) agentsArrs.get(0)).get("zip");
			data.setSessionData("S_API_DOB", mdtDateOfBirth);
			data.setSessionData("S_API_ZIP", Zip);
			data.addToLog("S_API_DOB", mdtDateOfBirth);
			data.addToLog("S_API_ZIP", Zip);
			if(agentsArr.size()==1)
			{
				JSONArray policyType00 = (JSONArray)(((JSONObject) agentsArr.get(0)).get("lineOfBusiness"));
				String policyProductCodes = policyType00.toString();
				data.setSessionData("S_API_POLICYCODE_1", policyProductCodes); //SingleFoundDoubleFoundTribleFound
				data.addToLog("S_API_POLICYCODE", policyProductCodes);
				data.setSessionData("S_API_POLICYFOUND", "SingleFound"); //SingleFoundDoubleFoundTribleFound
				data.addToLog("S_API_POLICYFOUND", "SingleFound");

			} else if(agentsArr.size()==2)
			{
				String policyNumber1 = (String)((JSONObject) agentsArr.get(0)).get("policyNumber");
				String policyNumber2 = (String)((JSONObject) agentsArr.get(1)).get("policyNumber");
				JSONArray policyType11 = (JSONArray)(((JSONObject) agentsArr.get(0)).get("lineOfBusiness"));
				String policyProductCode1 = policyType11.toString();
				JSONArray policyType22 = (JSONArray)(((JSONObject) agentsArr.get(1)).get("lineOfBusiness"));
				String policyProductCode2 = policyType22.toString();
				data.setSessionData("S_API_POLICYCODE_1", policyProductCode1); //SingleFoundDoubleFoundTribleFound
				data.addToLog("S_API_POLICYCODE", policyProductCode1);
				data.setSessionData("S_API_POLICYCODE_2", policyProductCode2); //SingleFoundDoubleFoundTribleFound
				data.addToLog("S_API_POLICYCODE2", policyProductCode2);
				if(policyNumber1.equalsIgnoreCase(policyNumber2))
				{
					data.setSessionData("S_API_POLICYFOUND", "TribleFound");
					data.addToLog("S_API_POLICYFOUND", "TribleFound");
				}
				data.setSessionData("S_API_POLICYFOUND", "DoubleFound");
				data.addToLog("S_API_POLICYFOUND", "DoubleFound");
					
			}else if(agentsArr.size()>2)
			{
				String policyNumber1 = (String)(((JSONObject) agentsArr.get(0)).get("policyNumber"));
				String policyNumber2 = (String)(((JSONObject) agentsArr.get(1)).get("policyNumber"));
				String policyNumber3 = (String)(((JSONObject) agentsArr.get(2)).get("policyNumber"));
				JSONArray policyType11 = (JSONArray)(((JSONObject) agentsArr.get(0)).get("lineOfBusiness"));
				String policyProductCode1 = policyType11.toString();
				JSONArray policyType22 = (JSONArray)(((JSONObject) agentsArr.get(1)).get("lineOfBusiness"));
				String policyProductCode2 = policyType22.toString();
				JSONArray policyType33 = (JSONArray)(((JSONObject) agentsArr.get(2)).get("lineOfBusiness"));
				String policyProductCode3 = policyType33.toString();
				data.setSessionData("S_API_POLICYCODE_1", policyProductCode1); //SingleFoundDoubleFoundTribleFound
				data.addToLog("S_API_POLICYCODE", policyProductCode1);
				data.setSessionData("S_API_POLICYCODE_2", policyProductCode2); //SingleFoundDoubleFoundTribleFound
				data.addToLog("S_API_POLICYCODE2", policyProductCode2);
				data.setSessionData("S_API_POLICYCODE_3", policyProductCode3); //SingleFoundDoubleFoundTribleFound
				data.addToLog("S_API_POLICYCODE3", policyProductCode3);
				
				if(policyNumber1.equalsIgnoreCase(policyNumber2)||policyNumber2.equalsIgnoreCase(policyNumber3)||policyNumber3.equalsIgnoreCase(policyNumber1))
				{
					data.setSessionData("S_API_POLICYFOUND", "TribleFound");
					data.addToLog("S_API_POLICYFOUND", "TribleFound");	
				}
				else {
					data.setSessionData("S_API_POLICYFOUND", "DoubleFound");
					data.addToLog("S_API_POLICYFOUND", "DoubleFound");
				}
			}
			else
			{
				data.setSessionData("S_API_POLICYFOUND", "DoubleFound");
				data.addToLog("S_API_POLICYFOUND", "DoubleFound");
			}
		} catch (Exception e) 
		{
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
	}
	
	private String fwsPolicyLookupNew(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		boolean startsWithAlphabets = false;
		String policyContractNumber = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL);
				data.addToLog(currElementName, " S_FWS_POLICYLOOKUP_URL : "+wsurl);
				if(null != data.getSessionData(Constants.S_POLICY_NUM) && !((String)data.getSessionData(Constants.S_POLICY_NUM)).isEmpty()) {
					policyContractNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
					startsWithAlphabets = Character.isLetter(policyContractNumber.charAt(0)); 

					if (startsWithAlphabets) {
						data.addToLog(currElementName, "The string starts with an alphabet.");
						policyContractNumber= policyContractNumber.substring(1,policyContractNumber.length());
			        	data.addToLog(currElementName, "Post Sub String :: " + policyContractNumber);
			        	data.setSessionData(Constants.S_POLICY_NUM, policyContractNumber);
			        } else {
			        	data.addToLog(currElementName, "The string does not start with an alphabet.");
			        }
				}
				String billingAccountNumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String telephoneNumber = (String) data.getSessionData(Constants.S_ANI);
				if(data.getSessionData(Constants.S_TELEPHONENUMBER) != null && !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
					telephoneNumber = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				}
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog(currElementName, "S_POLICY_NUM : "+policyContractNumber+" :: S_BILLING_ACC_NUM : "+billingAccountNumber+" :: S_ANI : "+telephoneNumber);
				Lookupcall lookups = new Lookupcall();
				org.json.simple.JSONObject responses = null;
				//UAT ENV CHANGE START
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				 region = regionDetails.get(Constants.S_FWS_POLICYLOOKUP_URL);
				}else {
					region="PROD";
				}
				
				if(policyContractNumber != null && !policyContractNumber.isEmpty()) {
					responses = lookups.GetFWSPolicyLookup(wsurl,tid, policyContractNumber,null,null,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				} else if(billingAccountNumber != null && !billingAccountNumber.equals("")){
					 responses = lookups.GetFWSPolicyLookup(wsurl,tid, null,billingAccountNumber,null,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				}else {
					 responses = lookups.GetFWSPolicyLookup(wsurl,tid, null,null,telephoneNumber,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				}
				data.addToLog("responses", responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

				if(responses != null) 
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_002 : FWSPolicyLookup API Response into session with the key name of "+currElementName+Constants._RESP);
						JSONObject respbody=(JSONObject) responses.get(Constants.RESPONSE_BODY);
						//Zip Code fix 
						if(!respbody.containsKey("transactionNotification")) {
							data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
							data.addToLog(currElementName, "Set SIDA_MN_005_VALUE_RESP: "+"SIDA_MN_005_VALUE_RESP");
						}
						
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));						
						strExitState = apiResponseManupulation_FwsPolicyLookupNew(data, caa, currElementName, strRespBody);
						if(Constants.SU.equalsIgnoreCase(strExitState)) {
							data.setSessionData(Constants.S_BU, "FWS_SVC");
							data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
							data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_NO);
							data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
							data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
							data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
							
							String menuSelectionKey = (String) data.getSessionData(Constants.S_MENU_SELCTION_KEY);
							//This check condition has been added because LOB can change to FWS in Shared ID Auth if Original BU is FDS
							if (null != menuSelectionKey && !menuSelectionKey.isEmpty() && menuSelectionKey.contains("FARMERS") && null != data.getSessionData(Constants.S_BU) && Constants.FWS.equalsIgnoreCase((String) data.getSessionData(Constants.S_BU)) && null != data.getSessionData("S_FLAG_FWS_BU") && Constants.STRING_YES.equalsIgnoreCase((String) data.getSessionData("S_FLAG_FWS_BU"))) {
								
								if(menuSelectionKey.startsWith("NLU")) {
									data.setSessionData(Constants.S_CATEGORY, "FWS_SVC");
									int colonIndex = menuSelectionKey.indexOf(':');

							        String result = "";
							        if (colonIndex != -1) {
							            // Preserve the part before colon and append the new string after colon
							            result = menuSelectionKey.substring(0, colonIndex + 1) + "FWS_SVC";
							        }
							        data.setSessionData(Constants.S_MENU_SELCTION_KEY, result);
							        data.addToLog(currElementName, "Replacing MSP From FARMERS to FWS Since BU switched in Shared ID & Auth :: New MSP ::  " +result);
								}else {
									menuSelectionKey = menuSelectionKey.replaceFirst("FARMERS", "FWS");
								   data.setSessionData(Constants.S_MENU_SELCTION_KEY, menuSelectionKey);
								   data.addToLog(currElementName, "Replacing MSP From FARMERS to FWS Since BU switched in Shared ID & Auth :: New MSP :: " +menuSelectionKey);
								}
								menuSelectionKey = menuSelectionKey.replaceFirst("FARMERS", "FWS");
								data.setSessionData(Constants.S_MENU_SELCTION_KEY, menuSelectionKey);
								data.addToLog(currElementName, "Replacing MSP From FARMERS to FWS Since BU switched in Shared ID & Auth :: New MSP :: " +menuSelectionKey);
							}
							//Replacing all the previously formed MSPs (BU - FARMERS) with the new BU(FWS)
							Queue<String> MSPQueue = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
							data.addToLog(currElementName, "MSP Queue Before BU was switched :: " +MSPQueue);
							
							Queue<String> tempQueue = new LinkedList<>();
							if (null != MSPQueue) {
								Iterator<String> iterator = MSPQueue.iterator();
						        data.addToLog(currElementName, "Iterating through the current Queue while replacing old BU (FARMERS) to new BU (FWS)");
						        while (iterator.hasNext()) { 
						        	data.setSessionData(Constants.S_CATEGORY, "FWS_SVC");
						        	data.addToLog("New Category code: ", (String)data.getSessionData(Constants.S_CATEGORY));
						        	String oldMSPEntry= iterator.next();
						        	data.addToLog(currElementName, "Old MSP entry " +oldMSPEntry);
						            //String tempKey = oldMSPEntry.replaceFirst("FARMERS", "FWS");
						            
						          	 String tempKey="";
							        	if(oldMSPEntry.startsWith("NLU")) {
											int colonIndex = oldMSPEntry.indexOf(':');
									       
									        if (colonIndex != -1) {
									            // Preserve the part before colon and append the new string after colon
									        	tempKey = oldMSPEntry.substring(0, colonIndex + 1) + "FWS_SVC";
									        }
										}else {
											tempKey = oldMSPEntry.replaceFirst("FARMERS", "FWS");
										}
						            data.addToLog(currElementName, "New MSP entry :: " +tempKey);
						            tempQueue.add(tempKey);
						        }
						        MSPQueue = tempQueue;
						        data.setSessionData("S_LAST_5_MSP_VALUES", MSPQueue);
						        data.addToLog(currElementName, "New MSP Queue post replacing the Old BU with New one :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));
							}
						}
					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
					
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_HOST_002  FWSPolicyLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		try {
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE) && (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName,"FWSPolicyLookup API call", strReqBody, region,(String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL));
				objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				objHostDetails.startHostReport(currElementName,"FWSPolicyLookup API call", strReqBody, region,(String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for FWSPolicyLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
	}
	
	private String apiResponseManupulation_FwsPolicyLookupNew(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {			
		String strExitState = Constants.ER;
		try {
			
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray policiesArr = (JSONArray)resp.get("policies");
		
			String lob = null;
			String prompt = Constants.EmptyString;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int mrprodtypecount = 0;
			boolean singleproducttype = false;
			
			if (policiesArr != null && policiesArr.size() == 1) {
				JSONObject policyData = (JSONObject) policiesArr.get(0);
				if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
					lob = (String) policyData.get("lineOfBusiness");
					data.setSessionData("S_LOB", lob);
		}
		}
			
			if(policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String,PolicyBean>>();
				for(int i = 0; i < policiesArr.size(); i++) {
					PolicyBean beanObj = new PolicyBean();
					String strPolicyNum = Constants.EmptyString;
					JSONObject policyData = (JSONObject) policiesArr.get(i);
					
					if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
						lob = (String) policyData.get("lineOfBusiness");
						data.setSessionData(Constants.S_FWS_POLICY_LOB, lob);
						beanObj.setStrPolicyLOB(lob);
						if (productTypeCounts.containsKey(lob)) productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						else productTypeCounts.put(lob, 1);
					}
					String zip = "";
					if (policyData.containsKey("addresses") && null != policyData.get("addresses")) {
						JSONArray addressesArr = (JSONArray)policyData.get("addresses");
						JSONObject addressesObj = (JSONObject) addressesArr.get(0);
						if (addressesObj.containsKey("zip")) {
							zip = zip.equals("")?""+addressesObj.get("zip").toString().substring(0, 5) : zip+","+addressesObj.get("zip").toString().substring(0, 5);
							//zip = (String) addressesObj.get("zip");
						}
						beanObj.setStrZipCode(zip);
						if (addressesObj.containsKey("state")) beanObj.setStrPolicyState((String)addressesObj.get("state"));
					}
					if (policyData.containsKey("insuredDetails")) {
						
						String strDOB = "";
						JSONArray insuredDetailsArr = (JSONArray)policyData.get("insuredDetails");
						for(int k =0;k<insuredDetailsArr.size();k++) {
							JSONObject obj =(JSONObject)insuredDetailsArr.get(k);
							strDOB = (strDOB.equals(""))?""+obj.get("birthDate"):strDOB+","+obj.get("birthDate");
						}
						beanObj.setStrDOB(strDOB);
						
						JSONObject insuredDetailsObj =(JSONObject)insuredDetailsArr.get(0);
						if(insuredDetailsObj.containsKey("firstName") && null != insuredDetailsObj.get("firstName")) beanObj.setStrFirstName((String)insuredDetailsObj.get("firstName"));
						if(insuredDetailsObj.containsKey("lastName") && null != insuredDetailsObj.get("lastName")) beanObj.setStrLastName((String)insuredDetailsObj.get("lastName"));
					}
					if (policyData.containsKey("policyNumber") && null != policyData.get("policyNumber")) {
						strPolicyNum = (String) policyData.get("policyNumber");
						beanObj.setStrPolicyNum(strPolicyNum);
					}
					
					if(policyData.containsKey("policySource") && null != policyData.get("policySource")) beanObj.setStrPolicySource((String)policyData.get("policySource"));
					if(policyData.containsKey("policyState") && null != policyData.get("policyState")) beanObj.setStrPolicyState((String)policyData.get("policyState"));
					if(policyData.containsKey("suffix") && null != policyData.get("suffix")) beanObj.setStrPolicySuffix((String)policyData.get("suffix"));
					if(policyData.containsKey("effectiveDate") && null != policyData.get("effectiveDate")) beanObj.setStrEffectiveDate((String)policyData.get("effectiveDate"));
					if(policyData.containsKey("InternalPolicyVersion") && null != policyData.get("InternalPolicyVersion")) beanObj.setStrInternalPolicyNumber((String)policyData.get("InternalPolicyVersion"));
					if(policyData.containsKey("InternalPolicyNumber") && null != policyData.get("InternalPolicyNumber")) beanObj.setStrInternalPolicyVersion((String)policyData.get("InternalPolicyNumber"));
					if(policyData.containsKey("billingAccountNumber") && null != policyData.get("billingAccountNumber")) {
						String tmpBillingAccNum = (String)policyData.get("billingAccountNumber");
						if("ARS".equalsIgnoreCase(beanObj.getStrPolicySource()) && tmpBillingAccNum.length() > 9) {
							beanObj.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj.setStrBillingAccountNumber(tmpBillingAccNum.substring(2, tmpBillingAccNum.length()));
						} else {
							beanObj.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj.setStrBillingAccountNumber(tmpBillingAccNum.substring(0, tmpBillingAccNum.length()));
						}
					}
					
					String tmpKey = Constants.EmptyString;
					switch (lob) {
					case Constants.PRODUCTTYPE_Y:
						tmpKey = Constants.MR_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_H:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_F:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_A:
						tmpKey = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_U:
						tmpKey = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
						break;
					default:
						break;
					}
					if(productPolicyMap.containsKey(tmpKey)) {
						HashMap<String, PolicyBean> tmpPolicyDetails = productPolicyMap.get(tmpKey);
						tmpPolicyDetails.put(strPolicyNum, beanObj);
						productPolicyMap.put(tmpKey, tmpPolicyDetails);
					} else {
						HashMap<String, PolicyBean> policyDetails = new HashMap<String, PolicyBean>();
						policyDetails.put(strPolicyNum, beanObj);
						productPolicyMap.put(tmpKey, policyDetails);
					}
				}
				data.setSessionData(Constants.S_POLICYDETAILS_MAP, productPolicyMap);
				data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);
				data.addToLog(currElementName, "policyDetails Hashmap : "+productPolicyMap);

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_Y)) {
					mrprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_Y);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_H)) {
					homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_H);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_F)) {
					homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_F);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_A)) {
					autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_A);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_U)) {
					umbrellaprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_U);
				}

				data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC, autoprodtypecount);
				data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC, homeprodtypecount);
				data.setSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC, umbrellaprodtypecount);
				data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC, mrprodtypecount);
				

				data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Home Product Type Count  = "+data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Umbrella Product Type Count = "+data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Boat Product Type Count  = "+data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(0, autoprodtypecount, homeprodtypecount, umbrellaprodtypecount, mrprodtypecount, 0);
				data.addToLog(currElementName, "Check if single Product : "+singleproducttype);

				if (policiesArr.size() == 1) {
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+Constants.S_SINGLE_POLICY_FOUND);
					for (String key : productPolicyMap.keySet()) {
						HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap.get(key);
						for (String key1 : policyDetailsMap.keySet()) {
							PolicyBean obj = (PolicyBean) policyDetailsMap.get(key1);
							data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
							data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
							
							data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", lob);
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
						}
					}
					strExitState = Constants.SU;
				} else if (!singleproducttype) {
					if(autoprodtypecount >= 1) prompt = prompt+" an Auto policy"; 
					if (homeprodtypecount >= 1) prompt = prompt+" a Home Policy"; 
					if (mrprodtypecount >= 1) prompt = prompt+" a Boat Policy"; 
					if (umbrellaprodtypecount >= 1) prompt = prompt+" or an Umbrella Policy"; 
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						prompt = prompt.replace(" an Auto policy"," una póliza de auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy"," una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy"," o una póliza de paraguas");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace(" a Boat Policy"," una póliza de barco");
					}
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 
					

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Multiple Product Types Scenario");
					data.addToLog(currElementName,"Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
					
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));
					
					strExitState = Constants.SU;
				} else if (policiesArr.size() > 1 && singleproducttype) {
					String produtType = "";
					if(autoprodtypecount >= 1) {
						prompt = " Auto "; 
						produtType = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
					}
					else if (homeprodtypecount >= 1) {
						prompt = " Home "; 
						produtType = Constants.HOME_PRODUCTTYPECOUNT_KYC;
					}
					else if (umbrellaprodtypecount >= 1) {
						prompt = " an Umbrella ";
						produtType = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
					}
					else if(mrprodtypecount >= 1) {
						prompt = " Boat ";
						produtType = Constants.MR_PRODUCTTYPECOUNT_KYC;
					}
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace(" Auto "," auto ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace(" Home "," casa ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella")) {
						prompt = prompt.replace(" an Umbrella "," paraguas ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat")) {
						prompt = prompt.replace(" Boat "," barco ");
					}
					
					HashMap<String, PolicyBean> policyDetailsMap = new HashMap<String, PolicyBean>();
					for (String key : productPolicyMap.keySet()) {
						policyDetailsMap = productPolicyMap.get(key);
					}
					data.setSessionData(Constants.POLICY_DETAILS_FOR_MN3, policyDetailsMap);
					
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 
					
					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
					
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));
					
					strExitState = Constants.SU;
				}
			} else {
				strExitState = Constants.ER;
			}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in AccLinkAniLookup method  :: "+e);
		caa.printStackTrace(e);
	}
	return strExitState;
}
	
	public static void main(String[] args) {
		 Queue<String> MSPQueue = new LinkedList<>();
		 MSPQueue.add("FARMERS:OSPM_MN_001:POLICY ASSISTANCE");
		 MSPQueue.add("FARMERS:OSPM_MN_005:POLICY YES");
		 MSPQueue.add("FARMERS:OSPM_MN_005:POLICY YES");

		 
		 Queue<String> tempReplacement = new LinkedList<>();
		 
		 
	        // Iterate using iterator
	        Iterator<String> iterator = MSPQueue.iterator();
	        while (iterator.hasNext()) { 
	            String tempKey = iterator.next().replaceFirst("FARMERS", "FWS");
	            tempReplacement.add(tempKey);
	        }
	        
	        MSPQueue = tempReplacement;

	        System.out.println(MSPQueue);
	}
	
}
