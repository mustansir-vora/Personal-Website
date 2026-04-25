 package com.farmers.host;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.CommercialBillingSummary_Get;
import com.farmers.FarmersAPI_NP.CommercialBillingSummary_NP_Get;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import java.util.HashMap;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CIDA_HOST_005 extends DecisionElementBase {
   public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
      String strExitState = "ER";
      CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
      SetHostDetails objHostDetails = new SetHostDetails(caa);
      objHostDetails.setinitalValue();
      String strReqBody = "";
      String strRespBody = "";
      String apiRespCode = "";
      String url = "";
      String finalurl = "";
      String region = null;
      HashMap regionDetails = (HashMap)data.getSessionData("RegionHM");

      try {
         if (data.getSessionData("S_CON_TIMEOUT") != null && data.getSessionData("S_READ_TIMEOUT") != null) {
            String accNum = (String)data.getSessionData("S_ACCNUM");
            if (accNum != null && !accNum.isEmpty()) {
               url = (String)data.getSessionData("S_COMMERCIAL_BILLING_SUMMARY_ACCNUM_URL");
               if (url.contains("S_URL_ACCNUM")) {
                  finalurl = url.replace("S_URL_ACCNUM", accNum);
               }

               data.addToLog(currElementName, "ACCNUM API URL : " + finalurl);
            }

            int conTimeout = Integer.valueOf((String)data.getSessionData("S_CON_TIMEOUT"));
            int readTimeout = Integer.valueOf((String)data.getSessionData("S_READ_TIMEOUT"));
            String callerId = (String)data.getSessionData("S_CALLID");
            LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData("A_CONTEXT");
            CommercialBillingSummary_Get obj = new CommercialBillingSummary_Get();
            data.addToLog("API URL: ", url);
            String prefix = "https://api-np-ss.farmersinsurance.com";
            String UAT_FLAG = "";
            if (url.startsWith(prefix)) {
               UAT_FLAG = "YES";
            }

            CommercialBillingSummary_NP_Get objNP = new CommercialBillingSummary_NP_Get();
            JSONObject resp = null;
            if ("YES".equalsIgnoreCase(UAT_FLAG)) {
               String Key = "S_COMMERCIAL_BILLING_SUMMARY_ACCNUM_URL";
               region = (String)regionDetails.get(Key);
               data.addToLog("Region for UAT endpoint: ", region);
               resp = objNP.start(finalurl, callerId, conTimeout, readTimeout, context, region);
            } else {
               region = "PROD";
               resp = obj.start(finalurl, callerId, conTimeout, readTimeout, context);
            }

            data.addToLog(currElementName, " API response  :" + resp);
            apiRespCode = String.valueOf((Integer)resp.get("responseCode"));
            if (resp != null) {
               if (resp.containsKey("requestBody")) {
                  strReqBody = resp.get("requestBody").toString();
               }

               if (resp.containsKey("responseCode") && (Integer)resp.get("responseCode") == 200 && resp.containsKey("responseBody")) {
                  data.addToLog(currElementName, "Set Commercial Billing Summary Lookup API Response into session with the key name of " + currElementName + "_RESP");
                  strRespBody = resp.get("responseBody").toString();
                  data.setSessionData(currElementName + "_RESP", resp.get("responseBody"));
                  strExitState = this.apiResponseManupulation(data, caa, currElementName, strRespBody);
               } else {
                  strRespBody = resp.get("responseMsg").toString();
               }

               data.setSessionData("S_COMMERCIAL_BILLING_JSON", strRespBody);
            }
         }
      } catch (Exception var25) {
         data.addToLog(currElementName, "Exception in  Commercial Billing Summary Lookup API call  :: " + var25);
         caa.printStackTrace(var25);
      }

      try {
         objHostDetails.startHostReport(currElementName, " Commercial Billing Summary LookupAPI by AccountNumber", strReqBody, region, url);
         objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase("ER") ? "ER" : "SU", apiRespCode, "");
      } catch (Exception var24) {
         data.addToLog(currElementName, "Exception while forming host reporting for  Commercial Billing Summary Lookup API call by Account Number  :: " + var24);
         caa.printStackTrace(var24);
      }

      data.setSessionData("S_Commercial_API", "True");
      return strExitState;
   }

   private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
      String strExitState = "ER";

      try {
         JSONObject resp = (JSONObject)(new JSONParser()).parse(strRespBody);
         data.addToLog(currElementName, "Value of Response Body : " + resp);
         JSONObject billingSummary = (JSONObject)resp.get("billingSummary");
         data.addToLog(currElementName, "Value of billingSummary : " + billingSummary);
         JSONArray accounts = (JSONArray)billingSummary.get("accounts");
         data.addToLog(currElementName, "Value of accounts : " + accounts);
         int noOfAccounts = 0;
         String payPlan = "";
         if (accounts != null && accounts.size() > 0) {
            JSONObject account = (JSONObject)accounts.get(0);
            data.addToLog(currElementName, "Value of Account" + account);
            String retrievedaccno = (String)account.get("accountNumber");
            data.addToLog(currElementName, "Value of retrievedaccno  : " + retrievedaccno);
            String accno = retrievedaccno.split("-")[0];
            data.addToLog(currElementName, "Value of AccNumber After Extraction  : " + accno);
            data.setSessionData("S_API_ACCNUM", accno);
            if (accno != null && !accno.isEmpty()) {
               ++noOfAccounts;
            }

            String stopCode = (String)account.get("stopCode");
            data.setSessionData("S_STOPCODE", stopCode);
            if (stopCode.equalsIgnoreCase("BBR")) {
               data.setSessionData("S_IS_RTB_ACCOUNT", "TRUE");
            } else {
               data.setSessionData("S_IS_RTB_ACCOUNT", "FALSE");
            }

            String payorZipCode = (String)account.get("payorZipCode");
            data.setSessionData("S_PAYOR_ZIP_CODE", payorZipCode);
            payPlan = (String)account.get("payPlan");
         }

         String isAccNumberProvided = (String)data.getSessionData("S_IS_ACCNO_PROVIDED");
         if (noOfAccounts == 1 && isAccNumberProvided != null && isAccNumberProvided.equalsIgnoreCase("TRUE")) {
            data.setSessionData("S_CALLER_AUTH", "Identified");
            data.addToLog(currElementName, " Account Found ::");
            if (payPlan != null && !payPlan.isEmpty() && payPlan.equalsIgnoreCase("RB")) {
               data.setSessionData("S_RTB_CALLER_FLAG", "Y");
            } else {
               data.addToLog(currElementName, "payplan is ::" + payPlan + " Not an RTB Caller");
            }

            data.setSessionData("S_POPUP_TYPE", "02");
            data.setSessionData("S_CALLER_INPUT", (String)data.getSessionData("S_ACCNUM"));
            strExitState = "AccountNumberFound";
         } else if (noOfAccounts == 0 && isAccNumberProvided != null && isAccNumberProvided.equalsIgnoreCase("TRUE")) {
            strExitState = "NoAccountFound";
            data.addToLog(currElementName, "No Account's Found ::");
         } else {
            strExitState = "ER";
         }
      } catch (Exception var16) {
         data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + var16);
         caa.printStackTrace(var16);
      }

      return strExitState;
   }
}