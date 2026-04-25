package com.farmers.bc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class TodayDate extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{
			
			ZoneId zone = ZoneId.of("America/Chicago");
			LocalDate currentDate = LocalDate.now(zone);
			String formattedDate = formatWithDaySuffix(currentDate);
		    if(formattedDate!=null) {
		    	formattedDate=formattedDate.replaceAll(" ",".");
		    	data.setSessionData(Constants.VXMLParam2, formattedDate);
		    	data.setSessionData(Constants.VXMLParam3, "NA");
		    	data.setSessionData(Constants.VXMLParam4, "NA");
		    	data.addToLog(currElementName, "TodayDate : VXMLParam2 : "+data.getSessionData(Constants.VXMLParam2));
		
		strExitState=Constants.STRING_YES;}
		
		
		}
			catch (Exception e) {
				data.addToLog(currElementName,"Exception in  TodayDate :: "+e);
				caa.printStackTrace(e);
			}
			data.addToLog(currElementName,"TodayDate  :: "+strExitState);
            return strExitState;
	}

	 public static String formatWithDaySuffix(LocalDate date) {
	        String day = String.valueOf(date.getDayOfMonth());
	        if (day.endsWith("1") && !day.equals("11")) {
	            day += "st";
	        } else if (day.endsWith("2") && !day.equals("12")) {
	            day += "nd";
	        } else if (day.endsWith("3") && !day.equals("13")) {
	            day += "rd";
	        } else {
	            day += "th";
	        }
	        return date.format(DateTimeFormatter.ofPattern("MMMM ")) + day;
	    }
	 
	 public static void main(String[] args) {
		 ZoneId zone = ZoneId.of("America/New_York");
		 LocalDate currentDate = LocalDate.now(zone);
	   	 System.out.println("Date :: "+currentDate);
	}
}

