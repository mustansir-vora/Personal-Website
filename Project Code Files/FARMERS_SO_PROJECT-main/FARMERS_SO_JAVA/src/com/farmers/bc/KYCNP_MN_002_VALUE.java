package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCNP_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		int rvprodtypecount = 0;
		int autoprodtypecount = 0;
		int homeprodtypecount = 0;
		int umbrellaprodtypecount = 0;
		int boatprodtypecount = 0;
		int spprodtypecount = 0;
		String prompt = null;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("KYCNP_MN_002_Call","Return_Value");
			data.addToLog(currElementName, "KYCNP_MN_002_Call Before : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.KYCNP_MN_002_VALUE, strReturnValue);
			data.setSessionData(Constants.PREVIOUS_MENU_BRANDCHECK, Constants.KYCNP_MN_002_MENUNAME);
			
			if (strReturnValue.toLowerCase().contains("both") || strReturnValue.toLowerCase().contains("all of these")) {
				strExitState = "AllOfThese";
			}
			
			if (strReturnValue.toLowerCase().contains("none of these")) {
				strExitState = "NoneOfTheseSelected";
			}
			
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			}   
			
			if (strReturnValue.toLowerCase().contains("motor home") || strReturnValue.toLowerCase().contains("motorcycle") || strReturnValue.toLowerCase().contains("auto") || strReturnValue.toLowerCase().contains("home") || strReturnValue.toLowerCase().contains("umbrella") || strReturnValue.toLowerCase().contains("boat") || strReturnValue.toLowerCase().contains("specialty dwelling") || strReturnValue.toLowerCase().contains("mobile home")) {
				
				if (strReturnValue.toLowerCase().contains("motor home") || strReturnValue.toLowerCase().contains("motorcycle") || strReturnValue.toLowerCase().contains("motor home or motorcycle")) {
					rvprodtypecount =  (int) data.getSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC);
					
					
					if (rvprodtypecount >= 2) {
						data.addToLog(currElementName, "More Than One Motor Home or MotorCycle Product Type Count : KYCNP_MN_002 : "+rvprodtypecount);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_YES);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "RV");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "RV");
						data.setSessionData("POLICY_TYPE", strReturnValue);
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : YES - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
						prompt = "Motor Home or Motorcycle";
						prompt = prompt.replaceAll(" " , ".");
						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
					}
					else {
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_NO);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "RV");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "RV");
						data.addToLog(currElementName, "One Zero Motor Home or MotorCycle Product Type Count : KYCNP_MN_002 : "+rvprodtypecount);
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : NO - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
					}
				}
				
				if (strReturnValue.toLowerCase().contains("auto")) {
					autoprodtypecount =  (int) data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC);
					
					
					if (autoprodtypecount >= 2) {
						data.addToLog(currElementName, "More Than One Auto Product Type Count : KYCNP_MN_002 : "+autoprodtypecount);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_YES);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "AUTO");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "AUTO");
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : YES - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
						prompt = "Auto";
						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
					}
					else {
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_NO);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "AUTO");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "AUTO");
						data.addToLog(currElementName, "One Auto Product Type Count : KYCNP_MN_002 : "+autoprodtypecount);
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : NO - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
					}
				}
				
				if (strReturnValue.toLowerCase().contains("home") && !strReturnValue.toLowerCase().contains("motor") && !strReturnValue.toLowerCase().contains("mobile")) {
					homeprodtypecount =  (int) data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC);
					
					
					if (homeprodtypecount >= 2) {
						data.addToLog(currElementName, "More Than One Home Product Type Count : KYCNP_MN_002 : "+homeprodtypecount);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_YES);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "HOME");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "HOME");
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : YES - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
						prompt = "Home";
						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
					}
					else {
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_NO);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "HOME");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "HOME");
						data.addToLog(currElementName, "One Home Product Type Count : KYCNP_MN_002 : "+homeprodtypecount);
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : NO - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
					}
				}
				
				if (strReturnValue.toLowerCase().contains("umbrella")) {
					umbrellaprodtypecount =  (int) data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC);
					
					
					if (umbrellaprodtypecount >= 2) {
						data.addToLog(currElementName, "More Than One Umbrella Product Type Count : KYCNP_MN_002 : "+umbrellaprodtypecount);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_YES);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "UMBRELLA");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "UMBRELLA");
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : YES - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
						prompt = "Umbrella";
						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
					}
					else {
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_NO);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "UMBRELLA");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "UMBRELLA");
						data.addToLog(currElementName, "One Umbrella Product Type Count : KYCNP_MN_002 : "+umbrellaprodtypecount);
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : NO - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
					}
				}
				
				if (strReturnValue.toLowerCase().contains("boat")) {
					boatprodtypecount =  (int) data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC);
					
					
					if (boatprodtypecount >= 2) {
						data.addToLog(currElementName, "More Than One Boat Product Type Count : KYCNP_MN_002 : "+boatprodtypecount);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_YES);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "MR");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "MR");
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : YES - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
						prompt = "Boat";
						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
					}
					else {
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_NO);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "MR");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "MR");
						data.addToLog(currElementName, "One Boat Product Type Count : KYCNP_MN_002 : "+boatprodtypecount);
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : YES - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
					}
				}
				
				if (strReturnValue.toLowerCase().contains("specialty dwelling") || strReturnValue.toLowerCase().contains("mobile home") || strReturnValue.toLowerCase().contains("specialty dwelling or mobile home")) {
					spprodtypecount =  (int) data.getSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC);
					
					
					if (spprodtypecount >= 2) {
						data.addToLog(currElementName, "More Than One Specialty Dwelling or Mobile Home Product Type Count : KYCNP_MN_002 : "+spprodtypecount);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_YES);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "SP");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "SP");
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : YES - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
						prompt = "Specialty Dwelling or Mobile Home";
						prompt = prompt.replaceAll(" " , ".");
						data.setSessionData("POLICY_TYPE", strReturnValue);
						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
					}
					else {
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_NO);
						data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "SP");
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, "SP");
						data.addToLog(currElementName, "One Specialty Dwelling or Mobile Home Product Type Count : KYCNP_MN_002 : "+spprodtypecount);
						data.addToLog(currElementName, "IS_MORE_THN_ONE_POLICY_PRODUCTTYPE : NO - Product Type - "+data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));
					}
				}
				
				strExitState = "PolicyTypeSelected";
			}
		//strExitState = strReturnValue;
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCNP_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"KYCNP_MN_002_VALUE :: "+strExitState);
		return strExitState;
	}
}