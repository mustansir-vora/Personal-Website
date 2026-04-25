package com.farmers.shared.bc;
 
import java.util.ArrayList;
 
import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class CustomerRetention_BUCheck extends DecisionElementBase {
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strBU= (String) data.getSessionData(Constants.S_BU);
		data.addToLog("BU: ", strBU);
		String productType="";
		if("Y".equalsIgnoreCase((String) data.getSessionData(Constants.S_KYC_RETENTION_CHECK))&& null!= (String)data.getSessionData(Constants.FINAL_PRODUCTTYPE)) {
		data.setSessionData(Constants.S_KYC_RETENTION_CHECK, "N"); 
		productType=(String) data.getSessionData(Constants.FINAL_PRODUCTTYPE);
		//Abinaya - CS1331437 - remove auto only filter - Start
//		data.addToLog("Product Type: ", productType);
		//Abinaya - CS1331437 - remove auto only filter - END
		}
		if("Y".equalsIgnoreCase((String) data.getSessionData(Constants.S_SIDA_RETENTION_CHECK)) && null!=(String)data.getSessionData("SIDA_RETENTION_PRODUCTYPE")) {
			data.setSessionData(Constants.S_SIDA_RETENTION_CHECK, "N"); 
			productType=(String) data.getSessionData("SIDA_RETENTION_PRODUCTYPE");
			//Abinaya - CS1331437 - remove auto only filter - Start
//			data.addToLog("Product Type: ", productType);
			//Abinaya - CS1331437 - remove auto only filter - END
		}
		String policySource=(String) data.getSessionData(Constants.S_POLICY_SOURCE);
		data.addToLog("Policy Source: ", policySource);
		ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
		String strExitState="ER";
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
 
		try {
			 if (null != data.getSessionData("S_FLAG_FDS_BU") && Constants.STRING_YES.equalsIgnoreCase((String) data.getSessionData("S_FLAG_FDS_BU"))) {
				//Abinaya - CS1331437 - remove auto only filter - Start
				 if("GWPC".equalsIgnoreCase(policySource)|| "PLA".equalsIgnoreCase(policySource)) {
				  data.addToLog("Entered for FDS retention Qualifiers", "");
				 strExitState = "FDS";
				 }
//				 else if("A".equalsIgnoreCase(productType) && ("GWPC".equalsIgnoreCase(policySource)||"PLA".equalsIgnoreCase(policySource))) {
//					 data.addToLog("Entered from SIDA check for FDS retention Qualifiers", "");
//					 strExitState = "FDS";
//				 }
				 else {
							strExitState="Others";
					}
				}else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
					 if("GWPC".equalsIgnoreCase(policySource)|| "PLA".equalsIgnoreCase(policySource)) {
						 data.addToLog("Entered for FDS retention Qualifiers", "");
						 strExitState = "FDS";
						 }
//					 else if("A".equalsIgnoreCase(productType) && ("GWPC".equalsIgnoreCase(policySource)||"PLA".equalsIgnoreCase(policySource))) {
//							 data.addToLog("Entered from SIDA check for FDS retention Qualifiers", "");
//							 strExitState = "FDS";
//						 }
					 else {
									strExitState="Others";
							}
				}else {
					strExitState="Others";
				}
			//Abinaya - CS1331437 - remove auto only filter - End
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in Customer retention BU Check :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Customer retention BU :: " + strExitState);
 
		
		return strExitState;
	}
}