package com.farmers.shared.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class IVR2Text  extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_IVR2TEXTURL) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_IVR2TEXTURL);
				String ivrCallID = (String) data.getSessionData(Constants.S_CALLID);
				if(null != wsurl && wsurl.contains(Constants.S_CALLID)) wsurl = wsurl.replace(Constants.S_CALLID, ivrCallID);
				String destinationapp = "SRM";
				String tollfreenumber = ((String) data.getSessionData(Constants.S_TOLLFREE_NUM)!=null)?((String)data.getSessionData(Constants.S_TOLLFREE_NUM)) : Constants.EmptyString;
				String poptype = "04";
				String callerani = ((String) data.getSessionData(Constants.S_ANI)!=null)?((String)data.getSessionData(Constants.S_ANI)) : Constants.EmptyString;
				String calledinto = ((String) data.getSessionData(Constants.S_DNIS)!=null)?((String)data.getSessionData(Constants.S_DNIS)) : Constants.EmptyString;
				String language = (String) data.getSessionData(Constants.S_PREF_LANG);
				if (language.equalsIgnoreCase("EN")) {
					language = "en-us";
				}
				else {
					language = "es-us";
				}
				String calledintime = ((String) data.getSessionData(Constants.S_CALLED_INTIME)!=null)?((String)data.getSessionData(Constants.S_CALLED_INTIME)) : Constants.EmptyString;
				String tollfreedescription =  ((String) data.getSessionData(Constants.S_TOLLFREE_DESCRIPTION)!=null)?((String)data.getSessionData(Constants.S_TOLLFREE_DESCRIPTION)) : Constants.EmptyString;
				String transferreason = ((String) data.getSessionData(Constants.S_TRANFER_REASON)!=null)?((String)data.getSessionData(Constants.S_TRANFER_REASON)) : Constants.S_IVR2TEXT_TRANSFERREASON;
				String intent = (String) data.getSessionData(Constants.APPTAG);
				//String brand =  (String) data.getSessionData(Constants.S_LOB);
				String brand = Constants.EmptyString;
				if (null != data.getSessionData("S_FLAG_FDS_BU") && data.getSessionData("S_FLAG_FDS_BU").equals(Constants.STRING_YES)) {
					brand = "FDS/SO";
				}
				else if (null != data.getSessionData("S_FLAG_BW_BU") && data.getSessionData("S_FLAG_BW_BU").equals(Constants.STRING_YES)) {
					brand = "BW";
				}
				else if(null != data.getSessionData("S_FLAG_FOREMOST_BU") && data.getSessionData("S_FLAG_FOREMOST_BU").equals(Constants.STRING_YES)) {
					brand = "FM";
				}
				else if(null != data.getSessionData("S_FLAG_FWS_BU") && data.getSessionData("S_FLAG_FWS_BU").equals(Constants.STRING_YES)) {
					brand = "FWS";
				}
				else if(null != data.getSessionData("S_FLAG_21ST_BU") && data.getSessionData("S_FLAG_21ST_BU").equals(Constants.STRING_YES)){
					brand = "21ST";
				}
				String callerinput = (String) data.getSessionData(Constants.IVRTT_MN_001_VALUE);
				String callertype = ((String) data.getSessionData(Constants.S_CALLLER_TYPE)!=null)?((String)data.getSessionData(Constants.S_CALLLER_TYPE)) : "01";
				String an1 = ((String) data.getSessionData(Constants.S_AN1)!=null)?((String)data.getSessionData(Constants.S_AN1)) : Constants.EmptyString;
				String upn1 = ((String) data.getSessionData(Constants.S_UPN1)!=null)?((String)data.getSessionData(Constants.S_UPN1)) : Constants.EmptyString;
				boolean validpolicynumber = false;
				if ( null != data.getSessionData("IS_CALLED_SHARED_ID_AUTH") && data.getSessionData("IS_CALLED_SHARED_ID_AUTH").toString().equalsIgnoreCase("TRUE")) {
					validpolicynumber = true;
				}
				else {
					validpolicynumber = false;
				}
				String callerrisklevel = ((String) data.getSessionData(Constants.S_TI_SCORE)!=null)?((String)data.getSessionData(Constants.S_TI_SCORE)) : Constants.EmptyString;
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog(currElementName, " IVR2Text : wsurl : "+wsurl);
				data.addToLog(currElementName, " destinationapp : "+destinationapp+" :: tollfreenumber : "+tollfreenumber+" :: poptype : "+poptype+" :: callerani : "+callerani
						+" :: calledinto : "+calledinto+" :: language : "+language+" :: calledintime : "+calledintime+" :: tollfreedescription : "+tollfreedescription
						+" :: transferreason : "+transferreason+" :: intent : "+intent+" :: brand : "+brand+" :: callerinput : "+callerinput+" :: callertype : "+callertype
						+" :: an1 : "+an1+" :: upn1 : "+upn1+" :: validpolicynumber : "+validpolicynumber+" :: callerrisklevel : "+callerrisklevel);
				Lookupcall lookups = new Lookupcall();
				//UAT ENV CHANGESTRT(PRIYA,SHAIK)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				 if("YES".equalsIgnoreCase(UAT_FLAG)) {
					 region = regionDetails.get(Constants.S_IVR2TEXTURL);
	                }else {
	                	region="PROD";
	                }
				 
				org.json.simple.JSONObject responses = lookups.GetIVR2Text_Post(wsurl, ivrCallID,  callerani, destinationapp, tollfreenumber, callerinput, poptype, calledinto,
						calledintime, tollfreedescription, callertype, language, an1, upn1, transferreason, intent , brand , validpolicynumber, callerrisklevel, conTimeout, 
						readTimeout, context,region,UAT_FLAG);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if(responses != null) {
					if(responses.containsKey(Constants.REQUEST_BODY)) strRespBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Set IVRTT_HOST_002 : IVR2Text API Response into session with the key name of "+currElementName+Constants._RESP);
						data.setSessionData("S_IVRTOTEXT_CALLOUTCOME", "Y");
						StrExitState = Constants.SU;
					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
			
		}catch(Exception e) {
			data.addToLog(currElementName,"Exception in IVRTT_HOST_002 IVR2Text API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"IVR2TextAPI", strReqBody,region, (String) data.getSessionData(Constants.S_IVR2TEXTURL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for IVR2TextAPI API call  :: "+e);
			caa.printStackTrace(e);
		}
	return StrExitState;
	}
	
}
