package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.BillPresentMents.AccountAgreementBillingSummaryView;
import com.farmers.bean.BillPresentMents.BillPresentMentRoot;
import com.farmers.bean.BillPresentMents.InsurancePolicyBillingSummaryView;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.google.gson.Gson;

public class FDS_BillPayments extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			strExitState = billpresentmentretrieveBillingSummary(data, caa, currElementName);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for SBP_HOST_001  :: " + e);
			caa.printStackTrace(e);
		}

		if(strExitState.equalsIgnoreCase(Constants.ER)) {
			//String mspKey = "FARMERS"+":"+"SBP_HOST_001"+":"+"ER";
			//data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
			//data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);
			caa.createMSPKey(caa, data, "SBP_HOST_001", "ER");
		}

		return strExitState;
	}

	private String billpresentmentretrieveBillingSummary(DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;

		String strRespBody = "";
		String strReqBody = "";
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.N_FLAG);
			data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.N_FLAG);

			if ((data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL) != null)
					&& (data.getSessionData(Constants.S_READ_TIMEOUT) != null)
					&& (data.getSessionData(Constants.S_CONN_TIMEOUT) != null)
					&& (data.getSessionData(Constants.S_POLICY_NUM) != null)) {
				SetHostDetails objHostDetails = new SetHostDetails(caa);
				objHostDetails.setinitalValue();

				String wsurl = (String) data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String appType = "VRS-101";
				String sysName = "VRS";
				boolean loopback = false;
				String productGroupName = "FAB";
				String PolicyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);

				data.addToLog(data.getCurrentElement(), "SBP_HOST_001 URL : " + wsurl);
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE(PRIYA,SHAIK)
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
				JSONObject responses = lookups.GetBillPresentmentretrieveBillingSummary(wsurl, tid, appType, sysName,
						loopback, PolicyNumber, productGroupName, conTimeout, readTimeout, context,region,UAT_FLAG);
				//UAT ENV CHANGEEND(SHAIK,PRIYA)
				data.addToLog(data.getCurrentElement(), "SBP_HOST_001 RESPONSE : " + responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if (responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if ((responses.containsKey(Constants.RESPONSE_CODE)) && ((Integer) responses.get(Constants.RESPONSE_CODE)) == 200
							&& responses.containsKey(Constants.REQUEST_BODY)) {
						data.addToLog(currElementName, "Set SBP_HOST_001 : billpresentmentretrieveBillingSummary API Response into session with the key name of "
								+ currElementName + "_RESP");
						strRespBody = responses.get("responseBody").toString();
						data.setSessionData(currElementName + "_RESP", responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_billpresentmentretrieveBillingSummary(data, caa,
								currElementName, strRespBody);

					} else {
						if(responses.get(Constants.RESPONSE_MSG)!=null)
							strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
				}

				try {
					data.addToLog(currElementName, "Reporting STARTS for FARMERS");
					objHostDetails.startHostReport(currElementName, "billpresentmentretrieveBillingSummary", strReqBody, region,(String) data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL));
					objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
					data.addToLog(currElementName, "Reporting ENDS for FARMERS");
				} catch (Exception e1) {
					data.addToLog(currElementName,
							"Exception while forming host reporting for SBP_HOST_001_FARMERS  billpresentmentretrieveBillingSummary call  :: " + e1);
					caa.printStackTrace(e1);
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception in KYCAF_HOST_001  billpresentmentretrieveBillingSummary API call  :: " + e);
			caa.printStackTrace(e);
		}



		return strExitState;
	}

	private String apiResponseManupulation_billpresentmentretrieveBillingSummary(DecisionElementData data,
			CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			// JSONObject resp = (JSONObject)new JSONParser().parse(strRespBody);
			// String GSONLIB = resp.toString();
			Gson gsonobj = new Gson();
			String lastPaymentDate = "";
			String lastPaymentAmount = "";
			String nextPaymentDate = "";
			String nextPaymentAmount = "";
			String policyStatus = "";
			String currentBalance = "";
			String totalOutStandAmt = "";
			String policyExpiryDate = "";
			String billingzipcode = "";
			BillPresentMentRoot billpresentroot = (BillPresentMentRoot) gsonobj.fromJson(strRespBody,
					BillPresentMentRoot.class);

			data.addToLog(data.getCurrentElement(), billpresentroot.toString());
			AccountAgreementBillingSummaryView objAcctAgmtBillSummView = billpresentroot
					.getRetrieveBillingSummaryResponse().getBillingSummary().getAccountAgreementBillingSummaryView();

			if (null != objAcctAgmtBillSummView && null != objAcctAgmtBillSummView.getBasicPartyContactPointDetails().getPostalAddress()) {
				billingzipcode = objAcctAgmtBillSummView.getBasicPartyContactPointDetails().getPostalAddress().getPostalCode();
				data.setSessionData("S_PAYOR_ZIP_CODE", billingzipcode);
				data.addToLog(currElementName, "Setting Billing Zip code for EPCPAYMENTUS into session :: "+billingzipcode);
			}

			if (objAcctAgmtBillSummView != null && objAcctAgmtBillSummView.getBillingAllocatedPaymentView() != null) {

				lastPaymentDate = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getReceivedDate();
				lastPaymentAmount = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getAmount().getTheCurrencyAmount();
				data.addToLog(data.getCurrentElement(), "Actual lastPaymentAmount : "+lastPaymentAmount);

				if (objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView() != null) {
					List<InsurancePolicyBillingSummaryView> objInsurancePolicyBillSummView = objAcctAgmtBillSummView
							.getInsurancePolicyBillingSummaryView();
					for (InsurancePolicyBillingSummaryView insurancePolicyBillingSummaryView : objInsurancePolicyBillSummView) {
						policyStatus = insurancePolicyBillingSummaryView.getInsurancePolicy().getStatus().getState();
					}
				}
				if (objAcctAgmtBillSummView.getBasicBillingAccountAgreementView() != null && objAcctAgmtBillSummView
						.getBasicBillingAccountAgreementView().getMinimumPaymentDueAmount() != null) {
					currentBalance = objAcctAgmtBillSummView.getBasicBillingAccountAgreementView().getMinimumPaymentDueAmount().getTheCurrencyAmount();
					nextPaymentAmount = currentBalance;
					nextPaymentAmount = removeNegativeSymbol(caa, data, currentBalance);
					nextPaymentDate = objAcctAgmtBillSummView.getBasicBillingAccountAgreementView().getPaymentDueDate();
				}

				if (objAcctAgmtBillSummView.getBasicBillingAccountAgreementView() != null && objAcctAgmtBillSummView
						.getBasicBillingAccountAgreementView().getOutStandingAmount() != null) {
					totalOutStandAmt = objAcctAgmtBillSummView.getBasicBillingAccountAgreementView().getOutStandingAmount().getTheCurrencyAmount();
				}
				if (null != objAcctAgmtBillSummView.getBasicBillingAccountAgreementView() 
						&& null != objAcctAgmtBillSummView.getBasicBillingAccountAgreementView().getAccountAgreementNumber()) {
					data.setSessionData("S_EPC_PAYMENT_POLICYNUM", objAcctAgmtBillSummView.getBasicBillingAccountAgreementView().getAccountAgreementNumber());
				}
			}

			data.addToLog(data.getCurrentElement(), "Last Payment Date : " + lastPaymentDate);
			data.addToLog(data.getCurrentElement(), "Last Payment Amount : " + lastPaymentAmount);
			data.addToLog(data.getCurrentElement(), "Next Payment Date : " + nextPaymentDate);
			data.addToLog(data.getCurrentElement(), "Next Payment Amount : " + nextPaymentAmount);
			data.addToLog(data.getCurrentElement(), "Current Balance : " + currentBalance);
			data.addToLog(data.getCurrentElement(), "Outstanding Amount : " + totalOutStandAmt);
			data.addToLog(data.getCurrentElement(), "Policy Status : " + policyStatus);
			data.addToLog(data.getCurrentElement(), "Policy Expiry Date : " + policyExpiryDate);


			data.setSessionData(Constants.S_LESS_THAN_DUE_DATE, Constants.N_FLAG);

			if ((policyStatus.equalsIgnoreCase(Constants.ACTIVE))) {
				data.setSessionData(Constants.S_POLICY_STATUS, Constants.ACTIVE);
			} else if (policyStatus.contains(Constants.CANCEL)|| (policyStatus.equalsIgnoreCase(Constants.CANCELLED)) || policyStatus.contains("Cancelled")) {
				data.setSessionData(Constants.S_POLICY_STATUS, Constants.CANCELLED);
			} else if (policyStatus.contains(Constants.EXPIRED)) {
				data.setSessionData(Constants.S_POLICY_STATUS, Constants.EXPIRED);
			} else {
				data.setSessionData(Constants.S_POLICY_STATUS, Constants.ACTIVE);
			}
			data.addToLog(data.getCurrentElement(), "Policy Status : " + policyStatus);

			data.setSessionData(Constants.S_CURRENT_BAL, currentBalance);
			data.setSessionData(Constants.S_TOT_OUT_AMT, totalOutStandAmt);
			if(currentBalance != null && !currentBalance.equals("")) {
				Double currBalanceDouble = Double.parseDouble(currentBalance);
				if (currBalanceDouble != 0 && currBalanceDouble != 0.00) {
					data.setSessionData("S_CURR_BAL_GT0", Constants.Y_FLAG);
				}
			}
			if(totalOutStandAmt != null && !totalOutStandAmt.equals("")) {
				Double totOutAmountDouble = Double.parseDouble(totalOutStandAmt);
				if (totOutAmountDouble != 0 && totOutAmountDouble != 0.00) {
					data.setSessionData("S_BALANCE_GT0", Constants.Y_FLAG);
				}
			}


			if ((lastPaymentDate != null) && (!lastPaymentDate.equals("")) && (lastPaymentAmount != null)
					&& (!lastPaymentAmount.equals("")) && !"0.00".equalsIgnoreCase(lastPaymentAmount)) {
				data.addToLog(data.getCurrentElement(), "Last Payment Amount & Date are avaialble");
				String lastAmount = removeNegativeSymbol(caa, data, lastPaymentAmount);
				data.setSessionData(Constants.S_LASTPAYMENT_AMOUNT, lastAmount);
				if (lastPaymentDate.contains(Constants.HYPHEN)) {
					lastPaymentDate = lastPaymentDate.replaceAll(Constants.HYPHEN, "");
				}
				data.setSessionData(Constants.S_LASTPAYMENT_DATE, lastPaymentDate);
				data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.Y_FLAG);
				strExitState = Constants.SU;
			}
			if ((nextPaymentDate != null) && (!nextPaymentDate.equals("")) && (nextPaymentAmount != null)
					&& (!nextPaymentAmount.equals("")&& !"0.00".equalsIgnoreCase(nextPaymentAmount))) {
				data.addToLog(data.getCurrentElement(), "Next Payment Amount & Date are avaialble");
				String nextAmount = removeNegativeSymbol(caa, data, nextPaymentAmount);
				data.setSessionData(Constants.S_NEXTPAYMENT_AMOUNT, nextAmount);
//TimeZone Fix by Venkatesh K M
				
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.PAYMENT_DATE_FORMAT);

				Date nextPmtDate = sdf.parse(nextPaymentDate);

				Instant instant = Instant.now();

				ZoneId centralTimeZone = ZoneId.of("America/Chicago");

				ZonedDateTime centralTime = instant.atZone(centralTimeZone);

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern((Constants.PAYMENT_DATE_FORMAT));

				String convertedCentralTimeZoneDate = dtf.format(centralTime);

				data.addToLog("convertedcentralTimeZoneDate", convertedCentralTimeZoneDate);

				Date systemDate = sdf.parse(convertedCentralTimeZoneDate);

				if (nextPmtDate.after(systemDate) || nextPmtDate.equals(systemDate)) {
					data.setSessionData(Constants.S_LESS_THAN_DUE_DATE, Constants.Y_FLAG);

				}
				if (nextPaymentDate.contains(Constants.HYPHEN)) {
					nextPaymentDate = nextPaymentDate.replaceAll(Constants.HYPHEN, "");
				}
				data.setSessionData(Constants.S_NEXTPAYMENT_DATE, nextPaymentDate);
				data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.Y_FLAG);
				data.setSessionData(Constants.S_NEXT_PAYMENT_ALLOWED, Constants.Y_FLAG);
				strExitState = Constants.SU;

			}


			if (policyExpiryDate != null && !policyExpiryDate.equals("") && policyStatus.contains(Constants.EXPIRED)) {
				policyExpiryDate = policyExpiryDate.replace("-", "");
				data.setSessionData(Constants.S_POLICY_EXPIRATION_DATE, policyExpiryDate);
				strExitState = Constants.SU;
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
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
