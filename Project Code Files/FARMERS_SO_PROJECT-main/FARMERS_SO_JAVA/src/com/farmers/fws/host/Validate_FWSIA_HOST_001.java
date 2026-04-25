package com.farmers.fws.host;

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
import com.farmers.FarmersAPI_NP.FWSBillingLookup_NP_Get;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Validate_FWSIA_HOST_001 extends DecisionElementBase{

	private String strReqBody;

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			strExitState = readPolicyLookupResp(caa, data);
			if(strExitState.equalsIgnoreCase(Constants.SU)) {
				strExitState = readBillingLookupResp(caa, data);
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in Validate_FWSARC_HOST_001 :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String readBillingLookupResp(CommonAPIAccess caa, DecisionElementData data) {
		String exitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		String region=null;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {

			if(data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL) != null &&  data.getSessionData(Constants.S_POLICY_SOURCE) != null &&  
					data.getSessionData(Constants.S_POLICY_STATE) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null &&
					data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				String url = (String) data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL);

				//	https://api-ss.farmersinsurance.com/billingms/v1/billingaccounts/S_FWS_POLICY_BILLING_ACCT_NO/billingSummary?policyNumber=S_FWS_INT_POLICY_NO&effectiveDate=S_FWS_POLICY_EFF_DATE&suffix=S_FWS_POLICY_SUFFIX&companyCode=S_FWS_POLICY_COMPANY_CODE

				String billingNo = (String) data.getSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO);
				if(billingNo != null && billingNo.length() >= 11) {
					billingNo =  billingNo.substring(2, billingNo.length());
				}
				String policyNo = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String policysource = (String)data.getSessionData(Constants.S_POLICY_SOURCE);
				String strLOB = (String) data.getSessionData(Constants.S_FWS_POLICY_LOB);
				if(!policysource.equalsIgnoreCase("ARS")) {
					//IF POLICY SOURCE IS A360 or M360. THEN IVR SHOULD concat the policyno along with lob 
					boolean startsWithAlphabets =  Character.isLetter(policyNo.charAt(0));
					if(!startsWithAlphabets) {
						policyNo = strLOB+policyNo;
					}
					
				}
				data.addToLog(data.getCurrentElement(), "Concatenated policy no : "+policyNo);
				String date = (String) data.getSessionData(Constants.S_FWS_POLICY_EFF_DATE);
				String suffix = (String) data.getSessionData(Constants.S_FWS_POLICY_SUFFIX);
				String companyCode = (String) data.getSessionData(Constants.S_FWS_POLICY_COMPANY_CODE);

				//Changes based on the case CS1150152 - 'TB1' Error when querying for FWS ARS billing data  Start//
				
				String intpolicyno=(String) data.getSessionData(Constants.S_FWS_INT_POLICY_NO);
				
				if (null != policysource && policysource.equalsIgnoreCase("ARS")) {
					
					if (null != billingNo && null != intpolicyno && null != date && null != suffix && null != companyCode) {
						data.addToLog(data.getCurrentElement(), "Internal Policy No: "+intpolicyno);
						url = url.replace(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingNo).replace(Constants.S_FWS_INT_POLICY_NO, intpolicyno).replace(Constants.S_FWS_POLICY_EFF_DATE, date).replace(Constants.S_FWS_POLICY_SUFFIX, suffix).replace(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
					}
				}
				else if (null != policysource && !policysource.equalsIgnoreCase("ARS")) {
					
					if (null != billingNo && null != policyNo && null != date && null != suffix && null != companyCode) {
						data.addToLog(data.getCurrentElement(), "MEt360/A360 Policy No: "+policyNo);
						url = url.replace(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingNo).replace(Constants.S_FWS_INT_POLICY_NO, policyNo).replace(Constants.S_FWS_POLICY_EFF_DATE, date).replace(Constants.S_FWS_POLICY_SUFFIX, suffix).replace(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
					}
				}
				
				//Changes based on the case CS1150152 - 'TB1' Error when querying for FWS ARS billing data  end //
				
				data.addToLog(data.getCurrentElement(), "FWS Billing URL : "+url);
				String callID = (String) data.getSessionData(Constants.S_CALLID);
				String policySource = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
				String policyState = (String) data.getSessionData(Constants.S_POLICY_STATE);
				
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));


				JSONObject responseJSON = null;
				FWSBillingLookup_Get objFWSBillingLookup = new FWSBillingLookup_Get();
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				data.addToLog("is UAT DNIS? ", UAT_FLAG);
				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				FWSBillingLookup_NP_Get objNP = new FWSBillingLookup_NP_Get();
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_FWS_BILLING_LOOKUP_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					responseJSON = objNP.start(url, callID, policySource, policyState, strLOB, conTimeout, readTimeout, context, region);
					
				}else {
					region="PROD";
					responseJSON = objFWSBillingLookup.start(url, callID, policySource, policyState, strLOB, conTimeout, readTimeout, context);
				}
                //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
				data.addToLog(data.getCurrentElement(), "Billing API Response : "+responseJSON);
				String strRespBody = "";
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responseJSON.get(Constants.RESPONSE_CODE));
				if(responseJSON != null && responseJSON.containsKey(Constants.RESPONSE_CODE) && (int) responseJSON.get(Constants.RESPONSE_CODE) == 200 && responseJSON.containsKey(Constants.RESPONSE_BODY) && responseJSON.get(Constants.RESPONSE_BODY) != null) {
					data.setSessionData("S_BILLING_RESP_AVAIL", "Y");
					strReqBody = responseJSON.get(Constants.REQUEST_BODY).toString();
					data.addToLog(data.getCurrentElement(), "Set FWS Billing Lookup API Response into session with the key name of "+data.getCurrentElement()+Constants._RESP);
					strRespBody = responseJSON.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(data.getCurrentElement()+Constants._RESP, responseJSON.get(Constants.RESPONSE_BODY));
					exitState = billingResponseManipulation(data, caa, strRespBody);
				}	
				else {
				strRespBody = (String) responseJSON.get(Constants.RESPONSE_MSG);
				}

				try {
					objHostDetails.startHostReport(data.getCurrentElement(),"FWS Billing Lookup API Call", strReqBody, region, (String) data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL));
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

	private String readPolicyLookupResp(CommonAPIAccess caa, DecisionElementData data) {
		String exitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		String region=null;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {

			if(data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL) != null &&  
					data.getSessionData(Constants.S_CONN_TIMEOUT) != null &&
					data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				String url = (String) data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL);
				String callID = (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));

				data.addToLog(data.getCurrentElement(), "POLICY NUMBER : "+data.getSessionData(Constants.S_POLICY_NUM));
				data.addToLog(data.getCurrentElement(), "BILLING ACCT NUMBER : "+data.getSessionData(Constants.S_BILLING_ACC_NUM));

				JSONObject responseJSON = null;
				
				FWSPolicyLookup_Post test = new FWSPolicyLookup_Post();

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

				data.addToLog(data.getCurrentElement(), "API Response : "+responseJSON);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responseJSON.get(Constants.RESPONSE_CODE));

				String strRespBody = "";
				String strReqBody="";
				if(responseJSON != null && responseJSON.containsKey(Constants.RESPONSE_CODE) && (int) responseJSON.get(Constants.RESPONSE_CODE) == 200 && responseJSON.containsKey(Constants.RESPONSE_BODY) && responseJSON.get(Constants.RESPONSE_BODY) != null) {
					data.addToLog(data.getCurrentElement(), "Set FWS Policy Lookup API Response into session with the key name of "+data.getCurrentElement()+Constants._RESP);
					strReqBody = responseJSON.get(Constants.REQUEST_BODY).toString();
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
					objHostDetails.startHostReport(data.getCurrentElement(),"FWS Policy Lookup API call", strReqBody,region, data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL).toString().length() > 99 ?  (String)data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL).toString().substring(0, 99) : (String)data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL));
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
					
					if(effectiveDate!=null && effectiveDate.length() >= 10) {
						effectiveDate = effectiveDate.substring(0, 10);
					}
					
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
					
					
					

					data.setSessionData(Constants.S_POLICY_STATE, policyState);
					data.setSessionData(Constants.S_FWS_POLICY_LOB, lineOfBusiness);
					data.setSessionData(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
					data.setSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingAcctNo);
					data.setSessionData(Constants.S_FWS_INT_POLICY_NO, internalPolicyNo);
					data.setSessionData(Constants.S_POLICY_SOURCE, policySource);
					data.setSessionData(Constants.S_FWS_POLICY_SUFFIX, suffix);
					data.setSessionData(Constants.S_FWS_POLICY_GPC, gpc);
					data.setSessionData(Constants.S_GPC, gpc);
					data.setSessionData(Constants.S_FWS_POLICY_EFF_DATE, effectiveDate);
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

	private String billingResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String strRespBody) {
		String exitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject billingSummary = (JSONObject) resp.get("billingSummary");

			
			String producerDistSysCd = (String) billingSummary.get("producerDistSysCd");
			String paymentLevelCode = (String) billingSummary.get("paymentLevelCode");
			
			
			if(producerDistSysCd == null || producerDistSysCd.equals("")) {
				if(billingSummary.containsKey("policy")) {
					JSONObject policyObject = (JSONObject) billingSummary.get("policy");
					 producerDistSysCd = (String) policyObject.get("producerDistSysCd");  
					 paymentLevelCode = (String) policyObject.get("paymentLevelCode");
				}
			}
			
			data.addToLog(data.getCurrentElement(),  Constants.S_FWS_BILLING_PROD_DIST_CD+" : "+producerDistSysCd);
			data.addToLog(data.getCurrentElement(), "paymentLevelCode : "+paymentLevelCode);
			
			data.setSessionData(Constants.S_FWS_BILLING_PROD_DIST_CD, producerDistSysCd); 
			String billingNo = (String) data.getSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO);
			if(billingNo.length() >= 2) {
				billingNo =  billingNo.substring(0, 2);
				data.addToLog(data.getCurrentElement(), "Account Number starts with : "+billingNo);
			}
			String paymentSiteCD = paymentLevelCode;
			
		/*	if(billingNo.equalsIgnoreCase("00")) {
				paymentSiteCD = "Direct Bill";
			}else if(billingNo.equalsIgnoreCase("ZZ")) {
				paymentSiteCD = "Pack II";
			}else if(billingNo.equalsIgnoreCase("HB")) {
				paymentSiteCD = "HAB";
			}else if(billingNo.equalsIgnoreCase("90")) {
				paymentSiteCD = "Ex/IT";
			}else if(billingNo.equalsIgnoreCase("3Z")) {
				paymentSiteCD = "Ex/IT";
			}
			
			*/
			
			String policySource =  (String) data.getSessionData(Constants.S_POLICY_SOURCE);
			if(!policySource.equalsIgnoreCase("Met360")) {
				if(billingNo.equalsIgnoreCase("00") || billingNo.equalsIgnoreCase("ZZ") || billingNo.equalsIgnoreCase("HB") || billingNo.equalsIgnoreCase("90") || billingNo.equalsIgnoreCase("3Z")) {
					data.addToLog(data.getCurrentElement(), "PAYROLL DEDUCT IS OFF");
				}else {
					data.addToLog(data.getCurrentElement(), "PAYROLL DEDUCT IS ON");
					paymentSiteCD = "Payroll Deduct";	
				}
			}
			data.setSessionData("S_PAYMENT_SITE_CD", paymentSiteCD); 
			data.setSessionData("S_PRODUCER_DISTRIBUTION_CODE", producerDistSysCd);
			
			
			boolean transferType = false;
			boolean activitydesc = false;

			JSONArray duesArr = (JSONArray) billingSummary.get("dues");
			for (Object objDues : duesArr) {
				JSONObject dues = (JSONObject) objDues;
				//if(dues.containsKey("activityDesc")) {
				if(dues.containsKey("type")) {
					String type = (String) dues.get("type");
					if(type.equalsIgnoreCase("TRANSFERTYPE")) {
						transferType = true;
						//data.addToLog(data.getCurrentElement(), "Setting "+Constants.S_FWS_BILLING_ACTIVITY_CD+ " as Y. TRANSFER TYPE is available");
						//data.setSessionData(Constants.S_FWS_BILLING_ACTIVITY_CD, "Y"); 
						exitState = Constants.SU;
						break;
					}
				}
			}
			
			for (Object objDues : duesArr) {
				JSONObject dues = (JSONObject) objDues;
				if(dues.containsKey("activityDesc")) {
					String activityDesc = (String) dues.get("activityDesc");
					if(activityDesc.equalsIgnoreCase("TRAN1")) {
						data.setSessionData("S_ACTIVITY_DESC", activityDesc);
						activitydesc = true;
						//data.addToLog(data.getCurrentElement(), "Setting "+Constants.S_FWS_BILLING_ACTIVITY_CD+ " as Y. TRAN1 activityDesc is available");
						//data.setSessionData(Constants.S_FWS_BILLING_ACTIVITY_CD, "Y"); 
						exitState = Constants.SU;
						break;
					}
				}
			}
			
			if(transferType && activitydesc) {
				data.addToLog(data.getCurrentElement(), "Setting "+Constants.S_FWS_BILLING_ACTIVITY_CD+ " as Y. TRANSFER TYPE is available & Activity Desc = TRAN1");
				data.setSessionData(Constants.S_FWS_BILLING_ACTIVITY_CD, "Y"); 
			}
			else{
				data.addToLog(data.getCurrentElement(), "Setting "+Constants.S_FWS_BILLING_ACTIVITY_CD+ " as N. TRANSFER TYPE is not available or ActivityDesc != TRAN1");
				data.setSessionData(Constants.S_FWS_BILLING_ACTIVITY_CD, "N");
			}
			exitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in policyResponseManipulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return exitState;
	}

}
