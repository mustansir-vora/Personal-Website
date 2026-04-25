package com.farmers.shared.host;

import java.text.DateFormat;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.farmers.FarmersAPI.FWSPolicyLookup_Post;
import com.farmers.FarmersAPI.PointPolicyInquiry_Get;
import com.farmers.FarmersAPI_NP.FWSPolicyLookup_NP_Post;
import com.farmers.bean.BillPresentMents.AccountAgreementBillingSummaryView;
import com.farmers.bean.BillPresentMents.BillPresentMentRoot;
import com.farmers.bean.BillPresentMents.InsurancePolicyBillingSummaryView;
import com.farmers.bean.BillPresentMents.NextFinancialTransactionCommunicationView;
import com.farmers.bean.FWSBillingLookup.ARS.PolicyBillingLookupARS;
import com.farmers.bean.FWSBillingLookup.PointBilling.PointbillingInquiry;
import com.farmers.bean.FWSBillingLookup.ThreeSixty.PolicyBillingLookup360;
import com.farmers.bean.FWSBillingLookup.ThreeSixty.ReturnMessage;
import com.farmers.bean.FWSBillingLookup.ThreeSixty.Terms;
import com.farmers.bean.Halo.BillingDetails.BillingDetail;
import com.farmers.bean.Halo.BillingDetails.HalloBillingDetails;
import com.farmers.bean.Halo.BillingDetails.Payment;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.google.gson.Gson;

public class Farmers_BillPayments {
	


	String strReqBody = Constants.EmptyString;

	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);


		try {
			
			strExitState = billpresentmentretrieveBillingSummary(data, caa,currElementName);
			String strBU = (String) data.getSessionData(Constants.S_BU);
			String policyNO = (String) data.getSessionData(Constants.S_POLICY_NUM);

			String strBristolCode = (String) data.getApplicationAPI().getApplicationData("A_BRISTOL_LOB");
			String strFarmersCode = (String) data.getApplicationAPI().getApplicationData("A_FARMERS_LOB");
			String strForemostCode = (String) data.getApplicationAPI().getApplicationData("A_FOREMOST_LOB");
			String strFWSCode = (String) data.getApplicationAPI().getApplicationData("A_FWS_LOB");
			String str21stCode = (String) data.getApplicationAPI().getApplicationData("A_21ST_LOB");

			data.addToLog(currElementName, " PolicyLookup : strBU :: " + strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : " + strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : " + strFarmersCode);
			data.addToLog(currElementName, " A_FWS_LOB : " + strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : " + str21stCode);

			data.addToLog(data.getCurrentElement(), "BU : " + strBU + " Policy No : " + policyNO);
			if (strFarmersCode != null && strBU != null && strFarmersCode.contains(strBU)
					|| strBU.equalsIgnoreCase(Constants.S_API_BU_FDS) || strBU.contains(Constants.S_API_BU_FDS)
					|| Constants.S_API_BU_FDS.contains(strBU)) {
				strBU = Constants.BU_FARMERS;
				strExitState = billpresentmentretrieveBillingSummary(data, caa,currElementName);
			} else if (strForemostCode != null && strBU != null && strForemostCode.contains(strBU)) {
				strExitState = epcHaloBillingDetailsGroup(data, caa, currElementName);
				strBU = Constants.BU_FOREMOST;
			} else if (strFWSCode != null && strBU != null
					&& (strFWSCode.contains(strBU) || strBU.contains(Constants.FWS))) {
				String exitState = fwsPolicyLookup(data, caa, currElementName);
				if (exitState.equalsIgnoreCase(Constants.SU)) {
					strExitState = FWSBillingLookup(data, caa, currElementName);
				}
				strBU = Constants.BU_FWS;
			} else if (str21stCode != null && strBU != null && str21stCode.contains(strBU)) {
				strExitState = pointgeneralbilling(data, caa, currElementName);
				strBU = Constants.BU_21ST;
			}else if (strBristolCode != null && strBU != null && strBristolCode.contains(strBU)) {
				strExitState = Constants.BU_BW;
				strBU = Constants.BU_BW;
			}  
			else {
				strExitState = Constants.BU_NOT_MATCH;
			}

			data.setSessionData("S_UNIQUE_BU", strBU);
			data.addToLog(data.getCurrentElement(), "EXIT STATE : " + strExitState);
			if (strExitState.equalsIgnoreCase(Constants.ER)) {
				//String msp = strBU + ":" + currElementName + ":" + strExitState;
				//data.addToLog(data.getCurrentElement(), "MSP Key : " + msp);
				//data.setSessionData(Constants.S_MENU_SELCTION_KEY, msp);
				caa.createMSPKey(caa, data, "SBP_HOST_001", "ER");
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for SBP_HOST_001  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String billpresentmentretrieveBillingSummary(DecisionElementData data, CommonAPIAccess caa, String currElementName) {
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
			data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.N_FLAG);
			data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.N_FLAG);

			if ((data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL) != null)
					&& (data.getSessionData(Constants.S_READ_TIMEOUT) != null)
					&& (data.getSessionData(Constants.S_CONN_TIMEOUT) != null)
					&& (data.getSessionData(Constants.S_POLICY_NUM) != null)) {
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
			//UAT ENV CHANGE END(SHAIK,PRIYA)
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
								currElementName, strRespBody, false, false);

					} else {
						if(responses.get(Constants.RESPONSE_MSG)!=null)
							strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception in KYCAF_HOST_001  billpresentmentretrieveBillingSummary API call  :: " + e);
			caa.printStackTrace(e);
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

			if ((data.getSessionData(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL) != null) && (data.getSessionData(Constants.S_CONN_TIMEOUT) != null)
					&& (data.getSessionData(Constants.S_READ_TIMEOUT) != null)) {
				String wsurl = (String) data.getSessionData(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String PolicyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String Actortype = "C";
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);

				data.addToLog(data.getCurrentElement(), "S_EPCHALO_BILLING_DETAILSGROUP_URL URL : " + wsurl);

				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE START(PRIYA,SHAIK)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				 region = regionDetails.get(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL);
				}else {
					region="PROD";
				}
				JSONObject responses = lookups.GetEPCHaloBillingDetailsGroup_Post(wsurl, tid, PolicyNumber, Actortype,
						Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context,region,UAT_FLAG);
				//UAT ENV CHANGEEND(PRIYA,SHAIK)
				data.addToLog(data.getCurrentElement(), "SBP_HOST_001 RESPONSE : " + responses.toString());
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if (responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if ((responses.containsKey(Constants.RESPONSE_CODE)) && (((Integer) responses.get(Constants.RESPONSE_CODE)) == 200)
							&& (responses.containsKey(Constants.RESPONSE_BODY))) {
						data.addToLog(currElementName,
								"Set KYCBA_HOST_001 : epcHaloBillingDetailsGroup API Response into session with the key name of "
										+ currElementName + "_RESP");
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + "_RESP", responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_epcHaloBillingDetailsGroup(data, caa, currElementName,
								strRespBody);
					}  else {
						if(responses.get(Constants.RESPONSE_MSG)!=null)
							strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
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
			objHostDetails.startHostReport(currElementName, "epcHaloBillingDetailsGroup", strReqBody, region,(String) data.getSessionData(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL));
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
			if ((data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL) != null)
					&& (data.getSessionData(Constants.S_READ_TIMEOUT) != null)
					&& (data.getSessionData(Constants.S_CONN_TIMEOUT) != null)
					&& (data.getSessionData(Constants.S_POLICY_NUM) != null)) {
				String wsurl = (String) data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String appType = "VRS-Foremost-101";
				String sysName = "VRS-Foremost";
				boolean loopback = false;
				String productGroupName = "FAB";
				String PolicyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT)).intValue();

				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);

				data.addToLog(data.getCurrentElement(), "wsurl : " + wsurl);
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE START(PRIYA,SHAIK)
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
				data.addToLog("responses", responses.toString());
				//UT ENV CHANGE END(SHAIK,PRIYA)
				data.addToLog("responses", responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

				if (responses != null) {
					if ((responses.containsKey(Constants.RESPONSE_CODE))
							&& (((Integer) responses.get(Constants.RESPONSE_CODE)) == 200)
							&& (responses.containsKey(Constants.RESPONSE_BODY))) {
						data.addToLog(currElementName,
								"Set KYCBA_HOST_001 : billpresentmentretrieveBillingSummary API Response into session with the key name of "
										+ currElementName + "_RESP");
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + "_RESP", responses.get(Constants.RESPONSE_BODY));
						exitState = apiResponseManupulation_billpresentmentretrieveBillingSummaryHalo(data, caa,
								currElementName, strRespBody, true, isLastPaymentDetailAvail);
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
			objHostDetails.startHostReport(currElementName, "billpresentmentAfterHalo", strReqBody, region,(String) data.getSessionData(Constants.S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL));
			objHostDetails.endHostReport(data, strRespBody, exitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			data.addToLog(currElementName, "Reporting ENDS for billpresentmentAfterHalo : " + objHostDetails);
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for SBP_HOST_001_FOREMOST  epcHaloBillingDetailsGroup call  :: " + e1);
			caa.printStackTrace(e1);
		}
		return exitState;
	}


	private String apiResponseManupulation_billpresentmentretrieveBillingSummaryHalo(DecisionElementData data,
			CommonAPIAccess caa, String currElementName, String strRespBody, boolean isComingFromHalo, boolean isLastPmtAvail) {
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
			BillPresentMentRoot billpresentroot = (BillPresentMentRoot) gsonobj.fromJson(strRespBody,
					BillPresentMentRoot.class);

			data.addToLog(data.getCurrentElement(), billpresentroot.toString());
			AccountAgreementBillingSummaryView objAcctAgmtBillSummView = billpresentroot
					.getRetrieveBillingSummaryResponse().getBillingSummary().getAccountAgreementBillingSummaryView();
			if (objAcctAgmtBillSummView != null && objAcctAgmtBillSummView.getBillingAllocatedPaymentView() != null) {
				
				if (null != objAcctAgmtBillSummView && null != objAcctAgmtBillSummView.getAccountAgreementAnalyticsView().getpostalAddress().getpostalCode()) {
					billingzipcode = objAcctAgmtBillSummView.getAccountAgreementAnalyticsView().getpostalAddress().getpostalCode();
					data.setSessionData("S_PAYOR_ZIP_CODE", billingzipcode);
					data.addToLog(currElementName, "Setting Billing zip code for EPCPAYMENTUS into session :: "+data.getSessionData("S_PAYOR_ZIP_CODE"));
				}
				/*
				lastPaymentDate = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getPaymentDate();
				lastPaymentAmount = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getAmount().getTheCurrencyAmount();
				 */
				if (objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView() != null) {
					List<InsurancePolicyBillingSummaryView> objInsurancePolicyBillSummView = objAcctAgmtBillSummView
							.getInsurancePolicyBillingSummaryView();
					for (InsurancePolicyBillingSummaryView insurancePolicyBillingSummaryView : objInsurancePolicyBillSummView) {
						if (isComingFromHalo) {
							policyStatus = insurancePolicyBillingSummaryView.getInsurancePolicy().getStatus()
									.getDescription();
						} else {
							policyStatus = insurancePolicyBillingSummaryView.getInsurancePolicy().getStatus()
									.getState();

						}
						if (isComingFromHalo && policyStatus.contains(Constants.EXPIRED)) {
							//policyExpiryDate = insurancePolicyBillingSummaryView.getInsurancePolicy().getEndDate();
							policyExpiryDate = objAcctAgmtBillSummView.getBasicBillingAccountAgreementView().getEndDate();
						}
					}
				}



				NextFinancialTransactionCommunicationView objNxtFinTransCommView = objAcctAgmtBillSummView.getNextFinancialTransactionCommunicationView();
				if(objNxtFinTransCommView != null && objNxtFinTransCommView.getBillingPostedPaymentDueView() != null && objNxtFinTransCommView.getBillingPostedPaymentDueView().getPaymentDue() !=null) { 
					nextPaymentDate =  objNxtFinTransCommView.getBillingPostedPaymentDueView().getPaymentDue().getDueDate(); 
					nextPaymentAmount = objNxtFinTransCommView.getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount(); 
					currentBalance = objNxtFinTransCommView.getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount(); 
				}



				if(objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView() != null &&  objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView().get(0).getInsurancePolicy().getCumulativePremiumAmount().getTheCurrencyAmount() != null) {
					renewalBalance = objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView().get(0).getInsurancePolicy().getCumulativePremiumAmount().getTheCurrencyAmount();

				}

				if (objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView() != null && objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount() != null) {
					totalOutStandAmt = objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount();
				}
				
				//As per Tauzia (Hypercare #18 issue), we need to consider previousFinancialTransactionCommunicationView obj for Foremost
				if (null != objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView()
						&& null != objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView()
						&& null != objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue()) {
					if(null != objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getDueDate()) {
						lastPaymentDate = objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getDueDate();
					}
					if(null != objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getAmount() 
							&& null != objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount()) {
						lastPaymentAmount = objAcctAgmtBillSummView.getPreviousFinancialTransactionCommunicationView().getBillingPostedPaymentDueView().getPaymentDue().getAmount().getTheCurrencyAmount();
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
			data.addToLog(data.getCurrentElement(),
					"Policy Expiry Date will applicable only for Foremost BU : " + policyExpiryDate);
			data.addToLog(data.getCurrentElement(), Constants.S_LAST_PMNT_AVAIL+" : "+lastpaymentFlag);

			data.setSessionData(Constants.S_LESS_THAN_DUE_DATE, Constants.N_FLAG);
			if ((policyStatus.equalsIgnoreCase(Constants.ACTIVE))) {
				data.setSessionData(Constants.S_POLICY_STATUS, Constants.ACTIVE);
			} else if (policyStatus.contains(Constants.CANCEL)) {
				data.setSessionData(Constants.S_POLICY_STATUS, Constants.CANCELLED);
			} else if (policyStatus.contains(Constants.EXPIRED)) {
				data.setSessionData(Constants.S_POLICY_STATUS, Constants.EXPIRED);
			} else {
				data.setSessionData(Constants.S_POLICY_STATUS, Constants.ACTIVE);
			}

			data.setSessionData(Constants.S_BILLINGINFO_RES, Constants.N_FLAG);
			data.setSessionData(Constants.S_CURRENT_BAL, currentBalance);


			if ((lastPaymentDate != null) && (!lastPaymentDate.equals("")) && (lastPaymentAmount != null)
					&& (!lastPaymentAmount.equals("")) && (!isLastPmtAvail)) {
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
			if ((nextPaymentDate != null) && (!nextPaymentDate.equals("")) && (nextPaymentAmount != null)
					&& (!nextPaymentAmount.equals(""))) {
				String nextAmount = removeNegativeSymbol(caa, data, nextPaymentAmount);
				data.setSessionData(Constants.S_NEXTPAYMENT_AMOUNT, nextAmount);

				SimpleDateFormat sdf = new SimpleDateFormat(Constants.PAYMENT_DATE_FORMAT);
				Date nextPmtDate = sdf.parse(nextPaymentDate);
				Date systemDate = sdf.parse(new SimpleDateFormat(Constants.PAYMENT_DATE_FORMAT).format(new Date()));

				if (nextPmtDate.after(systemDate) || nextPmtDate.equals(systemDate)) {
					data.setSessionData(Constants.S_LESS_THAN_DUE_DATE, Constants.Y_FLAG);

				}
				if (nextPaymentDate.contains(Constants.HYPHEN)) {
					nextPaymentDate = nextPaymentDate.replaceAll(Constants.HYPHEN, "");
				}
				data.setSessionData(Constants.S_NEXTPAYMENT_DATE, nextPaymentDate);
				data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.Y_FLAG);
				strExitState = Constants.SU;

			}
			data.setSessionData(Constants.S_TOT_OUT_AMT, totalOutStandAmt);
			if (policyExpiryDate != null && !policyExpiryDate.equals("") && policyStatus.contains(Constants.EXPIRED)) {
				policyExpiryDate = policyExpiryDate.replace("-", "");
				data.setSessionData(Constants.S_POLICY_EXPIRATION_DATE, policyExpiryDate);
				strExitState = Constants.SU;
			}

			data.setSessionData(Constants.S_NEXT_PAYMENT_ALLOWED, Constants.Y_FLAG);
			// data.addToLog("S_Current_Balance", currentBalance);
			data.setSessionData("S_Renewal_Balance", renewalBalance);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String fwsPolicyLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String exitState = Constants.ER;
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

			if (data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);

				String url = (String) data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL);
				String callID = (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT));

				data.addToLog(data.getCurrentElement(),
						"POLICY NUMBER : " + data.getSessionData(Constants.S_POLICY_NUM));
				data.addToLog(data.getCurrentElement(),
						"BILLING ACCT NUMBER : " + data.getSessionData(Constants.S_BILLING_ACC_NUM));

				JSONObject responseJSON = null;
				FWSPolicyLookup_Post test = new FWSPolicyLookup_Post();

				
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
				FWSPolicyLookup_NP_Post testNP = new FWSPolicyLookup_NP_Post();
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					region = regionDetails.get(Constants.S_FWS_POLICY_LOOKUP_URL);
					if (data.getSessionData(Constants.S_POLICY_NUM) != null) {
						String policyNum = (String) data.getSessionData(Constants.S_POLICY_NUM);
						data.addToLog(data.getCurrentElement(), "Policy Number " + policyNum);
						responseJSON = testNP.start(url, callID, policyNum, null, null, conTimeout, readTimeout, context,region);
					} else if (data.getSessionData(Constants.S_BILLING_ACC_NUM) != null) {
						String billingAcctNum = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
						data.addToLog(data.getCurrentElement(), "Billing Account Number " + billingAcctNum);
						responseJSON = testNP.start(url, callID, null, billingAcctNum, null, conTimeout, readTimeout,
								context,region);
					} else {
						String ani = (String) data.getSessionData(Constants.S_ANI);
						responseJSON = testNP.start(url, callID, null, null, ani, conTimeout, readTimeout, context,region);
					}

					}else {
						region="PROD";
						if (data.getSessionData(Constants.S_POLICY_NUM) != null) {
							String policyNum = (String) data.getSessionData(Constants.S_POLICY_NUM);
							data.addToLog(data.getCurrentElement(), "Policy Number " + policyNum);
							responseJSON = test.start(url, callID, policyNum, null, null, conTimeout, readTimeout, context);
						} else if (data.getSessionData(Constants.S_BILLING_ACC_NUM) != null) {
							String billingAcctNum = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
							data.addToLog(data.getCurrentElement(), "Billing Account Number " + billingAcctNum);
							responseJSON = test.start(url, callID, null, billingAcctNum, null, conTimeout, readTimeout,
									context);
						} else {
							String ani = (String) data.getSessionData(Constants.S_ANI);
							responseJSON = test.start(url, callID, null, null, ani, conTimeout, readTimeout, context);
						}

					}

				data.addToLog(data.getCurrentElement(), "API Response : " + responseJSON);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responseJSON.get(Constants.RESPONSE_CODE));

				if (responseJSON != null && responseJSON.containsKey(Constants.RESPONSE_CODE)
						&& (int) responseJSON.get(Constants.RESPONSE_CODE) == 200
						&& responseJSON.containsKey(Constants.RESPONSE_BODY)
						&& responseJSON.get(Constants.RESPONSE_BODY) != null) {
					data.addToLog(data.getCurrentElement(),
							"Set FWS Policy Lookup API Response into session with the key name of "
									+ data.getCurrentElement() + Constants._RESP);
					strRespBody = responseJSON.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(data.getCurrentElement() + Constants._RESP,
							responseJSON.get(Constants.RESPONSE_BODY));
					exitState = policyResponseManipulation(data, caa, strRespBody);
				}

			}

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in Validate_FWSARC_HOST_001 :: " + e);
			caa.printStackTrace(e);
		}
		try {
			data.addToLog(currElementName, "Reporting STARTS for FWS Policy LookUp");
			objHostDetails.startHostReport(currElementName, "SBP_HOST_001_FWS_POLICY", strReqBody,region, (String) data.getSessionData(Constants.S_FWS_POLICY_LOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody, exitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			data.addToLog(currElementName, "Reporting ENDS for FWS Policy LookUp");
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for Validate_FWSARC_HOST_001  fwsPolicyLookup call  :: " + e1);
			caa.printStackTrace(e1);
		}
		return exitState;
	}

	private String policyResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String strRespBody) {
		String exitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			if (resp.containsKey("policies")) {
				JSONArray policies = (JSONArray) resp.get("policies");
				for (Object policyObj : policies) {
					JSONObject policy = (JSONObject) policyObj;
					String lineOfBusiness = (String) policy.get("lineOfBusiness");
					String billingAcctNo = (String) policy.get("billingAccountNumber");
					String companyCode = "";
					String acctNo = "";
					if (billingAcctNo.length() > 2) {
						companyCode = billingAcctNo.substring(0, 2);
						// acctNo = billingAcctNo.substring(2, billingAcctNo.length());
					}
					String internalPolicyNo = (String) policy.get("InternalPolicyNumber");
					String policySource = (String) policy.get("policySource");
					String suffix = (String) policy.get("suffix");
					// String gpc = (String) policy.get("GPC");
					String policyState = (String) policy.get("policyState");
					data.setSessionData(Constants.S_POLICY_STATE_CODE, policyState);
					String policyStatus = (String) policy.get("policyStatus");
					String effectiveDate = (String) policy.get("effectiveDate");
					if (effectiveDate.length() >= 10) {
						effectiveDate = effectiveDate.substring(0, 10);
					}

					data.addToLog(data.getCurrentElement(), "policyState : " + policyState);
					data.addToLog(data.getCurrentElement(), "lineOfBusiness : " + lineOfBusiness);
					data.addToLog(data.getCurrentElement(), "companyCode : " + companyCode);
					data.addToLog(data.getCurrentElement(), "acctNo : " + billingAcctNo);
					data.addToLog(data.getCurrentElement(), "internalPolicyNo : " + internalPolicyNo);
					data.addToLog(data.getCurrentElement(), "policySource : " + policySource);
					data.addToLog(data.getCurrentElement(), "suffix : " + suffix);
					data.addToLog(data.getCurrentElement(), "effectiveDate : " + effectiveDate);
					data.addToLog(data.getCurrentElement(), "Policy Status : " + policyStatus);

					data.setSessionData(Constants.S_POLICY_STATE, policyState);
					data.setSessionData(Constants.S_FWS_POLICY_LOB, lineOfBusiness);
					data.setSessionData(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
					data.setSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingAcctNo);
					data.setSessionData(Constants.S_FWS_INT_POLICY_NO, internalPolicyNo);
					data.setSessionData(Constants.S_POLICY_SOURCE, policySource);
					data.setSessionData(Constants.S_FWS_POLICY_SUFFIX, suffix);
					// data.setSessionData(Constants.S_FWS_POLICY_GPC, gpc);
					data.setSessionData(Constants.S_FWS_POLICY_EFF_DATE, effectiveDate);
					if (policyStatus.equalsIgnoreCase(Constants.ACTIVE)) {
						data.setSessionData(Constants.S_POLICY_STATUS, Constants.ACTIVE);
					} else {
						data.setSessionData(Constants.S_POLICY_STATUS, Constants.CANCELLED);
					}

					exitState = Constants.SU;

				}
			}
			data.addToLog(data.getCurrentElement(), "FWS POLICY LOOKUP API  EXIT STATE : " + exitState);

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in policyResponseManipulation method  :: " + e);
			caa.printStackTrace(e);
		}
		String currElementName=data.getCurrentElement();
		return exitState;
	}

	private String FWSBillingLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName) {
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
			data.addToLog(data.getCurrentElement(), "Entering into FWS Billing Lookup");

			if ((data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL) != null)
					&& (data.getSessionData(Constants.S_READ_TIMEOUT) != null)
					&& (data.getSessionData(Constants.S_CONN_TIMEOUT) != null)) {
				String wsurl = (String) data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL);
				String callId = (String) data.getSessionData(Constants.S_CALLID);
				String billingAccountNumber = (String) data.getSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO);
				String companyCode = "";
				if(billingAccountNumber.length() > 2) {
					companyCode = billingAccountNumber.substring(0, 2);
					// acctNo = billingAccountNumber.substring(2, billingAccountNumber.length());
				}
				if (billingAccountNumber.length() >= 11) {
					billingAccountNumber = billingAccountNumber.substring(2, billingAccountNumber.length());
				}
				String policyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String lob = (String) data.getSessionData(Constants.S_FWS_POLICY_LOB);
				String policysource = (String) data.getSessionData(Constants.S_POLICY_SOURCE);

				if (!policysource.equalsIgnoreCase("ARS")) {
					// IF POLICY SOURCE IS A360 or M360. THEN IVR SHOULD concat the policyno along
					// with lob
					policyNumber = lob + policyNumber;
				}
				String effectiveDate = (String) data.getSessionData(Constants.S_FWS_POLICY_EFF_DATE);
				String suffix = (String) data.getSessionData(Constants.S_FWS_POLICY_SUFFIX);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String policystate = (String) data.getSessionData(Constants.S_POLICY_STATE);
				wsurl = wsurl.replace(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingAccountNumber)
						.replace(Constants.S_FWS_INT_POLICY_NO, policyNumber)
						.replace(Constants.S_FWS_POLICY_EFF_DATE, effectiveDate)
						.replace(Constants.S_FWS_POLICY_SUFFIX, suffix)
						.replace(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);

				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);

				data.addToLog(currElementName, "URl : " + wsurl);
				data.addToLog(currElementName, "CALL ID : " + callId);
				data.addToLog(currElementName, "Billing Acct No : " + billingAccountNumber);
				data.addToLog(currElementName, "Company Code : " + companyCode);
				data.addToLog(currElementName, "Policy Number : " + policyNumber);

				data.addToLog(currElementName, "Eff Date : " + effectiveDate);
				data.addToLog(currElementName, "Suffix : " + suffix);
				data.addToLog(currElementName, "Policy Source " + policysource);
				data.addToLog(currElementName, "Policy State :" + policystate);

				Lookupcall lookups = new Lookupcall();
				//UT ENV CHANGE(PRIYA,SHAIK)
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

				JSONObject responses = lookups.GetFWSBillingLookup(wsurl, callId, policysource, policystate, lob,
						Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
				
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if (responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if ((responses.containsKey(Constants.RESPONSE_CODE)) && ((Integer) responses.get(Constants.RESPONSE_CODE)) == 200
							&& (responses.containsKey("responseBody"))) {
						data.addToLog(currElementName,
								"Set KYCBA_HOST_001 : ForemostPolicyInquiry  PI Response into session with the key name of "
										+ currElementName + "_RESP");
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + "_RESP", responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_FWSBillingLookup(data, caa, currElementName,
								strRespBody);
					}  else {
						if(responses.get(Constants.RESPONSE_MSG)!=null)
							strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
				}
			} else {
				data.addToLog(currElementName, "Mandatory field missing");
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in KYCAF_HOST_001  ForemostPolicyInquiry API call  :: " + e);
			caa.printStackTrace(e);
		}
		try {
			data.addToLog(currElementName, "Reporting STARTS for FWS Billing LookUp");
			objHostDetails.startHostReport(currElementName, "SBP_HOST_001_FWS_BILLING_LOOKUP", strReqBody, region,(String) data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			data.addToLog(currElementName, "Reporting ENDS for FWS Billing LookUp");
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for SBP_HOST_001_FWS_BILLING_LOOKUP  FWSBillingLookup call  :: " + e1);
			caa.printStackTrace(e1);
		}
		return strExitState;
	}

	private String pointgeneralbilling(DecisionElementData data, CommonAPIAccess caa, String currElementName) {
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
			data.addToLog(currElementName, "Entering into Point General Billing API ");
			data.addToLog(currElementName, "" + data.getSessionData(Constants.S_POINTGENERAL_BILLING_URL));

			if ((data.getSessionData(Constants.S_POINTGENERAL_BILLING_URL) != null)
					&& (data.getSessionData(Constants.S_READ_TIMEOUT) != null)
					&& (data.getSessionData(Constants.S_CONN_TIMEOUT) != null)) {
				String wsurl = (String) data.getSessionData(Constants.S_POINTGENERAL_BILLING_URL);
				String policyNo = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String policymod = policyNo.substring(policyNo.length() - 2, policyNo.length());
				String policymco = (String) data.getSessionData("S_POLICY_MCO");
				data.addToLog(data.getCurrentElement(), "POLICY INQUIRY MCO : " + policymco);
				data.addToLog(data.getCurrentElement(), "POLICY INQUIRY MOD : " + policymod);

				wsurl = wsurl.replace(Constants.S_POLICY_CONTRACT_NUM, policyNo).replace("S_POLICY_MOD", policymod)
						.replace("S_POLICY_MCO", policymco);

				data.addToLog(data.getCurrentElement(), "URL : " + wsurl);
				data.addToLog(data.getCurrentElement(), "S_POLICY_MOD : " + policymod);
				data.addToLog(data.getCurrentElement(), "S_POLICY_MCO : " + policymco);
				data.addToLog(data.getCurrentElement(), "S_POLICY_NUM : " + policyNo);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);

				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);

				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE(PRIYA,SHAIK)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
             region = regionDetails.get(Constants.S_POINTGENERAL_BILLING_URL);
				}else {
					region="";
				}
				JSONObject responses = lookups.Getpointgeneralbilling(wsurl, tid,
						Integer.valueOf(Integer.parseInt(conntimeout)), Integer.valueOf(Integer.parseInt(readtimeout)),
						context,region,UAT_FLAG);
			//UAT ENV CHANGE END(SHAIK,PRIYA)
				data.addToLog("responses", responses.toString());
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if (responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if ((responses.containsKey(Constants.RESPONSE_CODE)) && (((Integer) responses.get(Constants.RESPONSE_CODE)) == 200)
							&& (responses.containsKey(Constants.RESPONSE_BODY))) {
						data.addToLog(currElementName,
								"Set KYCBA_HOST_001 : MulesoftFarmerPolicyInquiry_Post API Response into session with the key name of "
										+ currElementName + "_RESP");
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + "_RESP", responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_pointgeneralbilling(data, caa, currElementName,
								strRespBody);
					}  else {
						if(responses.get(Constants.RESPONSE_MSG)!=null)
							strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}

				}
			} else {
				data.addToLog(data.getCurrentElement(), "Mandatory fields are missing");
			}
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception in KYCAF_HOST_001  MulesoftFarmerPolicyInquiry_Post API call  :: " + e);
			caa.printStackTrace(e);
		}
		try {
			data.addToLog(currElementName, "Reporting STARTS for 21st Billing LookUp");
			objHostDetails.startHostReport(currElementName, "SBP_HOST_001_21st", strReqBody,region, (String) data.getSessionData(Constants.S_POINTGENERAL_BILLING_URL));
			objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			data.addToLog(currElementName, "Reporting ENDS for 21st Billing LookUp :: " + objHostDetails);
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for SBP_HOST_001_21st  pointgeneralbilling call  :: " + e1);
			caa.printStackTrace(e1);
		}
		return strExitState;
	}

	private String apiResponseManupulation_billpresentmentretrieveBillingSummary(DecisionElementData data,
			CommonAPIAccess caa, String currElementName, String strRespBody, boolean isComingFromHalo, boolean isLastPmtAvail) {
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
			String lastpaymentFlag = (String) data.getSessionData(Constants.S_LAST_PMNT_AVAIL);
			BillPresentMentRoot billpresentroot = (BillPresentMentRoot) gsonobj.fromJson(strRespBody,
					BillPresentMentRoot.class);

			data.addToLog(data.getCurrentElement(), billpresentroot.toString());
			AccountAgreementBillingSummaryView objAcctAgmtBillSummView = billpresentroot
					.getRetrieveBillingSummaryResponse().getBillingSummary().getAccountAgreementBillingSummaryView();
			
			if (null != objAcctAgmtBillSummView && null != objAcctAgmtBillSummView.getBasicPartyContactPointDetails().getPostalAddress()) {
				billingzipcode = objAcctAgmtBillSummView.getBasicPartyContactPointDetails().getPostalAddress().getPostalCode();
				data.setSessionData("S_PAYOR_ZIP_CODE", billingzipcode);
				data.addToLog(currElementName, "Setting Billing Zip code for EPCPAYMENTUS into session :: "+data.getSessionData(billingzipcode));
			}
			
			if (objAcctAgmtBillSummView != null && objAcctAgmtBillSummView.getBillingAllocatedPaymentView() != null) {

				lastPaymentDate = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment()
						.getReceivedDate();
				lastPaymentAmount = objAcctAgmtBillSummView.getBillingAllocatedPaymentView().getPayment().getAmount()
						.getTheCurrencyAmount();
				data.addToLog(data.getCurrentElement(), "Actual lastPaymentAmount : "+lastPaymentAmount);

				/*
				 * NextFinancialTransactionCommunicationView objNxtFinTransCommView =
				 * objAcctAgmtBillSummView.getNextFinancialTransactionCommunicationView();
				 * if(objNxtFinTransCommView != null &&
				 * objNxtFinTransCommView.getBillingPostedPaymentDueView() != null &&
				 * objNxtFinTransCommView.getBillingPostedPaymentDueView().getPaymentDue() !=
				 * null) { nextPaymentDate =
				 * objNxtFinTransCommView.getBillingPostedPaymentDueView().getPaymentDue().
				 * getDueDate(); nextPaymentAmount =
				 * objNxtFinTransCommView.getBillingPostedPaymentDueView().getPaymentDue().
				 * getAmount().getTheCurrencyAmount(); }
				 */
				if (objAcctAgmtBillSummView.getInsurancePolicyBillingSummaryView() != null) {
					List<InsurancePolicyBillingSummaryView> objInsurancePolicyBillSummView = objAcctAgmtBillSummView
							.getInsurancePolicyBillingSummaryView();
					for (InsurancePolicyBillingSummaryView insurancePolicyBillingSummaryView : objInsurancePolicyBillSummView) {
						if (isComingFromHalo) {
							policyStatus = insurancePolicyBillingSummaryView.getInsurancePolicy().getStatus()
									.getDescription();
						} else {
							policyStatus = insurancePolicyBillingSummaryView.getInsurancePolicy().getStatus()
									.getState();

						}
						if (isComingFromHalo && policyStatus.contains(Constants.EXPIRED)) {
							//policyExpiryDate = insurancePolicyBillingSummaryView.getInsurancePolicy().getEndDate();
							policyExpiryDate = objAcctAgmtBillSummView.getBasicBillingAccountAgreementView().getEndDate();
						}
					}
				}
				if (objAcctAgmtBillSummView.getBasicBillingAccountAgreementView() != null && objAcctAgmtBillSummView
						.getBasicBillingAccountAgreementView().getMinimumPaymentDueAmount() != null) {
					currentBalance = objAcctAgmtBillSummView.getBasicBillingAccountAgreementView()
							.getMinimumPaymentDueAmount().getTheCurrencyAmount();
					nextPaymentAmount = currentBalance;
					nextPaymentAmount = removeNegativeSymbol(caa, data, currentBalance);
					nextPaymentDate = objAcctAgmtBillSummView.getBasicBillingAccountAgreementView().getPaymentDueDate();
				}

				if (objAcctAgmtBillSummView.getBasicBillingAccountAgreementView() != null && objAcctAgmtBillSummView
						.getBasicBillingAccountAgreementView().getOutStandingAmount() != null) {
					totalOutStandAmt = objAcctAgmtBillSummView.getBasicBillingAccountAgreementView()
							.getOutStandingAmount().getTheCurrencyAmount();
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
			data.addToLog(data.getCurrentElement(),
					"Policy Expiry Date will applicable only for Foremost BU : " + policyExpiryDate);
			data.addToLog(data.getCurrentElement(), Constants.S_LAST_PMNT_AVAIL+" : "+lastpaymentFlag);

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

			data.setSessionData(Constants.S_BILLINGINFO_RES, Constants.N_FLAG);
			data.setSessionData(Constants.S_CURRENT_BAL, currentBalance);
			data.setSessionData("S_Renewal_Balance", "0");

			if ((lastPaymentDate != null) && (!lastPaymentDate.equals("")) && (lastPaymentAmount != null)
					&& (!lastPaymentAmount.equals("")) && (!isLastPmtAvail) && !"0.00".equalsIgnoreCase(lastPaymentAmount)) {
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
			if ((nextPaymentDate != null) && (!nextPaymentDate.equals("")) && (nextPaymentAmount != null)
					&& (!nextPaymentAmount.equals("")&& !"0.00".equalsIgnoreCase(nextPaymentAmount))) {
				String nextAmount = removeNegativeSymbol(caa, data, nextPaymentAmount);
				data.setSessionData(Constants.S_NEXTPAYMENT_AMOUNT, nextAmount);

				SimpleDateFormat sdf = new SimpleDateFormat(Constants.PAYMENT_DATE_FORMAT);
				Date nextPmtDate = sdf.parse(nextPaymentDate);
				Date systemDate = sdf.parse(new SimpleDateFormat(Constants.PAYMENT_DATE_FORMAT).format(new Date()));
				
				if (nextPmtDate.after(systemDate) || nextPmtDate.equals(systemDate)) {
					data.setSessionData(Constants.S_LESS_THAN_DUE_DATE, Constants.Y_FLAG);

				}
				if (nextPaymentDate.contains(Constants.HYPHEN)) {
					nextPaymentDate = lastPaymentDate.replaceAll(Constants.HYPHEN, "");
				}
				data.setSessionData(Constants.S_NEXTPAYMENT_DATE, nextPaymentDate);
				data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.Y_FLAG);
				strExitState = Constants.SU;

			}
			data.setSessionData(Constants.S_TOT_OUT_AMT, totalOutStandAmt);
			if (policyExpiryDate != null && !policyExpiryDate.equals("") && policyStatus.contains(Constants.EXPIRED)) {
				policyExpiryDate = policyExpiryDate.replace("-", "");
				data.setSessionData(Constants.S_POLICY_EXPIRATION_DATE, policyExpiryDate);
				strExitState = Constants.SU;
			}

			data.setSessionData(Constants.S_NEXT_PAYMENT_ALLOWED, Constants.Y_FLAG);
			// data.addToLog("S_Current_Balance", currentBalance);
			data.addToLog("S_Renewal_Balance", "0");
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
			HalloBillingDetails HaloBillingDetails = (HalloBillingDetails) gsonobj.fromJson(strRespBody,
					HalloBillingDetails.class);
			List<BillingDetail> billingDetails = HaloBillingDetails.getBillingDetails();

			data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.N_FLAG);
			data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.N_FLAG);
			boolean isLastPmtDetailsAvail = false;
			for (BillingDetail billingDetail : billingDetails) {

				List<com.farmers.bean.Halo.BillingDetails.Term> terms = ((BillingDetail) billingDetails.get(0))
						.getPolicy().getTerms();
				if ((((com.farmers.bean.Halo.BillingDetails.Term) terms.get(0)).getBillAmount() != null)
						&& (((com.farmers.bean.Halo.BillingDetails.Term) terms.get(0)).getBillAmount()
								.doubleValue() > 0.0D)) {
					BillingAmount = ((com.farmers.bean.Halo.BillingDetails.Term) terms.get(0)).getBillAmount();
					if ((BillingAmount != null) && (BillingAmount.doubleValue() > 0.0D)) {
						data.setSessionData(Constants.S_CURRENT_BAL, BillingAmount);
						data.addToLog(data.getCurrentElement(),
								Constants.S_CURRENT_BAL + " : " + BillingAmount.toString());
					}
				}



				List<Payment> paymentsList = billingDetail.getPolicy().getPayments();
				if (paymentsList != null) {
					for (Payment payment : paymentsList) {

						if (payment.getPaymentStatusCode() != null) {
							PaymentStatusCode = payment.getPaymentStatusCode();
						}
						if (payment.getPaymentStatus() != null) {
							PaymentStatus = payment.getPaymentStatus();
						}
						if (payment.getPaymentRecordType() != null) {
							PaymentRecordType = payment.getPaymentRecordType();
						}


						if(PaymentStatus.equalsIgnoreCase("PROCESSED") && PaymentRecordType.equalsIgnoreCase("PAY") && PaymentStatusCode.equalsIgnoreCase("COM") ) {
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
						}else if(PaymentStatus.equalsIgnoreCase("PROCESSED") && PaymentRecordType.equalsIgnoreCase("PAY") && PaymentStatusCode.equalsIgnoreCase("STL") ) {
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

	private String apiResponseManupulation_FWSBillingLookup(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.N_FLAG);
			data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.N_FLAG);

			String PaymentDueDate = "";
			String Types = "";
			String RenewalBalance = "";
			String Balance = "";
			String LastPaymetAmount = "";
			String LastPaymetDate = "";
			String NextPaymentDate = "";
			String NextPaymentAmount = "";
			String CurrentBalance = "";
			String PaymentDueDateformatted = "";
			String LastPaymetDateformatted = "";
			String NextPaymentDateformatted = "";

			LocalDate date;
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject BillingSummary = (JSONObject) resp.get("billingSummary");
			String policysource = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
			String billingAccountNumber = (String) data.getSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO);
			if (billingAccountNumber != null && billingAccountNumber.startsWith("90")
					|| billingAccountNumber.startsWith("3Z") || billingAccountNumber.startsWith("00")
					|| billingAccountNumber.startsWith("ZZ") || billingAccountNumber.startsWith("HB")) {
				data.addToLog(data.getCurrentElement(), "PAYROLL_DEDUCT_OFF");
				data.setSessionData("S_PAYROLL_DEDUCT_OFF", Constants.Y_FLAG);
			}

			if (policysource.equalsIgnoreCase("ARS")) {

				data.addToLog(data.getCurrentElement(), "Policy Source : " + policysource + " , Billing Acct No :  "
						+ data.getSessionData(Constants.S_API_POLICY_BILLINGACNUMB));
				if (billingAccountNumber != null && billingAccountNumber.startsWith("90")
						|| billingAccountNumber.startsWith("3Z")) {
					data.addToLog(data.getCurrentElement(), "Setting Auto Pay is enabled");
					data.setSessionData("S_AUTO_PAY", Constants.Y_FLAG);
				}
				JSONObject returnMessage = (JSONObject) BillingSummary.get("returnMessage");
				String ErroCode = (String) returnMessage.get("errorCode");
				String GPC = (String) BillingSummary.get("GPC");
				if (BillingSummary.containsKey("GPC")) {
					if (GPC.equalsIgnoreCase("017")) {
						data.setSessionData("S_GPC_VALUE", "Y");
						data.addToLog("ErrorCode", ErroCode);
					}
				}

				if ((ErroCode.equalsIgnoreCase("PB4")) || (ErroCode.equalsIgnoreCase("PB5"))
						|| (ErroCode.equalsIgnoreCase("NPR")) || (ErroCode.equalsIgnoreCase("NPAC"))) {
					data.setSessionData(Constants.S_POLICY_STATUS, Constants.CANCELLED);
					data.addToLog("ErrorCode", ErroCode);
					data.addToLog(Constants.S_POLICY_STATUS, Constants.CANCELLED);
				}
				if ((ErroCode.equalsIgnoreCase("PB1")) || (ErroCode.equalsIgnoreCase("PB7"))
						|| (ErroCode.equalsIgnoreCase("NPR")) || (ErroCode.equalsIgnoreCase("NPAC"))) {
					data.setSessionData("S_BILLINGINFO_RES", "Y");
					data.addToLog("ErrorCode", ErroCode);
					data.addToLog("S_BILLINGINFO_RES", "Y");
				}
				String GSONLIB = resp.toString();
				Gson gsonobj = new Gson();
				PolicyBillingLookupARS obj = (PolicyBillingLookupARS) gsonobj.fromJson(GSONLIB,
						PolicyBillingLookupARS.class);
				com.farmers.bean.FWSBillingLookup.ARS.BillingSummary billsummary = obj.getBillingSummary();
				String paymentLevelCode = billsummary.getPolicy().getPaymentLevelCode();
				String policyType = billsummary.getPolicy().getProducerDistSysCd();
				if (paymentLevelCode != null && !paymentLevelCode.equals("")) {
					data.addToLog(data.getCurrentElement(), "Payment Level COde : " + paymentLevelCode);
					data.setSessionData("S_PAYMENT_LEVEL_CODE", paymentLevelCode);
				}
				if (policyType != null && !policyType.equals("")) {
					data.addToLog(data.getCurrentElement(), "producerDistSysCd : " + policyType);
					data.setSessionData("S_PRODUCER_DIST_SYS_CD", policyType);
				}
				List<com.farmers.bean.FWSBillingLookup.ARS.Due> duelits = billsummary.getDues();
				for (int i = 0; i < duelits.size(); i++) {
					if (((com.farmers.bean.FWSBillingLookup.ARS.Due) duelits.get(i)).getType() != null) {
						Types = ((com.farmers.bean.FWSBillingLookup.ARS.Due) duelits.get(i)).getType();
						if (Types.equalsIgnoreCase("NEXT")) {
							PaymentDueDate = ((com.farmers.bean.FWSBillingLookup.ARS.Due) duelits.get(i)).getDate();
							CurrentBalance = ((com.farmers.bean.FWSBillingLookup.ARS.Due) duelits.get(i)).getAmount();
							if (PaymentDueDate != null && PaymentDueDate.contains("/")) {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
								date = LocalDate.parse(PaymentDueDate, formatter);
								DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
								PaymentDueDateformatted = date.format(formatterOutput);
							} else if (PaymentDueDate != null && PaymentDueDate.contains("-")) {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
								date = LocalDate.parse(PaymentDueDate, formatter);
								DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
								PaymentDueDateformatted = date.format(formatterOutput);
							}
							data.setSessionData("S_PaymentDue_Date", PaymentDueDateformatted);
							data.addToLog("S_PaymentDue_Date", PaymentDueDateformatted);
							data.setSessionData("S_Current_Balance", CurrentBalance);
							data.addToLog("S_Current_Balance", CurrentBalance);
						}
						if (Types.equalsIgnoreCase("MAX")) {
							RenewalBalance = ((com.farmers.bean.FWSBillingLookup.ARS.Due) duelits.get(i)).getAmount();
							data.setSessionData("S_Renewal_Balance", RenewalBalance);
							data.addToLog("S_Renewal_Balance", RenewalBalance);
						}
						if (Types.equalsIgnoreCase("LAST")) {
							data.addToLog("Type : Last - ", Types);
							LastPaymetAmount = ((com.farmers.bean.FWSBillingLookup.ARS.Due) duelits.get(i)).getAmount();
							LastPaymetDate = ((com.farmers.bean.FWSBillingLookup.ARS.Due) duelits.get(i)).getDate();
							if (LastPaymetDate != null && LastPaymetDate.contains("/")) {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
								date = LocalDate.parse(LastPaymetDate, formatter);
								DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
								LastPaymetDateformatted = date.format(formatterOutput);
								data.addToLog("Type : Last - MM/dd/yyyy", LastPaymetDateformatted + " : " + formatterOutput);
							} else if (LastPaymetDate != null && LastPaymetDate.contains("-")) {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
								date = LocalDate.parse(LastPaymetDate, formatter);
								DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
								LastPaymetDateformatted = date.format(formatterOutput);
							}

							if(LastPaymetAmount != null && !LastPaymetAmount.isEmpty() && LastPaymetDate != null && !LastPaymetDate.isEmpty() && !"0.00".equalsIgnoreCase(LastPaymetAmount)) {
								LastPaymetAmount = removeNegativeSymbol(caa, data, String.valueOf(LastPaymetAmount));
								data.setSessionData("S_LAST_PMNT_AVAIL", "Y");
							}


							/*	if (LastPaymetAmount != null && !LastPaymetAmount.isEmpty()
									&& !LastPaymetAmount.equals("")) {

								Double LastPaymetAmountint = Double.parseDouble(LastPaymetAmount);

								if (LastPaymetAmountint != 0 && LastPaymetAmountint != 0.00) {

									data.setSessionData("S_LAST_PMNT_AVAIL", "Y");


								}

								else {
									data.setSessionData("S_LAST_PMNT_AVAIL", "N");

								}

							}

							else {
								data.setSessionData("S_LAST_PMNT_AVAIL", "N");
							}*/

						}

						data.setSessionData("S_LastPayment_Amount", LastPaymetAmount);
						data.addToLog("S_LastPayment_Amount", LastPaymetAmount);
						data.setSessionData("S_LastPayment_Date", LastPaymetDateformatted);
						data.addToLog("S_LastPayment_Date", LastPaymetDateformatted);

					}
					if (Types.equalsIgnoreCase("NEXT")) {
						NextPaymentAmount = ((com.farmers.bean.FWSBillingLookup.ARS.Due) duelits.get(i)).getAmount();
						NextPaymentDate = ((com.farmers.bean.FWSBillingLookup.ARS.Due) duelits.get(i)).getDate();
						if (NextPaymentDate != null && NextPaymentDate.contains("/")) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
							date = LocalDate.parse(NextPaymentDate, formatter);
							DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
							NextPaymentDateformatted = date.format(formatterOutput);
						} else if (NextPaymentDate != null && NextPaymentDate.contains("-")) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							date = LocalDate.parse(NextPaymentDate, formatter);
							DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
							NextPaymentDateformatted = date.format(formatterOutput);
						}

						if(NextPaymentAmount != null && !NextPaymentAmount.isEmpty() && NextPaymentDate != null && !NextPaymentDate.isEmpty() && !"0.00".equalsIgnoreCase(NextPaymentAmount)) {
							data.setSessionData("S_NEXT_PMNT_AVAIL", "Y");
						}

						/*	if (NextPaymentAmount != null && !NextPaymentAmount.isEmpty()
								&& !NextPaymentAmount.equals("")) {

							Double NextPaymentAmountint = Double.parseDouble(NextPaymentAmount);

							if (NextPaymentAmountint != 0 && NextPaymentAmountint != 0.00) {

								data.setSessionData("S_NEXT_PMNT_AVAIL", "Y");

							}

							else {
								data.setSessionData("S_NEXT_PMNT_AVAIL", "N");

							}
						} else {
							data.setSessionData("S_NEXT_PMNT_AVAIL", "N");
						}*/
						String nextAmount = removeNegativeSymbol(caa, data, NextPaymentAmount);
						data.setSessionData("S_NextPayment_Amount", nextAmount);
						data.addToLog("S_NextPayment_Amount", NextPaymentAmount);
						data.setSessionData("S_NextPayment_Date", NextPaymentDateformatted);

						data.addToLog("S_NextPayment_Date", NextPaymentDateformatted);

					}
				}
				/*
				 * data.setSessionData("S_POLICY_STATUS", "Active"); data.addToLog("ErrorCode",
				 * ErroCode); data.addToLog("S_POLICY_STATUS", "Active");
				 */
			} else {
				String GSONLIB = resp.toString();
				Gson gsonobj = new Gson();
				PolicyBillingLookup360 obj = (PolicyBillingLookup360) gsonobj.fromJson(GSONLIB,
						PolicyBillingLookup360.class);
				com.farmers.bean.FWSBillingLookup.ThreeSixty.BillingSummary billsummary = obj.getBillingSummary();
				List<com.farmers.bean.FWSBillingLookup.ThreeSixty.Due> duelits = billsummary.getDues();
				String paymentLevelCode = billsummary.getPolicy().getPaymentLevelCode();
				if (paymentLevelCode != null && !paymentLevelCode.equals("")) {
					data.addToLog(data.getCurrentElement(), "Payment Level Code : " + paymentLevelCode);
					data.setSessionData("S_PAYMENT_LEVEL_CODE", paymentLevelCode);
				}
				if (billsummary.getTerms() != null) {
					Terms objTerms = billsummary.getTerms();
					String paymentMethod = objTerms.getPaymentMethod();
					if (paymentMethod != null && !paymentMethod.equals("")
							&& (paymentMethod.equalsIgnoreCase("ach") || paymentMethod.equalsIgnoreCase("creditcard")
									|| paymentMethod.equalsIgnoreCase("payrolldeduct_ml"))) {

						data.addToLog(data.getCurrentElement(), "Setting autopay is enabled");
						data.setSessionData("S_AUTO_PAY", "Y");
					}
				}
				
				if(null != billsummary.getReturnMessage()) {
					ReturnMessage returnMessage = (ReturnMessage) billsummary.getReturnMessage();
					String errorCode = returnMessage.getErrorCode();
					data.addToLog(currElementName, "errorCode : "+errorCode);
					if(null == errorCode || errorCode.isEmpty() || !errorCode.equalsIgnoreCase("TRANSFER") || !errorCode.equalsIgnoreCase("NPAC") || 
							!errorCode.equalsIgnoreCase("NPR") || !errorCode.equalsIgnoreCase("R0") || !errorCode.equalsIgnoreCase("R0") || 
							!errorCode.equalsIgnoreCase("PB8") || !errorCode.equalsIgnoreCase("PB9") || !errorCode.equalsIgnoreCase("PB1")
							 || !errorCode.equalsIgnoreCase("PB7")) {
						data.setSessionData("S_NEXT_PAYMENT_ALLOWED", Constants.YES);
						data.addToLog(currElementName, "Is Next Payment Allowed : "+data.getSessionData("S_NEXT_PAYMENT_ALLOWED"));
					}
				}

				for (int i = 0; i < duelits.size(); i++) {
					if (((com.farmers.bean.FWSBillingLookup.ThreeSixty.Due) duelits.get(i)).getType() != null) {
						Types = ((com.farmers.bean.FWSBillingLookup.ThreeSixty.Due) duelits.get(i)).getType();
						if (Types.equalsIgnoreCase("NEXT")) {
							PaymentDueDate = ((com.farmers.bean.FWSBillingLookup.ThreeSixty.Due) duelits.get(i))
									.getDate();
							CurrentBalance = ((com.farmers.bean.FWSBillingLookup.ThreeSixty.Due) duelits.get(i))
									.getAmount();
							if (PaymentDueDate != null && PaymentDueDate.contains("/")) {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
								date = LocalDate.parse(PaymentDueDate, formatter);
								DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
								PaymentDueDateformatted = date.format(formatterOutput);
							} else if (PaymentDueDate != null && PaymentDueDate.contains("-")) {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
								date = LocalDate.parse(PaymentDueDate, formatter);
								DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
								PaymentDueDateformatted = date.format(formatterOutput);
							}

							data.setSessionData("S_PaymentDue_Date", PaymentDueDateformatted);
							data.addToLog("S_PaymentDue_Date", PaymentDueDateformatted);
							data.setSessionData("S_Current_Balance", CurrentBalance);
							data.addToLog("S_Current_Balance", CurrentBalance);
						}
						if (Types.equalsIgnoreCase("MAX")) {
							RenewalBalance = ((com.farmers.bean.FWSBillingLookup.ThreeSixty.Due) duelits.get(i))
									.getAmount();
							data.setSessionData("S_Renewal_Balance", RenewalBalance);
							data.addToLog("S_Renewal_Balance", RenewalBalance);
						}
						if (Types.equalsIgnoreCase("LAST")) {
							LastPaymetAmount = ((com.farmers.bean.FWSBillingLookup.ThreeSixty.Due) duelits.get(i))
									.getAmount();
							LastPaymetDate = ((com.farmers.bean.FWSBillingLookup.ThreeSixty.Due) duelits.get(i))
									.getDate();
							if (LastPaymetDate != null && LastPaymetDate.contains("/")) {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
								date = LocalDate.parse(LastPaymetDate, formatter);
								DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
								LastPaymetDateformatted = date.format(formatterOutput);
							} else if (LastPaymetDate != null && LastPaymetDate.contains("-")) {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
								date = LocalDate.parse(LastPaymetDate, formatter);
								DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
								LastPaymetDateformatted = date.format(formatterOutput);
							}
							if (LastPaymetAmount != null && !LastPaymetAmount.isEmpty()
									&& !LastPaymetAmount.equals("")) {

								Double LastPaymetAmountint = Double.parseDouble(LastPaymetAmount);

								if (LastPaymetAmountint != 0 && LastPaymetAmountint != 0.00) {

									data.setSessionData("S_LAST_PMNT_AVAIL", "Y");

								}

								else {
									data.setSessionData("S_LAST_PMNT_AVAIL", "N");
								}

							}

							else {
								data.setSessionData("S_LAST_PMNT_AVAIL", "N");
							}

						}
						LastPaymetAmount = removeNegativeSymbol(caa, data, String.valueOf(LastPaymetAmount));
						data.setSessionData("S_LastPayment_Amount", LastPaymetAmount);
						data.addToLog("S_LastPayment_Amount", LastPaymetAmount);
						// LastPaymetDate = LastPaymetDate.replaceAll("-", "");
						data.setSessionData("S_LastPayment_Date", LastPaymetDateformatted);
						data.addToLog("S_LastPayment_Date", LastPaymetDateformatted);

					}
					if (Types.equalsIgnoreCase("NEXTACCOUNT")) {
						NextPaymentAmount = ((com.farmers.bean.FWSBillingLookup.ThreeSixty.Due) duelits.get(i))
								.getAmount();
						NextPaymentDate = ((com.farmers.bean.FWSBillingLookup.ThreeSixty.Due) duelits.get(i)).getDate();
						NextPaymentDate = NextPaymentDate.replaceAll("-", "");
						if (NextPaymentDate != null && NextPaymentDate.contains("/")) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
							date = LocalDate.parse(NextPaymentDate, formatter);
							DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
							NextPaymentDateformatted = date.format(formatterOutput);
						} else if (NextPaymentDate != null && NextPaymentDate.contains("-")) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							date = LocalDate.parse(NextPaymentDate, formatter);
							DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyyMMdd");
							NextPaymentDateformatted = date.format(formatterOutput);
						}
						if (NextPaymentAmount != null && !NextPaymentAmount.isEmpty()
								&& !NextPaymentAmount.equals("")) {

							Double NextPaymentAmountint = Double.parseDouble(NextPaymentAmount);

							if (NextPaymentAmountint != 0 && NextPaymentAmountint != 0.00) {

								data.setSessionData("S_NEXT_PMNT_AVAIL", "Y");

							}

							else {
								data.setSessionData("S_NEXT_PMNT_AVAIL", "N");

							}
						} else {
							data.setSessionData("S_NEXT_PMNT_AVAIL", "N");
						}
						String nextAmount = removeNegativeSymbol(caa, data, NextPaymentAmount);
						data.setSessionData("S_NextPayment_Amount", nextAmount);
						data.addToLog("S_NextPayment_Amount", NextPaymentAmount);
						data.setSessionData("S_NextPayment_Date", NextPaymentDateformatted);
						data.addToLog("S_NextPayment_Date", NextPaymentDateformatted);

					}
				}
			}
			// data.setSessionData("S_POLICY_STATUS", "Active");
			// data.addToLog("ErrorCode", erroCode);
			// data.addToLog("S_POLICY_STATUS", "Active"\\\



			data.addToLog("S_NEXT_PMNT_AVAIL", (String) data.getSessionData("S_NEXT_PMNT_AVAIL"));
			data.addToLog("S_LAST_PMNT_AVAIL", (String) data.getSessionData("S_LAST_PMNT_AVAIL"));

			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String apiResponseManupulation_pointgeneralbilling(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			data.setSessionData(Constants.S_LAST_PMNT_AVAIL, Constants.N_FLAG);
			data.setSessionData(Constants.S_NEXT_PMNT_AVAIL, Constants.N_FLAG);

			String PaymentDueDate = "";
			String Types = "";
			String RenewalBalance = "";
			String Balance = "";
			String LastPaymetAmount = "";
			String LastPaymetDate = "";
			String NextPaymentDate = "";
			String NextPaymentAmount = "";
			String CurrentBalance = "";
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			String GSONLIB = resp.toString();
			Gson gsonobj = new Gson();
			PointbillingInquiry obj = (PointbillingInquiry) gsonobj.fromJson(GSONLIB, PointbillingInquiry.class);
			List<com.farmers.bean.FWSBillingLookup.PointBilling.Term> BillTermsummary = obj.getBillingSummary()
					.getTerms();
			boolean pastFlag = false;
			for (int i = 0; i < BillTermsummary.size(); i++) {
				if (((com.farmers.bean.FWSBillingLookup.PointBilling.Term) BillTermsummary.get(i)).getPayplan()
						.getDescription() != null) {
					String PayPlanDescription = ((com.farmers.bean.FWSBillingLookup.PointBilling.Term) BillTermsummary
							.get(i)).getPayplan().getDescription();
					String status = ((com.farmers.bean.FWSBillingLookup.PointBilling.Term) BillTermsummary.get(i))
							.getPayplan().getStatus();

					data.addToLog(data.getCurrentElement(), "Status : " + status);
					if ((PayPlanDescription.equalsIgnoreCase("RACH")) || (PayPlanDescription.equalsIgnoreCase("RCC"))) {
						data.setSessionData("S_AUTO_PAY", "Y");
						data.addToLog("S_AUTO_PAY", PayPlanDescription);
					}
					if (status.equalsIgnoreCase(Constants.ACTIVE) || "PRE-CANCEL".equalsIgnoreCase(status)) {
						data.setSessionData(Constants.S_POLICY_STATUS, Constants.ACTIVE);
					} else if (status.contains("CANCEL") || "CANCELLED POLICY".equalsIgnoreCase(status)
							|| "CANCEL".equalsIgnoreCase(status) || "XLC".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
						data.setSessionData(Constants.S_POLICY_STATUS, Constants.CANCELLED);
					}
					data.addToLog(data.getCurrentElement(), "S_POLICY_STATUS : " + data.getSessionData(Constants.S_POLICY_STATUS));
				}
				List<com.farmers.bean.FWSBillingLookup.PointBilling.Due> DueList = ((com.farmers.bean.FWSBillingLookup.PointBilling.Term) BillTermsummary
						.get(i)).getDues();
				data.addToLog(currElementName, "Size : " + DueList.size());
				for (int j = 0; j < DueList.size(); j++) {
					// if
					// (((com.farmers.bean.FWSBillingLookup.PointBilling.Term)BillTermsummary.get(i)).getDues()
					// != null)
					// {

					Types = ((com.farmers.bean.FWSBillingLookup.PointBilling.Due) DueList.get(j)).getType();
					if (Types.equalsIgnoreCase("MINIMUM")) {
						PaymentDueDate = ((com.farmers.bean.FWSBillingLookup.PointBilling.Due) DueList.get(j))
								.getDate();
						CurrentBalance = ((com.farmers.bean.FWSBillingLookup.PointBilling.Due) DueList.get(j))
								.getAmount();
						data.setSessionData("S_PaymentDue_Date", PaymentDueDate);
						data.addToLog("S_PaymentDue_Date", PaymentDueDate);
						data.setSessionData("S_Current_Balance", CurrentBalance);
						data.addToLog("S_Current_Balance", CurrentBalance);
					}
					if (Types.equalsIgnoreCase("NEXT")) {
						CurrentBalance = ((com.farmers.bean.FWSBillingLookup.PointBilling.Due) DueList.get(j))
								.getAmount();
						data.setSessionData("S_Balance", Balance);
						data.addToLog("S_Balance", Balance);
						data.setSessionData("S_Renewal_Balance", CurrentBalance);
						data.addToLog("S_Renewal_Balance", CurrentBalance);
					}
					if (Types.equalsIgnoreCase("PAST") && !pastFlag) {
						data.addToLog(data.getCurrentElement(), "Get Last payment Details");
						pastFlag = true;
						LastPaymetAmount = ((com.farmers.bean.FWSBillingLookup.PointBilling.Due) DueList.get(j)).getAmount();
						LastPaymetAmount = removeNegativeSymbol(caa, data, LastPaymetAmount);
								
						LastPaymetDate = ((com.farmers.bean.FWSBillingLookup.PointBilling.Due) DueList.get(j)).getDate();
						
						if(LastPaymetDate != null && !LastPaymetDate.equals("") && LastPaymetDate.length() >= 10) {
							LastPaymetDate = LastPaymetDate.substring(0, 10);
						}
						
						data.addToLog("LastPaymetAmount", LastPaymetAmount);
						data.addToLog("LastPaymetDate", LastPaymetDate);
						
						//Consider only negative AMount objects for last payment
						//if(!(LastPaymetAmount.startsWith("-"))) continue;
						//if(LastPaymetAmount.startsWith("-")) LastPaymetAmount = LastPaymetAmount.replace("-", "");
						
						data.addToLog(data.getCurrentElement(), "Post converting date & amount");
						
						data.setSessionData("S_LastPayment_Amount", LastPaymetAmount);
						data.addToLog("S_LastPayment_Amount", LastPaymetAmount);
						
						DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
						Date date = sdf.parse(LastPaymetDate);
						LastPaymetDate = new SimpleDateFormat("yyyyMMdd").format(date);

						data.setSessionData("S_LastPayment_Date", LastPaymetDate);
						data.addToLog("S_LastPayment_Date", LastPaymetDate);

						if (LastPaymetAmount != null && !LastPaymetAmount.equals("") && LastPaymetDate != null
								&& !LastPaymetDate.equals("") && !"0.00".equalsIgnoreCase(LastPaymetAmount)) {
							data.addToLog(data.getCurrentElement(), "LAST PMT AVAILA : Y");
							data.setSessionData("S_LAST_PMNT_AVAIL", "Y");
						}

					}
					//As per Tauzia (Hypercare #18 issue), we need to consider MINIMUM is type for 21st
					if (Types.equalsIgnoreCase("MINIMUM")) {
						NextPaymentAmount = ((com.farmers.bean.FWSBillingLookup.PointBilling.Due) DueList.get(j))
								.getAmount();
						NextPaymentDate = ((com.farmers.bean.FWSBillingLookup.PointBilling.Due) DueList.get(j))
								.getDate();
						String nextAmount = removeNegativeSymbol(caa, data, NextPaymentAmount);
						data.setSessionData("S_NextPayment_Amount", nextAmount);
						data.addToLog("S_NextPayment_Amount", NextPaymentAmount);
						if (NextPaymentDate != null && !NextPaymentDate.equals("")) {
							DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
							Date date = sdf.parse(NextPaymentDate);
							NextPaymentDate = new SimpleDateFormat("yyyyMMdd").format(date);
						}
						data.setSessionData("S_NextPayment_Date", NextPaymentDate);
						data.addToLog("S_NextPayment_Date", NextPaymentDate);
						if (NextPaymentAmount != null && !NextPaymentAmount.equals("") && NextPaymentDate != null
								&& !NextPaymentDate.equals("") && !"0.00".equalsIgnoreCase(NextPaymentAmount)) {
							data.setSessionData("S_NEXT_PMNT_AVAIL", "Y");
							data.addToLog("S_NEXT_PMNT_AVAIL", "Y");
						}

					}
				}
			}

			if (data.getSessionData("S_NEXT_PMNT_AVAIL").equals("Y")
					|| data.getSessionData("S_LAST_PMNT_AVAIL").equals("Y")) {
				strExitState = Constants.SU;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String pointPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		try {
			data.addToLog(data.getCurrentElement(), "URL : " + data.getSessionData("S_POINT_POLICYINQUIIRY_URL"));
			if (data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
				String input = (String) data.getSessionData(Constants.S_POLICY_NUM);
				url = url.replace(Constants.S_POLICY_NUM, input);
				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);
				PointPolicyInquiry_Get obj = new PointPolicyInquiry_Get();
				JSONObject resp = (JSONObject) obj.start(url, input, conTimeout, readTimeout, context);
				data.addToLog(currElementName, "KYCBA_HOST_001 : PointPolicyInquiry API response  :" + resp);
				if (resp != null) {
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName,
								"Set SBP_HOST_001 : PointPolicyInquiry API Response into session with the key name of "
										+ currElementName + Constants._RESP);
						String strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						// apiResponseManupulation_PointPolicyInquiry(data, caa, currElementName,
						// strRespBody);
						JSONObject policySummaryJson = (JSONObject) new JSONParser().parse(strRespBody.toString());
						if (policySummaryJson.containsKey("policySummary")) {
							JSONObject policySummary = (JSONObject) policySummaryJson.get("policySummary");
							System.out.println(policySummary);
							String mco = (String) policySummary.get("masterCompanyCode");
							String policyNo = (String) policySummary.get("policyNumber");
							String mod = policyNo.substring(policyNo.length() - 2, policyNo.length());
							data.addToLog(data.getCurrentElement(), "POLICY INQUIRY MCO : " + mco);
							data.addToLog(data.getCurrentElement(), "POLICY INQUIRY MOD : " + mod);
							data.setSessionData("S_POLICY_MCO", mco);
							data.setSessionData("S_POLICY_MOD", mod);
							data.setSessionData(Constants.S_POLICY_NUM, policyNo);
							;
							strExitState = Constants.SU;
						}

					}

				}
			}
			data.addToLog(data.getCurrentElement(), "POINT POLICY INQUIRY API EXIT STATE : " + strExitState);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in KYCAF_HOST_001  PointPolicyInquiry API call  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private void apiResponseManupulation_PointPolicyInquiry(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) {

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
