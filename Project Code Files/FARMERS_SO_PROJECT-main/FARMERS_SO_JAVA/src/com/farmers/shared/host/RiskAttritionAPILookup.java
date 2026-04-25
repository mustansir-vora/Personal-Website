package com.farmers.shared.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
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
import java.time.OffsetDateTime;  
import java.time.ZoneOffset;  
import java.time.format.DateTimeFormatter;  
public class RiskAttritionAPILookup extends DecisionElementBase {  
    @Override  
    public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {  
        String StrExitState = Constants.ER;  
        CommonAPIAccess caa = CommonAPIAccess.getInstance(data);  
        SetHostDetails objHostDetails = new SetHostDetails(caa);  
        String strReqBody = Constants.EmptyString;  
        String strRespBody = Constants.EmptyString;  
        String region = null;  
        HashMap<String, String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);  
        String apiRespCode="";
        String apiString="";
		data.addToLog("S_RISKATTRITION_LOOKUPURL: ", (String) data.getSessionData(Constants.S_RISKATTRITION_LOOKUPURL));
		data.addToLog("readtimeout:" , (String) data.getSessionData(Constants.S_READ_TIMEOUT));
		data.addToLog("conntimeout:" , (String) data.getSessionData(Constants.S_CONN_TIMEOUT));
        try {  
            if (data.getSessionData(Constants.S_RISKATTRITION_LOOKUPURL) != null &&  
                data.getSessionData(Constants.S_READ_TIMEOUT) != null &&  
                data.getSessionData(Constants.S_CONN_TIMEOUT) != null){  
                
                String wsurl = (String) data.getSessionData(Constants.S_RISKATTRITION_LOOKUPURL);  
                String policycontractnumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
                String policySource =(String) data.getSessionData(Constants.S_POLICY_SOURCE);
                
                // Get the current timestamp  
                OffsetDateTime currentTimestamp = OffsetDateTime.now();               
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");  
                String formattedTimestamp = currentTimestamp.format(formatter);  
             
                String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);  
                String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);  
                String Callid = (String) data.getSessionData(Constants.S_CALLID);  
                LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
                
                data.addToLog("Callid", Callid);  
                data.addToLog("readtimeout", readtimeout);  
                data.addToLog("conntimeout", conntimeout);    
                data.addToLog("wsurl", wsurl);  
                data.addToLog("Policy number:", policycontractnumber); 
                data.addToLog("Policy Source ", policySource);
                data.addToLog("transactiontimestamp", formattedTimestamp);   
                
                Lookupcall lookups = new Lookupcall();  
                data.addToLog("API URL: ", wsurl);  
                String prefix = "https://api-np-ss.farmersinsurance.com";  
                String UAT_FLAG = "";  

                if (wsurl.startsWith(prefix)) {  
                    UAT_FLAG = "YES";  
                }  
                
                region = "YES".equalsIgnoreCase(UAT_FLAG) ? regionDetails.get(Constants.S_RISKATTRITION_LOOKUPURL) : "PROD";  
                data.addToLog("Region: ", region);  
                org.json.simple.JSONObject responses = lookups.GetRiskAttritionAPILookup(wsurl, Callid, policycontractnumber, policySource, formattedTimestamp,
                        Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context, region, UAT_FLAG);  
                data.addToLog("responses", responses.toString());  

                Integer HTTPStatusCode = (Integer) responses.get("responseCode");  
                String HTTPRespMsg = (String) responses.get("responseMsg");  
                apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
                if (responses != null) {  
                    if (responses.containsKey(Constants.REQUEST_BODY))  
                    	strReqBody = responses.get(Constants.REQUEST_BODY).toString();  

                    if (responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 &&  
                        responses.containsKey(Constants.RESPONSE_BODY)) {  
                        
                        data.addToLog(currElementName, "Set RiskAttritionAPILookup API Response into session with the key name of " + currElementName + Constants._RESP);  
                        strRespBody = responses.get(Constants.RESPONSE_BODY).toString();  
                        data.setSessionData(currElementName + Constants._RESP, responses.get(Constants.RESPONSE_BODY));  
                        data.setSessionData("RiskAttritionAPILookup response: ", strRespBody);  
                        StrExitState =apiResponseManipulation_RiskAttritionAPILookup(data, caa, currElementName, strRespBody);  
                        apiString=(String) data.getSessionData("APIString");
                       
                    } else {  
                        strRespBody = responses.get(Constants.RESPONSE_MSG).toString();  
                    }  
                }  
                try {
					data.addToLog(currElementName, "Reporting STARTS for FARMERS");
					objHostDetails.startHostReport(currElementName, "RiskAttritionAPILookup", strReqBody, region,(String) data.getSessionData(Constants.S_RISKATTRITION_LOOKUPURL));
					objHostDetails.endHostReport(data, strRespBody, StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,apiString);
					data.addToLog(currElementName, "Reporting ENDS for FARMERS");
				} catch (Exception e1) {
					data.addToLog(currElementName,
							"Exception while forming host reporting for SBP_HOST_001_FARMERS  billpresentmentretrieveBillingSummary call  :: " + e1);
					caa.printStackTrace(e1);
				}
            }  
        } catch (Exception e) {  
            data.addToLog(currElementName, "Exception in RiskAttrition API call  :: " + e);  
            caa.printStackTrace(e);  
        }  
        
      
        return StrExitState;  
    }  

  
    private String apiResponseManipulation_RiskAttritionAPILookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) throws ParseException, AudiumException {  
    	String strExitState = Constants.ER;
    	
    	try {
    	data.addToLog(currElementName, "Entered into api manipulation for risk Attrition");
    	// Parse the response body to a JSONObject  
        JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);  

        // Ensure the response contains the "report" object and handle possible nulls  
        JSONObject report = (JSONObject) resp.get("report");  
        if (report == null) {  
            throw new AudiumException("Response does not contain a valid report object.");  
        }  

        double percentile = 0.0;  
        if (report.containsKey("percentile") && report.get("percentile") instanceof Number) {  
            percentile = ((Number) report.get("percentile")).doubleValue(); // Get the double value safely 
            data.setSessionData("percentileValue", percentile );
        } else {  
            // Handle the case where the percentile is not present or not a Number  
            data.addToLog(currElementName, "Percentile is missing or invalid.");  
            return strExitState; // Early return if percentile is not found  
        }  

        double retentionThreshold = 0.90;  
        String apiString = "";  

        // Log the extracted percentile value  
        data.addToLog(currElementName, "Extracted Percentile Value: " + percentile);  
        data.setSessionData("APIString", "");  

        // Check if the percentile meets the retention threshold  
        if (percentile > retentionThreshold) {  
            data.setSessionData(Constants.S_POLICY_ATTRIBUTES, "FDS Retention");  
            String policyAttributes = (String) data.getSessionData(Constants.S_POLICY_ATTRIBUTES);  
            data.addToLog("Policy Attributes after customer retention check: ", policyAttributes);  
            
            // Construct apiString with retention information  
            apiString = policyAttributes + ", Retention Score=" + percentile;  
            data.setSessionData("APIString", apiString);  
        }
        // Log the constructed API string  
        data.addToLog("API String: ", (String)data.getSessionData("APIString"));  
        strExitState = Constants.SU;
    	}catch (Exception e) {
    		data.addToLog(currElementName,"Exception in AccLinkAniLookup method  :: "+e);
    		caa.printStackTrace(e);
    	}
        return strExitState; // Return the constructed API string  
    }  
}  