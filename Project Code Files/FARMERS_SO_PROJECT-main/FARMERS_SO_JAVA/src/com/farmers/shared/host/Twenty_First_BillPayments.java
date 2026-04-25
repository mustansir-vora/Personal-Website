package com.farmers.shared.host;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FWSBillingLookup.PointBilling.PointbillingInquiry;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.google.gson.Gson;

public class Twenty_First_BillPayments extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			strExitState = pointgeneralbilling(data, caa, currElementName);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for SBP_HOST_001  :: " + e);
			caa.printStackTrace(e);
		}
		if(strExitState.equalsIgnoreCase(Constants.ER)) {
			//String state = (String) data.getSessionData("S_STATENAME");
			//data.addToLog(data.getCurrentElement(), "Value of S_STATENAME : "+state);
			//String mspKey = Constants.EmptyString;
			//if("Hawaii".equalsIgnoreCase(state)) {
				//mspKey = "BW_HI"+":"+"SBP_HOST_001"+":"+"ER";
			//} else {
				//mspKey = "21ST"+":"+"SBP_HOST_001"+":"+"ER";
			//}
			//data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
			//data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);
			caa.createMSPKey(caa, data, "SBP_HOST_001", "ER");
		}
		return strExitState;
	}

	private String pointgeneralbilling(DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strRespBody = "";
		String strReqBody = "";
		
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
				//UAT ENV CHANGE START
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
				
				JSONObject responses = lookups.Getpointgeneralbilling(wsurl, tid,
						Integer.valueOf(Integer.parseInt(conntimeout)), Integer.valueOf(Integer.parseInt(readtimeout)),
						context,region,UAT_FLAG);
			//UAT ENV CHANGE END
				
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
			objHostDetails.startHostReport(currElementName, "SBP_HOST_001_21st", strReqBody, region,(String) data.getSessionData(Constants.S_POINTGENERAL_BILLING_URL));
			objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			data.addToLog(currElementName, "Reporting ENDS for 21st Billing LookUp :: " + objHostDetails);
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for SBP_HOST_001_21st  pointgeneralbilling call  :: " + e1);
			caa.printStackTrace(e1);
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
							data.setSessionData("S_CURRENTBALANCE", "TRUE");
							data.setSessionData("S_OUTSTANDINGBALANCE", "TRUE");
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
