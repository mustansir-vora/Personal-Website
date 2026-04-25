package com.farmers.nlu;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckEntryPoint_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strEntryPoint = (String) data.getSessionData("INVOKED_FROM");
			data.addToLog(currElementName, "SubFlow Entry : Invoked From : "+strEntryPoint);
			if(strEntryPoint != null && !strEntryPoint.equals("") && !strEntryPoint.equals("NA")) {
				
				if("NLU".equalsIgnoreCase(strEntryPoint)) {
					strExitState = "NLU";
					data.setSessionData("FM_INVOKED_FROM", "NLU");
					data.setSessionData("INVOKED_FROM", "NA");// reset to NA 
					data.addToLog(currElementName, "From NLU Module");
				}else {
					strExitState = "CONT";
					data.setSessionData("INVOKED_FROM", "NA");
					data.addToLog(currElementName, "Continue with initial point - no intent collected");
				}
			}else {
				strExitState = "INITIAL";
				data.addToLog(currElementName, "Continue with initial point");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FWS_Req_Coverage_Doc Entry :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FWS_Req_Coverage_Doc Entry  :: "+strExitState);
		return strExitState;
	}


	public static void main(String[] args) {
		String page = "Shared Main Menu";
		String lob = "Inside Sales";
		Properties flowKeyProp = new Properties();
	
		FileReader reader;
		try {
			reader = new FileReader("D:\\Farmer\\FLOW_LOB_KEY.properties");
			flowKeyProp.load(reader);
			
			for (String string : flowKeyProp.stringPropertyNames()) {
				System.out.println(string);
			}
			page=page.replaceAll("\\s", "");
			System.out.println(page);
			lob =lob.replaceAll("\\s", "");
			System.out.println(page+"_"+lob);
			System.out.println(flowKeyProp.getProperty(page+"_"+lob));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
}


