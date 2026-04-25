package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post;
import com.farmers.FarmersAPI_NP.PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BW_BillPayments extends DecisionElementBase  {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		//SetHostDetails objHostDetails = new SetHostDetails(caa);
		//objHostDetails.setinitalValue();
		String strReqBodyGeneral = Constants.EmptyString;
		String strRespBodyGeneral = Constants.EmptyString;
		String strReqBodyPayment = Constants.EmptyString;
		String strRespBodyPayment = Constants.EmptyString;
		try {

			StrExitState = policyEnquiry_BW(strReqBodyGeneral, strRespBodyGeneral, strReqBodyPayment, strRespBodyPayment, data, caa, currElementName, StrExitState);

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for " + currElementName + " :: " + e);
			caa.printStackTrace(e);
		}
		
		if(StrExitState.equalsIgnoreCase(Constants.ER)) {
			//String mspKey = "BW"+":"+"SBP_HOST_001"+":"+"ER";
			//data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
			//data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);
			caa.createMSPKey(caa, data, "SBP_HOST_001", "ER");
		}

		return StrExitState;
	}

	private String policyEnquiry_BW(String strReqBodyGeneral, String strRespBodyGeneral, String strReqBodyPayment, String strRespBodyPayment, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		String billingInfo = Constants.EmptyString;
		JSONObject generalBillingInfo = new JSONObject();
		JSONObject paymentsBillingInfo = new JSONObject();
		JSONObject paymentResponse = new JSONObject();
		JSONObject selectedPlObj = new JSONObject();
		String agreementMode = Constants.EmptyString;
		String agreementSymbol = Constants.EmptyString;
		String postalCode = Constants.EmptyString;
		String policyNumber = Constants.EmptyString;
		String billingzipcode = Constants.EmptyString;
		//SetHostDetails objHostDetails = new SetHostDetails(caa);
		//objHostDetails.setinitalValue();
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)

		try {

			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

			JSONParser parser = new JSONParser();
			policyNumber = (String) data.getSessionData("S_BW_POLICYNUM");
            
			data.addToLog(currElementName, "Policy Number :: " + policyNumber);

			if (null != policyNumber && !policyNumber.isEmpty()) {
				
				agreementMode = (String) data.getSessionData("POLICY_MOD");
				if (agreementMode == null || agreementMode.equals("")) {
					agreementMode = policyNumber.substring(policyNumber.length() - 2, policyNumber.length());
				}
				data.addToLog(currElementName, "Policy Mod :: "+agreementMode);
				
				agreementSymbol = (String) data.getSessionData("POLICY_SYMBOL") != null ? (String) data.getSessionData("POLICY_SYMBOL") : Constants.EmptyString;
				data.addToLog(currElementName, "Policy Symbol :: "+agreementSymbol);
			}

			data.addToLog(data.getCurrentElement(), "URL :: " + data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL) + " :: Call ID :: " + data.getSessionData(Constants.S_CALLID) + " : functionalNameGeneral ::" + Constants.FUNCTION_NAME_GENERAL + " : functionalNamePayments ::" + Constants.FUNCTION_NAME_PAYMENTS + " :: agreement  Symbol :: " + agreementSymbol + " :: agreement Mode :: " + agreementMode + " :: policyNumber :: " + policyNumber + " :: postalCode :: " + postalCode);

			if (null != data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL) && null != data.getSessionData(Constants.S_CALLID) && null != agreementSymbol && null != agreementMode && null != policyNumber && !(Constants.FUNCTION_NAME_PAYMENTS).isEmpty() && !(Constants.FUNCTION_NAME_GENERAL).isEmpty() && null != data.getSessionData(Constants.S_CONN_TIMEOUT) && null != data.getSessionData(Constants.S_READ_TIMEOUT)) {

				String url = (String) data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String functionNamePayments = Constants.FUNCTION_NAME_PAYMENTS;// Constant to be hardcoded
				String functionNameGeneral = Constants.FUNCTION_NAME_GENERAL;// Constant to be hardcoded
				int connTimeout = Integer.parseInt((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.parseInt((String) data.getSessionData(Constants.S_READ_TIMEOUT));

				PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post apiObj = new PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post();

				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_NP_Post objNP = new PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_NP_Post();
				JSONObject generalResponse=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					generalResponse = objNP.start(url, tid, "general", agreementMode, agreementSymbol, policyNumber, postalCode, connTimeout, readTimeout, context, region);
				}else {
					region="PROD";
					generalResponse = apiObj.start(url, tid, "general", agreementMode, agreementSymbol, policyNumber, postalCode, connTimeout, readTimeout, context);
				}
              //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
				data.addToLog(currElementName, " : BW Billing API General response  :: " + generalResponse);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) generalResponse.get(Constants.RESPONSE_CODE));

				if (generalResponse.containsKey(Constants.RESPONSE_CODE) && (int) generalResponse.get(Constants.RESPONSE_CODE) == 200 && generalResponse.containsKey(Constants.RESPONSE_BODY)) {

					strReqBodyGeneral = generalResponse.get(Constants.REQUEST_BODY).toString();
					data.addToLog(currElementName, "BW Billing General Final API response :: " + generalResponse.toString());

					strRespBodyGeneral = generalResponse.get(Constants.RESPONSE_BODY).toString();
					data.addToLog(currElementName, "BW Billing General Response Body :: " + strRespBodyGeneral);
					
					JSONObject objRespBodyGeneral = (JSONObject) generalResponse.get(Constants.RESPONSE_BODY);

					data.setSessionData(currElementName + Constants._RESP, generalResponse.get(Constants.RESPONSE_BODY));

					if (objRespBodyGeneral.containsKey(Constants.BILLING_INFO) && null != objRespBodyGeneral.get(Constants.BILLING_INFO) && !((JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO)).isEmpty()) {

						generalBillingInfo = (JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO);

						if ((((JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO)).containsKey("policyDetails")) && null != ((JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO)).get("policyDetails") && !((JSONObject) ((JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO)).get("policyDetails")).isEmpty()) {

							JSONObject plDetailsObj = (JSONObject) ((JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO)).get("policyDetails");

							if (plDetailsObj.containsKey("policyZipcode") && null != plDetailsObj.get("policyZipcode")) {

								String plZipCode = (String) plDetailsObj.get("policyZipcode");
								data.addToLog(currElementName, "BW Billing and Payments API General response Zip code :: " + plZipCode);
								postalCode = plZipCode.length() > 5 ? plZipCode.substring(0, 5) : plZipCode;
								data.addToLog(currElementName, "BW Billing and Payments API General postal code in the response :: " + postalCode);
								data.setSessionData(Constants.S_PAYOR_ZIP_CODE, postalCode);
								data.addToLog(currElementName, "Setting Billing zip code for EPCPAYMENTUS into session :: "+postalCode);
							} else {
								data.addToLog(currElementName, "BW Billing and Payments API General response if policy details object key not present or null");
								StrExitState = Constants.ER;
							}
						} else {
							data.addToLog(currElementName, "BW Billing and Payments API General response if billing info does not have policy Details field or null");
							StrExitState = Constants.ER;
						}
					} else {
						data.addToLog(currElementName, "BW Billing and Payments API General response does not have billing info Object");
					}
					if (currElementName.equalsIgnoreCase("BWPID_MN_001"))
						StrExitState = Constants.SU;
				} else {
					strRespBodyGeneral = generalResponse.get(Constants.RESPONSE_MSG).toString();
					data.addToLog(currElementName, "BW Billing and Payments API General response does not have responseBody Object");
					StrExitState = Constants.ER;
				}
				try {
					SetHostDetails objHostDetails1 = new SetHostDetails(caa);
					objHostDetails1.setinitalValue();
					objHostDetails1.startHostReport(currElementName, currElementName, strReqBodyGeneral,region, (String) data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL));
					objHostDetails1.endHostReport(data, strRespBodyGeneral,
							StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
				} catch (Exception e) {
					data.addToLog(currElementName, "Exception while forming host reporting for " + currElementName + "BW Policy Enquiry API General call  :: " + e);
					caa.printStackTrace(e);
				}

				if (null != data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_PAYMENT_URL) && null != postalCode && !postalCode.isEmpty()) {

					paymentResponse = (JSONObject) apiObj.start(url, tid, "payment", agreementMode, agreementSymbol, policyNumber, postalCode, connTimeout, readTimeout, context);
					
					data.addToLog(currElementName, currElementName + " : BW Billing API Payments response  :: " + paymentResponse);
				}
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) paymentResponse.get(Constants.RESPONSE_CODE));

				if (paymentResponse.containsKey(Constants.RESPONSE_CODE) && (int) paymentResponse.get(Constants.RESPONSE_CODE) == 200 && paymentResponse.containsKey(Constants.RESPONSE_BODY)) {

					strReqBodyPayment = paymentResponse.get(Constants.REQUEST_BODY).toString();
					
					data.addToLog(currElementName, "BW Billing Payment Request Body :: " + strReqBodyPayment);

					data.addToLog(currElementName, "BW Billing API Payment response into session with the key name of " + currElementName + Constants._RESP);

					strRespBodyPayment = paymentResponse.get(Constants.RESPONSE_BODY).toString();
					
					data.addToLog(currElementName, "BW Billing and Payments Request Body General :: " + strRespBodyPayment);

					JSONObject objRespBodyPayment = (JSONObject) generalResponse.get(Constants.RESPONSE_BODY);

					data.setSessionData(currElementName + Constants._RESP, generalResponse.get(Constants.RESPONSE_BODY));

					if (objRespBodyPayment.containsKey(Constants.BILLING_INFO) && null != objRespBodyPayment.get(Constants.BILLING_INFO) && !((JSONObject) objRespBodyPayment.get(Constants.BILLING_INFO)).isEmpty()) {
						paymentsBillingInfo = (JSONObject) objRespBodyPayment.get(Constants.BILLING_INFO);

					} else {
						data.addToLog(currElementName, "BW Billing and Payments API Payments response does not have billing info Object");
					}
					if (currElementName.equalsIgnoreCase("BWPID_MN_001"))
						StrExitState = Constants.SU;
				} else {
					strRespBodyPayment = generalResponse.get(Constants.RESPONSE_MSG).toString();
					data.addToLog(currElementName, "BW Billing and Payments API Payment response does not have responseBody Object");
				}

				try {
					SetHostDetails objHostDetails = new SetHostDetails(caa);
					objHostDetails.setinitalValue();
					objHostDetails.startHostReport(currElementName, currElementName, strReqBodyPayment,region,(String) data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_PAYMENT_URL));
					objHostDetails.endHostReport(data, strRespBodyPayment,
							StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
				} catch (Exception e) {
					
					data.addToLog(currElementName, "Exception while forming host reporting for " + currElementName + "BW Policy Enquiry API Payment call  :: " + e);
					caa.printStackTrace(e);
				}

				if ((!generalBillingInfo.isEmpty() || !paymentsBillingInfo.isEmpty()) && null != policyNumber && !policyNumber.isEmpty()) {
					StrExitState = checkCaseListNew(generalBillingInfo, paymentsBillingInfo, StrExitState, data, currElementName,caa);
					//StrExitState = Constants.SU;
				} else {
					data.addToLog(currElementName, "BW Billings and Payments API error if response objects are empty");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in " + currElementName + "PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post API call  :: " + e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}
	
	public String checkCaseListNew(JSONObject generalBillingInforesp, JSONObject paymentsBillingInforesp, String strExitState, DecisionElementData data, String currElementName, CommonAPIAccess caa) {
		
		String policyStatus = null;
		String policyCancellationDate = null;
		String isRenewalPending = null;
		String policyEffectiveDate = null;
		String nsfIndicator = null;
		String nsfAmount = null;
		String relapsePeriod = null;
		String isPolicyQualifiedForReinstatewithLapse = null;
		String isPolicyReWriteOfferAvailable = null;
		String isPolicyQualifiedForReWrittenWithLapse = null;
		String policyReWriteOfferExpiryDate = null;
		String nextPaymentAmount = null;
		String nextPaymentDate = null;
		String policyActivityDate = null;
		String policyBalanceDue = null;
		String isPolicyinCollectionMode = null;
		String collectionAmount = null;
		String renewalNextDate = null;
		String renewalNextAmount = null;
		String renewalLastAmount = null;
		String renewalLastDate = null;
		String renewalEffectiveDate = null;
		String renewalCancellationDate = null;
		
		try {
			
			JSONObject generalConsolidatedResp = null;
			generalConsolidatedResp = apiResponseManipulationGeneral(data, currElementName, caa, generalBillingInforesp);
			String WebserviceAmount = null;
			WebserviceAmount = apiResponseManipulationPayment(data, currElementName, caa, paymentsBillingInforesp);
			
			//policyStatus = getValuesFromJSONResp(generalBillingInforesp, "policyDetails:policyStatus");
			
			policyStatus = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "policystatus");
			policyCancellationDate = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "policycancellationdate");
			isRenewalPending = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "isrenewalpending");
			policyEffectiveDate = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "effectivedate");
			nsfIndicator = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "nsfindicator");
			nsfAmount = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "nsfamount");
			//relapsePeriod = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "relapseperiod");
			relapsePeriod = "29";
			isPolicyQualifiedForReinstatewithLapse = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "ispolicyqualifiedforreinstatewithlapse");
			isPolicyReWriteOfferAvailable = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "policyrewriteofferavailable");
			policyReWriteOfferExpiryDate = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "policyrewriteofferexpirydate");
			nextPaymentAmount = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "nextpaymentamount");
			nextPaymentDate = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "nextpaymentdate");
			policyActivityDate = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "policyactivitydate");
			policyBalanceDue = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "fullbalancedue");
			isPolicyinCollectionMode = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "ispolicyincollection");
			collectionAmount = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "collectionamount");
			renewalNextDate = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "nextrenewaldate");
			renewalNextAmount = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "nextrenewalamount");
			renewalLastAmount = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "lastrenewalamount");
			renewalLastDate = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "lastrenewaldate");
			renewalEffectiveDate = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "renewaleffectivedate");
			renewalCancellationDate = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "renewalcancellationdate");
			isPolicyQualifiedForReWrittenWithLapse = getValuesfromFinalObject(data, currElementName, caa, generalConsolidatedResp, "ispolicyqualifiedforrewrittenwithlapse");
			
if (null != policyStatus && !policyStatus.isEmpty()) {
				
				if (policyStatus.equalsIgnoreCase("ACT")) {
					
				if (null != isRenewalPending && isRenewalPending.equalsIgnoreCase("N") && null != policyBalanceDue && Double.parseDouble(policyBalanceDue) > 0.00 && null != nextPaymentAmount && Double.parseDouble(nextPaymentAmount) > 0.00 && null != nextPaymentDate && !nextPaymentDate.isEmpty() && null != WebserviceAmount && Double.parseDouble(WebserviceAmount) > 0.00) {
						data.setSessionData("CASE_BELONGS_TO", "CL_1");
						data.setSessionData("S_NEXT_PAYMENT_AMOUNT", nextPaymentAmount);
						data.setSessionData("S_POLICY_STATUS", "ACT");
						data.setSessionData("S_POLICY_BALANCE_GR_THAN_ZERO", "True");
						data.setSessionData("S_WEBSERVICE_AMOUNT_GR_THAN_ZERO", "True");
						data.setSessionData("S_NEXT_PAYMENT_AMOUNT_EXISTS", "True");
						data.setSessionData("S_NEXT_PAYMENT_DATE_EXISTS", "True");
						data.addToLog(currElementName, "Identified as Case 1 :: Setting Required Flags & Values Into Session :: S_NEXT_PAYMENT_AMOUNT :: " + data.getSessionData("S_NEXT_PAYMENT_AMOUNT") + " :: S_POLICY_STATUS :: "+data.getSessionData("S_POLICY_STATUS") + " :: S_POLICY_BALANCE_GR_THAN_ZERO :: " + data.getSessionData("S_POLICY_BALANCE_GR_THAN_ZERO") + " :: S_WEBSERVICE_AMOUNT_GR_THAN_ZERO :: " + data.getSessionData("S_WEBSERVICE_AMOUNT_GR_THAN_ZERO") + " :: S_NEXT_PAYMENT_AMOUNT_EXISTS :: " + data.getSessionData("S_NEXT_PAYMENT_AMOUNT_EXISTS") + " :: S_NEXT_PAYMENT_DATE_EXISTS :: " + data.getSessionData("S_NEXT_PAYMENT_DATE_EXISTS"));
						strExitState = Constants.SU;
					}
					
					else if(null != isRenewalPending && isRenewalPending.equalsIgnoreCase("N") && null != policyBalanceDue && Double.parseDouble(policyBalanceDue) > 0.00 && null != WebserviceAmount && !WebserviceAmount.isEmpty() && Double.parseDouble(WebserviceAmount) <= 0.00 ) {
						data.setSessionData("CASE_BELONGS_TO", "CL_1");
						data.setSessionData("S_POLICY_STATUS", "ACT");
						data.setSessionData("S_POLICY_BALANCE_GR_THAN_ZERO", "True");
						data.setSessionData("S_WEBSERVICE_LESS_THAN_EQUAL_ZERO", "True");
						data.addToLog(currElementName, "Identified as Case 1A :: Setting Required Flags & Values Into Session :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICY_BALANCE_GR_THAN_ZERO :: " + data.getSessionData("S_POLICY_BALANCE_GR_THAN_ZERO") + " :: S_WEBSERVICE_LESS_THAN_EQUAL_ZERO :: " + data.getSessionData("S_WEBSERVICE_LESS_THAN_EQUAL_ZERO"));
						strExitState = Constants.SU;
					}
					
					else if(null != isRenewalPending && isRenewalPending.equalsIgnoreCase("N") && null != policyBalanceDue && Double.parseDouble(policyBalanceDue) > 0.00 && null != nextPaymentDate && !nextPaymentDate.isEmpty() && null != nextPaymentAmount && !nextPaymentAmount.isEmpty() && Double.parseDouble(nextPaymentAmount) > 0.00) {
						data.setSessionData("CASE_BELONGS_TO", "CL_1");
						data.setSessionData("S_NEXT_PAYMENT_AMOUNT", nextPaymentAmount);
						data.setSessionData("S_NEXT_PAYMENT_DATE", nextPaymentDate.replaceAll("-", ""));
						data.setSessionData("S_POLICY_STATUS", "ACT");
						data.setSessionData("S_POLICY_BALANCE_GR_THAN_ZERO", "True");
						data.setSessionData("S_INVOICE_GENERATED", "Yes");
						data.addToLog(currElementName, "Identified as Case 2 :: Setting Required Flags & Values Into Session :: S_NEXT_PAYMENT_AMOUNT :: " + data.getSessionData("S_NEXT_PAYMENT_AMOUNT") + " :: S_NEXT_PAYMENT_DATE :: " + data.getSessionData("S_NEXT_PAYMENT_DATE") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICY_BALANCE_GR_THAN_ZERO :: " + data.getSessionData("S_POLICY_BALANCE_GR_THAN_ZERO") + " :: S_INVOICE_GENERATED :: "+data.getSessionData("S_INVOICE_GENERATED"));
						strExitState = Constants.SU;
					}
					
					else if(null != isRenewalPending && isRenewalPending.equalsIgnoreCase("N") && null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) <= 0.00) {
						data.setSessionData("CASE_BELONGS_TO", "CL_1");
						data.setSessionData("S_POLICY_STATUS", "ACT");
						data.setSessionData("S_POLICY_BALANCE_LESS_THAN_EQUAL_ZERO", "True");
						data.addToLog(currElementName, "Identified as Case 3 :: Setting Required Flags & Values Into Session :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICY_BALANCE_LESS_THAN_EQUAL_ZERO :: " + data.getSessionData("S_POLICY_BALANCE_LESS_THAN_EQUAL_ZERO"));
						strExitState = Constants.SU;
					}
					
					else if(null != isRenewalPending && isRenewalPending.equalsIgnoreCase("Y") && null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) == 0.00 && null != renewalEffectiveDate && !renewalEffectiveDate.isEmpty() && !isSystemDateGreater(data, currElementName, caa, renewalEffectiveDate) && null != renewalNextAmount && !renewalNextAmount.isEmpty() && Double.parseDouble(renewalNextAmount) > 0.00 && null != renewalNextDate && !renewalNextDate.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_3");
						data.setSessionData("S_POLICY_STATUS", "ACT");
						data.setSessionData("S_RENEWAL_AMOUNT_EXISTS", "True");
						data.setSessionData("S_RENEWAL_DATE_EXISTS", "True");
						data.setSessionData("S_RE_EFFDT_LESS_EQ_CURR_DT", "True");
						data.setSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK", "False");
						data.setSessionData("S_RENEWAL_PENDING", "Y");
						data.setSessionData("S_POLICY_BALANCE_EQ_ZERO", "True");
						data.setSessionData("S_RENEWAL_INVOICE_AMOUNT", renewalNextAmount);
						data.setSessionData("S_RE_INVOICE_DT_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, renewalNextDate, -1).replaceAll("-", ""));
						data.addToLog(currElementName, "Identified as Case 4 :: Setting Required Flags & Values Into Session :: S_RENEWAL_INVOICE_AMOUNT :: " + data.getSessionData("S_RENEWAL_INVOICE_AMOUNT") + " :: S_RE_INVOICE_DT_MINUS_1 :: " + data.getSessionData("S_RE_INVOICE_DT_MINUS_1") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_RENEWAL_AMOUNT_EXISTS :: " + data.getSessionData("S_RENEWAL_AMOUNT_EXISTS") + " :: S_RENEWAL_DATE_EXISTS :: " + data.getSessionData("S_RENEWAL_DATE_EXISTS") + " :: S_RE_EFFDT_LESS_EQ_CURR_DT :: " + data.getSessionData("S_RE_EFFDT_LESS_EQ_CURR_DT") + " :: S_RE_EFFDT_RELAPSE_PERIOD_CHECK :: " + data.getSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK") + " :: S_RENEWAL_PENDING :: " + data.getSessionData("S_RENEWAL_PENDING") + " :: S_POLICY_BALANCE_EQ_ZERO :: " + data.getSessionData("S_POLICY_BALANCE_EQ_ZERO"));
						strExitState = Constants.SU;
					}
					
					else if(null != isRenewalPending && isRenewalPending.equalsIgnoreCase("Y") && null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) <= 0.00 && null != renewalEffectiveDate && !renewalEffectiveDate.isEmpty() && !isSystemDateGreater(data, currElementName, caa, renewalEffectiveDate) && ((null == renewalNextAmount || renewalNextAmount.isEmpty() || Double.parseDouble(renewalNextAmount) == 0.00) || (null == renewalNextDate || renewalNextDate.isEmpty()))) {
						data.setSessionData("CASE_BELONGS_TO", "CL_3");
						data.setSessionData("S_POLICY_STATUS", "ACT");
						data.setSessionData("S_RENEWAL_AMOUNT_EXISTS", "False");
						data.setSessionData("S_RENEWAL_DATE_EXISTS", "False");
						data.setSessionData("S_RE_EFFDT_LESS_EQ_CURR_DT", "True");
						data.setSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK", "False");
						data.setSessionData("S_RENEWAL_PENDING", "Y");
						data.setSessionData("S_POLICY_BALANCE_EQ_ZERO", "True");
						data.addToLog(currElementName, "Identified as Case 4 :: Either Renewal Amount or Renewal Date is not Available :: Marking as Invalid Data :: Next Step - Transfer");
						strExitState = Constants.SU;
					}
					
					else if(null != isRenewalPending && isRenewalPending.equalsIgnoreCase("Y") && null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) > 0.00 && null != renewalNextAmount && !renewalNextAmount.isEmpty() && Double.parseDouble(renewalNextAmount) > 0.00 && null != renewalNextDate && !renewalNextDate.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_1");
						data.setSessionData("S_POLICY_STATUS", "ACT");
						data.setSessionData("S_RENEWAL_PENDING", "Y");
						data.setSessionData("S_POLICY_BALANCE_GR_THAN_ZERO", "True");
						data.setSessionData("S_RENEWAL_INVOICE_DATE_EXISTS", "True");
						data.setSessionData("S_RENEWAL_INVOICE_AMOUNT_EXISTS", "True");
						data.setSessionData("S_NEXT_PAYMENT_AMOUNT", renewalNextAmount);
						data.setSessionData("S_NEXT_PAYMENT_DATE", renewalNextDate.replaceAll("-", ""));
						data.addToLog(currElementName, "Identified as Case 5 :: Setting Required Flags & Values Into Session :: S_NEXT_PAYMENT_AMOUNT :: " + data.getSessionData("S_NEXT_PAYMENT_AMOUNT") + " :: S_NEXT_PAYMENT_DATE :: " + data.getSessionData("S_NEXT_PAYMENT_DATE") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_RENEWAL_PENDING :: "+data.getSessionData("S_RENEWAL_PENDING") + " :: S_POLICY_BALANCE_GR_THAN_ZERO :: " + data.getSessionData("S_POLICY_BALANCE_GR_THAN_ZERO") + ":: S_RENEWAL_INVOICE_DATE_EXISTS :: " + data.getSessionData("S_RENEWAL_INVOICE_DATE_EXISTS") + " :: S_RENEWAL_INVOICE_AMOUNT_EXISTS :: " + data.getSessionData("S_RENEWAL_INVOICE_AMOUNT_EXISTS"));
						strExitState = Constants.SU;
					}
					
					else if(null != isRenewalPending && isRenewalPending.equalsIgnoreCase("Y") && null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) > 0.00 && ((null == renewalNextAmount || renewalNextAmount.isEmpty() || Double.parseDouble(renewalNextAmount) == 0.00) || (null == renewalNextDate || renewalNextDate.isEmpty()))) {
						data.setSessionData("CASE_BELONGS_TO", "CL_1");
						data.setSessionData("S_POLICY_STATUS", "ACT");
						data.setSessionData("S_RENEWAL_PENDING", "Y");
						data.setSessionData("S_POLICY_BALANCE_GR_THAN_ZERO", "False");
						data.setSessionData("S_RENEWAL_INVOICE_DATE_EXISTS", "False");
						data.setSessionData("S_RENEWAL_INVOICE_AMOUNT_EXISTS", "True");
						data.setSessionData("S_NEXT_PAYMENT_AMOUNT", renewalNextAmount);
						data.setSessionData("S_NEXT_PAYMENT_DATE", renewalNextDate.replaceAll("-", ""));
						data.addToLog(currElementName, "Identified as Case 5 :: Either Renewal Amount or Renewal Date is not Available :: Marking as Invalid Data :: Next Step - Transfer");
						strExitState = Constants.SU;
					}
					else {
						data.addToLog(currElementName, "Parameters to check case list is null/empty");
						data.setSessionData("S_POLICY_STATUS", "ACT");
						data.setSessionData("CASE_BELONGS_TO", "CL_10");
						strExitState = Constants.SU;
					}
				  
				}
				
				else if(policyStatus.equalsIgnoreCase("PEN")) {
					
					if (null != nextPaymentAmount && !nextPaymentAmount.isEmpty() && Double.parseDouble(nextPaymentAmount) > 0.00 && null != policyActivityDate && !policyActivityDate.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_1");
						data.setSessionData("S_POLICY_STATUS", "PEN");
						data.setSessionData("S_NEXT_PAYMENT_AMOUNT_EXISTS", "True");
						data.setSessionData("S_POLICY_ACTIVITY_DATE_EXISTS", "True");
						data.setSessionData("S_PL_ACTIVITY_DT_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, policyActivityDate , -1).replaceAll("-", ""));
						data.setSessionData("S_NEXT_PAYMENT_AMOUNT", nextPaymentAmount);
						data.addToLog(currElementName, "Identified as Case 13 :: Setting Required Flags & Values Into Session :: S_NEXT_PAYMENT_AMOUNT :: " + data.getSessionData("S_NEXT_PAYMENT_AMOUNT") + " :: S_PL_ACTIVITY_DT_MINUS_1 :: " + data.getSessionData("S_PL_ACTIVITY_DT_MINUS_1") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_NEXT_PAYMENT_AMOUNT_EXISTS :: " + data.getSessionData("S_NEXT_PAYMENT_AMOUNT_EXISTS") + " :: S_POLICY_ACTIVITY_DATE_EXISTS :: " + data.getSessionData("S_POLICY_ACTIVITY_DATE_EXISTS"));
						strExitState = Constants.SU;
					}
					
					else if(null != nextPaymentAmount && !nextPaymentAmount.isEmpty() && Double.parseDouble(nextPaymentAmount) > 0.00 && null != policyCancellationDate && !policyCancellationDate.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_1");
						data.setSessionData("S_POLICY_STATUS", "PEN");
						data.setSessionData("S_NEXT_PAYMENT_AMOUNT_EXISTS", "True");
						data.setSessionData("S_POLICY_CANCELLATION_DATE_EXISTS", "True");
						data.setSessionData("S_PL_CANCELLATION_DT_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, policyCancellationDate, -1).replaceAll("-", ""));
						data.setSessionData("S_NEXT_PAYMENT_AMOUNT", nextPaymentAmount);
						data.setSessionData("S_PL_CANCEL_DT_PLUS_9", subtractOrAddDaysfromDate(data, currElementName, caa, policyCancellationDate, 9).replaceAll("-", ""));
						data.addToLog(currElementName, "Identified as Case 14 :: Setting Required Flags & Values Into Session :: S_NEXT_PAYMENT_AMOUNT :: " + data.getSessionData("S_NEXT_PAYMENT_AMOUNT") + " :: S_PL_CANCELLATION_DT_MINUS_1 :: " + data.getSessionData("S_PL_CANCELLATION_DT_MINUS_1") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_NEXT_PAYMENT_AMOUNT_EXISTS :: " + data.getSessionData("S_NEXT_PAYMENT_AMOUNT_EXISTS") + " :: S_POLICY_CANCELLATION_DATE_EXISTS :: " + data.getSessionData("S_POLICY_CANCELLATION_DATE_EXISTS"));
						strExitState = Constants.SU;
					}
					else {
						data.addToLog(currElementName, "Parameters to check case list is null/empty");
						data.setSessionData("CASE_BELONGS_TO", "CL_10");
						data.setSessionData("S_POLICY_STATUS", "PEN");
						strExitState = Constants.SU;
					}
				}
				
				else if(policyStatus.equalsIgnoreCase("REN")) {
					
//					if (null != renewalEffectiveDate && !renewalEffectiveDate.isEmpty() && !isSystemDateGreater(data, currElementName, caa, renewalEffectiveDate) && null != renewalNextAmount && !renewalNextAmount.isEmpty() && Double.parseDouble(renewalNextAmount) > 0.00 && null != renewalNextDate && !renewalNextDate.isEmpty() && null != nsfIndicator && !nsfIndicator.equalsIgnoreCase("Y")) {
//						data.setSessionData("CASE_BELONGS_TO", "CL_2");
//						data.setSessionData("S_POLICY_STATUS", "REN");
//						data.setSessionData("S_POLICY_EFFECTIVE_DATE_EXISTS", "False");
//						data.setSessionData("S_NFS_STATE", "N");
//						data.setSessionData("S_POLICY_BALANCE_EQ_ZERO", "True");
//						data.setSessionData("S_RE_EFFDT_LESS_CURR_DT", "True");
//						data.setSessionData("S_RENEWAL_PAYMENT_AMOUNT_EXISTS", "True");
//						data.setSessionData("S_RENEWAL_PAYMENT_EXISTS", "True");
//						data.setSessionData("S_RENEWAL_PAYMENT_AMOUNT", renewalNextAmount);
//						data.setSessionData("S_RENEWAL_PAYMENT_DATE", renewalNextDate.replaceAll("-", ""));
//						data.setSessionData("S_RENEWAL_EFFECTIVE_DATE", renewalEffectiveDate.replaceAll("-", ""));
//						data.addToLog(currElementName, "Identified as Case 8 :: Setting Required Flags & Values Into Session :: S_RENEWAL_PAYMENT_AMOUNT :: " + data.getSessionData("S_RENEWAL_PAYMENT_AMOUNT") + " :: S_RENEWAL_PAYMENT_DATE :: " + data.getSessionData("S_RENEWAL_PAYMENT_DATE") + " :: S_RENEWAL_EFFECTIVE_DATE :: " + data.getSessionData("S_RENEWAL_EFFECTIVE_DATE") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICY_EFFECTIVE_DATE_EXISTS :: " + data.getSessionData("S_POLICY_EFFECTIVE_DATE_EXISTS") + " :: S_NFS_STATE :: " +data.getSessionData("S_NFS_STATE") + " :: S_POLICY_BALANCE_EQ_ZERO :: " + data.getSessionData("S_POLICY_BALANCE_EQ_ZERO") + " :: S_RE_EFFDT_LESS_CURR_DT :: " + data.getSessionData("S_RE_EFFDT_LESS_CURR_DT") + " :: S_RENEWAL_PAYMENT_AMOUNT_EXISTS :: " + data.getSessionData("S_RENEWAL_PAYMENT_AMOUNT_EXISTS") + " :: S_RENEWAL_PAYMENT_EXISTS :: " + data.getSessionData("S_RENEWAL_PAYMENT_EXISTS"));
//						strExitState = Constants.SU;
//					}
//					
//					else if(null != renewalEffectiveDate && !renewalEffectiveDate.isEmpty() && !isSystemDateGreater(data, currElementName, caa, subtractOrAddDaysfromDate(data, currElementName, caa, renewalEffectiveDate, 29)) && null != nsfIndicator && !nsfIndicator.equalsIgnoreCase("Y")) {
//						data.setSessionData("CASE_BELONGS_TO", "CL_3");
//						data.setSessionData("S_POLICY_STATUS", "REN");
//						data.setSessionData("S_RENEWAL_AMOUNT_EXISTS", "True");
//						data.setSessionData("S_RENEWAL_DATE_EXISTS", "True");
//						data.setSessionData("S_RE_EFFDT_LESS_EQ_CURR_DT", "True");
//						data.setSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK", "True");
//						data.setSessionData("S_NSF_STATE", "N");
//						data.setSessionData("S_RENEWL_EFFECTIVE_DATE", renewalEffectiveDate.replaceAll("-", ""));
//						data.setSessionData("S_RENEWAL_INVOICE_AMOUNT", renewalNextAmount);
//						data.setSessionData("S_RE_EFFDT_PLUS_RELAPSE_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, renewalEffectiveDate, -29).replaceAll("-", ""));
//						data.addToLog(currElementName, "Identified as Case 9 :: Setting Required Flags & Values Into Session :: S_RENEWL_EFFECTIVE_DATE :: " + data.getSessionData("S_RENEWL_EFFECTIVE_DATE") + " :: S_RENEWAL_INVOICE_AMOUNT :: " + data.getSessionData("S_RENEWAL_INVOICE_AMOUNT") + " :: S_RE_EFFDT_PLUS_RELAPSE_MINUS_1 :: " + data.getSessionData("S_RE_EFFDT_PLUS_RELAPSE_MINUS_1") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_RENEWAL_AMOUNT_EXISTS :: " + data.getSessionData("S_RENEWAL_AMOUNT_EXISTS") + " :: S_RE_EFFDT_LESS_EQ_CURR_DT :: " + data.getSessionData("S_RE_EFFDT_LESS_EQ_CURR_DT") + " :: S_RE_EFFDT_RELAPSE_PERIOD_CHECK :: " + data.getSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK") + " :: S_NSF_STATE :: " + data.getSessionData("S_NSF_STATE"));
//						strExitState = Constants.SU;
//					}
//					
//					else if(null != renewalEffectiveDate && !renewalEffectiveDate.isEmpty() && !isSystemDateGreater(data, currElementName, caa, renewalEffectiveDate) && null != renewalNextAmount && !renewalNextAmount.isEmpty() && Double.parseDouble(renewalNextAmount) > 0.00 && null != renewalNextDate && !renewalNextDate.isEmpty() && null != nsfIndicator && nsfIndicator.equalsIgnoreCase("Y")) {
//						data.setSessionData("CASE_BELONGS_TO", "CL_4");
//						data.setSessionData("S_POLICY_STATUS", "REN");
//						data.setSessionData("S_NSF_STATE", "Y");
//						data.setSessionData("S_RE_EFFDT_LESS_CURR_DT", "True");
//						data.setSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK", "False");
//						data.setSessionData("S_RENEWAL_AMOUNT_EXISTS", "True");
//						data.setSessionData("S_RENEWAL_DATE_EXISTS", "True");
//						data.setSessionData("S_RE_CANCEL_DT_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, -1).replaceAll("-", ""));
//						Double amount = Double.parseDouble(renewalNextAmount) + Double.parseDouble(nsfAmount);
//						String finalamount = amount.toString();
//						data.setSessionData("S_RE_PAY_AMOUNT_PLUS_NSFFEE", finalamount);
//						data.setSessionData("S_RE_CANCEL_DT_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, -1).replaceAll("-", ""));
//						data.addToLog(currElementName, "Identified as Case 10 :: Setting Required Flags & Values Into Session :: S_RE_CANCEL_DT_MINUS_1 :: " + data.getSessionData("S_RE_CANCEL_DT_MINUS_1") + " :: S_RE_PAY_AMOUNT_PLUS_NSFFEE :: " + data.getSessionData("S_RE_PAY_AMOUNT_PLUS_NSFFEE") + " :: S_RE_CANCEL_DT_MINUS_1 :: " + data.getSessionData("S_RE_CANCEL_DT_MINUS_1") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_NSF_STATE :: " + data.getSessionData("S_NSF_STATE") + " :: S_RE_EFFDT_LESS_CURR_DT :: " + data.getSessionData("S_RE_EFFDT_LESS_CURR_DT") + " :: S_RE_EFFDT_RELAPSE_PERIOD_CHECK :: " + data.getSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK") + " :: S_RENEWAL_AMOUNT_EXISTS :: " + data.getSessionData("S_RENEWAL_AMOUNT_EXISTS") + " :: S_RENEWAL_DATE_EXISTS :: " +data.getSessionData("S_RENEWAL_DATE_EXISTS"));
//						strExitState = Constants.SU;
//					}
//					
//					else if(null != renewalCancellationDate && !renewalCancellationDate.isEmpty() && !isSystemDateGreater(data, currElementName, caa, subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, 29)) && null != nsfIndicator && nsfIndicator.equalsIgnoreCase("Y") && null != renewalEffectiveDate && renewalCancellationDate.equalsIgnoreCase(renewalEffectiveDate)) {
//						data.setSessionData("CASE_BELONGS_TO", "CL_4");
//						data.setSessionData("S_POLICY_STATUS", "REN");
//						data.setSessionData("S_NSF_STATE", "Y");
//						data.setSessionData("S_RE_EFFDT_LESS_CURR_DT", "True");
//						data.setSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK", "True");
//						data.setSessionData("S_RE_CANCELDT_EQ_RE_EFFDT", "True");
//						data.setSessionData("S_RE_CANCEL_DT_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, -1).replaceAll("-", ""));
//						data.setSessionData("S_NSF_FEE", nsfAmount);
//						data.setSessionData("S_RE_CANCEL_DT_PLUS_44", subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, 44).replaceAll("-", ""));
//						Double amount = Double.parseDouble(renewalNextAmount) + Double.parseDouble(nsfAmount);
//						String finalAmount = amount.toString();
//						data.setSessionData("S_RE_PAY_AMOUNT_PLUS_NSFFEE", finalAmount);
//						data.setSessionData("S_RE_CANCELDT_PLUS_RELAPSE_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, 29).replaceAll("-", ""));
//						data.addToLog(currElementName, "Identified as Case 11 :: Setting Required Flags & Values Into Session :: S_RE_CANCEL_DT_MINUS_1 :: " + data.getSessionData("S_RE_CANCEL_DT_MINUS_1") + " :: S_NSF_FEE :: " + data.getSessionData("S_NSF_FEE") + " :: S_RE_CANCEL_DT_PLUS_44 :: " + data.getSessionData("S_RE_CANCEL_DT_PLUS_44") + " :: S_RE_PAY_AMOUNT_PLUS_NSFFEE :: " + data.getSessionData("S_RE_PAY_AMOUNT_PLUS_NSFFEE") + " :: S_RE_CANCELDT_PLUS_RELAPSE_MINUS_1 :: " + data.getSessionData("S_RE_CANCELDT_PLUS_RELAPSE_MINUS_1") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_NSF_STATE :: " + data.getSessionData("S_NSF_STATE") + " :: S_RE_EFFDT_LESS_CURR_DT :: " + data.getSessionData("S_RE_EFFDT_LESS_CURR_DT") + " :: S_RE_EFFDT_RELAPSE_PERIOD_CHECK :: " + data.getSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK") + " :: S_RENEWAL_AMOUNT_EXISTS :: " + data.getSessionData("S_RENEWAL_AMOUNT_EXISTS") + " :: S_RENEWAL_DATE_EXISTS :: " + data.getSessionData("S_RENEWAL_DATE_EXISTS"));
//						strExitState = Constants.SU;
//					}
//					
//					else if(null != renewalCancellationDate && !renewalCancellationDate.isEmpty() && !isSystemDateGreater(data, currElementName, caa, subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, 29)) && null != nsfIndicator && nsfIndicator.equalsIgnoreCase("Y")) {
//						data.setSessionData("CASE_BELONGS_TO", "CL_4");
//						data.setSessionData("S_POLICY_STATUS", "REN");
//						data.setSessionData("S_NSF_STATE", "Y");
//						data.setSessionData("S_RE_EFFDT_LESS_CURR_DT", "True");
//						data.setSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK", "False");
//						data.setSessionData("S_RENEWAL_AMOUNT_EXISTS", "False");
//						data.setSessionData("S_RENEWAL_DATE_EXISTS", "False");
//						data.setSessionData("S_RE_CANCEL_DT_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, -1).replaceAll("-", ""));
//						data.setSessionData("S_NSF_FEE", nsfAmount);
//						data.setSessionData("S_RE_CANCEL_DT_PLUS_44", subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, 44).replaceAll("-", ""));
//						data.addToLog(currElementName, "Identified as Case 12 :: Setting Required Flags & Values Into Session :: S_RE_CANCEL_DT_MINUS_1 :: " + data.getSessionData("S_RE_CANCEL_DT_MINUS_1") + " :: S_NSF_FEE :: " + data.getSessionData("S_NSF_FEE") + " :: S_RE_CANCEL_DT_PLUS_44 :: " + data.getSessionData("S_RE_CANCEL_DT_PLUS_44") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_NSF_STATE :: " + data.getSessionData("S_NSF_STATE") + " :: S_RE_EFFDT_LESS_CURR_DT :: " + data.getSessionData("S_RE_EFFDT_LESS_CURR_DT") + " :: S_RE_EFFDT_RELAPSE_PERIOD_CHECK :: " + data.getSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK") + " :: S_RENEWAL_AMOUNT_EXISTS :: " + data.getSessionData("S_RENEWAL_AMOUNT_EXISTS") + " :: S_RENEWAL_DATE_EXISTS :: " + data.getSessionData("S_RENEWAL_DATE_EXISTS"));
//						strExitState = Constants.SU;
//					}
					data.addToLog(currElementName, "Identified as Case_9");
					data.setSessionData("CASE_BELONGS_TO", "CL_9");
					data.setSessionData("S_POLICY_STATUS", "REN");
					strExitState = Constants.SU;
				}
				
				else if(policyStatus.equalsIgnoreCase("CAN")) {
					
					if (null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) <= 0.00 && null != nsfIndicator && !nsfIndicator.equalsIgnoreCase("Y")) {
						data.setSessionData("S_POLICY_STATUS", "CAN");
						if(null!=policyActivityDate && !policyActivityDate.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_2");
						data.setSessionData("S_POLICY_EFFECTIVE_DATE_EXISTS", "False");
						data.setSessionData("S_NFS_STATE", "N");
						data.setSessionData("S_POLICY_BALANCE_LESSTHAN_ZERO", "True");
						data.setSessionData("S_POLICY_BALANCE_ANN", policyBalanceDue);
						data.setSessionData("S_POLICY_ACTIVITY_DATE", policyActivityDate.replaceAll("-", ""));
						data.addToLog(currElementName, "Identified as Case 19 :: Setting Required Flags & Values Into Session :: S_POLICY_BALANCE_ANN :: " + data.getSessionData("S_POLICY_BALANCE_ANN") + " :: S_POLICY_ACTIVITY_DATE :: " + data.getSessionData("S_POLICY_ACTIVITY_DATE") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICY_EFFECTIVE_DATE_EXISTS :: " + data.getSessionData("S_POLICY_EFFECTIVE_DATE_EXISTS") + " :: S_NFS_STATE :: " + data.getSessionData("S_NFS_STATE") + " :: S_POLICY_BALANCE_LESSTHAN_ZERO :: " +data.getSessionData("S_POLICY_BALANCE_LESSTHAN_ZERO"));
						strExitState = Constants.SU;
						}else {
							data.addToLog(currElementName, "Parameters to check case list is null/empty");
							data.setSessionData("S_POLICY_STATUS", "CAN");
							data.setSessionData("CASE_BELONGS_TO", "CL_10");
							strExitState = Constants.SU;
						}
					}
					
					else if(null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) >= 0.00 && null != isPolicyinCollectionMode && isPolicyinCollectionMode.equalsIgnoreCase("Y") && null != policyCancellationDate && daysSinceDate(data, currElementName, caa, policyCancellationDate) < 45) {
						data.setSessionData("S_POLICY_STATUS", "CAN");
						if(collectionAmount!=null && !collectionAmount.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_6");						
						data.setSessionData("S_POLICYIN_COLLECTION_MODE", "Y");
						data.setSessionData("S_POLICY_CANCELLED_BEFORE_44_DAYS", "True");
						data.setSessionData("S_POLICY_BALANCE_GR_THANEQ_ZERO", "True");
						data.setSessionData("S_COLLECTION_AMOUNT", collectionAmount);
						data.addToLog(currElementName, "Identified as Case 17 :: Setting Required Flags & Values Into Session :: S_COLLECTION_AMOUNT :: " +data.getSessionData("S_COLLECTION_AMOUNT") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICYIN_COLLECTION_MODE :: " + data.getSessionData("S_POLICYIN_COLLECTION_MODE") + " :: S_POLICY_CANCELLED_BEFORE_44_DAYS :: " + data.getSessionData("S_POLICY_CANCELLED_BEFORE_44_DAYS") + " :: S_POLICY_BALANCE_GR_THANEQ_ZERO :: " + data.getSessionData("S_POLICY_BALANCE_GR_THANEQ_ZERO"));
						strExitState = Constants.SU;
						}else {
							
							data.addToLog(currElementName, "Parameters to check case list is null/empty");
							data.setSessionData("CASE_BELONGS_TO", "CL_10");
							strExitState = Constants.SU;
						}
					}
					
					else if(null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) >= 0.00 && null != isPolicyinCollectionMode && isPolicyinCollectionMode.equalsIgnoreCase("Y") && null != policyCancellationDate && daysSinceDate(data, currElementName, caa, policyCancellationDate) > 45 && daysSinceDate(data, currElementName, caa, policyCancellationDate) < 66) {
						data.setSessionData("CASE_BELONGS_TO", "CL_6");
						data.setSessionData("S_POLICY_STATUS", "CAN");
						data.setSessionData("S_POLICYIN_COLLECTION_MODE", "Y");
						data.setSessionData("S_POLICY_CANCELLED_BEFORE_44_DAYS", "False");
						data.setSessionData("S_POLICY_CANCELLED_BW_44_66_DAYS", "True");
						data.addToLog(currElementName, "Identified as Case 17 ::  policy Is Cancelled Between 45 to 66 Days :: Marking as Invalid Data :: Next Step - Transfer");
						strExitState = Constants.SU;
					}
					
					else if(null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) >= 0.00 && null != isPolicyinCollectionMode && isPolicyinCollectionMode.equalsIgnoreCase("Y") && null != policyCancellationDate && daysSinceDate(data, currElementName, caa, policyCancellationDate) > 66) {
						data.setSessionData("S_POLICY_STATUS", "CAN");
						if(collectionAmount!=null && !collectionAmount.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_6");
						
						data.setSessionData("S_POLICYIN_COLLECTION_MODE", "Y");
						data.setSessionData("S_POLICY_CANCELLED_BEFORE_44_DAYS", "False");
						data.setSessionData("S_POLICY_CANCELLED_BW_44_66_DAYS", "False");
						data.setSessionData("S_COLLECTION_AMOUNT", collectionAmount);
						data.addToLog(currElementName, "Identified as Case 17A :: Setting Required Flags & Values Into Session :: S_COLLECTION_AMOUNT :: " + data.getSessionData("S_COLLECTION_AMOUNT") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICYIN_COLLECTION_MODE :: " + data.getSessionData("S_POLICYIN_COLLECTION_MODE") + " :: S_POLICY_CANCELLED_BEFORE_44_DAYS :: " + data.getSessionData("S_POLICY_CANCELLED_BEFORE_44_DAYS") + " :: S_POLICY_CANCELLED_BW_44_66_DAYS :: " + data.getSessionData("S_POLICY_CANCELLED_BW_44_66_DAYS"));
						strExitState = Constants.SU;
						}else {
							data.addToLog(currElementName, "Parameters to check case list is null/empty");
							data.setSessionData("CASE_BELONGS_TO", "CL_10");
							strExitState = Constants.SU;
						}
					} 
					
					else if(null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) >= 0.00 && null != isPolicyinCollectionMode && isPolicyinCollectionMode.equalsIgnoreCase("Y") && null != isRenewalPending && isRenewalPending.equalsIgnoreCase("Y") && null != isPolicyQualifiedForReinstatewithLapse && isPolicyQualifiedForReinstatewithLapse.equalsIgnoreCase("Y") && null != isPolicyQualifiedForReWrittenWithLapse && isPolicyQualifiedForReWrittenWithLapse.equalsIgnoreCase("Y") && null != nsfIndicator && nsfIndicator.equalsIgnoreCase("Y")) {
						data.setSessionData("S_POLICY_STATUS", "CAN");
						if(renewalCancellationDate!=null && !renewalCancellationDate.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_7");
						
						data.setSessionData("S_POLICYIN_COLLECTION_MODE", "Y");
						data.setSessionData("S_RENEWAL_PENDING", "Y");
						data.setSessionData("S_PL_QUALIFIED_REINSTATE_WITH_LAPSE", "Y");
						data.setSessionData("S_NSF_STATE", "Y");
						data.setSessionData("S_POLICY_BALANCE_ANN", policyBalanceDue);
						data.setSessionData("S_RE_CANCEL_DT_PLUS_44", subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, 44).replaceAll("-", ""));
						data.addToLog(currElementName, "Identified as Case 18 :: Setting Required Flags & Values Into Session :: S_POLICY_BALANCE_ANN :: " + data.getSessionData("S_POLICY_BALANCE_ANN") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICYIN_COLLECTION_MODE :: " + data.getSessionData("S_POLICYIN_COLLECTION_MODE") + " :: S_RENEWAL_PENDING :: " + data.getSessionData("S_RENEWAL_PENDING") + " :: S_PL_QUALIFIED_REINSTATE_WITH_LAPSE :: " + data.getSessionData("S_PL_QUALIFIED_REINSTATE_WITH_LAPSE") + " :: S_NSF_STATE :: " + data.getSessionData("S_NSF_STATE"));
						strExitState = Constants.SU;
						}else {
							data.addToLog(currElementName, "Parameters to check case list is null/empty");
							data.setSessionData("CASE_BELONGS_TO", "CL_10");
							strExitState = Constants.SU;
						}
					}
					
					else if (null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) >= 0.00 && null != isPolicyinCollectionMode && isPolicyinCollectionMode.equalsIgnoreCase("Y") && null != isRenewalPending && isRenewalPending.equalsIgnoreCase("Y") && null != isPolicyQualifiedForReinstatewithLapse && isPolicyQualifiedForReinstatewithLapse.equalsIgnoreCase("Y") && null != isPolicyQualifiedForReWrittenWithLapse && isPolicyQualifiedForReWrittenWithLapse.equalsIgnoreCase("Y") && null != nsfIndicator && !nsfIndicator.equalsIgnoreCase("Y")) {
						data.setSessionData("S_POLICY_STATUS", "CAN");
						if(renewalCancellationDate!=null && !renewalCancellationDate.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_7");
						
						data.setSessionData("S_POLICYIN_COLLECTION_MODE", "Y");
						data.setSessionData("S_RENEWAL_PENDING", "Y");
						data.setSessionData("S_PL_QUALIFIED_REINSTATE_WITH_LAPSE", "Y");
						data.setSessionData("S_NSF_STATE", "N");
						data.setSessionData("S_POLICY_BALANCE_ANN", policyBalanceDue);
						data.setSessionData("S_RE_CANCEL_DT_PLUS_44", subtractOrAddDaysfromDate(data, currElementName, caa, renewalCancellationDate, 44).replaceAll("-", ""));
						data.addToLog(currElementName, "Identified as Case 21 :: Setting Required Flags & Values Into Session :: S_POLICY_BALANCE_ANN :: " + data.getSessionData("S_POLICY_BALANCE_ANN") + " :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICYIN_COLLECTION_MODE :: " + data.getSessionData("S_POLICYIN_COLLECTION_MODE") + " :: S_RENEWAL_PENDING :: " + data.getSessionData("S_RENEWAL_PENDING") + " :: S_PL_QUALIFIED_REINSTATE_WITH_LAPSE :: " + data.getSessionData("S_PL_QUALIFIED_REINSTATE_WITH_LAPSE") + " :: S_NSF_STATE :: " + data.getSessionData("S_NSF_STATE"));
						strExitState = Constants.SU;
						}else {
							data.addToLog(currElementName, "Parameters to check case list is null/empty");
							data.setSessionData("CASE_BELONGS_TO", "CL_10");
							strExitState = Constants.SU;
						}
					}
					
					else if (null != policyBalanceDue && !policyBalanceDue.isEmpty() && Double.parseDouble(policyBalanceDue) >= 0.00 && null != isPolicyinCollectionMode && isPolicyinCollectionMode.equalsIgnoreCase("Y") && null != isRenewalPending && isRenewalPending.equalsIgnoreCase("Y")) {
						data.setSessionData("CASE_BELONGS_TO", "CL_8");
						data.setSessionData("S_POLICY_STATUS", "CAN");
						data.setSessionData("S_POLICYIN_COLLECTION_MODE", "Y");
						data.setSessionData("S_RENEWAL_PENDING", "Y");
						data.setSessionData("S_POLICY_BALANCE_GR_THAN_ZERO", "Y");
						data.addToLog(currElementName, "Identified as Case 20 :: Setting Required Flags & Values Into Session :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICYIN_COLLECTION_MODE :: " + data.getSessionData("S_POLICYIN_COLLECTION_MODE") + " :: S_RENEWAL_PENDING :: " + data.getSessionData("S_RENEWAL_PENDING") + " :: S_POLICY_BALANCE_GR_THAN_ZERO :: " + data.getSessionData("S_POLICY_BALANCE_GR_THAN_ZERO"));
						strExitState = Constants.SU;
					}
					else {
						data.addToLog(currElementName, "Parameters to check case list is null/empty");
						data.setSessionData("CASE_BELONGS_TO", "CL_10");
						data.setSessionData("S_POLICY_STATUS", "CAN");
						strExitState = Constants.SU;
					}
				}
				
				else if (policyStatus.equalsIgnoreCase("EXP")) {
					
					if (null != policyEffectiveDate && !policyEffectiveDate.isEmpty()) {
						data.setSessionData("CASE_BELONGS_TO", "CL_2");
						data.setSessionData("S_POLICY_STATUS", "EXP");
						data.setSessionData("S_POLICY_EFFECTIVE_DATE_EXISTS", "True");
						data.setSessionData("S_PL_EFF_DT_MINUS_1", subtractOrAddDaysfromDate(data, currElementName, caa, policyEffectiveDate, -1).replaceAll("-", ""));
						data.addToLog(currElementName, "Identified as Case 22 :: Setting Required Flags & Values Into Session :: S_PL_EFF_DT_MINUS_1 :: " + data.getSessionData("S_PL_EFF_DT_MINUS_1") + "  :: S_POLICY_STATUS :: " + data.getSessionData("S_POLICY_STATUS") + " :: S_POLICY_EFFECTIVE_DATE_EXISTS :: " + data.getSessionData("S_POLICY_EFFECTIVE_DATE_EXISTS"));
						strExitState = Constants.SU;
					}else {
						strExitState = Constants.ER;
					}
				}
			}
			else {
				data.addToLog(currElementName, "Policy Status Value is null/Empty from the API response :: Setting Exit State as Error");
				data.setSessionData("CASE_BELONGS_TO", "CL_10");
				strExitState = Constants.SU;
			}
			
			
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in checkCaseListNew Method in BW Billing :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	public String getValuesfromFinalObject(DecisionElementData data, String currElementName, CommonAPIAccess caa, JSONObject finalObject, String key) {
		String value = null;
		
		try {
			
			if (null != finalObject && !finalObject.isEmpty()) {
				
				if (null != key && finalObject.containsKey(key)) {
					value = (String) finalObject.get(key);
					return value;
				}
				else {
					data.addToLog(currElementName, key + " Not Available in final consolidated Object :: "+finalObject);
				}
			}
			else {
				data.addToLog(currElementName, "Final Object is null :: "+finalObject);
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in checkCaseListNew Method in BW Billing :: " + e);
			caa.printStackTrace(e);
		}
		return value;
	}
	
	public boolean isSystemDateGreater(DecisionElementData data, String currElementName, CommonAPIAccess caa, String Date) {
		boolean isSystemDateGreater = false; 
		
		try {
			Date systemDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date providedDate = sdf.parse(Date);
			isSystemDateGreater = systemDate.after(providedDate);
			data.setSessionData("S_SYSTEMDATE_GREATER", isSystemDateGreater);
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in checkCaseListNew Method in BW Billing :: " + e);
			caa.printStackTrace(e);
		}
		return isSystemDateGreater;
	}
	
	public String subtractOrAddDaysfromDate(DecisionElementData data, String currElementName, CommonAPIAccess caa, String Date, int noOfDays) {
		String finalDate = null;
		
		try {
			data.addToLog(currElementName, "Before Modifying Date :: "+Date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    Date date = dateFormat.parse(Date);
		    
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(date);
		    calendar.add(Calendar.DAY_OF_MONTH, noOfDays); // Subtract or Add days
		    
		    Date newDate = calendar.getTime();
		    finalDate = dateFormat.format(newDate);
		    data.addToLog(currElementName, "After Modifying/Subtracting 1 Day from Date :: "+finalDate);
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in checkCaseListNew Method in BW Billing :: " + e);
			caa.printStackTrace(e);
		}
		return finalDate;
	}
	
	public long daysSinceDate(DecisionElementData data, String currElementName, CommonAPIAccess caa, String Date) {
		long daysSince = 0;
		
		try {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    Date inputDate = dateFormat.parse(Date);
		    
		    // Get today's date
		    Date today = new Date();
		    
		    // Calculate the difference in milliseconds
		    long differenceInMilliseconds = today.getTime() - inputDate.getTime();
		    
		    // Convert milliseconds to days and round down to whole days
		    daysSince = differenceInMilliseconds / (1000 * 60 * 60 * 24);
			
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in checkCaseListNew Method in BW Billing :: " + e);
			caa.printStackTrace(e);
		}
		
		return daysSince;
	}
	
	
	public JSONObject apiResponseManipulationGeneral(DecisionElementData data, String currElementName, CommonAPIAccess caa, JSONObject generalBillingInforesp) {
		JSONObject finalObject = new JSONObject();
		JSONObject policyDetailsObj = new JSONObject();
		JSONObject billingDetailsObj = new JSONObject();
		JSONObject renewalBillingDetailsObj = new JSONObject();
		JSONObject renewalPolicyDetailsObj = new JSONObject();
		String policyStatus = null;
		String nextPaymentAmount = null;
		String nextPaymentDate = null;
		String policyActivityDate = null;
		String policyCancellationDate = null;
		String isRenewalPendingIndicator = null;
		String policyBalanceDue = null;
		String renewalNextDate = null;
		String renewalNextAmount = null;
		String policyEffectiveDate = null;
		String nsfIndicator = null;
		String renewalEffectiveDate = null;
		String renewalLastAmount = null;
		String renewalLastDate = null;
		String relapsePeriod = null;
		String renewalCancellationDate = null;
		String isPolicyinCollectionMode = null;
		String collectionAmount = null;
		String isPolicyQualifiedForReinstatewithLapse = null;
		String isPolicyQualifiedForRewrittenwithLapse = null;
		String isPolicyReWriteOfferAvailable = null;
		String policyReWriteOfferExpiryDate = null;
		String nsfAmount = null;
		String policyNumber = null;
		
		try {
			
			if (null != generalBillingInforesp && !generalBillingInforesp.isEmpty()) {
				
				
				if (generalBillingInforesp.containsKey("policyDetails")) {
					policyDetailsObj = (JSONObject) generalBillingInforesp.get("policyDetails");
					
					if (null != policyDetailsObj) {
						
						if (policyDetailsObj.containsKey("policyNumber")) {
							policyNumber = (String) policyDetailsObj.get("policyNumber");
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", policyNumber);
							data.addToLog(currElementName, "BW EPC PaymentUS ID :: "+policyNumber);
							finalObject.put("policynumber", policyNumber);
						}
						
						if (policyDetailsObj.containsKey("policyStatus")) {
							policyStatus = (String) policyDetailsObj.get("policyStatus");
							data.addToLog(currElementName, "Policy Status :: "+policyStatus);
							finalObject.put("policystatus", policyStatus);
						}
						else {
							data.addToLog(currElementName, "Policy Status Not Available in API Response :: "+policyDetailsObj.toString());
						}
						
						if (policyDetailsObj.containsKey("policyCancellationDate")) {
							policyCancellationDate = (String) policyDetailsObj.get("policyCancellationDate");
							data.addToLog(currElementName, "Policy Cancellation Date :: "+policyCancellationDate);
							finalObject.put("policycancellationdate",policyCancellationDate);
						}
						else {
							data.addToLog(currElementName, "Policy Cancellation Date Not Available in API Response :: "+policyDetailsObj.toString());
						}
						
						if (policyDetailsObj.containsKey("isRenewalPendingIndicatorAsString")) {
							isRenewalPendingIndicator = (String) policyDetailsObj.get("isRenewalPendingIndicatorAsString");
							data.addToLog(currElementName, "Is Renewal Pending Indicator :: "+isRenewalPendingIndicator);
							finalObject.put("isrenewalpending", isRenewalPendingIndicator);
						}
						else {
							data.addToLog(currElementName, "Renewal Pending Indicator Not Available in API Response :: "+policyDetailsObj.toString());
						}
						
						if (policyDetailsObj.containsKey("policyEffectiveDate")) {
							policyEffectiveDate = (String) policyDetailsObj.get("policyEffectiveDate");
							data.addToLog(currElementName, "Effective Date :: "+policyEffectiveDate);
							finalObject.put("effectivedate", policyEffectiveDate);
						}
						else {
							data.addToLog(currElementName, "Effective Date Not Available in API Response :: "+policyDetailsObj.toString());
						}
						
						if (policyDetailsObj.containsKey("notSufficientFundIndicatorAsString")) {
							nsfIndicator = (String) policyDetailsObj.get("notSufficientFundIndicatorAsString");
							data.addToLog(currElementName, "Not Sufficient Fund (NSF) Indicator :: "+nsfIndicator);
							finalObject.put("nsfindicator", nsfIndicator);
						}
						else {
							data.addToLog(currElementName, "Not Sufficient Fund (NSF) Indicator Not Available in API Response :: "+policyDetailsObj.toString());
						}
						
						if (policyDetailsObj.containsKey("notSufficientFundAmount")) {
							JSONObject notSufficientFundAmountObj = (JSONObject) policyDetailsObj.get("notSufficientFundAmount");
							
							if (null != notSufficientFundAmountObj && notSufficientFundAmountObj.containsKey("theCurrencyAmount")) {
								nsfAmount = (String) notSufficientFundAmountObj.get("theCurrencyAmount");
								data.addToLog(currElementName, "Not Sufficient Fund (NSF) Amount :: "+nsfAmount);
								finalObject.put("nsfamount", nsfAmount);
							}
						}
						else {
							data.addToLog(currElementName, "Not Sufficient Fund (NSF) Amount Not Available in API Response :: "+policyDetailsObj.toString());
						}
						
						if (policyDetailsObj.containsKey("lapsePeriodForPolicyRenewal")) {
							relapsePeriod = (String) policyDetailsObj.get("lapsePeriodForPolicyRenewal");
							data.addToLog(currElementName, "Relapse Period :: "+relapsePeriod);
							finalObject.put("relapseperiod", relapsePeriod);
						}
						else {
							data.addToLog(currElementName, "Relapse Period Not Available in API Response :: "+policyDetailsObj.toString());
						}
						
						if (policyDetailsObj.containsKey("isPolicyQualifiedForReinstateWithLapse")) {
							isPolicyQualifiedForReinstatewithLapse = (String) policyDetailsObj.get("isPolicyQualifiedForReinstateWithLapse");
							data.addToLog(currElementName, "Is Policy Qualified For Reinstate with Lapse :: "+isPolicyQualifiedForReinstatewithLapse);
							finalObject.put("ispolicyqualifiedforreinstatewithlapse", isPolicyQualifiedForReinstatewithLapse);
						}
						else {
							data.addToLog(currElementName, "Is Policy Qualified For Reinstate with Lapse Indicator Not Available in API Response :: "+policyDetailsObj.toString());
						}
						
						if (policyDetailsObj.containsKey("isPolicyQualifiedForRewrittenWithLapse")) {
							isPolicyQualifiedForRewrittenwithLapse = (String) policyDetailsObj.get("isPolicyQualifiedForRewrittenWithLapse");
							data.addToLog(currElementName, "Is Policy Qualified For ReWritten with Lapse :: "+isPolicyQualifiedForRewrittenwithLapse);
							finalObject.put("ispolicyqualifiedforrewrittenwithlapse", isPolicyQualifiedForRewrittenwithLapse);
						}
						
						if (policyDetailsObj.containsKey("isPolicyRewriteOfferAvailable")) {
							isPolicyReWriteOfferAvailable = (String) policyDetailsObj.get("isPolicyRewriteOfferAvailable");
							data.addToLog(currElementName, "policy ReWrite Offer Available ? :: "+isPolicyReWriteOfferAvailable);
							finalObject.put("policyrewriteofferavailable", isPolicyReWriteOfferAvailable);
						}
						else {
							data.addToLog(currElementName, "Policy ReWrite Offer Available Indicator Not Available in API Response :: "+policyDetailsObj.toString());
						}
						
						if (policyDetailsObj.containsKey("policyRewriteOfferExpiryDate")) {
							policyReWriteOfferExpiryDate = (String) policyDetailsObj.get("policyRewriteOfferExpiryDate");
							data.addToLog(currElementName, "policy ReWrite Offer Expiry Date :: "+policyReWriteOfferExpiryDate);
							finalObject.put("policyrewriteofferexpirydate", policyReWriteOfferExpiryDate);
						}
						else {
							data.addToLog(currElementName, "Policy ReWrite Offer Expiry Date Not Available in API Response :: "+policyDetailsObj.toString());
						}
					}
				}
				else {
					data.addToLog(currElementName, "General Billing Info Response does not contain Policy Details Object :: "+generalBillingInforesp.toString());
				}
				
				
				if (generalBillingInforesp.containsKey("billingDetails")) {
					billingDetailsObj = (JSONObject) generalBillingInforesp.get("billingDetails");
					
					if (null != billingDetailsObj) {
						
						if (billingDetailsObj.containsKey("nextPaymentAmount")) {
							JSONObject nextPaymentAmountObj = (JSONObject) billingDetailsObj.get("nextPaymentAmount");
							
							if (null != nextPaymentAmountObj && nextPaymentAmountObj.containsKey("theCurrencyAmount")) {
								nextPaymentAmount = (String) nextPaymentAmountObj.get("theCurrencyAmount");
								data.addToLog(currElementName, "Next Payment Amount :: "+nextPaymentAmount);
								finalObject.put("nextpaymentamount", nextPaymentAmount);
							}
						}
						else{
							data.addToLog(currElementName, "Next Payment Amount Object Not Available in API Response :: "+billingDetailsObj.toString());
						}
						
						if (billingDetailsObj.containsKey("nextPaymentDate")) {
							nextPaymentDate = (String) billingDetailsObj.get("nextPaymentDate");
							data.addToLog(currElementName, "Next Payment Date :: "+nextPaymentDate);
							finalObject.put("nextpaymentdate", nextPaymentDate);
						}
						else {
							data.addToLog(currElementName, "Next Payment Date Not Available in API Response :: "+billingDetailsObj.toString());
						}
						
						if (billingDetailsObj.containsKey("policyActivityDate")) {
							policyActivityDate = (String) billingDetailsObj.get("policyActivityDate");
							data.addToLog(currElementName, "Policy Activity Date :: "+policyActivityDate);
							finalObject.put("policyactivitydate", policyActivityDate);
						}
						else{
							data.addToLog(currElementName, "Policy Activity Date Not Available in API Response :: "+billingDetailsObj.toString());
						}
						
						if (billingDetailsObj.containsKey("fullBalanceDue")) {
							JSONObject PolicyBalanceDueObj = (JSONObject) billingDetailsObj.get("fullBalanceDue");
							
							if (null != PolicyBalanceDueObj && PolicyBalanceDueObj.containsKey("theCurrencyAmount")) {
								policyBalanceDue = (String) PolicyBalanceDueObj.get("theCurrencyAmount");
								data.addToLog(currElementName, "Full Balance Due :: "+policyBalanceDue);
								finalObject.put("fullbalancedue", policyBalanceDue);
							}
						}
						else {
							data.addToLog(currElementName, "Full Balance Due Object Not Available in API Response :: "+billingDetailsObj.toString());
						}
						
						if (billingDetailsObj.containsKey("isPolicyInCollectionMode")) {
							isPolicyinCollectionMode = (String) billingDetailsObj.get("isPolicyInCollectionMode");
							data.addToLog(currElementName, "Is Policy In Collection :: "+isPolicyinCollectionMode);
							finalObject.put("ispolicyincollection", isPolicyinCollectionMode);
						}
						else {
							data.addToLog(currElementName, "Is Policy in Collection Mode Indicator Not Available in API Response :: "+billingDetailsObj.toString());
						}
						
						if (billingDetailsObj.containsKey("policyCollectionAmount")) {
							JSONObject CollectionAmountObj = (JSONObject) billingDetailsObj.get("policyCollectionAmount");
							
							if (null != CollectionAmountObj && CollectionAmountObj.containsKey("theCurrencyAmount")) {
								collectionAmount = (String) CollectionAmountObj.get("theCurrencyAmount");
								data.addToLog(currElementName, "Policy Collection Amount :: "+collectionAmount);
								finalObject.put("collectionamount", collectionAmount);
							}
						}
						else {
							data.addToLog(currElementName, "Policy Collection Amount Not Available in API Response :: "+billingDetailsObj.toString());
						}
					}
				}
				else {
					data.addToLog(currElementName, "General Billing Info Response does not contain Billing Details Object :: "+generalBillingInforesp.toString());
				}
				
				
				if (generalBillingInforesp.containsKey("renewalPolicyBillingDetails")) {
					renewalBillingDetailsObj = (JSONObject) generalBillingInforesp.get("renewalPolicyBillingDetails");
					
					if (null != renewalBillingDetailsObj) {
						
						if (renewalBillingDetailsObj.containsKey("nextPaymentDate")) {
							renewalNextDate = (String) renewalBillingDetailsObj.get("nextPaymentDate");
							data.addToLog(currElementName, "Next Renewal Date :: "+renewalNextDate);
							finalObject.put("nextrenewaldate", renewalNextDate);
						}
						else {
							data.addToLog(currElementName, "Next Renewal Date Not Available in API Response :: "+renewalBillingDetailsObj.toString());
						}
						
						if (renewalBillingDetailsObj.containsKey("nextPaymentAmount")) {
							JSONObject renewalPaymentAmountObj = (JSONObject) renewalBillingDetailsObj.get("nextPaymentAmount");
							
							if (null != renewalPaymentAmountObj && renewalPaymentAmountObj.containsKey("theCurrencyAmount")) {
								renewalNextAmount = (String) renewalPaymentAmountObj.get("theCurrencyAmount");
								data.addToLog(currElementName, "Next Renewal Amount :: "+renewalNextAmount);
								finalObject.put("nextrenewalamount", renewalNextAmount);
							}
						}
						else {
							data.addToLog(currElementName, "Next Renewal Amount Not Available in API Response :: "+renewalBillingDetailsObj.toString());
						}
						
						if (renewalBillingDetailsObj.containsKey("lastPaymentAmount")) {
							JSONObject renewalLastAmountObj = (JSONObject) renewalBillingDetailsObj.get("lastPaymentAmount");
							
							if (null != renewalLastAmountObj && renewalLastAmountObj.containsKey("theCurrencyAmount")) {
								renewalLastAmount = (String) renewalLastAmountObj.get("theCurrencyAmount");
								data.addToLog(currElementName, "Last Renewal Amount :: "+renewalLastAmount);
								finalObject.put("lastrenewalamount", renewalLastAmount);
							}
						}
						else {
							data.addToLog(currElementName, "Last Renewal Amount Not Available in API Response :: "+renewalBillingDetailsObj.toString());
						}
						
						if (renewalBillingDetailsObj.containsKey("lastPaymentDate")) {
							renewalLastDate = (String) renewalBillingDetailsObj.get("lastPaymentDate");
							data.addToLog(currElementName, "Last Renewal Date :: "+renewalLastDate);
							finalObject.put("lastrenewaldate", renewalLastDate);
						}
						else {
							data.addToLog(currElementName, "Last Renewal Date Not Available in API Response :: "+renewalBillingDetailsObj.toString());
						}
					}
				}
				else {
					data.addToLog(currElementName, "General Billing info Response does not contain Renewal Billing Details :: "+generalBillingInforesp.toString());
				}
				
				
				if (generalBillingInforesp.containsKey("renewalPolicyDetails")) {
					renewalPolicyDetailsObj = (JSONObject) generalBillingInforesp.get("renewalPolicyDetails");
					
					if (null != renewalPolicyDetailsObj) {
						
						if (renewalPolicyDetailsObj.containsKey("policyEffectiveDate")) {
							renewalEffectiveDate = (String) renewalPolicyDetailsObj.get("policyEffectiveDate");
							data.addToLog(currElementName, "Renewal Effective Date :: "+renewalEffectiveDate);
							finalObject.put("renewaleffectivedate", renewalEffectiveDate);
						}
						else {
							data.addToLog(currElementName, "Renewal Effective Date not Available in API response :: "+renewalPolicyDetailsObj.toString());
						}
						
						if (renewalPolicyDetailsObj.containsKey("policyCancellationDate")) {
							renewalCancellationDate = (String) renewalPolicyDetailsObj.get("policyCancellationDate");
							data.addToLog(currElementName, "Renewal Cancellation Date :: "+renewalCancellationDate);
							finalObject.put("renewalcancellationdate", renewalCancellationDate);
						}
						else {
							data.addToLog(currElementName, "Renewal Cancellation Date not Available in API response :: "+renewalPolicyDetailsObj.toString());
						}
					}
				}
				else {
					data.addToLog(currElementName, "General Billing info Response does not contain Renewal Policy Details :: "+generalBillingInforesp.toString());
				}
			}
			else {
				data.addToLog(currElementName, "General Billing Info Response is null :: "+generalBillingInforesp.toString());
			}
		
			data.addToLog(currElementName, "Final Object with Data from BW Billing General Response :: "+finalObject);
			
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManipulationGeneral Method in BW Billing :: " + currElementName +" :: "  + e);
			caa.printStackTrace(e);
		}
		return finalObject;
	}
	
	public String apiResponseManipulationPayment(DecisionElementData data, String currElementName, CommonAPIAccess caa, JSONObject paymentsBillingInforesp) {
		String webServiceAmount = null;
		JSONObject billingDetailsObj = null;
		try {
			
			if (null != paymentsBillingInforesp && !paymentsBillingInforesp.isEmpty()) {
				
				if (paymentsBillingInforesp.containsKey("billingDetails")) {
					billingDetailsObj = (JSONObject) paymentsBillingInforesp.get("billingDetails");
					
					if (null != billingDetailsObj) {
						
						if (billingDetailsObj.containsKey("minimumBalanceDue")) {
							JSONObject minimumBalanceDueObj = (JSONObject) billingDetailsObj.get("minimumBalanceDue");
							
							if (null != minimumBalanceDueObj && minimumBalanceDueObj.containsKey("theCurrencyAmount")) {
								webServiceAmount = (String) minimumBalanceDueObj.get("theCurrencyAmount");
								data.addToLog(currElementName, "WebService Amount :: "+webServiceAmount);
								return webServiceAmount;
							}
						}
						else {
							data.addToLog(currElementName, "Web Service Amount Not Available in API Response :: "+billingDetailsObj.toString());
						}
					}
					else {
						data.addToLog(currElementName, "Billing Details Object is null :: "+paymentsBillingInforesp.toString());
					}
				}
				else {
					data.addToLog(currElementName, "BW Billing Payments Response does not contain Billing Details Object :: "+paymentsBillingInforesp.toString());
				}
			}
			else {
				data.addToLog(currElementName, "Payment Billing Info Response is null :: "+paymentsBillingInforesp.toString());
			}
		}
		catch(Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManipulationGeneral Method in BW Billing :: " + currElementName +" :: "  + e);
			caa.printStackTrace(e);
		}
		return webServiceAmount;
	}

	
	private String removeNegativeSymbol(CommonAPIAccess caa, DecisionElementData data, String amount) {
		String strAmount = amount;
		try {
			if(amount != null && !amount.isEmpty() && amount.startsWith("-")) {
				strAmount = amount.replace("-", "");
				data.addToLog(data.getCurrentElement(), "Removed Negative in amount "+amount);
			}else {
				data.addToLog(data.getCurrentElement(), "There is no negative in amount "+amount);
			}
			
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in removeNegativeSymbol  :: " + e);
			caa.printStackTrace(e);
		}
		return strAmount;
	}
	
	public String getValuesFromJSONResp(JSONObject response, String location) {
		String finalString = null;
		JSONObject retrieve_JSON = null;
		String retrieve_String = null;
		Integer retrieve_Int = null;
		Double retrieve_Double = null;
		Object retrieve_Object = null;
		Boolean retrieve_Boolean;
		String [] locationSplitter = location.split(":");
		
		try {
			
			for (int i = 0; i < locationSplitter.length; i++) {
				if (null != response) {
					if (response.containsKey(locationSplitter[i])) {
						if (response.get(locationSplitter[i]) instanceof JSONObject) {
							retrieve_JSON = (JSONObject) response.get(locationSplitter[i]);
							response = retrieve_JSON;
						}
						else if (response.get(locationSplitter[i]) instanceof String) {
							retrieve_String = (String) response.get(locationSplitter[i]);
							finalString = retrieve_String;
						}
						else if(response.get(locationSplitter[i]) instanceof Integer) {
							retrieve_Int = (Integer) response.get(locationSplitter[i]);
							finalString = retrieve_Int.toString();
						}
						else if (response.get(locationSplitter[i]) instanceof Double) {
							retrieve_Double = (Double) response.get(locationSplitter[i]);
							finalString = retrieve_Double.toString();
						}
						else if(response.get(locationSplitter[i]) instanceof Boolean) {
							retrieve_Boolean = (Boolean) response.get(locationSplitter[i]);
							finalString = retrieve_Boolean.toString();
						}
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("Exception Occured in getValuesFromJSONResp Method :: " + e);
			e.printStackTrace();
		}
		return finalString;
		
	}
	
	public static void main(String[] args) {
		boolean isSystemDateGreater = false;
		String date = "2024-05-18";
		System.out.println("Original Date :: "+date);
		try {
			Date systemDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date providedDate = sdf.parse(date);
			isSystemDateGreater = systemDate.after(providedDate);
			System.out.println("Is System Date Greater :: " + isSystemDateGreater);
		}
		catch (Exception e) {
		}
	}


}
