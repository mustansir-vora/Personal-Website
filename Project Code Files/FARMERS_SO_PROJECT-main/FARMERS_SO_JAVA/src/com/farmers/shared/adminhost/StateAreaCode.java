package com.farmers.shared.adminhost;

import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.AdminStateAreaCode.Re;
import com.farmers.bean.AdminStateAreaCode.StateAreaCodeRoot;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.google.gson.Gson;


public class StateAreaCode extends DecisionElementBase 
{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try
		{if(data.getSessionData(Constants.AREACODESURL) != null && 
		data.getSessionData(Constants.TENANTID) != null && 
		data.getSessionData(Constants.NAME) != null && 
		data.getSessionData(Constants.S_CALLID) != null && 
		data.getSessionData(Constants.AREACODE) != null&&
		data.getSessionData(Constants.S_CONN_TIMEOUT)!=null&&
		data.getSessionData(Constants.S_READ_TIMEOUT)!=null)
		{
		
		String AREACODESURL = (String) data.getSessionData(Constants.AREACODESURL);
		String TENANTID = "2";
		String CALLID = (String) data.getSessionData(Constants.S_CALLID);
		String AREACODE = (String) data.getSessionData(Constants.AREACODE);
		String  conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
		String  readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
		LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
		Lookupcall lookups = new Lookupcall();
		org.json.simple.JSONObject responses = lookups.TableByAreaCode(AREACODESURL,CALLID,Integer.parseInt(TENANTID),AREACODE,Integer.parseInt(conntimeout), Integer.parseInt(readtimeout),context);
		data.addToLog("responses", responses.toString());
		
		
		
	//	JSONObject jsonobj = (JSONObject) new JSONParser().parse(responses);
	//	JSONObject jsonobj = new JSONObject(responses);
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
		
		if(responses != null) {
			if(responses.containsKey(Constants.REQUEST_BODY)) strRespBody = responses.get(Constants.REQUEST_BODY).toString();
			if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) 
			{
				data.addToLog(currElementName, "Set KYCBA_HOST_001 : ForemostPolicyInquiry �PI Response into session with the key name of "+currElementName+Constants._RESP);
				strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
				data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
				apiResponseManupulation_StateAreaCode(data, caa, currElementName, strRespBody);
				StrExitState = Constants.SU;
			} else
			{
				strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
			}
		}

		}
		
		}	catch (Exception e)
			{
			data.addToLog(currElementName,"Exception while forming host reporting for FSLS_ADM_001  PolicyInquiryAPI call  :: "+e);
			caa.printStackTrace(e);
			}

		try {
			objHostDetails.startHostReport(currElementName,"KYCBA_HOST_001", strReqBody,"", (String) data.getSessionData(Constants.AREACODESURL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for KYCBA_HOST_001  PolicyInquiryAPI call  :: "+e);
			caa.printStackTrace(e);
		}
		
		return StrExitState;
	}
	private void apiResponseManupulation_StateAreaCode(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) throws ParseException, AudiumException
	{
		JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
		String GSONLIB = resp.toString();
		Gson gsonobj = new Gson();
		StateAreaCodeRoot StateAreaCode = gsonobj.fromJson(GSONLIB, StateAreaCodeRoot.class);
		List<Re> objs = StateAreaCode.getResponseBody().getRes();
		String AreaCode = objs.get(0).getAreacode();
		String Name = objs.get(0).getName();
		data.setSessionData("S_AREA_CODE", AreaCode);
		data.addToLog("S_AREA_CODE", AreaCode);
		data.setSessionData("S_STATE_NAME", Name);
		data.addToLog("S_STATE_NAME", Name);
	}
}
