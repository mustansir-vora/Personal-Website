package com.farmers.CallerVerification;
import org.json.simple.JSONObject;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CV_Lang_Check extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		
		String strExitState = "N";
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		String FinalLang=(String) data.getSessionData("S_FINAL_LANG");
		try {
			
			if(FinalLang.equalsIgnoreCase("English")){
				
				//strExitState="Y";
				
			data.setSessionData(Constants.S_CV_GDF_CONNECTOR_FLAG, Constants.YES);
				
			data.addToLog("Languag Check completed set to", FinalLang);
			data.addToLog("CV_GDF_Connector_Falg Value :", (String) data.getSessionData(Constants.S_CV_GDF_CONNECTOR_FLAG));
				
			//strExitState=SettingJsonforGDF(caa, data);
			strExitState=SettingStringinputforGDF(currElementName, data);
			
			
			}
			else {
				
				strExitState = "N";
			}
			
			
			
			
		} 
			catch (Exception e) {
				
				data.addToLog(currElementName,"Exception in CIDA_MN_001_VALUE :: "+e);
				caa.printStackTrace(e);
			}
			
		
		
		
		return strExitState;
	}
	
	
	private String SettingStringinputforGDF(String currElementName, DecisionElementData data){
		
		String strExitState = Constants.NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		 String jsonString = Constants.EmptyString;
		 String jsonStringGDF=Constants.EmptyString;
		 String strRespBody="200";
		
		
		try {
			
			
		
			if((((String) data.getSessionData(Constants.S_KYC_AUTHENTICATED)).equalsIgnoreCase("YES")) && (((String) data.getSessionData(Constants.S_KYC_AUTHENTICATED_CCAI)).equalsIgnoreCase("NO")) ) {
				
				data.setSessionData(Constants.S_IVR_AUTHENTICATED_CCAI, Constants.STRING_YES);
			}
			
			else {
				
				data.setSessionData(Constants.S_IVR_AUTHENTICATED_CCAI, Constants.STRING_NO);
				
			}
			
			if((((String) data.getSessionData(Constants.S_KYC_AUTHENTICATED_CCAI)).equalsIgnoreCase("YES")) || (((String) data.getSessionData(Constants.S_IVR_AUTHENTICATED_CCAI)).equalsIgnoreCase("YES")) ) {
				
				 
				String CiscoGUID= ((String) data.getSessionData(Constants.S_CALLID) != null && !((String) data.getSessionData(Constants.S_CALLID)).isEmpty()) ? ((String) data.getSessionData(Constants.S_CALLID)) : "NA";
				 String AOR = ((String) data.getSessionData(Constants.S_PRODUCER_CODE) != null && !((String) data.getSessionData(Constants.S_PRODUCER_CODE)).isEmpty()) ? ((String) data.getSessionData(Constants.S_PRODUCER_CODE)) : "NA";
				 String DOB = ((String) data.getSessionData(Constants.S_API_DOB) != null && !((String) data.getSessionData(Constants.S_API_DOB)).isEmpty()) ? ((String) data.getSessionData(Constants.S_API_DOB)) : "NA";
				 String PolicyNo = ((String) data.getSessionData(Constants.S_POLICY_NUM) != null && !((String) data.getSessionData(Constants.S_POLICY_NUM)).isEmpty()) ? ((String) data.getSessionData(Constants.S_POLICY_NUM)) : "NA";
				 String ZipCode = ((String) data.getSessionData(Constants.S_PAYOR_ZIP_CODE) != null && !((String) data.getSessionData(Constants.S_PAYOR_ZIP_CODE)).isEmpty()) ? ((String) data.getSessionData(Constants.S_PAYOR_ZIP_CODE)) : "NA";
				 String CustomerANI = ((String) data.getSessionData(Constants.S_OANI) != null && !((String) data.getSessionData(Constants.S_OANI)).isEmpty()) ? ((String) data.getSessionData(Constants.S_OANI)) : "NA";
				 String PromptedANI = ((String) data.getSessionData(Constants.S_TELEPHONENUMBER) != null && !((String) data.getSessionData(Constants.S_TELEPHONENUMBER)).isEmpty()) ? ((String) data.getSessionData(Constants.S_TELEPHONENUMBER)) : "NA";
				
				 String KYC_Authenticated = ((String) data.getSessionData(Constants.S_KYC_AUTHENTICATED_CCAI) != null && !((String) data.getSessionData(Constants.S_KYC_AUTHENTICATED_CCAI)).isEmpty()) ? ((String) data.getSessionData(Constants.S_KYC_AUTHENTICATED_CCAI)) : "NO";
				 String IVR_Authenticated = ((String) data.getSessionData(Constants.S_IVR_AUTHENTICATED_CCAI) != null && !((String) data.getSessionData(Constants.S_IVR_AUTHENTICATED_CCAI)).isEmpty()) ? ((String) data.getSessionData(Constants.S_IVR_AUTHENTICATED_CCAI)) : "NO";
			
				 String Names = ((String) data.getSessionData(Constants.S_CCAI_NAME) != null && !((String) data.getSessionData(Constants.S_CCAI_NAME)).isEmpty()) ? ((String) data.getSessionData(Constants.S_CCAI_NAME)) : "NA";
				String State = ((String) data.getSessionData(Constants.S_CCAI_STATE) != null && !((String) data.getSessionData(Constants.S_CCAI_STATE)).isEmpty()) ? ((String) data.getSessionData(Constants.S_CCAI_STATE)) : "NA";
				 String AgencyName = ((String) data.getSessionData(Constants.S_AGENCY_NAME) != null && !((String) data.getSessionData(Constants.S_AGENCY_NAME)).isEmpty()) ? ((String) data.getSessionData(Constants.S_AGENCY_NAME)) : "NA";
				 String CallerType = ((String) data.getSessionData(Constants.S_CALLLER_TYPE) != null && !((String) data.getSessionData(Constants.S_CALLLER_TYPE)).isEmpty()) ? ((String) data.getSessionData(Constants.S_CALLLER_TYPE)) : "NA";
				
				 String AgentType = ((String) data.getSessionData(Constants.S_AgentType_CCAI) != null && !((String) data.getSessionData(Constants.S_AgentType_CCAI)).isEmpty()) ? ((String) data.getSessionData(Constants.S_AgentType_CCAI)) : "NA";
				 
				 String BU = ((String) data.getSessionData(Constants.S_BU) != null && !((String) data.getSessionData(Constants.S_BU)).isEmpty()) ? ((String) data.getSessionData(Constants.S_BU)) : "NA";

			    jsonString = "{\"CiscoGUID\"\\:\"" + CiscoGUID + 
			    		"\"\\,\"AOR\"\\:\"" + AOR + 
			    		"\"\\,\"DOB\"\\:\"" + DOB + 
			    		"\"\\,\"PolicyNo\"\\:\"" + PolicyNo + 
			    		"\"\\,\"ZipCode\"\\:\"" + ZipCode + 
			    		"\"\\,\"CustomerANI\"\\:\"" + CustomerANI + 
			    		"\"\\,\"PromptedANI\"\\:\"" + PromptedANI + 
			    		"\"\\,\"KYC_Authenticated\"\\:\"" + KYC_Authenticated + 
			    		"\"\\,\"IVR_Authenticated\"\\:\"" + IVR_Authenticated + 
			    		"\"\\,\"Names\"\\:\"" + Names + 
			    		"\"\\,\"State\"\\:\"" + State + 
			    		"\"\\,\"AgencyName\"\\:\"" + AgencyName + 
			    		"\"\\,\"CallerType\"\\:\"" + CallerType + 
			    		"\"\\,\"AgentType\"\\:\"" + AgentType + 
			    		"\"\\,\"BU\"\\:\"" 
			    		+ BU + "\"}";
	 
		            data.addToLog("InputStringToGoogle", jsonString);
		            data.addToLog("inside YES authentication", "YES");
				    data.setSessionData(Constants.VXMLParam1, jsonString);
				   
				    JSONObject json = new JSONObject();

				    

				
				 json.put("CiscoGUID", CiscoGUID);
				 json.put("AOR", AOR);
				 json.put("DOB", DOB != null && !DOB.isEmpty() ? DOB : "NA"); 
				 json.put("PolicyNo", PolicyNo != null && !PolicyNo.isEmpty() ? PolicyNo : "NA"); 
				 json.put("ZipCode", ZipCode != null && !ZipCode.isEmpty() ? ZipCode : "NA");
				 json.put("CustomerANI", CustomerANI);
				 json.put("PromptedANI", PromptedANI);
				 json.put("KYC_Authenticated", KYC_Authenticated);
				 json.put("IVR_Authenticated", IVR_Authenticated);

				
				 json.put("Names", Names); 

				
				 json.put("State", State != null && !State.isEmpty() ? State : "NA");
				 json.put("AgencyName", AgencyName != null && !AgencyName.isEmpty() ? AgencyName : "NA");
				 json.put("CallerType", CallerType);
				 json.put("AgentType", AgentType);
				 json.put("BU", BU);

			
				  jsonStringGDF = json.toJSONString();
		 
				 data.addToLog("jsonStringGDF_DB inside the if loop", jsonStringGDF);
		     
				
				strExitState = "Y";
				
			}
			
			else{
				
				
				String CiscoGUID= ((String) data.getSessionData(Constants.S_CALLID) != null && !((String) data.getSessionData(Constants.S_CALLID)).isEmpty()) ? ((String) data.getSessionData(Constants.S_CALLID)) : "NA";
			
				 String AOR = ((String) data.getSessionData(Constants.S_PRODUCER_CODE) != null && !((String) data.getSessionData(Constants.S_PRODUCER_CODE)).isEmpty()) ? ((String) data.getSessionData(Constants.S_PRODUCER_CODE)) : "NA";
			 String DOB =  "NA";
			 String PolicyNo =  "NA";
			 String ZipCode = "NA";
			 String CustomerANI = ((String) data.getSessionData(Constants.S_OANI) != null && !((String) data.getSessionData(Constants.S_OANI)).isEmpty()) ? ((String) data.getSessionData(Constants.S_OANI)) : "NA";
			 String PromptedANI = ((String) data.getSessionData(Constants.S_TELEPHONENUMBER) != null && !((String) data.getSessionData(Constants.S_TELEPHONENUMBER)).isEmpty()) ? ((String) data.getSessionData(Constants.S_TELEPHONENUMBER)) : "NA";
			
			 String KYC_Authenticated = ((String) data.getSessionData(Constants.S_KYC_AUTHENTICATED_CCAI) != null && !((String) data.getSessionData(Constants.S_KYC_AUTHENTICATED_CCAI)).isEmpty()) ? ((String) data.getSessionData(Constants.S_KYC_AUTHENTICATED_CCAI)) : "NO";
			 String IVR_Authenticated = ((String) data.getSessionData(Constants.S_IVR_AUTHENTICATED_CCAI) != null && !((String) data.getSessionData(Constants.S_IVR_AUTHENTICATED_CCAI)).isEmpty()) ? ((String) data.getSessionData(Constants.S_IVR_AUTHENTICATED_CCAI)) : "NO";
		
			 String Names =  "NA";
			String State =  "NA";
			 String AgencyName =  "NA";
			 String CallerType = ((String) data.getSessionData(Constants.S_CALLLER_TYPE) != null && !((String) data.getSessionData(Constants.S_CALLLER_TYPE)).isEmpty()) ? ((String) data.getSessionData(Constants.S_CALLLER_TYPE)) : "NA";
			
			 String AgentType =  "NA";
			 
			 String BU = ((String) data.getSessionData(Constants.S_BU) != null && !((String) data.getSessionData(Constants.S_BU)).isEmpty()) ? ((String) data.getSessionData(Constants.S_BU)) : "NA";

		    jsonString = "{\"CiscoGUID\"\\:\"" + CiscoGUID + 
		    		"\"\\,\"AOR\"\\:\"" + AOR + 
		    		"\"\\,\"DOB\"\\:\"" + DOB + 
		    		"\"\\,\"PolicyNo\"\\:\"" + PolicyNo + 
		    		"\"\\,\"ZipCode\"\\:\"" + ZipCode + 
		    		"\"\\,\"CustomerANI\"\\:\"" + CustomerANI + 
		    		"\"\\,\"PromptedANI\"\\:\"" + PromptedANI + 
		    		"\"\\,\"KYC_Authenticated\"\\:\"" + KYC_Authenticated + 
		    		"\"\\,\"IVR_Authenticated\"\\:\"" + IVR_Authenticated + 
		    		"\"\\,\"Names\"\\:\"" + Names + 
		    		"\"\\,\"State\"\\:\"" + State + 
		    		"\"\\,\"AgencyName\"\\:\"" + AgencyName + 
		    		"\"\\,\"CallerType\"\\:\"" + CallerType + 
		    		"\"\\,\"AgentType\"\\:\"" + AgentType + 
		    		"\"\\,\"BU\"\\:\"" 
		    		+ BU + "\"}";
 
	            data.addToLog("InputStringToGoogle inside the else loop", jsonString);
			    data.setSessionData(Constants.VXMLParam1, jsonString);
			   
			    JSONObject json = new JSONObject();

			    

			
			 json.put("CiscoGUID", CiscoGUID);
			 json.put("AOR", AOR);
			 json.put("DOB", DOB != null && !DOB.isEmpty() ? DOB : "NA"); 
			 json.put("PolicyNo", PolicyNo != null && !PolicyNo.isEmpty() ? PolicyNo : "NA"); 
			 json.put("ZipCode", ZipCode != null && !ZipCode.isEmpty() ? ZipCode : "NA");
			 json.put("CustomerANI", CustomerANI);
			 json.put("PromptedANI", PromptedANI);
			 json.put("KYC_Authenticated", KYC_Authenticated);
			 json.put("IVR_Authenticated", IVR_Authenticated);

			
			 json.put("Names", Names); 

			
			 json.put("State", State != null && !State.isEmpty() ? State : "NA");
			 json.put("AgencyName", AgencyName != null && !AgencyName.isEmpty() ? AgencyName : "NA");
			 json.put("CallerType", CallerType);
			 json.put("AgentType", AgentType);
			 json.put("BU", BU);

		
			  jsonStringGDF = json.toJSONString();
	 
			 data.addToLog("jsonStringGDF_DB", jsonStringGDF);
	     
			
			strExitState = "Y";
			} 
		} catch (Exception e) {
			data.addToLog("Exception in FWS_SRMCTI_API call  :: "+e, strExitState);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"CCAI_INPUT_GDF","", jsonStringGDF, "");
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.NO) ? Constants.NO : Constants.YES ,"200","");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for FWS_SRMCTI_API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		
		
		
		
		return strExitState;
	}
	
	private void appendKeyValue(StringBuilder sb, String key, String value) {
	    appendKeyValue(sb, key, value, "NA");
	}
	 
	private void appendKeyValue(StringBuilder sb, String key, String value, String defaultValue) {
	    if (value == null || value.isEmpty()) {
	        value = defaultValue;
	    }
	    sb.append(key).append("|").append(value).append(",");
	}
	
	private static void appendKeyValueWithEscapes(StringBuilder sb, String key, String value) {
        sb.append("\"").append(key).append("\\\":\\\"").append(value).append("\\\"");
    }
	
	
	

}
