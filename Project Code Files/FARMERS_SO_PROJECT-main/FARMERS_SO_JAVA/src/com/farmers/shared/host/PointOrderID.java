package com.farmers.shared.host;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class PointOrderID extends DecisionElementBase {

	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		try {
			String strBU = (String)data.getSessionData(Constants.S_BU);
			String policyNO = (String)data.getSessionData(Constants.S_POLICY_NUM);
			data.addToLog(currElementName, "BU : " + strBU+" ::  Policy No : "+policyNO);

			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");

			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);

			if (strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				strExitState = getFarmersIDCard_FDS(strReqBody, strRespBody, data, caa, currElementName);
			} else if (strFWSCode!=null && strBU!=null && strFWSCode.contains(strBU)) {
				strExitState =  GetOrderIDCard_FWS_Post(strReqBody, strRespBody, data, caa, currElementName);
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				strExitState =  Get21IDCard_21C_Post(strReqBody, strRespBody, data, caa, currElementName);
			} else if(strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
				strExitState = GetBWCard_BW_Post(strReqBody, strRespBody, data, caa, currElementName);
			} 
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for SIDC_HOST_004 : OrderIdCard API   :: " + e);
			caa.printStackTrace(e);
		}

		

		return strExitState;
	}

	private String GetBWCard_BW_Post(String strReqBody, String strRespBody, DecisionElementData data,	CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_POINTERID_URL) != null) {
				//PolicyBean polcyDetails = (PolicyBean)data.getSessionData(Constants.S_FINAL_POLICY_OBJ);
				String wsurl = (String) data.getSessionData(Constants.S_POINTERID_URL);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				String  PolicyNumber = (String) data.getSessionData("S_EPC_PAYMENT_POLICYNUM");
				String  companyCode = (String) data.getSessionData("BW_COMPANYCODE");
				String  deliveryMethod = (String) data.getSessionData(Constants.S_CARD_DELIVERY_METHOD);
				String  Size = "LS";
				String  FaxNumber = (String) data.getSessionData(Constants.S_FAX_NUMBER);
				String EmailAddress = data.getSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE) != null && data.getSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE).toString().equalsIgnoreCase(Constants.STRING_YES) ? (String) data.getSessionData(Constants.S_EMAIL) : Constants.EmptyString;
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog("wsurl", wsurl);
				data.addToLog(currElementName, " PolicyNumber : "+PolicyNumber+" :: companyCode : "+companyCode+" :: deliveryMethod : "+deliveryMethod+" :: Size : "+Size+" :: FaxNumber : "+FaxNumber +" :: EmailAddress : "+EmailAddress);
				Lookupcall lookups = new Lookupcall();
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				 if("YES".equalsIgnoreCase(UAT_FLAG)) {
					 region = regionDetails.get(Constants.S_POINTERID_URL);
	                }else {
	                	region="PROD";
	                }				
				
				org.json.simple.JSONObject responses = lookups.BWOrderIDCard(wsurl, tid, PolicyNumber, companyCode, deliveryMethod, Size, FaxNumber, EmailAddress, 11000, 11000, context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if(responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200) {
						//data.addToLog(currElementName, "Set SIDC_HOST_004 : BWOrderIdcardLookup API Response into session with the key name of "+currElementName+Constants._RESP);
						//strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						//data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strRespBody = "Success";
						strExitState = Constants.SU;
					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDC_HOST_004 : BWOrderIdcardLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"SIDC_HOST_004 : OrderIdCard API", strReqBody,region, (String) data.getSessionData(Constants.S_POINTERID_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDC_HOST_004 : OrderIdCard API  :: "+ e);
			caa.printStackTrace(e);
		}
		

		return strExitState;
	}

	private String getFarmersIDCard_FDS(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_POINTERID_URL) != null) {
				//PolicyBean polcyDetails = (PolicyBean)data.getSessionData(Constants.S_FINAL_POLICY_OBJ);
				String wsurl = (String) data.getSessionData(Constants.S_POINTERID_URL);
				String PolicyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String policysource = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
				String size = (String) data.getSessionData(Constants.S_SIZE);
				String policyState = (String) data.getSessionData(Constants.S_STATE);
				if("FL".equalsIgnoreCase(policyState)) {
					size =  "WS";
				}
				else {
					size =  "LS";
				}
				String  deliverymethod = (String) data.getSessionData(Constants.S_CARD_DELIVERY_METHOD);
				String  faxnumber = (String) data.getSessionData(Constants.S_FAX_NUMBER);
				String EmailAddress = data.getSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE) != null && data.getSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE).toString().equalsIgnoreCase(Constants.STRING_YES) ? (String) data.getSessionData(Constants.S_EMAIL) : Constants.EmptyString;
				String  conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String  readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog("wsurl", wsurl);
				data.addToLog("PolicyNumber", PolicyNumber);
				data.addToLog("faxnumber", faxnumber);
				data.addToLog("conntimeout", conntimeout);
				data.addToLog("readtimeout", readtimeout);
				data.addToLog("tid", tid);
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE END(SHAIK,PRIYA)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				 if("YES".equalsIgnoreCase(UAT_FLAG)) {
					 region = regionDetails.get(Constants.S_POINTERID_URL);
	                }else {
	                	region="PROD";
	                }
				
				//UAT ENV CHANGE END(SHAIK,PRIYA)
				org.json.simple.JSONObject responses = lookups.FDSOrderIDCard(wsurl, tid,PolicyNumber,policysource,deliverymethod,size,faxnumber,EmailAddress, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout),context, region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if(responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200) {
						//data.addToLog(currElementName, "Set SIDC_HOST_004 : FDSOrderIdcardLookup API Response into session with the key name of "+currElementName+Constants._RESP);
						//strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						//data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strRespBody = "Success";
						strExitState = Constants.SU;
					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDC_HOST_004 : FDSOrderIdcardLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"SIDC_HOST_004 : OrderIdCard API", strReqBody, region,(String) data.getSessionData(Constants.S_POINTERID_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDC_HOST_004 : OrderIdCard API  :: "+ e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

	private String GetOrderIDCard_FWS_Post(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_POINTERID_URL) != null) {
				//PolicyBean polcyDetails = (PolicyBean)data.getSessionData(Constants.S_FINAL_POLICY_OBJ);
				String wsurl = (String) data.getSessionData(Constants.S_POINTERID_URL);
				String PolicyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String faxnumber = (String) data.getSessionData(Constants.S_FAX_NUMBER);
				String deliverymethod = (String) data.getSessionData(Constants.S_CARD_DELIVERY_METHOD);
				String policysource = (String) data.getSessionData("S_POLICY_SOURCE");
				String policysuffix = (String) data.getSessionData("S_FWS_POLICY_SUFFIX");
				if (deliverymethod.equalsIgnoreCase("Mail") && PolicyNumber.length() >=10 && policysource.equalsIgnoreCase("ARS")) {
					PolicyNumber = PolicyNumber.substring(0, 9);
				}
				
              //START : CS1172205 : Farmers Insurance | US | FWS ID Cards Not Being Sent in IVR	
				
				if (policysource.equalsIgnoreCase("Met360") && PolicyNumber.endsWith("0")) {
				    PolicyNumber = PolicyNumber.substring(0, PolicyNumber.length() - 1);
				    data.addToLog(currElementName," FWS Met 360 Trailing Zero "+PolicyNumber);	
				}
			//END : CS1172205 : Farmers Insurance | US | FWS ID Cards Not Being Sent in IVR
				
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String lob = (String) data.getSessionData("S_FWS_POLICY_LOB");
				String policystate = (String) data.getSessionData("S_POLICY_STATE");
				String effectivedate = (String) data.getSessionData("S_FWS_POLICY_EFF_DATE");
				String firstname = (String) data.getSessionData("S_FIRST_NAME");
				String lastname = (String) data.getSessionData("S_LAST_NAME");
				String middlename = Constants.EmptyString;
				String internalpolicynumber = (String) data.getSessionData("S_FWS_INT_POLICY_NO");
				String internalpolicyversion = (String) data.getSessionData("S_FWS_INTERNAL_POLICY_VERSION");
				String  conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String  readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String emailAddress = (String) data.getSessionData(Constants.S_EMAIL);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				data.addToLog("wsurl", wsurl);
				
				data.addToLog(currElementName, " PolicyNumber : "+PolicyNumber+" :: faxnumber : "+faxnumber+" :: deliverymethod : "+deliverymethod+" :: lob : "+lob+" :: policysource : "+policysource +" :: policysuffix : "+policysuffix+" :: effectivedate : "+effectivedate+" :: firstname : "+firstname+" :: lastname : "+lastname+" :: internalpolicynumber : "+internalpolicynumber+" :: internalpolicyversion : "+internalpolicyversion+" :: emailAddress : "+emailAddress);
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE END(SHAIK,PRIYA)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				 if("YES".equalsIgnoreCase(UAT_FLAG)) {
					 region = regionDetails.get(Constants.S_POINTERID_URL);
	                }else {
	                	region="PROD";
	                }
				//UAT ENV CHANGE END(SHAIK,PRIYA)
				org.json.simple.JSONObject responses = lookups.FWSOrderIDCard(wsurl, tid, PolicyNumber, policysource, lob, policystate, policysuffix, deliverymethod, effectivedate, faxnumber, firstname, lastname, middlename, internalpolicynumber, internalpolicyversion,emailAddress, Integer.parseInt(conntimeout),  Integer.parseInt(readtimeout),context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if(responses != null)  {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200) {
						//data.addToLog(currElementName, "Set SIDC_HOST_004 : FWSOrderIdcardLookup API Response into session with the key name of "+currElementName+Constants._RESP);
						//strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						//data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strRespBody = "Success";
						strExitState = Constants.SU;
					} else {
						strRespBody = (null != responses.get(Constants.RESPONSE_MSG)) ? responses.get(Constants.RESPONSE_MSG).toString() : Constants.EmptyString;
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDC_HOST_004  FWSOrderIdcardLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"SIDC_HOST_004 : OrderIdCard API", strReqBody,region, (String) data.getSessionData(Constants.S_POINTERID_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDC_HOST_004 : OrderIdCard API  :: "+ e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String Get21IDCard_21C_Post(String strRespBody, String strReqBody, DecisionElementData data,CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_POINTERID_URL) != null) {
				//PolicyBean polcyDetails = (PolicyBean)data.getSessionData(Constants.S_FINAL_POLICY_OBJ);
				String wsurl = (String) data.getSessionData(Constants.S_POINTERID_URL);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String ani = (String) data.getSessionData(Constants.S_ANI);
				String PolicyNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String MasterCompanyCode = (String) data.getSessionData(Constants.S_MASTERCOMPANYCODE);
				String DeliveryMethod = (String) data.getSessionData(Constants.S_CARD_DELIVERY_METHOD);
				String Size = (String) data.getSessionData(Constants.S_SIZE);
				if(null != data.getSessionData(Constants.S_STATE) && "FL".equalsIgnoreCase((String) data.getSessionData(Constants.S_STATE))) {
					Size =  "WS";
				}
				else {
					Size =  "LS";
				}
				String FaxNumber = (String) data.getSessionData(Constants.S_FAX_NUMBER);
				String EmailAddress = data.getSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE) != null && data.getSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE).toString().equalsIgnoreCase(Constants.STRING_YES) ? (String) data.getSessionData(Constants.S_EMAIL) : Constants.EmptyString;
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				data.addToLog("wsurl", wsurl);
				data.addToLog("ani", ani);
				data.addToLog("conntimeout", conntimeout);
				data.addToLog("readtimeout", readtimeout);
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGE END(SHAIK,PRIYA)
				data.addToLog("API URL: ",wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				 if("YES".equalsIgnoreCase(UAT_FLAG)) {
					 region = regionDetails.get(Constants.S_POINTERID_URL);
	                }else {
	                	region="PROD";
	                }
				
				//UAT ENV CHANGE END(SHAIK,PRIYA)
				org.json.simple.JSONObject responses = lookups.TwentyOrderIDCard(wsurl,tid, PolicyNumber, MasterCompanyCode,DeliveryMethod,Size,FaxNumber,EmailAddress,Integer.parseInt(conntimeout),Integer.parseInt(readtimeout),context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());		
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if(responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 ) {
						//data.addToLog(currElementName, "Set SIDC_HOST_004 : 21stOrderIdcardLookup API Response into session with the key name of "+currElementName+Constants._RESP);
						//strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						//data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strRespBody = "Success";
						strExitState = Constants.SU;
					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch (Exception e)
		{
			data.addToLog(currElementName,"Exception in SIDC_HOST_004 : 21stOrderIdcardLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"SIDC_HOST_004 : OrderIdCard API", strReqBody,region, (String) data.getSessionData(Constants.S_POINTERID_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDC_HOST_004 : OrderIdCard API  :: "+ e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	/*private String apiResponseManupulation_BWCard(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			String GSONLIB = resp.toString();
			Gson gsonobj = new Gson();
			BWRoot billpresentroot = gsonobj.fromJson(GSONLIB, BWRoot.class); 
			strExitState = Constants.SU;	
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation_BWCard method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}*/

	/*private String apiResponseManupulation_FarmersIDCard_FDS_FWS(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			Double BillingAmount = null  ;
			String PaymentStatus = "";
			String PaymentRecordType = "";
			String PaymentStatusCode = ""; 

			String GSONLIB = resp.toString();
			Gson gsonobj = new Gson();
			com.farmers.bean.Halo.BillingDetails.HalloBillingDetails HaloBillingDetails = gsonobj.fromJson(GSONLIB, com.farmers.bean.Halo.BillingDetails.HalloBillingDetails.class);
			List<com.farmers.bean.Halo.BillingDetails.BillingDetail> billingDetails = HaloBillingDetails.getBillingDetails();
			for(int i=0;i<billingDetails.size();i++) {
				List<Payment> Payment = billingDetails.get(i).getPolicy().getPayments();
				for(int z=0;z<Payment.size();z++) {
					if(Payment.get(z).getPaymentStatusCode()!=null) PaymentStatusCode = Payment.get(z).getPaymentStatusCode();
					if (Payment.get(z).getPaymentStatus()!=null) PaymentStatus = Payment.get(z).getPaymentStatus();
					if (Payment.get(z).getPaymentRecordType()!=null) PaymentRecordType = Payment.get(z).getPaymentRecordType();
				}
				List<com.farmers.bean.Halo.BillingDetails.Term> terms = billingDetails.get(i).getPolicy().getTerms();
				for(int j=0;j<terms.size();j++) {
					if(terms.get(j).getBillAmount()!=null&&terms.get(j).getBillAmount()>0) BillingAmount = terms.get(j).getBillAmount();
				}
			}
			Collection<String> strings = new ArrayList<>();
			strings.add("PENDING");
			strings.add("PROCESSED");
			strings.add("PAY");
			strings.add("COM");
			strings.add("APL");
			strings.add("ATL");
			strings.add("pay");
			strings.add("SCH");
			if(BillingAmount!=null&&BillingAmount>0) {
				data.setSessionData("S_Current_Balance", BillingAmount);
				data.addToLog("S_Current_Balance",BillingAmount.toString());
			}
			if(strings.contains(PaymentStatus)&&strings.contains(PaymentRecordType)&&strings.contains(PaymentStatusCode)) {
				data.setSessionData("S_LastPayment_Amount", "");
				data.addToLog("S_LastPayment_Amount", "");
				data.setSessionData("S_LastPayment_Date", "");
				data.addToLog("S_LastPayment_Date", "");
				data.addToLog("PaymentStatus","");
			}
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation_FarmersIDCard_FDS method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}*/

	/*private String apiResponseManupulation_21IDCard(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			String PaymentDueDate = "";
			String Types = "";
			String Balance = "";
			String LastPaymetAmount ="";
			String LastPaymetDate = "";
			String NextPaymentDate = "";
			String NextPaymentAmount = "";
			String CurrentBalance = "";
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			String GSONLIB = resp.toString();
			Gson gsonobj = new Gson();
			com.farmers.bean.FWSBillingLookup.PointBilling.PointbillingInquiry obj = gsonobj.fromJson(GSONLIB,com.farmers.bean.FWSBillingLookup.PointBilling.PointbillingInquiry.class);
			List<com.farmers.bean.FWSBillingLookup.PointBilling.Term> BillTermsummary = obj.getBillingSummary().getTerms();
			for (int i = 0; i < BillTermsummary.size(); i++) {	
				if(BillTermsummary.get(i).getPayplan().getDescription()!=null) {
					String PayPlanDescription = BillTermsummary.get(i).getPayplan().getDescription();
					if(PayPlanDescription.equalsIgnoreCase("RACH")||PayPlanDescription.equalsIgnoreCase("RCC")) {
						data.setSessionData("S_Auto_Pay", "Y");
						data.addToLog("S_Auto_Pay", PayPlanDescription);
					}
				}
				List<com.farmers.bean.FWSBillingLookup.PointBilling.Due> DueList = BillTermsummary.get(i).getDues();
				for (int j = 0; j < DueList.size(); i++) {
					if(BillTermsummary.get(i).getDues()!=null) {
						Types = DueList.get(i).getType();
					    if(Types.equalsIgnoreCase("MINIMUM")) {
					    	PaymentDueDate = DueList.get(i).getDate();
					    	CurrentBalance = DueList.get(i).getAmount();
					    	data.setSessionData("S_PaymentDue_Date", PaymentDueDate);
							data.addToLog("PaymentDueDate", PaymentDueDate);
							data.setSessionData("S_CurrentBalance", CurrentBalance);
							data.addToLog("S_CurrentBalance", CurrentBalance);
					    }
					    if(Types.equalsIgnoreCase("NEXT")) {
					    	CurrentBalance = DueList.get(i).getAmount();
							data.setSessionData("S_Balance", Balance);
							data.addToLog("Balance",Balance);
						   	data.setSessionData("S_RenewalBalance", CurrentBalance);
							data.addToLog("S_RenewalBalance", CurrentBalance);
					    }
					    if(Types.equalsIgnoreCase("PAST")) {
					    	LastPaymetAmount = DueList.get(i).getAmount();
					    	LastPaymetDate = DueList.get(i).getDate(); // Need to Filter most recent dates
					    	data.setSessionData("S_LastPayment_Amount", LastPaymetAmount);
							data.addToLog("S_LastPayment_Amount", LastPaymetAmount);
							data.setSessionData("S_LastPayment_Date", LastPaymetDate);
							data.addToLog("S_LastPayment_Date", LastPaymetDate);
					    }
					    if(Types.equalsIgnoreCase("NEXT")) {
					    	NextPaymentAmount = DueList.get(i).getAmount();
					    	NextPaymentDate = DueList.get(i).getDate();
					    	data.setSessionData("S_NextPayment_Amount", NextPaymentAmount);
							data.addToLog("S_NextPayment_Amount", NextPaymentAmount);
							data.setSessionData("S_NextPayment_Date", NextPaymentDate);
							data.addToLog("S_NextPayment_Date", NextPaymentDate);	
					    }
					}
				} 
			}
			strExitState = Constants.SU;
		}catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}*/



}
