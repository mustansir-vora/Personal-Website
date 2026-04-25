package com.farmers.host;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.CommercialBillingSummary_Get;
import com.farmers.FarmersAPI_NP.CommercialBillingSummary_NP_Get;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;


public class CommMdm_Billing extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

		String exitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		JSONObject resp =null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		String url = Constants.EmptyString;
		String finalurl = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)

		try {
			data.setSessionData("S_IS_RTB_ACCOUNT","FALSE");
			String commercialJSONResponse = (String)data.getSessionData(Constants.S_COMMERCIAL_BILLING_JSON);
			if(commercialJSONResponse==null || Constants.EmptyString.equalsIgnoreCase(commercialJSONResponse)) {
				if(data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY)!=null && data.getSessionData(Constants.S_CONN_TIMEOUT)!=null && data.getSessionData(Constants.S_READ_TIMEOUT)!=null){
					url = (String) data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY);
					String policyNum = (String)data.getSessionData(Constants.S_POLICY_NUM);
					String accNum = (String)data.getSessionData(Constants.S_ACCNUM);
					if(null != policyNum && !policyNum.isEmpty()) {
						url = (String)data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY_POLICYNUM_URL);
						if(url.contains(Constants.S_URL_POLICYNUM)) finalurl = url.replace(Constants.S_URL_POLICYNUM, policyNum);
						data.addToLog(currElementName,"POLICYNUM API URL : "+ finalurl);
					} else {
						url = (String)data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY_ACCNUM_URL);
						if(url.contains(Constants.S_URL_POLICYNUM)) finalurl = url.replace(Constants.S_URL_POLICYNUM, accNum);
						data.addToLog(currElementName,"ACCNUM API URL : "+ finalurl);
					}
					String callerId = (String) data.getSessionData(Constants.S_CALLID);
					int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
					int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
					LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
					CommercialBillingSummary_Get obj = new CommercialBillingSummary_Get();
					//Non prod changes-Priya
					data.addToLog("API URL: ", url);
					String prefix = "https://api-np-ss.farmersinsurance.com"; 
					String UAT_FLAG="";
			        if(url.startsWith(prefix)) {
			        	UAT_FLAG="YES";
			        }
					CommercialBillingSummary_NP_Get objNP = new CommercialBillingSummary_NP_Get();
					//JSONObject resp=null ;
					if("YES".equalsIgnoreCase(UAT_FLAG)) {
						String Key =Constants.S_COMMERCIAL_BILLING_SUMMARY_ACCNUM_URL;
						 region = regionDetails.get(Key);
						data.addToLog("Region for UAT endpoint: ", region);
						resp = objNP.start(finalurl,callerId,conTimeout,readTimeout, context, region);
					}else {
						region="PROD";
					    resp = obj.start(finalurl,callerId,conTimeout,readTimeout, context);
					}
					//Non prod changes-Priya
					data.addToLog(currElementName, " API response  :"+resp);	
					
					//Mustan - Alerting Mechanism ** Response Code Capture
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				
					if(resp != null) {
						if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
						if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY) && resp.get(Constants.RESPONSE_BODY) != null) {
							data.addToLog(currElementName, "Set Commercial Billing Summary Lookup API Response into session with the key name of "+currElementName+Constants._RESP);
							strRespBody =  resp.get(Constants.RESPONSE_BODY).toString();
							data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
							exitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
							data.addToLog(data.getCurrentElement(), "exitState : "+exitState);
						} else {
							strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
						}		
					}
					try {
						objHostDetails.startHostReport(currElementName," Commercial Billing Summary LookupAPI by PolicyNumber", strReqBody, region,(String) data.getSessionData(Constants.S_COMMERCIAL_BILLING_SUMMARY));
						objHostDetails.endHostReport(data,strRespBody , exitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
					} catch (Exception e) {
						data.addToLog(currElementName,"Exception while forming host reporting for  Commercial Billing Summary Lookup API call by Policy Number  :: "+e);
						caa.printStackTrace(e);
					}
				}
			}else {
				exitState = apiResponseManupulation(data, caa, currElementName, commercialJSONResponse);
				data.addToLog(data.getCurrentElement(), "exitState Comm MDM : "+exitState);
			}
		} catch (Exception e) {

			data.addToLog(currElementName,"Exception in  Commercial Billing Summary Lookup API call  :: "+e);
			caa.printStackTrace(e);
		}


		data.setSessionData("S_Commercial_API","True");

		String hostMSPEndKey = Constants.EmptyString;
		if(exitState.equalsIgnoreCase(Constants.ER)) hostMSPEndKey = Constants.API_FAILURE;
		else if(null != data.getSessionData("S_IS_RTB_ACCOUNT") && ((String)data.getSessionData("S_IS_RTB_ACCOUNT")).equalsIgnoreCase(Constants.TRUE)) hostMSPEndKey = Constants.ACTIVE_RTB_ACCOUNT;
		if(null != hostMSPEndKey && !hostMSPEndKey.isEmpty()) data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CAIF_HOST_001:"+hostMSPEndKey);
		data.addToLog(currElementName,"S_MENU_SELCTION_KEY for  CAIF_HOST_001 :: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));

		return exitState;

	}



	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState =Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject billingSummary = (JSONObject) resp.get("billingSummary");
			JSONArray accounts = (JSONArray) billingSummary.get("accounts");
			for (Object accountObj : accounts) {
				JSONObject account = (JSONObject) accountObj;

				String strAccNum = (String)account.get("accountNumber");
				if(strAccNum.contains("-")) strAccNum =strAccNum.split("\\-")[0];
				data.setSessionData(Constants.S_ACCNUM, strAccNum);
				String stopCode = (String) account.get("stopCode");
				data.setSessionData("S_STOPCODE", stopCode);
				data.addToLog(data.getCurrentElement(), "STOP CODE : "+stopCode);
				
				if(account.containsKey("payorZipCode")) {
					String payzipCode = (String)account.get("payorZipCode");
					data.addToLog(data.getCurrentElement(), "payzipCode : "+payzipCode);
					data.setSessionData(Constants.S_PAYOR_ZIP_CODE, payzipCode);
				}
				
				if(stopCode.equalsIgnoreCase("BBR")) {
					data.addToLog(data.getCurrentElement(), "STOP CODE : "+stopCode+ " Hence transferring the call");
					data.setSessionData("S_IS_RTB_ACCOUNT","TRUE");
				}
				else{
					data.addToLog(data.getCurrentElement(), "STOP CODE : "+stopCode+ " Ready to play the payment details");
					data.setSessionData("S_IS_RTB_ACCOUNT","FALSE");

					JSONObject jsoPaymentDetails = (JSONObject) account.get("paymentDetails");
					String lastPmtAmt = "";
					String lastPmtDate = "";
					String totalOutstandingAmt = "";
					String accStatus = "";
					String paymentDueDate = "";
					String paymentDueAmt = "";
					String nextInvoiceUpdateDate = "";
					String nextInvoiceUpdateAmount="";

					if(jsoPaymentDetails.containsKey("lastPmtAmount") ) {
						JSONObject objLastPmtAmt = (JSONObject) jsoPaymentDetails.get("lastPmtAmount");
						lastPmtAmt = String.valueOf((double)objLastPmtAmt.get("amount"));
					}

					if(jsoPaymentDetails.containsKey("lastPmtDate")) {
						lastPmtDate = (String) jsoPaymentDetails.get("lastPmtDate");
						//lastPmtDate = lastPmtDate.replace("-", "");
					}

					if(jsoPaymentDetails.containsKey("totalOutstandingBalance") ) {
						JSONObject objTotaOutAmt = (JSONObject) jsoPaymentDetails.get("totalOutstandingBalance");
						totalOutstandingAmt =  String.valueOf((double)objTotaOutAmt.get("amount"));
					}

					if(account.containsKey("status")) {
						accStatus = (String) account.get("status");
						if(accStatus.equalsIgnoreCase("ACT")) {
							data.setSessionData("S_COMM_STATUS_AUDIO", "CAIF_PA_001j.wav");
						}else {
							data.setSessionData("S_COMM_STATUS_AUDIO", "CAIF_PA_001h.wav");
						}
					}

					if(jsoPaymentDetails.containsKey("paymentDueDate") ) {
						if(null != jsoPaymentDetails.get("paymentDueDate")) {
							paymentDueDate = (String) jsoPaymentDetails.get("paymentDueDate");
							//paymentDueDate = paymentDueDate.replace("-", "");
						}

					}
					
					if(jsoPaymentDetails.containsKey("nextBillDate") ) {
						if(null != jsoPaymentDetails.get("nextBillDate")) {
							nextInvoiceUpdateDate = (String) jsoPaymentDetails.get("nextBillDate");
							//nextInvoiceUpdateDate = nextInvoiceUpdateDate.replace("-", "");
						}

					}

					if(jsoPaymentDetails.containsKey("paymentDueAmount") && null != jsoPaymentDetails.get("paymentDueAmount")) {
						JSONObject objPmtDueAmt = (JSONObject) jsoPaymentDetails.get("paymentDueAmount");
						paymentDueAmt = String.valueOf((double)objPmtDueAmt.get("amount")) ;
					}
					
					if(jsoPaymentDetails.containsKey("nextBillAmount") && null != jsoPaymentDetails.get("nextBillAmount")) {
						JSONObject objnxtbillamt = (JSONObject) jsoPaymentDetails.get("nextBillAmount");
						nextInvoiceUpdateAmount = String.valueOf((double)objnxtbillamt.get("amount")) ;
					}



				
					LocalDate currentDate = LocalDate.now();
			        
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");
			        String formattedDate = currentDate.format(formatter);
			        
			        LocalDate date = LocalDate.parse(lastPmtDate);

			      
			        String lastPmtDateModified = date.format(DateTimeFormatter.ofPattern("MMdd"));
			        
			        LocalDate date1 = LocalDate.parse(paymentDueDate);

				      
			        String paymentDueDateModified = date1.format(DateTimeFormatter.ofPattern("MMdd"));
			        LocalDate date2 = LocalDate.parse(nextInvoiceUpdateDate);

				      
			        String nextInvoiceUpdateDateModified = date2.format(DateTimeFormatter.ofPattern("MMdd"));
			        
					data.setSessionData("S_CURR_YEAR",formattedDate);
					data.setSessionData("S_COMM_LAST_PMT_AMT",lastPmtAmt);
					data.setSessionData("S_COMM_LAST_PMT_DATE",lastPmtDateModified);
					data.setSessionData("S_COMM_TOT_OUT_BAL",totalOutstandingAmt);
					data.setSessionData("S_COMM_ACCT_STATUS",accStatus);
					data.setSessionData("S_COMM_PMT_DUE_AMT",paymentDueAmt);
					data.setSessionData("S_COMM_PMT_DUE_DATE",paymentDueDateModified);
					data.setSessionData("S_COMM_NXT_BIL_DATE",nextInvoiceUpdateDateModified);
					data.setSessionData("S_COMM_NXT_BIL_AMT",nextInvoiceUpdateAmount);
					
					
					data.addToLog(data.getCurrentElement(), "currentYear : "+formattedDate+" :: lastPmtAmt : "+lastPmtAmt+" :: lastPmtDateModified : "+lastPmtDateModified+
							" :: paymentDueAmt : "+paymentDueAmt+" :: paymentDueDateModified : "+paymentDueDateModified+" :: totalOutstandingAmt : "+totalOutstandingAmt+" :: Account Status : "+accStatus  +" :: nextInvoiceUpdateDateModified : "+nextInvoiceUpdateDateModified +" :: nextInvoiceUpdateAmount : "+nextInvoiceUpdateAmount  );

					

					if (accStatus.equalsIgnoreCase("EXP") || accStatus.equalsIgnoreCase("LAP") || accStatus.equalsIgnoreCase("NWP") || accStatus.equalsIgnoreCase("PCN") || accStatus.equalsIgnoreCase("SPC")) {
					    strExitState = "ER";
					    data.addToLog(data.getCurrentElement(), strExitState);
					    
					} else if (accStatus.equalsIgnoreCase("CNO") || accStatus.equalsIgnoreCase("CNP")) {
					    if (lastPmtAmt != null && !lastPmtAmt.equals("") && lastPmtDate != null && !lastPmtDate.equals("") && paymentDueAmt != null && !paymentDueAmt.equals("") && paymentDueDate != null && !paymentDueDate.equals("") && totalOutstandingAmt != null && !totalOutstandingAmt.equals("")) {
					        strExitState = "CASE2";
					        data.addToLog(data.getCurrentElement(), strExitState);
					    } else if (lastPmtAmt != null && !lastPmtAmt.equals("") && lastPmtDate != null && !lastPmtDate.equals("") && paymentDueAmt != null && !paymentDueAmt.equals("") && paymentDueDate != null && !paymentDueDate.equals("")) {
					        strExitState = "CASE3";
					        data.addToLog(data.getCurrentElement(), strExitState);
					    } else if (lastPmtAmt != null && !lastPmtAmt.equals("") && lastPmtDate != null && !lastPmtDate.equals("") && totalOutstandingAmt != null && !totalOutstandingAmt.equals("")) {
					        strExitState = "CASE1";
					        data.addToLog(data.getCurrentElement(), strExitState);
					    }
					} else if (accStatus.equalsIgnoreCase("ACT")) {
					    if (lastPmtAmt != null && !lastPmtAmt.equals("") && lastPmtDate != null && !lastPmtDate.equals("") && paymentDueAmt != null && !paymentDueAmt.equals("") && paymentDueDate != null && !paymentDueDate.equals("") && totalOutstandingAmt != null && !totalOutstandingAmt.equals("")) {
					        if (nextInvoiceUpdateDate != null && !nextInvoiceUpdateDate.equals("") && nextInvoiceUpdateAmount != null && !nextInvoiceUpdateAmount.equals("")) {
					            strExitState = "CASE5";
					            data.addToLog(data.getCurrentElement(), strExitState);
					        } else if (lastPmtAmt != null && !lastPmtAmt.equals("") && lastPmtDate != null && !lastPmtDate.equals("") && (paymentDueAmt.equals("0"))) {
					            strExitState = "CASE4";
					            data.addToLog(data.getCurrentElement(), strExitState);
					        } else if (lastPmtAmt != null && !lastPmtAmt.equals("") && lastPmtDate != null && !lastPmtDate.equals("") && totalOutstandingAmt != null && !totalOutstandingAmt.equals("")) {
						        strExitState = "CASE7";
						        data.addToLog(data.getCurrentElement(), strExitState);
						    }
					        }
					    }
						else if (accStatus != null && !accStatus.equals("")) {
						if(accStatus.equalsIgnoreCase("ACT")) {
							strExitState = "CASE8";
							}
						else if (accStatus.equalsIgnoreCase("CNO") || (accStatus.equalsIgnoreCase("CNP")) )
								{
							strExitState = "CASE9";
							}
							}
							

					data.addToLog(data.getCurrentElement(), "Current Exit State : "+strExitState);
				}	

			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
}

