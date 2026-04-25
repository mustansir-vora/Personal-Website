package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.farmers.bean.PolicyBean;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SinglePolicy_Found_KYC_IDAuth extends DecisionElementBase {
	


	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		try {
			String strBU = (String) data.getSessionData(Constants.S_FINAL_BRAND);
			data.addToLog(currElementName, "Post Brand Auth : Selected Policy Brand - "+strBU);
			
			if(strBU!=null)
				strBU = strBU.trim();

			switch (strBU) {
			case Constants.S_API_BU_BW:
				data.addToLog(currElementName,":: BW Execution  :: ");
				data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
				strExitState = policyInquiry_RetriveInsurancePoliciesByParty(strRespBody, strRespBody, data, caa, currElementName);
				break;
			case Constants.S_API_BU_FDS_PLA:
				data.addToLog(currElementName,":: FDS Execution  :: ");
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
				strExitState = mulesoftFarmerPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
				break;
			case Constants.S_API_BU_FDS_GWPC:
				data.addToLog(currElementName,":: GWPC Execution  :: ");
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
				strExitState = mulesoftFarmerPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
				break;
			case Constants.S_API_BU_FM:
				data.addToLog(currElementName,":: FM Execution  :: ");
				strExitState = foremostPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
				break;
			case Constants.S_API_BU_FWS_ARS:
				data.addToLog(currElementName,":: FWS Execution  :: ");
				strExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
				break;
			case Constants.S_API_BU_FWS_A360:
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
				strExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
				break;
			case Constants.S_API_BU_21C:
				data.addToLog(currElementName,":: Auto Execution  :: ");
				strExitState = PointPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
				break;
			default:
				break;
			}
			data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host details for SIDA_HOST_001  :: "+e);
			caa.printStackTrace(e);
		}
	
		return strExitState;
	}

	private String PointPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		boolean startsWithAlphabets = false;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try { 
			if(data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
				String input = (String) data.getSessionData(Constants.S_POLICY_NUM);
				if(input!=null) {
				startsWithAlphabets = Character.isLetter(input.charAt(0));
				if(startsWithAlphabets) {
					input =input.toUpperCase();
					data.addToLog(currElementName, "Policy Number from caller contains Alphabet and so changing the value to Upper Case :: " + input);
					data.setSessionData(Constants.S_POLICY_NUM, input);
				}else {
					data.addToLog(currElementName, "Policy Number from caller does not contains Alphabet and so Setting the same value " + input);
				}
				}
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				//https://api-ss.farmersinsurance.com/plcyms/v3/policies/21365978
				
				if(wsurl!=null && input!=null) {
					wsurl=wsurl.replace(Constants.S_POLICY_NUM, input);
				}
				data.addToLog(currElementName, "Set wsurl : "+wsurl);
				
				Lookupcall lookups = new Lookupcall();
				//UAT CHANGE START
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				 region = regionDetails.get(Constants.S_POINT_POLICYINQUIIRY_URL);
				}else {
					region="PROD";
				}

				
				org.json.simple.JSONObject responses = lookups.Getpointpolicyinquiry(wsurl,input,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				//UAT CHANGE END
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				
				if(responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_001 : PointPolicyInquiry �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
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
		
		try {
			objHostDetails.startHostReport(currElementName,"PointPolicyInquiry API call", strReqBody, region,(String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
	}

	private String fwsPolicyLookup(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
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
				//UAT CHANGE START
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
				org.json.simple.JSONObject responses = null;
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
						data.addToLog(currElementName, "Set SIDA_HOST_001 : FWSPolicyLookup API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
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
		
		try {
			objHostDetails.startHostReport(currElementName,"FWSPolicyLookup API call", strReqBody,region, (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
	}

	private String foremostPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		boolean startsWithAlphabets = false;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try
		{
			if(data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) != null)
			{
				String url = (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				String policynumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				startsWithAlphabets = Character.isLetter(policynumber.charAt(0));
				if(policynumber!=null) {
				if(startsWithAlphabets) {
					policynumber =policynumber.toUpperCase();
					data.addToLog(currElementName, "Policy Number from caller contains Alphabet and so changing the value to Upper Case :: " + policynumber);
					data.setSessionData(Constants.S_POLICY_NUM, policynumber);
				}else {
					data.addToLog(currElementName, "Policy Number from caller does not contains Alphabet and so Setting the same value " + policynumber);
				}
				}
				//Foremost policies are 15 digits but caller can enter the full 15 digits, 13 digits, 12 digits, OR 10 digits.
				if(policynumber.length() == 12) {
					policynumber = policynumber.substring(0, 10);
					data.addToLog(currElementName, "Considered 10 digit policynumber from 12 digits :: "+policynumber);
				} else if(policynumber.length() == 13) {
					data.setSessionData("S_EPC_PAYMENT_POLICYNUM", policynumber.substring(0, 13));
					policynumber = policynumber.substring(3, 13);
					data.addToLog(currElementName, "Considered 10 digit policynumber from 13 digits :: "+policynumber);
				} else if(policynumber.length() == 15) {
					data.setSessionData("S_EPC_PAYMENT_POLICYNUM", policynumber.substring(0, 13));
					policynumber = policynumber.substring(3, 13);
					data.addToLog(currElementName, "Considered 10 digit policynumber from 15 digits :: "+policynumber);
				}
				
				String systemDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
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
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				 region = regionDetails.get(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				}else {
					region="PROD";
				}
				
				org.json.simple.JSONObject responses = lookups.GetforemostPolicyInquiry(url,tid, policynumber, systemDate,policysource, Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				
				if(responses != null)
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) 
					{
						data.addToLog(currElementName, "Set SIDA_HOST_001 : ForemostPolicyInquiry API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						 data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
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
		
		
		try {
			objHostDetails.startHostReport(currElementName,"ForemostPolicyInquiry API call", strReqBody,region, (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String mulesoftFarmerPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {	
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		boolean startsWithAlphabets = false;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
				String policyContractNumbers = (String) data.getSessionData(Constants.S_POLICY_NUM);
				if(policyContractNumbers!=null) {
					startsWithAlphabets = Character.isLetter(policyContractNumbers.charAt(0));
					if(startsWithAlphabets) {
						policyContractNumbers =policyContractNumbers.toUpperCase();
						data.addToLog(currElementName, "Policy Number from caller contains Alphabet and so changing the value to Upper Case :: " + policyContractNumbers);
						data.setSessionData(Constants.S_POLICY_NUM, policyContractNumbers);
					}else {
						data.addToLog(currElementName, "Policy Number from caller does not contains Alphabet and so Setting the same value " + policyContractNumbers);
					}
				}
				String billingAccountNumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String telephoneNumber = (String) data.getSessionData(Constants.S_ANI);
				if(data.getSessionData(Constants.S_TELEPHONENUMBER) != null && !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
					telephoneNumber = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				}
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				//https://api-ss.farmersinsurance.com/plcyms/v3/policies?operation=searchByCriteria
				Lookupcall lookups = new Lookupcall();
				data.addToLog(currElementName, "policyContractNumbers : "+policyContractNumbers);
				data.addToLog(currElementName, "billingAccountNumber : "+billingAccountNumber);
				data.addToLog(currElementName, "telephoneNumber : "+telephoneNumber);
				
				//UAT CHANGE START
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
				      
				data.addToLog(currElementName, "policyContractNumbers : "+policyContractNumbers);
				data.addToLog(currElementName, "billingAccountNumber : "+billingAccountNumber);
				data.addToLog(currElementName, "telephoneNumber : "+telephoneNumber);
				
				org.json.simple.JSONObject responses = null;
				if(policyContractNumbers != null && !policyContractNumbers.equals("") ) {
					
					responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl,tid, policyContractNumbers,null,null,Integer.parseInt(conntimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				}else if(billingAccountNumber != null && !billingAccountNumber.equals("")) {
					responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl,tid, null,billingAccountNumber,null,Integer.parseInt(conntimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				}else {
					responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl,tid, null,null,telephoneNumber,Integer.parseInt(conntimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				}
			//end	
				data.addToLog("responses", responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if(responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_001 : MulesoftFarmerPolicyInquiry_Post API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_MulesoftFarmerPolicyInquiry(data, caa, currElementName, strRespBody);
					} else  {
						if(responses.get(Constants.RESPONSE_MSG)!=null)
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch (Exception e)
		{
			data.addToLog(currElementName,"Exception in SIDA_HOST_001  MulesoftFarmerPolicyInquiry_Post API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"MulesoftFarmerPolicyInquiry_Post API call", strReqBody, region,(String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
		
		

	}

	private String policyInquiry_RetriveInsurancePoliciesByParty(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL) != null)
			{
				String wsurl = (String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
				String phoneNo = (String) data.getSessionData(Constants.S_ANI);
				if(data.getSessionData(Constants.S_TELEPHONENUMBER) != null && !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
					phoneNo = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				}
				String policyNum = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				Lookupcall lookups = new Lookupcall();
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				//UAT CHANGE START
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				 region = regionDetails.get(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
				}else {
					region="PROD";
				}
				
				org.json.simple.JSONObject responses = lookups.GetPolicyInquiry_RetrieveInsurance(wsurl,tid,phoneNo,Integer.parseInt(connTimeout),Integer.parseInt(readTimeout),context,region,UAT_FLAG);
			//end
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if(responses != null)
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_001 : GetPolicyInquiry_RetrieveInsurance API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_RetriveInsurancePoliciesByParty(data, caa, currElementName, strRespBody, policyNum);
					
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
		
		try {
			objHostDetails.startHostReport(currElementName,"RetrieveIntermediarySegmentationInfo API call", strReqBody,region, (String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

	private String  apiResponseManupulation_MulesoftFarmerPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray policiesArr = (JSONArray)resp.get("policies");
		
			String lob = null;
			String prompt = Constants.EmptyString;
			int autoprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int homeprodtypecount = 0;
			boolean singleproducttype = false;
			String agentAORid = Constants.EmptyString;
			String strPolicySource = Constants.EmptyString;
			String strPolicyGPC = Constants.EmptyString;
			
			if(policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String,PolicyBean>>();

				for(int i = 0; i < policiesArr.size(); i++) {
					JSONObject policyData = (JSONObject) policiesArr.get(i);
					PolicyBean beanObj = new PolicyBean();
					String strPolicyNum = Constants.EmptyString;
					
					if (policyData.containsKey("lineOfBusiness")) {
						lob = (String) policyData.get("lineOfBusiness");
						if(Constants.PRODUCTTYPE_F.equalsIgnoreCase(lob)) {
							lob=Constants.PRODUCTTYPE_H;
						}
						if (productTypeCounts.containsKey(lob)) 
							productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						else 
							productTypeCounts.put(lob, 1);
					}
					
					if (policyData.containsKey("address")) {
						JSONArray addressesArr = (JSONArray)policyData.get("address");
						JSONObject addressesObj = (JSONObject) addressesArr.get(0);
						if (addressesObj.containsKey("zip")) beanObj.setStrZipCode((String)addressesObj.get("zip"));
						if (addressesObj.containsKey("state")) beanObj.setStrPolicyState((String)addressesObj.get("state"));
					}
					
					String strDOB ="";
					if (policyData.containsKey("insuredDetails")) {
						JSONArray insuredDetailsArr = (JSONArray)policyData.get("insuredDetails");
						for(int k =0;k<insuredDetailsArr.size();k++) {
							JSONObject obj =(JSONObject)insuredDetailsArr.get(k);
							strDOB = (strDOB.equals(""))?""+obj.get("birthDate"):strDOB+","+obj.get("birthDate");
						}
						beanObj.setStrDOB(strDOB);
					}
					
					if (policyData.containsKey("policyNumber")) {
						strPolicyNum = (String) policyData.get("policyNumber");
					}
					
					if (policyData.containsKey("billingAccountNumber") && null != policyData.get("billingAccountNumber")) {
						beanObj.setStrBillingAccountNumber((String)policyData.get("billingAccountNumber"));
					}
					
					if (policyData.containsKey("agentReference")) {
						agentAORid = (String) policyData.get("agentReference");
						beanObj.setStrAgentAORID(agentAORid);
						data.setSessionData("S_AGENT_ID", agentAORid);
						
						String policyAttributeCode = agentAORid.substring(2,4);
						String policyAtttribute = "";
						if("99".equalsIgnoreCase(policyAttributeCode)) {
							policyAtttribute = "AOR99";
						}
						data.addToLog(data.getCurrentElement(), "Set Policy Attribute into session  "+Constants.S_POLICY_ATTRIBUTES+" : "+policyAtttribute);
						data.setSessionData(Constants.S_POLICY_ATTRIBUTES, policyAtttribute);
					}
					
					if (policyData.containsKey("policySource")) {
						strPolicySource = (String) policyData.get("policySource");
						data.addToLog(data.getCurrentElement(), "Set policy source into session  "+Constants.S_POLICY_SOURCE+" : "+strPolicySource);
						data.setSessionData(Constants.S_POLICY_SOURCE, strPolicySource);
						beanObj.setStrPolicySource(strPolicySource);
					}
					if (policyData.containsKey("GPC")) {
						strPolicyGPC = (String) policyData.get("GPC");
						String fasPolicySeg = (String) data.getSessionData("S_FDS_GPC_CA_HOME");
						data.addToLog(data.getCurrentElement(), "GPC : "+strPolicyGPC);
						data.addToLog(data.getCurrentElement(), "POLICY SEGMENTATION : "+fasPolicySeg);
						if(strPolicyGPC != null && !strPolicyGPC.equals("") && fasPolicySeg != null && !fasPolicySeg.equals("") && fasPolicySeg.contains(strPolicyGPC)) {
							data.addToLog(data.getCurrentElement(), "Set policy segmena into session  "+Constants.S_POLICY_SEGMENTATION+" : FDS_GPC_CA_HOME");
							data.setSessionData(Constants.S_POLICY_SEGMENTATION, "FDS_GPC_CA_HOME");
						}
					}
					
					String tmpKey = Constants.EmptyString;
					switch (lob) {
					case Constants.PRODUCTTYPE_H:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_A:
						tmpKey = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_U:
						tmpKey = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_F:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
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

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_H) || productTypeCounts.containsKey(Constants.PRODUCTTYPE_F)) {
					homeprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_H);
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

				data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Home Product Type Count  = "+data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Umbrella Product Type Count = "+data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(0, autoprodtypecount, homeprodtypecount, umbrellaprodtypecount, 0, 0);
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
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
							data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
						}
					}
					data.addToLog(currElementName, "Value of S_POLICY_STATE_CODE : " + data.getSessionData(Constants.S_POLICY_STATE_CODE));
					strExitState = Constants.SU;
				} else if (!singleproducttype) {
					if(autoprodtypecount >= 1) prompt = prompt+" an Auto policy"; 
					if (homeprodtypecount >= 1) prompt = prompt+" a Home Policy"; 
					if (umbrellaprodtypecount >= 1) prompt = prompt+" or an Umbrella Policy"; 
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
					
					if(autoprodtypecount >= 1) prompt = " Auto "; 
					else if (homeprodtypecount >= 1) prompt = " Home "; 
					else if (umbrellaprodtypecount >= 1) prompt = " an Umbrella ";
					
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 
					HashMap<String, PolicyBean> policyDetailsMap = new HashMap<String, PolicyBean>();
					for (String key : productPolicyMap.keySet()) {
						policyDetailsMap = productPolicyMap.get(key);
					}
					data.setSessionData(Constants.POLICY_DETAILS_FOR_MN3, policyDetailsMap);
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
	
	private String apiResponseManupulation_ForemostPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {	
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray policiesArr = (JSONArray)resp.get("policies");
		
			String policyProductCode = null;
			String prompt = Constants.EmptyString;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int mrprodtypecount = 0;
			int rvprodtypecount = 0;
			int spprodtypecount = 0;
			boolean singleproducttype = false;
			String agentAORid = Constants.EmptyString;
			
			if(policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String,PolicyBean>>();

				for(int i = 0; i < policiesArr.size(); i++) {
					JSONObject policyData = (JSONObject) policiesArr.get(i);
					PolicyBean beanObj = new PolicyBean();
					String strPolicyNum = Constants.EmptyString;
					
					if (policyData.containsKey("policyProductCode")) {
						policyProductCode = (String) policyData.get("policyProductCode");
						if (productTypeCounts.containsKey(policyProductCode)) productTypeCounts.put(policyProductCode, productTypeCounts.get(policyProductCode) + 1);
						else productTypeCounts.put(policyProductCode, 1);
						beanObj.setStrPolicyProductCode(policyProductCode);
					}
					
					if (policyData.containsKey("addresses")) {
						JSONArray addressesArr = (JSONArray)policyData.get("addresses");
						JSONObject addressesObj = (JSONObject) addressesArr.get(0);
						if (addressesObj.containsKey("zip")) {
							String zip = (String) addressesObj.get("zip");
							if (zip.length() > 5) {
								zip = zip.substring(0, 5);
							}
							beanObj.setStrZipCode(zip);
						}
						if (addressesObj.containsKey("state"))
							beanObj.setStrPolicyState((String)addressesObj.get("state"));
					}
					
					if (policyData.containsKey("insureds")) {
						String strDOB ="";
						JSONArray insuredDetailsArr = (JSONArray)policyData.get("insureds");
						for(int k =0;k<insuredDetailsArr.size();k++) {
							JSONObject obj =(JSONObject)insuredDetailsArr.get(k);
							strDOB = (strDOB.equals(""))?""+obj.get("birthDate"):strDOB+","+obj.get("birthDate");
						}
						beanObj.setStrDOB(strDOB);
					}
					
					if (policyData.containsKey("policyNumber")) {
						strPolicyNum = (String) policyData.get("policyNumber");
					}
					
					if (policyData.containsKey("agentOfRecordId")) {
						agentAORid = (String) policyData.get("agentOfRecordId");
						beanObj.setStrAgentAORID(agentAORid);
						data.setSessionData("S_AGENT_ID", agentAORid);
					}
					
					String tmpKey = Constants.EmptyString;
					switch (policyProductCode) {
					case Constants.PRODUCTTYPE_601:
						tmpKey = Constants.MR_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_602:
						tmpKey = Constants.MR_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_077:
						tmpKey = Constants.RV_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_255:
						tmpKey = Constants.RV_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_276:
						tmpKey = Constants.RV_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_103:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_105:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_106:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_107:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_381:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_444:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
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

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_601)) {
					mrprodtypecount = mrprodtypecount+productTypeCounts.get(Constants.PRODUCTTYPE_601);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_602)) {
					mrprodtypecount = mrprodtypecount+productTypeCounts.get(Constants.PRODUCTTYPE_602);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_077)) {
					rvprodtypecount = rvprodtypecount+productTypeCounts.get(Constants.PRODUCTTYPE_077);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_255)) {
					rvprodtypecount = rvprodtypecount+productTypeCounts.get(Constants.PRODUCTTYPE_255);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_276)) {
					rvprodtypecount = rvprodtypecount+productTypeCounts.get(Constants.PRODUCTTYPE_276);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_103)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_103);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_105)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_105);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_106)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_106);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_107)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_107);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_381	)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_381);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_444)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_444);
				}

				data.setSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC, spprodtypecount);
				data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC, mrprodtypecount);
				data.setSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC, rvprodtypecount);

				data.addToLog(currElementName,"specialty dwelling or mobile home = "+data.getSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Boat Product Type Count  = "+data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"motor home or motorcycle  = "+data.getSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(rvprodtypecount, 0, 0, 0, mrprodtypecount, spprodtypecount);
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
							data.setSessionData("POLICY_PRODUCT_CODE", obj.getStrPolicyProductCode());
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrPolicyProductCode()+key1);
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
						}
					}
					strExitState = Constants.SU;
				} else if (!singleproducttype) {
					if(autoprodtypecount >= 1) prompt = prompt+" an Auto policy"; 
					if (homeprodtypecount >= 1) prompt = prompt+" a Home Policy"; 
					if (mrprodtypecount >= 1) prompt = prompt+" a Boat Policy"; 
					if (umbrellaprodtypecount >= 1) prompt = prompt+" or an Umbrella Policy"; 
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
					if(autoprodtypecount >= 1) prompt = " Auto "; 
					else if (homeprodtypecount >= 1) prompt = " Home "; 
					else if (umbrellaprodtypecount >= 1) prompt = " an Umbrella ";
					else if(mrprodtypecount >= 1) prompt = " Boat ";
					
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

	private String apiResponseManupulation_FwsPolicyLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {			
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
		

	private String apiResponseManupulation_RetriveInsurancePoliciesByParty(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, String policyNum) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject household = null;
			if (resp.containsKey("household")) {
				household = (JSONObject) resp.get("household");
			}

			String agentAORid = Constants.EmptyString;
			if(household!=null) {
			JSONArray autoPoliciesarr = (JSONArray)household.get("autoPolicies");
			if(policyNum==null || "".equals(policyNum)) {
			
			if(autoPoliciesarr != null && autoPoliciesarr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, autoPoliciesarr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String,PolicyBean>>();

				data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH, autoPoliciesarr.size());
				data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH));
				
				
				for(int i = 0; i < autoPoliciesarr.size(); i++) {
					JSONObject policyData = (JSONObject) autoPoliciesarr.get(i);
					JSONObject basicPolicyData = (JSONObject) policyData.get("basicPolicy");
					JSONObject basicPolicyDetail = (JSONObject) policyData.get("basicPolicyDetail");
					JSONObject insuredVehicleData = (JSONObject) policyData.get("insuredVehicle");
					JSONObject namedInsuredData = (JSONObject) policyData.get("namedInsured");
					String strPolicyNum = Constants.EmptyString, strPolicyMod = Constants.EmptyString, strPolicySymbol = Constants.EmptyString;
					
				if (basicPolicyData!=null && basicPolicyData.containsKey("policyNumber")) {
						strPolicyNum = (String) basicPolicyData.get("policyNumber");
						data.setSessionData("S_POLICY_NUM", basicPolicyData.get("policyNumber"));
					}
					HashMap<String, PolicyBean> productbeanObj = productPolicyMap.get(Constants.PRODUCTTYPE_AUTO);
					PolicyBean beanObj = new PolicyBean();
					if(productbeanObj!=null && productbeanObj.containsKey(strPolicyNum) && productbeanObj.get(strPolicyNum) != null ) {
						beanObj  =	(PolicyBean)productbeanObj.get(strPolicyNum);
					}else {
						beanObj = new PolicyBean();
					}
					
					String zip = beanObj.getStrZipCode();
					if (insuredVehicleData.containsKey("garagingAddress")) {
						JSONObject garagingAddress = (JSONObject) insuredVehicleData.get("garagingAddress");
						if (garagingAddress.containsKey("postalCode")) {
							zip = (zip.equals(""))?""+garagingAddress.get("postalCode"):zip+","+garagingAddress.get("postalCode");
							beanObj.setStrZipCode(zip);
						}
					}
					
					String strDOB = beanObj.getStrDOB();
					if (namedInsuredData.containsKey("birthDate")) {
						strDOB = (strDOB.equals(""))?""+namedInsuredData.get("birthDate"):strDOB+","+namedInsuredData.get("birthDate");
						beanObj.setStrDOB(strDOB);
					}
					
					/**
					 * Policy Mod and Policy Symbol retreived
					 */
				
					if (basicPolicyData.containsKey("policyModNumber")) {
						strPolicyMod = (String) basicPolicyData.get("policyModNumber");
						data.setSessionData("POLICY_MOD", strPolicyMod);
						data.addToLog(currElementName, "Setting POLICY_MOD into session :: "+data.getSessionData("POLICY_MOD"));
					}
					
					if (basicPolicyDetail.containsKey("policySymbol")) {
						strPolicySymbol = (String) basicPolicyDetail.get("policySymbol");
						data.setSessionData("POLICY_SYMBOL", strPolicySymbol);
						data.setSessionData(Constants.S_FULL_POLICY_NUM, strPolicySymbol+""+strPolicyNum);
						if(basicPolicyDetail.containsKey("insurerCompanyCode")) {
							String strInsurerCompanyCode = (String) basicPolicyDetail.get("insurerCompanyCode");
							setBWSegmentation(caa, data, strPolicySymbol+"-"+strInsurerCompanyCode);
						}
						data.addToLog(currElementName, "Setting POLICY_SYMBOL into session :: "+data.getSessionData("POLICY_SYMBOL"));
					}
					
					/**
					 * Policy Mod and Policy Symbol retreived
					 */
					
					if(productPolicyMap.containsKey(Constants.PRODUCTTYPE_AUTO)) {
						HashMap<String, PolicyBean> tmpPolicyDetails = productPolicyMap.get(Constants.PRODUCTTYPE_AUTO);
						tmpPolicyDetails.put(strPolicyNum, beanObj);
						productPolicyMap.put(Constants.PRODUCTTYPE_AUTO, tmpPolicyDetails);
					} else {
						HashMap<String, PolicyBean> policyDetails = new HashMap<String, PolicyBean>();
						policyDetails.put(strPolicyNum, beanObj);
						productPolicyMap.put(Constants.PRODUCTTYPE_AUTO, policyDetails);
					}
					
					if (policyData.containsKey("agentDetails")) {
						JSONObject agentdetails = (JSONObject) policyData.get("agentDetails");
						agentAORid = (String) agentdetails.get("agentCode");
						beanObj.setStrAgentAORID(agentAORid);
						data.setSessionData("S_AGENT_ID", agentAORid);
					}
					
				}

				data.setSessionData(Constants.S_POLICYDETAILS_MAP, productPolicyMap);
				data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);
				data.addToLog(currElementName, "policyDetails Hashmap : "+productPolicyMap);
				if (productPolicyMap !=null && productPolicyMap.get(Constants.PRODUCTTYPE_AUTO) !=null && productPolicyMap.get(Constants.PRODUCTTYPE_AUTO).size()==1) {
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
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
						}
					}
					strExitState = Constants.SU;
				} else if (productPolicyMap !=null && productPolicyMap.get(Constants.PRODUCTTYPE_AUTO) !=null && productPolicyMap.get(Constants.PRODUCTTYPE_AUTO).size()>1) {

					data.setSessionData(Constants.VXMLParam1, "AUTO");
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Single Product Type Multiple Policy scenario");
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap.get(Constants.PRODUCTTYPE_AUTO);
					data.setSessionData(Constants.POLICY_DETAILS_FOR_MN3, policyDetailsMap);
					
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));
					
					strExitState = Constants.SU;
				} else {
					strExitState = Constants.ER;
				}
			} else {
				strExitState = Constants.ER;
			}
			}else {
				
				data.addToLog(currElementName, "Check BW Policy ");
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, autoPoliciesarr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String,PolicyBean>>();
				String strDOB ="", strPolicyMod = "", strPolicySymbol = "";
				for(int i = 0; i < autoPoliciesarr.size(); i++) {
					JSONObject policyData = (JSONObject) autoPoliciesarr.get(i);
					data.addToLog(currElementName, "Policy Returned"+autoPoliciesarr.size());	
				 if(policyData !=null && policyData.get("basicPolicy")!=null &&policyData.get("insuredVehicle")!=null && policyData.get("namedInsured")!=null) {
					 data.addToLog(currElementName, "Policy Returned in correct format"+policyData.get("basicPolicy"));	
				
					 JSONObject basicPolicyData = (JSONObject) policyData.get("basicPolicy");
						JSONObject basicPolicyDetail = (JSONObject) policyData.get("basicPolicyDetail");
					JSONObject insuredVehicleData = (JSONObject) policyData.get("insuredVehicle");
					JSONObject namedInsuredData = (JSONObject) policyData.get("namedInsured");
					
					
					if (basicPolicyData.containsKey("policyNumber") && policyNum.contains(""+basicPolicyData.get("policyNumber")) ){
						 data.setSessionData("S_POLICY_NUM", basicPolicyData.get("policyNumber"));
						data.setSessionData(Constants.S_API_DOB, ""+namedInsuredData.get("birthDate"));
						JSONObject garagingAddress = (JSONObject) insuredVehicleData.get("garagingAddress");
						data.setSessionData(Constants.S_API_ZIP, (String)garagingAddress.get("postalCode"));
						data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
						data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_SINGLE_POLICY_FOUND));
						data.addToLog(currElementName,"S_API_DOB : "+namedInsuredData.get("birthDate"));
						
						
						if (policyData.containsKey("namedInsured")) {
							JSONObject obj = (JSONObject)policyData.get("namedInsured");
							strDOB = (strDOB.equals(""))?""+obj.get("birthDate"):strDOB+","+obj.get("birthDate");
							data.setSessionData(Constants.S_API_DOB, ""+strDOB);
							data.addToLog(currElementName,"S_API_DOB : "+strDOB);
						}
						
						if (basicPolicyDetail.containsKey("policySymbol")) {
							strPolicySymbol = (String) basicPolicyDetail.get("policySymbol");
							if(basicPolicyDetail.containsKey("insurerCompanyCode")) {
								String strInsurerCompanyCode = (String) basicPolicyDetail.get("insurerCompanyCode");
								setBWSegmentation(caa, data, strPolicySymbol+"-"+strInsurerCompanyCode);
							}
						}
						
						
					}
					
					/**
					 * Policy Mod and Policy Symbol retreived
					 */
				
					if (basicPolicyData.containsKey("policyModNumber")) {
						strPolicyMod = (String) basicPolicyData.get("policyModNumber");
						data.setSessionData("POLICY_MOD", strPolicyMod);
					}
					
					if (basicPolicyDetail.containsKey("policySymbol")) {
						strPolicySymbol = (String) basicPolicyDetail.get("policySymbol");
						data.setSessionData("POLICY_SYMBOL", strPolicySymbol);
					}
					
					/**
					 * Policy Mod and Policy Symbol retreived
					 */
				}

				
				strExitState = Constants.SU;
			}
				data.addToLog(currElementName, "Product Type Count Hashmap : "+data.getSessionData(Constants.S_API_DOB));
				data.addToLog(currElementName, "policyDetails Hashmap : "+data.getSessionData(Constants.S_API_ZIP));
		}
			}} catch (Exception e) 
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
			JSONObject policySummaryObj = (JSONObject)resp.get("policySummary");
			
			if(policySummaryObj != null && policySummaryObj.containsKey("policyNumber")) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, 1);
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String,PolicyBean>>();
				String strPolicyNum = Constants.EmptyString;
				String strPolicyCompanyCode = Constants.EmptyString;
				String strPolicySourceSystem =Constants.EmptyString;
				PolicyBean beanObj = new PolicyBean();
					
					if (policySummaryObj.containsKey("address")) {
						JSONArray autoPoliciesarr = (JSONArray)policySummaryObj.get("address");
						JSONObject addressObj = (JSONObject) autoPoliciesarr.get(0);
						if (addressObj.containsKey("zip")) beanObj.setStrZipCode((String)addressObj.get("zip"));
					}
					
					if (policySummaryObj.containsKey("autoPolicy")) {
						JSONObject autoPolicyObj = (JSONObject) policySummaryObj.get("autoPolicy");
						JSONArray driverssarr = (JSONArray)autoPolicyObj.get("drivers");
						JSONObject driversObj = (JSONObject) driverssarr.get(0);
						if (driversObj.containsKey("dateofbirth")) beanObj.setStrDOB((String)driversObj.get("dateofbirth"));
					}
					
					if (policySummaryObj.containsKey("policyNumber")) {
						strPolicyNum = (String) policySummaryObj.get("policyNumber");
					}
					
					if (policySummaryObj.containsKey("masterCompanyCode")) {
						strPolicyCompanyCode = (String) policySummaryObj.get("masterCompanyCode");
					}
					
					if (policySummaryObj.containsKey("policySourceSystem")) {
						strPolicySourceSystem = (String) policySummaryObj.get("policySourceSystem");
						 beanObj.setStrPolicySource(strPolicySourceSystem);
					}
					
					
					HashMap<String, PolicyBean> policyDetails = new HashMap<String, PolicyBean>();
					policyDetails.put(strPolicyNum, beanObj);
					productPolicyMap.put("21C", policyDetails);
				data.setSessionData("S_POLICY_MCO", strPolicyCompanyCode);
				data.setSessionData(Constants.S_POLICYDETAILS_MAP, productPolicyMap);
				data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);
				data.addToLog(currElementName, "policyDetails Hashmap : "+productPolicyMap);
				if (policyDetails.size() == 1) {
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
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
							data.setSessionData(Constants.S_POLICY_SOURCE, strPolicySourceSystem);
						}
					}
					strExitState = Constants.SU;
				} else {
					strExitState = Constants.ER;
				}
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) 
		{
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
public static void main(String[] args) {

	String lob = "Foremost";
	String list = "Foremost AutoBristol West";
	
	/*
	 * String[] lobList = list.split(","); ArrayList<String> foremost_Lob = null;
	 * 
	 * foremost_Lob = new ArrayList<String>(); for (String lobVal : lobList) {
	 * foremost_Lob.add(lobVal); }
	 * 
	 * if(foremost_Lob.contains(lob)) { System.out.println("Contains"); }else {
	 * System.out.println("Doesn't Contain"); }
	 */
	/*
	String[] arr = null;
	if(list.contains(",")) {
		arr = list.split(",");
	}else {
		arr = new String[1];
		arr[0] = list;
	}
	
	for(String str : arr) {
		System.out.println("String :: "+ str);
	}
	*/
	String policynumber = "103700043593901";
	if(policynumber.length() == 12) {
		policynumber = policynumber.substring(0, 10);
	} else if(policynumber.length() == 13) {
		policynumber = policynumber.substring(3, 13);
	} else if(policynumber.length() == 15) {
		policynumber = policynumber.substring(3, 13);
	}
	System.out.println(policynumber);
}
	


}
