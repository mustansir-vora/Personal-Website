package com.farmers.host;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.PointValidateCardandRoutingNumber_Post;
import com.farmers.FarmersAPI_NP.PointValidateCardandRoutingNumber_NP_Post;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;

/*KYCMF_HOST_001*/
public class PointValidateCardandRoutingNumber_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String accountNumber = null;
		String routingnumber = null;
		String readTimeout = null;
		String conTimeout = null;
		String cardnumber = null;
		String url = null;
		String paymentmethodtype = Constants.EmptyString;
		String strInvokedFrom = Constants.EmptyString;
		String finalurl = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {

			LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
			
			String tid = (String) data.getSessionData(Constants.S_CALLID);
			strInvokedFrom = (String) data.getSessionData("POINT_VALIDATE_CARD_API_INVOKED_FROM");
			
			if(null != strInvokedFrom && "PAY_BY_CREDIT_OR_DEBIT".equalsIgnoreCase(strInvokedFrom)) {
				url = (String) data.getSessionData("S_POINT_VALIDATE_CARD_BY_DEBIT_CREDIT");
				finalurl = (String) data.getSessionData("S_POINT_VALIDATE_CARD_BY_DEBIT_CREDIT");
				cardnumber = (String) data.getSessionData(Constants.S_CARD_NUMBER);
				routingnumber = null;
				paymentmethodtype="CC";
			}else if (null != strInvokedFrom && "21st_ACH1".equalsIgnoreCase(strInvokedFrom)) {
				url = (String) data.getSessionData(Constants.S_POINTVALIDATECARDROUTINGNUM_URL+(String)data.getSessionData("S_BANK_ROUTING_NUMBER"));
				finalurl = (String) data.getSessionData(Constants.S_POINTVALIDATECARDROUTINGNUM_URL);
				routingnumber = (String) data.getSessionData(Constants.S_BANK_ROUTING_NUMBER);
				cardnumber = null;
				paymentmethodtype = "ACH";
			}
			
			conTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
			 
			readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

			data.addToLog(currElementName,
					" : Request body forming :: url ::" + url + " : tid ::" + tid + " : invokedFrom :: "
							+ strInvokedFrom + " :: PaymentMethodType :: " + paymentmethodtype +  " : routingnumber ::" + routingnumber 
							+ " : cardnumber ::" + cardnumber + " : conTimeoutStr :: " + conTimeout
							+ " : readTimeoutStr :: " + readTimeout);

			if (data.getSessionData(Constants.S_POINTVALIDATECARDROUTINGNUM_URL) != null
					&& data.getSessionData(Constants.S_CALLID) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null) {

				
				
				PointValidateCardandRoutingNumber_Post apiObj = new PointValidateCardandRoutingNumber_Post();
				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				PointValidateCardandRoutingNumber_NP_Post objNP = new PointValidateCardandRoutingNumber_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_POINTVALIDATECARDROUTINGNUM_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, accountNumber, cardnumber, routingnumber, paymentmethodtype,Integer.parseInt(conTimeout), Integer.parseInt(readTimeout), context, region);
				}else {
					region="PROD";
				    resp = apiObj.start(url, tid, accountNumber, cardnumber, routingnumber, paymentmethodtype,Integer.parseInt(conTimeout), Integer.parseInt(readTimeout), context);
				}
               //END- UAT ENV SETUP CODE (PRIYA, SHAIK);
				data.addToLog(currElementName, "�PI response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (null != resp) {

					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName,
								"Valid Routing Number"
										+ currElementName);
						StrExitState = Constants.SU;
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));

					} else {
						StrExitState = Constants.STRING_NO;
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
						data.addToLog(currElementName, "Routing Number is not valid?" + Constants.RESPONSE_BODY);
					}
				} else {
					
					data.addToLog(currElementName, "Response is null " + resp);

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in PointValidateCardandRoutingNumber_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "PointValidateCardandRoutingNumber_BC", strReqBody,region, finalurl);
			objHostDetails.endHostReport(data, strRespBody,
					StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for PointValidateCardandRoutingNumber_BC API call  :: "
							+ e);
			caa.printStackTrace(e);
		}

		if("ER".equalsIgnoreCase(StrExitState)) {
			caa.createMSPKey(caa, data, "TFPC_HOST_001", "FAILURE");
		}
		return StrExitState;
	}

}
