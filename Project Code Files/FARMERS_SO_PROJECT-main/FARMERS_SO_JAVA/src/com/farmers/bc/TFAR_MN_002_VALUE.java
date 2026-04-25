package com.farmers.bc;

	
	
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
	public class TFAR_MN_002_VALUE extends DecisionElementBase{

		
		//Added a new menu for California and other state Policies
		public String doDecision(String currElementName, DecisionElementData data) {
			String strExitState=Constants.ER;
			CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
			try{
				
				
				//String strReturnValue = (String) data.getElementData("TFAR_MN_002","Return_Value");
				//data.addToLog(currElementName, " TFAR_MN_002 : Return_Value :: "+strReturnValue);
				
				String strReturnValue = (String) data.getElementData("TFAR_MN_002_Call", "Return_Value");
				data.addToLog(currElementName, " TFAR_MN_002_VALUE : Return_Value :: " + strReturnValue);
				data.setSessionData("TFAR_MN_002_VALUE", strReturnValue);
				
				
				if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					data.addToLog(currElementName, " TFAR_MN_002_VALUE : Return_Value FOR No Input :: " + strReturnValue);
				} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					data.addToLog(currElementName, " TFAR_MN_002_VALUE : Return_Value  For No Match:: " + strReturnValue);
				} 
				
				//Created a new Value for MAX NO_INPUT,MAX NO_MATCH,MAXCOMBINEDTRIES
				
				 else if(strReturnValue.equalsIgnoreCase(Constants.MAX_NOINPUT))
				 {
						strExitState = Constants.MAX_NOINPUT;
						data.addToLog(currElementName, " TFAR_MN_002_VALUE : Return_Value  For max_noinput:: " + strReturnValue);
				 }
				
				 else if(strReturnValue.equalsIgnoreCase(Constants.MAX_NOMATCH)) {
						strExitState = Constants.MAX_NOMATCH;
						data.addToLog(currElementName, " TFAR_MN_002_VALUE : Return_Value  For max_nomatch:: " + strReturnValue);
				 }
				 else if(strReturnValue.equalsIgnoreCase(Constants.MAXCOMBINEDTRIES)) {
						strExitState = Constants.MAXCOMBINEDTRIES;
						data.addToLog(currElementName, " TFAR_MN_002_VALUE : Return_Value  For maxcombinedtries:: " + strReturnValue);
				 }
				
				
				else if(strReturnValue.equalsIgnoreCase(Constants.FARMERS_OTHERSTATE)) 
				{
					strExitState =Constants.FARMERS_OTHERSTATE;
					caa.createMSPKey(caa, data, "TFAR_MN_002", "OTHERSTATE");
					//caa.createMSPKey(caa, data, "TFAR_MN_002_Value", "OTHERSTATE");
					data.setSessionData("If you are not California policy holder", strExitState);
					data.addToLog(currElementName, " TFAR_MN_002_VALUE : Return_Value for Other State :: " + strReturnValue);
				} 
				else if(strReturnValue.equalsIgnoreCase(Constants.FARMERS_CASTATE))
				{
					strExitState =Constants.FARMERS_CASTATE;
					caa.createMSPKey(caa, data, "TFAR_MN_002", "CASTATE");
					//caa.createMSPKey(caa, data, "TFAR_MN_002_Value", "CASTATE");
					data.setSessionData("If you have California policy holder", strExitState);
					data.addToLog(currElementName, " TFAR_MN_002_VALUE : Return_Value  For CA State:: " + strReturnValue);
				} 

				else
				{
					strExitState=Constants.ER;
					data.addToLog(currElementName, " TFAR_MN_002_VALUE : Return_Value :: " + strReturnValue);
						
				}
				
			}
			catch (Exception e) {
				data.addToLog(currElementName,"Exception in TFAR_MN_002 :: "+e);
				caa.printStackTrace(e);
			}
			data.addToLog(currElementName,"TFAR_MN_002 :: "+strExitState);

//			String menuExitState = strExitState;
//			if(strExitState.contains(" ")) menuExitState = menuExitState.replaceAll(" ", "_");
//			if(null != (String)data.getSessionData("FNAC_MN_001_"+menuExitState) && !((String)data.getSessionData("FNAC_MN_001_"+menuExitState)).isEmpty()) menuExitState = (String)data.getSessionData("FNAC_MN_001_"+menuExitState);
//			data.addToLog(currElementName, "Final Value of Menu Exit State for FNAC_MN_001: "+menuExitState);
//			data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FNAC_MN_001:"+menuExitState);
			return strExitState;

			}
			
	}