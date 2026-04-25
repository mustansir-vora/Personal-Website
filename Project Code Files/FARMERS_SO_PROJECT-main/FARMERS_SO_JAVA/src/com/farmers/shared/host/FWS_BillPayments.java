	package com.farmers.shared.host;

	import java.text.SimpleDateFormat;
	import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

	import org.apache.logging.log4j.core.LoggerContext;
	import org.json.simple.JSONArray;
	import org.json.simple.JSONObject;
	import org.json.simple.parser.JSONParser;
	import org.json.simple.parser.ParseException;

	import com.audium.server.AudiumException;
	import com.audium.server.session.DecisionElementData;
	import com.audium.server.voiceElement.DecisionElementBase;
	import com.farmers.client.Lookupcall;
	import com.farmers.report.SetHostDetails;
	import com.farmers.util.CommonAPIAccess;
	import com.farmers.util.Constants;

	public class FWS_BillPayments extends DecisionElementBase {
		String strReqBody = Constants.EmptyString;

		public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
			String strExitState = Constants.ER;
			CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

			try {
				strExitState = FWSBillingLookup(data, caa, currElementName);
			} 
			catch (Exception e) {
				data.addToLog(currElementName, "Exception while forming host details for SBP_HOST_001  :: " + e);
				caa.printStackTrace(e);
			}
			if(strExitState.equalsIgnoreCase(Constants.ER)) {
				//String mspKey = "FWS"+":"+"SBP_HOST_001"+":"+"ER";
				//data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
				//data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);
				caa.createMSPKey(caa, data, "SBP_HOST_001", "ER");
			}
			return strExitState;
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

				if ((data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL) != null) && (data.getSessionData(Constants.S_READ_TIMEOUT) != null) && (data.getSessionData(Constants.S_CONN_TIMEOUT) != null)) {
					String wsurl = (String) data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL);
					String callId = (String) data.getSessionData(Constants.S_CALLID);
					String billingAccountNumber = (String) data.getSessionData("S_FWS_POLICY_BILLING_ACCT_NO");
					String policysource = (String) data.getSessionData("S_POLICY_SOURCE");
					
					
					String companyCode = "";
					
					if (null != billingAccountNumber) {
						
						if(billingAccountNumber.length() > 2) {
							companyCode = billingAccountNumber.substring(0, 2);
						}
						if (billingAccountNumber.length() >= 11) {
							billingAccountNumber = billingAccountNumber.substring(2, billingAccountNumber.length());
						}
					}
					
					String policyNumber = (String) data.getSessionData("S_POLICY_NUM");
					String lob = (String) data.getSessionData("S_FWS_POLICY_LOB");
					

					if (null != policyNumber && null != policysource && null != lob && !policysource.equalsIgnoreCase("ARS")) {
						// IF POLICY SOURCE IS A360 or M360. THEN IVR SHOULD CONCATENATE THE LOB FOLLOWED BY THE POLICY NUMBER 
						boolean startsWithAlphabets = Character.isLetter(policyNumber.charAt(0));
						if (!startsWithAlphabets) {
							policyNumber = lob + policyNumber;
						}
					}
					
					String effectiveDate = (String) data.getSessionData("S_FWS_POLICY_EFF_DATE");
					if (effectiveDate.length() > 10) {
						effectiveDate = effectiveDate.substring(0, 10); 
					}
					String suffix = (String) data.getSessionData("S_FWS_POLICY_SUFFIX");
					String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
					String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
					String policystate = (String) data.getSessionData("S_POLICY_STATE");
					
					//Changes based on the case CS1150152 - 'TB1' Error when querying for FWS ARS billing data  Start//
					
					String intpolicyno=(String) data.getSessionData("S_FWS_INT_POLICY_NO");
					
					if (null != billingAccountNumber && null != intpolicyno && null != effectiveDate && null != suffix && null != companyCode && null != policysource && policysource.equalsIgnoreCase("ARS")) {
						
						data.addToLog(data.getCurrentElement(), "Internal Policy No: "+intpolicyno);
						wsurl = wsurl.replace(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingAccountNumber).replace(Constants.S_FWS_INT_POLICY_NO, intpolicyno).replace(Constants.S_FWS_POLICY_EFF_DATE, effectiveDate).replace(Constants.S_FWS_POLICY_SUFFIX, suffix).replace(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
					}
					
					else if(null != billingAccountNumber && null != policyNumber && null != effectiveDate && null != suffix && null != companyCode && null != policysource && !policysource.equalsIgnoreCase("ARS")){
						data.addToLog(data.getCurrentElement(), "MEt360/A360 Policy No: "+policyNumber);
						wsurl = wsurl.replace(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingAccountNumber).replace(Constants.S_FWS_INT_POLICY_NO, policyNumber).replace(Constants.S_FWS_POLICY_EFF_DATE, effectiveDate).replace(Constants.S_FWS_POLICY_SUFFIX, suffix).replace(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
					}
					
					//Changes based on the case CS1150152 - 'TB1' Error when querying for FWS ARS billing data  end //

					LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

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
					//UAT CHANGE START
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
					
					JSONObject responses = lookups.GetFWSBillingLookup(wsurl, callId, policysource, policystate, lob, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context,region,UAT_FLAG);
				
					//UAT CHANGE END
					data.addToLog("responses", responses.toString());
					
					//Mustan - Alerting Mechanism ** Response Code Capture
					apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
					if (responses != null) {
						
						if(responses.containsKey(Constants.REQUEST_BODY)) {
							strReqBody = responses.get(Constants.REQUEST_BODY).toString();
						}
						
						if ((responses.containsKey(Constants.RESPONSE_CODE)) && ((Integer) responses.get(Constants.RESPONSE_CODE)) == 200 && (responses.containsKey("responseBody"))) {
							data.addToLog(currElementName, "Set FWS Billing API Response into session with the key name of " + currElementName + "_RESP");
							strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
							data.setSessionData(currElementName + "_RESP", responses.get(Constants.RESPONSE_BODY));
							strExitState = apiResponseManupulation_FWSBillingLookup(data, caa, currElementName, strRespBody);
						}  else {
							if(responses.get(Constants.RESPONSE_MSG)!=null)
								strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
						}
					}
				} else {
					data.addToLog(currElementName, "Mandatory field missing");
				}
			} catch (Exception e) {
				data.addToLog(currElementName, "Exception in FWSBillingLookup API call  :: " + e);
				caa.printStackTrace(e);
			}
			try {
				data.addToLog(currElementName, "Reporting STARTS for FWS Billing LookUp");
				objHostDetails.startHostReport(currElementName, "SBP_HOST_001_FWS_BILLING_LOOKUP", strReqBody,region, (String) data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL));
				objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
				data.addToLog(currElementName, "Reporting ENDS for FWS Billing LookUp");
			} catch (Exception e1) {
				data.addToLog(currElementName,
						"Exception while forming host reporting for SBP_HOST_001_FWS_BILLING_LOOKUP  FWSBillingLookup call  :: " + e1);
				caa.printStackTrace(e1);
			}
			return strExitState;
		}
		
		
		private String SecondaryFWSBillingLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName) {
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
				data.addToLog(data.getCurrentElement(), "Entering into Secondary FWS Billing Lookup");

				if ((data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL) != null) && (data.getSessionData(Constants.S_READ_TIMEOUT) != null) && (data.getSessionData(Constants.S_CONN_TIMEOUT) != null)) {
					String wsurl = (String) data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL);
					String callId = (String) data.getSessionData(Constants.S_CALLID);
					String billingAccountNumber = (String) data.getSessionData("S_FWS_POLICY_BILLING_ACCT_NO");
					String companyCode = "";
					
					if (null != billingAccountNumber) {
						
						if(billingAccountNumber.length() > 2) {
							companyCode = billingAccountNumber.substring(0, 2);
						}
						if (billingAccountNumber.length() >= 11) {
							billingAccountNumber = billingAccountNumber.substring(2, billingAccountNumber.length());
						}
					}
					
					String policyNumber = (String) data.getSessionData("S_POLICY_NUM");
					String lob = (String) data.getSessionData("S_FWS_POLICY_LOB");
					String policysource = (String) data.getSessionData("S_POLICY_SOURCE");

					if (null != policysource && !policysource.equalsIgnoreCase("ARS")) {
						// IF POLICY SOURCE IS A360 or M360. THEN IVR SHOULD CONCATENATE THE LOB FOLLOWED BY THE POLICY NUMBER 
						policyNumber = lob + policyNumber;
					}
					
					String renewaleffectiveDate = (String) data.getSessionData("S_FWS_POLICY_REN_EFF_DATE");
					if (null != renewaleffectiveDate && renewaleffectiveDate.length() > 10) {
						renewaleffectiveDate = renewaleffectiveDate.substring(0, 10);
					}
					String suffix = (String) data.getSessionData("S_FWS_POLICY_SUFFIX");
					String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
					String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
					String policystate = (String) data.getSessionData("S_POLICY_STATE");
					
					//Changes based on the case CS1150152 - 'TB1' Error when querying for FWS ARS billing data  Start//
					data.addToLog(data.getCurrentElement(),"Line before internal policy");
					String intpolicyno=(String) data.getSessionData("S_FWS_INT_POLICY_NO");
					data.addToLog(data.getCurrentElement(), "Line after internal policy");
					if (null != billingAccountNumber && null != intpolicyno && null != renewaleffectiveDate && null != suffix && null != companyCode && null != policysource && policysource.equalsIgnoreCase("ARS")) {
						
						data.addToLog(data.getCurrentElement(), "Internal Policy No: "+intpolicyno);
						wsurl = wsurl.replace(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingAccountNumber).replace(Constants.S_FWS_INT_POLICY_NO, intpolicyno).replace(Constants.S_FWS_POLICY_EFF_DATE, renewaleffectiveDate).replace(Constants.S_FWS_POLICY_SUFFIX, suffix).replace(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
					}
					
					else if(null != billingAccountNumber && null != policyNumber && null != renewaleffectiveDate && null != suffix && null != companyCode && null != policysource && !policysource.equalsIgnoreCase("ARS")){
						data.addToLog(data.getCurrentElement(), "MEt360/A360 Policy No: "+policyNumber);
						wsurl = wsurl.replace(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingAccountNumber).replace(Constants.S_FWS_INT_POLICY_NO, policyNumber).replace(Constants.S_FWS_POLICY_EFF_DATE, renewaleffectiveDate).replace(Constants.S_FWS_POLICY_SUFFIX, suffix).replace(Constants.S_FWS_POLICY_COMPANY_CODE, companyCode);
					}
					
					//Changes based on the case CS1150152 - 'TB1' Error when querying for FWS ARS billing data  end //

					LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

					data.addToLog(currElementName, "URl : " + wsurl);
					data.addToLog(currElementName, "CALL ID : " + callId);
					data.addToLog(currElementName, "Billing Acct No : " + billingAccountNumber);
					data.addToLog(currElementName, "Company Code : " + companyCode);
					data.addToLog(currElementName, "Policy Number : " + policyNumber);

					data.addToLog(currElementName, "Eff Date : " + renewaleffectiveDate);
					data.addToLog(currElementName, "Suffix : " + suffix);
					data.addToLog(currElementName, "Policy Source " + policysource);
					data.addToLog(currElementName, "Policy State :" + policystate);

					Lookupcall lookups = new Lookupcall();
					//UAT ENV CHGE(PRIYA,SHAIK)
					data.addToLog("API URL: ", wsurl);
					String prefix = "https://api-np-ss.farmersinsurance.com"; 
					String UAT_FLAG="";
			        if(wsurl.startsWith(prefix)) {
			        	UAT_FLAG="YES";
			        }
					 if("YES".equalsIgnoreCase(UAT_FLAG)){
					 region = regionDetails.get(Constants.S_FWS_BILLING_LOOKUP_URL);
					}else {
						region="";
					}
					JSONObject responses = lookups.GetFWSBillingLookup(wsurl, callId, policysource, policystate, lob, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context,region,UAT_FLAG);
					data.addToLog("Secondary (Error code - NPA) FWS Billing response ::", responses.toString());
					//Mustan - Alerting Mechanism ** Response Code Capture
					apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
					if (responses != null) {
						
						if(responses.containsKey(Constants.REQUEST_BODY)) {
							strReqBody = responses.get(Constants.REQUEST_BODY).toString();
						}
						
						if ((responses.containsKey(Constants.RESPONSE_CODE)) && ((Integer) responses.get(Constants.RESPONSE_CODE)) == 200 && (responses.containsKey("responseBody"))) {
							data.addToLog(currElementName, "Set Secondary FWS Billing API Response into session with the key name of " + currElementName + "_RESP");
							strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
							data.setSessionData(currElementName + "_RESP", responses.get(Constants.RESPONSE_BODY));
							//strExitState = apiResponseManupulation_FWSBillingLookup(data, caa, currElementName, strRespBody);
						}  else {
							if(responses.get(Constants.RESPONSE_MSG)!=null)
								strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
							data.addToLog(currElementName, "Secondary API Hit not successful :: API Response :: "+responses);
						}
					}
				} else {
					data.addToLog(currElementName, "Mandatory field missing for API Call");
				}
			} catch (Exception e) {
				data.addToLog(currElementName, "Exception in SecondaryFWSBillingLookups API call  :: " + e);
				caa.printStackTrace(e);
			}
			try {
				data.addToLog(currElementName, "Reporting STARTS for Secondary FWS Billing LookUp");
				objHostDetails.startHostReport(currElementName, "SBP_HOST_001_FWS_BILLING_LOOKUP", strReqBody, region,(String) data.getSessionData(Constants.S_FWS_BILLING_LOOKUP_URL));
				objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
				data.addToLog(currElementName, "Reporting ENDS for FWS Billing LookUp");
			} catch (Exception e1) {
				data.addToLog(currElementName,
						"Exception while forming host reporting for SBP_HOST_001_FWS_BILLING_LOOKUP  FWSBillingLookup call  :: " + e1);
				caa.printStackTrace(e1);
			}
			return strRespBody;
		}
		

		private String apiResponseManupulation_FWSBillingLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			String strExitState = Constants.ER;
			try {
				int secondaryAPICall = 0;
				String policySource = (String) data.getSessionData("S_POLICY_SOURCE");
				String nextPaymentAmount = null;
				String nextPaymentDate = null;
				String lastPaymentAmount = null;
				String lastPaymentDate = null;
				String secondarystrRespBody = null;
				Double nextPayAmount = 0.00;
				boolean isSecondaryAPICallRequired = false;
				String lastPaymentAmountAndDate = null;

				String PolicyStatus = FWSBillingPolicyStatus(data, caa, currElementName, strRespBody);
				data.addToLog(currElementName, "Policy Status :: "+PolicyStatus);
				if (null != PolicyStatus && PolicyStatus.equalsIgnoreCase("ACTIVE")) {
					data.setSessionData("S_POLICY_STATUS", "ACTIVE");
				}
				else {
					data.setSessionData("S_POLICY_STATUS", "CANCELLED");
				}
				
				
				
				boolean billinginforestricted = checkBillinInfoRestricted(data, caa, currElementName, strRespBody);
				data.addToLog(currElementName, "Billing Info Restricted :: "+billinginforestricted);
				if (billinginforestricted) {
					data.setSessionData("S_BILLING_INFO_RESTRICTED", "TRUE");
				}
				else{
					data.setSessionData("S_BILLING_INFO_RESTRICTED", "FALSE");
				}
				
				String DNRFlag = checkDNRFlag(data, caa, currElementName, strRespBody);
				if(DNRFlag !=null && !DNRFlag.isEmpty() && DNRFlag.equals("Y")) {
					data.setSessionData("S_FWS_DNR_FLAG", "Y");
				}else {
					data.setSessionData("S_FWS_DNR_FLAG", "N");
				}
				
				boolean isAutoPayEnabled = isAutoPayEnabled(data, caa, currElementName, strRespBody);
				data.addToLog(currElementName, "AutoPay Enabled? :: "+isAutoPayEnabled);
				if (isAutoPayEnabled) {
					data.setSessionData("S_AUTOPAY_ENABLED", "TRUE");
				}
				else {
					data.setSessionData("S_AUTOPAY_ENABLED", "FALSE");
				}
				
				boolean isPaymentAllowed = isPaymentAllowed(data, caa, currElementName, strRespBody);
				data.addToLog(currElementName, "Is Payment Allowed :: "+isPaymentAllowed);
				if (isPaymentAllowed) {
					data.setSessionData("S_PAYMENT_ALLOWED", "TRUE");
				}
				else {
					data.setSessionData("S_PAYMENT_ALLOWED", "FALSE");
				}
				
				lastPaymentAmountAndDate = getLastPaymentAmountAndDate(data, caa, currElementName, strRespBody);
				if (null != lastPaymentAmountAndDate) {
					String[] lastPaymentAndAmountSplitter = lastPaymentAmountAndDate.split(",");
					lastPaymentAmount = lastPaymentAndAmountSplitter[0];
					lastPaymentDate = lastPaymentAndAmountSplitter[1];
					
					if (null != policySource && policySource.equalsIgnoreCase("ARS")) {
						if (null != lastPaymentDate) {
							SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
							SimpleDateFormat resultSDF = new SimpleDateFormat("yyyyMMdd");
							lastPaymentDate = resultSDF.format(inputSDF.parse(lastPaymentDate));
						}
					}
					else {
						if (null != lastPaymentDate) {
							SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat resultSDF = new SimpleDateFormat("yyyyMMdd");
							lastPaymentDate = resultSDF.format(inputSDF.parse(lastPaymentDate));
						}
					}
					
					data.setSessionData("S_LastPayment_Amount", lastPaymentAmount);
					data.setSessionData("S_LastPayment_Date", lastPaymentDate);
					data.setSessionData("S_LAST_PMNT_AVAIL", "Y");
					data.addToLog(currElementName, "LAST PAYMENT AMOUNT & DATE AVAILABLE :: LAST PAYMENT AMOUNT :: "+lastPaymentAmount+" :: NEXT PAYMENT DATE :: "+lastPaymentDate);
				}
				else {
					data.addToLog(currElementName, "LAST PAYMENT AMOUNT/DATE NOT AVAILABLE :: LAST PAYMENT AMOUNT :: "+lastPaymentAmount+" :: LAST PAYMENT DATE :: "+lastPaymentDate);
					data.setSessionData("S_LAST_PMNT_AVAIL", "N");
				}
				
				nextPaymentAmount = comboBalanceCheck(data, caa, currElementName, strRespBody);
				if (null != nextPaymentAmount) {
					nextPayAmount = Double.parseDouble(nextPaymentAmount);
					data.setSessionData("Full_BALANCE_DUE", nextPaymentAmount);
					
					if (nextPayAmount > 0.00) {
						nextPaymentDate = comboGetDueDate(data, caa, currElementName, strRespBody);
						
						if (null != policySource && policySource.equalsIgnoreCase("ARS")) {
							if (null != nextPaymentDate) {
								SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
								SimpleDateFormat resultSDF = new SimpleDateFormat("yyyyMMdd");
								nextPaymentDate = resultSDF.format(inputSDF.parse(nextPaymentDate));
							}
						}
						else {
							if (null != nextPaymentDate) {
								SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd");
								SimpleDateFormat resultSDF = new SimpleDateFormat("yyyyMMdd");
								nextPaymentDate = resultSDF.format(inputSDF.parse(nextPaymentDate));
							}
						}
						
						if (null != nextPaymentAmount && null != nextPaymentDate) {
							data.setSessionData("S_NextPayment_Amount", nextPaymentAmount);
							data.setSessionData("S_NextPayment_Date", nextPaymentDate);
							data.setSessionData("S_Current_Balance", nextPaymentAmount);
							data.setSessionData("S_NEXT_PMNT_AVAIL", "Y");
							data.addToLog(currElementName, "COMBO NEXT PAYMENT AMOUNT & DATE AVAILABLE :: NEXT PAYMENT AMOUNT :: "+nextPaymentAmount+" :: NEXT PAYMENT DATE :: "+nextPaymentDate);
							data.setSessionData("S_CURRENTBALANCE", "TRUE");
							
						}
						else {
							data.addToLog(currElementName, "COMBO NEXT PAYMENT AMOUNT/DATE NOT AVAILABLE :: NEXT PAYMENT AMOUNT :: "+nextPaymentAmount+" :: NEXT PAYMENT DATE :: "+nextPaymentDate);
							data.setSessionData("S_CURRENTBALANCE", "FALSE");
							data.setSessionData("S_NEXT_PMNT_AVAIL", "N");
						}
					}
				}
				else {
					setFullBalanceIntoSession(data, caa, currElementName, strRespBody);
				}
				
				if(nextPaymentAmount == null || nextPaymentDate == null) {
					String nextPaymentAmountAndDate = NextPaymentAndNextPaymentDateCheck(data, caa, currElementName, strRespBody);
					if (null != nextPaymentAmountAndDate) {
						String[] dateAndAmountSplitter = nextPaymentAmountAndDate.split(",");
							nextPaymentAmount = dateAndAmountSplitter[0];
							nextPaymentDate = dateAndAmountSplitter[1];
					}
					
					if (null != nextPaymentAmount) {
						nextPayAmount = Double.parseDouble(nextPaymentAmount);
					}
					
					if (null != policySource && policySource.equalsIgnoreCase("ARS")) {
						if (null != nextPaymentDate) {
							SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
							SimpleDateFormat resultSDF = new SimpleDateFormat("yyyyMMdd");
							nextPaymentDate = resultSDF.format(inputSDF.parse(nextPaymentDate));
						}
					}
					else {
						if (null != nextPaymentDate) {
							SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat resultSDF = new SimpleDateFormat("yyyyMMdd");
							nextPaymentDate = resultSDF.format(inputSDF.parse(nextPaymentDate));
						}
					}
					
					if (null != nextPaymentAmount && null != nextPaymentDate && nextPayAmount > 0.00 && !nextPaymentDate.isEmpty()) {
						data.setSessionData("S_NextPayment_Amount", nextPaymentAmount);
						data.setSessionData("S_NextPayment_Date", nextPaymentDate);
						data.setSessionData("S_Current_Balance", nextPaymentAmount);
						data.setSessionData("S_NEXT_PMNT_AVAIL", "Y");
						data.addToLog(currElementName, "NEXT PAYMENT AMOUNT & DATE AVAILABLE :: NEXT PAYMENT AMOUNT :: "+nextPaymentAmount+" :: NEXT PAYMENT DATE :: "+nextPaymentDate);
						data.setSessionData("S_CURRENTBALANCE", "TRUE");
					}
					else {
						isSecondaryAPICallRequired = isSecondaryAPICallRequired(data, caa, currElementName, strRespBody);
						
						String Counter = data.getSessionData("S_Secondary_API_Call") == null ? "0" : (String) data.getSessionData("S_Secondary_API_Call");
						data.addToLog(currElementName, "Counter :: "+Counter);
						
						try {
							secondaryAPICall = Integer.parseInt(Counter);
						}
						catch (Exception e) {
							data.addToLog(currElementName, "Exception in Parsing Counter :: " + e);
							caa.printStackTrace(e);
							secondaryAPICall=0;
						}
						
						if (isSecondaryAPICallRequired && secondaryAPICall == 0) {
							strRespBody = SecondaryFWSBillingLookup(data, caa, currElementName);
							data.addToLog(currElementName, "Balance Not available && Secondary API call required :: Error Code = NPA :: Renewal Effective Date :: "+data.getSessionData("S_FWS_POLICY_REN_EFF_DATE"));
							secondaryAPICall += 1;
							data.setSessionData("S_Secondary_API_Call", ""+secondaryAPICall);
							if (null != strRespBody && !strRespBody.isEmpty()) {
								apiResponseManupulation_FWSBillingLookup(data, caa, currElementName, strRespBody);
							}
						}
						else{
							data.addToLog(currElementName, "Balance Not available && Secondary API call not required :: Amount Paid in full :: Only play Last Payment Amount & date to be considered if available");
							data.setSessionData("S_CURRENTBALANCE", "FALSE");
							data.setSessionData("S_NEXT_PMNT_AVAIL", "N");
						}
					}
				}
				else {
					setMinimumDueintoSession(data, caa, currElementName, strRespBody);
				}
					setDataIntoSession(data, caa, currElementName, strRespBody, nextPaymentDate, policySource);
					//setFullBalanceAndMinimumDueintoSession(data, caa, currElementName, strRespBody);
					
					
			} 
			catch (Exception e) {
				data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
				caa.printStackTrace(e);
			}
			
			strExitState = Constants.SU;
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
		
		public String FWSBillingPolicyStatus(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			JSONObject resp = null;
			JSONObject billingsummaryobj = null;
			JSONObject returnmsgObj = null;
			JSONObject policyobj = null;
			String cancelStatus = null;
			String statuscode = null;
			String errorcode = null;
			String finalStatus = "";
			String FWSPolicyType = (String) data.getSessionData("S_POLICY_SOURCE");
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
				
					if (null != resp) {
					
						if (null != FWSPolicyType) {
						
							//Check Policy Status for ARS
							if (FWSPolicyType.equalsIgnoreCase("ARS")) {
							
								if (resp.containsKey("billingSummary")) {
									billingsummaryobj = (JSONObject) resp.get("billingSummary");
								
									if (null != billingsummaryobj) {
									
										if (billingsummaryobj.containsKey("returnMessage")) {
											returnmsgObj = (JSONObject) billingsummaryobj.get("returnMessage");
										
											if (null != returnmsgObj) {
											
												if (returnmsgObj.containsKey("statusCode")) {
													statuscode = (String) returnmsgObj.get("statusCode");
												}
												if (returnmsgObj.containsKey("errorCode")) {
													errorcode = (String) returnmsgObj.get("errorCode");
												}
											
												if (null != statuscode && (statuscode.equalsIgnoreCase("CANC-NO EP") || statuscode.equalsIgnoreCase("CANC-PREM DUE") || statuscode.equalsIgnoreCase("CANCEL") || statuscode.equalsIgnoreCase("NON PAYCANCEL"))) {
													finalStatus = "CANCELLED";
												}
												else if (null != errorcode && (errorcode.equalsIgnoreCase("PB4") || errorcode.equalsIgnoreCase("PB5") || errorcode.equalsIgnoreCase("NPR") || errorcode.equalsIgnoreCase("NPAC"))) {
													finalStatus = "CANCELLED";
												}
												else {
													finalStatus = "ACTIVE";
												}
											}
										}
									}
								}
							}
							//Check for ARS is finished
						
							//Check Policy Status for Met360
							else {
							
								if (resp.containsKey("billingSummary")) {
									billingsummaryobj = (JSONObject) resp.get("billingSummary");
								
									if (null != billingsummaryobj) {
									
										if(billingsummaryobj.containsKey("policy")) {
											policyobj = (JSONObject) billingsummaryobj.get("policy");
										
											if (null != policyobj && policyobj.containsKey("cancelStatus")) {
												cancelStatus = (String) policyobj.get("cancelStatus");
											
												if (null != cancelStatus && (cancelStatus.equalsIgnoreCase("CANCELLED") || cancelStatus.equalsIgnoreCase("CANCELED"))) {
													finalStatus = "CANCELLED";
												}
												else {
													finalStatus = "ACTIVE";
												}
											}
										}
									}
								}	
							}
							//Check for Met360 is finished
						}
					}
				}
			} catch (ParseException e) {
				data.addToLog(data.getCurrentElement(), "Exception in FWSBillingPolicyStatus Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			return finalStatus;
		}
		
		public boolean checkBillinInfoRestricted(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			boolean billinginforestricted = true;
			JSONObject resp = null;
			JSONObject billingsummaryobj = null;
			JSONObject returnmsgObj = null;
			String errorcode = null;
			
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
				
					if (resp != null) {
					
						if (resp.containsKey("billingSummary")) {
							billingsummaryobj = (JSONObject) resp.get("billingSummary");
						
							if (null != billingsummaryobj) {
							
								if (billingsummaryobj.containsKey("returnMessage")) {
									returnmsgObj = (JSONObject) billingsummaryobj.get("returnMessage");
								
									if (null != returnmsgObj) {
									
										if (returnmsgObj.containsKey("errorCode")) {
											errorcode = (String) returnmsgObj.get("errorCode");
											
											if (null != errorcode && (errorcode.equalsIgnoreCase("PB7Z") || errorcode.equalsIgnoreCase("PB7Y") || errorcode.equalsIgnoreCase("PB1") || errorcode.equalsIgnoreCase("PB7"))) {
												billinginforestricted = true;
											}
											else {
												billinginforestricted = false;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in checkBillinInfoRestricted Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			return billinginforestricted;
		}
		
		public boolean isAutoPayEnabled(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			JSONObject resp = null;
			boolean isautopayenabled = false;
			JSONObject billingsummaryobj = null;
			String paymentSiteCD = null;
			JSONObject terms = null;
			String FWSPolicyType = (String) data.getSessionData("S_POLICY_SOURCE");
			String billingaccountnumber = (String) data.getSessionData("S_FWS_POLICY_BILLING_ACCT_NO");
			String paymentMethod = null;
			
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
				
					if (resp != null) {
					
						if (null != FWSPolicyType) {
						
							//Check if Autopay is enabled if policy Type is ARS
							if (null != billingaccountnumber && FWSPolicyType.equalsIgnoreCase("ARS")) {
								paymentSiteCD = billingaccountnumber.substring(0, 2);
							
								if (paymentSiteCD.equalsIgnoreCase("90") || paymentSiteCD.equalsIgnoreCase("3Z")) {
									isautopayenabled = true;
								}
								else {
									isautopayenabled = false;
								}
							}
							//Check for ARS End
						
							//Check if Autopay is enabled if policy Type is Met360
							else{
								if(resp.containsKey("billingSummary")) {
									billingsummaryobj = (JSONObject) resp.get("billingsummaryobj");
								
									if (null != billingsummaryobj) {
									
										if (billingsummaryobj.containsKey("terms")) {
											terms = (JSONObject) billingsummaryobj.get("terms");
										
											if (null != terms && terms.containsKey("paymentMethod")) {
												paymentMethod = (String) terms.get("paymentMethod");
											}
										
											if (null != paymentMethod && (paymentMethod.equalsIgnoreCase("ach") || paymentMethod.equalsIgnoreCase("creditcard") || paymentMethod.equalsIgnoreCase("payrolldeduct_ml"))) {
												isautopayenabled = true;
											}
											else {
												isautopayenabled = false;
											}
										}
									}
								}
							}
							//Check for Met360 End
						}
					}
				}
			}
			catch (Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in isAutoPayEnabled Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			return isautopayenabled;
		}
		
		public String comboBalanceCheck(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			JSONObject resp = null;
			JSONObject billingsummaryobj = null;
			JSONArray duesArray = null;
			JSONObject policyObj = null;
			String paymentLevelCode = null;
			JSONObject duesObject = null;
			String dueType = null;
			String finalDueAmount = null;
			String errorcode = null;
			JSONObject returnmsgObj = null;
			String comboPackageIndicator = (String) data.getSessionData("S_FWS_COMBO_PACKAGE_INDICATOR");
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					
					if (resp != null) {
						
						if (resp.containsKey("billingSummary")) {
							billingsummaryobj = (JSONObject) resp.get("billingSummary");
							
							if (null != billingsummaryobj) {
							
								if (billingsummaryobj.containsKey("policy")) {
									policyObj = (JSONObject) billingsummaryobj.get("policy");
								
									if (null != policyObj && policyObj.containsKey("paymentLevelCode")) {
									paymentLevelCode = (String) policyObj.get("paymentLevelCode");
									}
								}
							
								if (billingsummaryobj.containsKey("returnMessage")) {
									returnmsgObj = (JSONObject) billingsummaryobj.get("returnMessage");
							
									if (null != returnmsgObj) {
								
										if (returnmsgObj.containsKey("errorCode")) {
										errorcode = (String) returnmsgObj.get("errorCode");
										}
									}
								}
							
								if (billingsummaryobj.containsKey("dues")) {
									duesArray = (JSONArray) billingsummaryobj.get("dues");
									
									if (null != comboPackageIndicator && null != paymentLevelCode && null != duesArray && (comboPackageIndicator.equalsIgnoreCase("C") || comboPackageIndicator.equalsIgnoreCase("G")) && paymentLevelCode.equalsIgnoreCase("A")) {
										
										for (Object duesIterator : duesArray) {
											duesObject = (JSONObject) duesIterator;
											
											if (null != duesObject) {
												
												if (duesObject.containsKey("type")) {
													dueType = (String) duesObject.get("type");
													
													if (null != dueType && dueType.equalsIgnoreCase("COMBOUNPAIDPREM") && duesObject.containsKey("amount")) {
														String dueAmount = (String) duesObject.get("amount");
														Double amount = 0.00;
														
														if (null != dueAmount) {
															amount = Double.parseDouble(dueAmount);
														}
														
														if (amount > 0.00) {
															finalDueAmount = dueAmount;
															data.setSessionData("S_FULLBALANCE", finalDueAmount);
															data.setSessionData("Full_BALANCE_DUE", finalDueAmount);
															data.addToLog(currElementName, "Setting Full Balance (1) due into session :: "+finalDueAmount);
														}
													}
													
													if (null != errorcode && errorcode.equalsIgnoreCase("PB1X")) {
														if (null != dueType && dueType.equalsIgnoreCase("MAX") && duesObject.containsKey("amount")) {
															String dueAmount = (String) duesObject.get("amount");
															Double amount = 0.00;
															
															if (null != dueAmount) {
																amount = Double.parseDouble(dueAmount);
															}
															
															if (amount > 0.00) {
																finalDueAmount = dueAmount; 
																data.setSessionData("S_FULLBALANCE", finalDueAmount);
																data.setSessionData("Full_BALANCE_DUE", finalDueAmount);
																data.addToLog(currElementName, "Setting Full Balance (2) due into session :: "+finalDueAmount);
																return finalDueAmount;
															}
														}
													}
													if (null != duesArray && (null == finalDueAmount || Double.parseDouble(finalDueAmount) <= 0.00)) {
														for (Object duesIterators : duesArray) {
															duesObject = (JSONObject) duesIterators;
															
															if (duesObject.containsKey("type")) {
																dueType = (String) duesObject.get("type");
																
																if (null != dueType && dueType.equalsIgnoreCase("MAX") && duesObject.containsKey("amount")) {
																	String dueAmount = (String) duesObject.get("amount");
																	Double amount = 0.00;
																	
																	if (null != dueAmount) {
																		amount = Double.parseDouble(dueAmount);
																	}
																	
																	if (amount > 0.00) {
																		finalDueAmount = dueAmount;
																		data.setSessionData("S_FULLBALANCE", finalDueAmount);
																		data.setSessionData("Full_BALANCE_DUE", finalDueAmount);
																		data.addToLog(currElementName, "Setting Full Balance (3) due into session :: "+finalDueAmount);
																		return finalDueAmount;
																	}
																}
																	
																if (null != dueType && null == finalDueAmount && dueType.equalsIgnoreCase("REINSTATE") && duesObject.containsKey("amount")) {
																	String dueAmount = (String) duesObject.get("amount");
																	Double amount = 0.00;
																	if (null != dueAmount) {
																		amount = Double.parseDouble(dueAmount);
																	}
																	
																	if (null != dueAmount && amount > 0.00) {
																		finalDueAmount = dueAmount;
																		data.setSessionData("S_FULLBALANCE", finalDueAmount);
																		data.setSessionData("Full_BALANCE_DUE", finalDueAmount);
																		data.addToLog(currElementName, "Setting Full Balance (4) due into session :: "+finalDueAmount);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
									else {
										for (Object duesIterator : duesArray) {
											duesObject = (JSONObject) duesIterator;
											
											if (null != duesObject) {
												if (duesObject.containsKey("type")) {
													dueType = (String) duesObject.get("type");
													
													if (null != dueType) {
														
														if (dueType.equalsIgnoreCase("MAX") && duesObject.containsKey("amount")) {
															Double amount = 0.00;
															String dueAmount = (String) duesObject.get("amount");
															amount = Double.parseDouble(dueAmount);
															
															if (amount > 0.00) {
																data.setSessionData("S_FULLBALANCE", dueAmount);
																data.setSessionData("Full_BALANCE_DUE", dueAmount);
																data.addToLog(currElementName, "Setting Full Balance (5) due into session :: "+dueAmount);
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in balanceCheck Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			return finalDueAmount;
		}
		
		public String comboGetDueDate(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			String dueDate = null;
			String finaldueDate = null;
			JSONObject resp = null;
			JSONObject policyObj = null;
			JSONArray duesArray = null;
			JSONObject duesObject = null;
			String dueType = null;
			JSONObject billingSummaryObj = null;
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					if (resp != null) {
						
						if (resp.containsKey("billingSummary")) {
							billingSummaryObj = (JSONObject) resp.get("billingSummary");
							
							if (billingSummaryObj.containsKey("policy")) {
								policyObj = (JSONObject) billingSummaryObj.get("policy");
								
								if (null != policyObj && policyObj.containsKey("dues")) {
									duesArray = (JSONArray) policyObj.get("dues");
									
									if (null != duesArray) {
										
										for(Object duesIterator : duesArray) {
											duesObject = (JSONObject) duesIterator;
											
											if (null != duesObject) {
												
												if (duesObject.containsKey("type")) {
													dueType = (String) duesObject.get("type");
													
													if (null != dueType && dueType.equalsIgnoreCase("NEXT") && duesObject.containsKey("date")) {
														dueDate = (String) duesObject.get("date");
														
														if (null != dueDate && !"".equalsIgnoreCase(dueDate)) {
															finaldueDate = dueDate;
														}
														else{
															if (null != dueType && dueType.equalsIgnoreCase("NEXTACCOUNT") && duesObject.containsKey("date")) {
																dueDate = (String) duesObject.get("date");
																
																if (null != dueDate && !"".equalsIgnoreCase(dueDate)) {
																	finaldueDate = dueDate;
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in getDueDate Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			return finaldueDate;
		}
		
		public String NextPaymentAndNextPaymentDateCheck(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			JSONObject resp = null;
			JSONObject billingsummaryobj = null;
			JSONArray duesArray = null;
			JSONObject duesObject = null;
			String dueType = null;
			String finalDueAmountAndDate = null;
			
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					if (resp != null) {
						
						if (resp.containsKey("billingSummary")) {
							billingsummaryobj = (JSONObject) resp.get("billingSummary");
							
							if (null != billingsummaryobj) {
								
								if (billingsummaryobj.containsKey("dues")) {
									duesArray = (JSONArray) billingsummaryobj.get("dues");
									
									if (null != duesArray) {
										
										for (Object duesIterator : duesArray) {
											duesObject = (JSONObject) duesIterator;
											
											if (null != duesObject) {
												
												if (duesObject.containsKey("type")) {
													dueType = (String) duesObject.get("type");
													
													if (null != dueType) {
														
														if (dueType.equalsIgnoreCase("NEXT") && duesObject.containsKey("amount") && duesObject.containsKey("date")) {
															String dueAmount = (String) duesObject.get("amount");
															String dueDate = (String) duesObject.get("date");
															Double amount = 0.00;
															
															if (null!= dueAmount) {
																amount = Double.parseDouble(dueAmount);
															}
															
															if (null != dueAmount && null != dueDate &&  amount > 0.00) {
																data.setSessionData("S_MINIMUMDUE", dueAmount);
																data.addToLog(currElementName, "Setting Minimum Due Balance due into session :: "+dueAmount);
																finalDueAmountAndDate = dueAmount+","+dueDate;
																return finalDueAmountAndDate;
															}
														}
														else if(dueType.equalsIgnoreCase("NEXTACCOUNT") && duesObject.containsKey("amount") && duesObject.containsKey("date")){
															String dueAmount = (String) duesObject.get("amount");
															String dueDate = (String) duesObject.get("date");
															Double amount = 0.00;
															
															if (null != dueAmount) {
																amount = Double.parseDouble(dueAmount);
															}
															
															if (null != dueDate &&  amount > 0.00) {
																data.setSessionData("S_MINIMUMDUE", dueAmount);
																data.addToLog(currElementName, "Setting Minimum Due Balance due into session :: "+dueAmount);
																finalDueAmountAndDate = dueAmount+","+dueDate;
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in balanceCheck Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			return finalDueAmountAndDate;
		}
		
		
		public boolean isSecondaryAPICallRequired(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			boolean isAPICallRequired = false;
			JSONObject resp = null;
			JSONObject billingSummaryObj = null;
			JSONObject returnMsgObj = null;
			String errorcode = null;
			
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					if (resp != null) {
						
						if (resp.containsKey("billingSummary")) {
							billingSummaryObj = (JSONObject) resp.get("billingSummary");
							
							if (null != billingSummaryObj && billingSummaryObj.containsKey("returnMessage")) {
								returnMsgObj = (JSONObject) billingSummaryObj.get("returnMessage");
								
								if (null != returnMsgObj && returnMsgObj.containsKey("errorCode")) {
									errorcode = (String) returnMsgObj.get("errorCode");
									
									if (null != errorcode && errorcode.equalsIgnoreCase("NPA")) {
										isAPICallRequired = true;
									}
									else {
										isAPICallRequired = false;
									}
								}
							}
						}
					}
				}
				
			}
			catch (Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in getDueDate Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			return isAPICallRequired;
		}
		
		public boolean isPaymentAllowed(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			boolean isPaymentAllowed = true;
			String paymentAllowed = null;
			JSONObject billingSummaryObj = null;
			String FWSPolicyType = (String) data.getSessionData("S_POLICY_SOURCE");
			JSONObject resp = null;
			JSONObject returnMsgObj = null;
			String errorcode = null;
			JSONObject termsObj = null;
			
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					if (resp != null) {
						
						if (resp.containsKey("billingSummary")) {
							billingSummaryObj = (JSONObject) resp.get("billingSummary");
							
							if (null != FWSPolicyType) {
								
								if (FWSPolicyType.equalsIgnoreCase("ARS")) {
									
									if (null != billingSummaryObj && billingSummaryObj.containsKey("returnMessage")) {
										returnMsgObj = (JSONObject) billingSummaryObj.get("returnMessage");
										
										if (null != returnMsgObj && returnMsgObj.containsKey("errorCode")) {
											errorcode = (String) returnMsgObj.get("errorCode");
											
											if (null != errorcode && (errorcode.equalsIgnoreCase("NPAC") || errorcode.equalsIgnoreCase("NPR") || errorcode.equalsIgnoreCase("R0") || errorcode.equalsIgnoreCase("TB1") || errorcode.equalsIgnoreCase("PB8") || errorcode.equalsIgnoreCase("PB9") || errorcode.equalsIgnoreCase("PB1") || errorcode.equalsIgnoreCase("PB7") || errorcode.equalsIgnoreCase("TRANSFER"))) {
												isPaymentAllowed = false;
											}
											else {
												isPaymentAllowed = true;
											}
										}
									}
								}
								else {
									if (null != billingSummaryObj && billingSummaryObj.containsKey("terms")) {
										termsObj = (JSONObject) billingSummaryObj.get("terms");
										
										if (null != termsObj && termsObj.containsKey("paymentAllowed")) {
											paymentAllowed = (String) termsObj.get("paymentAllowed");
											
											if (null != paymentAllowed && paymentAllowed.equalsIgnoreCase("N")) {
												isPaymentAllowed = false;
											}
											else {
												isPaymentAllowed = true;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in isPaymentAllowed Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			return isPaymentAllowed;
		}
		
		public String getLastPaymentAmountAndDate(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			String finalLastPaymentAmountAndDate = null;
			String lastPaymentAmount = null;
			String lastPaymentDate = null;
			JSONObject resp = null;
			JSONObject billingSummaryObj = null;
			JSONArray dueTypesArray = null;
			JSONObject dueTypesObj = null;
			String dueTypes = null;
			Double amount = 0.00;
			
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					if (resp != null) {
						
						if (resp.containsKey("billingSummary")) {
							billingSummaryObj = (JSONObject) resp.get("billingSummary");
							
							if (null != billingSummaryObj) {
								
								if (billingSummaryObj.containsKey("dues")) {
									dueTypesArray = (JSONArray) billingSummaryObj.get("dues");
									
									if (null != dueTypesArray) {
										
										for (Object dueTypesIterator : dueTypesArray) {
											dueTypesObj = (JSONObject) dueTypesIterator;
											
											if (null != dueTypesObj && dueTypesObj.containsKey("type")) {
												dueTypes = (String) dueTypesObj.get("type");
												
												if(null != dueTypes) {
													
													if (dueTypes.equalsIgnoreCase("LAST") && dueTypesObj.containsKey("amount") && dueTypesObj.containsKey("date")) {
														lastPaymentAmount = (String) dueTypesObj.get("amount");
														lastPaymentDate = (String) dueTypesObj.get("date");
														
														if (null != lastPaymentAmount) {
															amount = Double.parseDouble(lastPaymentAmount);
															
															if (amount > 0.00) {
																finalLastPaymentAmountAndDate = lastPaymentAmount+","+lastPaymentDate;
															}
														}
													}
												}
												
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch(Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in getLastPaymentAmountAndDate Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			
			return finalLastPaymentAmountAndDate;
		}
		
		public void setDataIntoSession(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, String DueDate, String policySource) {
			JSONObject resp = null;
			JSONObject billingSummaryObj = null;
			JSONObject policyObj = null;
			String paymentLevelCode = null;
			String billingAccountNumber = null;
			String payrollDeduct = null;
			JSONArray addressArr = null;
			JSONObject addressObj = null;
			String zip = null;
			
			try {
				
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					if (null != resp) {
						if (resp.containsKey("billingSummary")) {
							billingSummaryObj = (JSONObject) resp.get("billingSummary");
							
							if (null != billingSummaryObj && billingSummaryObj.containsKey("policy")) {
								policyObj = (JSONObject) billingSummaryObj.get("policy");
								
								if (null != policyObj) {
									
									if (policyObj.containsKey("paymentLevelCode")) {
										paymentLevelCode = (String) policyObj.get("paymentLevelCode");
										if (null != paymentLevelCode) {
											data.setSessionData("S_PAYMENT_LEVEL_CODE", paymentLevelCode);
											data.addToLog(currElementName, "Setting Payment level Code into session :: "+data.getSessionData("S_PAYMENT_LEVEL_CODE"));
										}
									}
									
									if (policyObj.containsKey("addresses")) {
										addressArr = (JSONArray) policyObj.get("addresses");
										
										if (null != addressArr && addressArr.size() > 0) {
											addressObj = (JSONObject) addressArr.get(0);
											
											if (null != addressObj && addressObj.containsKey("zip")) {
												zip = (String) addressObj.get("zip");
												if (null != zip) {
													zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
													data.setSessionData(Constants.S_PAYOR_ZIP_CODE, zip);
													data.addToLog(currElementName, "Overriding Billing zip code into session :: "+data.getSessionData(Constants.S_PAYOR_ZIP_CODE));
												}
											}
										}
									}
								}
							}
							
							billingAccountNumber = (String) data.getSessionData("S_FWS_POLICY_BILLING_ACCT_NO");
							data.addToLog(currElementName, "FWS Billing Account Number :: "+billingAccountNumber);
							
							if (null != billingAccountNumber && billingAccountNumber.length() > 2) {
								
								payrollDeduct = billingAccountNumber.substring(0, 2);
								
								if (null != payrollDeduct && (payrollDeduct.equalsIgnoreCase("00") || payrollDeduct.equalsIgnoreCase("ZZ") || payrollDeduct.equalsIgnoreCase("HB") || payrollDeduct.equalsIgnoreCase("90") || payrollDeduct.equalsIgnoreCase("3Z"))) {
									data.setSessionData("S_PAYROLL_DEDUCT", "OFF");
									data.addToLog(currElementName, "Payment Site CD :: "+payrollDeduct+" :: PAYROLL DEDUCT :: OFF");
								}
								else {
									data.setSessionData("S_PAYROLL_DEDUCT", "ON");
									data.addToLog(currElementName, "Payment Site CD :: "+payrollDeduct+" :: PAYROLL DEDUCT :: ON");
								}
							}
							
							if (null != DueDate && !DueDate.isEmpty()) {
								
								if (null != policySource && policySource.equalsIgnoreCase("ARS")) {
									Date systemDate = new Date();
									SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
									Date providedDate = sdf.parse(DueDate);
									boolean isSystemDateGreater = systemDate.after(providedDate);
									data.setSessionData("S_SYSTEMDATE_GREATER", isSystemDateGreater);
								}
								else {
									Date systemDate = new Date();
									SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
									Date providedDate = sdf.parse(DueDate);
									boolean isSystemDateGreater = systemDate.after(providedDate);
									data.setSessionData("S_SYSTEMDATE_GREATER", isSystemDateGreater);
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in setDataIntoSession Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
		}
		
	/*	public void setFullBalanceAndMinimumDueintoSession(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			JSONObject resp = null;
			JSONObject billingSummaryObj = null;
			String finalFullBalanceAndMinimumDue = null;
			JSONArray dueTypesArray = null;
			JSONObject dueTypesObj = null;
			String dueType = null;
			
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					if (null != resp) {
						if (resp.containsKey("billingSummary")) {
							billingSummaryObj = (JSONObject) resp.get("billingSummary");
							
							if (null != billingSummaryObj && billingSummaryObj.containsKey("dues")) {
								dueTypesArray = (JSONArray) billingSummaryObj.get("dues");
								
								for(Object dueTypesIterator : dueTypesArray) {
									dueTypesObj = (JSONObject) dueTypesIterator;
									if (null != dueTypesObj) {
										if (dueTypesObj.containsKey("type")) {
											dueType = (String) dueTypesObj.get("type");
											
											if (null != dueType) {
												if (dueType.equalsIgnoreCase("MAX") && dueTypesObj.containsKey("amount")) {
													data.setSessionData("S_FULLBALANCE", dueTypesObj.get("amount"));
												}
												
												if (dueType.equalsIgnoreCase("NEXT") && dueTypesObj.containsKey("amount")) {
													data.setSessionData("S_MINIMUMDUE", dueTypesObj.get("amount"));
												}
											}
										}
									}
								}
							}
						}
					}
				}
				
			}
			catch(Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in setDataIntoSession Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
		}	*/

		public void setMinimumDueintoSession(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			
			JSONObject resp = null;
			JSONObject billingSummaryObj = null;
			String finalFullBalanceAndMinimumDue = null;
			JSONArray dueTypesArray = null;
			JSONObject dueTypesObj = null;
			String dueType = null;
			
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					if (null != resp) {
						if (resp.containsKey("billingSummary")) {
							billingSummaryObj = (JSONObject) resp.get("billingSummary");
							
							if (null != billingSummaryObj && billingSummaryObj.containsKey("dues")) {
								dueTypesArray = (JSONArray) billingSummaryObj.get("dues");
								
								for(Object dueTypesIterator : dueTypesArray) {
									dueTypesObj = (JSONObject) dueTypesIterator;
									if (null != dueTypesObj) {
										if (dueTypesObj.containsKey("type")) {
											dueType = (String) dueTypesObj.get("type");
											
											if (dueType.equalsIgnoreCase("NEXT") && dueTypesObj.containsKey("amount")) {
												data.setSessionData("S_MINIMUMDUE", dueTypesObj.get("amount"));
											}
											
											if (dueType.equalsIgnoreCase("NEXTACCOUNT") && dueTypesObj.containsKey("amount") && data.getSessionData("S_MINIMUMDUE") == null) {
												data.setSessionData("S_MINIMUMDUE", dueTypesObj.get("amount"));
											}
										}
									}
								}
							}
						}
					}
				}
				
			}
			catch(Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in setMinimumDueintoSession Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			
		}
		
		
		public void setFullBalanceIntoSession(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
			JSONObject resp = null;
			JSONObject billingSummaryObj = null;
			JSONArray dueTypesArray = null;
			JSONObject dueTypesObj = null;
			String dueType = null;
			
			try {
				if (null != strRespBody) {
					resp = (JSONObject) new JSONParser().parse(strRespBody);
					
					if (null != resp) {
						if (resp.containsKey("billingSummary")) {
							billingSummaryObj = (JSONObject) resp.get("billingSummary");
							
							if (null != billingSummaryObj && billingSummaryObj.containsKey("dues")) {
								dueTypesArray = (JSONArray) billingSummaryObj.get("dues");
								
								for(Object dueTypesIterator : dueTypesArray) {
									dueTypesObj = (JSONObject) dueTypesIterator;
									if (null != dueTypesObj) {
										if (dueTypesObj.containsKey("type")) {
											dueType = (String) dueTypesObj.get("type");
											
											if (dueType.equalsIgnoreCase("MAX") && dueTypesObj.containsKey("amount")) {
												data.setSessionData("Full_BALANCE_DUE", dueTypesObj.get("amount"));
											}
										}
									}
								}
							}
						}
					}
				}
				
			}
			catch(Exception e) {
				data.addToLog(data.getCurrentElement(), "Exception in setFullBalanceIntoSession Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
		}
		
		private String checkDNRFlag(DecisionElementData data, CommonAPIAccess caa, String currentElementName,String strRespBody) {
			String exitState = Constants.EmptyString;
			JSONObject resp;
			try {
				resp = (JSONObject) new JSONParser().parse(strRespBody);
				JSONObject billingSummaryObj = new JSONObject();
				JSONObject policyObj = new JSONObject();
				if(resp !=null && !resp.isEmpty()) {
					if (resp.containsKey("billingSummary")) {
						billingSummaryObj = (JSONObject) resp.get("billingSummary");
						if(billingSummaryObj !=null && !billingSummaryObj.isEmpty() && billingSummaryObj.containsKey("policy")) {
							policyObj = (JSONObject) billingSummaryObj.get("policy");
							if(policyObj!=null && !policyObj.isEmpty()&& policyObj.containsKey("doNotRenewInd")) {
								exitState = (String) policyObj.get("doNotRenewInd");
							}else {
								data.addToLog(currentElementName, "doNotRenewInd key is not exsist in policyObj from billing summary API Response");
							}
						}
				}else {
					data.addToLog(currentElementName, "Billing API response empty in checkDNRFlag");
				}
				}
			} catch (Exception e) {
				
				data.addToLog(data.getCurrentElement(), "Exception in checkDNRFlag Method in FWS Billing & Payments  :: " + e);
				caa.printStackTrace(e);
			}
			return exitState;
			
			
		}
		
		public static void main(String[] args) {
			try {
				String DueDate = "2024-01-30";
				Date systemDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date providedDate = sdf.parse(DueDate);
				boolean isSystemDateGreater = systemDate.after(providedDate);
				System.out.println("Date Comparison result :: "+isSystemDateGreater);
			}
			catch(Exception e){
				
			}

		}


	}
