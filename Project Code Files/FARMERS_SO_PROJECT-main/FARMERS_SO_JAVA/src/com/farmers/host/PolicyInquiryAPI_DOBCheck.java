package com.farmers.host;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.FWSPolicyLookup_Post;
import com.farmers.FarmersAPI.ForemostPolicyInquiry_Post;
import com.farmers.FarmersAPI.MulesoftFarmerPolicyInquiry_Post;
import com.farmers.FarmersAPI.PointPolicyInquiry_Get;
import com.farmers.FarmersAPI.PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post;
import com.farmers.FarmersAPI_NP.FWSPolicyLookup_NP_Post;
import com.farmers.FarmersAPI_NP.ForemostPolicyInquiry_NP_Post;
import com.farmers.FarmersAPI_NP.MulesoftFarmerPolicyInquiry_NP_Post;
import com.farmers.FarmersAPI_NP.PointPolicyInquiry_NP_Get;
import com.farmers.FarmersAPI_NP.PolicyInquiry_RetrieveInsurancePoliciesbyParty_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class PolicyInquiryAPI_DOBCheck extends DecisionElementBase {
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		try {
			String strBU = (String) data.getSessionData(Constants.S_FINAL_BRAND);
			data.addToLog(currElementName, "Brand Auth : Selected Policy Brand - "+strBU);
			if(strBU!=null)
				strBU = strBU.trim();
			switch (strBU) {
			case Constants.S_API_BU_BW:
				data.addToLog(currElementName,":: BW Execution  :: ");
				data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
				StrExitState = policyInquiry_RetriveInsurancePoliciesByParty(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
				data.addToLog("code came out of API method", "");
				break;
			case Constants.S_API_BU_FDS_PLA:
				data.addToLog(currElementName,":: FDS Execution  :: ");
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
				StrExitState = mulesoftFarmerPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
				break;
			case Constants.S_API_BU_FDS_GWPC:
				data.addToLog(currElementName,":: GWPC Execution  :: ");
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
				StrExitState = mulesoftFarmerPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
				break;
			case Constants.S_API_BU_FM:
				data.addToLog(currElementName,":: FM Execution  :: ");
				data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
				StrExitState = foremostPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
				break;
			case Constants.S_API_BU_FWS_ARS:
				data.addToLog(currElementName,":: FWS Execution  :: ");
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
				StrExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
				break;
			case Constants.S_API_BU_FWS_A360:
				data.addToLog(currElementName,":: FWS Execution  :: ");
				data.addToLog(currElementName,":: A360 Execution  :: ");
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
				StrExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
				break;
			case Constants.S_API_BU_21C:
				data.addToLog(currElementName,":: Auto Execution  :: ");
				data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
				StrExitState = PointPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
				break;
			default:
				break;
			}
			data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host details for KYCBA_HOST_001  :: "+e);
			caa.printStackTrace(e);
		}

//		try {
//			objHostDetails.startHostReport(currElementName,"KYCBA_HOST_001", strReqBody,region,url);
//			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU);
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception while forming host reporting for KYCBA_HOST_001  PolicyInquiryAPI call  :: "+e);
//			caa.printStackTrace(e);
//		}

		/*
		String hostMSPEndKey = Constants.EmptyString;
		if(StrExitState.equalsIgnoreCase(Constants.ER)) hostMSPEndKey = Constants.API_FAILURE;
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":KYCBA_HOST_001:"+hostMSPEndKey);
		data.addToLog(currElementName,"S_MENU_SELCTION_KEY for  KYCBA_HOST_001 :: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));
		 */
		return StrExitState;
}
	private String policyInquiry_RetriveInsurancePoliciesByParty(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String url=null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			data.addToLog("code reached API method", "");

			if(data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				data.addToLog("code reached inside if condition", "");
				 url = (String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String telephoneNum = (String) data.getSessionData(Constants.S_ANI);
				//String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				//UAT ENV CHANGE START (PRIYA ,SHAIK)
				PolicyInquiry_RetrieveInsurancePoliciesbyParty_NP_Post objNP=new PolicyInquiry_RetrieveInsurancePoliciesbyParty_NP_Post();
				
				PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post obj = new PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post();
				
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				JSONObject resp=null ;
				
				if("YES".equalsIgnoreCase(UAT_FLAG))
				{
					String Key =Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL;
					 region = regionDetails.get(Key);
					
					data.addToLog("Region for UAT endpoint: ", region);
					resp =  objNP.start(url, callerId, telephoneNum, conTimeout, readTimeout, context,region);		
				}else {
					region="PROD";
					resp =   obj.start(url, callerId, telephoneNum, conTimeout, readTimeout, context);
				}
                //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
				data.addToLog(currElementName, "KYCBA_HOST_001 : policyInquiry_RetriveInsurancePoliciesByParty API response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : policyInquiry_RetriveInsurancePoliciesByParty �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						//bypassing DOB change
						data.setSessionData("S_POLICYAPI_RESPONSE", strRespBody);
					    //bypassing dob change
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						//CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", resp.get(Constants.RESPONSE_BODY));
						
						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
						
						
						apiResponseManupulation_RetriveInsurancePoliciesByParty(data, caa, currElementName, strRespBody);
						StrExitState = Constants.SU;
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  RetrieveIntermediarySegmentationInfo API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName,"RetrieveIntermediarySegmentationInfo API call", strReqBody, region,(String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}

		return StrExitState;


	}

	private String mulesoftFarmerPolicyInquiry(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String url=null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				 url = (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String policycontractnumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String billingaccountnumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				//String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
				String telephonenumber = (String) data.getSessionData(Constants.S_ANI);

				data.addToLog(currElementName, "KYCBA_HOST_001 : policycontractnumber :"+policycontractnumber);
				data.addToLog(currElementName, "KYCBA_HOST_001 : billingaccountnumber :"+billingaccountnumber);
				if (null != policycontractnumber) {
					billingaccountnumber = null;
					telephonenumber = null;
				}else if (null!= billingaccountnumber) {
					telephonenumber = null;
				}
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				MulesoftFarmerPolicyInquiry_Post obj = new MulesoftFarmerPolicyInquiry_Post();
				
				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				MulesoftFarmerPolicyInquiry_NP_Post objNP = new MulesoftFarmerPolicyInquiry_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp =  objNP.start(url, callerId, policycontractnumber, billingaccountnumber, telephonenumber, conTimeout, readTimeout, context, region);
				}else {
					region="PROD";
				    resp =  obj.start(url, callerId, policycontractnumber, billingaccountnumber, telephonenumber, conTimeout, readTimeout, context);
				}
               //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
				data.addToLog(currElementName, "KYCBA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						//bypassing DOB change
						data.setSessionData("S_POLICYAPI_RESPONSE", strRespBody);
					    //bypassing dob change
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						//CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", resp.get(Constants.RESPONSE_BODY));
						
						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
						
						apiResponseManupulation_MulesoftFarmerPolicyInquiry(data, caa, currElementName, strRespBody);
						StrExitState = Constants.SU;
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  MulesoftFarmerPolicyInquiry_Post API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName,"MulesoftFarmerPolicyInquiry_Post API call", strReqBody,region, (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

	private String foremostPolicyInquiry(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String url=null;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
			if(data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				 url = (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String policynumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				//String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
				SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy");
				String systemdate = SDF.format(new Date());
				//String policysource = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
				String policysource = "Foremost";
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT)); 
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				ForemostPolicyInquiry_Post obj = new ForemostPolicyInquiry_Post();
				
				//UAT ENV CHANGE START(PRIYA,SHAIK)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				ForemostPolicyInquiry_NP_Post objNP=new ForemostPolicyInquiry_NP_Post();
				JSONObject resp =null;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_FOREMOST_POLICYINQUIIRY_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, callerId, policynumber, systemdate, policysource, conTimeout, readTimeout, context,region);
				}else
				{
					region="PROD";
					resp =   obj.start(url, callerId, policynumber, systemdate, policysource, conTimeout, readTimeout, context);
				}
				data.addToLog(currElementName, "KYCBA_HOST_001 : ForemostPolicyInquiry �PI response  :"+resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : ForemostPolicyInquiry �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						//bypassing DOB change
						data.setSessionData("S_POLICYAPI_RESPONSE", strRespBody);
					    //bypassing dob change
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						//CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", resp.get(Constants.RESPONSE_BODY));
						
						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
						
						apiResponseManupulation_ForemostPolicyInquiry(data, caa, currElementName, strRespBody);
						StrExitState = Constants.SU;
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  ForemostPolicyInquiry API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName,"ForemostPolicyInquiry API call", strReqBody,region, (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

	private String fwsPolicyLookup(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String url=null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)

		try {
			if(data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				 url = (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String policycontractnumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String billingaccountnumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
//				String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
				String telephonenumber = (String) data.getSessionData(Constants.S_ANI);

				if (null != policycontractnumber) {
					billingaccountnumber = null;
					telephonenumber = null;
				}else if (null!= billingaccountnumber) {
					telephonenumber = null;
				}

				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				FWSPolicyLookup_NP_Post objNP=new FWSPolicyLookup_NP_Post();
				FWSPolicyLookup_Post obj = new FWSPolicyLookup_Post();
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG))
				{
					String Key =Constants.S_FWS_POLICYLOOKUP_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, callerId, policycontractnumber, billingaccountnumber,  telephonenumber, conTimeout, readTimeout, context,region);
				}
					else {
						region="PROD";
						resp =  obj.start(url, callerId, policycontractnumber, billingaccountnumber,  telephonenumber, conTimeout, readTimeout, context);
					}
				 //END- UAT ENV SETUP CODE (PRIYA, SHAIK);
				data.addToLog(currElementName, "KYCBA_HOST_001 : FWSPolicyLookup �PI response  :"+resp);
			
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : FWSPolicyLookup �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						//bypassing DOB change
						data.setSessionData("S_POLICYAPI_RESPONSE", strRespBody);
					    //bypassing dob change
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						//CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", resp.get(Constants.RESPONSE_BODY));
						
						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
						
						apiResponseManupulation_FwsPolicyLookup(data, caa, currElementName, strRespBody);
						StrExitState = Constants.SU;
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  FWSPolicyLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"FWSPolicyLookup API call", strReqBody,region, (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

	private String PointPolicyInquiry(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String url=null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		
		try {
			if(data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				 url = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
				String input = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				//String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
				if (url!= null && url.contains("S_POLICY_NUM")) {
					url.replaceAll("S_POLICY_NUM", input);
				}
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				PointPolicyInquiry_Get obj = new PointPolicyInquiry_Get();

				//Non prod changes-Priya
				PointPolicyInquiry_NP_Get objNP=new PointPolicyInquiry_NP_Get();
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }

               JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG))
				{
					String Key =Constants.S_POINT_POLICYINQUIIRY_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp =  objNP.start(url, callerId, conTimeout, readTimeout, context,region);
				}
				else
				{
					region="PROD";
					resp =  obj.start(url, callerId, conTimeout, readTimeout, context);
				}
				
				//Non prod changes-Priya
				data.addToLog(currElementName, "KYCBA_HOST_001 : PointPolicyInquiry �PI response  : "+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : PointPolicyInquiry �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						//bypassing DOB change
						data.setSessionData("S_POLICYAPI_RESPONSE", strRespBody);
					    //bypassing dob change
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						//CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", resp.get(Constants.RESPONSE_BODY));
						
						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
						
						apiResponseManupulation_PointPolicyInquiry(data, caa, currElementName, strRespBody);
						StrExitState = Constants.SU;
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  PointPolicyInquiry API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"PointPolicyInquiry API call", strReqBody,region, (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}
//**********************************************Response Manipulation Code to check if DOB available****************************************
	private void apiResponseManupulation_RetriveInsurancePoliciesByParty(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			data.addToLog(currElementName, "BW - Policy Lookup Resp : "+resp);
			JSONObject finalpolicyobj = null;
			JSONObject household = new JSONObject();
			JSONArray autopolicyArray = new JSONArray();
			
			JSONObject policyobj = new JSONObject();
			JSONObject namedinsured = new JSONObject();
			JSONObject agentdetails = new JSONObject();
			JSONObject basicpolicy = new JSONObject();
			JSONObject basicpolicydetails = new JSONObject();
			String policyDOB = null;
			String policymodnumber = Constants.EmptyString;
			String policysymbol = Constants.EmptyString;
			String policynumber = Constants.EmptyString;
			String agentAORid = Constants.EmptyString;
			data.setSessionData("DOB_CHECK", "NO");
			data.addToLog("DOB_CHECK before response manipulation: ",data.getSessionData("DOB_CHECK").toString());
			if (null != resp && !resp.isEmpty() && resp.containsKey("household")) {
				household = (JSONObject) resp.get("household");
				data.setSessionData("DOB_CHECK", "NO");
				if (null != household && !household.isEmpty() && household.containsKey("autoPolicies")) {
					autopolicyArray = (JSONArray) household.get("autoPolicies");
					
					if (null != autopolicyArray && autopolicyArray.size() > 0) {
						for(Object objPolicies : autopolicyArray) {

							policyobj = (JSONObject) objPolicies;
							
							
							if (null != policyobj && !policyobj.isEmpty() && policyobj.containsKey("namedInsured")) {
								namedinsured = (JSONObject) policyobj.get("namedInsured");
								
								if (null != namedinsured && !namedinsured.isEmpty() && namedinsured.containsKey("birthDate")) {
									policyDOB = (String) namedinsured.get("birthDate");
									data.addToLog(currElementName, "API DOB :: "+policyDOB);
									data.setSessionData("DOB_CHECK", "YES");
									
								}
								
							}
						}
					
					}
					else {
						data.addToLog(currElementName, "AutoPolicy Array does not contain data :: "+resp);
					}
				}
				else {
					data.addToLog(currElementName, "Household object does not contain AutoPolicies key :: "+resp);
				}
			}
			else {
				data.addToLog(currElementName, "BW API Response does not contain household key :: "+resp);
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog("DOB_CHECK before response manipulation: ",data.getSessionData("DOB_CHECK").toString());
	}

	private void apiResponseManupulation_MulesoftFarmerPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			data.addToLog(currElementName, "Farmers - Policy Lookup Resp : "+resp);
			
			if (null != resp && !resp.isEmpty() && resp.containsKey("policies")) {
				
				JSONArray policiesArr = (JSONArray)resp.get("policies");
				JSONObject policydata = new JSONObject();
				JSONArray insuredDetailsArray = new JSONArray();
				JSONObject insuredDetails = new JSONObject();
				String agentAORid = Constants.EmptyString;
				String policyDOB = "";
				String overrideEmail = null;
				String email = null;
				data.setSessionData("DOB_CHECK", "NO");
				data.addToLog("DOB_CHECK before response manipulation: ",data.getSessionData("DOB_CHECK").toString());
				if (null != policiesArr && !policiesArr.isEmpty() && policiesArr.size()>0) {
					
					for(Object policyArrIterator : policiesArr) {
						policydata = (JSONObject) policyArrIterator;
						
						if (null != policydata && !policydata.isEmpty()) {
							//Start CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
							String gpcCode = (String)policydata.get("GPC");
							 String payPlan = (String)policydata.get("payPlan");
							 String servicingPhoneNumber = (String)policydata.get("servicingPhoneNumber");
							 data.setSessionData("S_FDS_GPC", gpcCode);
							 data.setSessionData("S_FDS_PAYPLAN", payPlan);
							 data.setSessionData("S_FDS_SERVICING_PHONE_NUMBER", servicingPhoneNumber);
						     data.addToLog(currElementName, "GPC code for FDS PayrollDeduct ::"+gpcCode);
						     data.addToLog(currElementName, "Pay Plan for FDS PayrollDeduct ::"+payPlan);
						     data.addToLog(currElementName, "Servicing Phone Number for FDS PayrollDeduct ::"+servicingPhoneNumber);
						     //End CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
							if (policydata.containsKey("insuredDetails")) {
								
								insuredDetailsArray = (JSONArray) policydata.get("insuredDetails");
								
								if (null != insuredDetailsArray && insuredDetailsArray.size()>0) {
									
									for(Object insuredDetailsArrayIterator : insuredDetailsArray) {
										insuredDetails = (JSONObject) insuredDetailsArrayIterator;
										data.addToLog(currElementName, "Insured Details Object :: "+insuredDetails);										
										if (null != insuredDetails) {
											
											if (insuredDetails.containsKey("birthDate")) {
												policyDOB = (String) insuredDetails.get("birthDate").toString().trim();
												data.addToLog(currElementName, "Date Received from API :: "+policyDOB);
												
												data.setSessionData("DOB_CHECK", "YES");
												
											}
										}
									}
								}
							}
							else {
								data.addToLog(currElementName, "Policy Object does not contain insured Details Array :: "+policydata);
							}
						}
					}
					
				}
				
				else {
					data.addToLog(currElementName, "Policies Array is either null or empty :: "+resp);
				}
				
			}
			else {
				data.addToLog(currElementName, "Mulesoft Response does not contain policies Array :: "+resp);
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog("DOB_CHECK after response manipulation: ",data.getSessionData("DOB_CHECK").toString());
	}

	private void apiResponseManupulation_ForemostPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject policydata = new JSONObject();
			JSONObject policyobj = new JSONObject();
			String month = Constants.EmptyString;
		    String day = Constants.EmptyString;
		    String year = Constants.EmptyString;
			String agentAORid = Constants.EmptyString;
			String policyDOB = Constants.EmptyString;
			data.addToLog(currElementName, "Foremost - Policy Lookup Resp : "+resp);
			String productcode = null;
			String policynumber = null;
			data.setSessionData("DOB_CHECK", "NO");
			data.addToLog("DOB_CHECK before response manipulation: ",data.getSessionData("DOB_CHECK").toString());
			if (null != resp && !resp.isEmpty() && resp.containsKey("policies")) {
				JSONArray policiesArr = (JSONArray)resp.get("policies");
				
				if(policiesArr !=null && policiesArr.size()>0) {
					
					for (Object PolicyData : policiesArr) {
						policydata = (JSONObject) PolicyData;
						
						if (null != policydata && !policydata.isEmpty() && policydata.containsKey("insureds")) {
							
							JSONArray insuredDetailsArr = (JSONArray) policydata.get("insureds");
							
							if(insuredDetailsArr!=null && insuredDetailsArr.size()>0) {
								
								for(Object objPolicies : insuredDetailsArr) {
									policyobj = (JSONObject) objPolicies;
									
									if (null != policyobj && policyobj.containsKey("birthDate")) {
										policyDOB = (String) policyobj.get("birthDate");
										
										SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
										SimpleDateFormat resultSDF = new SimpleDateFormat("yyyy-MM-dd");
										policyDOB = resultSDF.format(inputSDF.parse(policyDOB));
								       
								        data.setSessionData("DOB_CHECK", "YES");
								       
									}
								}

							}
							else {
								data.addToLog(currElementName, "Insured Details Array either null or empty :: "+policydata);
							}
						}
					}
				}
				else {
					data.addToLog(currElementName, "Policies Array Either null or empty or does not contain insured details :: ");
				}
			}
			else {
				data.addToLog(currElementName, "Foremost API Response Either null or empty :: "+resp);
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog("DOB_CHECK after response manipulation: ",data.getSessionData("DOB_CHECK").toString());
	}

	private void apiResponseManupulation_FwsPolicyLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			data.addToLog(currElementName, "FWS - Policy Lookup Resp : "+resp);
			String LOB = (String) data.getSessionData("S_LOB");
			data.setSessionData("DOB_CHECK", "NO");
			data.addToLog("DOB_CHECK before response manipulation: ",data.getSessionData("DOB_CHECK").toString());
			if (null != resp && !resp.isEmpty() && resp.containsKey("policies")) {
				JSONArray policies = (JSONArray)resp.get("policies");
				String policyDOB = Constants.EmptyString;
				JSONArray insuredDetailsArr = new JSONArray();
				JSONObject insuredDetailsData = new JSONObject();
				String policysource = Constants.EmptyString;
				String lob = Constants.EmptyString;
				String billingaccountnumber = Constants.EmptyString;
				String suffix = Constants.EmptyString;
				String effectivedate = Constants.EmptyString;
				String policystate = Constants.EmptyString;
				String firstname = Constants.EmptyString;
				String lastname = Constants.EmptyString;
				
				if(policies !=null && policies.size()>0) {
					
					for (Object policydata : policies) {
						JSONObject policyobj = (JSONObject) policydata;
						
						if (null != policyobj && policyobj.containsKey("insuredDetails")) {
							insuredDetailsArr = (JSONArray) policyobj.get("insuredDetails");
							
							for (Object insuredDetailsobj : insuredDetailsArr) {
								insuredDetailsData = (JSONObject) insuredDetailsobj;
								if (null != insuredDetailsData && insuredDetailsData.containsKey("birthDate")) {
								policyDOB = (String) insuredDetailsData.get("birthDate");
								data.addToLog(currElementName, "Date Received from API :: "+policyDOB);
								data.setSessionData("DOB_CHECK", "YES");
						        
								}
							}
							

						}
						else {
							data.addToLog(currElementName, "Policy object from Policy Array does not contain Insured Details :: "+policies);
						}
					}
				}
				else {
					data.addToLog(currElementName, "Policy Array Empty : No Policies Found :: "+resp);
				}
				
			}
			else {
				data.addToLog(currElementName, "FWS API Response Either Null or empty :: "+resp);
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog("DOB_CHECK before response manipulation: ",data.getSessionData("DOB_CHECK").toString());
	}

	private void apiResponseManupulation_PointPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			String mastercompanycode = Constants.EmptyString, policySource = Constants.EmptyString, email = null, overrideEmail = null;
			data.addToLog(currElementName, "21C - Policy Lookup Resp : "+resp);
			data.setSessionData("DOB_CHECK","NO");
			data.addToLog("DOB_CHECK before response manipulation: ",data.getSessionData("DOB_CHECK").toString());
			if (null != resp && !resp.isEmpty()) {
				JSONObject policysummary = (JSONObject) resp.get("policySummary");
				if (null != policysummary && !policysummary.isEmpty()) {
					
					JSONObject autopolicy = (JSONObject) policysummary.get("autoPolicy");
					JSONArray drivers = (JSONArray) autopolicy.get("drivers");
					JSONObject policyobj = new JSONObject();
					String policyDOB = null;

					for(Object objPolicies : drivers) {
						policyobj = (JSONObject) objPolicies;
						if(policyobj!=null && policyobj.containsKey("dateofbirth") ) {
						policyDOB = (String) policyobj.get("dateofbirth");
						data.setSessionData("DOB_CHECK","YES");
						}												
					}


				}
				else {
					data.addToLog(currElementName, "Policy Summary Oject is Empty : "+resp);
				}
			}
			else {
				data.addToLog(currElementName, "21st API Response either null or Empty :: "+resp);
			}
			
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		
		data.addToLog("DOB_CHECK before response manipulation: ",data.getSessionData("DOB_CHECK").toString());
	}
	
	private void setBWSegmentation(CommonAPIAccess caa, DecisionElementData data, String segmentationValue) {
		String strKeyName = Constants.EmptyString;
		try {
			
			String bwOther = (String) data.getSessionData("S_BW_OTHER");
			String bw44 = (String) data.getSessionData("S_BW_44");
			String bwCMMLOther = (String) data.getSessionData("S_BW_CMML_OTHER");
			String bwCMML44 = (String) data.getSessionData("S_BW_CMML_44");
			
			if(bwOther.toUpperCase().contains(segmentationValue.toUpperCase())) {
				strKeyName = "BW_OTHER";
			}else if(bw44.toUpperCase().contains(segmentationValue.toUpperCase())) {
				strKeyName = "BW_44";
			}else if(bwCMMLOther.toUpperCase().contains(segmentationValue.toUpperCase())) {
				strKeyName = "BW_CMML_OTHER";
			}else if(bwCMML44.toUpperCase().contains(segmentationValue.toUpperCase())) {
				strKeyName = "BW_CMML_44";
			}
			
			
			data.addToLog(data.getCurrentElement(), "BW Segmenation Value : "+segmentationValue+" Segmenation key : "+strKeyName);
			if(strKeyName != null && !strKeyName.equals("")) {
				data.addToLog(data.getCurrentElement(), "Set policy segmentation into session  "+Constants.S_POLICY_SEGMENTATION+" : "+strKeyName);
				data.setSessionData(Constants.S_POLICY_SEGMENTATION, strKeyName);
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception while fetching BW Segmentation "+e);
			caa.printStackTrace(e);
		}
	}

	//CS1151307 : Update Policy State Name
		public String setStateName(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strPolicyState) {
		    
			String strExitState = Constants.EMPTY;
		 
		    try {
		        // Get state name from session
		        strExitState = (String) data.getSessionData(Constants.S_STATENAME);
		        data.addToLog("Caller State Name :::::", strExitState);

		        // Get state code and policy state code from session
		        String strStateCode = (String) data.getSessionData(Constants.S_STATECODE);
		        String strStateValue = (String) data.getSessionData(Constants.S_POLICY_STATE_CODE);

		        // Check if state code and policy state code are not null
		        if (strStateCode != null && strStateValue != null) {
		            data.addToLog(currElementName, "strStateCode : " + strStateCode);
		            data.addToLog(currElementName, "strPolicyStateCode :: " + strStateValue);

		            // Check if state code matches policy state code
		            if (strStateCode.equalsIgnoreCase(strStateValue)) {
		                strExitState = (String) data.getSessionData(Constants.S_STATENAME);
		                data.addToLog(currElementName, "State Name : " + strExitState);
		    
		            } else {
		                // Retrieve configuration state code from session data
		                String strConfigState = (String) data.getSessionData("S_CONFIG_STATE_CODE");
		                data.addToLog(currElementName, "Config State Code :::: " + strConfigState);

		                // Initialize a map to store state codes and their corresponding values
		                HashMap<String, String> stateMap = new HashMap<>();

		                // Split the configuration state code string and populate the map
		                if (strConfigState != null) {
		                    for (String strObject : strConfigState.split("\\|")) {
		                        data.addToLog(currElementName, "State Code Object ::::: " + strObject);
		                        String[] keyValue = strObject.split(":");
		                        if (keyValue.length == 2) {
		                            stateMap.put(keyValue[0], keyValue[1]);
		                        } else {
		                            data.addToLog(currElementName, "Invalid State Code Object :: " + strObject);
		                        }
		                    }

		                    // Check if the state map contains the state value
		                    if (stateMap.containsValue(strStateValue)) {
		                        for (Map.Entry<String, String> entry : stateMap.entrySet()) {
		                            String key = entry.getKey();
		                            String value = entry.getValue();

		                            // If the value matches the state value, update the session data
		                            if (value.equalsIgnoreCase(strStateValue)) {
		                                data.setSessionData(Constants.S_POLICY_STATE_CODE, strStateValue);
		                                data.setSessionData(Constants.S_STATENAME, key);
		                                strExitState = (String) data.getSessionData(Constants.S_STATENAME);
		                                data.addToLog(currElementName, "Caller State Name after verification :" + strExitState);
		                                break; // Exit loop after first match
		                            }
		                        }
		                    } else {
		                        data.addToLog(currElementName, "State Value not found in the config state map");
		                    }
		                } else {
		                    data.addToLog(currElementName, "Config State Code is null");
		                }
		            }
		        } else {
		            data.addToLog(currElementName, "State Code or Policy State Code is null");
		        }
		    } catch (Exception e) {
		        data.addToLog(currElementName, "Exception in processConfigState :: " + e.getMessage());
		        e.printStackTrace();
		    }
		    return strExitState;
		}
}
