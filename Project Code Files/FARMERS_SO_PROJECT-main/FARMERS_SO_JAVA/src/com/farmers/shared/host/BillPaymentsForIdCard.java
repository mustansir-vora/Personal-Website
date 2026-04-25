package com.farmers.shared.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post;
import com.farmers.FarmersAPI_NP.PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_NP_Post;
import com.farmers.bean.PolicyBean;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BillPaymentsForIdCard extends DecisionElementBase {

	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		try {
			String strBU = (String)data.getSessionData(Constants.S_BU);
			String policyNO = (String)data.getSessionData(Constants.S_POLICY_NUM);
			data.addToLog(currElementName, "BU : " + strBU+" ::  Policy No : "+policyNO);
			
			String strBristolCode = (String)data.getApplicationAPI().getApplicationData("A_BRISTOL_LOB");
			String strFarmersCode = (String)data.getApplicationAPI().getApplicationData("A_FARMERS_LOB");
			String strFWSCode = (String)data.getApplicationAPI().getApplicationData("A_FWS_LOB");
			String str21stCode = (String)data.getApplicationAPI().getApplicationData("A_21ST_LOB");
			
			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);
			
			if (strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				strExitState = billpresentmentretrieveBillingSummary(strReqBody, strRespBody, data, caa, currElementName);
			} else if (strFWSCode!=null && strBU!=null && strFWSCode.contains(strBU)) {
				strExitState =  fwsBillingLookup(strReqBody, strRespBody, data, caa, currElementName);
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				strExitState =  pointgeneralbilling(strReqBody, strRespBody, data, caa, currElementName);
			} else if (strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
				strExitState = policyInquiry_RetrieveBillingDetailsByPolicyNumber(strReqBody, strRespBody, data, caa, currElementName);
			} 
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for SIDC_HOST_003 : BillPaymentsForIdCard API   :: " + e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
	}


	private String billpresentmentretrieveBillingSummary(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if (data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL) != null) {
				String wsurl = (String)data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL);
				String tid = (String)data.getSessionData(Constants.S_CALLID);
				boolean loopback = false;
				String productGroupName = "FAB";
				String PolicyNumber = (String)data.getSessionData(Constants.S_POLICY_NUM);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				data.addToLog(data.getCurrentElement(), "SIDC_HOST_003  : billpresentmentretrieveBillingSummary URL : " + wsurl);
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE(PRIYA SHAIK)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				region = regionDetails.get(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL);
				}else {
					region="PROD";
				}
				JSONObject resp = lookups.GetBillPresentmentretrieveBillingSummary(wsurl, tid, Constants.USER_ID, Constants.SYSTEM_NAME, loopback, PolicyNumber, productGroupName, conTimeout, readTimeout, context,region,UAT_FLAG);
				data.addToLog(data.getCurrentElement(), "SIDC_HOST_003 RESPONSE : " + resp.toString());
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDC_HOST_003  : billpresentmentretrieveBillingSummary  Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_billpresentmentretrieveBillingSummary(data, caa, currElementName, strRespBody);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		}
		catch (Exception e)
		{
			data.addToLog(currElementName, "Exception in KYCAF_HOST_001  billpresentmentretrieveBillingSummary API call  :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"SIDC_HOST_003 : BillPaymentsForIdCard API ", strReqBody, region,(String)data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDC_HOST_003 : BillPaymentsForIdCard API  call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String policyInquiry_RetrieveBillingDetailsByPolicyNumber(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if (data.getSessionData(Constants.S_RetrieveBillingDetailsByPolicyNumber_URL) != null) {
				String wsurl = (String)data.getSessionData(Constants.S_RetrieveBillingDetailsByPolicyNumber_URL);
				String tid = (String)data.getSessionData(Constants.S_CALLID);
				String functionname = "general";
				String agreementmode = (String) data.getSessionData("POLICY_MOD");
				 String agreementsymbol = (String) data.getSessionData("POLICY_SYMBOL");
				String postalcode = (String) data.getSessionData(Constants.S_PAYOR_ZIP_CODE);
				String policynumber = (String)data.getSessionData("S_BW_POLICYNUM");
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				data.addToLog(data.getCurrentElement(), "SIDC_HOST_003  : policyInquiry_RetrieveBillingDetailsByPolicyNumber URL : " + wsurl);
				PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post test = new PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post();
				//UAT ENV CHANGE START(PRIYA,SHAIK)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_NP_Post objNP=new PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_NP_Post();
				JSONObject resp=null;
				if("YES".equalsIgnoreCase(UAT_FLAG))
				{
					String Key =Constants.S_RetrieveBillingDetailsByPolicyNumber_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(wsurl, tid, functionname, agreementmode,agreementsymbol, policynumber, postalcode, conTimeout, readTimeout, context, region);
				}else {
					region="PROD";
				    resp = test.start(wsurl, tid, functionname, agreementmode,agreementsymbol, policynumber, postalcode, conTimeout, readTimeout, context);
				}
               //END- UAT ENV SETUP CODE (PRIYA, SHAIK
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDC_HOST_003  : policyInquiry_RetrieveBillingDetailsByPolicyNumber  Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_RetrieveBillingDetailsByPolicyNumber(data, caa, currElementName, strRespBody);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SIDC_HOST_003  policyInquiry_RetrieveBillingDetailsByPolicyNumber API call  :: " + e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"SIDC_HOST_003 : BillPaymentsForIdCard API ", strReqBody, region,(String)data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDC_HOST_003 : BillPaymentsForIdCard API  call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	


	private String fwsBillingLookup(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if ((data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL) != null)) {
				String wsurl = (String)data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL);
				String callId = (String)data.getSessionData(Constants.S_CALLID);
				PolicyBean polcyDetails = (PolicyBean)data.getSessionData(Constants.S_FINAL_POLICY_OBJ);
				String billingAccountNumber = polcyDetails.getStrBillingAccountNumber();
				String companyCode = "";
				if(billingAccountNumber.length() > 2) {
					companyCode = billingAccountNumber.substring(0, 2);
					//acctNo =  billingAccountNumber.substring(2, billingAccountNumber.length());
				}
				if(billingAccountNumber.length() >= 11) {
					billingAccountNumber =  billingAccountNumber.substring(billingAccountNumber.length() - 9, billingAccountNumber.length());
				}
				
				String effectiveDate = polcyDetails.getStrEffectiveDate();
				if(effectiveDate.length() >= 10) {
					effectiveDate = effectiveDate.substring(0, 10);
				}
				String policyNumber = (String)data.getSessionData(Constants.S_POLICY_NUM);
				String lob = polcyDetails.getStrPolicyLOB();
				String policysource = polcyDetails.getStrPolicySource();
				
				if(!policysource.equalsIgnoreCase("ARS")) {
					//IF POLICY SOURCE IS A360 or M360. THEN IVR SHOULD concat the policyno along with lob 
					policyNumber = lob+policyNumber;
				}
				
				String suffix = polcyDetails.getStrPolicySuffix();
				String policystate = polcyDetails.getStrPolicyState();
				wsurl = wsurl.replace(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingAccountNumber).replace(Constants.S_FWS_INT_POLICY_NO, policyNumber)
						.replace(Constants.S_FWS_POLICY_EFF_DATE, effectiveDate).replace(Constants.S_FWS_POLICY_SUFFIX, suffix).replace(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
				String conntimeout = (String)data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String)data.getSessionData(Constants.S_READ_TIMEOUT);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
	
				data.addToLog(currElementName, "URl : "+wsurl);
				data.addToLog(currElementName, "CALL ID : "+callId);
				data.addToLog(currElementName, "Billing Acct No : "+billingAccountNumber);
				data.addToLog(currElementName, "Company Code : "+companyCode);
				data.addToLog(currElementName, "Policy Number : "+policyNumber);
				data.addToLog(currElementName, "Eff Date : "+effectiveDate);
				data.addToLog(currElementName, "Suffix : "+suffix);
				data.addToLog(currElementName, "Policy Source "+policysource);
				data.addToLog(currElementName, "Policy State :"+policystate);
	
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE(PRIYA,SHAIK)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				 region = regionDetails.get(Constants.S_FWS_BILLING_LOOKUP_URL);
				}else {
					region="PROD";
				}
				
				JSONObject resp = lookups.GetFWSBillingLookup(wsurl, callId, policysource, policystate, lob, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context,region,UAT_FLAG);
				data.addToLog("responses", resp.toString());
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set fwsBillingLookup  Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_FWSBillingLookup(data, caa, currElementName, strRespBody);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
	
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SIDC_ADM_001  fwsBillingLookup API call  :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"SIDC_HOST_003 : BillPaymentsForIdCard API ", strReqBody,region, (String)data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDC_HOST_003 : BillPaymentsForIdCard API  call  :: "+e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}



	private String pointgeneralbilling(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			
			String policymod="";
			String policymco = "";
			String policyContractNum="";
			
			
			if ((data.getSessionData(Constants.S_POINTGENERAL_BILLING_URL) != null)) {
				String wsurl = (String)data.getSessionData(Constants.S_POINTGENERAL_BILLING_URL);
				
				if (((String)data.getSessionData("S_POLICY_MOD") != null)) {
				
				 policymod = (String)data.getSessionData("S_POLICY_MOD");
				}
				
				if (((String)data.getSessionData("S_POLICY_MCO") != null)) {
					
					 policymco = (String)data.getSessionData("S_POLICY_MCO");
					}
					
				if (( (String)data.getSessionData(Constants.S_POLICY_NUM) != null)) {
					
					policyContractNum = (String)data.getSessionData(Constants.S_POLICY_NUM);
					}
					
				
				 
				wsurl = wsurl.replace(Constants.S_POLICY_CONTRACT_NUM, policyContractNum).replace("S_POLICY_MOD", policymod).replace("S_POLICY_MCO", policymco);

				data.addToLog(data.getCurrentElement(), "URL : "+wsurl);
				data.addToLog(data.getCurrentElement(), "S_POLICY_MOD : "+policymod);
				data.addToLog(data.getCurrentElement(), "S_POLICY_MCO : "+policymco);
				data.addToLog(data.getCurrentElement(), "S_POLICY_CONTRACT_NUM : "+policyContractNum);
				String tid = (String)data.getSessionData(Constants.S_CALLID);
				String readtimeout = (String)data.getSessionData(Constants.S_READ_TIMEOUT);
				String conntimeout = (String)data.getSessionData(Constants.S_CONN_TIMEOUT);

				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE START(PRIYA,SHAIK)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				 region = regionDetails.get(Constants.S_POINTGENERAL_BILLING_URL);
				}else {
					region="PROD";
				}
				
				
				JSONObject resp = lookups.Getpointgeneralbilling(wsurl, tid, Integer.valueOf(Integer.parseInt(conntimeout)), Integer.valueOf(Integer.parseInt(readtimeout)), context,region,UAT_FLAG);
				//UAT ENV CHANGE END(PRIYA,SHAIK)
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set pointgeneralbilling  Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_pointgeneralbilling(data, caa, currElementName, strRespBody);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in SIDC_ADM_001  pointgeneralbilling API call  :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"SIDC_HOST_003 : BillPaymentsForIdCard API ", strReqBody,region,  (String)data.getSessionData(Constants.S_POINTGENERAL_BILLING_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDC_HOST_003 : BillPaymentsForIdCard API  call  :: "+e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

	private String apiResponseManupulation_billpresentmentretrieveBillingSummary(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			strExitState = Constants.SU;
		}
		catch (Exception e)
		{
			data.addToLog(currElementName, "Exception in apiResponseManupulation_billpresentmentretrieveBillingSummary method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	private String apiResponseManupulation_RetrieveBillingDetailsByPolicyNumber(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			if (null != strRespBody) {
				JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
				
				if (null != resp && resp.containsKey("billingInfo")) {
					JSONObject billingInfoObj = (JSONObject) resp.get("billingInfo");
					
					if (null != billingInfoObj && billingInfoObj.containsKey("insuredDetails")) {
						JSONObject insuredDetailsObj = (JSONObject) billingInfoObj.get("insuredDetails");
						
						if (null != insuredDetailsObj && insuredDetailsObj.containsKey("email")) {
							String strEmail = (String)insuredDetailsObj.get(Constants.email);
							
							if (null != strEmail && !strEmail.isEmpty()) {
								data.setSessionData(Constants.S_EMAIL, strEmail);
								data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
								strExitState = Constants.SU;
								data.addToLog(data.getCurrentElement(), "SIDC_HOST_003 : apiResponseManupulation_RetrieveBillingDetailsByPolicyNumber : EMAIL : "+strEmail);
							}
							else {
								data.addToLog(currElementName, "Email not available in BW Billing API response :: " + insuredDetailsObj);
							}
						}
						else {
							data.addToLog(currElementName, "Insured Details Object does not contain Email Field :: " + insuredDetailsObj);
						}
					}
					else {
						data.addToLog(currElementName, "Billing Info Object does not contain Insured Details Object :: EMAIL not available :: " +  billingInfoObj);
					}
				}
				else {
					data.addToLog(currElementName, "Resp does not contain Billing Info Object :: EMAIL not available :: " + resp);
				}
			}
			else {
				data.addToLog(currElementName, "Response Body is null :: " + strRespBody);
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation_billpresentmentretrieveBillingSummary method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}


	private String apiResponseManupulation_FWSBillingLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody)
	{
		String strExitState = Constants.ER;
		try
		{
			strExitState = Constants.SU;
		}
		catch (Exception e)
		{
			data.addToLog(currElementName, "Exception in apiResponseManupulation_FWSBillingLookup method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String apiResponseManupulation_pointgeneralbilling(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation_pointgeneralbilling method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
