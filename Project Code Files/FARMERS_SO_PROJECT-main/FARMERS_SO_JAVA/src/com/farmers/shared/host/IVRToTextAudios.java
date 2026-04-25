package com.farmers.shared.host;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetIVRToTextRecordings;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class IVRToTextAudios extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		data.setSessionData("DestinationApp","SRM");
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
			if(data.getSessionData(Constants.S_IVR2TEXT_AUDIOS_URL) != null) {
				String url = (String) data.getSessionData(Constants.S_IVR2TEXT_AUDIOS_URL);
				String callid =  (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog(currElementName, "url : "+url);
				GetIVRToTextRecordings obj = new GetIVRToTextRecordings();
				JSONObject resp =  (JSONObject) obj.start(url, callid, Constants.tenantid, conTimeout, readTimeout, context);
				data.addToLog(currElementName, "IVRToTextAudios API response  :"+resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set IVRToTextAudios  Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch(Exception e) {
			data.addToLog(currElementName,"Exception in IVRToTextAudios API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName,"IVRToTextAudios", strReqBody,"", (String) data.getSessionData(Constants.S_IVR2TEXT_AUDIOS_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for IVRToTextAudios API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;

	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			if(null == resArr || resArr.size() == 0) return Constants.ER;
			data.addToLog(currElementName,"IVRToTextAudios Admin API resp size :"+resArr.size());
			@SuppressWarnings("unchecked")
			List<JSONObject> list = (List<JSONObject>) StreamSupport.stream(resArr.spliterator(), false).map(e -> (JSONObject) e).collect(Collectors.toList());
			for(JSONObject	tempObj : list) {
				if(tempObj.containsKey(Constants.key) && null != tempObj.get(Constants.key)) {
					if(Constants.invite_prompt.equalsIgnoreCase((String)tempObj.get(Constants.key))) {
						setAudios(tempObj, data, caa, currElementName, Constants.IVRTT_MN_001);
						String audioForMN1 = (String)data.getSessionData(Constants.IVRTT_MN_001+"_TTS");
						if(audioForMN1.contains(" ")) audioForMN1 = audioForMN1.replaceAll(" ", ".");
						if(audioForMN1.contains(",")) audioForMN1 = audioForMN1.replaceAll(",", ".");
						data.setSessionData(Constants.VXMLParam1, audioForMN1);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
						data.addToLog(currElementName,"IVRTT_MN_001 : VXMLParam1 = "+data.getSessionData(Constants.VXMLParam1));
					} else if(Constants.success_prompt.equalsIgnoreCase((String)tempObj.get(Constants.key))) {
						setAudios(tempObj, data, caa, currElementName, Constants.IVRTT_PA_002);
					} else if(Constants.failure_prompt.equalsIgnoreCase((String)tempObj.get(Constants.key))) {
						setAudios(tempObj, data, caa, currElementName, Constants.IVRTT_PA_001);
					}
				}
			}
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in IVRToTextIntents apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	private void setAudios(JSONObject Obj, DecisionElementData data, CommonAPIAccess caa, String currElementName, String elementKey) {
		try {
			String wavKeyName = elementKey+"_WAV";
			String ttsKeyName = elementKey+"_TTS";
			if(data.getSessionData(Constants.S_PREF_LANG).equals(Constants.EN)) {
				if(!((String)Obj.get("audioenglishwavpath")).equals("") && ((String)Obj.get("audioenglishwavpath")).contains(".wav")) {
					String wavFile = (String)Obj.get("audioenglishwavpath"); 
					if(wavFile.contains("\\")) {
						String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
						wavFile = wavFileArr[wavFileArr.length-1];
					}
					data.setSessionData(wavKeyName, wavFile);
				}
				if(!((String)Obj.get("audioenglishtext")).equals(""))	data.setSessionData(ttsKeyName, Obj.get("audioenglishtext"));
				data.addToLog(currElementName, "EN :: audioenglishwavpath : for "+wavKeyName+" session Key : "+data.getSessionData(wavKeyName)+" :: audioenglishtext :  for "+wavKeyName+" session Key : "+data.getSessionData(ttsKeyName));
			} else {
				if(!((String)Obj.get("audiospanishwavpath")).equals("") && ((String)Obj.get("audiospanishwavpath")).contains(".wav")) {
					String wavFile = (String)Obj.get("audiospanishwavpath"); 
					if(wavFile.contains("\\")) {
						String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
						wavFile = wavFileArr[wavFileArr.length-1];
					}
					data.setSessionData(wavKeyName, wavFile);
				}
				if(!((String)Obj.get("audiospanishtext")).equals("")) data.setSessionData(ttsKeyName, Obj.get("audiospanishtext"));
				data.addToLog(currElementName, "SP:: audiospanishwavpath :  for "+wavKeyName+" session Key : "+data.getSessionData(wavKeyName)+" :: audiospanishtext :  for "+wavKeyName+" session Key : "+data.getSessionData(ttsKeyName));
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in IVRToTextIntents setAudios method  :: "+e);
			caa.printStackTrace(e);
		}
	}
	
}
