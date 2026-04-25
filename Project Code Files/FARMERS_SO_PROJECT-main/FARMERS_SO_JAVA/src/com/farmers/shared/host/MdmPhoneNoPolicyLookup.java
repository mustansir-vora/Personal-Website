package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

public class MdmPhoneNoPolicyLookup extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		try {
			String strBU = (String) data.getSessionData(Constants.S_BU);
			
			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");
			
			data.addToLog(currElementName, " PolicyLookup : strBU :: "+strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(currElementName, " A_FOREMOST_LOB : "+strForemostCode);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);
			
			if(strBristolCode!=null && null != strBU && strBristolCode.contains(strBU)) {
				data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
				strExitState = policyInquiry_RetriveInsurancePoliciesByParty(strRespBody, strRespBody, data, caa, currElementName);
			} else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				//S_FLAG_FDS_BU
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
				strExitState = mulesoftFarmerPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
			} else if(strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				data.addToLog(currElementName, "Its foremost LOB");
				strExitState = foremostPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
			} else if(strFWSCode!=null && strBU!=null && strFWSCode.contains(strBU)) {
				strExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				strExitState = PointPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
			} else {
				strExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName);
			}
			data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host details for SIDA_HOST_001  :: "+e);
			caa.printStackTrace(e);
		}
	
		/*
		try {
			objHostDetails.startHostReport(currElementName,"SIDA_HOST_001", strReqBody);
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU);
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDA_HOST_001  PolicyInquiryAPI call  :: "+e);
			caa.printStackTrace(e);
		}
		*/
		
		
		// Change for bypassing DOB/ZIP collection if values are not available in API response
//		data.addToLog("DOB: ", data.getSessionData(Constants.S_API_DOB).toString());
//		
//		data.addToLog("ZIP: ", data.getSessionData(Constants.S_API_ZIP).toString());
//		
		
		
		return strExitState;
	}


	private String PointPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER,
				strReqBody = Constants.EMPTY;
		boolean startsWithAlphabets = false;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
        String wsurl=null;
		String region=null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		try { 
			if(data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null) {
				 wsurl = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
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
				//UAT CHNGE START(SHAIK,PRIYA)
				data.addToLog("API URL: ",wsurl);
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
				data.addToLog("responses", responses.toString());
				//UAT ENV CHANGE END(SHAIK,PRIYA)
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

				if(responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_001 : PointPolicyInquiry  PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
						//CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));
						
						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
						
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
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE) && (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName,"PointPolicyInquiry API call", strReqBody,region,(String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				objHostDetails.startHostReport(currElementName,"PointPolicyInquiry API call", strReqBody,region,(String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
	}

	//START : CS1245054:Mdm FWS PhoneNoLookup changes
	
	private String fwsPolicyLookup(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		boolean startsWithAlphabets = false;
		String policyContractNumber = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
        String wsurl=null;
		String region=null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		try {
			
			//Checking MDM phone number lookup URL is available in session
			if(data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_URL) != null) {
				 wsurl = (String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_URL);
				data.addToLog(currElementName, "S_MDM_PHONENO_LOOKUP_URL : "+wsurl);
				//Checking policy number is available in session	
				if(null != data.getSessionData(Constants.S_POLICY_NUM) && !((String)data.getSessionData(Constants.S_POLICY_NUM)).isEmpty()) {
					policyContractNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
					startsWithAlphabets = Character.isLetter(policyContractNumber.charAt(0)); 
					//Log and adjust policy number if it starts with alphabet
					if (startsWithAlphabets) {
						data.addToLog(currElementName, "The string starts with an alphabet.");
						policyContractNumber= policyContractNumber.substring(1,policyContractNumber.length());
			        	data.addToLog(currElementName, "Post Sub String :: " + policyContractNumber);
			        	data.setSessionData(Constants.S_POLICY_NUM, policyContractNumber);
			        } else {
			        	data.addToLog(currElementName, "The string does not start with an alphabet.");
			        }
				}
				//retrieve billing account number, ani/telephone number and excluded source	
				String billingAccountNumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String telephoneNumber = (String) data.getSessionData(Constants.S_ANI);
				String excludedSource = (String) data.getSessionData(Constants.S_EXCLUDED_SOURCE) != null ? 
                        (String) data.getSessionData(Constants.S_EXCLUDED_SOURCE) : "Life";//default excluded source value

				//check if telephone/ani number is available in session 	
				if(data.getSessionData(Constants.S_TELEPHONENUMBER) != null && !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
					telephoneNumber = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				}
				
				//retrieve connection timeout value from session or use default 
		        int connTimeout = data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_CONN_TIMEOUT) != null 
			            ? Integer.valueOf((String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_READ_TIMEOUT)) 
			            : 12000; // default connection timeout
		        
		        //retrieve read time out value from session or use default 
			    int readTimeout = data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_CONN_TIMEOUT) != null 
			            ? Integer.valueOf((String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_READ_TIMEOUT)) 
			            : 12000; // default read timeout
		
			    //retrieve call ID from session and logger context from application data
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				//log the policy number, billing account number and telephone number, Created object for Lookupcall class file
				data.addToLog(currElementName, "S_POLICY_NUM : "+policyContractNumber+" :: S_BILLING_ACC_NUM : "+billingAccountNumber+" :: S_ANI : "+telephoneNumber);
				Lookupcall lookups = new Lookupcall();
				
				//check UAT enviroment flag and set region respectively
				data.addToLog("API URL: ",wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				 if("YES".equalsIgnoreCase(UAT_FLAG)) {
					 region = regionDetails.get(Constants.S_MDM_PHONENO_LOOKUP_URL);
	                }else {
	                	region="PROD";
	                }
				//Initialize JSON object for response
				org.json.simple.JSONObject responses = null;
				
				//Perform lookup based on available data like policy number, billing account number or telephone number(ani)
				if(policyContractNumber != null && !policyContractNumber.isEmpty()) {
					responses = lookups.GetFWSPolicyLookup(wsurl,tid, policyContractNumber,null,null,connTimeout, readTimeout,context,region,UAT_FLAG);
				} else if(billingAccountNumber != null && !billingAccountNumber.equals("")){
					 responses = lookups.GetFWSPolicyLookup(wsurl,tid, null,billingAccountNumber,null,connTimeout, readTimeout,context,region,UAT_FLAG);
				}else {
					 responses = lookups.GetMdmFWSPhoneNoLookup(wsurl,tid, telephoneNumber,excludedSource,connTimeout, readTimeout,context,region,UAT_FLAG);
				}
				data.addToLog("responses", responses.toString());
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

				//Process the response if not null
				if(responses != null) 
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_001 : FWSPolicyLookup API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
				        data.setSessionData("PHONE_NO_LOOKUP", "Y");				
				        //CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));
						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
						JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
						if(resp.containsKey("transactionNotification")) {
							strExitState = Constants.ER;
						}else {
							//Calling method for MDM FWS Policy Lookup based on phone number 		
							strExitState = apiResponseManupulation_MdmFwsPolicyLookup(data, caa, currElementName, strRespBody);
						}
				
					} else {
						//if response code is not 200, get the response message
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
					
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_HOST_001  FWSPolicyLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		try {
			// Check if the session data for policy lookup exit state matches any of the specified constants
			
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE) && (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
			
				// Start host reporting for the FWSPolicyLookup API call	
				objHostDetails.startHostReport(currElementName,"MdmFWSPhoneNoLookup", strReqBody,region,(String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_URL));
				// End host reporting with success or error based on the exit state
				objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				// If policy lookup exit state does not match, start and end host reporting indicating DB mismatch
				objHostDetails.startHostReport(currElementName,"MdmFWSPhoneNoLookup", strReqBody,region,(String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_URL));		
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
	}

	//END : CS1245054:Mdm FWS PhoneNoLookup changes
	
	private String foremostPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		boolean startsWithAlphabets = false;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)

        String url=null;
		String region=null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		try
		{
			if(data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) != null)
			{
				
				url= (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
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
				data.addToLog("API URL: ",url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
			//UAT ENV CHANGE START(SHAIK,PRIYA)
				if("YES".equalsIgnoreCase(UAT_FLAG)){
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
						//CALLER VERIFICATION
							data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));
							
							data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
							
						 
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
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE) && (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName,"ForemostPolicyInquiry API call", strReqBody,region,(String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				objHostDetails.startHostReport(currElementName,"ForemostPolicyInquiry API call", strReqBody,region,(String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			
			
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
		String wsurl=null;
		String region=null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL) != null) {
				 wsurl = (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
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
				//UT ENV CHANGE START(SHAIK,PIYA)
				data.addToLog("API URL: ",wsurl);
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
						//CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));
						
						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
						
						
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
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE) && (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName,"MulesoftFarmerPolicyInquiry_Post API call", strReqBody,region,(String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
				objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				objHostDetails.startHostReport(currElementName,"MulesoftFarmerPolicyInquiry_Post API call", strReqBody,region,(String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for MulesoftFarmerPolicyInquiry_Post API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
		
		

	}

	private String policyInquiry_RetriveInsurancePoliciesByParty(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    	//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
        String wsurl=null;
		String region=null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		try {
			if(data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL) != null)
			{
				 wsurl = (String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
				String phoneNo = (String) data.getSessionData(Constants.S_ANI);
				if(data.getSessionData(Constants.S_TELEPHONENUMBER) != null && !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
					phoneNo = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				}
				String policyNum = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE START(SHIAK,PRIYA)
				data.addToLog("API URL: ",wsurl);
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
				
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				org.json.simple.JSONObject responses = lookups.GetPolicyInquiry_RetrieveInsurance(wsurl,tid,phoneNo,Integer.parseInt(connTimeout),Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
			//UAT CHANGE END(SHAIK,PRIYA)
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
						//CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));
						
						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
						
						
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
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE) && (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName,"BW policyinquiry-retrieveInsurancePoliciesByParty API call", strReqBody,region,(String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL));
				objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				objHostDetails.startHostReport(currElementName,"BW policyinquiry-retrieveInsurancePoliciesByParty API call", strReqBody,region,(String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			
			
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
						
						//CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName : Wyoming)
						String strPolicyState = (String) beanObj.getStrPolicyState();
						data.setSessionData(Constants.S_POLICY_STATE_CODE,strPolicyState);
						data.addToLog(currElementName, "Policy State Code :" + strPolicyState);
						String strPolicyStateName=setStateName(data, caa, currElementName, strPolicyState);
						data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);
				
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
					
					//START : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add Routing for 'True Direct' HouseAccountSubCategor
					if (policyData.containsKey("subCategory")) {
						
						String apiSubcategory = (String) policyData.get("subCategory");
						beanObj.setStrsubCategory(apiSubcategory);
						data.addToLog(currElementName, "apiSubcategory :: " + apiSubcategory);
						data.setSessionData("S_API_SUBCATEGORY", apiSubcategory);
						
						String strsubcategory=setSubcategory(data, caa, currElementName, apiSubcategory);
			            data.addToLog(currElementName, "Subcategory : "+strsubcategory);

						
						if(strsubcategory.equalsIgnoreCase(apiSubcategory)) {
							data.setSessionData(Constants.S_POLICY_ATTRIBUTES, apiSubcategory);
							data.addToLog(currElementName, "S_API_SUBCATEGORY : "+apiSubcategory);
							
						}else {
								if (policyData.containsKey("agentReference")) {
									agentAORid = (String) policyData.get("agentReference");
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
						}
					}
					//END : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add Routing for 'True Direct' HouseAccountSubCategor
					
					
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
							PolicyBean obj = policyDetailsMap.get(key1);
							data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
							data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
							data.addToLog("DOB: ", obj.getStrDOB());
							data.addToLog("ZIP: ", obj.getStrZipCode());
							data.setSessionData("DOB_CHECK", "YES");
							data.setSessionData("ZIP_CHECK","YES");
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
							data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
						}
					}
					
					data.addToLog(currElementName, "Value of S_POLICY_STATE_CODE : " + data.getSessionData(Constants.S_POLICY_STATE_CODE));
					//DOB BYPASS CHANGE
					if(null==data.getSessionData(Constants.S_API_DOB) |data.getSessionData(Constants.S_API_DOB).toString().isEmpty()) {
						data.setSessionData("DOB_CHECK", "NO");
						data.addToLog("DOB_CHECK", data.getSessionData("DOB_CHECK").toString());
					}
					if(null==data.getSessionData(Constants.S_API_ZIP) || data.getSessionData(Constants.S_API_ZIP).toString().isEmpty()) {
						data.setSessionData("ZIP_CHECK", "NO");
						data.addToLog("ZIP_CHECK", data.getSessionData("ZIP_CHECK").toString());
					}
					
				//DOB BYPASS CHANGE
					strExitState = Constants.SU;
				} else if (!singleproducttype) {
					if(autoprodtypecount >= 1) prompt = prompt+" an Auto policy"; 
					if (homeprodtypecount >= 1) prompt = prompt+" a Home Policy"; 
					if (umbrellaprodtypecount >= 1) prompt = prompt+" or an Umbrella Policy"; 
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						data.addToLog(currElementName, "Enterd auto policy replace condition");
						prompt = prompt.replace(" an Auto policy", " una póliza de auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy", " una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy"," o una póliza de paraguas");
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
					
					if(autoprodtypecount >= 1) prompt = " Auto "; 
					else if (homeprodtypecount >= 1) prompt = " Home "; 
					else if (umbrellaprodtypecount >= 1) prompt = " an Umbrella ";
					
					
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
						
						//CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName : Wyoming)
						String strPolicyState = (String) beanObj.getStrPolicyState();
						data.setSessionData(Constants.S_POLICY_STATE_CODE,strPolicyState);
						data.addToLog(currElementName, "Policy State Code :" + strPolicyState);
						String strPolicyStateName=setStateName(data, caa, currElementName, strPolicyState);
						data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);
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
						data.addToLog(currElementName, "AOR ID :: " + agentAORid);
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
							PolicyBean obj = policyDetailsMap.get(key1);
							data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());							
							data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());							
							data.addToLog("DOB: ", obj.getStrDOB());
							data.addToLog("ZIP: ", obj.getStrZipCode());
							data.setSessionData("DOB_CHECK", "YES");
							data.setSessionData("ZIP_CHECK","YES");
							data.setSessionData("POLICY_PRODUCT_CODE", obj.getStrPolicyProductCode());
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrPolicyProductCode()+key1);
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
						}
					}
				//DOB BYPASS CHANGE
					if(null==data.getSessionData(Constants.S_API_DOB) || data.getSessionData(Constants.S_API_DOB).toString().isEmpty()) {
						data.setSessionData("DOB_CHECK", "NO");
						data.addToLog("DOB_CHECK", data.getSessionData("DOB_CHECK").toString());
					}
					if(null==data.getSessionData(Constants.S_API_ZIP) || data.getSessionData(Constants.S_API_ZIP).toString().isEmpty()) {
						data.setSessionData("ZIP_CHECK", "NO");
						data.addToLog("ZIP_CHECK", data.getSessionData("ZIP_CHECK").toString());
					}
					
				//DOB BYPASS CHANGE
					strExitState = Constants.SU;
				} else if (!singleproducttype) {
					if(autoprodtypecount >= 1) prompt = prompt+" an Auto policy"; 
					if (homeprodtypecount >= 1) prompt = prompt+" a Home Policy"; 
					if (mrprodtypecount >= 1) prompt = prompt+" a Boat Policy"; 
					if (umbrellaprodtypecount >= 1) prompt = prompt+" or an Umbrella Policy";  
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						data.addToLog(currElementName, "Enterd auto policy replace condition");
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
					if(autoprodtypecount >= 1) prompt = " Auto "; 
					else if (homeprodtypecount >= 1) prompt = " Home "; 
					else if (umbrellaprodtypecount >= 1) prompt = " Umbrella ";
					else if(mrprodtypecount >= 1) prompt = " Boat ";
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace(" Auto "," auto ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace(" Home "," casa ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella")) {
						prompt = prompt.replace(" Umbrella "," paraguas ");
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

	private String apiResponseManupulation_MdmFwsPolicyLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {			
		String strExitState = Constants.ER;
		JSONArray policiesArr=null;
		JSONArray insuredDetails=null;
		JSONArray fwsPolicyArray = new JSONArray();
		JSONArray insuredDetailsArray = new JSONArray();
		JSONArray postalAdddressArray = new JSONArray();
		PolicyBean beanObj = new PolicyBean();
		JSONObject customerNamesObj = null;
		JSONObject policyData = null;
		JSONArray customerNamesArr = null;
		String lob = null;
		String prompt = Constants.EmptyString;
		String strPolicyNum = Constants.EmptyString;
		String policySource = Constants.EmptyString;
		int autoprodtypecount = 0;
		int homeprodtypecount = 0;
		int umbrellaprodtypecount = 0;
		int mrprodtypecount = 0;
		boolean singleproducttype = false;
		String strDOB_2 ="";
		String temp = "";
		Map<String, Integer> productTypeCounts = new HashMap<>();

		
		try {
		
			//caputuring JSON response into JSON objects & array
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject results = (JSONObject) resp.get("results");
			JSONArray enterpriseHousehold = (results.containsKey("enterpriseHousehold")&&results.get("enterpriseHousehold") != null ) 
				    ? (JSONArray) results.get("enterpriseHousehold") 
				    : new JSONArray();
			JSONArray customers = (results.containsKey("customers")&&results.get("customers") != null) 
				    ? (JSONArray) results.get("customers") 
				    : new JSONArray();
			data.addToLog(currElementName, "MDM _API Response :"+resp.toJSONString());
			//taking enterpriseHousehold into policiesArr and insuredDetails
			if(enterpriseHousehold.size()>0) {
			for (int singleEnterprise = 0; singleEnterprise < enterpriseHousehold.size(); singleEnterprise++) {
			   
			    JSONObject currentEnterprise = (JSONObject) enterpriseHousehold.get(singleEnterprise);

			    // Checking if the current object contains "policies" and assign to policiesArr
			    if (currentEnterprise.containsKey("policies")) {
			    	 JSONArray policiesArray = (JSONArray) currentEnterprise.get("policies");
			        policiesArr = getPoliciesMatchingFWS(policiesArray);
			        
			        fwsPolicyArray.add(policiesArr);
			    }else {
			    	data.addToLog(currElementName, "policies does not exist in enterpriseHousehold");
			    }

			    // Checking if the current object contains "insuredDetails" and assign to insuredDetailsimport org.json.JSONObject;
			    if (currentEnterprise.containsKey("insuredDetails")) {
			        insuredDetails = (JSONArray) currentEnterprise.get("insuredDetails");
			        insuredDetailsArray.add(insuredDetails);
			    }else {
			    	data.addToLog(currElementName, "insuredDetails does not exist in enterpriseHousehold");
			    }
			}
		}else {
			data.addToLog(currElementName, "Enterprise house hold array is empty from the response:"+enterpriseHousehold.size());
		}
			
			if(customers.size() > 0) {
				for(int singleCustomer = 0 ; singleCustomer < customers.size(); singleCustomer++) {
					 JSONObject currentCustomer = (JSONObject) customers.get(singleCustomer);
					 // Checking if the current customer object contains "policies" and assign to policiesArr
					    if (currentCustomer.containsKey("policies")) {
					    	  JSONArray policiesArray = (JSONArray) currentCustomer.get("policies");
					        policiesArr = getPoliciesMatchingFWS(policiesArray);
					       
					        fwsPolicyArray.add(policiesArr);
					    }else {
					    	data.addToLog(currElementName, "policies does not exist in customers:"+policiesArr.size());
					    }

					    // Checking if the current customer object contains "insuredDetails" and assign to insuredDetailsimport org.json.JSONObject;
					    if (currentCustomer.containsKey("insuredDetails")) {
					        insuredDetails = (JSONArray) currentCustomer.get("insuredDetails");
					        insuredDetailsArray.add(insuredDetails);
					    }else {
					    	data.addToLog(currElementName, "insuredDetails does not exist in customers");
					    }
				}
			}else {
				data.addToLog(currElementName, "customers array is empty from the response:"+customers.size());
			}

			// checking insuredDetails and date of birth
			data.addToLog(currElementName, "InsuredDetails Array MDM :"+insuredDetailsArray.toJSONString());
			data.addToLog(currElementName,"InsuredDetails Array Size MDM::"+insuredDetailsArray.size());
			if (insuredDetailsArray != null && !insuredDetailsArray.isEmpty() && insuredDetailsArray.size() > 0) {

				for (int singleCus = 0; singleCus < insuredDetailsArray.size(); singleCus++) {
					JSONArray insuredDetailsArr = (JSONArray) insuredDetailsArray.get(singleCus);
					for(int singleInsured = 0 ;singleInsured < insuredDetailsArr.size();singleInsured++ ) {
						JSONObject insuredDetailsObj = (JSONObject)insuredDetailsArr.get(singleInsured);
						String strDOB = (String) insuredDetailsObj.get("dob");
					    data.addToLog(currElementName, "DOB after sub string MDM :"+strDOB);					
					    if(insuredDetailsObj.containsKey("postalAddress")) {
					    	data.addToLog(currElementName, "postal address adding...");
					    	postalAdddressArray.add((JSONArray)insuredDetailsObj.get("postalAddress"));
					    }else {
					    	data.addToLog(currElementName, "Postal Address not available in Insured Details");
					    }
					    if(strDOB!=null && !strDOB.isEmpty()) {							
		                // Extract the first 10 characters of DOB
		                strDOB = strDOB.substring(0, Math.min(strDOB.length(), 10));
		                data.addToLog(currElementName, "Truncated DOB: " + strDOB);
		                
		                strDOB_2 = (strDOB_2.equals(""))?""+strDOB:strDOB_2+","+strDOB;
		                	            
		            }
		        }
		    }
			
			    beanObj.setStrDOB(strDOB_2);
			    //data.setSessionData(Constants.S_API_DOB, beanObj.getStrDOB());
			    
			
			} else {
			       data.addToLog(currElementName, "Insured Details array : "+insuredDetailsArray);
				// Get the first insuredDetails object
					JSONArray insuredDetailsArr = (JSONArray)insuredDetailsArray.get(0);
					data.addToLog(currElementName, "Insured Details array get o'th Index:"+insuredDetailsArray.get(0).toString());
					for(int singleInsured = 0 ; singleInsured < insuredDetailsArr.size();singleInsured++) {
						JSONObject insuredDetailsObj = (JSONObject)insuredDetailsArr.get(singleInsured);
				if (insuredDetailsObj.containsKey("customerNames")) {
					 customerNamesArr = (JSONArray) insuredDetailsObj.get("customerNames");
					 String strDOB = (String) insuredDetailsObj.get("dob");
					 if(strDOB!=null && !strDOB.isEmpty()) {							
			                // Extract the first 10 characters of DOB
			                strDOB = strDOB.substring(0, Math.min(strDOB.length(), 10));   
			                strDOB_2 = (strDOB_2.equals(""))?""+strDOB:strDOB_2+","+strDOB;
			                beanObj.setStrDOB(strDOB_2);
			                	            
			            }else {
			            	data.addToLog(currElementName, "DOB is not present in 0th index Insured Details Array:"+strDOB);
			            }
					if (!customerNamesArr.isEmpty()) {
						// Access the first customerNames object
						customerNamesObj = (JSONObject) customerNamesArr.get(0);

						// Extract firstName and lastName
						if (customerNamesObj.containsKey("firstName") && customerNamesObj.get("firstName") != null) {
							beanObj.setStrFirstName((String) customerNamesObj.get("firstName"));
						}

						if (customerNamesObj.containsKey("lastName") && customerNamesObj.get("lastName") != null) {
							beanObj.setStrLastName((String) customerNamesObj.get("lastName"));
						}
					} else {
						data.addToLog(currElementName, "customerNames array is empty in insuredDetails at index 0.");
					}
				} else {
					data.addToLog(currElementName, "customerNames array does not exist in insuredDetails at index 0.");
				}
			}
				
			
			}
			data.addToLog(currElementName, "Final Concatenated DOBs: " + beanObj.getStrDOB());
			data.addToLog(currElementName, "Fws policies before sorting:"+fwsPolicyArray.size());
			
			 //filter the policies and remove duplicates
		    data.addToLog(currElementName, "policies array before removing duplicates:"+fwsPolicyArray);
		    policiesArr= filterDuplicatePoliciesByNumber(policiesArr);
		    data.addToLog(currElementName, "policies array after removing duplicates:"+policiesArr);
		    data.addToLog(currElementName,"Length of policy array is: "+ policiesArr.size());
		    
			   // sort the policies array based on Inception Date
		        if(policiesArr.size()>1) {
			    policiesArr = filterPoliciesByInceptionDate(policiesArr,data);
			    }
			    data.addToLog(currElementName, "policies after sorting by inception Date:"+policiesArr);
			    data.addToLog(currElementName, "policies after sorting by inception Date size:"+policiesArr.size());
				
				if (policiesArr != null && policiesArr.size() == 1) {		
						policyData = (JSONObject) policiesArr.get(0);

						if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
							lob = (String) policyData.get("lineOfBusiness");
							data.setSessionData("S_LOB", lob);
						}
					
				}
				
				if(policiesArr != null && policiesArr.size() > 0) {
					data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
					data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
					HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String,PolicyBean>>();
					for(int i = 0; i < policiesArr.size(); i++) {
						
						 policyData = (JSONObject) policiesArr.get(i);
						
						if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
							lob = (String) policyData.get("lineOfBusiness");
							data.setSessionData(Constants.S_FWS_POLICY_LOB, lob);
							beanObj.setStrPolicyLOB(lob);
							if (productTypeCounts.containsKey(lob)) productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
							else productTypeCounts.put(lob, 1);
						}
						
						
						String zip = "";
						if (policyData.containsKey("billingAddress") && null != policyData.get("billingAddress")) {
							JSONArray billingAddressesArr = (JSONArray)policyData.get("billingAddress");
							//JSONObject billingAddressesObj = (JSONObject) billingAddressesArr.get(0);
							for(int singleBillingAddress = 0 ; singleBillingAddress < billingAddressesArr.size();singleBillingAddress++) {
								JSONObject billingAddressesObj = (JSONObject)billingAddressesArr.get(singleBillingAddress);
								if (billingAddressesObj !=null && billingAddressesObj.containsKey("postalcode")) {
								    String postalCode = (String) billingAddressesObj.get("postalcode");
								    if (postalCode != null && postalCode.isEmpty() && postalCode.length() >= 5) {       
								            zip = zip.equals("") ? postalCode.substring(0, 5) : zip + "," + postalCode.substring(0, 5);
								        }else {
								        	data.addToLog(currElementName, "postalCode or zip code is null or not in proper format in billing address:" + (String) billingAddressesObj.get("postalcode"));
											
								        }
								    }
							beanObj.setStrZipCode(zip);
							if (billingAddressesObj.containsKey("state")) beanObj.setStrPolicyState((String)billingAddressesObj.get("state"));
							
							//CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName : Wyoming)
							String strPolicyState = (String) beanObj.getStrPolicyState();
							data.setSessionData(Constants.S_POLICY_STATE_CODE,strPolicyState);
							data.addToLog(currElementName, "Policy State Code :" + strPolicyState);
							String strPolicyStateName=setStateName(data, caa, currElementName, strPolicyState);
							data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);
							}
							
						}
						data.addToLog(currElementName, "Postal Address Array:" + postalAdddressArray.toJSONString());
						if (!postalAdddressArray.isEmpty() && postalAdddressArray.size() > 0) {

							for (int singlePostalAddress = 0; singlePostalAddress < postalAdddressArray
									.size(); singlePostalAddress++) {
								JSONArray singlePostalAddressesArray = (JSONArray) postalAdddressArray
										.get(singlePostalAddress);
								for(int singlePostalArrofArr = 0; singlePostalArrofArr < singlePostalAddressesArray.size();singlePostalArrofArr++) {
									JSONObject postalAddressesObj = (JSONObject)singlePostalAddressesArray.get(singlePostalArrofArr);
								if (postalAddressesObj != null && postalAddressesObj.containsKey("PostalCode")) {
									String postalCode = (String) postalAddressesObj.get("PostalCode");
									String nonHyphenPostalCode = postalCode.replace("-", "");
									//data.addToLog(currElementName, "Non Hyphen Postal Code in postal address object" + nonHyphenPostalCode);
									if (nonHyphenPostalCode != null && !nonHyphenPostalCode.isEmpty() && nonHyphenPostalCode.length() >= 5) {
										zip = zip.equals("") ? nonHyphenPostalCode.substring(0, 5)
												: zip + "," + nonHyphenPostalCode.substring(0, 5);
										beanObj.setStrZipCode(zip);
										//data.addToLog(currElementName, "Zip code is added in policy bean:"+beanObj.getStrZipCode());
									} else {
										data.addToLog(currElementName,
												"Non Hiphen postalCode or zip code is null or not in proper format in postal address:"
														+ nonHyphenPostalCode);
									}
									
									
								}
								if (postalAddressesObj.containsKey("State")) beanObj.setStrPolicyState((String)postalAddressesObj.get("State"));
								String strPolicyState = (String) beanObj.getStrPolicyState();
								data.setSessionData(Constants.S_POLICY_STATE_CODE,strPolicyState);
								data.addToLog(currElementName, "Policy State Code in Postal Address:" + strPolicyState);
								String strPolicyStateName=setStateName(data, caa, currElementName, strPolicyState);
								data.addToLog(currElementName, "Policy State Name Using Postal State:" + strPolicyStateName);
								
							}
						}
						} else {
							data.addToLog(currElementName,
									"Postal Address array is empty with size:" + postalAdddressArray.size());
						}
						
	
						if (policyData.containsKey("policyNumber") && null != policyData.get("policyNumber")) {
							strPolicyNum = (String) policyData.get("policyNumber");
							beanObj.setStrPolicyNum(strPolicyNum);
						}
						
						//CS1151307 : Update Policy State Name(Policy State : HI, PolicyStateName : Hawaii)
						if (policyData.containsKey("policyStateCode") && policyData.get("policyStateCode") != null 
								&& !policyData.get("policyStateCode").toString().isEmpty()) {
							beanObj.setStrPolicyState((String) policyData.get("policyStateCode"));
							String strPolicyState = beanObj.getStrPolicyState();
							data.setSessionData(Constants.S_POLICY_STATE_CODE, strPolicyState);
							data.addToLog(currElementName, "Policy State Code: " + strPolicyState);
							String strPolicyStateName = setStateName(data, caa, currElementName, strPolicyState);
							data.addToLog(currElementName, "Policy State Name: " + strPolicyStateName);
						}
						
						if(policyData.containsKey("policySource") && null != policyData.get("policySource")) beanObj.setStrPolicySource((String)policyData.get("policySource"));
						if(policyData.containsKey("policyState") && null != policyData.get("policyState")) beanObj.setStrPolicyState((String)policyData.get("policyState"));
						if(policyData.containsKey("suffix") && null != policyData.get("suffix")) beanObj.setStrPolicySuffix((String)policyData.get("suffix"));
						if(policyData.containsKey("termStartDate") && policyData.get("termStartDate") !=null);
						{	
							String EffDate=formatDate(data, (String) policyData.get("termStartDate"),"yyyy-MM-dd HH:mm:ss.S","MM/dd/yyyy");
							beanObj.setStrEffectiveDate(EffDate);
							data.addToLog(currElementName,"StrEffectiveDate :" +beanObj.getStrEffectiveDate());
							data.setSessionData("S_FWS_POLICY_EFF_DATE", EffDate);
						}
						
						//if(policyData.containsKey("InternalPolicyVersion") && null != policyData.get("InternalPolicyVersion")) beanObj.setStrInternalPolicyNumber((String)policyData.get("InternalPolicyVersion"));
						//if(policyData.containsKey("InternalPolicyNumber") && null != policyData.get("InternalPolicyNumber")) beanObj.setStrInternalPolicyVersion((String)policyData.get("InternalPolicyNumber"));
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
						case Constants.PRODUCTTYPE_HOME:
							tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
							break;
						case Constants.PRODUCTTYPE_F:
							tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
							break;
						case Constants.PRODUCTTYPE_AUTO:
							tmpKey = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
							break;
						case Constants.PRODUCTTYPE_UMBRELLA:
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
					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_HOME)) {
						homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_HOME);
					}
					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_F)) {
						homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_F);
					}
					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_AUTO)) {
						autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_AUTO);
					}
					if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_UMBRELLA)) {
						umbrellaprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_UMBRELLA);
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
							data.addToLog(currElementName,"policyDetailsMap"+policyDetailsMap);
							for (String key1 : policyDetailsMap.keySet()) {
								PolicyBean obj = policyDetailsMap.get(key1);
								data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
								data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
								data.addToLog("DOB: ", obj.getStrDOB());
								data.addToLog("ZIP: ", obj.getStrZipCode());
								data.setSessionData(Constants.S_POLICY_NUM, key1);
								data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
								data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
								data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
								
								data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", lob);
								data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
							}
						}
						//DOB BYPASS CHANGE
						if(null==data.getSessionData(Constants.S_API_DOB) || data.getSessionData(Constants.S_API_DOB).toString().isEmpty()) {
							data.setSessionData("DOB_CHECK", "NO");
							data.addToLog("DOB_CHECK", data.getSessionData("DOB_CHECK").toString());
						}
						if(null==data.getSessionData(Constants.S_API_ZIP) || data.getSessionData(Constants.S_API_ZIP).toString().isEmpty()) {
							data.setSessionData("ZIP_CHECK", "NO");
							data.addToLog("ZIP_CHECK", data.getSessionData("ZIP_CHECK").toString());
						}
						
					//DOB BYPASS CHANGE
						strExitState = Constants.SU;
					} else if (!singleproducttype) {
						if(autoprodtypecount >= 1) prompt = prompt+" an Auto policy"; 
						if (homeprodtypecount >= 1) prompt = prompt+" a Home Policy"; 
						if (mrprodtypecount >= 1) prompt = prompt+" a Boat Policy"; 
						if (umbrellaprodtypecount >= 1) prompt = prompt+" or an Umbrella Policy"; 
						
						String lang = (String) data.getSessionData("S_PREF_LANG");
						
						if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
							data.addToLog(currElementName, "Enterd auto policy replace condition");
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
							prompt = prompt.replace("  Auto "," auto ");
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
			data.addToLog(currElementName,"Exception in MdmFWSPhoneNoPolicyLookup method  :: "+e);
			caa.printStackTrace(e);
		}
	return strExitState;

}		

	private JSONArray filterDuplicatePoliciesByNumber(JSONArray policiesArr) {
		 HashSet<String> uniquePolicyNumbers = new HashSet<>();  
	       JSONArray filteredPolicies = new JSONArray();  

	        for (int singlePolicy = 0; singlePolicy < policiesArr.size(); singlePolicy++) {  
	        	 JSONObject policyData = (JSONObject) policiesArr.get(singlePolicy);
	        	 String policyNumber = (String) policyData.get("policyNumber");	            
	        	 if (uniquePolicyNumbers.add(policyNumber)) {  
	                filteredPolicies.add(policyData); // Only add if policyNumber was not already present  
	            }  
	        }  
	        
	        return filteredPolicies; 
	}
	private JSONArray getPoliciesMatchingFWS(JSONArray policies) {
	    JSONArray matchingPolicies = new JSONArray();
	    
	    for (int singlePolicy = 0; singlePolicy < policies.size(); singlePolicy++) {
	        JSONObject policyData = (JSONObject) policies.get(singlePolicy);
	        String policySource = (String) policyData.get("policySource");
	        
	        if (null != policyData &&policyData.containsKey("policySource") 
	                && policySource != null 
	                && policySource.contains("FWS")) {
	            matchingPolicies.add(policyData);
	        }
	    }
	    return matchingPolicies;
	}
	

	private JSONArray filterPoliciesByInceptionDate(JSONArray policies, DecisionElementData data) {
		data.addToLog(data.getCurrentElement(),"policies enter in filterPoliciesByInceptionDate method"+policies);
	    // Prepare to hold the latest policies
	    JSONArray latestPoliciesArray = new JSONArray();
	    // SimpleDateFormat to compare dates
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	    // List to hold policies for sorting
	    List<JSONObject> policyList = new ArrayList<>();
	    if (policies != null) {
	        for (int singlepolicyArr = 0;singlepolicyArr < policies.size();singlepolicyArr++) {
	            JSONArray policyArray = (JSONArray) policies.get(singlepolicyArr);
	            for(int singlePolicyObject = 0 ;singlePolicyObject < policyArray.size();singlePolicyObject++) {
	            	JSONObject policy = (JSONObject)policyArray.get(singlePolicyObject);
	            String inceptionDateStr = (String) policy.get("inceptionDate");
	            if (inceptionDateStr != null && !inceptionDateStr.isEmpty()) {
	                data.addToLog(data.getCurrentElement(), "Inception date present in policies loop: " + inceptionDateStr);
	                try {
	                    // Parse the inception date
	                    Date inceptionDate = sdf.parse(inceptionDateStr);
	                    // Store the parsed date in the policy for sorting
	                    policy.put("inceptionDateParsed", inceptionDate);
	                    policyList.add(policy);
	                } catch (java.text.ParseException e) {
	                    e.printStackTrace();
	                }
	            } else {
	                data.addToLog(data.getCurrentElement(),
	                        "Inception date field is not present in MDM Policies array filter failed: " + inceptionDateStr);
	            }
	        }
	        }
	        // If there are multiple policies, sort them by inception date in descending order (latest date first)
	        if (policyList.size() > 1) {
	            policyList.sort((policy1, policy2) -> {
	                Date date1 = (Date) policy1.get("inceptionDateParsed");
	                Date date2 = (Date) policy2.get("inceptionDateParsed");
	                return date2.compareTo(date1); // Descending order
	            });
	        }
	        // Add all policies, even if they have the same inception date, to the result array
	        latestPoliciesArray.addAll(policyList);
	    } else {
	        data.addToLog(data.getCurrentElement(),
	                "MDM Policies are empty filter failed: " + policies.size());
	    }
	    return latestPoliciesArray;
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
						beanObj  =	productbeanObj.get(strPolicyNum);
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
						data.addToLog(currElementName, "AOR ID :: " + agentAORid);
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
							PolicyBean obj = policyDetailsMap.get(key1);
							data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
							data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
							data.addToLog("DOB: ", obj.getStrDOB());
							data.addToLog("ZIP: ", obj.getStrZipCode());
							data.setSessionData("DOB_CHECK", "YES");
							data.setSessionData("ZIP_CHECK","YES");
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
						}
					}
					
					//DOB BYPASS CHANGE
					if(null==data.getSessionData(Constants.S_API_DOB) || data.getSessionData(Constants.S_API_DOB).toString().isEmpty()) {
						data.setSessionData("DOB_CHECK", "NO");
						data.addToLog("DOB_CHECK", data.getSessionData("DOB_CHECK").toString());
					}
					if(null==data.getSessionData(Constants.S_API_ZIP) || data.getSessionData(Constants.S_API_ZIP).toString().isEmpty()) {
						data.setSessionData("ZIP_CHECK", "NO");
						data.addToLog("ZIP_CHECK", data.getSessionData("ZIP_CHECK").toString());
					}
					
				//DOB BYPASS CHANGE
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
					
					if (null != policyData && policyData.containsKey("agentDetails")) {
						JSONObject agentdetails = (JSONObject) policyData.get("agentDetails");
						agentAORid = (String) agentdetails.get("agentCode");
						data.addToLog(currElementName, "AOR ID :: " + agentAORid);
						//beanObj.setStrAgentAORID(agentAORid);
						data.setSessionData("S_AGENT_ID", agentAORid);
					}
					
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
						data.setSessionData("ZIP_CHECK","YES");
						
						data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
						data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_SINGLE_POLICY_FOUND));
						data.addToLog(currElementName,"S_API_DOB : "+namedInsuredData.get("birthDate"));
						
						
						if (policyData.containsKey("namedInsured")) {
							JSONObject obj = (JSONObject)policyData.get("namedInsured");
							strDOB = (strDOB.equals(""))?""+obj.get("birthDate"):strDOB+","+obj.get("birthDate");
							data.setSessionData(Constants.S_API_DOB, ""+strDOB);
							data.addToLog(currElementName,"S_API_DOB : "+strDOB);
							data.setSessionData("DOB_CHECK", "YES");
							
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
				
				//DOB BYPASS CHANGE
				if(null==data.getSessionData(Constants.S_API_DOB) || data.getSessionData(Constants.S_API_DOB).toString().isEmpty()) {
					data.setSessionData("DOB_CHECK", "NO");
					data.addToLog("DOB_CHECK", data.getSessionData("DOB_CHECK").toString());
				}
				if(null==data.getSessionData(Constants.S_API_ZIP) || data.getSessionData(Constants.S_API_ZIP).toString().isEmpty()) {
					data.setSessionData("ZIP_CHECK", "NO");
					data.addToLog("ZIP_CHECK", data.getSessionData("ZIP_CHECK").toString());
				}
				
			//DOB BYPASS CHANGE
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
				String concatenatedDOB = Constants.EmptyString;
				PolicyBean beanObj = new PolicyBean();
					
				if (policySummaryObj.containsKey("address")) {
					JSONArray autoPoliciesarr = (JSONArray)policySummaryObj.get("address");
					JSONObject addressObj = (JSONObject) autoPoliciesarr.get(0);
					if (addressObj.containsKey("zip")) beanObj.setStrZipCode((String)addressObj.get("zip"));
				
					//CS1151307 : Update Policy State Name(Policy State : HI, PolicyStateName : Hawaii)
					if (addressObj.containsKey("state")) beanObj.setStrPolicyState((String)addressObj.get("state"));
					String strPolicyState = (String) beanObj.getStrPolicyState();
					data.setSessionData(Constants.S_POLICY_STATE_CODE,strPolicyState);
					data.addToLog(currElementName, "Policy State Code :" + strPolicyState);
					String strPolicyStateName=setStateName(data, caa, currElementName, strPolicyState);
					data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);
				}
					
					
					
					if (policySummaryObj.containsKey("autoPolicy")) {
						JSONObject autoPolicyObj = (JSONObject) policySummaryObj.get("autoPolicy");
						JSONArray driverssarr = (JSONArray)autoPolicyObj.get("drivers");
						for (Object driverArrIterator : driverssarr) {
							JSONObject driverObject = (JSONObject) driverArrIterator;
							if (driverObject.containsKey("dateofbirth")) {
								if ("".equalsIgnoreCase(concatenatedDOB)) {
									concatenatedDOB = (String) driverObject.get("dateofbirth");
								}
								else {
									concatenatedDOB = concatenatedDOB + "," + (String) driverObject.get("dateofbirth");
								}
							}
						}
						data.addToLog(currElementName, "Concatenated DOB :: " + concatenatedDOB);
						beanObj.setStrDOB(concatenatedDOB);
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
							PolicyBean obj = policyDetailsMap.get(key1);
							data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
							data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
							data.addToLog("DOB: ", obj.getStrDOB());
							data.addToLog("ZIP: ", obj.getStrZipCode());
							data.setSessionData("DOB_CHECK","YES");
							data.setSessionData("ZIP_CHECK","YES");
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
							data.setSessionData(Constants.S_POLICY_SOURCE, strPolicySourceSystem);
						}
					}
					
					//DOB BYPASS CHANGE
					if(null==data.getSessionData(Constants.S_API_DOB) || data.getSessionData(Constants.S_API_DOB).toString().isEmpty()) {
						data.setSessionData("DOB_CHECK", "NO");
						data.addToLog("DOB_CHECK", data.getSessionData("DOB_CHECK").toString());
					}
					if(null==data.getSessionData(Constants.S_API_ZIP) || data.getSessionData(Constants.S_API_ZIP).toString().isEmpty()) {
						data.setSessionData("ZIP_CHECK", "NO");
						data.addToLog("ZIP_CHECK", data.getSessionData("ZIP_CHECK").toString());
					}
					
				//DOB BYPASS CHANGE
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
				 

					// Retrieve the configuration categories from the ivrconfig properties
		            String configCategories = (String) data.getSessionData("S_CONFIG_CATEGORIES");
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
				
//START : CS1245054:Mdm FWS PhoneNoLookup changes
				public static String formatDate(DecisionElementData data, String dateStr, String inputFormat, String outputFormat) {
				
					data.addToLog(data.getCurrentElement(), "Inside method formatDate : "+dateStr);
					data.addToLog(data.getCurrentElement(), "Inside method formatDate : "+inputFormat);
					data.addToLog(data.getCurrentElement(), "Inside method formatDate : "+outputFormat);
					
				
					if (dateStr == null || inputFormat == null || outputFormat == null) {
						return "EmptyDate"; //Exception will occurred based on requirement 
						
						}

					    SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat);
					    SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat);
					    
						data.addToLog(data.getCurrentElement(), "SimpleDateFormat inputSDF : "+inputSDF);
						data.addToLog(data.getCurrentElement(), "SimpleDateFormat outputSDF : "+outputSDF);
					    

					    try {
					    //Input date 
					        Date date = inputSDF.parse(dateStr);
							data.addToLog(data.getCurrentElement(), "Date : "+date);
					    
					        return outputSDF.format(date);
					   
					    } catch (Exception e) {

					        return e.getMessage(); // or throw an exception, depending on your requirements
					    }
					}					
				
//END : CS1245054:Mdm FWS PhoneNoLookup changes
	
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

