package com.farmers.shared.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.FWSBillingLookup_Get;
import com.farmers.FarmersAPI.FWSPolicyLookup_Post;
import com.farmers.FarmersAPI_NP.FWSPolicyLookup_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FWS_SBP_POLICY_LOOKUP extends DecisionElementBase{

	private String strReqBody;

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			strExitState = readPolicyLookupResp(caa, data);
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in Validate_FWSARC_HOST_001 :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	
	private String readPolicyLookupResp(CommonAPIAccess caa, DecisionElementData data) {
		String exitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		String region=null;
		HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		
		try {

			if(data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL) != null &&  
					data.getSessionData(Constants.S_CONN_TIMEOUT) != null &&
					data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				String url = (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL);
				String callID = (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));

				data.addToLog(data.getCurrentElement(), "POLICY NUMBER : "+data.getSessionData(Constants.S_POLICY_NUM));
				data.addToLog(data.getCurrentElement(), "BILLING ACCT NUMBER : "+data.getSessionData(Constants.S_BILLING_ACC_NUM));

				JSONObject responseJSON = null;
				
				FWSPolicyLookup_Post test = new FWSPolicyLookup_Post();

				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				FWSPolicyLookup_NP_Post testNP = new FWSPolicyLookup_NP_Post();
				data.addToLog("API URL: ",url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				data.addToLog("Is UAT DNIS?: ", UAT_FLAG);
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					data.addToLog("S_FWS_POLICYLOOKUP_URL: ", url);
					String key= Constants.S_FWS_POLICYLOOKUP_URL;
					 region = regionDetails.get(key);
					if(data.getSessionData(Constants.S_POLICY_NUM) != null) {
						String policyNum = (String) data.getSessionData(Constants.S_POLICY_NUM);
						data.addToLog(data.getCurrentElement(), "Policy Number "+policyNum);
						responseJSON = testNP.start(url, callID, policyNum, null, null, conTimeout, readTimeout, context, region);
					}else if(data.getSessionData(Constants.S_BILLING_ACC_NUM) != null) {
						String billingAcctNum = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
						data.addToLog(data.getCurrentElement(), "Billing Account Number "+billingAcctNum);
						responseJSON = testNP.start(url, callID, null, billingAcctNum, null, conTimeout, readTimeout, context, region);
					}else {
						String ani = (String) data.getSessionData(Constants.S_ANI);
						responseJSON = testNP.start(url, callID, null, null, ani, conTimeout, readTimeout, context, region);
					}
					//END- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				}else {
			       region="PROD";
				if(data.getSessionData(Constants.S_POLICY_NUM) != null) {
					String policyNum = (String) data.getSessionData(Constants.S_POLICY_NUM);
					data.addToLog(data.getCurrentElement(), "Policy Number "+policyNum);
					responseJSON = test.start(url, callID, policyNum, null, null, conTimeout, readTimeout, context);
				}else if(data.getSessionData(Constants.S_BILLING_ACC_NUM) != null) {
					String billingAcctNum = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
					data.addToLog(data.getCurrentElement(), "Billing Account Number "+billingAcctNum);
					responseJSON = test.start(url, callID, null, billingAcctNum, null, conTimeout, readTimeout, context);
				}else {
					String ani = (String) data.getSessionData(Constants.S_ANI);
					responseJSON = test.start(url, callID, null, null, ani, conTimeout, readTimeout, context);
				}
			}
			
				data.addToLog(data.getCurrentElement(), "API Response : "+responseJSON);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responseJSON.get(Constants.RESPONSE_CODE));

				String strRespBody = "";
				if(responseJSON != null && responseJSON.containsKey(Constants.RESPONSE_CODE) && (int) responseJSON.get(Constants.RESPONSE_CODE) == 200 && responseJSON.containsKey(Constants.RESPONSE_BODY) && responseJSON.get(Constants.RESPONSE_BODY) != null) {
				    strReqBody = responseJSON.get(Constants.REQUEST_BODY).toString();
					data.addToLog(data.getCurrentElement(), "Set FWS Policy Lookup API Response into session with the key name of "+data.getCurrentElement()+Constants._RESP);
					strRespBody = responseJSON.get(Constants.RESPONSE_BODY).toString();
					//START Caller Verification fix for No caller name - Priya
					data.setSessionData("SIDA_MN_005_VALUE_RESP", responseJSON.get(Constants.RESPONSE_BODY));
					//CALLER VERIFICATION
					data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responseJSON.get(Constants.RESPONSE_BODY));
					data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW", (String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());
					//END Caller Verification fix for No caller name- Priya
					data.setSessionData(data.getCurrentElement()+Constants._RESP, responseJSON.get(Constants.RESPONSE_BODY));
					exitState = policyResponseManipulation(data, caa, strRespBody);
				}	
				
				try {
					objHostDetails.startHostReport(data.getCurrentElement(),"FWS Policy Lookup API call", strReqBody, region,data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL).toString().length() > 99 ?  (String)data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL).toString().substring(0, 99) : (String)data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL));
					objHostDetails.endHostReport(data,strRespBody , exitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
				} catch (Exception e) {
					data.addToLog(data.getCurrentElement(),"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: "+e);
					caa.printStackTrace(e);
				}

			}

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in Validate_FWSARC_HOST_001 :: "+e);
			caa.printStackTrace(e);
		}
		
		
		return exitState;
	}

	private String policyResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String strRespBody) {
		String exitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			if(resp.containsKey("policies")) {
				JSONArray policies = (JSONArray) resp.get("policies");
				for (Object policyObj : policies) {
					JSONObject policy = (JSONObject) policyObj;
					String lineOfBusiness = (String) policy.get("lineOfBusiness");
					String billingAcctNo = (String) policy.get("billingAccountNumber");
					String companyCode = "";
					//String acctNo = "";
					if(billingAcctNo!=null && billingAcctNo.length() > 2) {
						companyCode = billingAcctNo.substring(0, 2);
						//acctNo =  billingAcctNo.substring(2, billingAcctNo.length());
					}
					String internalPolicyNo = (String) policy.get("internalPolicyNum");
					String policySource = (String) policy.get("policySource");
					String suffix = (String) policy.get("suffix");
					String gpc =  (String) policy.get("GPC");
					String policyState = (String) policy.get("policyState");
					String effectiveDate =  (String) policy.get("effectiveDate");
					String callRoutingIndicator =  (String) policy.get("callRoutingIndicator");
					String producerRoleCode = (String) policy.get("producerRoleCode");
					String serviceLevel = "";
					if(policy.containsKey("serviceLevels")) {
						serviceLevel = (String) policy.get("serviceLevels");
					}
					
//					if(effectiveDate!=null && effectiveDate.length() >= 10) {
//						effectiveDate = effectiveDate.substring(0, 10);
//					}
					
					//Caller verification changes for FWS zip code-Priya
					  if(policy.containsKey("addresses")){
						  JSONArray addresses= (JSONArray) policy.get("addresses");
						  for(Object addressObj :addresses) {
							  JSONObject address =(JSONObject) addressObj;
							  if(address.containsKey("zip")) {
								  data.setSessionData(Constants.S_PAYOR_ZIP_CODE, address.get("zip"));
							  }
						  }
						  
					  }
					//Caller verification changes for FWS zip code-priya
					
					data.addToLog(data.getCurrentElement(), "policyState : "+policyState);
					data.addToLog(data.getCurrentElement(), "lineOfBusiness : "+lineOfBusiness);
					data.addToLog(data.getCurrentElement(), "companyCode : "+companyCode);
					data.addToLog(data.getCurrentElement(), "acctNo : "+billingAcctNo);
					data.addToLog(data.getCurrentElement(), "internalPolicyNo : "+internalPolicyNo);
					data.addToLog(data.getCurrentElement(), "policySource : "+policySource);
					data.addToLog(data.getCurrentElement(), "suffix : "+suffix);
					data.addToLog(data.getCurrentElement(), "gpc : "+gpc);
					data.addToLog(data.getCurrentElement(), "effectiveDate : "+effectiveDate);
					data.addToLog(data.getCurrentElement(), "callRoutingIndicator : "+callRoutingIndicator);
					data.addToLog(data.getCurrentElement(), "producerRoleCode : "+producerRoleCode);
					data.addToLog(data.getCurrentElement(), "serviceLevel : "+serviceLevel);
					String EffDate=MdmPhoneNoPolicyLookup.formatDate(data, effectiveDate,"yyyy-MM-dd HH:mm:ss.S","MM/dd/yyyy");
					
					
 
					data.setSessionData(Constants.S_POLICY_STATE, policyState);
					
					if(data.getSessionData(Constants.S_POLICY_STATE_CODE) == null
							|| data.getSessionData(Constants.S_POLICY_STATE_CODE).toString().isEmpty()) {
					data.setSessionData(Constants.S_POLICY_STATE_CODE,policyState);
					}
					data.setSessionData(Constants.S_FWS_POLICY_LOB, lineOfBusiness);
					data.setSessionData(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
					data.setSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingAcctNo);
					data.setSessionData(Constants.S_FWS_INT_POLICY_NO, internalPolicyNo);
										
					data.setSessionData(Constants.S_POLICY_SOURCE, policySource);					
					data.setSessionData(Constants.S_FWS_POLICY_SUFFIX, suffix);
					data.setSessionData(Constants.S_FWS_POLICY_GPC, gpc);
					data.setSessionData(Constants.S_GPC, gpc);
					if (data.getSessionData(Constants.S_FWS_POLICY_EFF_DATE) == null
							|| data.getSessionData(Constants.S_FWS_POLICY_EFF_DATE).toString().isEmpty()) {
						data.setSessionData(Constants.S_FWS_POLICY_EFF_DATE, EffDate);
					}
					data.setSessionData(Constants.S_CALL_ROUTING_INDICATOR, callRoutingIndicator);
					data.setSessionData(Constants.S_PRODUCER_ROLE_CODE, producerRoleCode);
					data.setSessionData(Constants.S_SERVICE_LEVELS, serviceLevel);
					exitState = Constants.SU;
 
				}
			}
			
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in policyResponseManipulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return exitState;
	}
}
	
