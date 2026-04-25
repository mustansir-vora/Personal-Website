package com.farmers.adminHost;

import java.util.ArrayList;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetBusinessObjectsByDnisKey;
import com.farmers.FarmersAPI.AccountLinkAniLookup_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*SANI_ADM_004*/
public class BusinessObjects extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		data.addToLog(currElementName, "BusinessObjects started");
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
		if(data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String)data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			String strDnis = (String)data.getSessionData(Constants.S_DNIS);
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			data.addToLog(currElementName, "strDnis  :"+strDnis+ " :: API url : "+data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL)+ " :: conTimeout : "+conTimeout);
			GetBusinessObjectsByDnisKey test = new GetBusinessObjectsByDnisKey();
			JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, strDnis , conTimeout, readTimeout,context);
			strRespBody = resp.toString();
			data.addToLog(currElementName, "GetBusinessObjectsByDnisKey API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					data.addToLog(currElementName, "Set EM_ADM_002  GetBusinessObjectsByDnisKey API Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in GetBusinessObjectsByDnisKey API call  :: "+e);
		caa.printStackTrace(e);
	}
		
		try {
			String strBU = (String) data.getSessionData(Constants.S_BU);
			
			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");
			
			data.addToLog(currElementName, " PolicyLookup : strBU :: "+strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(currElementName, " A_FOREMOST_LOB : "+strForemostCode);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);
			
			if(strBristolCode!=null && null != strBU && strBristolCode.contains(strBU)) {
				//data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
			} else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				//data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
			} else if(strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				//data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
			} else if(strFWSCode!=null && strBU!=null && strFWSCode.contains(strBU)) {
				//data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				//data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
			}
			data.addToLog(currElementName, " strBU : "+strBU+" :: S_FLAG_BW_BU : "+data.getSessionData("S_FLAG_BW_BU")
			+" :: S_FLAG_FDS_BU : "+ data.getSessionData("S_FLAG_FDS_BU")+" :: S_FLAG_FOREMOST_BU : "+ data.getSessionData("S_FLAG_FOREMOST_BU")
			+" :: S_FLAG_FWS_BU : "+ data.getSessionData("S_FLAG_FWS_BU")+" :: S_FLAG_21ST_BU : "+ data.getSessionData("S_FLAG_21ST_BU"));
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host details for SIDA_HOST_001  :: "+e);
			caa.printStackTrace(e);
		}
          
		try {
			objHostDetails.startHostReport(currElementName,"GetBusinessObjectsByDnisKey API", strReqBody,"", (String)data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetBusinessObjectsByDnisKey API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
	return StrExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String StrExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			if(resArr.size() == 0 ) return Constants.ER;
			JSONObject obj = (JSONObject) resArr.get(0);
			data.setSessionData(Constants.S_DNISGROUP, obj.get("dnisgroup"));
			data.setSessionData(Constants.S_CATEGORY, obj.get("category"));
			data.setSessionData(Constants.S_LOB, obj.get("lineofbusiness"));
			data.setSessionData(Constants.S_BU, obj.get("lineofbusiness"));
			data.setSessionData(Constants.S_BRAND, obj.get("lineofbusiness"));
			data.setSessionData(Constants.S_ORIGINAL_LOB, obj.get("lineofbusiness"));
			data.setSessionData(Constants.S_ORIGINAL_DNIS_GROUP, obj.get("dnisgroup"));
			data.setSessionData(Constants.S_ORIGINAL_CATEGORY, obj.get("category"));
			data.setSessionData(Constants.S_FINAL_LOB, obj.get("lineofbusiness"));
			data.setSessionData(Constants.S_FINAL_DNIS_GROUP, obj.get("dnisgroup"));
			data.setSessionData(Constants.S_FINAL_CATEGORY, obj.get("category"));
			data.setSessionData(Constants.S_TOLLFREE_NUM, obj.get("tollfree"));
			data.setSessionData(Constants.S_TOLLFREE_DESCRIPTION, obj.get("description"));
			StrExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}

}