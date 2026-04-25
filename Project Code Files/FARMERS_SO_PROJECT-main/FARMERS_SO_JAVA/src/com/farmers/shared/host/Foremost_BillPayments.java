package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.BillPresentMents.AccountAgreementBillingSummaryView;
import com.farmers.bean.BillPresentMents.BillPresentMentRoot;
import com.farmers.bean.BillPresentMents.InsurancePolicyBillingSummaryView;
import com.farmers.bean.BillPresentMents.NextFinancialTransactionCommunicationView;
import com.farmers.bean.Halo.BillingDetails.BillingDetail;
import com.farmers.bean.Halo.BillingDetails.HalloBillingDetails;
import com.farmers.bean.Halo.BillingDetails.Payment;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.google.gson.Gson;

public class Foremost_BillPayments extends DecisionElementBase {
	
	String strReqBody = Constants.EmptyString;

	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			strExitState = epcHaloBillingDetailsGroup(data, caa, currElementName);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for SBP_HOST_001  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String epcHaloBillingDetailsGroup(DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strRespBody = "";
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {

			if ((data.getSessionData(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL) != null) && (data.getSessionData(Constants.S_CONN_TIMEOUT) != null) && (data.getSessionData(Constants.S_READ_TIMEOUT) != null)) {
				
				String wsurl = (String) data.getSessionData(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String PolicyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String Actortype = "C";
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				data.addToLog(data.getCurrentElement(), "S_EPCHALO_BILLING_DETAILSGROUP_URL URL : " + wsurl);

				Lookupcall lookups = new Lookupcall();
				data.addToLog("API URL: ",wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				//UAT ENV CHANGE(SHAIK,PRIYA)
					if("YES".equalsIgnoreCase(UAT_FLAG)) {
					 region = regionDetails.get(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL);
					}else {
						region="PROD";
					}
					JSONObject responses = lookups.GetEPCHaloBillingDetailsGroup_Post(wsurl, tid, PolicyNumber, Actortype, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context,region,UAT_FLAG);
					//UAT ENV CHANGEEND(SHAIK,PRIYA)
				
				data.addToLog(data.getCurrentElement(), "SBP_HOST_001 RESPONSE : " + responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				
				if (responses != null) {
				
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					
					if ((responses.containsKey(Constants.RESPONSE_CODE)) && (((Integer) responses.get(Constants.RESPONSE_CODE)) == 200) && (responses.containsKey(Constants.RESPONSE_BODY))) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : epcHaloBillingDetailsGroup API Response into session with the key name of " + currElementName + "_RESP");
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + "_RESP", responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_epcHaloBillingDetailsGroup(data, caa, currElementName,
								strRespBody);
					}  
					else {
						if(responses.get(Constants.RESPONSE_MSG)!=null) {
							strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
						}
					}
				}
			} else {
				data.addToLog(data.getCurrentElement(), "Mandatory API Input are missing");
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in KYCAF_HOST_001  FWSPolicyLookup API call  :: " + e);
			caa.printStackTrace(e);
		}
		try {
			data.addToLog(currElementName, "Reporting STARTS for FOREMOST");
			objHostDetails.startHostReport(currElementName, "epcHaloBillingDetailsGroup", strReqBody,region, (String) data.getSessionData(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL));
			objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			data.addToLog(currElementName, "Reporting ENDS for FOREMOST : " + objHostDetails);
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for SBP_HOST_001_FOREMOST  epcHaloBillingDetailsGroup call  :: " + e1);
			caa.printStackTrace(e1);
		}
		return strExitState;
	}

	private String billpresentmentAfterHalo(DecisionElementData data, CommonAPIAccess caa, String currElementName, boolean isLastPaymentDetailAvail) {
		String exitState = "";
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strRespBody = "";
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if ((data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL) != null) && (data.getSessionData(Constants.S_READ_TIMEOUT) != null) && (data.getSessionData(Constants.S_CONN_TIMEOUT) != null) && (data.getSessionData(Constants.S_POLICY_NUM) != null)) {
				
				String wsurl = (String) data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String appType = "VRS-Foremost-101";
				String sysName = "VRS-Foremost";
				boolean loopback = false;
				String productGroupName = "FAB";
				String PolicyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT)).intValue();

				LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				data.addToLog(data.getCurrentElement(), "wsurl : " + wsurl);
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE(SHAIK,PRIYA)
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
				
				JSONObject responses = lookups.GetBillPresentmentretrieveBillingSummary(wsurl, tid, appType, sysName, loopback, PolicyNumber, productGroupName, conTimeout, readTimeout, context,region,UAT_FLAG);

				data.addToLog("responses", responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

				if (responses != null) {
					
					if ((responses.containsKey(Constants.RESPONSE_CODE)) && (((Integer) responses.get(Constants.RESPONSE_CODE)) == 200) && (responses.containsKey(Constants.RESPONSE_BODY))) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : billpresentmentretrieveBillingSummary API Response into session with the key name of " + currElementName + "_RESP");
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + "_RESP", responses.get(Constants.RESPONSE_BODY));
						exitState = apiResponseManupulation_billpresentmentretrieveBillingSummaryHalo(data, caa, currElementName, strRespBody, true, isLastPaymentDetailAvail);
					} else {
						strRespBody = responses.get("responseMsg").toString();
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception in KYCAF_HOST_001  billpresentmentretrieveBillingSummary API call  :: " + e);
			caa.printStackTrace(e);
		}
		try {
			data.addToLog(currElementName, "Reporting STARTS for billpresentmentAfterHalo");
			objHostDetails.startHostReport(currElementName, "billpresentmentAfterHalo", strReqBody,region, data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL).toString().length() > 99 ?  (String)data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL).toString().substring(0, 99) : (String)data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL));
			objHostDetails.endHostReport(data, strRespBody, exitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			data.addToLog(currElementName, "Reporting ENDS for billpresentmentAfterHalo : " + objHostDetails);
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for SBP_HOST_001_FOREMOST  epcHaloBillingDetailsGroup call  :: " + e1);
			caa.printStackTrace(e1);
		}
		return exitState;
	}


	private String apiResponseManupulation_billpresentmentretrieveBillingSummaryHalo(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, boolean isComingFromHalo, boolean isLastPmtAvail) {
		
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
			String renewalBalance = "";
			String billingzipcode = "";
			String lastpaymentFlag = (String) data.getSessionData(Constants.S_LAST_PMNT_AVAIL);
			BillPresentMentRoot billpresentroot = (BillPresentMentRoot) gsonobj.fromJson(strRespBody, BillPresentMentRoot.class);

			data.addToLog(data.getCurrentElement(), billpresentroot.toString());
			AccountAgreementBillingSummaryView objAcctAgmtBillSummView = billpresentroot.getRetrieveBillingSummaryResponse().getBillingSummary().getAccountAgreementBillingSummaryView();
			if (objAcctAgmtBillSummView != null && objAcctAgmtBillSummView.getBillingAllocatedPaymentView() != null) {
				
				if (null != objAcctAgmtBillSummView && null != objAcctAgmtBillSummView.getAccountAgreementAnalyticsView().getpostalAddress().getpostalCode()) {
					billingzipcode = objAcctAgmtBillSummView.getAccountAgreementAnalyticsView().getpostalAddress().getpostalCode();
					billingzipcode = billingzipcode.length() > 5 ? billingzipcode.substring(0,  5) : billingzipcode;
					data.setSessionData("S_PAYOR_ZIP_CODE", billingzipcode);
					data.addToLog(currElementName, "Setting Billing zip code for EPCPAYMENTUS into session :: "+data.getSessionData("S_PAYOR_ZIP_CODE"));
				}
				/*
				lastPaymentDate = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getPaymentDate();
				lastPaymentAmount = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getAmount().getTheCurrencyAmount();
				 */
				
				if (objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView() != null) {
					List<InsurancePolicyBillingSummaryView> objInsurancePolicyBillSummView = objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView();
					
					for (InsurancePolicyBillingSummaryView insurancePolicyBillingSummaryView : objInsurancePolicyBillSummView) {
					
						if (isComingFromHalo) {
							policyStatus = insurancePolicyBillingSummaryView.getInsurancePolicy().getStatus().getDescription();
							policyStatus = policyStatus.trim();
						} 
						else {
							policyStatus = insurancePolicyBillingSummaryView.getInsurancePolicy().getStatus().getState();
							policyStatus = policyStatus.trim();
						}
						if (null != policyStatus && isComingFromHalo && (policyStatus.equalsIgnoreCase("EXPIRED_NOT_RENEWED") || policyStatus.equalsIgnoreCase("EXPIRED"))) {
							//policyExpiryDate = insurancePolicyBillingSummaryView.getInsurancePolicy().getEndDate();
							policyExpiryDate = objAcctAgmtBillSummView.getBasicBillingAccountAgreementView().getEndDate();
						}
					}
				}

				NextFinancialTransactionCommunicationView objNxtFinTransCommView = objAcctAgmtBillSummView.getNextFinancialTransactionCommunicationView();
				
				
				if(null != objAcctAgmtBillSummView && null != objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView() && null != objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView() && null != objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue()) { 
					nextPaymentDate =  objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getDueDate(); 
					nextPaymentAmount = objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount();
					currentBalance = objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount();
				}

				if(objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView() != null &&  objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView().get(0).getInsurancePolicy().getCumulativePremiumAmount().getTheCurrencyAmount() != null) {
					renewalBalance = objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView().get(0).getInsurancePolicy().getCumulativePremiumAmount().getTheCurrencyAmount();

				}

				if (objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView() != null && objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount() != null) {
					totalOutStandAmt = objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount();
				}
				
				//As per Tauzia (Hypercare #18 issue), we need to consider previousFinancialTransactionCommunicationView obj for Foremost
				if (null != objAcctAgmtBillSummView && null != objAcctAgmtBillSummView.getBillingAllocatedPaymentView() && null != objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment() && null != objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getAmount()) {
					
					if(null != objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getPaymentDate()) {
						lastPaymentDate = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getPaymentDate();
					}
					if(null != objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getAmount()  && null != objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getAmount().getTheCurrencyAmount()) {
						lastPaymentAmount = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getAmount().getTheCurrencyAmount();
					}
				}
			}

			data.addToLog(data.getCurrentElement(), "Last Payment Date : " + lastPaymentDate);
			data.addToLog(data.getCurrentElement(), "Last Payment Amount : " + lastPaymentAmount);
			data.addToLog(data.getCurrentElement(), "Next Payment Date : " + nextPaymentDate);
			data.addToLog(data.getCurrentElement(), "Next Payment Amount : " + nextPaymentAmount);
			data.addToLog(data.getCurrentElement(), "Current Balance : " + currentBalance);
			data.addToLog(data.getCurrentElement(), "Outstanding Amount : " + totalOutStandAmt);
			data.addToLog(data.getCurrentElement(), "Policy Status : " + policyStatus);
			data.addToLog(data.getCurrentElement(), "Renewal Balance : " + renewalBalance);
			data.addToLog(data.getCurrentElement(), "Policy Expiry Date will applicable only for Foremost BU : " + policyExpiryDate);
			data.addToLog(data.getCurrentElement(), Constants.S_LAST_PMNT_AVAIL+" : "+lastpaymentFlag);

			data.setSessionData(Constants.S_LESS_THAN_DUE_DATE, Constants.N_FLAG);
			if (policyStatus.equalsIgnoreCase("PAID_IN_FULL") || policyStatus.equalsIgnoreCase("SPEC_BILL_OUTSTND") || policyStatus.equalsIgnoreCase("REG_BILL_OUTSTND") || policyStatus.equalsIgnoreCase("REMINDER_OUTSTND") || policyStatus.equalsIgnoreCase("WAIT_TO_BILL") || policyStatus.equalsIgnoreCase("Down_PMT_OUTSTND") || policyStatus.equalsIgnoreCase("FLEX_STOP") || policyStatus.equalsIgnoreCase("EXPIRE_PENDING") || policyStatus.equalsIgnoreCase("CANCEL_PENDING") || policyStatus.equalsIgnoreCase("CANCEL_REQ_OUTSTND") || policyStatus.equalsIgnoreCase("CANCEL_EP_OUTSTND")) {
				data.setSessionData(Constants.S_POLICY_STATUS, "ACTIVE");
				policyStatus = "ACTIVE";
			} else if (policyStatus.contains("CANCELLED")) {
				data.setSessionData(Constants.S_POLICY_STATUS, "CANCELLED");
				policyStatus = "EXPIRED";
			} else if (policyStatus.contains("EXPIRED_NOT_RENEWED") || policyStatus.contains("EXPIRED")) {
				data.setSessionData(Constants.S_POLICY_STATUS, "EXPIRED");
				policyStatus = "EXPIRED";
			} else {
				data.setSessionData(Constants.S_POLICY_STATUS, "OTHER");
				policyStatus = "OTHER";
			}

			data.setSessionData(Constants.S_BILLINGINFO_RES, Constants.N_FLAG);
			data.setSessionData(Constants.S_CURRENT_BAL, currentBalance);
			data.addToLog(data.getCurrentElement(), "Policy Status after consolidating  :: " + data.getSessionData(Constants.S_POLICY_STATUS));

			if (!isLastPmtAvail) {
				if ((lastPaymentDate != null) && (!lastPaymentDate.equals("")) && (lastPaymentAmount != null) && (!lastPaymentAmount.equals(""))) {
					data.addToLog(data.getCurrentElement(), "Last Payment Not available in Halo. So fetching Last Payment & date from bill presentment");
					String lastAmount = removeNegativeSymbol(caa, data, lastPaymentAmount);
					data.setSessionData(Constants.S_LASTPAYMENT_AMOUNT, lastAmount);
				
					if (lastPaymentDate.contains(Constants.HYPHEN)) {
						lastPaymentDate = lastPaymentDate.replaceAll(Constants.HYPHEN, "");
					}
					
					data.setSessionData(Constants.S_LASTPAYMENT_DATE, lastPaymentDate);
					data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.Y_FLAG);
					strExitState = Constants.SU;
				}
				else {
					data.addToLog(currElementName, "Last Payment/Date not available in Mainframe API");
					data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.N_FLAG);
				}
			}
			else {
				data.addToLog(currElementName, "Last Payment Available in HALO :: Not fetching from MainFrame API");
				//data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.N_FLAG);
				strExitState = Constants.SU;
			}
			
			if ((nextPaymentDate != null) && (!nextPaymentDate.equals("")) && (nextPaymentAmount != null) && (!nextPaymentAmount.equals("")) && !"0.00".equalsIgnoreCase(nextPaymentAmount)) {
				String nextAmount = removeNegativeSymbol(caa, data, nextPaymentAmount);
				data.setSessionData(Constants.S_NEXTPAYMENT_AMOUNT, nextAmount);

			//	SimpleDateFormat sdf = new SimpleDateFormat(Constants.PAYMENT_DATE_FORMAT);
			//	Date nextPmtDate = sdf.parse(nextPaymentDate);
			//	Date systemDate = sdf.parse(new SimpleDateFormat(Constants.PAYMENT_DATE_FORMAT).format(new Date()));
				
				
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
				
				if (Double.parseDouble(nextPaymentAmount) > 0.00) {
					data.setSessionData("S_CURRENTBALANCE", "TRUE");
				}
						Date sysDate = new Date();
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
						Date providedDate = sdf2.parse(nextPaymentDate);
						boolean isSystemDateGreater = sysDate.after(providedDate);
						data.setSessionData("S_SYSTEMDATE_GREATER", isSystemDateGreater);
				
				strExitState = Constants.SU;

			}
			else {
				data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.N_FLAG);
				strExitState = Constants.SU;
			}
			
			data.setSessionData(Constants.S_TOT_OUT_AMT, totalOutStandAmt);
			
			if (policyExpiryDate != null && !policyExpiryDate.equals("") && policyStatus.equalsIgnoreCase("EXPIRED")) {
				policyExpiryDate = policyExpiryDate.replace("-", "");
				data.setSessionData(Constants.S_POLICY_EXPIRATION_DATE, policyExpiryDate);
				strExitState = Constants.SU;
			}
			
			if (policyStatus.equalsIgnoreCase("OTHER")) {
				strExitState = Constants.SU;
			}

			data.setSessionData(Constants.S_NEXT_PAYMENT_ALLOWED, Constants.Y_FLAG);
			data.setSessionData("S_BILLING_INFO_RESTRICTED", "FALSE");
			data.setSessionData("S_PAYMENT_ALLOWED", "TRUE");
			// data.addToLog("S_Current_Balance", currentBalance);
			data.setSessionData("S_Renewal_Balance", renewalBalance);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String apiResponseManupulation_epcHaloBillingDetailsGroup(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) {
		String exitState = Constants.ER;
		try {
			Double BillingAmount = null;
			String PaymentStatus = "";
			String PaymentRecordType = "";
			String PaymentStatusCode = "";

			Gson gsonobj = new Gson();
			HalloBillingDetails HaloBillingDetails = (HalloBillingDetails) gsonobj.fromJson(strRespBody, HalloBillingDetails.class);
			List<BillingDetail> billingDetails = HaloBillingDetails.getBillingDetails();
			
			

			data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.N_FLAG);
			data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.N_FLAG);
			boolean isLastPmtDetailsAvail = false;
			
			for (BillingDetail billingDetail : billingDetails) {

				//List<com.farmers.bean.Halo.BillingDetails.Term> terms = ((BillingDetail) billingDetails.get(0)).getPolicy().getTerms();
				//if ((((com.farmers.bean.Halo.BillingDetails.Term) terms.get(0)).getBillAmount() != null) && (((com.farmers.bean.Halo.BillingDetails.Term) terms.get(0)).getBillAmount().doubleValue() > 0.0D)) {
				
					//BillingAmount = ((com.farmers.bean.Halo.BillingDetails.Term) terms.get(0)).getBillAmount();
					
					//if ((BillingAmount != null) && (BillingAmount.doubleValue() > 0.0D)) {
						//data.setSessionData(Constants.S_CURRENT_BAL, BillingAmount);
						//data.addToLog(data.getCurrentElement(), Constants.S_CURRENT_BAL + " : " + BillingAmount.toString());
				//	}
			//	}

				List<Payment> paymentsList = billingDetail.getPolicy().getPayments();
				if (paymentsList != null) {
						if (paymentsList.size() > 0) {
							
							Payment payment = paymentsList.get(0);

							if (payment.getPaymentStatusCode() != null) {
								PaymentStatusCode = payment.getPaymentStatusCode();
							}
							if (payment.getPaymentStatus() != null) {
								PaymentStatus = payment.getPaymentStatus();
							}
							if (payment.getPaymentRecordType() != null) {
								PaymentRecordType = payment.getPaymentRecordType();
							}

							if(null != PaymentStatus && null != PaymentRecordType && null != PaymentStatusCode && PaymentStatus.toUpperCase().equalsIgnoreCase("PENDING") && PaymentRecordType.toUpperCase().equalsIgnoreCase("PAY") && PaymentStatusCode.toUpperCase().equalsIgnoreCase("COM") ) {
								String date = "";
								String lastPaymentAmount = removeNegativeSymbol(caa, data, String.valueOf(payment.getPaymentAmount()));
								if(lastPaymentAmount!=null && !"".equalsIgnoreCase(lastPaymentAmount) && !"0.00".equalsIgnoreCase(lastPaymentAmount)) {
									data.setSessionData(Constants.S_LASTPAYMENT_AMOUNT, lastPaymentAmount);
									if (payment.getPaymentDate() != null && payment.getPaymentDate().contains(Constants.HYPHEN)) {
										date = payment.getPaymentDate().replace(Constants.HYPHEN, "");
									}
									data.setSessionData(Constants.S_LASTPAYMENT_DATE, date);
									data.addToLog(data.getCurrentElement(), Constants.S_LASTPAYMENT_AMOUNT + " : " + payment.getPaymentAmount());
									data.addToLog(data.getCurrentElement(), Constants.S_LASTPAYMENT_DATE + " : " + date);
									data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.Y_FLAG);
									isLastPmtDetailsAvail = true;
								}
							
								break;
								
							}
							else if(null != PaymentStatus && null != PaymentRecordType && null != PaymentStatusCode && PaymentStatus.toUpperCase().equalsIgnoreCase("PROCESSED") && PaymentRecordType.toUpperCase().equalsIgnoreCase("PAY") && PaymentStatusCode.toUpperCase().equalsIgnoreCase("STL") ) {
								String date = "";
								String lastPaymentAmount = removeNegativeSymbol(caa, data, String.valueOf(payment.getPaymentAmount()));
								
								if(lastPaymentAmount!=null && !"".equalsIgnoreCase(lastPaymentAmount) && !"0.00".equalsIgnoreCase(lastPaymentAmount)) {
									
									data.setSessionData(Constants.S_LASTPAYMENT_AMOUNT, lastPaymentAmount);
									
									if (payment.getPaymentDate() != null && payment.getPaymentDate().contains(Constants.HYPHEN)) {
										date = payment.getPaymentDate().replace(Constants.HYPHEN, "");
									}
									data.setSessionData(Constants.S_LASTPAYMENT_DATE, date);
									data.addToLog(data.getCurrentElement(), Constants.S_LASTPAYMENT_AMOUNT + " : " + payment.getPaymentAmount());
									data.addToLog(data.getCurrentElement(), Constants.S_LASTPAYMENT_DATE + " : " + date);
									data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.Y_FLAG);
									isLastPmtDetailsAvail = true;
							}
							
						}
						



						/*	Collection<String> strings = new ArrayList();
						strings.add("PENDING");
						strings.add("PROCESSED");
						strings.add("PAY");
						strings.add("COM");
						strings.add("APL");
						strings.add("ATL");
						strings.add("pay");
						strings.add("SCH");

						if ((strings.contains(PaymentStatus)) && (strings.contains(PaymentRecordType))
								&& (strings.contains(PaymentStatusCode))) {
							String date = "";
							data.setSessionData(Constants.S_LASTPAYMENT_AMOUNT, payment.getPaymentAmount());
							if (payment.getPaymentDate() != null
									&& payment.getPaymentDate().contains(Constants.HYPHEN)) {
								date = payment.getPaymentDate().replace(Constants.HYPHEN, "");
							}
							data.setSessionData(Constants.S_LASTPAYMENT_DATE, date);
							data.addToLog(data.getCurrentElement(),
									Constants.S_LASTPAYMENT_AMOUNT + " : " + payment.getPaymentAmount());
							data.addToLog(data.getCurrentElement(), Constants.S_LASTPAYMENT_DATE + " : " + date);

						}*/
					}
				}




			}

			data.addToLog(data.getCurrentElement(), "Is Last Payment Available in Halo : "+isLastPmtDetailsAvail);

			exitState = billpresentmentAfterHalo(data, caa, currElementName, isLastPmtDetailsAvail);



			/*
			 * List<com.farmers.bean.Halo.BillingDetails.Payment> Payment =
			 * ((BillingDetail)billingDetails.get(0)).getPolicy().getPayments(); if
			 * (Payment.size() > 0) { if (Payment.get(0).getPaymentStatusCode() != null) {
			 * PaymentStatusCode = Payment.get(0).getPaymentStatusCode(); } if
			 * (Payment.get(0).getPaymentStatus() != null) { PaymentStatus =
			 * Payment.get(0).getPaymentStatus(); } if
			 * (Payment.get(0).getPaymentRecordType() != null) { PaymentRecordType =
			 * Payment.get(0).getPaymentRecordType(); }
			 * List<com.farmers.bean.Halo.BillingDetails.Term> terms =
			 * ((BillingDetail)billingDetails.get(0)).getPolicy().getTerms(); if
			 * ((((com.farmers.bean.Halo.BillingDetails.Term)terms.get(0)).getBillAmount()
			 * != null) &&
			 * (((com.farmers.bean.Halo.BillingDetails.Term)terms.get(0)).getBillAmount().
			 * doubleValue() > 0.0D)) { BillingAmount =
			 * ((com.farmers.bean.Halo.BillingDetails.Term)terms.get(0)).getBillAmount(); }
			 * Collection<String> strings = new ArrayList(); strings.add("PENDING");
			 * strings.add("PROCESSED"); strings.add("PAY"); strings.add("COM");
			 * strings.add("APL"); strings.add("ATL"); strings.add("pay");
			 * strings.add("SCH"); if ((BillingAmount != null) &&
			 * (BillingAmount.doubleValue() > 0.0D)) {
			 * data.setSessionData("S_Current_Balance", BillingAmount);
			 * data.addToLog("S_Current_Balance", BillingAmount.toString()); } if
			 * ((strings.contains(PaymentStatus)) && (strings.contains(PaymentRecordType))
			 * && (strings.contains(PaymentStatusCode))) {
			 * data.setSessionData("S_LastPayment_Amount",
			 * Payment.get(0).getPaymentAmount());
			 * data.setSessionData("S_LastPayment_Date",Payment.get(0).getPaymentDate());
			 * data.addToLog("S_LastPayment_Amount", ""+Payment.get(0).getPaymentAmount());
			 * data.addToLog("S_LastPayment_Date", Payment.get(0).getPaymentDate());
			 * 
			 * 
			 * exitState = billpresentmentAfterHalo(strRespBody, strRespBody, data, caa,
			 * currElementName, exitState); data.addToLog(data.getCurrentElement(),
			 * "Exit State : " + exitState); } }
			 */

			/*
			 * List<com.farmers.bean.Halo.BillingDetails.Term> term =
			 * ((BillingDetail)billingDetails.get(0)).getPolicy().getTerms(); if
			 * (term.size() > 0) { if (term.get(0).getExpirationDate()!= null) { String
			 * expirationDate = term.get(0).getExpirationDate(); expirationDate =
			 * expirationDate.replace("-", "");
			 * data.setSessionData("S_EXPIRATION_Date",expirationDate);
			 * data.addToLog("S_EXPIRATION_Date", ""+expirationDate); }
			 * 
			 * }
			 */

			data.addToLog(data.getCurrentElement(), "Exit State : " + exitState);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
			caa.printStackTrace(e);
		}
		return exitState;
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
	
	public static void main(String[] args) {
		String LastPaymetDate = "03/22/2024";
		String LastPaymetDateformatted = "";
		LocalDate date;
		if (LastPaymetDate != null && LastPaymetDate.contains("/")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			date = LocalDate.parse(LastPaymetDate, formatter);
			DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
			LastPaymetDateformatted = date.format(formatterOutput);
			System.out.println("Last Payment Date: " + LastPaymetDateformatted);
		}else {
			System.out.println("Last payment date doesnt contain /");
		}

	}


}
