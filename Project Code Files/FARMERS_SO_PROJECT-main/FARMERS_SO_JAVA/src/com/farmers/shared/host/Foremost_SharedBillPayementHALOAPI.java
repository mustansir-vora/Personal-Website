package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Foremost_SharedBillPayementHALOAPI extends DecisionElementBase {

    @Override
    public String doDecision(String currElementName, DecisionElementData data) throws Exception {

        String strExitState = Constants.ER;
        CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
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

            if ((data.getSessionData(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL) != null) &&
                (data.getSessionData(Constants.S_CONN_TIMEOUT) != null) &&
                (data.getSessionData(Constants.S_READ_TIMEOUT) != null)) {

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
                //UAT ENV CHANGE START(SHAIK,PRIYA)

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

                data.addToLog(data.getCurrentElement(), "HALOAPI_HOST_001 RESPONSE : " + responses.toString());
                
              //Mustan - Alerting Mechanism ** Response Code Capture
                apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

                if (responses != null) {

                    if (responses.containsKey(Constants.REQUEST_BODY))
                        strReqBody = responses.get(Constants.REQUEST_BODY).toString();

                    if ((responses.containsKey(Constants.RESPONSE_CODE)) &&
                        (((Integer) responses.get(Constants.RESPONSE_CODE)) == 200) &&
                        (responses.containsKey(Constants.RESPONSE_BODY))) {
                        data.addToLog(currElementName,
                            "Set KYCBA_HOST_001 : epcHaloBillingDetailsGroup API Response into session with the key name of " +
                            currElementName + "_RESP");
                        strRespBody = responses.get(Constants.RESPONSE_BODY).toString();

                        data.setSessionData("S_HALO_API_RESP", responses.get(Constants.RESPONSE_BODY));

                        JSONObject HaloAPIResponse = (JSONObject) data.getSessionData("S_HALO_API_RESP");

                        strExitState = apiResponseManupulation_epcHaloBillingDetailsGroup(data, caa, currElementName,
                            HaloAPIResponse);
                    } else {
                        if (responses.get(Constants.RESPONSE_MSG) != null) {
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
            objHostDetails.startHostReport(currElementName, "epcHaloBillingDetailsGroup", strReqBody, region,(String) data.getSessionData(Constants.S_EPCHALO_BILLING_DETAILSGROUP_URL));
            objHostDetails.endHostReport(data, strRespBody,
                strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
            data.addToLog(currElementName, "Reporting ENDS for FOREMOST : " + objHostDetails);
        } catch (Exception e1) {
            data.addToLog(currElementName,
                "Exception while forming host reporting for SBP_HOST_001_FOREMOST  epcHaloBillingDetailsGroup call  :: " +
                e1);
            caa.printStackTrace(e1);
        }

        return strExitState;
    }

    private String apiResponseManupulation_epcHaloBillingDetailsGroup(DecisionElementData data, CommonAPIAccess caa,
        String currElementName, JSONObject haloAPIResponse) {

        String StrExitState = "ER";

        String billingstatusCode = "";

        String Status = "";
        String printstatus = "";
        String billAmount = "0";

        String billDueDate = "";

        String outstandingBalance = "0";

        String PaymentStatus = "";

        String PaymentStatusCode = "";

        String lastpaymentAmount = "0";
        String lastPaymentDate = "";
        String paymentRecordType = "";
        try {

            if (haloAPIResponse != null) {

                data.addToLog(currElementName, "haloAPIResponse Lookup resp :: " + haloAPIResponse);

                JSONArray billingDetailsarray = (JSONArray) haloAPIResponse.get("billingDetails");

                data.addToLog(currElementName, "haloAPIResponse Lookup resp_Billing Details :: " + billingDetailsarray);

                for (Object billingDetailsiterator: billingDetailsarray) {

                    JSONObject billingdetails = (JSONObject) billingDetailsiterator;

                    if (null != billingdetails && billingdetails.containsKey("policy")) {

                        JSONObject policydetails = (JSONObject) billingdetails.get("policy");

                        data.addToLog(currElementName, "haloAPIResponse Lookup resp_policydetails :: " + policydetails);

                        if (null != policydetails && policydetails.containsKey("terms")) {

                            JSONArray termdetails = (JSONArray) policydetails.get("terms");

                            data.addToLog(currElementName, "haloAPIResponse Lookup resp_termdetails :: " + termdetails);

                            data.setSessionData(Constants.S_HALO_API_BillDue, "N");

                            data.setSessionData(Constants.S_HALO_API_Waiting_TO_Bill, "N");
                            data.setSessionData(Constants.S_HALO_API_ZerooutstandingBalance, "N");
                            data.setSessionData(Constants.S_HALO_API_BillAmount, "N");
                            data.setSessionData(Constants.S_HALO_API_BillingFlag, "N");
                            data.setSessionData(Constants.S_HALO_API_PendingFlag, "N");
                            data.setSessionData(Constants.S_HALO_API_ExpiredFlag, "N");
                            data.setSessionData(Constants.S_HALO_API_CancelPendingBillDue, "N");
                            data.setSessionData(Constants.S_HALO_API_ExpirePendingBillDue, "N");
                            data.setSessionData(Constants.S_HALO_API_CancelledGenericFlag, "N");
                            data.setSessionData(Constants.S_HALO_API_CancelledEPFlag, "N");
                            data.setSessionData(Constants.S_HALO_API_CancelledFlag, "N");


                            //****************************************Logic changes 17/07*****************************************************
                            if (termdetails.size() == 0) {
                                StrExitState = "ER";
                                return StrExitState;
                            }

                            if (termdetails.size() >= 2) {

                                for (int i = 0; i < 2; i++) {

                                    JSONObject termdetailsObj = (JSONObject) termdetails.get(i);

                                    Status = (String) termdetailsObj.get("status").toString().trim();                                 

                                    billingstatusCode = (String) termdetailsObj.get("billingStatusCode").toString().trim();

                                    if(Status.equalsIgnoreCase("Expired")&& (billingstatusCode.equalsIgnoreCase("07")||billingstatusCode.equalsIgnoreCase("10")||billingstatusCode.equalsIgnoreCase("12")||billingstatusCode.equalsIgnoreCase("13"))) {
                                    	if(billingstatusCode.equalsIgnoreCase("07")||billingstatusCode.equalsIgnoreCase("12")) {
                                    		data.setSessionData(Constants.S_HALO_API_CancelledFlag, "Y");
                                    		data.setSessionData(Constants.S_HALO_API_CancelledGenericFlag, "Y");
                                        }else if(billingstatusCode.equalsIgnoreCase("13")) {
                                        	data.setSessionData(Constants.S_HALO_API_CancelledFlag, "Y");
                                        	data.setSessionData(Constants.S_HALO_API_CancelledEPFlag, "Y");
                                        }else {
                                        	 data.setSessionData(Constants.S_HALO_API_ExpiredFlag, "Y");
                                        }       
                  
                                    }
                                    if (Status.equalsIgnoreCase("Expired")){
                                        StrExitState = "ER";
                                        break;
                                    }

                                    //code to set next payment and bill due flag for 02/03/04/05  
                                    if (Status.equalsIgnoreCase("Current") || Status.equalsIgnoreCase("Future")) {

                                        if (billingstatusCode.equalsIgnoreCase("02") || billingstatusCode.equalsIgnoreCase("03") || billingstatusCode.equalsIgnoreCase("04") || billingstatusCode.equalsIgnoreCase("05")) {

                                            if (Status.equalsIgnoreCase("Current")) {
                                                Double amount = (Double) termdetailsObj.get("billAmount");
                                                Double billAmountFuture = Double.parseDouble(billAmount);
                                                if (amount > 0) {
                                                    billAmount = (String) amount.toString().trim();
                                                    billDueDate = (String) termdetailsObj.get("billDueDate").toString().trim();
                                                    data.addToLog("BillAmount updated from Current-02/03/04/05", billAmount);
                                                    String FormatReq = "yyyyMMdd";

                                                    String formattedDate = LocalDateTime.parse(billDueDate,
                                                            DateTimeFormatter.ISO_DATE_TIME)
                                                        .format(DateTimeFormatter.ofPattern(FormatReq));

                                                    data.setSessionData(Constants.S_HALO_API_nextPaymentdueDate, formattedDate);
                                                    data.addToLog("BillAmount updated from Current-02/03/04/05", billAmount);
                                                } else if (billAmountFuture > 0) {
                                                    break;
                                                } else {
                                                    outstandingBalance = (String) termdetailsObj.get("outstandingBalance").toString().trim();
                                                    data.addToLog("Outstanding balance set from Current-02/03/04/05", outstandingBalance);
                                                }

                                                data.setSessionData(Constants.S_HALO_API_BillingFlag, "Y");
                                                data.setSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE, billingstatusCode);
                                                data.addToLog(currElementName, "S_HALO_API_BILLING_STATUS_CODE for loop:" + i + "::" +
                                                    data.getSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE));
                                            } else if (Status.equalsIgnoreCase("Future")) {
                                                billAmount = (String) termdetailsObj.get("billAmount").toString().trim();
                                                billDueDate = (String) termdetailsObj.get("billDueDate").toString().trim();
                                                outstandingBalance = (String) termdetailsObj.get("outstandingBalance").toString().trim();
                                                data.addToLog("BillAmount set from future-02/03/04/05", billAmount);
                                                data.addToLog("Outstanding balance set from future-02/03/04/05", outstandingBalance);
                                                String FormatReq = "yyyyMMdd";

                                                String formattedDate = LocalDateTime.parse(billDueDate,
                                                        DateTimeFormatter.ISO_DATE_TIME)
                                                    .format(DateTimeFormatter.ofPattern(FormatReq));

                                                data.setSessionData(Constants.S_HALO_API_nextPaymentdueDate, formattedDate);

                                                // data.setSessionData(Constants.S_HALO_API_BillDue, "Y");

                                                data.setSessionData(Constants.S_HALO_API_BillingFlag, "Y");
                                                data.setSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE, billingstatusCode);
                                                data.addToLog(currElementName, "S_HALO_API_BILLING_STATUS_CODE for loop:" + i + "::" +
                                                    data.getSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE));
                                            }

                                        }
                                    }
                                    //code to set outstandingBalance and waiting to bill flag for 01 
                                    if (Status.equalsIgnoreCase("Current") || Status.equalsIgnoreCase("Future")) {

                                        if (billingstatusCode.equalsIgnoreCase("01")) {

                                            outstandingBalance = (String) termdetailsObj.get("outstandingBalance")
                                                .toString().trim();

                                            //data.setSessionData(Constants.S_HALO_API_Waiting_TO_Bill, "Y");
                                            data.setSessionData(Constants.S_HALO_API_BillingFlag, "Y");
                                            data.setSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE, billingstatusCode);
                                            data.addToLog(currElementName, "S_HALO_API_BILLING_STATUS_CODE for loop:" + i + "::" +
                                                data.getSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE));
                                        }

                                    }
                                    
                                    if(billingstatusCode.equalsIgnoreCase("07")||billingstatusCode.equalsIgnoreCase("12")) {
                                		data.setSessionData(Constants.S_HALO_API_CancelledFlag, "Y");
                                		data.setSessionData(Constants.S_HALO_API_CancelledGenericFlag, "Y");
                                    }else if(billingstatusCode.equalsIgnoreCase("13")) {
                                    	data.setSessionData(Constants.S_HALO_API_CancelledFlag, "Y");
                                    	data.setSessionData(Constants.S_HALO_API_CancelledEPFlag, "Y");
                                    }else {
                                    	 data.setSessionData(Constants.S_HALO_API_ExpiredFlag, "Y");
                                    }
                                    
                                    if(Status.equalsIgnoreCase("Current")&& (billingstatusCode.equalsIgnoreCase("EX")||billingstatusCode.equalsIgnoreCase("06"))) {
                                   	 billAmount = (String) termdetailsObj.get("billAmount").toString().trim();
                                        billDueDate = (String) termdetailsObj.get("cancelNoticeDate").toString().trim();
                                        String Format = "yyyy-MM-dd";
                                        String cancelDate = LocalDateTime.parse(billDueDate,
                                                DateTimeFormatter.ISO_DATE_TIME)
                                            .format(DateTimeFormatter.ofPattern(Format));
                                       
                                        data.addToLog("BillAmount set from pending", billAmount);
                                        LocalDate date = LocalDate.now(); 
                                        LocalDate cancelDateParsed  = LocalDate.parse(cancelDate); 
                                        String datestring= (String) date.toString();
                                        String cancelDtaeSTring = (String) cancelDateParsed.toString();
                                        data.addToLog("Today's Date: ", datestring);
                                        data.addToLog("Cancel due date: ", cancelDtaeSTring);
                                        
                                        if(cancelDateParsed.isBefore(date)) {
                                        	data.addToLog("cancel due date is already passed:: ", billDueDate);
                                        	data.setSessionData("PENDING_ERROR_FLAG", "Y"); 
                                        	data.setSessionData(Constants.S_HALO_API_PendingFlag, "Y");
                                        	StrExitState="PENDING_ER";
                                        	caa.createMSPKey(caa, data, "SBP_MN_001", "BILLING QUESTIONS");
                                        	return StrExitState;
                                        }               
                                        
                                        String FormatReq = "yyyyMMdd";
                                        String formattedDate = LocalDateTime.parse(billDueDate,
                                                DateTimeFormatter.ISO_DATE_TIME)
                                            .format(DateTimeFormatter.ofPattern(FormatReq));

                                        data.setSessionData(Constants.S_HALO_API_nextPaymentdueDate, formattedDate);


                                        data.setSessionData(Constants.S_HALO_API_PendingFlag, "Y");
                                        data.setSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE, billingstatusCode);
                                        data.addToLog(currElementName, "S_HALO_API_BILLING_STATUS_CODE for loop:" + i + "::" +
                                        data.getSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE));
                                        if(billingstatusCode.equalsIgnoreCase("EX")) {
                                        	data.setSessionData(Constants.S_HALO_API_ExpirePendingBillDue, "Y");
                                        	data.addToLog(currElementName, "Setting Expire Pending bill due as: " + data.getSessionData(Constants.S_HALO_API_ExpirePendingBillDue));
                                        	data.addToLog(currElementName, "S_HALO_API_PENDING_STATUS_CODE for loop:" + i + "::" +
                                                    data.getSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE));	
                                        }else if (billingstatusCode.equalsIgnoreCase("06")) {
                                        		data.setSessionData(Constants.S_HALO_API_CancelPendingBillDue, "Y");
                                            	data.addToLog(currElementName, "Setting Expire Pending bill due as:  " + data.getSessionData(Constants.S_HALO_API_CancelPendingBillDue));
                                            	data.addToLog(currElementName, "S_HALO_API_PENDING_STATUS_CODE for loop:" + i + "::" +
                                                        data.getSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE));		
                                        }
                                   }

                                    //code to set billingflag for 11
                                    if (billingstatusCode.equalsIgnoreCase("11")) {

                                        // data.setSessionData(Constants.S_HALO_API_ZerooutstandingBalance, "Y");
                                        data.setSessionData(Constants.S_HALO_API_BillingFlag, "Y");
                                        data.setSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE, billingstatusCode);
                                        data.addToLog(currElementName, "S_HALO_API_BILLING_STATUS_CODE for loop:" + i + "::" +
                                        data.getSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE));
                                        break;
                                    }
                                }
                            } else {

                                for (int i = 0; i < 1; i++) {

                                    JSONObject termdetailsObj = (JSONObject) termdetails.get(i);

                                    Status = (String) termdetailsObj.get("status").toString().trim();

                                    billingstatusCode = (String) termdetailsObj.get("billingStatusCode").toString().trim();
                                    if(Status.equalsIgnoreCase("Expired")&& (billingstatusCode.equalsIgnoreCase("07")||billingstatusCode.equalsIgnoreCase("10")||billingstatusCode.equalsIgnoreCase("12")||billingstatusCode.equalsIgnoreCase("13"))) {
                                    	if(billingstatusCode.equalsIgnoreCase("07")||billingstatusCode.equalsIgnoreCase("12")) {
                                    		data.setSessionData(Constants.S_HALO_API_CancelledGenericFlag, "Y");
                                        }else if(billingstatusCode.equalsIgnoreCase("13")) {
                                        	data.setSessionData(Constants.S_HALO_API_CancelledEPFlag, "Y");
                                        }else {
                                        	 data.setSessionData(Constants.S_HALO_API_ExpiredFlag, "Y");
                                        }       
                  
                                    }
                                    if (Status.equalsIgnoreCase("Expired")) {

                                        StrExitState = "ER";

                                    }
                                    
                                    if(billingstatusCode.equalsIgnoreCase("02")) {
                                    	
                                    }

                                    //code to set next payment and bill due flag for 02/03/04/05  
                                    if (Status.equalsIgnoreCase("Current") || Status.equalsIgnoreCase("Future")) {

                                        if (billingstatusCode.equalsIgnoreCase("02") ||
                                            billingstatusCode.equalsIgnoreCase("03") ||
                                            billingstatusCode.equalsIgnoreCase("04") ||
                                            billingstatusCode.equalsIgnoreCase("05")) {

                                            //nextpayment amount
                                            billAmount = (String) termdetailsObj.get("billAmount").toString().trim();
                                            //nextpayment date
                                            billDueDate = (String) termdetailsObj.get("billDueDate").toString().trim();
                                            outstandingBalance = (String) termdetailsObj.get("outstandingBalance")
                                                .toString().trim();

                                            String FormatReq = "yyyyMMdd";

                                            String formattedDate = LocalDateTime.parse(billDueDate,
                                                    DateTimeFormatter.ISO_DATE_TIME)
                                                .format(DateTimeFormatter.ofPattern(FormatReq));

                                            data.setSessionData(Constants.S_HALO_API_nextPaymentdueDate, formattedDate);

                                            // data.setSessionData(Constants.S_HALO_API_BillDue, "Y");

                                            data.setSessionData(Constants.S_HALO_API_BillingFlag, "Y");

                                        }
                                    }

                                    //code to set outstandingBalance and waiting to bill flag for 01 
                                    if (Status.equalsIgnoreCase("Current") || Status.equalsIgnoreCase("Future")) {

                                        if (billingstatusCode.equalsIgnoreCase("01")) {

                                            outstandingBalance = (String) termdetailsObj.get("outstandingBalance")
                                                .toString().trim();

                                            //data.setSessionData(Constants.S_HALO_API_Waiting_TO_Bill, "Y");
                                            data.setSessionData(Constants.S_HALO_API_BillingFlag, "Y");
                                        }

                                    }
                                    
                                    if(billingstatusCode.equalsIgnoreCase("07")||billingstatusCode.equalsIgnoreCase("12")) {
                                		data.setSessionData(Constants.S_HALO_API_CancelledFlag, "Y");
                                		data.setSessionData(Constants.S_HALO_API_CancelledGenericFlag, "Y");
                                    }else if(billingstatusCode.equalsIgnoreCase("13")) {
                                    	data.setSessionData(Constants.S_HALO_API_CancelledFlag, "Y");
                                    	data.setSessionData(Constants.S_HALO_API_CancelledEPFlag, "Y");
                                    }else {
                                    	 data.setSessionData(Constants.S_HALO_API_ExpiredFlag, "Y");
                                    }  
                                    
                                    if(Status.equalsIgnoreCase("Current")&& (billingstatusCode.equalsIgnoreCase("EX")||billingstatusCode.equalsIgnoreCase("06"))) {
                                      	 billAmount = (String) termdetailsObj.get("billAmount").toString().trim();
                                           billDueDate = (String) termdetailsObj.get("cancelNoticeDate").toString().trim();
                                     
                                           data.addToLog("BillAmount set from pending", billAmount);
                                           
                                           String Format = "yyyy-MM-dd";
                                           String cancelDate = LocalDateTime.parse(billDueDate,
                                                   DateTimeFormatter.ISO_DATE_TIME)
                                               .format(DateTimeFormatter.ofPattern(Format));
                                          
                                           data.addToLog("BillAmount set from pending", billAmount);
                                           LocalDate date = LocalDate.now(); 
                                           LocalDate cancelDateParsed  = LocalDate.parse(cancelDate); 
                                           String datestring= (String) date.toString();
                                           String cancelDtaeSTring = (String) cancelDateParsed.toString();
                                           data.addToLog("Today's Date: ", datestring);
                                           data.addToLog("Cancel due date: ", cancelDtaeSTring);
                                           
                                           if(cancelDateParsed.isBefore(date)) {
                                           	data.addToLog("cancel due date is already passed:: ", billDueDate);
                                           	data.setSessionData("PENDING_ERROR_FLAG", "Y"); 
                                           	data.setSessionData(Constants.S_HALO_API_PendingFlag, "Y");
                                           	StrExitState="PENDING_ER";
                                           	caa.createMSPKey(caa, data, "SBP_MN_001", "BILLING QUESTIONS");
                                           	return StrExitState;
                                           }                
                                         
                                           String FormatReq = "yyyyMMdd";
                                           String formattedDate = LocalDateTime.parse(billDueDate,
                                                   DateTimeFormatter.ISO_DATE_TIME)
                                               .format(DateTimeFormatter.ofPattern(FormatReq));

                                           data.setSessionData(Constants.S_HALO_API_nextPaymentdueDate, formattedDate);


                                           data.setSessionData(Constants.S_HALO_API_PendingFlag, "Y");
                                           data.setSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE, billingstatusCode);
                                           data.addToLog(currElementName, "S_HALO_API_BILLING_STATUS_CODE for loop:" + i + "::" +
                                           data.getSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE));
                                           if(billingstatusCode.equalsIgnoreCase("EX")) {
                                           	data.setSessionData(Constants.S_HALO_API_ExpirePendingBillDue, "Y");
                                           	data.addToLog(currElementName, "Setting Expire Pending bill due as: " + data.getSessionData(Constants.S_HALO_API_ExpirePendingBillDue));
                                           	}else if (billingstatusCode.equalsIgnoreCase("06")) {
                                           		data.setSessionData(Constants.S_HALO_API_CancelPendingBillDue, "Y");
                                               	data.addToLog(currElementName, "Setting Expire Pending bill due as:  " + data.getSessionData(Constants.S_HALO_API_CancelPendingBillDue));
                                           	}
                                      }
                                    

                                    //code to set billingflag for 11
                                    if (billingstatusCode.equalsIgnoreCase("11")) {

                                        // data.setSessionData(Constants.S_HALO_API_ZerooutstandingBalance, "Y");
                                        data.setSessionData(Constants.S_HALO_API_BillingFlag, "Y");
                                    }
                                }

                            }
                            // setting if policy is PAID, balaance DUE or waiting to bill
                            Double billAmountCheck = Double.parseDouble(billAmount);
                            Double outstandingBalanceCheck = Double.parseDouble(outstandingBalance);


                            if (billAmountCheck > 0) {
                                data.setSessionData(Constants.S_HALO_API_BillDue, "Y");
                                data.addToLog(currElementName, "BillAmount is greater than zero, hence setting Bill due as: " + data.getSessionData(Constants.S_HALO_API_BillDue));
                            } else if (outstandingBalanceCheck > 0) {
                                data.setSessionData(Constants.S_HALO_API_Waiting_TO_Bill, "Y");
                                data.addToLog(currElementName, "Outstanding balance is greater than zero, hence setting Wait to bill as: " + data.getSessionData(Constants.S_HALO_API_Waiting_TO_Bill));
                            }else if("Y".equalsIgnoreCase(Constants.S_HALO_API_PendingFlag)){
                            	data.addToLog(currElementName, "Pending flag is Y, Hence setting Pending flag: " + data.getSessionData(Constants.S_HALO_API_PendingFlag));
                            } else {
                                data.setSessionData(Constants.S_HALO_API_ZerooutstandingBalance, "Y");
                                data.addToLog(currElementName, "No bill due or outstanding balance, hence setting Paid in full as: " + data.getSessionData(Constants.S_HALO_API_Waiting_TO_Bill));
                            }


                            //setting payment details for PAID IN FULL
                            //setting payment details
                            JSONArray Paymentdetails = (JSONArray) policydetails.get("payments");
                                if (Paymentdetails.size() == 0) {
                                    StrExitState = "ER";
                                    return StrExitState;
                                }

                                for (int i = 0; i < Paymentdetails.size(); i++) {

                                    JSONObject PaymentdetailsObj = (JSONObject) Paymentdetails.get(i);

                                    PaymentStatus = (String) PaymentdetailsObj.get("paymentStatus").toString().trim();

                                    data.setSessionData(Constants.S_HALO_API_PayMentStatus, PaymentStatus);

                                    data.addToLog(currElementName, "S_HALO_API_PayMentStatus:: " +
                                        data.getSessionData(Constants.S_HALO_API_PayMentStatus));

                                    PaymentStatusCode = (String) PaymentdetailsObj.get("paymentStatusCode").toString()
                                        .trim();

                                    data.setSessionData(Constants.S_HALO_API_PaymentStatusCode, PaymentStatusCode);

                                    data.addToLog(currElementName, "S_HALO_API_PaymentStatusCode:: " +
                                        data.getSessionData(Constants.S_HALO_API_PaymentStatusCode));

                                    paymentRecordType = (String) PaymentdetailsObj.get("paymentRecordType").toString().trim();

                                    if (paymentRecordType.equalsIgnoreCase("PAY")) {
                                        if (PaymentStatus.equalsIgnoreCase("PROCESSED") &&
                                          (PaymentStatusCode.equalsIgnoreCase("STL") ||
                                                PaymentStatusCode.equalsIgnoreCase("APL"))) {

                                            lastpaymentAmount = (String) PaymentdetailsObj.get("paymentAmount").toString()
                                                .trim();
                                            data.setSessionData(Constants.S_HALO_API_lastpaymentAmount, lastpaymentAmount);

                                            data.addToLog(currElementName, "S_HALO_API_lastpaymentAmount:: " +
                                                data.getSessionData(Constants.S_HALO_API_lastpaymentAmount));

                                            lastPaymentDate = (String) PaymentdetailsObj.get("paymentDate").toString()
                                                .trim();

                                            String newformattedDate = lastPaymentDate.replace(Constants.HYPHEN, "");

                                            data.setSessionData(Constants.S_HALO_API_lastPaymentDate, newformattedDate);

                                            data.addToLog(currElementName, "S_HALO_API_lastPaymentDate:: " +
                                                data.getSessionData(Constants.S_HALO_API_lastPaymentDate));
                                            StrExitState = "SU";
                                            break;

                                        } else if ((PaymentStatus.equalsIgnoreCase("PENDING") && (PaymentStatusCode.equalsIgnoreCase("COM"))))

                                        {
                                            lastpaymentAmount = (String) PaymentdetailsObj.get("paymentAmount").toString()
                                                .trim();
                                            data.setSessionData(Constants.S_HALO_API_lastpaymentAmount, lastpaymentAmount);

                                            data.addToLog(currElementName, "S_HALO_API_lastpaymentAmount:: " +
                                                data.getSessionData(Constants.S_HALO_API_lastpaymentAmount));

                                            lastPaymentDate = (String) PaymentdetailsObj.get("paymentDate").toString()
                                                .trim();
                                            String newformattedDate = lastPaymentDate.replace(Constants.HYPHEN, "");

                                            data.setSessionData(Constants.S_HALO_API_lastPaymentDate, newformattedDate);

                                            data.addToLog(currElementName, "S_HALO_API_lastPaymentDate:: " +
                                                data.getSessionData(Constants.S_HALO_API_lastPaymentDate));
                                            StrExitState = "SU";
                                            break;
                                        }
         

                                    } else {
                                        StrExitState = "ER";
                                        return StrExitState;
                                    }

                                }

                            data.setSessionData(Constants.S_HALO_API_STATUS, printstatus);
                            //data.setSessionData(Constants.S_HALO_API_BILLING_STATUS_CODE, billingstatusCode);
                            data.setSessionData(Constants.S_HALO_API_nextPaymentdue, billAmount);

                            data.setSessionData(Constants.S_HALO_API_outstandingBalance, outstandingBalanceCheck);

                            data.addToLog(currElementName,
                                "S_HALO_API_STATUS:: " + data.getSessionData(Constants.S_HALO_API_STATUS));
                            data.addToLog(currElementName, "S_HALO_API_nextPaymentdue:: " +
                                data.getSessionData(Constants.S_HALO_API_nextPaymentdue));
                            data.addToLog(currElementName, "S_HALO_API_nextPaymentdueDate:: " +
                                data.getSessionData(Constants.S_HALO_API_nextPaymentdueDate));
                            data.addToLog(currElementName, "S_HALO_API_outstandingBalance:: " +
                                data.getSessionData(Constants.S_HALO_API_outstandingBalance));
                            data.addToLog(currElementName, "S_HALO_API_ZerooutstandingBalance:: " +
                                data.getSessionData(Constants.S_HALO_API_ZerooutstandingBalance));
                            data.addToLog(currElementName, "S_HALO_API_Waiting_TO_Bill:: " +
                                data.getSessionData(Constants.S_HALO_API_Waiting_TO_Bill));
                            data.addToLog(currElementName,
                                "S_HALO_API_BillDue:: " + data.getSessionData(Constants.S_HALO_API_BillDue));
                            data.addToLog(currElementName,
                                "S_HALO_API_BillAmount:: " + data.getSessionData(Constants.S_HALO_API_BillAmount));
                            data.addToLog(currElementName, "S_HALO_API_BillingFlag:: " +
                                data.getSessionData(Constants.S_HALO_API_BillingFlag));
                            data.addToLog(currElementName, "S_HALO_API_ExpirePendingBillDue:: " +
                                    data.getSessionData(Constants.S_HALO_API_ExpirePendingBillDue));
                            data.addToLog(currElementName, "S_HALO_API_CancelPendingBillDue:: " +
                                    data.getSessionData(Constants.S_HALO_API_CancelPendingBillDue));
                            data.addToLog(currElementName, "S_HALO_API_PendingFlag:: " +
                                    data.getSessionData(Constants.S_HALO_API_PendingFlag));
                            data.addToLog(currElementName, "S_HALO_API_ExpiredFlag:: " +
                                    data.getSessionData(Constants.S_HALO_API_ExpiredFlag));
                            data.addToLog(currElementName, "S_HALO_API_CancelledFlag:: " +
                                    data.getSessionData(Constants.S_HALO_API_CancelledFlag));
                            data.addToLog(currElementName, "S_HALO_API_CancelledEPFlag:: " +
                                    data.getSessionData(Constants.S_HALO_API_CancelledEPFlag));
                            data.addToLog(currElementName, "S_HALO_API_CancelledGenericFlag:: " +
                                    data.getSessionData(Constants.S_HALO_API_CancelledGenericFlag));
                            
                            

                        }

                    }

                }

            }

        } catch (Exception e) {

            data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
            caa.printStackTrace(e);
        }

        return StrExitState;
    }

}