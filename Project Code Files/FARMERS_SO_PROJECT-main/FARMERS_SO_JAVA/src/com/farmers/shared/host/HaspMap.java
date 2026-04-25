package com.farmers.shared.host;

import java.util.HashMap;

import com.audium.server.AudiumException;
import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.audium.server.voiceElement.ElementInterface;

public class HaspMap extends ActionElementBase implements ElementInterface
{

	public String getElementName() 
	{

		return "HaspMap";
	}

	public String getDisplayFolderName() {

		return "HaspMap";
	}

	public String getDescription() {

		return "Retrieve the HaspMap Lookup webservice response";

	}

	@Override
	public void doAction(String arg0, ActionElementData data) throws AudiumException
	{
		HashMap<String, String> haspmap = new HashMap<>();
		haspmap.put("103", "Specialty dwelling or mobile home");
		haspmap.put("105", "Specialty dwelling or mobile home");
		haspmap.put("106", "Specialty dwelling or mobile home");
		haspmap.put("107", "Specialty dwelling or mobile home");
		haspmap.put("255", "motor home or motorcycle");
		haspmap.put("276", "motor home or motorcycle");
		haspmap.put("381", "Dwelling Fire Specialty Homeowners");
		haspmap.put("444", "Specialty dwelling or mobile home");
		haspmap.put("601", "Boat");
		haspmap.put("603", "Boat");
		haspmap.put("A", "Auto");
		haspmap.put("H", "Home");
		haspmap.put("U", "Umbrella");
		haspmap.put("Y", "Yacht");
		haspmap.put("F", "Fire");
      String ProductName_1 = "";
      String ProductName_2 = "";
      String ProductName_3 = "";
      
		if(data.getSessionData("S_API_POLICYCODE_1")!=null)
		{
			String PolicyCode1 = (String) data.getSessionData("S_API_POLICYCODE_1"); //SingleFoundDoubleFoundTribleFound
			
			 ProductName_1 = haspmap.get(PolicyCode1);
			data.setSessionData("ProductName_1", ProductName_1);
			data.addToLog("PolicyCode1", PolicyCode1);
			data.addToLog("ProductName", ProductName_1);
			
		}
	
		 if(data.getSessionData("S_API_POLICYCODE_2")!=null)
		{
			 	String PolicyCode2 = (String) data.getSessionData("S_API_POLICYCODE_2");
				 ProductName_2 = haspmap.get(PolicyCode2);
				data.setSessionData("ProductName_1", ProductName_2);
				data.addToLog("PolicyCode2", PolicyCode2);
				data.addToLog("ProductName", ProductName_2);
		}
		 if(data.getSessionData("S_API_POLICYCODE_3")!=null)
		{
			 		String PolicyCode3 = (String) data.getSessionData("S_API_POLICYCODE_3");
					 ProductName_3 = haspmap.get(PolicyCode3);
					data.setSessionData("ProductName_3", ProductName_3);
					data.addToLog("PolicyCode3", PolicyCode3);
					data.addToLog("ProductName", ProductName_3);
		}		
		
		 
		 	String FullProductName = ProductName_1+ProductName_2+ProductName_3;
		 	data.setSessionData("FullProductName", FullProductName);
			data.addToLog("FullProductName", FullProductName);
		
		
	}
	
	
	




}
