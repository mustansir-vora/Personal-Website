
package com.farmers.adminHost;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONArray;
import org.json.JSONObject;


import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetFWSRoutingByPolicySource;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;


public class FWSRouting extends DecisionElementBase {



	@Override
    public String doDecision(String currElementName, DecisionElementData data) {
        String exitState = Constants.ER;
        String strReqBody = Constants.EmptyString;
        String strRespBody = Constants.EmptyString;
        
        //Mustan - Alerting Mechanism ** Response Code Capture
        String apiRespCode = Constants.EmptyString;
        CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
        SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();

        try {
        	 String url = (String) data.getSessionData(Constants.S_FWSRouting_URL);
             String callerId = (String) data.getSessionData(Constants.S_CALLID);
             String connectionTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
             String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
             String policysource = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
             
             data.addToLog(currElementName, "URL IS"+url);

             int connTimeout = Integer.parseInt(connectionTimeout);
             int readTimeoutInt = Integer.parseInt(readTimeout);
             LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

             GetFWSRoutingByPolicySource api = new GetFWSRoutingByPolicySource();
             org.json.simple.JSONObject simpleResp = api.start(url, callerId, Constants.tenantid, policysource, connTimeout, readTimeoutInt, context);

            if (simpleResp == null) {
                data.addToLog(currElementName, "API response is null.");
                return exitState;
            }
            
          //Start - Mustan - Alerting Mechanism ** Response Code Capture
            if (simpleResp.containsKey(Constants.REQUEST_BODY)) {
            	strReqBody = simpleResp.get(Constants.REQUEST_BODY).toString();
			}
            if (simpleResp.containsKey(Constants.RESPONSE_BODY)) {
            	strRespBody = simpleResp.get(Constants.RESPONSE_BODY).toString();
			}
            apiRespCode = String.valueOf((int) simpleResp.get(Constants.RESPONSE_CODE));
            //End

            JSONObject resp = new JSONObject(simpleResp.toString());
            data.addToLog(currElementName, "Full API response: " + resp.toString(4));

            JSONObject responseBody = resp.optJSONObject("responseBody");
            if (responseBody == null) {
                data.addToLog(currElementName, "No 'responseBody' found in API response.");
                return exitState;
            }

            JSONArray jsonArray = responseBody.optJSONArray("res");
            if (jsonArray == null || jsonArray.length() == 0) {
                data.addToLog(currElementName, "No data found in 'res' array or 'res' is empty.");
                return exitState;
            }

            JSONObject bestMatch = findBestMatchByPriority(jsonArray, data, currElementName);
            if (bestMatch != null) {
                String routingPolicySource = bestMatch.optString("routingpolicysource");
                data.addToLog(currElementName, "Row with the most matching qualifiers: " + bestMatch.toString(4));
                data.setSessionData(currElementName + Constants._RESP, bestMatch.toString());
                data.setSessionData(Constants.S_POLICY_SOURCE, routingPolicySource);
                data.addToLog(currElementName, "Routing Policy Source Picked: " + routingPolicySource);
                
                exitState = Constants.SU;
            } else {
                data.addToLog(currElementName, "No row matches the given qualifiers.");
            }
        } catch (Exception e) {
            System.err.println("Exception in FWS Routing decision process: " + e);
            data.addToLog(currElementName, "Exception in processing: " + e.toString());
        }
        
        try {
			objHostDetails.startHostReport(currElementName,"FWS Routing API", strReqBody,"",(String) data.getSessionData(Constants.S_FWSRouting_URL));
			objHostDetails.endHostReport(data,strRespBody , exitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode," ");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for StateAreaCode API call  :: "+e);
			caa.printStackTrace(e);
		}

        return exitState;
    }

    private JSONObject findBestMatchByPriority(JSONArray jsonArray, DecisionElementData data, String currElementName) throws Exception {
        JSONObject bestMatch = null;
        boolean qualifierNullEmpty = false;
        List<JSONObject> filteredList = new ArrayList<>();

        String policysource = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
        if (policysource == null || policysource.isEmpty()) {
            data.addToLog(currElementName, "Policy source is null or empty, exiting method.");
            return null;  
        }
        data.addToLog(currElementName, "Using policy source: " + policysource);
		// Start-MDM route rule fix for FWS A360 policies
		if (policysource.equalsIgnoreCase("FWS-A360")) {
			policysource = "Met360";
			data.addToLog(currElementName, "Updating policy source to Met360");
		}
		data.addToLog(currElementName, "Using policy source: " + policysource);
		// End-MDM route rule fix for FWS A360 policies
        boolean payrollDeduct = false;
        if(data.getSessionData(Constants.S_PAYMENT_SITE_CD) != null) {
        	String paymentSiteCD = (String) data.getSessionData(Constants.S_PAYMENT_SITE_CD);
        	if(paymentSiteCD.equalsIgnoreCase("Payroll Deduct")) {
        		payrollDeduct = true;
        	}
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonRow = jsonArray.optJSONObject(i);
            if (jsonRow != null && policysource.equals(jsonRow.optString("policysource"))) {
                filteredList.add(jsonRow);
            }
        }
        
        
        
        data.addToLog(currElementName, "Filtered list contains " + filteredList.size() + " rows matching policy source.");

        Collections.sort(filteredList, (a, b) -> Integer.compare(a.optInt("priority", Integer.MAX_VALUE), b.optInt("priority", Integer.MAX_VALUE)));
        data.addToLog(currElementName, "Sorted filtered list by priority.");
        data.addToLog(currElementName, "Filtered list: "+filteredList.toString());
        
        Map<String, String> qualifiers = getQualifiers(data);
        qualifiers.remove("policysource");
        data.addToLog(currElementName, "Evaluating qualifiers: " + qualifiers.toString());
        data.addToLog(currElementName, "Entering outerloop for filteredList");       
        outerLoop:
        for (JSONObject jsonRow : filteredList) {
        	
            StringBuilder logDetails = new StringBuilder("Evaluating row: " + jsonRow.toString(4)); 
            boolean gpcMatchFound = false; 
            boolean qualifierMatchFound = false;
            
            
            for (Map.Entry<String, String> qualifier : qualifiers.entrySet()) {
            	data.addToLog(currElementName, "*******Entering innerloop for qualifier"+qualifier.getKey()+"********");
            	String paymentSiteCD =  jsonRow.optString("grouppaymentcode", null);
                if(paymentSiteCD != null && paymentSiteCD.equalsIgnoreCase("Payroll Deduct") && !payrollDeduct) {
                	 data.addToLog(currElementName, "PaymentSiteCD value is not a Payroll Deduct. So not considering Payroll Deduct rows.");
                	continue;
                }
                data.addToLog(currElementName,"Call before if condition ");
                
                if ("gpc".equals(qualifier.getKey())) {
                	data.addToLog(currElementName,"Call at if condition ");
                   JSONArray gpcArray = jsonRow.optJSONArray("gpc");
                    if (gpcArray != null) {
                        for (int j = 0; j < gpcArray.length(); j++) {
                        	data.addToLog(currElementName,"Call at for  loop for GPC ");
                            JSONObject gpcObject = gpcArray.optJSONObject(j);
                            data.addToLog(currElementName,"Call before if condition ");
                            if (gpcObject != null && qualifier.getValue()!=null && qualifier.getValue().equals(gpcObject.optString("key"))) {
                                gpcMatchFound = true;
                                bestMatch = jsonRow; 
                                logDetails.append("Match found for GPC key with value: ").append(gpcObject.optString("key"));
                                data.addToLog(currElementName,"Match found for GPC key ");
                                break outerLoop; 
                            }
                        }
                        if (!gpcMatchFound) {
                            logDetails.append("No GPC match found in array.");
                            data.addToLog(currElementName,"NO Match found for GPC key hence breaking the loop for qualifier");
                            data.addToLog(currElementName, logDetails.toString());
                            break; 
                        }
                    }else {
                    	qualifierNullEmpty= true;
                    	data.addToLog(currElementName, "gpc is null");
                    }
                } else {      
                	data.addToLog(currElementName,"Call at else condition ");
                    String jsonValue = jsonRow.optString(qualifier.getKey(), "");
                    data.addToLog(currElementName,"Call at else condition 2");
                    if (!jsonValue.equalsIgnoreCase("")){                  	
                        if(jsonValue.equals(qualifier.getValue())) {
                    	qualifierMatchFound=true;
                        bestMatch = jsonRow;
                        logDetails.append("Match found for key '").append(qualifier.getKey()).append("' with value: '").append(jsonValue).append("'");
                        data.addToLog(currElementName,"No match found.");
                        break outerLoop; 
                        }
                        data.addToLog(currElementName,"Call at else condition3 ");
                        if(!qualifierMatchFound) {
                        	 logDetails.append("No "+qualifier.getKey()+ " match found in array.");
                             data.addToLog(currElementName,"No "+qualifier.getKey()+" match found in array.");
                             data.addToLog(currElementName, logDetails.toString());
                        	break;
                        }
                        
                    } else {
                    	qualifierNullEmpty= true;
                        logDetails.append("No match found for key '").append(qualifier.getKey()).append("' with expected value: '").append(qualifier.getValue()).append("', actual value: '").append(jsonValue).append("'");
                        data.addToLog(currElementName,"Qualifier is empty.");
                    }              	 
                }
                
            }
            if(qualifierNullEmpty) {
            bestMatch=jsonRow;
            }
            
        }

        if (bestMatch == null) {
            data.addToLog(currElementName, "No suitable match found after evaluating all rows.");
        } else {
            data.addToLog(currElementName, "Best match found with details: " + bestMatch.toString(4));
        }

        return bestMatch;
    }


    private Map<String, String> getQualifiers(DecisionElementData data) {
        Map<String, String> qualifiers = new HashMap<>();
        
        qualifiers.put("gpc", (String) data.getSessionData(Constants.S_GPC));
        qualifiers.put("producerrolecode", (String) data.getSessionData(Constants.S_PRODUCER_ROLE_CODE));
        qualifiers.put("callroutingindicator", (String) data.getSessionData(Constants.S_CALL_ROUTING_INDICATOR));
        qualifiers.put("grouppaymentcode", (String) data.getSessionData(Constants.S_PAYMENT_SITE_CD));
        qualifiers.put("comboindicator", (String) data.getSessionData(Constants.S_COMBO_INDICATOR));
        qualifiers.put("policysource", (String) data.getSessionData(Constants.S_POLICY_SOURCE));
        qualifiers.put("billingactivitycode", (String) data.getSessionData(Constants.S_BILLING_ACTIVITY_CD));
        qualifiers.put("servicelevel", (String) data.getSessionData(Constants.S_SERVICE_LEVEL));
        qualifiers.put("producerdistributioncode", (String) data.getSessionData(Constants.S_PRODUCER_DISTRIBUTION_CODE));
        qualifiers.put("activitydescription", (String) data.getSessionData(Constants.S_ACTIVITY_DESC));
        data.addToLog(getElementName(), "Qualifiers extracted for decision making: " + qualifiers.toString());
        return qualifiers;
    }
}
