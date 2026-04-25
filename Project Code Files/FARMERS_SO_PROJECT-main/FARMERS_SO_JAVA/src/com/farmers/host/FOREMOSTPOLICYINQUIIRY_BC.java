package com.farmers.host;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.ForemostPolicyInquiry_Post;
import com.farmers.FarmersAPI_NP.ForemostPolicyInquiry_NP_Post;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;

public class FOREMOSTPOLICYINQUIIRY_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;

		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;

		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {

			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

			data.addToLog(data.getCurrentElement(),
					"URL :: " + data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) + " :: Call ID :: "
							+ data.getSessionData(Constants.S_CALLID) + " :: S_POLICY_NUM :: "
							+ data.getSessionData(Constants.S_POLICY_NUM));

			if (data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) != null
					&& data.getSessionData(Constants.S_CALLID) != null
					&& data.getSessionData(Constants.S_POLICY_NUM) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {

				String url = (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				String policynumber = (String) data.getSessionData(Constants.S_POLICY_NUM); // will get this from GDF
				String sysDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String policysource = Constants.Foremost; 

				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				data.addToLog(currElementName, "Brands Table API conntimeout ::" + conTimeout);
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT));
				data.addToLog(currElementName, "Brands Table API readtimeout ::" + readTimeout);

				ForemostPolicyInquiry_Post apiObj = new ForemostPolicyInquiry_Post();
				//Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				ForemostPolicyInquiry_NP_Post objNP = new ForemostPolicyInquiry_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_FOREMOST_POLICYINQUIIRY_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, policynumber, sysDate, policysource, conTimeout, readTimeout,context, region);
				}else {
					region="PROD";
				    resp = apiObj.start(url, tid, policynumber, sysDate, policysource, conTimeout, readTimeout,context);
				}
				//Non prod changes-Priya
				data.addToLog(currElementName, "�PI response  :" + resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (null != resp) {

					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {

						JSONObject strRespBodyTemp = (JSONObject) resp.get(Constants.RESPONSE_BODY);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						
						if (resp.containsKey(Constants.REQUEST_BODY)) {
							strReqBody = (String) resp.get(Constants.REQUEST_BODY).toString();
						}


						if (!strRespBodyTemp.isEmpty()) {
							if (strRespBodyTemp.containsKey("policies")
									&& null!= strRespBodyTemp.get("policies")
									&& !((JSONArray) strRespBodyTemp.get("policies")).isEmpty()){
								JSONArray policies = (JSONArray) strRespBodyTemp.get("policies");

								if (((JSONObject) policies.get(0)).containsKey("policyProductCode")) {
									String policyProductCode = (String) ((JSONObject) policies.get(0))
											.get("policyProductCode");
									data.addToLog(currElementName,
											"policies has policyProductCode :: " + policyProductCode);

									if (policyProductCode != null && !policyProductCode.isEmpty()) {
										data.setSessionData("S_PRODUCT_CODE", policyProductCode);
										data.setSessionData("S_PL_PROD_CODE_AVA", true);
										StrExitState = "Policy Found";
									} else {
										data.addToLog(currElementName,
												"policies doesn't has policyProductCode :: " + policies);
										StrExitState = "Policy Not Found";
									}
									
								} else {
									data.setSessionData("S_PL_PROD_CODE_AVA", false);
									data.addToLog(currElementName,
											"Response doesn't have POLICIES :: " + strRespBodyTemp);
									StrExitState = "Policy Not Found";
								}
								

							} else {
								StrExitState = "Policy Not Found";
								data.setSessionData("S_PL_PROD_CODE_AVA", false);
								data.addToLog(currElementName, "Response doesn't has POLICIES :: " + strRespBodyTemp);
							}

						} else {

							data.addToLog(currElementName, "strRespBodyTemp is empty :: " + strRespBodyTemp);
						}

					} else {
						//strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
						data.setSessionData(currElementName, "FOREMOSTPOLICYINQUIIRY_BC API strRespBody" + strRespBody);
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FOREMOSTPOLICYINQUIIRY_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			
			if (null != StrExitState && "Policy Found".equalsIgnoreCase(StrExitState)) {
				objHostDetails.startHostReport(currElementName, "FOREMOSTPOLICYINQUIIRY_BC", strReqBody, region,(String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data, strRespBody, StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				objHostDetails.startHostReport(currElementName, "FOREMOSTPOLICYINQUIIRY_BC", strReqBody, region,(String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody, StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			
		} catch (Exception e) {
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

}
