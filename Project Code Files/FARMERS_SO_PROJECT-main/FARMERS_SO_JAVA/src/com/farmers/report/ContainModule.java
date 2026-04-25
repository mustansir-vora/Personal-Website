package com.farmers.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.audium.server.logger.events.ElementExitEvent;
import com.audium.server.session.ControllerData;
import com.audium.server.session.DecisionElementData;
import com.audium.server.session.LoggerAPI;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class ContainModule {	
	
	private static final LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
	private static Logger logger = LogManager.getLogger(ContainModule.class.getName());
	HashMap<String, String> headerDetails = null;

	@SuppressWarnings("unchecked")
	public void reportingLogic(String ElementName, ElementExitEvent pLocalEvent, String strCurDTMF) {
		
		try {
			logger.info("Element Name :: " + ElementName);
			String strBU = (String) pLocalEvent.getLoggerAPI().getSessionData(Constants.S_BU);
			logger.info("Current BU :: " + strBU);
			
			int containablecount;
			int containedcount;
			int partiallyContainedcount;
			
			headerDetails = (HashMap<String, String>)  pLocalEvent.getLoggerAPI().getSessionData("S_HEADER_DETAILS");
			
			HashMap<String, String> containableMap = (HashMap<String, String>) pLocalEvent.getLoggerApplicationAPI().getApplicationData("CONTAINABLE_ELEMENT_MAP");
			logger.info("Containable MAP :: " + containableMap);
			
			HashMap<String, String> containedMap= (HashMap<String, String>) pLocalEvent.getLoggerApplicationAPI().getApplicationData("CONTAINED_ELEMENT_MAP");
			logger.info("Contained MAP :: " + containedMap);
			
			HashMap<String, String> partiallyContainedMap = (HashMap<String, String>) pLocalEvent.getLoggerApplicationAPI().getApplicationData("PARTIALLY_CONTAINED_ELEMENT_MAP");
			logger.info("PartiallyContained MAP :: " + partiallyContainedMap);
			
			if (null != containableMap && containableMap.containsKey(ElementName)) {
				logger.info("Containable hashmap return value for element :: " + containableMap.get(ElementName));
				
				if (null ==  headerDetails.get("S_ContainableCount")) {
					containablecount = 0;
				}
				else {
					containablecount = Integer.parseInt((String) headerDetails.get("S_ContainableCount"));
				}
				
				ArrayList<String> LOBList = (ArrayList<String>) pLocalEvent.getLoggerAPI().getSessionData(containableMap.get(ElementName)+"_LOB_LIST");
				logger.info("ELEMENT :: " + ElementName + " :: LOB LIST :: " + LOBList);
				
				if (null != LOBList && null != strBU && LOBList.contains(strBU)) {
					
					if (ElementName.equalsIgnoreCase("EPSL_MN_005_VALUE")) {
						logger.info("Current Element :: " + ElementName);
						if (null == headerDetails.get("EMP_SERV_STARTED")) {
							headerDetails.put("EMP_SERV_STARTED", "TRUE");
							pLocalEvent.getLoggerAPI().setLoggerScratchData("EMP_SERV_STARTED", "TRUE");
							logger.info("EMP_SERV_STARTED FLAG :: " + headerDetails.get("EMP_SERV_STARTED"));
							headerDetails.put("S_ContainableCount", String.valueOf(containablecount+1));
						}
					}
					else if (ElementName.equalsIgnoreCase("TFPM_MN_001_VALUE")) {
						logger.info("Current Element :: " + ElementName);
						if (strCurDTMF.equalsIgnoreCase("Debit Card") || strCurDTMF.equalsIgnoreCase("Credit Card") || strCurDTMF.equalsIgnoreCase("Bank Account")) {
							headerDetails.put("S_ContainableCount", String.valueOf(containablecount+1));
						}
					}
					else {
						headerDetails.put("S_ContainableCount", String.valueOf(containablecount+1));
					}
					
				/*	if (ElementName.equalsIgnoreCase("EPSL_MN_005") && Integer.parseInt((String) headerDetails.get("S_ContainableCount")) > 1) {
						headerDetails.put("S_ContainableCount", String.valueOf(containablecount - 1));
					}
					*/
					
					pLocalEvent.getLoggerAPI().setLoggerScratchData("S_HEADER_DETAILS",headerDetails);
					logger.info("S_ContainableCount post increment for "+ElementName+" is : "+headerDetails.get("S_ContainableCount"));
				}
				
			} 
			if (null != containedMap && containedMap.containsKey(ElementName)) {
				logger.info("Contained hashmap return value for element :: " + containedMap.get(ElementName));
				
				if (null ==  headerDetails.get("S_ContainedCount")) {
					containedcount = 0;
				}
				else {
					containedcount = Integer.parseInt((String) headerDetails.get("S_ContainedCount"));
				}
				
				ArrayList<String> LOBList = (ArrayList<String>) pLocalEvent.getLoggerAPI().getSessionData(containedMap.get(ElementName)+"_LOB_LIST");
				logger.info("ELEMENT :: " + ElementName + " :: LOB LIST :: " + LOBList);
				
				//CS1174093 Temporary - while the payment is disabled in CISCO IVR - Force transfer to Agent so Incrementing contained & Partially Contained here. Once Payment is accepted in CISCO IVR, this IF condition needs to be removed
				if (ElementName.equalsIgnoreCase("TFPM_MN_001_VALUE")) {
					logger.info("Current Element :: " + ElementName);
					if (null != LOBList && null != strBU && LOBList.contains(strBU) && Integer.parseInt( null != (String) headerDetails.get("S_ContainableCount") ? (String) headerDetails.get("S_ContainableCount") : "0") > 0 && containedcount < Integer.parseInt( null != (String) headerDetails.get("S_ContainableCount") ? (String) headerDetails.get("S_ContainableCount") : "0") && (strCurDTMF.equalsIgnoreCase("Debit Card") || strCurDTMF.equalsIgnoreCase("Credit Card") || strCurDTMF.equalsIgnoreCase("Bank Account"))) {
						headerDetails.put("S_ContainedCount", String.valueOf(containedcount+1));
						pLocalEvent.getLoggerAPI().setLoggerScratchData("S_HEADER_DETAILS",headerDetails);
						logger.info("S_ContainedCount post increment for "+ElementName+" is : "+headerDetails.get("S_ContainedCount"));
					}
				}
				else if (null != LOBList && null != strBU && LOBList.contains(strBU) && Integer.parseInt( null != (String) headerDetails.get("S_ContainableCount") ? (String) headerDetails.get("S_ContainableCount") : "0") > 0 && containedcount < Integer.parseInt( null != (String) headerDetails.get("S_ContainableCount") ? (String) headerDetails.get("S_ContainableCount") : "0")) {
					headerDetails.put("S_ContainedCount", String.valueOf(containedcount+1));
					pLocalEvent.getLoggerAPI().setLoggerScratchData("S_HEADER_DETAILS",headerDetails);
					logger.info("S_ContainedCount post increment for "+ElementName+" is : "+headerDetails.get("S_ContainedCount"));
				}
				
			} 
			if (null != partiallyContainedMap && !partiallyContainedMap.containsKey(ElementName)) {
				logger.info("PartiallyContained hashmap does not contain current Element :: " + ElementName);
				logger.info("Element :: " + ElementName);
				logger.info("strCurDTMF :: " + strCurDTMF);
				if(Integer.parseInt(null != (String) headerDetails.get("S_ContainableCount") ? (String) headerDetails.get("S_ContainableCount") : "0") > 0 
						&& Integer.parseInt(null != (String) headerDetails.get("S_ContainedCount") ? (String) headerDetails.get("S_ContainedCount") : "0") > 0 
						&& Integer.parseInt((String) headerDetails.get("S_ContainableCount")) == Integer.parseInt((String) headerDetails.get("S_ContainedCount")) 
						&& Integer.parseInt(null != (String) headerDetails.get("S_PartiallyContainedCount") ? (String) headerDetails.get("S_PartiallyContainedCount") : "0") < (Integer.parseInt(null != (String) headerDetails.get("S_ContainedCount") ? (String) headerDetails.get("S_ContainedCount") : "0"))
						&& ((ElementName.equalsIgnoreCase("Shared_Transfer_CBIZ_PA_001") || ElementName.equalsIgnoreCase("Shared_Transfer_TFAR_PA_002"))
								|| (null != strCurDTMF && strCurDTMF.equalsIgnoreCase("representative")) 
								|| (null != strCurDTMF && strCurDTMF.equalsIgnoreCase(Constants.NOMATCH)) 
								|| (null != strCurDTMF && strCurDTMF.equalsIgnoreCase(Constants.NOINPUT)) 
								|| (null != strCurDTMF && strCurDTMF.equalsIgnoreCase(Constants.BILLING_QUESTIONS)) 
								|| (null != strCurDTMF && strCurDTMF.equalsIgnoreCase(Constants.OTHER_BILLING_QUESTIONS))
								//CS1174093 Temporary - while the payment is disabled in CISCO IVR - Force transfer to Agent so Incrementing contained & Partially Contained here. Once Payment is accepted in CISCO IVR, this IF condition needs to be removed 
								|| (ElementName.equalsIgnoreCase("TFPM_MN_001_VALUE") && null != strCurDTMF && strCurDTMF.equalsIgnoreCase(Constants.S_CREDITCARD))
								|| (ElementName.equalsIgnoreCase("TFPM_MN_001_VALUE") && null != strCurDTMF && strCurDTMF.equalsIgnoreCase(Constants.S_DEBITCARD))
								|| (ElementName.equalsIgnoreCase("TFPM_MN_001_VALUE") && null != strCurDTMF && strCurDTMF.equalsIgnoreCase("Bank Account")))) {
					
					if (null ==  headerDetails.get("S_PartiallyContainedCount")) {
						partiallyContainedcount = 0;
					}
					else {
						partiallyContainedcount = Integer.parseInt((String) headerDetails.get("S_PartiallyContainedCount"));
					}
					
					//ArrayList<String> LOBList = (ArrayList<String>) pLocalEvent.getLoggerAPI().getSessionData(partiallyContainedMap.get(ElementName)+"_LOB_LIST");
					//logger.info("ELEMENT :: " + ElementName + " :: LOB LIST :: " + LOBList);
					
					//if (null != LOBList && null != strBU && LOBList.contains(strBU)) {
						headerDetails.put("S_PartiallyContainedCount", String.valueOf(partiallyContainedcount+1));
						pLocalEvent.getLoggerAPI().setLoggerScratchData("S_HEADER_DETAILS",headerDetails);
						logger.info("S_PartiallyContained post increment for "+ElementName+" is : "+headerDetails.get("S_PartiallyContainedCount"));
					//}
				}
			}
		} catch (Exception e) {
			logger.info(ElementName, "Exception in SetAudioPathWithSP :: " + e);
		}
	}
}
