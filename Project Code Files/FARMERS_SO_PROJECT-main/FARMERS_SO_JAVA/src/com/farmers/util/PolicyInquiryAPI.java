//package com.farmers.util;
//
//import org.json.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//
//import com.audium.server.AudiumException;
//import com.audium.server.session.DecisionElementData;
//import com.audium.server.voiceElement.DecisionElementBase;
//import com.farmers.report.SetHostDetails;
//import com.farmers.util.CommonAPIAccess;
//import com.farmers.util.Constants;
//
///*KYCBA_HOST_001*/
//public class PolicyInquiryAPI extends DecisionElementBase {
//
//	@Override
//	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
//		String StrExitState = Constants.ER;
//		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
//		SetHostDetails objHostDetails = new SetHostDetails(caa);
//		String strReqBody = Constants.EmptyString;
//		String strRespBody = Constants.EmptyString;
//		
//		try {
//			String strBU = (String) data.getSessionData(Constants.S_BU);
//			switch (strBU) {
//			case Constants.BW:
//				StrExitState = policyInquiry_RetriveInsurancePoliciesByParty(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
//				break;
//			case Constants.Farmers:
//				StrExitState = mulesoftFarmerPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
//				break;
//			case Constants.Foremost:
//				StrExitState = foremostPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
//				break;
//			case Constants.FWS:
//				StrExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
//				break;
//			case Constants.BU_21st:
//				StrExitState = PointPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
//				break;
//			default:
//				break;
//			}
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception while forming host details for KYCBA_HOST_001  :: "+e);
//			caa.printStackTrace(e);
//		}
//	
//		try {
//			objHostDetails.startHostReport(currElementName,"KYCBA_HOST_001", strReqBody);
//			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU);
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception while forming host reporting for KYCBA_HOST_001  PolicyInquiryAPI call  :: "+e);
//			caa.printStackTrace(e);
//		}
//		
//	return StrExitState;
//	}
//	
//	
//	
//	private String policyInquiry_RetriveInsurancePoliciesByParty(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
//		try {
//			if(data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
//					&& data.getSessionData(Constants.S_DOB) != null && data.getSessionData(Constants.S_POSTAL_CODE) != null
//					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
//				String url = (String) data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
//				String phoneNo = (String) data.getSessionData(Constants.S_ANI);
//				String dob = (String) data.getSessionData(Constants.S_DOB);
//				String postalcode = (String) data.getSessionData(Constants.S_POSTAL_CODE);
//				int connTimeout = (int) data.getSessionData(Constants.S_CONN_TIMEOUT);
//				int readTimeout = (int) data.getSessionData(Constants.S_READ_TIMEOUT);
//				
//				JSONObject resp =  (JSONObject) PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post.start(url, phoneNo, dob, postalcode, connTimeout, readTimeout);
//				data.addToLog(currElementName, "KYCBA_HOST_001 : policyInquiry_RetriveInsurancePoliciesByParty �PI response  :"+resp);
//				if(resp != null) {
//					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
//					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
//						data.addToLog(currElementName, "Set KYCBA_HOST_001 : policyInquiry_RetriveInsurancePoliciesByParty �PI Response into session with the key name of "+currElementName+Constants._RESP);
//						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
//						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
//						apiResponseManupulation_RetriveInsurancePoliciesByParty(data, caa, currElementName, strRespBody);
//						StrExitState = Constants.SU;
//					} else {
//						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
//					}
//					
//				}
//			}
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  RetrieveIntermediarySegmentationInfo API call  :: "+e);
//			caa.printStackTrace(e);
//		}
//		return StrExitState;
//	}
//	
//	private String mulesoftFarmerPolicyInquiry(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
//		try {
//			if(data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
//					&& data.getSessionData(Constants.S_POLICY_CONTRACT_NUM) != null && data.getSessionData(Constants.S_BILLING_ACC_NUM) != null
//					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
//				String url = (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
//				String policycontractnumber = (String) data.getSessionData(Constants.S_POLICY_CONTRACT_NUM);
//				String billingaccountnumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
//				String telephonenumber = (String) data.getSessionData(Constants.S_ANI);
//				int connTimeout = (int) data.getSessionData(Constants.S_CONN_TIMEOUT);
//				int readTimeout = (int) data.getSessionData(Constants.S_READ_TIMEOUT);
//				
//				JSONObject resp =  (JSONObject) MulesoftFarmerPolicyInquiry_Post.start(url, policycontractnumber, billingaccountnumber, telephonenumber, connTimeout, readTimeout);
//				data.addToLog(currElementName, "KYCBA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI response  :"+resp);
//				if(resp != null) {
//					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
//					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
//						data.addToLog(currElementName, "Set KYCBA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI Response into session with the key name of "+currElementName+Constants._RESP);
//						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
//						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
//						apiResponseManupulation_MulesoftFarmerPolicyInquiry(data, caa, currElementName, strRespBody);
//						StrExitState = Constants.SU;
//					} else {
//						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
//					}
//					
//				}
//			}
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  MulesoftFarmerPolicyInquiry_Post API call  :: "+e);
//			caa.printStackTrace(e);
//		}
//		return StrExitState;
//	}
//	
//	private String foremostPolicyInquiry(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
//		try {
//			if(data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
//					&& data.getSessionData(Constants.S_POLICY_CONTRACT_NUM) != null && data.getSessionData(Constants.S_BILLING_ACC_NUM) != null
//					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
//				String url = (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
//				String policynumber = (String) data.getSessionData(Constants.S_POLICY_CONTRACT_NUM);
//				String systemdate = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
//				String policysource = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
//				int connTimeout = (int) data.getSessionData(Constants.S_CONN_TIMEOUT);
//				int readTimeout = (int) data.getSessionData(Constants.S_READ_TIMEOUT);
//				
//				JSONObject resp =  (JSONObject) ForemostPolicyInquiry_Post.start(url, policynumber, systemdate, policysource, connTimeout, readTimeout);
//				data.addToLog(currElementName, "KYCBA_HOST_001 : ForemostPolicyInquiry �PI response  :"+resp);
//				if(resp != null) {
//					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
//					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
//						data.addToLog(currElementName, "Set KYCBA_HOST_001 : ForemostPolicyInquiry �PI Response into session with the key name of "+currElementName+Constants._RESP);
//						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
//						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
//						apiResponseManupulation_ForemostPolicyInquiry(data, caa, currElementName, strRespBody);
//						StrExitState = Constants.SU;
//					} else {
//						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
//					}
//					
//				}
//			}
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  ForemostPolicyInquiry API call  :: "+e);
//			caa.printStackTrace(e);
//		}
//		return StrExitState;
//	}
//	
//	private String fwsPolicyLookup(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
//		try {
//			if(data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
//					&& data.getSessionData(Constants.S_POLICY_CONTRACT_NUM) != null && data.getSessionData(Constants.S_BILLING_ACC_NUM) != null
//					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
//				String url = (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL);
//				String policycontractnumber = (String) data.getSessionData(Constants.S_POLICY_CONTRACT_NUM);
//				String billingaccountnumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
//				String telephonenumber = (String) data.getSessionData(Constants.S_ANI);
//				int connTimeout = (int) data.getSessionData(Constants.S_CONN_TIMEOUT);
//				int readTimeout = (int) data.getSessionData(Constants.S_READ_TIMEOUT);
//				
//				JSONObject resp = (JSONObject) FWSPolicyLookup_Post.start(url, policycontractnumber, billingaccountnumber,  telephonenumber, connTimeout, readTimeout);
//				data.addToLog(currElementName, "KYCBA_HOST_001 : FWSPolicyLookup �PI response  :"+resp);
//				if(resp != null) {
//					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
//					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
//						data.addToLog(currElementName, "Set KYCBA_HOST_001 : FWSPolicyLookup �PI Response into session with the key name of "+currElementName+Constants._RESP);
//						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
//						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
//						apiResponseManupulation_FwsPolicyLookup(data, caa, currElementName, strRespBody);
//						StrExitState = Constants.SU;
//					} else {
//						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
//					}
//					
//				}
//			}
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  FWSPolicyLookup API call  :: "+e);
//			caa.printStackTrace(e);
//		}
//		return StrExitState;
//	}
//	
//	private String PointPolicyInquiry(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
//		try {
//			if(data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null &&  data.getSessionData(Constants.S_ANI) != null 
//					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
//				String url = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
//				String input = (String) data.getSessionData(Constants.S_POLICY_CONTRACT_NUM);
//				int connTimeout = (int) data.getSessionData(Constants.S_CONN_TIMEOUT);
//				int readTimeout = (int) data.getSessionData(Constants.S_READ_TIMEOUT);
//				
//				JSONObject resp = (JSONObject) PointPolicyInquiry_Get.start(url, input, connTimeout, readTimeout);
//				data.addToLog(currElementName, "KYCBA_HOST_001 : PointPolicyInquiry �PI response  :"+resp);
//				if(resp != null) {
//					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
//					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
//						data.addToLog(currElementName, "Set KYCBA_HOST_001 : PointPolicyInquiry �PI Response into session with the key name of "+currElementName+Constants._RESP);
//						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
//						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
//						apiResponseManupulation_PointPolicyInquiry(data, caa, currElementName, strRespBody);
//						StrExitState = Constants.SU;
//					} else {
//						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
//					}
//					
//				}
//			}
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  PointPolicyInquiry API call  :: "+e);
//			caa.printStackTrace(e);
//		}
//		return StrExitState;
//	}
//	
//	private void apiResponseManupulation_RetriveInsurancePoliciesByParty(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
//		try {
//			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
//			JSONObject resp1 =(JSONObject) resp.get("transactionNotification");
//			JSONArray agentsArr = (JSONArray)resp1.get("remark");
//			String mdtDateOfBirth = (String)((JSONObject) agentsArr.get(0)).get("mdtDateOfBirth");
//			data.setSessionData("S_API_DOB", mdtDateOfBirth);
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
//			caa.printStackTrace(e);
//		}
//	}
//	
//	private void apiResponseManupulation_MulesoftFarmerPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
//		try {
//			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
//			JSONArray agentsArr = (JSONArray)resp.get("policies");
//			JSONArray insuredDetailsArr = (JSONArray)(((JSONObject) agentsArr.get(0)).get("insuredDetails"));
//			String birthDate = (String)((JSONObject) insuredDetailsArr.get(0)).get("birthDate");
//			data.setSessionData("S_API_DOB", birthDate);
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
//			caa.printStackTrace(e);
//		}
//	}
//	
//	private void apiResponseManupulation_ForemostPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
//		try {
//			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
//			JSONArray agentsArr = (JSONArray)resp.get("policies");
//			JSONArray insuredDetailsArr = (JSONArray)(((JSONObject) agentsArr.get(0)).get("insuredDetails"));
//			String birthDate = (String)((JSONObject) insuredDetailsArr.get(0)).get("birthDate");
//			data.setSessionData("S_API_DOB", birthDate);
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
//			caa.printStackTrace(e);
//		}
//	}
//	
//	private void apiResponseManupulation_FwsPolicyLookup(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
//		try {
//			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
//			JSONArray agentsArr = (JSONArray)resp.get("policies");
//			JSONArray insuredDetailsArr = (JSONArray)(((JSONObject) agentsArr.get(0)).get("insuredDetails"));
//			String birthDate = (String)((JSONObject) insuredDetailsArr.get(0)).get("birthDate");
//			data.setSessionData("S_API_DOB", birthDate);
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
//			caa.printStackTrace(e);
//		}
//	}
//	
//	private void apiResponseManupulation_PointPolicyInquiry(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
//		try {
//			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
//			JSONArray agentsArr = (JSONArray)resp.get("policies");
//			JSONArray insuredDetailsArr = (JSONArray)(((JSONObject) agentsArr.get(0)).get("insuredDetails"));
//			String birthDate = (String)((JSONObject) insuredDetailsArr.get(0)).get("birthDate");
//			data.setSessionData("S_API_DOB", birthDate);
//		} catch (Exception e) {
//			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
//			caa.printStackTrace(e);
//		}
//	}
//	
//}