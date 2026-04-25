package com.farmers.shared.bc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class TFNPolicyBrandMatch extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
	    String exitState = "ER";
	    String tfnBrand = (String) data.getSessionData(Constants.S_BU);
	    String selectedBrand = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
        CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
	    if (tfnBrand != null && !tfnBrand.isEmpty()) {
	        if (selectedBrand != null && !selectedBrand.isEmpty()) {
	            if (tfnBrand.equalsIgnoreCase(selectedBrand)) {
	                exitState = Constants.STRING_YES;
	            } else {
	            	switch (selectedBrand) {
					case "BW":
						data.setSessionData(Constants.S_BU, "Foremost Auto");
						data.setSessionData(Constants.S_LOB,"Foremost Auto");
						data.setSessionData(Constants.S_CATEGORY,"FM_AUTO");
						data.setSessionData(Constants.S_FINAL_LOB,"Foremost Auto");
						data.setSessionData(Constants.S_FINAL_CATEGORY,"FM_AUTO");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
						data.addToLog(currElementName, "User has selected BW Policy :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						updateMSPBU(data, (String)data.getSessionData(Constants.S_BU), (String)data.getSessionData(Constants.S_LOB), (String) data.getSessionData(Constants.S_CATEGORY), currElementName, caa);
						break;
					case "FWS-ARS":
						data.setSessionData(Constants.S_BU, "FWS Service");
						data.setSessionData(Constants.S_LOB, "FWS Service");
						data.setSessionData(Constants.S_CATEGORY,"FWS_SVC");
						data.setSessionData(Constants.S_FINAL_LOB, "FWS Service");
						data.setSessionData(Constants.S_FINAL_CATEGORY,"FWS_SVC");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
						data.addToLog(currElementName, "User has selected FWS Policy :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						updateMSPBU(data, (String)data.getSessionData(Constants.S_BU), (String)data.getSessionData(Constants.S_LOB), (String) data.getSessionData(Constants.S_CATEGORY), currElementName, caa);
						break;
					case "FWS-A360":
						data.setSessionData(Constants.S_BU, "FWS Service");
						data.setSessionData(Constants.S_LOB, "FWS Service");
						data.setSessionData(Constants.S_CATEGORY,"FWS_SVC");
						data.setSessionData(Constants.S_FINAL_LOB, "FWS Service");
						data.setSessionData(Constants.S_FINAL_CATEGORY,"FWS_SVC");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
						data.addToLog(currElementName, "User has selected FWS Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						updateMSPBU(data, (String)data.getSessionData(Constants.S_BU), (String)data.getSessionData(Constants.S_LOB), (String) data.getSessionData(Constants.S_CATEGORY), currElementName, caa);
						break;
					case "ARS":
						data.setSessionData(Constants.S_BU, "FWS Service");
						data.setSessionData(Constants.S_LOB, "FWS Service");
						data.setSessionData(Constants.S_CATEGORY,"FWS_SVC");
						data.setSessionData(Constants.S_FINAL_LOB, "FWS Service");
						data.setSessionData(Constants.S_FINAL_CATEGORY,"FWS_SVC");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
						data.addToLog(currElementName, "User has selected FWS Policy :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						updateMSPBU(data, (String)data.getSessionData(Constants.S_BU), (String)data.getSessionData(Constants.S_LOB), (String) data.getSessionData(Constants.S_CATEGORY), currElementName, caa);
						break;
					case "A360":
						data.setSessionData(Constants.S_BU, "FWS Service");
						data.setSessionData(Constants.S_LOB, "FWS Service");
						data.setSessionData(Constants.S_CATEGORY,"FWS_SVC");
						data.setSessionData(Constants.S_FINAL_LOB, "FWS Service");
						data.setSessionData(Constants.S_FINAL_CATEGORY,"FWS_SVC");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
						data.addToLog(currElementName, "User has selected FWS Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						updateMSPBU(data, (String)data.getSessionData(Constants.S_BU), (String)data.getSessionData(Constants.S_LOB), (String) data.getSessionData(Constants.S_CATEGORY), currElementName, caa);
						break;
						
					case "PLA":
						data.setSessionData(Constants.S_BU, "Farmers");
						data.setSessionData(Constants.S_LOB, "Farmers");
						data.setSessionData(Constants.S_CATEGORY,"FarmersSRV");
						data.setSessionData(Constants.S_FINAL_LOB, "Farmers");
						data.setSessionData(Constants.S_FINAL_CATEGORY,"FarmersSRV");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
						data.addToLog(currElementName, "User has selected Farmers Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						updateMSPBU(data, (String)data.getSessionData(Constants.S_BU), (String)data.getSessionData(Constants.S_LOB), (String) data.getSessionData(Constants.S_CATEGORY), currElementName, caa);
						break;
					case "GWPC":
						data.setSessionData(Constants.S_BU, "Farmers");
						data.setSessionData(Constants.S_LOB, "Farmers");
						data.setSessionData(Constants.S_CATEGORY,"FarmersSRV");
						data.setSessionData(Constants.S_FINAL_LOB, "Farmers");
						data.setSessionData(Constants.S_FINAL_CATEGORY,"FarmersSRV");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
						data.addToLog(currElementName, "User has selected Farmers Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						updateMSPBU(data, (String)data.getSessionData(Constants.S_BU), (String)data.getSessionData(Constants.S_LOB), (String) data.getSessionData(Constants.S_CATEGORY), currElementName, caa);
						break;
					case "AUTO":
						data.setSessionData(Constants.S_BU, "Auto-Service");
						data.setSessionData(Constants.S_LOB, "Auto-Service");
						data.setSessionData(Constants.S_CATEGORY,"AutoSrv");
						data.setSessionData(Constants.S_FINAL_LOB, "Auto-Service");
						data.setSessionData(Constants.S_FINAL_CATEGORY,"AutoSrv");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
						updateMSPBU(data, (String)data.getSessionData(Constants.S_BU), (String)data.getSessionData(Constants.S_LOB), (String) data.getSessionData(Constants.S_CATEGORY), currElementName, caa);
						break;
					case "FM":
						data.setSessionData(Constants.S_BU, "Foremost");
						data.setSessionData(Constants.S_LOB, "Foremost");
						data.setSessionData(Constants.S_CATEGORY,"FM-SLS-SRV");
						data.setSessionData(Constants.S_FINAL_LOB, "Foremost");
						data.setSessionData(Constants.S_FINAL_CATEGORY,"FM-SLS-SRV");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_NO);
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
						data.addToLog(currElementName, "User has selected Foremost Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						updateMSPBU(data, (String)data.getSessionData(Constants.S_BU), (String)data.getSessionData(Constants.S_LOB), (String) data.getSessionData(Constants.S_CATEGORY), currElementName, caa);
						break;
					default:
						break;
					}
					data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
	                data.addToLog(currElementName, "Rebranded as Identified Policy ::" + selectedBrand);
	                exitState = Constants.STRING_NO;
	            }
	        } else {
	            data.addToLog(currElementName, "Selected brand from Policy is null or empty: " + selectedBrand);
	        }
	    } else {
	        data.addToLog(currElementName, "TFN Brand is null or empty: " + tfnBrand);
	    }
	    return exitState;
	}
	
	private void updateMSPBU(DecisionElementData data, String sBU, String sLob, String sCategory,String currElementName, CommonAPIAccess caa) {
		// TODO Auto-generated method stub
		try {
		String menuSelectionKey = (String) data.getSessionData(Constants.S_MENU_SELCTION_KEY);
		
		if (null != menuSelectionKey && !menuSelectionKey.isEmpty()) {
			data.addToLog(currElementName, "Old Menu selection Key: "+menuSelectionKey);
			if(menuSelectionKey.startsWith("NLU")) {
				int colonIndex = menuSelectionKey.indexOf(':');

		        String result = "";
		        if (colonIndex != -1) {
		            // Preserve the part before colon and append the new string after colon
		            result = menuSelectionKey.substring(0, colonIndex + 1) + sCategory;
		        }
		        data.setSessionData(Constants.S_MENU_SELCTION_KEY, result);
		        data.addToLog(currElementName, "Updating MSP since BU switched in VIA :: New MSP :: " +result);
			}else {
			   menuSelectionKey = menuSelectionKey.replaceFirst("^[^:]+", sBU);
			   data.setSessionData(Constants.S_MENU_SELCTION_KEY, menuSelectionKey);
			   data.addToLog(currElementName, "Updating MSP since BU switched in VIA :: New MSP :: " +menuSelectionKey);
			}
			
			
		}
		//Replacing all the previously formed MSPs (BU - FARMERS) with the new BU(FWS)
		Queue<String> MSPQueue = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
		data.addToLog(currElementName, "MSP Queue Before BU was switched :: " +MSPQueue);
		
		Queue<String> tempQueue = new LinkedList<>();
		if (null != MSPQueue) {
			Iterator<String> iterator = MSPQueue.iterator();
	        data.addToLog(currElementName, "Iterating through the current Queue while replacing old BU to new BU");
	        while (iterator.hasNext()) { 
	        	String oldMSPEntry= iterator.next();
	        	data.addToLog(currElementName, "Old MSP entry " +oldMSPEntry);
	        	 String result="";
	        	if(oldMSPEntry.startsWith("NLU")) {
					int colonIndex = oldMSPEntry.indexOf(':');
			       
			        if (colonIndex != -1) {
			            // Preserve the part before colon and append the new string after colon
			            result = oldMSPEntry.substring(0, colonIndex + 1) + sCategory;
			        }
				}else {
					result = oldMSPEntry.replaceFirst("^[^:]+", sBU);
				}
	           
	            data.addToLog(currElementName, "New MSP entry :: " + result);
	            tempQueue.add(result);
	        }
	            MSPQueue = tempQueue;
	    
				data.setSessionData("S_LAST_5_MSP_VALUES", MSPQueue);

	        data.addToLog(currElementName, "New MSP Queue post replacing the Old BU with New one :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));
		}
		}catch(Exception e) {
			data.addToLog(currElementName,"Exception in TFNPolicyBrandMatch  :: "+e);
			caa.printStackTrace(e);
		}
	}
}