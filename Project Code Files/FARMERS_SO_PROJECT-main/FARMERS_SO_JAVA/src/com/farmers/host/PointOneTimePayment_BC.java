package com.farmers.host;

import java.io.File;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.PointOneTimePayment_Post;
import com.farmers.FarmersAPI_NP.PointOneTimePayment_NP_Post;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;

/*KYCMF_HOST_001*/
public class PointOneTimePayment_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strInvokedFrom = Constants.EmptyString;
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
	
		String routingnumber = null;
		String cardexpirationdate= null;
		String cardnumber = null;
		String paymentid = null;
		String bankaccounttype = null;
		String bankaccountnumber = null;
		String additionalchargeamount = "2.7500";
		String url = null;
		String cvvnumber = null;
		
		String additionalchargetype = (String) data.getSessionData("S_ADDITIONAL_CHARGE_TYPE");
		String paymentmethodtype = null;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);

			File file = new File(
					new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
			context.setConfigLocation(file.toURI());
			
			if (data.getSessionData(Constants.S_POINTONETIMEPAYMENT_URL) != null
					&& data.getSessionData(Constants.S_CALLID) != null) {

				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String paymentamount = (String) data.getSessionData(Constants.S_PAYMENTAMOUNT);
				String firstname = (String) data.getSessionData(Constants.S_FIRSTNAME);
				String middlename = (String) data.getSessionData(Constants.S_MIDDLENAME);
				String lastname = (String) data.getSessionData(Constants.S_LASTNAME);
				String address = (String) data.getSessionData(Constants.S_ADDRESS);
				String city = (String) data.getSessionData(Constants.S_CITY);
				String postalcode = (String) data.getSessionData(Constants.S_POSTALCODE);// Policy lookup response
				String state = (String) data.getSessionData(Constants.S_STATE);
				String phonenumber = (String) data.getSessionData(Constants.S_TID);
				strInvokedFrom = (String) data.getSessionData("POINT_VALIDATE_CARD_API_INVOKED_FROM");
				if (null != strInvokedFrom && "PAY_BY_CREDIT_OR_DEBIT".equalsIgnoreCase(strInvokedFrom)) {
					// url
					// https://api-np-ss.farmersinsurance.com/PLE/pymtms/v1/paymentType?operation=validateCardNumber
					cardnumber = (String) data.getSessionData(Constants.S_CARD_NUMBER);
					cardexpirationdate = (String) data.getSessionData(Constants.S_EXPIRATIONDATE);
					routingnumber = null;
					paymentmethodtype = "ATM";
					bankaccountnumber = null;
					bankaccounttype = null;
				} else if (null != strInvokedFrom && "21st_ACH1".equalsIgnoreCase(strInvokedFrom)) {
					url = (String) data.getSessionData(Constants.S_POINTVALIDATECARDROUTINGNUM_URL
							+ (String) data.getSessionData("S_BANK_ROUTING_NUMBER"));
					routingnumber = (String) data.getSessionData(Constants.S_BANK_ROUTING_NUMBER);
					cardnumber = null;
					cardexpirationdate =null;
					bankaccounttype = (String) data.getSessionData("S_BANK_ACCOUNT_TYPE");
					bankaccountnumber = (String) data.getSessionData(Constants.S_BACKACCNUMBER);
					paymentmethodtype = "ACH";
				}
				int conTimeout = (int) data.getSessionData(Constants.S_CONN_TIMEOUT);
				int readTimeout = (int) data.getSessionData(Constants.S_READ_TIMEOUT);
				
				data.addToLog(currElementName,
						" : Request body forming :: url ::" + url + " : tid ::" + tid + " : invokedFrom :: "+ strInvokedFrom + " :paymentamount ::"+ paymentamount + "  :: PaymentMethodType :: " + paymentmethodtype +  " : routingnumber ::" + routingnumber 
								+ " : cardnumber ::" + "*******" + ":firstname ::" +firstname+  ":middlename ::" +middlename+ ": conTimeoutStr :: " + conTimeout
								+ " : readTimeoutStr :: " + readTimeout);
				PointOneTimePayment_Post apiObj = new PointOneTimePayment_Post();

				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				PointOneTimePayment_NP_Post objNP = new PointOneTimePayment_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_POINTONETIMEPAYMENT_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, paymentamount, firstname, middlename, lastname, address, city,
							postalcode, state, phonenumber, paymentmethodtype, bankaccountnumber, bankaccounttype,
							routingnumber, cardnumber, cardexpirationdate, cvvnumber, additionalchargetype,
							additionalchargeamount, paymentid, conTimeout, readTimeout, context, region);
				}else {
					region="PROD";
				    resp = apiObj.start(url, tid, paymentamount, firstname, middlename, lastname, address, city,
							postalcode, state, phonenumber, paymentmethodtype, bankaccountnumber, bankaccounttype,
							routingnumber, cardnumber, cardexpirationdate, cvvnumber, additionalchargetype,
							additionalchargeamount, paymentid, conTimeout, readTimeout, context);
				}
             //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
				//data.addToLog(currElementName, "�PI response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));

				if (null != resp) {

					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName,
								"Set PointOneTimePayment_BC API Response into session with the key name of "
										+ currElementName + Constants._RESP);
//						JSONObject strRespBodyTemp = (JSONObject) resp.get(Constants.RESPONSE_BODY);
						strRespBody = (String) resp.get(Constants.RESPONSE_BODY);
						StrExitState = Constants.SU;

						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));

					} else {
						StrExitState = Constants.ER;
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (

		Exception e) {
			data.addToLog(currElementName, "Exception in PointOneTimePayment_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "PointOneTimePayment_BC", strReqBody, region,(String) data.getSessionData(Constants.S_POINTVALIDATECARDROUTINGNUM_URL));
			objHostDetails.endHostReport(data, strRespBody,
					StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for PointOneTimePayment_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

}
