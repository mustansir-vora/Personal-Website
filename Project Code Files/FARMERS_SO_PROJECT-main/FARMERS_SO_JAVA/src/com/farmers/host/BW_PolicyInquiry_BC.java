package com.farmers.host;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

public class BW_PolicyInquiry_BC extends DecisionElementBase {

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

			StrExitState = policyEnquiry_BW(strReqBodyGeneral, strRespBodyGeneral, strReqBodyPayment,
					strRespBodyPayment, data, caa, currElementName, StrExitState);

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for " + currElementName + " :: " + e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

	private String policyEnquiry_BW(String strReqBodyGeneral, String strRespBodyGeneral, String strReqBodyPayment,
			String strRespBodyPayment, DecisionElementData data, CommonAPIAccess caa, String currElementName,
			String StrExitState) {
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

			policyNumber = (String) data.getSessionData("S_POLICY_NUM");
			data.addToLog(currElementName, "Policy Number from session data :: " + policyNumber);

			if (null != policyNumber && !policyNumber.isEmpty()) {
				// String regex = "[^0-9]";
				// Pattern pattern = Pattern.compile(regex);
				// Matcher matcher = pattern.matcher(policyNumber);

				/*
				 * if(policyNumber.length()==11) { agreementSymbol =
				 * policyNumber.toUpperCase().substring(0, 3); policyNumber =
				 * policyNumber.toUpperCase().substring(3, policyNumber.length()); }else
				 * if(policyNumber.length()==10) { agreementMode =
				 * policyNumber.substring(policyNumber.length() - 2); policyNumber =
				 * policyNumber.toUpperCase().substring(3, policyNumber.length()); }else
				 * if(policyNumber.length()==13) { agreementSymbol =
				 * policyNumber.toUpperCase().substring(0, 3); agreementMode =
				 * policyNumber.substring(policyNumber.length() - 2); policyNumber =
				 * policyNumber.toUpperCase().substring(3, policyNumber.length()- 2); }
				 */
				
				/*
				 * agreementSymbol = (String) data.getSessionData("POLICY_SYMBOL"); postalCode =
				 * (String) data.getSessionData(Constants.S_API_ZIP);
				 * 
				 * if (postalCode.length() > 5) { postalCode = postalCode.substring(0, 5); }
				 * data.setSessionData("S_PAYOR_ZIP_CODE", postalCode);
				 */
				
				agreementMode = (String) data.getSessionData("POLICY_MOD");
				if (agreementMode == null || agreementMode.equals("")) {
					agreementMode = policyNumber.substring(policyNumber.length() - 2, policyNumber.length());
				}

				boolean startsWithAlphabets = Character.isLetter(policyNumber.charAt(0));
				if (startsWithAlphabets) {
					
					data.addToLog(data.getCurrentElement(), "Policy number starts with alpha");
					if (agreementSymbol == null || agreementSymbol.equals("")) {
						agreementSymbol = policyNumber.substring(0, 3);
					}
					
					if(policyNumber.length()==10) { 
						policyNumber = policyNumber.substring(3, policyNumber.length());
					}else if(policyNumber.length()==12) {
						policyNumber = policyNumber.substring(3, policyNumber.length()-2);
					}
					
					data.setSessionData("POLICY_SYMBOL", agreementSymbol);
					data.addToLog(data.getCurrentElement(), "Agreement Symbol : " + agreementSymbol + " Policy number length : " + policyNumber.length());
					data.addToLog(data.getCurrentElement(), "Policy number post substring from session :: " + policyNumber);
					
					//change for BWPROD ID issue
					data.setSessionData("S_BW_POLICYNUM",policyNumber);
				}else {
					if(policyNumber.startsWith("00")) {
						policyNumber = policyNumber.substring(2, policyNumber.length());
					}
					data.addToLog(data.getCurrentElement(), "Policy number starts with 00 - post substring " + policyNumber);
				}
			}

			data.addToLog(data.getCurrentElement(),
					"URL :: " + data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL)
					+ " :: Call ID :: " + data.getSessionData(Constants.S_CALLID)
					+ " : functionalNameGeneral ::" + Constants.FUNCTION_NAME_GENERAL
					+ " : functionalNamePayments ::" + Constants.FUNCTION_NAME_PAYMENTS
					+ " :: agreement  Symbol :: " + agreementSymbol + " :: agreement Mode :: " + agreementMode
					+ " :: policyNumber :: " + policyNumber + " :: postalCode :: " + postalCode);

			if (null != data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL)
					&& null != data.getSessionData(Constants.S_CALLID) && null != agreementSymbol
					&& null != agreementMode && null != policyNumber && !(Constants.FUNCTION_NAME_PAYMENTS).isEmpty()
					&& !(Constants.FUNCTION_NAME_GENERAL).isEmpty()
					&& null != data.getSessionData(Constants.S_CONN_TIMEOUT)
					&& null != data.getSessionData(Constants.S_READ_TIMEOUT)) {

				String url = (String) data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String functionNamePayments = Constants.FUNCTION_NAME_PAYMENTS;// Constant to be hardcoded
				String functionNameGeneral = Constants.FUNCTION_NAME_GENERAL;// Constant to be hardcoded
				int connTimeout = Integer.parseInt((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.parseInt((String) data.getSessionData(Constants.S_READ_TIMEOUT));

				PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post apiObj = new PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post();
				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH
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
					generalResponse = objNP.start(url, tid, "general", agreementMode,agreementSymbol, policyNumber, postalCode, connTimeout, readTimeout, context, region);
				}else {
					region="PROD";
					generalResponse = apiObj.start(url, tid, "general", agreementMode,agreementSymbol, policyNumber, postalCode, connTimeout, readTimeout, context);
				}
				//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
				data.addToLog(currElementName,
						currElementName
						+ " : PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post API General response  :"
						+ generalResponse);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) generalResponse.get(Constants.RESPONSE_CODE));

				if (generalResponse.containsKey(Constants.RESPONSE_CODE)
						&& (int) generalResponse.get(Constants.RESPONSE_CODE) == 200
						&& generalResponse.containsKey(Constants.RESPONSE_BODY)) {

					strReqBodyGeneral = generalResponse.get(Constants.REQUEST_BODY).toString();
					data.addToLog(currElementName,
							"BW Billing and Payments Request Body General :: " + strReqBodyGeneral);

					data.addToLog(currElementName,
							"PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post API General response into session with the key name of "
									+ currElementName + Constants._RESP);
                   
					strRespBodyGeneral = generalResponse.get(Constants.RESPONSE_BODY).toString();
					data.addToLog(currElementName,
							"BW Billing and Payments Request Body General :: " + strRespBodyGeneral);

					JSONObject objRespBodyGeneral = (JSONObject) generalResponse.get(Constants.RESPONSE_BODY);

					data.setSessionData(currElementName + Constants._RESP,
							generalResponse.get(Constants.RESPONSE_BODY));

					if (objRespBodyGeneral.containsKey(Constants.BILLING_INFO)
							&& null != objRespBodyGeneral.get(Constants.BILLING_INFO)
							&& !((JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO)).isEmpty()) {

						generalBillingInfo = (JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO);

						if ((((JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO)).containsKey("policyDetails"))
								&& null != ((JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO))
								.get("policyDetails")
								&& !((JSONObject) ((JSONObject) objRespBodyGeneral.get(Constants.BILLING_INFO))
										.get("policyDetails")).isEmpty()) {

							JSONObject plDetailsObj = (JSONObject) ((JSONObject) objRespBodyGeneral
									.get(Constants.BILLING_INFO)).get("policyDetails");

							if (plDetailsObj.containsKey("policyZipcode")
									&& null != plDetailsObj.get("policyZipcode")) {

								String plZipCode = (String) plDetailsObj.get("policyZipcode");
								data.addToLog(currElementName,
										"BW Billing and Payments API General response Zip code :: " + plZipCode);
								postalCode = plZipCode.substring(0, 5);
								data.addToLog(currElementName,
										"BW Billing and Payments API General postal code in the response :: "
												+ postalCode);
								data.setSessionData(Constants.S_PAYOR_ZIP_CODE, postalCode);
								data.addToLog(currElementName, "Setting Billing zip code for EPCPAYMENTUS into session :: "+postalCode);
							} else {
								data.addToLog(currElementName,
										"BW Billing and Payments API General response if policy details object key not present or null");
								StrExitState = Constants.ER;
							}
						} else {
							data.addToLog(currElementName,
									"BW Billing and Payments API General response if billing info does not have policy Details field or null");
							StrExitState = Constants.ER;
						}
					} else {
						data.addToLog(currElementName,
								"BW Billing and Payments API General response does not have billing info Object");
					}
					if (currElementName.equalsIgnoreCase("BWPID_MN_001"))
						StrExitState = Constants.SU;
				} else {
					strRespBodyGeneral = generalResponse.get(Constants.RESPONSE_MSG).toString();
					data.addToLog(currElementName,
							"BW Billing and Payments API General response does not have responseBody Object");
					StrExitState = Constants.ER;
				}
				try {
					SetHostDetails objHostDetails1 = new SetHostDetails(caa);
					objHostDetails1.setinitalValue();
					objHostDetails1.startHostReport(currElementName, currElementName, strReqBodyGeneral,region, (String) data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL));
					objHostDetails1.endHostReport(data, strRespBodyGeneral,
							StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
				} catch (Exception e) {
					data.addToLog(currElementName, "Exception while forming host reporting for " + currElementName
							+ "BW Policy Enquiry API General call  :: " + e);
					caa.printStackTrace(e);
				}

				if (null != data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_PAYMENT_URL) && null != postalCode
						&& !postalCode.isEmpty()) {

					paymentResponse = (JSONObject) apiObj.start(url, tid, "payment", agreementMode,
							agreementSymbol, policyNumber, postalCode, connTimeout, readTimeout, context);
					data.addToLog(currElementName, currElementName
							+ " : PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post API Payments response  :"
							+ paymentResponse);
				}
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) paymentResponse.get(Constants.RESPONSE_CODE));

				if (paymentResponse.containsKey(Constants.RESPONSE_CODE)
						&& (int) paymentResponse.get(Constants.RESPONSE_CODE) == 200
						&& paymentResponse.containsKey(Constants.RESPONSE_BODY)) {

					strReqBodyPayment = paymentResponse.get(Constants.REQUEST_BODY).toString();
					data.addToLog(currElementName,
							"BW Billing and Payments Request Body Payment :: " + strReqBodyPayment);

					data.addToLog(currElementName,
							"PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post API Payment response into session with the key name of "
									+ currElementName + Constants._RESP);

					strRespBodyPayment = paymentResponse.get(Constants.RESPONSE_BODY).toString();
					data.addToLog(currElementName,
							"BW Billing and Payments Request Body General :: " + strRespBodyPayment);

					JSONObject objRespBodyPayment = (JSONObject) generalResponse.get(Constants.RESPONSE_BODY);

					data.setSessionData(currElementName + Constants._RESP,
							generalResponse.get(Constants.RESPONSE_BODY));

					if (objRespBodyPayment.containsKey(Constants.BILLING_INFO)
							&& null != objRespBodyPayment.get(Constants.BILLING_INFO)
							&& !((JSONObject) objRespBodyPayment.get(Constants.BILLING_INFO)).isEmpty()) {
						paymentsBillingInfo = (JSONObject) objRespBodyPayment.get(Constants.BILLING_INFO);

					} else {
						data.addToLog(currElementName,
								"BW Billing and Payments API Payments response does not have billing info Object");
					}
					if (currElementName.equalsIgnoreCase("BWPID_MN_001"))
						StrExitState = Constants.SU;
				} else {
					strRespBodyPayment = generalResponse.get(Constants.RESPONSE_MSG).toString();
					data.addToLog(currElementName,
							"BW Billing and Payments API Payment response does not have responseBody Object");
				}

				try {
					SetHostDetails objHostDetails = new SetHostDetails(caa);
					objHostDetails.setinitalValue();
					objHostDetails.startHostReport(currElementName, currElementName, strReqBodyPayment,region, (String) data.getSessionData(Constants.S_BW_BILLING_POLICY_INQUIRY_PAYMENT_URL));
					objHostDetails.endHostReport(data, strRespBodyPayment,
							StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
				} catch (Exception e) {
					data.addToLog(currElementName, "Exception while forming host reporting for " + currElementName
							+ "BW Policy Enquiry API Payment call  :: " + e);
					caa.printStackTrace(e);
				}

				if ((!generalBillingInfo.isEmpty() || !paymentsBillingInfo.isEmpty()) && null != policyNumber
						&& !policyNumber.isEmpty()) {
					if (currElementName.equalsIgnoreCase("BWBP_HOST_001")) {
						checkCaseList(generalBillingInfo, paymentsBillingInfo, policyNumber, StrExitState, data,
								currElementName,caa);
					}
					StrExitState = Constants.SU;
				} else {
					data.addToLog(currElementName, "BW Billings and Payments API error if response objects are empty");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in " + currElementName
					+ "PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post API call  :: " + e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}

	public void checkCaseList(JSONObject generalBillingInfo, JSONObject paymentsBillingInfo, String policyNumber,
			String strExitState, DecisionElementData data, String currElementName, CommonAPIAccess caa) {

		String caseList = Constants.EmptyString; 
		String policyStatus = Constants.EmptyString;
		String policyActivityDate = Constants.EmptyString;
		String renewalPending = Constants.EmptyString;
		String invoiceGenerated = Constants.EmptyString;
		String nextPaymentDate = Constants.EmptyString;
		String renewalInvoiceDate = Constants.EmptyString;
		String policyCancellationDate = Constants.EmptyString;
		String policyEffectiveDate = Constants.EmptyString;
		String NSFState = Constants.EmptyString;
		String renewalEffectiveDate = Constants.EmptyString;
		String renewalPayment = Constants.EmptyString;
		String renewalPaymentAmount = Constants.EmptyString;
		String renewalAmount = Constants.EmptyString;
		String nsfFee = Constants.EmptyString;
		String renewalDate = Constants.EmptyString;
		String policyEndingNum = Constants.EmptyString;
		int relapsePeriod = 0;
		String renewalCancelDate = Constants.EmptyString;
		String policyCollectionMode = Constants.EmptyString;
		String qualifiedForReinstateWithLapse = Constants.EmptyString;
		String qualifiedForRewrittenWithLapse = Constants.EmptyString;
		String policyRewriteOfferAva = Constants.EmptyString;
		String rewriteOfferExpiryDate = Constants.EmptyString;
		String collectionAmount = Constants.EmptyString;
		double policyBalance = 0;
		String nextPaymentAmount = Constants.EmptyString;
		double webserviceAmount = 0;
		String renewalInvoiceAmount = Constants.EmptyString;
		String lastPaymentAmount = Constants.EmptyString;
		String plActivityDtMinus_1 = Constants.EmptyString;
		String policyCancelDtPlus_9 = Constants.EmptyString;
		String plCancelDtMinus_1 = Constants.EmptyString;
		LocalDate reEffDtPlusRelapseMinus_1 = null;
		String reInvoiceDtMinus_1 = Constants.EmptyString;
		LocalDate reEffDtPlusRelapsePeriod = null;
		String plEffDtMinus_1 = Constants.EmptyString;
		LocalDate rewriteOfferExpiryDtTemp = null;
		int cancelledDays = 0;
		boolean currDtGNReOffExpDt = false;
		String reCancelDtPlus_44 = Constants.EmptyString;
		String reCancelDtMinus_1 = Constants.EmptyString;
		String reCancelDtPlusRelapseMinus_1 = Constants.EmptyString;
		LocalDate reCancelDateTemp = null;
		LocalDate reCancelDtPlusRelapsePeriod = null;
		LocalDate reEffectiveDateTemp = null;
		String rePaymentAmtPlusNSFfee = Constants.EmptyString;
		String lastPaymentAmtPlusNSFfee = Constants.EmptyString;
		double policyBalanceTemp = 0;

		try {
			String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			JSONObject genBillingDetails = new JSONObject();
			JSONObject genPolicyDetails = new JSONObject();
			JSONObject genRePolicyBillingDetails = new JSONObject();
			JSONObject genRePolicyDetails = new JSONObject();
			JSONObject payBillingDetails = new JSONObject();

			if (!generalBillingInfo.isEmpty()) {

				data.addToLog(currElementName + "Billing and Payments General response Billing Info :: ",
						generalBillingInfo.toString());

				if (generalBillingInfo.containsKey("billingDetails")
						&& null != generalBillingInfo.get("billingDetails")) {
					genBillingDetails = (JSONObject) generalBillingInfo.get("billingDetails");
					data.addToLog(currElementName + "Billing object in General response :: ",
							genBillingDetails.toString());
				}
				// JSONObject genInsuredDetails = (JSONObject)
				// generalBillingInfo.get("insuredDetails");
				if (generalBillingInfo.containsKey("policyDetails")
						&& null != generalBillingInfo.get("policyDetails")) {
					genPolicyDetails = (JSONObject) generalBillingInfo.get("policyDetails");
					data.addToLog(currElementName + "Policy object in General response :: ",
							genPolicyDetails.toString());
				}
				if (generalBillingInfo.containsKey("renewalPolicyBillingDetails")
						&& null != generalBillingInfo.get("renewalPolicyBillingDetails")) {
					genRePolicyBillingDetails = (JSONObject) generalBillingInfo.get("renewalPolicyBillingDetails");
					data.addToLog(currElementName + "RenewalPolicyBillingDetails object in General response :: ",
							genRePolicyBillingDetails.toString());
				}
				if (generalBillingInfo.containsKey("renewalPolicyDetails")
						&& null != generalBillingInfo.get("renewalPolicyDetails")) {
					genRePolicyDetails = (JSONObject) generalBillingInfo.get("renewalPolicyDetails");
					data.addToLog(currElementName + "RenewalPolicyDetails object in General response :: ",
							genRePolicyDetails.toString());
				}
				if (genPolicyDetails.containsKey("policyStatus") && null != genPolicyDetails.get("policyStatus")) {
					policyStatus = (String) genPolicyDetails.get("policyStatus");
					data.addToLog(currElementName, "policy status :: " + policyStatus);
				}
				if (genBillingDetails.containsKey("policyActivityDate")
						&& null != genBillingDetails.get("policyActivityDate")) {
					policyActivityDate = genBillingDetails.get("policyActivityDate").toString();
					data.addToLog(currElementName, "policyActivityDate :: " + policyActivityDate);
				}
				if (genPolicyDetails.containsKey("isRenewalPendingIndicatorAsString")
						&& null != genPolicyDetails.get("isRenewalPendingIndicatorAsString")) {
					renewalPending = genPolicyDetails.get("isRenewalPendingIndicatorAsString").toString();
					data.addToLog(currElementName, "renewalPending :: " + renewalPending);
				}
				if (genBillingDetails.containsKey("fullBalanceDue")
						&& null != genBillingDetails.get("fullBalanceDue")) {
					JSONObject fullBalanceDue = (JSONObject) genBillingDetails.get("fullBalanceDue");
					if (fullBalanceDue.containsKey("theCurrencyAmount")
							&& null != fullBalanceDue.get("theCurrencyAmount")) {
						policyBalance = Double.parseDouble(((JSONObject) genBillingDetails.get("fullBalanceDue"))
								.get("theCurrencyAmount").toString());
						data.addToLog(currElementName, "policyBalance :: " + policyBalance);
					}
				}

				if (genPolicyDetails.containsKey("nextPaymentDate")
						&& null != genPolicyDetails.get("nextPaymentDate")) {
					invoiceGenerated = genPolicyDetails.get("nextPaymentDate").toString();
					data.addToLog(currElementName, "invoiceGenerated :: " + invoiceGenerated);
				}
				if (genBillingDetails.containsKey("nextPaymentDate")
						&& null != genBillingDetails.get("nextPaymentDate")) {
					nextPaymentDate = genBillingDetails.get("nextPaymentDate").toString();
					data.addToLog(currElementName, "nextPaymentDate :: " + nextPaymentDate);
				}
				if (genRePolicyBillingDetails.containsKey("nextPaymentDate")
						&& null != genRePolicyBillingDetails.get("nextPaymentDate")) {
					renewalInvoiceDate = genRePolicyBillingDetails.get("nextPaymentDate").toString();
					data.addToLog(currElementName, "renewalInvoiceDate :: " + renewalInvoiceDate);
				}
				if (genPolicyDetails.containsKey("policyEffectiveDate")
						&& null != genPolicyDetails.get("policyEffectiveDate")) {
					policyEffectiveDate = genPolicyDetails.get("policyEffectiveDate").toString();
					data.addToLog(currElementName, "policyEffectiveDate :: " + policyEffectiveDate);
				}
				if (genPolicyDetails.containsKey("notSufficientFundIndicatorAsString")
						&& null != genPolicyDetails.get("notSufficientFundIndicatorAsString")) {
					NSFState = genPolicyDetails.get("notSufficientFundIndicatorAsString").toString();
					data.addToLog(currElementName, "NSFState :: " + NSFState);
				}
				if (genRePolicyDetails.containsKey("policyEffectiveDate")
						&& null != genRePolicyDetails.get("policyEffectiveDate")) {
					renewalEffectiveDate = genRePolicyDetails.get("policyEffectiveDate").toString();
					data.addToLog(currElementName, "renewalEffectiveDate :: " + renewalEffectiveDate);
				}

				if (genRePolicyBillingDetails.containsKey("lastPaymentAmount")
						&& null != genRePolicyBillingDetails.get("lastPaymentAmount")) {
					JSONObject lastPaymentAmountObj = (JSONObject) genRePolicyBillingDetails.get("lastPaymentAmount");
					if (lastPaymentAmountObj.containsKey("theCurrencyAmount")
							&& null != lastPaymentAmountObj.get("theCurrencyAmount")) {
						renewalPayment = ((JSONObject) genRePolicyBillingDetails.get("lastPaymentAmount"))
								.get("theCurrencyAmount").toString();
						data.addToLog(currElementName, "renewalPayment :: " + renewalPayment);
					}
				}
				if (genRePolicyBillingDetails.containsKey("nextPaymentAmount")
						&& null != genRePolicyBillingDetails.get("nextPaymentAmount")) {
					JSONObject nextPaymentAmountObj = (JSONObject) genRePolicyBillingDetails.get("nextPaymentAmount");
					if (nextPaymentAmountObj.containsKey("theCurrencyAmount")
							&& null != nextPaymentAmountObj.get("theCurrencyAmount")) {
						renewalPaymentAmount = ((JSONObject) genRePolicyBillingDetails.get("nextPaymentAmount"))
								.get("theCurrencyAmount").toString();
						data.addToLog(currElementName, "renewalPaymentAmount :: " + renewalPaymentAmount);
					}
				}
				if (genRePolicyBillingDetails.containsKey("nextPaymentDate")
						&& null != genRePolicyBillingDetails.get("nextPaymentDate")) {
					renewalDate = genRePolicyBillingDetails.get("nextPaymentDate").toString();
					data.addToLog(currElementName, "renewalDate :: " + renewalDate);
				}
				if (genBillingDetails.containsKey("fullBalanceDue")
						&& null != genBillingDetails.get("fullBalanceDue")) {
					JSONObject fullBalanceDueObj = (JSONObject) genBillingDetails.get("fullBalanceDue");
					if (fullBalanceDueObj.containsKey("theCurrencyAmount")
							&& null != fullBalanceDueObj.get("theCurrencyAmount")) {
						renewalAmount = ((JSONObject) genBillingDetails.get("fullBalanceDue")).get("theCurrencyAmount")
								.toString();
						data.addToLog(currElementName, "renewalAmount :: " + renewalAmount);
					}
				}
				if (genRePolicyDetails.containsKey("lapsePeriodForPolicyRenewal")
						&& null != genRePolicyDetails.get("lapsePeriodForPolicyRenewal")) {
					relapsePeriod = Integer.parseInt(genRePolicyDetails.get("lapsePeriodForPolicyRenewal").toString());
					data.addToLog(currElementName, "relapsePeriod :: " + relapsePeriod);
				}
				if (genRePolicyDetails.containsKey("policyCancellationDate")
						&& null != genRePolicyDetails.get("policyCancellationDate")) {
					renewalCancelDate = genRePolicyDetails.get("policyCancellationDate").toString();
					data.addToLog(currElementName, "renewalCancelDate :: " + renewalCancelDate);
				}
				if (genBillingDetails.containsKey("isPolicyInCollectionMode")
						&& null != genBillingDetails.get("isPolicyInCollectionMode")) {
					policyCollectionMode = genBillingDetails.get("isPolicyInCollectionMode").toString();
					data.addToLog(currElementName, "policyCollectionMode :: " + policyCollectionMode);
				}
				if (genPolicyDetails.containsKey("isPolicyQualifiedForReinstateWithLapse")
						&& null != genPolicyDetails.get("isPolicyQualifiedForReinstateWithLapse")) {
					qualifiedForReinstateWithLapse = genPolicyDetails.get("isPolicyQualifiedForReinstateWithLapse")
							.toString();
					data.addToLog(currElementName,
							"qualifiedForReinstateWithLapse :: " + qualifiedForReinstateWithLapse);
				}
				if (genPolicyDetails.containsKey("isPolicyRewriteOfferAvailable")
						&& null != genPolicyDetails.get("isPolicyRewriteOfferAvailable")) {
					policyRewriteOfferAva = genPolicyDetails.get("isPolicyRewriteOfferAvailable").toString();
					data.addToLog(currElementName, "policyRewriteOfferAva :: " + policyRewriteOfferAva);
				}
				if (genPolicyDetails.containsKey("policyRewriteOfferExpiryDate")
						&& null != genPolicyDetails.get("policyRewriteOfferExpiryDate")) {
					rewriteOfferExpiryDate = genPolicyDetails.get("policyRewriteOfferExpiryDate").toString();
					data.addToLog(currElementName, "rewriteOfferExpiryDate :: " + rewriteOfferExpiryDate);
				}
				if (genPolicyDetails.containsKey("policyCancellationDate")
						&& null != genPolicyDetails.get("policyCancellationDate")) {
					policyCancellationDate = genPolicyDetails.get("policyCancellationDate").toString();
					data.addToLog(currElementName, "policyCancellationDate :: " + policyCancellationDate);
				}

				if (genRePolicyBillingDetails.containsKey("nextPaymentAmount")
						&& null != genRePolicyBillingDetails.get("nextPaymentAmount")) {
					JSONObject nextPaymentAmountObj = (JSONObject) genRePolicyBillingDetails.get("nextPaymentAmount");
					if (nextPaymentAmountObj.containsKey("theCurrencyAmount")
							&& null != nextPaymentAmountObj.get("theCurrencyAmount")) {
						renewalInvoiceAmount = ((JSONObject) genRePolicyBillingDetails.get("nextPaymentAmount"))
								.get("theCurrencyAmount").toString();
						data.addToLog(currElementName, "renewalInvoiceAmount :: " + renewalInvoiceAmount);
					}
				}
				if (genPolicyDetails.containsKey("notSufficientFundAmount")
						&& null != genPolicyDetails.get("notSufficientFundAmount")) {
					JSONObject notSufficientFundAmountObj = (JSONObject) genPolicyDetails
							.get("notSufficientFundAmount");
					if (notSufficientFundAmountObj.containsKey("theCurrencyAmount")
							&& null != notSufficientFundAmountObj.get("theCurrencyAmount")) {
						nsfFee = ((JSONObject) genPolicyDetails.get("notSufficientFundAmount")).get("theCurrencyAmount")
								.toString();
						data.addToLog(currElementName, "nsfFee :: " + nsfFee);
					}
				}

				if (genBillingDetails.containsKey("policyCollectionAmount")
						&& null != genBillingDetails.get("policyCollectionAmount")) {
					JSONObject policyCollectionAmountObj = (JSONObject) genBillingDetails.get("policyCollectionAmount");
					if (policyCollectionAmountObj.containsKey("theCurrencyAmount")
							&& null != policyCollectionAmountObj.get("theCurrencyAmount")) {
						collectionAmount = ((JSONObject) genBillingDetails.get("policyCollectionAmount"))
								.get("theCurrencyAmount").toString();
						data.addToLog(currElementName, "collectionAmount :: " + collectionAmount);
					}
				}
				if (null != policyNumber && !policyNumber.isEmpty()) {
					policyEndingNum = policyNumber.substring(policyNumber.length() - 4);
					data.addToLog(currElementName, "policyEndingNum :: " + policyEndingNum);
				}
				if (genBillingDetails.containsKey("lastPaymentAmount")
						&& null != genBillingDetails.get("lastPaymentAmount")) {
					JSONObject lastPaymentAmountObj = (JSONObject) genBillingDetails.get("lastPaymentAmount");
					if (lastPaymentAmountObj.containsKey("theCurrencyAmount")
							&& null != lastPaymentAmountObj.get("theCurrencyAmount")) {
						lastPaymentAmount = ((JSONObject) genBillingDetails.get("lastPaymentAmount"))
								.get("theCurrencyAmount").toString();
						data.addToLog(currElementName, "lastPaymentAmount :: " + lastPaymentAmount);
					}
				}
				if (genBillingDetails.containsKey("nextPaymentAmount")
						&& null != genBillingDetails.get("nextPaymentAmount")) {
					JSONObject nextPaymentAmountObj = (JSONObject) genBillingDetails.get("nextPaymentAmount");
					if (nextPaymentAmountObj.containsKey("theCurrencyAmount")
							&& null != nextPaymentAmountObj.get("theCurrencyAmount")) {
						nextPaymentAmount = ((JSONObject) genBillingDetails.get("nextPaymentAmount"))
								.get("theCurrencyAmount").toString();
						data.addToLog(currElementName, "nextPaymentAmount :: " + nextPaymentAmount);
						nextPaymentAmount = removeNegativeSymbol(caa, data, nextPaymentAmount);
								
					}
				}
			}

			if (!paymentsBillingInfo.isEmpty()) {

				data.addToLog(currElementName + "Billing and Payments Payment response Billing Info :: ",
						paymentsBillingInfo.toString());

				payBillingDetails = (JSONObject) paymentsBillingInfo.get("billingDetails");
				data.addToLog(currElementName + "Billing object in payment response :: ", payBillingDetails.toString());
				// JSONObject payInsuredDetails = (JSONObject)
				// paymentsBillingInfo.get("insuredDetails");
				JSONObject payPolicyDetails = (JSONObject) paymentsBillingInfo.get("policyDetails");
				data.addToLog(currElementName + "Billing object in payment response :: ", payPolicyDetails.toString());

				if (payBillingDetails.containsKey("minimumBalanceDue")
						&& null != payBillingDetails.get("minimumBalanceDue")) {
					JSONObject minimumBalanceDueObj = (JSONObject) payBillingDetails.get("minimumBalanceDue");
					if (minimumBalanceDueObj.containsKey("theCurrencyAmount")
							&& null != minimumBalanceDueObj.get("theCurrencyAmount")) {
						webserviceAmount = Double.parseDouble(((JSONObject) payBillingDetails.get("minimumBalanceDue"))
								.get("theCurrencyAmount").toString());
						data.addToLog(currElementName, "webserviceAmount :: " + webserviceAmount);
					}
				}
			}

			LocalDate currDateTemp = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			data.addToLog(currElementName, "Curent date in BW Billings and payments API :: " + currentDate);

			if (null != renewalEffectiveDate && !renewalEffectiveDate.isEmpty()) {
				reEffectiveDateTemp = LocalDate.parse(renewalEffectiveDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName, "reEffectiveDateTemp :: " + reEffectiveDateTemp);

				reEffDtPlusRelapseMinus_1 = (reEffectiveDateTemp.plusDays(relapsePeriod)).minusDays(1);
				data.addToLog(currElementName,
						"BW Billings and Payments API renewal effective date plus relapse period :: "
								+ reEffDtPlusRelapseMinus_1);

			}
			if (null != renewalInvoiceDate && !renewalInvoiceDate.isEmpty()) {
				LocalDate reInvoiceDateTemp = LocalDate.parse(renewalInvoiceDate,
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName, "reInvoiceDateTemp :: " + reInvoiceDateTemp);

				reInvoiceDtMinus_1 = (reInvoiceDateTemp.minusDays(1)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName,
						"BW Billings and Payments API renewal Invoice date Minus 1 day :: " + reInvoiceDtMinus_1);
			}
			if (null != renewalCancelDate && !renewalCancelDate.isEmpty()) {
				reCancelDateTemp = LocalDate.parse(renewalCancelDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName, "reCancelDateTemp :: " + reCancelDateTemp);

				reCancelDtMinus_1 = (reCancelDateTemp.minusDays(1)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName,
						"BW Billings and Payments API renewal cancel date minus 1 day :: " + reCancelDtMinus_1);

				reCancelDtPlus_44 = (reCancelDateTemp.plusDays(44)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName,
						"BW Billings and Payments API renewal cancel date plus 44 days :: " + reCancelDtPlus_44);

				String reCancelDtPlus_9 = (reCancelDateTemp.plusDays(9))
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName,
						"BW Billings and Payments API renewal cancel date plus 9 days :: " + reCancelDtPlus_9);

				reCancelDtPlusRelapsePeriod = reCancelDateTemp.plusDays(relapsePeriod).minusDays(1);
				data.addToLog(currElementName,
						"BW Billings and Payments API renewal cancel date plus relapse period :: "
								+ reCancelDtPlusRelapsePeriod);

				reCancelDtPlusRelapseMinus_1 = reCancelDtPlusRelapsePeriod
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName,
						"BW Billings and Payments API renewal cancel date plus relapse period minus 1 :: "
								+ reCancelDtPlusRelapseMinus_1);

			}
			if (null != rewriteOfferExpiryDate && !rewriteOfferExpiryDate.isEmpty()) {
				rewriteOfferExpiryDtTemp = LocalDate.parse(rewriteOfferExpiryDate,
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName, "rewriteOfferExpiryDtTemp :: " + rewriteOfferExpiryDtTemp);

				currDtGNReOffExpDt = currDateTemp.isAfter(rewriteOfferExpiryDtTemp);
				data.addToLog(currElementName,
						"BW Billings and Payments API current date greater than renewal expiry date :: "
								+ currDtGNReOffExpDt);
			}
			if (null != policyCancellationDate && !policyCancellationDate.isEmpty()) {
				LocalDate policyCancelDateTemp = LocalDate.parse(policyCancellationDate,
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName, "policyCancelDateTemp :: " + policyCancelDateTemp);

				policyCancelDtPlus_9 = (policyCancelDateTemp.plusDays(9))
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName,
						"BW Billings and Payments API Policy Cancel date plus 9 days :: " + policyCancelDtPlus_9);

				plCancelDtMinus_1 = (policyCancelDateTemp.minusDays(1))
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName,
						"BW Billings and Payments API policy cancel date minus 1 day :: " + plCancelDtMinus_1);

				cancelledDays = policyCancelDateTemp.until(currDateTemp).getDays();
				data.addToLog(currElementName,
						"BW Billings and Payments API cancelled Days in integer format" + cancelledDays);
			}
			if (null != policyActivityDate && !policyActivityDate.isEmpty()) {
				LocalDate policyActivityDateTemp = LocalDate.parse(policyActivityDate,
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName, "policyActivityDateTemp :: " + policyActivityDateTemp);

				plActivityDtMinus_1 = (policyActivityDateTemp.minusDays(1))
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName,
						"BW Billings and Payments API policy activity date minus 1 day :: " + plActivityDtMinus_1);
			}
			if (null != policyEffectiveDate && !policyEffectiveDate.isEmpty()) {
				LocalDate policyEffDateTemp = LocalDate.parse(policyEffectiveDate,
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName, "policyEffDateTemp :: " + policyEffDateTemp);

				plEffDtMinus_1 = (policyEffDateTemp.minusDays(1)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				data.addToLog(currElementName,
						"BW Billings and Payments API policy effective date minus 1 day :: " + plEffDtMinus_1);
			}

			if (null != renewalPaymentAmount && !renewalPaymentAmount.isEmpty() && null != nsfFee
					&& !nsfFee.isEmpty()) {
				double renewalPaymentAmountTemp = Double.parseDouble(renewalPaymentAmount);
				double nsfFeeTemp = Double.parseDouble(nsfFee);
				if (renewalPaymentAmountTemp > 0 && nsfFeeTemp > 0) {
					rePaymentAmtPlusNSFfee = String.valueOf(renewalPaymentAmountTemp + nsfFeeTemp);
					data.addToLog(currElementName,
							"BW Billings and Payments API renewal payment amount plus NSF fee :: "
									+ rePaymentAmtPlusNSFfee);
				}
			}
			if (null != renewalPaymentAmount && !renewalPaymentAmount.isEmpty() && null != nsfFee
					&& !nsfFee.isEmpty()) {
				double lastPaymentAmountTemp = Double.parseDouble(renewalPaymentAmount);
				double nsfFeeTemp = Double.parseDouble(nsfFee);
				if (lastPaymentAmountTemp > 0 && nsfFeeTemp > 0) {
					lastPaymentAmtPlusNSFfee = String.valueOf(lastPaymentAmountTemp + nsfFeeTemp);
					data.addToLog(currElementName, "BW Billings and Payments API last payment amount plus NSF fee :: "
							+ lastPaymentAmtPlusNSFfee);
				}
			}

			if (!generalBillingInfo.isEmpty() || !paymentsBillingInfo.isEmpty()) {

				if((policyStatus.equalsIgnoreCase("ACT") || policyStatus.startsWith("ACT") || policyStatus.equalsIgnoreCase("PEN")) && 
						(!nextPaymentAmount.isEmpty() && !policyActivityDate.isEmpty()) ||
						((policyActivityDate == null ||policyActivityDate.isEmpty()) && !nextPaymentAmount.isEmpty() && !policyCancellationDate.isEmpty()) || 
						(renewalPending.equalsIgnoreCase(Constants.Y_FLAG) && policyBalance > 0 && !renewalInvoiceDate.isEmpty() && !renewalInvoiceAmount.isEmpty()) || 
						((renewalPending == null || renewalPending.equalsIgnoreCase(Constants.N_FLAG)) && policyBalance <= 0) || 
						((renewalPending == null || renewalPending.equalsIgnoreCase(Constants.N_FLAG)) && policyBalance > 0 && !invoiceGenerated.isEmpty()) ||
						((renewalPending == null || renewalPending.equalsIgnoreCase(Constants.N_FLAG)) && policyBalance > 0 && invoiceGenerated.isEmpty() && webserviceAmount <= 0) ||
						((renewalPending == null || renewalPending.equalsIgnoreCase(Constants.N_FLAG)) && policyBalance > 0 && invoiceGenerated.isEmpty() && webserviceAmount > 0 && !nextPaymentAmount.isEmpty() && !nextPaymentDate.isEmpty()) 
						) {

					data.addToLog(currElementName, "Customer belongs to CaseList 1 ::");

					plActivityDtMinus_1 = plActivityDtMinus_1.replace("-", "");
					data.setSessionData("S_PL_ACTIVITY_DT_MINUS_1", plActivityDtMinus_1);
					data.addToLog(currElementName, "Case List 1 Pliocy Activity date minus 1 day :: "
							+ (String) data.getSessionData("S_PL_ACTIVITY_DT_MINUS_1"));

					data.setSessionData("S_NEXT_PAYMENT_AMOUNT", nextPaymentAmount);
					data.addToLog(currentDate, "Case List 1 Next Payment amount :: "
							+ (String) data.getSessionData("S_NEXT_PAYMENT_AMOUNT"));

					plCancelDtMinus_1 = plCancelDtMinus_1.replace("-", "");
					data.setSessionData("S_PL_CANCELLATION_DT_MINUS_1", plCancelDtMinus_1);
					data.addToLog(currentDate, "Case List 1 Policy Cancellation date minus 1 day :: "
							+ (String) data.getSessionData("S_PL_CANCELLATION_DT_MINUS_1"));

					nextPaymentDate = nextPaymentDate.replace("-", "");
					data.setSessionData("S_NEXT_PAYMENT_DATE", nextPaymentDate);
					data.addToLog(currentDate,
							"Case List 1 Next pament date :: " + (String) data.getSessionData("S_NEXT_PAYMENT_DATE"));

					policyCancelDtPlus_9 = policyCancelDtPlus_9.replace("-", "");
					data.setSessionData("S_PL_CANCEL_DT_PLUS_9", policyCancelDtPlus_9);
					data.addToLog(currElementName, "Case List 1 Policy cancellation date plus 9 days :: "
							+ (String) data.getSessionData("S_PL_CANCEL_DT_PLUS_9"));

					data.setSessionData("S_POLICY_STATUS", policyStatus);
					data.addToLog(currElementName,
							"Case List 1 Policy Status :: " + (String) data.getSessionData("S_POLICY_STATUS"));

					data.setSessionData("S_NEXT_PAYMENT_AMOUNT_EXISTS",
							!nextPaymentAmount.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 Is Next Payment Amount exists :: "
							+ (String) data.getSessionData("S_NEXT_PAYMENT_AMOUNT_EXISTS"));

					data.setSessionData("S_POLICY_ACTIVITY_DATE_EXISTS",
							!policyActivityDate.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 Is Policy Activity Date Exits :: "
							+ (String) data.getSessionData("S_POLICY_ACTIVITY_DATE_EXISTS"));

					data.setSessionData("S_POLICY_CANCELLATION_DATE_EXISTS",
							!policyCancellationDate.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 Is Policy Cancellation Date Exits :: "
							+ (String) data.getSessionData("S_POLICY_CANCELLATION_DATE_EXISTS"));

					data.setSessionData("S_RENEWAL_PENDING", renewalPending);
					data.addToLog(currElementName,
							"Case List 1 Renewal Pending :: " + (String) data.getSessionData("S_RENEWAL_PENDING"));

					// Need to change
					data.setSessionData("S_POLICY_BALANCE_GR_THAN_ZERO", policyBalance > 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 S_POLICY_BALANCE_GR_THAN_ZERO :: "
							+ (String) data.getSessionData("S_POLICY_BALANCE_GR_THAN_ZERO"));

					data.setSessionData("S_POLICY_BALANCE_LESS_THAN_EQUAL_ZERO", policyBalance <= 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 S_POLICY_BALANCE_LESS_THAN_EQUAL_ZERO :: "
							+ (String) data.getSessionData("S_POLICY_BALANCE_LESS_THAN_EQUAL_ZERO"));

					data.setSessionData("S_INVOICE_GENERATED", !invoiceGenerated.isEmpty() ? "Yes" : "No");
					data.addToLog(currElementName,
							"Case List 1 Invoice generated :: " + (String) data.getSessionData("S_INVOICE_GENERATED"));

					data.setSessionData("S_WEBSERVICE_AMOUNT_GR_THAN_ZERO", webserviceAmount > 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 S_WEBSERVICE_AMOUNT_GR_THAN_ZERO :: "
							+ (String) data.getSessionData("S_WEBSERVICE_AMOUNT_GR_THAN_ZERO"));

					data.setSessionData("S_WEBSERVICE_LESS_THAN_EQUAL_ZERO", webserviceAmount <= 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 S_WEBSERVICE_LESS_THAN_EQUAL_ZERO :: "
							+ (String) data.getSessionData("S_WEBSERVICE_LESS_THAN_EQUAL_ZERO"));

					data.setSessionData("S_NEXT_PAYMENT_DATE_EXISTS", !nextPaymentDate.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 Is Next Payment Date exists :: "
							+ (String) data.getSessionData("S_NEXT_PAYMENT_DATE_EXISTS"));

					data.setSessionData("S_RENEWAL_INVOICE_DATE_EXISTS",
							!renewalInvoiceDate.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 Is Renewal Invoice date exists :: "
							+ (String) data.getSessionData("S_RENEWAL_INVOICE_DATE_EXISTS"));

					data.setSessionData("S_RENEWAL_INVOICE_AMOUNT_EXISTS",
							!renewalInvoiceAmount.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 Is Renewal Invoice Amount exists :: "
							+ (String) data.getSessionData("S_RENEWAL_INVOICE_AMOUNT_EXISTS"));

					data.setSessionData("CASE_BELONGS_TO", "CL_1");
					data.addToLog(currElementName,
							"CASE_BELONGS_TO Session data value :: " + (String) data.getSessionData("CASE_BELONGS_TO"));
					// strExitState = "CL_1";

				}

				else if ((!plEffDtMinus_1.isEmpty()) || (plEffDtMinus_1.isEmpty() && !NSFState.isEmpty() && NSFState.equals("N") &&  policyBalance < 0)
						|| (plEffDtMinus_1.isEmpty() && !NSFState.isEmpty() && NSFState.equals("N") &&  policyBalance < 0)
						|| (plEffDtMinus_1.isEmpty() && !NSFState.isEmpty() && NSFState.equals("N") &&  policyBalance == 0 
								&& null != reEffectiveDateTemp && currDateTemp.isBefore(reEffectiveDateTemp)
								&& !renewalPaymentAmount.isEmpty() && !renewalDate.isEmpty())
						|| (plEffDtMinus_1.isEmpty() && !NSFState.isEmpty() && NSFState.equals("N") &&  policyBalance == 0 
								&& null != reEffectiveDateTemp && currDateTemp.isBefore(reEffectiveDateTemp)
								&& renewalPaymentAmount.isEmpty() && renewalDate.isEmpty())) {

					data.addToLog(currElementName, "Customer belongs to CaseList 2 ::");

					// data.setSessionData("S_POLICY_EFFECTIVE_DATE", policyEffectiveDate);
					plEffDtMinus_1 = plEffDtMinus_1.replace("-", "");
					data.setSessionData("S_PL_EFF_DT_MINUS_1", plEffDtMinus_1);
					data.addToLog(currElementName, "Case List 2 Policy effective date minus 1 day :: "
							+ (String) data.getSessionData("S_PL_EFF_DT_MINUS_1"));

					/*
					 * policyActivityDate = policyActivityDate.replace("-", "");
					 * data.setSessionData("S_POLICY_ACTIVITY_DATE", policyActivityDate);
					 * data.addToLog(currElementName, "Case List 2 Policy effective date :: " +
					 * (String) data.getSessionData("S_POLICY_ACTIVITY_DATE"));
					 */
					String renewalPmtAmount = removeNegativeSymbol(caa, data, renewalPaymentAmount);
					data.setSessionData("S_RENEWAL_PAYMENT_AMOUNT", renewalPmtAmount);
					data.addToLog(currElementName, "Case List 2 Renewal Payment :: "
							+ (String) data.getSessionData("S_RENEWAL_PAYMENT_AMOUNT"));

					renewalDate = renewalDate.replace("-", "");
					data.setSessionData("S_RENEWAL_PAYMENT_DATE", renewalDate);
					data.addToLog(currElementName, "Case List 2 Renewal Payment date :: "
							+ (String) data.getSessionData("S_RENEWAL_PAYMENT_DATE"));

					renewalEffectiveDate = renewalEffectiveDate.replace("-", "");
					data.setSessionData("S_RENEWAL_EFFECTIVE_DATE", renewalEffectiveDate);
					data.addToLog(currElementName, "Case List 2 Renewal Effective date :: "
							+ (String) data.getSessionData("S_RENEWAL_EFFECTIVE_DATE"));

					data.setSessionData("S_POLICY_EFFECTIVE_DATE_EXISTS",
							!policyEffectiveDate.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 1 Is Policy Effective date exists :: "
							+ (String) data.getSessionData("S_POLICY_EFFECTIVE_DATE_EXISTS"));

					data.setSessionData("S_NSF_STATE", NSFState);
					data.addToLog(currElementName,
							"Case List 2 NFS State :: " + (String) data.getSessionData("S_NSF_STATE"));

					// Need to change
					data.setSessionData("S_POLICY_BALANCE_EQ_ZERO", policyBalance == 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 2 S_POLICY_BALANCE_EQ_ZERO :: "
							+ (String) data.getSessionData("S_POLICY_BALANCE_EQ_ZERO"));

					data.setSessionData("S_POLICY_BALANCE_LESSTHAN_ZERO", policyBalance < 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 2 S_POLICY_BALANCE_LESSTHAN_ZERO :: "
							+ (String) data.getSessionData("S_POLICY_BALANCE_LESSTHAN_ZERO"));

					data.setSessionData("S_POLICY_BALANCE_ANN", String.valueOf(policyBalance));
					data.addToLog(currElementName,
							"Case List 2 Policy Balance :: " + (String) data.getSessionData("S_POLICY_BALANCE_ANN"));

					data.addToLog(data.getCurrentElement(), "reEffectiveDateTemp : "+reEffectiveDateTemp);
					data.addToLog(data.getCurrentElement(), "currDateTemp : "+currDateTemp);
					if(reEffectiveDateTemp != null && currDateTemp != null) {
						data.setSessionData("S_RE_EFFDT_LESS_CURR_DT",
								(currDateTemp.isBefore(reEffectiveDateTemp)) ? "True" : "False");
						data.addToLog(currElementName, "Case List 2 Is renewal current date before effective date :: "
								+ (String) data.getSessionData("S_RE_EFFDT_LESS_CURR_DT"));
					}else {
						data.setSessionData("S_RE_EFFDT_LESS_CURR_DT", "False");
						data.addToLog(data.getCurrentElement(), "reEffectiveDateTemp is null");
					}
					

					data.setSessionData("S_RENEWAL_PAYMENT_AMOUNT_EXISTS",
							!renewalPaymentAmount.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 2 Is Renewal Payment Exists :: "
							+ (String) data.getSessionData("S_RENEWAL_PAYMENT_AMOUNT_EXISTS"));

					data.setSessionData("S_RENEWAL_PAYMENT_EXISTS", !renewalAmount.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 2 Renewal Payment :: "
							+ (String) data.getSessionData("S_RENEWAL_PAYMENT_EXISTS"));

					data.setSessionData("CASE_BELONGS_TO", "CL_2");
					data.addToLog(currElementName,
							"CASE_BELONGS_TO Session data value :: " + (String) data.getSessionData("CASE_BELONGS_TO"));
					// strExitState = "CL_2";

				} else if ((renewalDate.isEmpty() && renewalAmount.isEmpty() && renewalDate==null && renewalAmount==null)
						||(!renewalDate.isEmpty() && !renewalAmount.isEmpty() && renewalDate!=null && renewalAmount!=null
						&& (reEffectiveDateTemp!=null && currDateTemp!=null && (currDateTemp.isBefore(reEffectiveDateTemp) || currDateTemp.isEqual(reEffectiveDateTemp)))
						&& null != reEffDtPlusRelapseMinus_1 && (currDateTemp.isBefore(reEffDtPlusRelapseMinus_1)
								|| currDateTemp.isEqual(reEffDtPlusRelapseMinus_1) ) && !NSFState.isEmpty() && NSFState!=null && !NSFState.equals("N"))
						|| (!renewalDate.isEmpty() && !renewalAmount.isEmpty() && renewalDate!=null && renewalAmount!=null
								&& (reEffectiveDateTemp!=null && currDateTemp!=null && (currDateTemp.isBefore(reEffectiveDateTemp) || currDateTemp.isEqual(reEffectiveDateTemp)))
								&& null != reEffDtPlusRelapseMinus_1 && (!currDateTemp.isBefore(reEffDtPlusRelapseMinus_1)
										&& !currDateTemp.isEqual(reEffDtPlusRelapseMinus_1) ) && renewalPending!=null && !renewalPending.isEmpty() && "Y".equalsIgnoreCase(renewalPending)
										&& policyBalance == 0)) {

					data.addToLog(currElementName, "Customer belongs to CaseList 3::");

					String renewalInvAmount = removeNegativeSymbol(caa, data, renewalInvoiceAmount);
					data.setSessionData("S_RENEWAL_INVOICE_AMOUNT", renewalInvAmount);
					data.addToLog(currElementName, "Case List 3 Renewal Invoice Amount :: "
							+ (String) data.getSessionData("S_RENEWAL_INVOICE_AMOUNT"));

					reInvoiceDtMinus_1 = reInvoiceDtMinus_1.replace("-", "");
					data.setSessionData("S_RE_INVOICE_DT_MINUS_1", reInvoiceDtMinus_1);
					data.addToLog(currElementName, "Case List 3 Renewal Invoice Date minus 1 day :: "
							+ (String) data.getSessionData("S_RE_INVOICE_DT_MINUS_1"));

					renewalEffectiveDate = renewalEffectiveDate.replace("-", "");
					data.setSessionData("S_RENEWL_EFFECTIVE_DATE", renewalEffectiveDate);
					data.addToLog(currElementName, "Case List 3 Renewal Effective Date :: "
							+ (String) data.getSessionData("S_RENEWL_EFFECTIVE_DATE"));

					String reEffDtPlusRelapseMinus_1Temp = (reEffDtPlusRelapseMinus_1.toString()).replace("-", "");
					data.setSessionData("S_RE_EFFDT_PLUS_RELAPSE_MINUS_1", reEffDtPlusRelapseMinus_1Temp);
					data.addToLog(currElementName,
							"Case List 3 Renewal Effective Date plus relapse period minus 1 day :: "
									+ (String) data.getSessionData("S_RE_EFFDT_PLUS_RELAPSE_MINUS_1"));

					data.setSessionData("S_RENEWAL_AMOUNT_EXISTS", !renewalAmount.isEmpty() ? "True" : "False");
					data.addToLog(currElementName,
							"Case List 3 Renewal Amount :: " + (String) data.getSessionData("S_RENEWAL_AMOUNT_EXISTS"));

					data.setSessionData("S_RENEWAL_DATE_EXISTS", !renewalDate.isEmpty() ? "True" : "False");
					data.addToLog(currElementName,
							"Case List 3 Renewal Date :: " + (String) data.getSessionData("S_RENEWAL_DATE_EXISTS"));

					data.setSessionData("S_RE_EFFDT_LESS_EQ_CURR_DT",
							((currDateTemp.isBefore(reEffectiveDateTemp) || currDateTemp.isEqual(reEffectiveDateTemp)))
							? "True"
									: "False");
					data.addToLog(currElementName,
							"Case List 3 Renewal Effective date before or equal to current date :: "
									+ (String) data.getSessionData("S_RE_EFFDT_LESS_EQ_CURR_DT"));

					data.setSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK",
							(currDateTemp.isBefore(reEffDtPlusRelapseMinus_1)
									|| currDateTemp.isEqual(reEffDtPlusRelapseMinus_1) ? "True" : "False"));
					data.addToLog(currElementName,
							"Case List 3 Renewal Effective date plus relapse period munis 1 day is before or equal to current date :: "
									+ (String) data.getSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK"));

					data.setSessionData("S_RENEWAL_PENDING", renewalPending);
					data.addToLog(currElementName,
							"Case List 3 Is Renewal pending :: " + (String) data.getSessionData("S_RENEWAL_PENDING"));

					// Need to change
					data.setSessionData("S_POLICY_BALANCE_EQ_ZERO", policyBalance == 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 3 S_POLICY_BALANCE_EQ_ZERO :: "
							+ (String) data.getSessionData("S_POLICY_BALANCE_EQ_ZERO"));

					data.setSessionData("S_NSF_STATE", NSFState);
					data.addToLog(currElementName,
							"Case List 3 Is NSF State :: " + (String) data.getSessionData("S_NSF_STATE"));

					data.setSessionData("CASE_BELONGS_TO", "CL_3");
					data.addToLog(currElementName,
							"CASE_BELONGS_TO Session data value :: " + (String) data.getSessionData("CASE_BELONGS_TO"));
					// strExitState = "CL_3";

				} else if(((NSFState == null || NSFState.equalsIgnoreCase(Constants.N_FLAG)) && currDateTemp.isBefore(reEffectiveDateTemp) 
						&& (currDateTemp.isBefore(reEffDtPlusRelapseMinus_1)|| currDateTemp.isEqual(reEffDtPlusRelapseMinus_1)) 
						&& (reCancelDateTemp.isEqual(reEffectiveDateTemp)) )  || 
													((NSFState == null || NSFState.equalsIgnoreCase(Constants.N_FLAG)) && currDateTemp.isBefore(reEffectiveDateTemp) 
						&& (!currDateTemp.isBefore(reEffDtPlusRelapseMinus_1)|| !currDateTemp.isEqual(reEffDtPlusRelapseMinus_1)) 
						&& ((currDateTemp.isBefore(reEffDtPlusRelapseMinus_1)|| currDateTemp.isEqual(reEffDtPlusRelapseMinus_1)))) ||
													((NSFState == null || NSFState.equalsIgnoreCase(Constants.N_FLAG)) && currDateTemp.isBefore(reEffectiveDateTemp) 
						&& (!currDateTemp.isBefore(reEffDtPlusRelapseMinus_1)|| !currDateTemp.isEqual(reEffDtPlusRelapseMinus_1)) 
						&& ((!currDateTemp.isBefore(reEffDtPlusRelapseMinus_1)|| !currDateTemp.isEqual(reEffDtPlusRelapseMinus_1)))) ) {

					data.addToLog(currElementName, "Customer belongs to CaseList 4 ::");

					reCancelDtMinus_1 = reCancelDtMinus_1.replace("-", "");
					data.setSessionData("S_RE_CANCEL_DT_MINUS_1", reCancelDtMinus_1);
					data.addToLog(currElementName, "Case List 4 Renewal cancel date minus 1 day :: "
							+ (String) data.getSessionData("S_RE_CANCEL_DT_MINUS_1"));

					reCancelDtPlus_44 = reCancelDtPlus_44.replace("-", "");
					data.setSessionData("S_RE_CANCEL_DT_PLUS_44", reCancelDtPlus_44);
					data.addToLog(currElementName, "Case List 4 Renewal cancel date plus 44 days :: "
							+ (String) data.getSessionData("S_RE_CANCEL_DT_PLUS_44"));

					String reEffDtPlusRelapseMinus_1Temp = (reEffDtPlusRelapseMinus_1.toString()).replace("-", "");
					data.setSessionData("S_RE_EFFDT_PLUS_RELAPSE_MINUS_1", reEffDtPlusRelapseMinus_1Temp);
					data.addToLog(currElementName,
							"Case List 4 Renewal effective date plus relapse period minus 1 day :: "
									+ (String) data.getSessionData("S_RE_EFFDT_PLUS_RELAPSE_MINUS_1"));

					reCancelDtPlusRelapseMinus_1 = reCancelDtPlusRelapseMinus_1.replace("-", "");
					data.setSessionData("S_RE_CANCELDT_PLUS_RELAPSE_MINUS_1", reCancelDtPlusRelapseMinus_1);
					data.addToLog(currElementName, "Case List 4 Renewal cancel date plus relapse period minus 1 day :: "
							+ (String) data.getSessionData("S_RE_CANCELDT_PLUS_RELAPSE_MINUS_1"));

					String rePmtAmtPlusNSFFee = removeNegativeSymbol(caa, data, rePaymentAmtPlusNSFfee);
					data.setSessionData("S_RE_PAY_AMOUNT_PLUS_NSFFEE", rePmtAmtPlusNSFFee);
					data.addToLog(currElementName, "Case List 4 Renewal payment amount plus NSF Fee :: "
							+ (String) data.getSessionData("S_RE_PAY_AMOUNT_PLUS_NSFFEE"));

					data.setSessionData("S_NSF_FEE", nsfFee);
					data.addToLog(currElementName,
							"Case List 4 NSF Fee :: " + (String) data.getSessionData("S_NSF_FEE"));

					data.setSessionData("S_NSF_STATE", NSFState);
					data.addToLog(currElementName,
							"Case List 4 NSF State :: " + (String) data.getSessionData("S_NSF_STATE"));

					data.setSessionData("S_RE_EFFDT_LESS_CURR_DT",
							(currDateTemp.isBefore(reEffectiveDateTemp)) ? "True" : "False");
					data.addToLog(currElementName, "Case List 4 Is Renewal Effective date before current date :: "
							+ (String) data.getSessionData("S_RE_EFFDT_LESS_CURR_DT"));

					data.setSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK",
							((currDateTemp.isBefore(reEffDtPlusRelapseMinus_1)
									|| currDateTemp.isEqual(reEffDtPlusRelapseMinus_1))) ? "True" : "False");
					data.addToLog(currElementName,
							"Case List 4 Is Renewal Effective plus relapse period miuns 1 day is before or equal to current date :: "
									+ (String) data.getSessionData("S_RE_EFFDT_RELAPSE_PERIOD_CHECK"));

					data.setSessionData("S_RE_CANCELDT_EQ_RE_EFFDT",
							(reCancelDateTemp.isEqual(reEffectiveDateTemp)) ? "True" : "False");
					data.addToLog(currElementName,
							"Case List 4 Is Renewal cancel date is equal to Renewal Effective date :: "
									+ (String) data.getSessionData("S_RE_CANCELDT_EQ_RE_EFFDT"));

					data.setSessionData("S_RENEWAL_AMOUNT_EXISTS", !renewalAmount.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 4 Is Renewal Amount exists :: "
							+ (String) data.getSessionData("S_RENEWAL_AMOUNT_EXISTS"));

					data.setSessionData("S_RENEWAL_DATE_EXISTS", !renewalDate.isEmpty() ? "True" : "False");
					data.addToLog(currElementName, "Case List 4 Is Renewal date exists :: "
							+ (String) data.getSessionData("S_RENEWAL_DATE_EXISTS"));

					data.setSessionData("CASE_BELONGS_TO", "CL_4");
					data.addToLog(currElementName,
							"CASE_BELONGS_TO Session data value :: " + (String) data.getSessionData("CASE_BELONGS_TO"));
					// strExitState = "CL_4";

				} else if ((policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && "Y".equalsIgnoreCase(policyCollectionMode) && null != renewalPending && (!renewalPending.isEmpty()) && "Y".equalsIgnoreCase(renewalPending) 
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && "Y".equalsIgnoreCase(qualifiedForReinstateWithLapse) && null != NSFState && (!NSFState.isEmpty()) && "Y".equalsIgnoreCase(NSFState))
						|| (policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && "Y".equalsIgnoreCase(policyCollectionMode) && null != renewalPending && (!renewalPending.isEmpty()) && "Y".equalsIgnoreCase(renewalPending) 
								&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && "Y".equalsIgnoreCase(qualifiedForReinstateWithLapse) && (NSFState.isEmpty()))) {

					data.addToLog(currElementName, "Customer belongs to CaseList 5 ::");

					data.setSessionData(Constants.VXMLParam1, policyBalance);
					data.addToLog(currElementName, "BW Billing and Payments VXMLParam1 :: "
							+ (String) data.getSessionData("Constants.VXMLParam1"));

					data.setSessionData(Constants.VXMLParam2, lastPaymentAmtPlusNSFfee);
					data.addToLog(currElementName, "BW Billing and Payments VXMLParam2 :: "
							+ (String) data.getSessionData(Constants.VXMLParam2));

					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");

					data.setSessionData("S_LAST_PAYAMT_PLUS_NFSFEE", lastPaymentAmtPlusNSFfee);
					data.addToLog(currElementName, "Case List 5 last payment amount plus NSF fee :: "
							+ (String) data.getSessionData("S_LAST_PAYAMT_PLUS_NFSFEE"));

					data.setSessionData("S_POLICY_ENDING_NUM", policyEndingNum);
					data.addToLog(currElementName, "Case List 5 Policy last four digits :: "
							+ (String) data.getSessionData("S_POLICY_ENDING_NUM"));

					reCancelDtPlus_44 = reCancelDtPlus_44.replace("-", "");
					data.setSessionData("S_RE_CANCEL_DT_PLUS_44", reCancelDtPlus_44);
					data.addToLog(currElementName, "Case List 5 renewal cancel date plus 44 days :: "
							+ (String) data.getSessionData("S_RE_CANCEL_DT_PLUS_44"));

					policyCancelDtPlus_9 = policyCancelDtPlus_9.replace("-", "");
					data.setSessionData("S_PL_CANCEL_DT_PLUS_9", policyCancelDtPlus_9);
					data.addToLog(currElementName, "Case List 5 Policy cancel date plus 9 days :: "
							+ (String) data.getSessionData("S_PL_CANCEL_DT_PLUS_9"));

					// Need to change
					data.setSessionData("S_POLICY_BALANCE_GR_THANEQ_ZERO", policyBalance >= 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 5 S_POLICY_BALANCE_GR_THANEQ_ZERO :: "
							+ (String) data.getSessionData("S_POLICY_BALANCE_GR_THANEQ_ZERO"));

					data.setSessionData("S_POLICY_BALANCE_ANN", String.valueOf(policyBalance));
					data.addToLog(currElementName,
							"Case List 2 Policy Balance :: " + (String) data.getSessionData("S_POLICY_BALANCE_ANN"));

					data.setSessionData("S_POLICYIN_COLLECTION_MODE", policyCollectionMode);
					data.addToLog(currElementName, "Case List 5 Is Policy in Collection mode :: "
							+ (String) data.getSessionData("S_POLICYIN_COLLECTION_MODE"));

					data.setSessionData("S_RENEWAL_PENDING", renewalPending);
					data.addToLog(currElementName,
							"Case List 5 Is Renewal pending :: " + (String) data.getSessionData("S_RENEWAL_PENDING"));

					data.setSessionData("S_PL_QUALIFIED_REINSTATE_WITH_LAPSE", qualifiedForReinstateWithLapse);
					data.addToLog(currElementName, "Case List 5 Is Policy Qualified for renistate with lapse :: "
							+ (String) data.getSessionData("S_PL_QUALIFIED_REINSTATE_WITH_LAPSE"));

					data.setSessionData("S_RE_CANCELDT_RELAPSE_PERIOD_CHECK",
							((currDateTemp
									.isBefore(LocalDate.parse(reCancelDtPlusRelapseMinus_1,
											DateTimeFormatter.ofPattern("yyyy-MM-dd")))
									|| currDateTemp.isEqual(LocalDate.parse(reCancelDtPlusRelapseMinus_1,
											DateTimeFormatter.ofPattern("yyyy-MM-dd"))))) ? "True" : "False");
					data.addToLog(currElementName,
							"Case List 5 Is renewal cancel date plus relapse period is before or equal to current date :: "
									+ (String) data.getSessionData("S_RE_CANCELDT_RELAPSE_PERIOD_CHECK"));

					data.setSessionData("S_NSF_STATE", NSFState);
					data.addToLog(currElementName,
							"Case List 5 S_NSF_STATE :: " + (String) data.getSessionData("S_NSF_STATE"));

					data.setSessionData("CASE_BELONGS_TO", "CL_5");
					data.addToLog(currElementName,
							"CASE_BELONGS_TO Session data value :: " + (String) data.getSessionData("CASE_BELONGS_TO"));
					// strExitState = "CL_5";

				} else if ((policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (!policyRewriteOfferAva.isEmpty()) && policyRewriteOfferAva.equalsIgnoreCase("Y")) || 
					    
						(policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (policyRewriteOfferAva.isEmpty())) ||
					    
						(policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (policyRewriteOfferAva.isEmpty()) && null != NSFState && (!NSFState.isEmpty()) && "Y".equalsIgnoreCase(NSFState)) || 
						
						(policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (policyRewriteOfferAva.isEmpty()) && null != NSFState && (NSFState.isEmpty())) ||
						
						(policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (!policyRewriteOfferAva.isEmpty()) && "Y".equalsIgnoreCase(policyRewriteOfferAva)) || 
						
						(policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (!policyRewriteOfferAva.isEmpty()) && "Y".equalsIgnoreCase(policyRewriteOfferAva) && currDateTemp.isAfter(rewriteOfferExpiryDtTemp)) || 
						
						(policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (!policyRewriteOfferAva.isEmpty()) && "Y".equalsIgnoreCase(policyRewriteOfferAva) && (!currDateTemp.isAfter(rewriteOfferExpiryDtTemp))) || 
					    
					    (policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (!policyRewriteOfferAva.isEmpty()) && "Y".equalsIgnoreCase(policyRewriteOfferAva) && currDateTemp.isAfter(rewriteOfferExpiryDtTemp) && null != NSFState && (!NSFState.isEmpty()) && "Y".equalsIgnoreCase(NSFState)) || 
					    
					    (policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (!policyRewriteOfferAva.isEmpty()) && "Y".equalsIgnoreCase(policyRewriteOfferAva) && currDateTemp.isAfter(rewriteOfferExpiryDtTemp) && null != NSFState && (!NSFState.isEmpty()) && (!"Y".equalsIgnoreCase(NSFState))) || 
					    
					    (policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (!policyRewriteOfferAva.isEmpty()) && "Y".equalsIgnoreCase(policyRewriteOfferAva) && (!currDateTemp.isAfter(rewriteOfferExpiryDtTemp)) && null != NSFState && (!NSFState.isEmpty()) && "Y".equalsIgnoreCase(NSFState)) || 
					    
					    (policyBalance >= 0 && null != policyCollectionMode && (!policyCollectionMode.isEmpty()) && policyCollectionMode.equalsIgnoreCase("Y") && null != renewalPending && (!renewalPending.isEmpty()) && renewalPending.equalsIgnoreCase("Y")
						&& null != qualifiedForReinstateWithLapse && (!qualifiedForReinstateWithLapse.isEmpty()) && qualifiedForReinstateWithLapse.equalsIgnoreCase("Y") && null != qualifiedForRewrittenWithLapse && (!qualifiedForRewrittenWithLapse.isEmpty()) && qualifiedForRewrittenWithLapse.equalsIgnoreCase("Y")
					    && null != policyRewriteOfferAva && (!policyRewriteOfferAva.isEmpty()) && "Y".equalsIgnoreCase(policyRewriteOfferAva) && (!currDateTemp.isAfter(rewriteOfferExpiryDtTemp)) && null != NSFState && (!NSFState.isEmpty()) && (!"Y".equalsIgnoreCase(NSFState))))  {
					data.addToLog(currElementName, "Customer belongs to CaseList 6 ::");

					data.setSessionData("S_CURR_DT_GT_RE_OFF_EXP_DT", currDtGNReOffExpDt ? "True" : "False");
					data.addToLog(currElementName, "Case List 6 Is Current date is after renewal offer expiry date :: "
							+ (String) data.getSessionData("S_CURR_DT_GT_RE_OFF_EXP_DT"));

					reCancelDtPlus_44 = reCancelDtPlus_44.replace("-", "");
					data.setSessionData("S_RE_CANCEL_DT_PLUS_44", reCancelDtPlus_44);
					data.addToLog(currElementName, "Case List 6 Renewal cancel date plus 44 days :: "
							+ (String) data.getSessionData("S_RE_CANCEL_DT_PLUS_44"));

					// Need to change
					// data.setSessionData("S_POLICY_BALANCE", policyBalance);
					// data.addToLog(currElementName,
					// "Case List 6 S_POLICY_BALANCE :: " + (String)
					// data.getSessionData("S_POLICY_BALANCE"));

					data.setSessionData("S_POLICY_BALANCE_ANN", String.valueOf(policyBalance));
					data.addToLog(currElementName,
							"Case List 2 Policy Balance :: " + (String) data.getSessionData("S_POLICY_BALANCE_ANN"));

					data.setSessionData("S_POLICYIN_COLLECTION_MODE", policyCollectionMode);
					data.addToLog(currElementName, "Case List 6 Is policy in collection mode :: "
							+ (String) data.getSessionData("S_POLICYIN_COLLECTION_MODE"));

					data.setSessionData("S_RENEWAL_PENDING", renewalPending);
					data.addToLog(currElementName,
							"Case List 6 S_RENEWAL_PENDING :: " + (String) data.getSessionData("S_RENEWAL_PENDING"));

					data.setSessionData("S_PL_QUALIFIED_REINSTATE_WITH_LAPSE", qualifiedForReinstateWithLapse);
					data.addToLog(currElementName, "Case List 6 Is policy qualified for reinstate with lapse :: "
							+ (String) data.getSessionData("S_PL_QUALIFIED_REINSTATE_WITH_LAPSE"));

					data.setSessionData("S_PL_REWRITE_OFFER_AVA", policyRewriteOfferAva);
					data.addToLog(currElementName, "Case List 6 Is Policy rewrite offer available :: "
							+ (String) data.getSessionData("S_PL_REWRITE_OFFER_AVA"));

					data.setSessionData("S_RE_EFFDT_LESS_CURR_DT",
							(currDateTemp.isAfter(rewriteOfferExpiryDtTemp)) ? "True" : "False");
					data.addToLog(currElementName, "Case List 6 Is renewal effective date is after current date :: "
							+ (String) data.getSessionData("S_RE_EFFDT_LESS_CURR_DT"));

					data.setSessionData("CASE_BELONGS_TO", "CL_6");
					data.addToLog(currElementName,
							"CASE_BELONGS_TO Session data value :: " + (String) data.getSessionData("CASE_BELONGS_TO"));
					// strExitState = "CL_6";

				} else if ((policyBalance >= 0 && policyCollectionMode.equals("Y") && cancelledDays < 45)
						|| (policyBalance >= 0 && policyCollectionMode.equals("Y") && (cancelledDays > 45 && cancelledDays < 66) 
						|| (policyBalance >= 0 && policyCollectionMode.equals("Y") &&  cancelledDays > 66))) {

					data.addToLog(currElementName, "Customer belongs to CaseList 7 ::");

					data.setSessionData("S_COLLECTION_AMOUNT", collectionAmount);
					data.addToLog(currElementName, "Case List 7 S_COLLECTION_AMOUNT :: "
							+ (String) data.getSessionData("S_COLLECTION_AMOUNT"));

					data.setSessionData("S_POLICYIN_COLLECTION_MODE", policyCollectionMode);
					data.addToLog(currElementName, "Case List 7 Is Policy in collection mode :: "
							+ (String) data.getSessionData("S_POLICYIN_COLLECTION_MODE"));

					data.setSessionData("S_POLICY_CANCELLED_BEFORE_44_DAYS", cancelledDays < 44 ? "True" : "False");
					data.addToLog(currElementName, "Case List 7 S_POLICY_CANCELLED_BEFORE_44_DAYS :: "
							+ (String) data.getSessionData("S_POLICY_CANCELLED_BEFORE_44_DAYS"));

					data.setSessionData("S_POLICY_CANCELLED_BW_44_66_DAYS",
							(cancelledDays > 44 && cancelledDays < 66) ? "True" : "False");
					data.addToLog(currElementName, "Case List 7 S_POLICY_CANCELLED_BW_44_66_DAYS :: "
							+ (String) data.getSessionData("S_POLICY_CANCELLED_BW_44_66_DAYS"));

					// Need to change
					data.setSessionData("S_POLICY_BALANCE_GR_THANEQ_ZERO", policyBalance >= 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 7 S_POLICY_BALANCE_GR_THANEQ_ZERO :: "
							+ (String) data.getSessionData("S_POLICY_BALANCE_GR_THANEQ_ZERO"));

					data.setSessionData("CASE_BELONGS_TO", "CL_7");
					data.addToLog(currElementName,
							"CASE_BELONGS_TO Session data value :: " + (String) data.getSessionData("CASE_BELONGS_TO"));
					// strExitState = "CL_7";

				} else if (policyBalance >= 0 && policyCollectionMode.equals("Y") && renewalPending.equals("Y")) {

					data.addToLog(currElementName, "Customer belongs to CaseList 8 ::");

					data.setSessionData("S_POLICYIN_COLLECTION_MODE", policyCollectionMode);
					data.addToLog(currElementName, "Case List 8 Is Policy in cancellation mode :: "
							+ (String) data.getSessionData("S_POLICYIN_COLLECTION_MODE"));

					data.setSessionData("S_RENEWAL_PENDING", renewalPending);
					data.addToLog(currElementName,
							"Case List 8 S_RENEWAL_PENDING :: " + (String) data.getSessionData("S_RENEWAL_PENDING"));

					// Need to change
					data.setSessionData("S_POLICY_BALANCE_GR_THANEQ_ZERO", policyBalance >= 0 ? "True" : "False");
					data.addToLog(currElementName, "Case List 7 S_POLICY_BALANCE_GR_THANEQ_ZERO :: "
							+ (String) data.getSessionData("S_POLICY_BALANCE_GR_THANEQ_ZERO"));

					data.setSessionData("CASE_BELONGS_TO", "CL_8");
					data.addToLog(currElementName,
							"CASE_BELONGS_TO Session data value :: " + (String) data.getSessionData("CASE_BELONGS_TO"));
					// strExitState = "CL_8";

				}
				data.addToLog(currElementName, "BW Billing and Payments Customer does not belongs any case list ::");
			}
		} catch (Exception e) {

			strExitState = Constants.ER;
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			data.addToLog(currElementName + "Error in Case list check method :: ",
					e.getMessage() + " :: Full Exception  :: " + sw.toString());
		}
		data.addToLog(currElementName + "Case Customer belongs to at last :: ",
				(String) data.getSessionData("CASE_BELONGS_TO"));
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
}