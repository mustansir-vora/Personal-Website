package com.farmers.host;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.json.JsonConfiguration;
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
import com.farmers.bean.PolicyBean;
import com.farmers.bean.PointerIDCard.PolicyRewriteOfferAmount;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*KYCBA_HOST_001*/
public class PolicyInquiryAPI extends DecisionElementBase {

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
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {

			if(data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CALLER_INPUT_DOB) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String telephoneNum = (String) data.getSessionData(Constants.S_ANI);
				String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post obj = new PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post();
				PolicyInquiry_RetrieveInsurancePoliciesbyParty_NP_Post objNP = new PolicyInquiry_RetrieveInsurancePoliciesbyParty_NP_Post();
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
				data.addToLog(currElementName, "KYCBA_HOST_001 : policyInquiry_RetriveInsurancePoliciesByParty API response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : policyInquiry_RetriveInsurancePoliciesByParty �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						apiResponseManupulation_RetriveInsurancePoliciesByParty(data, caa, currElementName, strRespBody, callerenteredDOB);
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
			objHostDetails.startHostReport(currElementName,"RetrieveIntermediarySegmentationInfo API call", strRespBody,region, (String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}

		return StrExitState;


	}

	private String mulesoftFarmerPolicyInquiry(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String policycontractnumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String billingaccountnumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
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
               //END- UAT ENV SETUP CODE (PRIYA, SHAIK)				data.addToLog(currElementName, "KYCBA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						apiResponseManupulation_MulesoftFarmerPolicyInquiry(data, caa, currElementName, strRespBody,callerenteredDOB);
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
			objHostDetails.startHostReport(currElementName,"MulesoftFarmerPolicyInquiry_Post API call", strRespBody,region, (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

	private String foremostPolicyInquiry(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String policynumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
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
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						apiResponseManupulation_ForemostPolicyInquiry(data, caa, currElementName, strRespBody,callerenteredDOB);
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
			objHostDetails.startHostReport(currElementName,"ForemostPolicyInquiry API call", strRespBody,region, (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

	private String fwsPolicyLookup(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String policycontractnumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String billingaccountnumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
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
				 //END- UAT ENV SETUP CODE (PRIYA, SHAIK);data.addToLog(currElementName, "KYCBA_HOST_001 : FWSPolicyLookup �PI response  :"+resp);
				data.addToLog(currElementName, "KYCBA_HOST_001 : FWSPolicyLookup �PI response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : FWSPolicyLookup �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						apiResponseManupulation_FwsPolicyLookup(data, caa, currElementName, strRespBody,callerenteredDOB);
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
			objHostDetails.startHostReport(currElementName,"FWSPolicyLookup API call", strRespBody,region, (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

	private String PointPolicyInquiry(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
				String input = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String callerenteredDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
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
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", resp.get(Constants.RESPONSE_BODY));
						apiResponseManupulation_PointPolicyInquiry(data, caa, currElementName, strRespBody, callerenteredDOB);
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
			objHostDetails.startHostReport(currElementName,"PointPolicyInquiry API call", strRespBody,region, (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}

	private void apiResponseManupulation_RetriveInsurancePoliciesByParty(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, String callerenteredDOB) {
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
			
			if (null != resp && !resp.isEmpty() && resp.containsKey("household")) {
				household = (JSONObject) resp.get("household");
				
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
								}
								
								if (null != callerenteredDOB && null != policyDOB && callerenteredDOB.equalsIgnoreCase(policyDOB)) {
									
									if (null != namedinsured && namedinsured.containsKey("birthName")) {
										JSONObject birthNameObject = (JSONObject) namedinsured.get("birthName");
										
										if (null != birthNameObject) {
											if (birthNameObject.containsKey("firstName")) {
												String firstname = (String) birthNameObject.get("firstName").toString().trim();
												data.setSessionData(Constants.S_FIRST_NAME, firstname);
											}
											if (birthNameObject.containsKey("lastName")) {
												String lastname = (String) birthNameObject.get("lastName").toString().trim();
												data.setSessionData(Constants.S_LAST_NAME, lastname);
											}
										}
									}
									else {
										data.addToLog(currElementName, "namedinsured Object does not contain birthName Object :: "+namedinsured);
									}
									
									if (null != policyobj && policyobj.containsKey("insuredVehicle")) {
										JSONObject insuredvehicleObject = (JSONObject) policyobj.get("insuredVehicle");
										
										if (null != insuredvehicleObject && insuredvehicleObject.containsKey("garagingAddress")) {
											JSONObject garagingaddressObject = (JSONObject) insuredvehicleObject.get("garagingAddress");
											
											if (null != garagingaddressObject && garagingaddressObject.containsKey("postalCode")) {
												String zip = (String) garagingaddressObject.get("postalCode").toString().trim();
												if (null != zip) {
													zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
												}
												data.setSessionData(Constants.S_PAYOR_ZIP_CODE, zip);
											}
										}
									}
									
									if (null != policyobj && !policyobj.isEmpty() && policyobj.containsKey("agentDetails")) {
										agentdetails = (JSONObject) policyobj.get("agentDetails");
										if (null != agentdetails && !agentdetails.isEmpty() && agentdetails.containsKey("agentCode")) {
											agentAORid = (String) agentdetails.get("agentCode").toString().trim();
											data.addToLog(currElementName, "Agent AOR ID :: "+agentAORid);
										}
									}
									
									if (null != policyobj && !policyobj.isEmpty() && policyobj.containsKey("basicPolicy")) {
										basicpolicy = (JSONObject) policyobj.get("basicPolicy");
										if (null != basicpolicy && !basicpolicy.isEmpty()) {
											policymodnumber = (String) basicpolicy.get("policyModNumber").toString().trim();
											policynumber = (String) basicpolicy.get("policyNumber").toString().trim();
											data.setSessionData("POLICY_MOD", policymodnumber);
											data.setSessionData("S_BW_POLICYNUM", policynumber);
											data.addToLog(currElementName, "POLICY_MOD :: "+data.getSessionData("POLICY_MOD"));
											data.addToLog(currElementName, "S_POLICY_NUM :: "+ data.getSessionData("S_POLICY_NUM"));
										}
									}
									else {
										data.addToLog(currElementName, "PolicyMod Number & Policy Number Not Available in BW API Response :: "+resp);
									}
									
									if (null != policyobj && !policyobj.isEmpty() && policyobj.containsKey("basicPolicyDetail")) {
										basicpolicydetails = (JSONObject) policyobj.get("basicPolicyDetail");
										if (null != basicpolicydetails && !basicpolicydetails.isEmpty() && basicpolicydetails.containsKey("policySymbol")) {
											policysymbol = (String) basicpolicydetails.get("policySymbol").toString().trim();
											if(basicpolicydetails.containsKey("insurerCompanyCode")) {
												String strInsurerCompanyCode = (String) basicpolicydetails.get("insurerCompanyCode").toString().trim();
												data.setSessionData("BW_COMPANYCODE", strInsurerCompanyCode);
												setBWSegmentation(caa, data, policysymbol+"-"+strInsurerCompanyCode);
											}
											data.setSessionData("POLICY_SYMBOL", policysymbol);
											data.addToLog(currElementName, "POLICY_SYMBOL :: "+data.getSessionData("POLICY_SYMBOL"));
											data.setSessionData("S_EPC_PAYMENT_POLICYNUM", policysymbol + policynumber + data.getSessionData("POLICY_MOD"));
											data.setSessionData(Constants.S_FULL_POLICY_NUM, policysymbol+""+policynumber);
										}
									}
									else {
										data.addToLog(currElementName, "PolicySymbol Not available in BW Policy Lookup Response :: "+resp);
									}
									if (!policysymbol.isEmpty() && !policynumber.isEmpty() && !policymodnumber.isEmpty()) {
										String epcpaymentusid = policysymbol + policynumber + policymodnumber;
										data.setSessionData("S_EPC_PAYMENT_POLICYNUM", epcpaymentusid);
									}
									
									finalpolicyobj = policyobj;
									data.setSessionData("S_AGENT_ID", agentAORid);
									data.setSessionData(Constants.S_API_DOB, policyDOB);
									data.setSessionData(Constants.S_FINAL_POLICY_OBJ, finalpolicyobj);
									data.setSessionData(currElementName+Constants._RESP, finalpolicyobj);
									data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
									data.setSessionData(Constants.S_CATEGORY,"FM_AUTO");
									data.setSessionData(Constants.S_BU, "Foremost Auto");
									data.setSessionData(Constants.S_LOB, "Foremost Auto");
									data.addToLog(currElementName, "User has selected BW Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
									
									data.addToLog(currElementName, "Final Policy Object Set into session After KYC Auth is Successful ::"+data.getSessionData(Constants.S_FINAL_POLICY_OBJ));
									data.addToLog(currElementName, "S_FLAG_BW_BU :: "+data.getSessionData("S_FLAG_BW_BU"));
									data.addToLog(currElementName, "Overriding Business Unit After Successful Auth :: "+data.getSessionData(Constants.S_BU));
								}
							}
						}
						
						if (null != data.getSessionData("S_AGENT_ID") && data.getSessionData("S_AGENT_ID").equals("")) {
							data.addToLog(currElementName, "Agent AOR ID not found :: Fetching 1st (DEFAULT) Object in Response");
						}
						if (null != data.getSessionData(Constants.S_API_DOB) && data.getSessionData(Constants.S_API_DOB).equals("")) {
							data.addToLog(currElementName, "API DOB did not match User Entered DOB :: Fetching 1st (DEFAULT) Object in Response");
						}

						if (finalpolicyobj == null) {
							if (null != autopolicyArray && autopolicyArray.size() > 0) {
								policyobj = (JSONObject) autopolicyArray.get(0);
								if (null != policyobj && policyobj.containsKey("namedInsured")) {
									namedinsured = (JSONObject) policyobj.get("namedInsured");
									if (null != namedinsured && namedinsured.containsKey("birthDate")) {
										policyDOB = (String) namedinsured.get("birthDate");
										data.setSessionData(Constants.S_API_DOB, policyDOB);
									}
									else {
										data.addToLog(currElementName, "DOB not available in API response");
									}
									if (null != policyobj && policyobj.containsKey("agentDetails")) {
										agentdetails = (JSONObject) policyobj.get("agentDetails");
										if (null != agentdetails && agentdetails.containsKey("agentCode")) {
											agentAORid = (String) agentdetails.get("agentCode");
											data.setSessionData("S_AGENT_ID", agentAORid);
										}
										else {
											data.addToLog(currElementName, "Agent AOR ID Not available in API Response");
										}
									}
								}
								data.setSessionData(currElementName+Constants._RESP, policyobj);
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
	}

	private void apiResponseManupulation_MulesoftFarmerPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody,String callerenteredDOB) {
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
				
				String apiSubcategory = Constants.EmptyString;
				String strsubcategory = Constants.EmptyString;
				String strpayPlan=Constants.EmptyString;
				PolicyBean beanObj = new PolicyBean();
				
				
				if (null != policiesArr && !policiesArr.isEmpty() && policiesArr.size()>0) {
					
					for(Object policyArrIterator : policiesArr) {
						policydata = (JSONObject) policyArrIterator;
						
						if (null != policydata && !policydata.isEmpty()) {
							
							data.addToLog(data.getCurrentElement(), "Setting Session details for routing");
							if (policydata.containsKey("policySource")) {
								String strPolicySource = (String) policydata.get("policySource");
								data.addToLog(data.getCurrentElement(), "Set policy source into session  "+Constants.S_POLICY_SOURCE+" : "+strPolicySource);
								data.setSessionData(Constants.S_POLICY_SOURCE, strPolicySource);
							}
							if (policydata.containsKey("GPC")) {
								String strPolicyGPC = (String) policydata.get("GPC");
								String fasPolicySeg = (String) data.getSessionData("S_FDS_GPC_CA_HOME");
								data.addToLog(data.getCurrentElement(), "GPC : "+strPolicyGPC);
								data.addToLog(data.getCurrentElement(), "POLICY SEGMENTATION : "+fasPolicySeg);
								if(strPolicyGPC != null && !strPolicyGPC.equals("") && fasPolicySeg != null && !fasPolicySeg.equals("") && fasPolicySeg.contains(strPolicyGPC)) {
									data.addToLog(data.getCurrentElement(), "Set policy segmena into session  "+Constants.S_POLICY_SEGMENTATION+" : FDS_GPC_CA_HOME");
									data.setSessionData(Constants.S_POLICY_SEGMENTATION, "FDS_GPC_CA_HOME");
								}
							}
							
							//START : CS1265959 : Farmers Insurance | US | FDS - Payroll Deduct indicator & routing                 
							if (policydata.get("payPlan") != null &&  policydata.containsKey("payPlan")) {
								strpayPlan = (String) policydata.get("payPlan");
								if("PB".equalsIgnoreCase(strpayPlan)||"PA".equalsIgnoreCase(strpayPlan)) {
								data.addToLog(currElementName, "S_API_PAYPLAN : " + (String) policydata.get("payPlan"));
								//Start CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
								data.setSessionData("S_FDS_PAYROLL_DEDUCT", "Y");
								//End CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
							    data.setSessionData(Constants.S_POLICY_ATTRIBUTES, "Payroll Deduct");
							    data.addToLog(currElementName, "POLICY ATTRIBUTES : " + data.getSessionData(Constants.S_POLICY_ATTRIBUTES));
								}
							}

							// END : CS1265959 : Farmers Insurance | US | FDS - Payroll Deduct  indicator & routing
							// START : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add Routing for 'True Direct' HouseAccountSubCategor
							if (policydata.containsKey("subCategory") && policydata.get("subCategory") != null &&
									!(("PB".equalsIgnoreCase((String) policydata.get("payPlan"))) || ("PA".equalsIgnoreCase((String) policydata.get("payPlan"))))) {
								apiSubcategory = (String) policydata.get("subCategory");
								beanObj.setStrsubCategory(apiSubcategory);
								data.addToLog(currElementName, "apiSubcategory :: " + apiSubcategory);
								data.setSessionData("S_API_SUBCATEGORY", apiSubcategory);						
								strsubcategory=setSubcategory(data, caa, currElementName, apiSubcategory);
					            data.addToLog(currElementName, "Subcategory : "+strsubcategory);
								if(strsubcategory.equalsIgnoreCase(apiSubcategory)) {
									data.setSessionData(Constants.S_POLICY_ATTRIBUTES, apiSubcategory);
									data.addToLog(currElementName, "S_API_SUBCATEGORY : "+apiSubcategory);
									}
								}
							if (policydata.containsKey("agentReference") && !strsubcategory.equalsIgnoreCase(apiSubcategory) &&
									!(("PB".equalsIgnoreCase((String) policydata.get("payPlan"))) || ("PA".equalsIgnoreCase((String) policydata.get("payPlan"))))) {
									agentAORid = (String) policydata.get("agentReference");
									beanObj.setStrAgentAORID(agentAORid);
									data.addToLog(currElementName, "AOR ID :: " + agentAORid);
									data.setSessionData("S_AGENT_ID", agentAORid);	
									String policyAttributeCode = agentAORid.substring(2,4);
									String policyAtttribute = "";
									if("99".equalsIgnoreCase(policyAttributeCode)) {
										policyAtttribute = "AOR99";
									}
										data.addToLog(data.getCurrentElement(), "Set Policy Attribute into session  "+Constants.S_POLICY_ATTRIBUTES+" : "+policyAtttribute);
										data.setSessionData(Constants.S_POLICY_ATTRIBUTES, policyAtttribute);
								}
							//END : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add Routing for 'True Direct' HouseAccountSubCategor
							
							if (policydata.containsKey("billingAccountNumber") && null != policydata.get("billingAccountNumber")) {
								data.setSessionData("S_EPC_PAYMENT_POLICYNUM", policydata.get("billingAccountNumber").toString().trim());
							}
							
							if (policydata.containsKey("insuredDetails")) {
								
								insuredDetailsArray = (JSONArray) policydata.get("insuredDetails");
								
								if (null != insuredDetailsArray && insuredDetailsArray.size()>0) {
									
									for(Object insuredDetailsArrayIterator : insuredDetailsArray) {
										insuredDetails = (JSONObject) insuredDetailsArrayIterator;
										data.addToLog(currElementName, "Insured Details Object :: "+insuredDetails);
										
										if (null != insuredDetails) {
											
											if (insuredDetails.containsKey("basicElectronicAddress")) {
												email = (String) insuredDetails.get("basicElectronicAddress");
												
												if (null != email && !email.isEmpty() && null == overrideEmail) {
													data.setSessionData(Constants.S_EMAIL, email.trim());
													data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
													data.addToLog(currElementName, "EMAIL_AVAILABLE :: YES :: EMAIL SET INTO SESSION :: " +email);
												}
											}
											
											if (insuredDetails.containsKey("birthDate")) {
												policyDOB = (String) insuredDetails.get("birthDate").toString().trim();
												data.addToLog(currElementName, "Date Received from API :: "+policyDOB);
												
												if (callerenteredDOB.equalsIgnoreCase(policyDOB)) {
													
													data.setSessionData(Constants.S_API_DOB, policyDOB);
													data.setSessionData(Constants.S_FINAL_POLICY_OBJ, policydata);
													data.setSessionData(currElementName+Constants._RESP, policydata);
													data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
													data.setSessionData(Constants.S_BU, "Farmers");
													data.setSessionData(Constants.S_CATEGORY,"FarmersSRV");
													data.setSessionData(Constants.S_LOB, "Farmers");
													data.addToLog(currElementName, "Setting Date from API into session :: "+ data.getSessionData("S_API_DOB"));
													data.addToLog(currElementName, "Final Policy Object Set into session After KYC Auth is Successful ::"+data.getSessionData(Constants.S_FINAL_POLICY_OBJ));
													data.addToLog(currElementName, "S_FLAG_FDS_BU :: "+data.getSessionData("S_FLAG_FDS_BU"));
													data.addToLog(currElementName, "User has selected Farmers Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
													data.addToLog(currElementName, "Overriding Business Unit After Successful Auth :: "+data.getSessionData(Constants.S_BU));
													
													
													
													if (policydata.containsKey("agentReference")) {
														agentAORid = (String) policydata.get("agentReference").toString().trim();
														data.setSessionData("S_AGENT_ID", agentAORid);
														data.addToLog(currElementName, "Agent AOR ID :: "+agentAORid);
													}
													
													if (insuredDetails.containsKey("firstName")) {
														String firstname = (String) insuredDetails.get("firstName").toString().trim();
														data.setSessionData(Constants.S_FIRST_NAME, firstname);
													}
													if (insuredDetails.containsKey("lastName")) {
														String lastname = (String) insuredDetails.get("lastName").toString().trim();
														data.setSessionData(Constants.S_LAST_NAME, lastname);
													}
													if (insuredDetails.containsKey("basicElectronicAddress")) {
														overrideEmail = (String) insuredDetails.get("basicElectronicAddress").toString().trim();
														if (null != overrideEmail && !overrideEmail.isEmpty()) {
															data.setSessionData(Constants.S_EMAIL, overrideEmail);
															data.addToLog(currElementName, "OVERRIDING EMAIL WHICH MATCHED WITH DOB INTO SESSION :: " +overrideEmail);
														}
													}
												}
											}
										}
									}
								}
							}
							else {
								data.addToLog(currElementName, "Policy Object does not contain insured Details Array :: "+policydata);
							}
							if (policydata.containsKey("address")) {
								JSONArray addressArray = (JSONArray) policydata.get("address");
								
								if (null != addressArray && addressArray.size() > 0) {
									JSONObject addressobj = (JSONObject) addressArray.get(0);
									
									if (null != addressobj) {
										if (addressobj.containsKey("zip")) {
											String zip = (String) addressobj.get("zip").toString().trim();
											if (null != zip) {
												zip = zip.length() > 5 ? zip.substring(0, 5) : zip; 
											}
											data.setSessionData(Constants.S_PAYOR_ZIP_CODE, zip);
										}
										if (addressobj.containsKey("city")) {
											String city = (String) addressobj.get("city").toString().trim();
											data.setSessionData(Constants.S_CITY, city);
										}
										if (addressobj.containsKey("state")) {
											String state = (String) addressobj.get("state").toString().trim();
											data.setSessionData(Constants.S_STATE, state);
											data.setSessionData(Constants.S_POLICY_STATE_CODE, state);
											
											//CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName : Hawaii)
											data.addToLog(currElementName, "Policy State Code :" + state);
											String strPolicyStateName=setStateName(data, caa, currElementName, state);
											data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);

											
										}
										if (addressobj.containsKey("streetAddress1")) {
											String address = (String) addressobj.get("streetAddress1").toString().trim();
											data.setSessionData(Constants.S_LINE1, address);
										}
										if (addressobj.containsKey("country")) {
											String country = (String) addressobj.get("country").toString().trim();
											data.setSessionData(Constants.S_COUNTRY, country);
										}
									}
								}
							}
							else {
								data.addToLog(currElementName, "Policy Object does not contain Address Object :: "+policydata);
							}
						}
					}
					
					if (null == data.getSessionData(Constants.S_API_DOB)) {
						policydata = (JSONObject) policiesArr.get(0);
						if (null != policydata && policydata.containsKey("insuredDetails")) {
							insuredDetailsArray = (JSONArray) policydata.get(0);
							insuredDetails = (JSONObject) insuredDetailsArray.get(0);
							
							if (null != insuredDetails && insuredDetails.containsKey("birthDate")) {
								policyDOB = (String) insuredDetails.get("birthDate");
								data.setSessionData(Constants.S_API_DOB, policyDOB);
								data.setSessionData(Constants.S_FINAL_POLICY_OBJ, policydata);
								data.setSessionData(currElementName+Constants._RESP, policydata);
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
	}

	private void apiResponseManupulation_ForemostPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody,String callerenteredDOB) {
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
								
										if (callerenteredDOB.equalsIgnoreCase(policyDOB)) {
											data.setSessionData(Constants.S_API_DOB, policyDOB);
											data.addToLog(currElementName, "Date Match : API Date Recieved :: "+policyDOB);
											data.setSessionData(currElementName+Constants._RESP, policydata);
											data.setSessionData(Constants.S_FINAL_POLICY_OBJ, policydata);
											data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
											data.setSessionData(Constants.S_BU, "Foremost");
											data.setSessionData(Constants.S_CATEGORY,"FM-SLS-SRV");
											data.setSessionData(Constants.S_LOB, "Foremost");
											
											if (policydata.containsKey("policyNumber")) {
												policynumber = (String) policydata.get("policyNumber");
												data.addToLog(currElementName, "Foremost Product Code Received from API :: " + productcode);
											}
											if (policydata.containsKey("policyProductCode")) {
												productcode = (String) policydata.get("policyProductCode");
												if (null != productcode && productcode.length() == 2) {
													productcode = "0"+productcode;
													data.addToLog(currElementName, "Foremost Product Code is 2 digits :: Adding zero as Prefix :: Product code Post manipulation :: " + productcode);
												}
												data.setSessionData("POLICY_PRODUCT_CODE", productcode);
												data.addToLog(currElementName, "Setting Foremost Product Code Into Session Post Manipulation :: " + data.getSessionData("POLICY_PRODUCT_CODE"));
											}
											
											//Set EPC PAYMENTUS ID
											if (null != policynumber && null != productcode) {
													data.setSessionData("S_EPC_PAYMENT_POLICYNUM", productcode + policynumber);
													data.addToLog(currElementName, "Setting EPC PaymentUS ID into session :: "+data.getSessionData("S_EPC_PAYMENT_POLICYNUM"));
											}
											
											data.addToLog(currElementName, "Final Policy Object Set into session After KYC Auth is Successful ::"+data.getSessionData(Constants.S_FINAL_POLICY_OBJ));
											data.addToLog(currElementName, "S_FLAG_FOREMOST_BU :: "+data.getSessionData("S_FLAG_FOREMOST_BU"));
											data.addToLog(currElementName, "User has selected Foremost Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
											data.addToLog(currElementName, "Overriding Business Unit After Successful Auth :: "+data.getSessionData(Constants.S_BU));
											
											if (policydata.containsKey("drivers")) {
												JSONArray driversArr = (JSONArray) policydata.get("drivers");
												
												if (null != driversArr && driversArr.size() > 0) {
													JSONObject driverObject = (JSONObject) driversArr.get(0);
													
													if (null != driverObject && driverObject.containsKey("name")) {
														JSONObject name = (JSONObject) driverObject.get("name");
														
														if (null != name) {
															if (name.containsKey("firstName")) {
																String firstname = (String) name.get("firstName").toString().trim();
																data.setSessionData(Constants.S_FIRST_NAME, firstname);
															}
															if (name.containsKey("lastName")) {
																String lastname = (String) name.get("lastName").toString().trim();
																data.setSessionData(Constants.S_LAST_NAME, lastname);
															}
														}
													}
												}
											}
											if (policydata.containsKey("addresses")) {
												JSONArray addressesArr = (JSONArray) policydata.get("addresses");
												
												if (null != addressesArr && addressesArr.size() > 0) {
													JSONObject addressObj = (JSONObject) addressesArr.get(0);
													
													if (addressObj.containsKey("zip")) {
														String zip = (String) addressObj.get("zip").toString().trim();
														if (null != zip) {
															zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
														}
														data.setSessionData(Constants.S_PAYOR_ZIP_CODE, zip);
													}
													if (addressObj.containsKey("city")) {
														String city = (String) addressObj.get("city").toString().trim();
														data.setSessionData(Constants.S_CITY, city);
													}
													if (addressObj.containsKey("state")) {
														String state = (String) addressObj.get("state").toString().trim();
														data.setSessionData(Constants.S_STATE, state);
														data.setSessionData(Constants.S_POLICY_STATE_CODE, state);
													
														//CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName : Hawaii)
														data.addToLog(currElementName, "Policy State Code :" + state);
														String strPolicyStateName=setStateName(data, caa, currElementName, state);
														data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);
			
													}
													if (addressObj.containsKey("streetAddress1")) {
														String address = (String) addressObj.get("streetAddress1").toString().trim();
														data.setSessionData(Constants.S_LINE1, address);
													}
													if (addressObj.containsKey("country")) {
														String country = (String) addressObj.get("country").toString().trim();
														data.setSessionData(Constants.S_COUNTRY, country);
													}
												}
											}
											if (policydata.containsKey("emailAddresses")) {
												JSONArray emailaddressArr = (JSONArray) policydata.get("emailAddresses");
												
												if (null != emailaddressArr && emailaddressArr.size() > 0) {
													JSONObject emailaddressObject = (JSONObject) emailaddressArr.get(0);
													
													if (null != emailaddressObject && emailaddressObject.containsKey("emailAddress")) {
														String email = (String) emailaddressObject.get("emailAddress").toString().trim();
														data.setSessionData(Constants.S_EMAIL, email);
													}
												}
											}
										}
									}
								}
								
								if (null == data.getSessionData(Constants.S_API_DOB)) {
									for (Object objpolicies : insuredDetailsArr) {
										if (null != insuredDetailsArr && insuredDetailsArr.size() > 0) {
											policyobj = (JSONObject) objpolicies;
											if (null != policyobj && policyobj.containsKey("birthDate")) {
												policyDOB = (String) policyobj.get("birthDate");
												
												SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
												SimpleDateFormat resultSDF = new SimpleDateFormat("yyyy-MM-dd");
												policyDOB = resultSDF.format(inputSDF.parse(policyDOB));
												
												data.setSessionData(Constants.S_API_DOB, policyDOB);
												data.addToLog(currElementName, "Date didn't Match : API Date Recieved :: "+policyDOB);
											}
										}
									else {
										data.addToLog(currElementName, "insured Details either null or empty :: "+policyobj);
									}
									}
									
									data.setSessionData(currElementName+Constants._RESP, policydata);
									data.setSessionData(Constants.S_FINAL_POLICY_OBJ, policydata);
								}
							}
							else {
								data.addToLog(currElementName, "Insured Details Array either null or empty :: "+policydata);
							}
						}
						if (null != policydata && !policydata.isEmpty() && policydata.containsKey("agentOfRecordId")) {
							agentAORid = (String) policydata.get("agentOfRecordId");
							data.setSessionData("S_AGENT_ID", agentAORid);
						}
						else {
							data.addToLog(currElementName, "Agent AOR ID Not available in API response :: "+policydata);
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
	}

	private void apiResponseManupulation_FwsPolicyLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody,String callerenteredDOB) {
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			data.addToLog(currElementName, "FWS - Policy Lookup Resp : "+resp);
			String LOB = (String) data.getSessionData("S_LOB");
			
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
				PolicyBean beanObj=new PolicyBean();
				
				if(policies !=null && policies.size()>0) {
					
					for (Object policydata : policies) {
						JSONObject policyobj = (JSONObject) policydata;
						
						if (null != policyobj && policyobj.containsKey("policySource")) {
							policysource = (String) policyobj.get("policySource");
							data.setSessionData(Constants.S_POLICY_SOURCE, policysource);
							data.addToLog(currElementName, "S_POLICY_SOURCE :: "+data.getSessionData(Constants.S_POLICY_SOURCE));
						}
						
						if (null != policyobj && policyobj.containsKey("insuredDetails")) {
							insuredDetailsArr = (JSONArray) policyobj.get("insuredDetails");
							
							for (Object insuredDetailsobj : insuredDetailsArr) {
								insuredDetailsData = (JSONObject) insuredDetailsobj;
								
								firstname = (String) insuredDetailsData.get("firstName");
								lastname = (String) insuredDetailsData.get("lastName");
								data.setSessionData(Constants.S_FIRST_NAME, firstname);
								data.setSessionData(Constants.S_LAST_NAME, lastname);
								
								//START : CS1175316 Farmers Insurance | US | Add Emailed ID Card Self-Service
								if(insuredDetailsData.containsKey("email") && null != insuredDetailsData.get("email"));
								String email=(String) insuredDetailsData.get("email");
								
								if (null != email && !email.isEmpty()) {
									beanObj.setStrEmail(email);							
									data.setSessionData(Constants.S_EMAIL, email);
									data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
									data.addToLog(currElementName, "Is Email available"+beanObj.getStrEmail());
								}
								else {
									data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_NO);
									data.addToLog(currElementName, "Is Email not available");
								}
								//END : CS1175316 Farmers Insurance | US | Add Emailed ID Card Self-Service
								
								policyDOB = (String) insuredDetailsData.get("birthDate");
								data.addToLog(currElementName, "Date Received from API :: "+policyDOB);
								if (!policysource.isEmpty() && policysource.equalsIgnoreCase("ARS")) {
									data.addToLog(currElementName, "Date before conversion :: "+policyDOB);
									SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
									SimpleDateFormat resultSDF = new SimpleDateFormat("yyyy-MM-dd");
									policyDOB = resultSDF.format(inputSDF.parse(policyDOB));
									data.addToLog(currElementName, "Date After Conversion :: "+policyDOB);
								}
								if (null != callerenteredDOB && callerenteredDOB.equalsIgnoreCase(policyDOB)) {
									
									if (policyobj.containsKey("lineOfBusiness")) {
										lob = (String) policyobj.get("lineOfBusiness");
										lob = lob.trim();
										data.setSessionData(Constants.S_FWS_POLICY_LOB, lob);
										data.addToLog(currElementName, "S_FWS_POLICY_LOB :: "+data.getSessionData(Constants.S_FWS_POLICY_LOB));
									}
									
									if (policyobj.containsKey("billingAccountNumber")) {
										billingaccountnumber = (String) policyobj.get("billingAccountNumber");
										billingaccountnumber = billingaccountnumber.trim();
										data.setSessionData("S_EPC_PAYMENT_POLICYNUM", billingaccountnumber);
										
										data.setSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingaccountnumber);
										data.addToLog(currElementName, "S_FWS_POLICY_BILLING_ACCT_NO :: "+data.getSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO));
									}
									
									if (policyobj.containsKey("suffix")) {
										suffix = (String) policyobj.get("suffix");
										suffix = suffix.trim();
										data.setSessionData(Constants.S_FWS_POLICY_SUFFIX, suffix);
										data.addToLog(currElementName, "S_FWS_POLICY_SUFFIX :: "+data.getSessionData(Constants.S_FWS_POLICY_SUFFIX));
									}
									
									if (policyobj.containsKey("effectiveDate")) {
										effectivedate = (String) policyobj.get("effectiveDate");
										effectivedate = effectivedate.trim();
										effectivedate = effectivedate.length() > 10 ? effectivedate.substring(0, 10) : effectivedate;
										data.setSessionData(Constants.S_FWS_POLICY_EFF_DATE, effectivedate);
										data.addToLog(currElementName, "S_FWS_POLICY_EFF_DATE :: "+data.getSessionData(Constants.S_FWS_POLICY_EFF_DATE));
									}
									
									if (policyobj.containsKey("policyState")) {
										policystate = (String) policyobj.get("policyState");
										policystate = policystate.trim();
										data.setSessionData(Constants.S_POLICY_STATE, policystate);
										data.setSessionData(Constants.S_POLICY_STATE_CODE, policystate);
										data.addToLog(currElementName, "S_POLICY_STATE :: "+data.getSessionData(Constants.S_POLICY_STATE));
									
										//CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName : Hawaii)
										data.addToLog(currElementName, "Policy State Code :" + policystate);
										String strPolicyStateName=setStateName(data, caa, currElementName, policystate);
										data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);

									
									}
									
									if (policyobj.containsKey("policyNumber")) {
										data.setSessionData("S_POLICY_NUM", (String) policyobj.get("policyNumber").toString().trim());
										data.addToLog(currElementName, "Setting Policy Number into session :: "+policyobj.get("policyNumber"));
									}
									if (policyobj.containsKey("policyNumber") && policyobj.containsKey("lineOfBusiness")) {
										data.setSessionData("S_FWS_EPC_POLICY_NUM", (String) policyobj.get("lineOfBusiness").toString().trim() +(String) policyobj.get("policyNumber").toString().trim());
										data.addToLog(currElementName, "Setting FWS Policy Number for EPC into session :: "+data.getSessionData("S_FWS_EPC_POLICY_NUM"));
									}
									if (policyobj.containsKey("billingAccountNumber")) {
										data.setSessionData("S_FWS_POLICY_BILLING_ACCT_NO", (String) policyobj.get("billingAccountNumber").toString().trim());
										data.addToLog(currElementName, "Setting Billing Account Number into session :: "+policyobj.get("billingAccountNumber"));
									}
									if (policyobj.containsKey("lineOfBusiness")) {
										data.setSessionData("S_FWS_POLICY_LOB", (String) policyobj.get("lineOfBusiness").toString().trim());
										data.addToLog(currElementName, "Setting Policy LOB into session :: "+policyobj.get("lineOfBusiness"));
									}
									if (policyobj.containsKey("effectiveDate")) {
										String effectiveDate = (String) policyobj.get("effectiveDate");
										effectiveDate = null != effectiveDate && effectiveDate.trim().length() > 10 ? effectiveDate.trim().substring(0, 10) : effectiveDate.trim();
										data.setSessionData("S_FWS_POLICY_EFF_DATE", effectiveDate);
										data.addToLog(currElementName, "Setting Policy Effective Date into session :: "+effectiveDate);
									}
									if (policyobj.containsKey("renewalEffectiveDate")) {
										String renEffectiveDate = (String) policyobj.get("effectiveDate");
										renEffectiveDate = null != renEffectiveDate && renEffectiveDate.trim().length() > 10 ? renEffectiveDate.trim().substring(0, 10) : renEffectiveDate.trim();
										data.setSessionData("S_FWS_POLICY_REN_EFF_DATE", renEffectiveDate);
										data.addToLog(currElementName, "Setting Policy Renewal Effective Date into session :: "+renEffectiveDate);
									}
									if (policyobj.containsKey("suffix")) {
										data.setSessionData("S_FWS_POLICY_SUFFIX", (String) policyobj.get("suffix").toString().trim());
										data.addToLog(currElementName, "Setting Policy Suffix into session :: "+policyobj.get("suffix"));
									}
									if (policyobj.containsKey("policyState")) {
										data.setSessionData("S_POLICY_STATE", (String) policyobj.get("policyState").toString().trim());
										data.addToLog(currElementName, "Setting Policy State into session :: "+policyobj.get("policyState"));
									}
									if (policyobj.containsKey("policySource")) {
										data.setSessionData("S_POLICY_SOURCE", (String) policyobj.get("policySource"));
										data.addToLog(currElementName, "Setting Policy Source into session :: "+policyobj.get("policySource"));
									}
									if (policyobj.containsKey("internalPolicyNum")) {
										data.setSessionData("S_FWS_INT_POLICY_NO", (String) policyobj.get("internalPolicyNum").toString().trim());
										data.addToLog(currElementName, "Setting Internal Policy Num into session :: "+policyobj.get("internalPolicyNum"));
									}
									if (policyobj.containsKey("internalPolicyVersion")) {
										data.setSessionData("S_FWS_INTERNAL_POLICY_VERSION", (String) policyobj.get("internalPolicyVersion").toString().trim());
										data.addToLog(currElementName, "Setting Internal Policy version into session :: "+policyobj.get("internalPolicyVersion"));
									}
									if (policyobj.containsKey("GPC")) {
										data.setSessionData("S_FWS_GPC", (String) policyobj.get("GPC"));
										data.addToLog(currElementName, "Setting Policy GPC into session :: "+policyobj.get("GPC"));
									}
									if (policyobj.containsKey("comboPackageIndicator")) {
										data.setSessionData("S_FWS_COMBO_PACKAGE_INDICATOR", (String) policyobj.get("comboPackageIndicator").toString().trim());
										data.addToLog(currElementName, "Setting Policy Combo Package Indicator into session :: "+policyobj.get("comboPackageIndicator"));
									}
									if (policyobj.containsKey("serviceLevels")) {
										data.setSessionData("S_FWS_SERVICE_LEVEL", (String) policyobj.get("serviceLevels").toString().trim());
										data.addToLog(currElementName, "Setting Policy Service Levels into session :: "+policyobj.get("serviceLevels"));
									}
									if (policyobj.containsKey("producerRoleCode")) {
										data.setSessionData("S_FWS_PRODUCER_ROLE_CODE", (String) policyobj.get("producerRoleCode").toString().trim());
										data.addToLog(currElementName, "Setting Policy Producer Role Code into session :: "+policyobj.get("producerRoleCode"));
									}
									if (policyobj.containsKey("companyProductCode")) {
										data.setSessionData("S_FWS_COMPANY_PRODUCT_CODE", (String) policyobj.get("companyProductCode").toString().trim());
										data.addToLog(currElementName, "Setting Policy Company Product Code into session :: "+policyobj.get("companyProductCode"));
									}
									if (policyobj.containsKey("callRoutingIndicator")) {
										data.setSessionData("S_FWS_CALL_ROUTING_INDICATOR", (String) policyobj.get("callRoutingIndicator").toString().trim());
										data.addToLog(currElementName, "Setting Policy Call Routing Indicator into session :: "+policyobj.get("callRoutingIndicator"));
									}
									if (policyobj.containsKey("addresses")) {
										JSONArray addressArr = (JSONArray) policyobj.get("addresses");
										
										if (null != addressArr && addressArr.size() > 0) {
											JSONObject addressObj = (JSONObject) addressArr.get(0);
											
											if (null != addressObj & addressObj.containsKey("zip")) {
												String zip = (String) addressObj.get("zip").toString().trim();
												
												if (null != zip) {
													zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
													data.setSessionData(Constants.S_PAYOR_ZIP_CODE, zip);
													data.addToLog(currElementName, "Setting Zip Code into session :: "+ data.getSessionData(Constants.S_PAYOR_ZIP_CODE));
												}
											}
										}
									}
									
									data.setSessionData(Constants.S_API_DOB, policyDOB);
									data.setSessionData(currElementName+Constants._RESP, policyobj);
									data.setSessionData(Constants.S_FINAL_POLICY_OBJ, policyobj);
									data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
									data.setSessionData(Constants.S_BU, "FWS Service");
									data.setSessionData(Constants.S_CATEGORY,"FWS_SVC");
									data.setSessionData(Constants.S_LOB, "FWS Service");
									data.addToLog(currElementName, "User has selected FWS Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
									data.addToLog(currElementName, "Overriding Business Unit After Successful Auth :: "+data.getSessionData(Constants.S_BU));
									data.addToLog(currElementName, "Final Policy Object Set into session After KYC Auth is Successful ::"+data.getSessionData(Constants.S_FINAL_POLICY_OBJ));
									data.addToLog(currElementName, "S_FLAG_FWS_BU :: "+data.getSessionData("S_FLAG_FWS_BU"));
								}
							}
							
							if (null == data.getSessionData(Constants.S_API_DOB) && null != insuredDetailsArr && insuredDetailsArr.size() > 0) {
								insuredDetailsData = (JSONObject) insuredDetailsArr.get(0);
								policyDOB = (String) insuredDetailsData.get("birthDate");
								data.addToLog(currElementName, "Date Received from API :: "+policyDOB);
								if (!policysource.isEmpty() && policysource.equalsIgnoreCase("ARS")) {
									data.addToLog(currElementName, "Date before conversion :: "+policyDOB);
									SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
									SimpleDateFormat resultSDF = new SimpleDateFormat("yyyy-MM-dd");
									policyDOB = resultSDF.format(inputSDF.parse(policyDOB));
									data.addToLog(currElementName, "Date After Conversion :: "+policyDOB);
								}
								
								data.setSessionData(Constants.S_API_DOB, policyDOB);
								data.setSessionData(currElementName+Constants._RESP, policyobj);
								data.setSessionData(Constants.S_FINAL_POLICY_OBJ, policyobj);
								
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
	}

	private void apiResponseManupulation_PointPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, String callerenteredDOB) {
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			String mastercompanycode = Constants.EmptyString, policySource = Constants.EmptyString, email = null, overrideEmail = null;
			data.addToLog(currElementName, "21C - Policy Lookup Resp : "+resp);
			
			if (null != resp && !resp.isEmpty()) {
				JSONObject policysummary = (JSONObject) resp.get("policySummary");
				if (null != policysummary && !policysummary.isEmpty()) {
					
					if (policysummary.containsKey("masterCompanyCode")) {
						mastercompanycode = (String) policysummary.get("masterCompanyCode");
					}
					
					
					if (policysummary.containsKey("policySourceSystem")) {
						policySource = (String) policysummary.get("policySourceSystem");
					}
					
					JSONObject autopolicy = (JSONObject) policysummary.get("autoPolicy");
					JSONArray drivers = (JSONArray) autopolicy.get("drivers");
					JSONObject policyobj = new JSONObject();
					String policyDOB = null;

					for(Object objPolicies : drivers) {
						policyobj = (JSONObject) objPolicies;
						policyDOB = (String) policyobj.get("dateofbirth");
						
						if (null != policyobj) {
							
							if (policyobj.containsKey("electronicMailAddress")) {
								email = (String) policyobj.get("electronicMailAddress");
								
								if (null != email && !email.isEmpty() && null == overrideEmail) {
									data.setSessionData(Constants.S_EMAIL, email);
									data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
									data.addToLog(currElementName, "EMAIL_AVAILABLE :: YES :: EMAIL SET INTO SESSION :: " +email);
								}
							}
							
							if (null != callerenteredDOB && callerenteredDOB.equalsIgnoreCase(policyDOB)) {
								data.setSessionData("S_POLICY_MCO", mastercompanycode);
								data.setSessionData(Constants.S_API_DOB, policyDOB);
								data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
								data.setSessionData(currElementName+Constants._RESP, policysummary);
								data.setSessionData(Constants.S_FINAL_POLICY_OBJ, policysummary);
								data.setSessionData(Constants.S_BU, "Auto-Service");
								data.setSessionData(Constants.S_POLICY_SOURCE, policySource);
								data.setSessionData(Constants.S_LOB, "Auto-Service");
								data.addToLog(currElementName, "Final Policy Object Set into session After KYC Auth is Successful ::"+data.getSessionData(Constants.S_FINAL_POLICY_OBJ));
								data.addToLog(currElementName, "S_FLAG_21ST_BU :: "+data.getSessionData("S_FLAG_21ST_BU"));
								data.addToLog(currElementName, "Overriding Business Unit After Successful Auth :: "+data.getSessionData(Constants.S_BU));
								
								if (policysummary.containsKey("address")) {
									JSONObject addressObject = (JSONObject) policysummary.get("address");
									
									if (null != addressObject && !addressObject.isEmpty()) {
										if (addressObject.containsKey("zip")) {
											String zip = (String) addressObject.get("zip").toString().trim();
											if (null != zip) {
												zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
											}
											data.setSessionData(Constants.S_PAYOR_ZIP_CODE, zip);
										}
										if (addressObject.containsKey("city")) {
											String city = (String) addressObject.get("city").toString().trim();
											data.setSessionData(Constants.S_CITY, city);
										}
										if (addressObject.containsKey("state")) {
											String state = (String) addressObject.get("state").toString().trim();
											data.setSessionData(Constants.S_STATE, state);
											data.setSessionData(Constants.S_POLICY_STATE_CODE, state);
										
											//CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName : Hawaii)
											data.addToLog(currElementName, "Policy State Code :" + state);
											String strPolicyStateName=setStateName(data, caa, currElementName, state);
											data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);

										
										
										}
										if (addressObject.containsKey("streetAddress1")) {
											String address = (String) addressObject.get("streetAddress1").toString().trim();
											data.setSessionData(Constants.S_LINE1, address);
										}
									}
								}
								if (policyobj.containsKey("person")) {
									JSONObject person = (JSONObject) policyobj.get("person");
									
									if (null != person && !person.isEmpty()) {
										if (person.containsKey("firstName")) {
											String firstname = (String) person.get("firstName").toString().trim();
											data.setSessionData(Constants.S_FIRST_NAME, firstname);
										}
										if (person.containsKey("lastName")) {
											String lastname = (String) person.get("lastName").toString().trim();
											data.setSessionData(Constants.S_LAST_NAME, lastname);
										}
									}
								}
								if (policyobj.containsKey("electronicMailAddress")) {
									overrideEmail = (String) policyobj.get("electronicMailAddress").toString().trim();
									
									if (null != overrideEmail && !overrideEmail.isEmpty()) {
										data.setSessionData(Constants.S_EMAIL, overrideEmail);
										data.addToLog(currElementName, "OVERRIDING EMAIL WHICH MATCHED WITH DOB INTO SESSION :: " +overrideEmail);
									}
								}
							}
						}
					}

					if (null == data.getSessionData(Constants.S_API_DOB)) {
						policyobj = (JSONObject) drivers.get(0);
						policyDOB = (String) policyobj.get("dateofbirth");
						data.setSessionData(Constants.S_API_DOB, policyDOB);
					}
					data.setSessionData(currElementName+Constants._RESP, policysummary);
					data.setSessionData(Constants.S_FINAL_POLICY_OBJ, policysummary);
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

	//CS1200021 | Farmers Insurance | US | Farmers Service (FDS) - Add Routing for 'True Direct' HouseAccountSubCategor
			
			public String setSubcategory(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strapiSubcategory) {
			

				// Retrieve the configuration categories from the ivrconfig properties : S_CONFIG_CATEGORIES
		        String configCategories = (String) data.getSessionData(Constants.S_CONFIG_CATEGORIES);
		        data.addToLog(currElementName, "configCategories : "+configCategories);
		        String strsubCategory = (String) data.getSessionData("S_API_SUBCATEGORY");
		        data.addToLog(currElementName, "API subCategory : "+strsubCategory);
				
				try {
			    
			        
			        // Check if the configCategories array is valid before proceeding for matching with ivrconfig properties
			        
			        if (configCategories != null) {
			        	
			            for (String subcategory : configCategories.split("\\|")) {

			        	    if (null!=strsubCategory && strsubCategory.equalsIgnoreCase(subcategory)) {
			                	 data.addToLog(currElementName, "Inside method subcategory : "+subcategory);
			                   
			                	 return strsubCategory;
			                }
			            }
			        }

			        // If no matching subcategory is found in ivrconfig properties, return this message
			        return "NA";
			        
			    } catch (Exception e) {
			        // Log the exception if necessary and return error message
			        return "ER";
			    }
		}
	
	public static void main(String[] args) throws ParseException {
		String a = "02/02/24";
		JSONObject resp = new JSONObject();
		resp.put("a", a);
		System.out.println(resp);
		System.out.println(resp.get("a"));
	}

}