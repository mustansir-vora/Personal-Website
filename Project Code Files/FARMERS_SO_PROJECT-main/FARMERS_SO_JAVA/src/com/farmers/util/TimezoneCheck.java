package com.farmers.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.regex.Pattern;

import com.audium.server.session.DecisionElementData;

public class TimezoneCheck {
	
	
	
	public static Date getCurrentDateInCST() {
		Instant currTimeUTC = Instant.now();
		System.out.println(currTimeUTC);
		ZonedDateTime centralTime = currTimeUTC.atZone(ZoneId.of("America/New_York"));
		Date dateObj = Date.from(centralTime.toInstant());
		return dateObj;
	}
	
	
	public static boolean checkwithinTime(String timeWindow, String timeZone,DecisionElementData data) {
		boolean retValue = false;
		
		data.addToLog(data.getCurrentElement(),"time window check initiated");
		
		String[] timewindowArr = (Pattern.compile("\\|").split(timeWindow));
		for(String timeWindowObj : timewindowArr) {
			timeWindowObj = timeWindowObj.replace(" - ", "-");
			LocalTime startTime = LocalTime.of(Integer.parseInt(timeWindowObj.split("\\-")[0].split("\\:")[0]), Integer.parseInt(timeWindowObj.split("\\-")[0].split("\\:")[1]));
			LocalTime endTime = LocalTime.of(Integer.parseInt(timeWindowObj.split("\\-")[1].split("\\:")[0]), Integer.parseInt(timeWindowObj.split("\\-")[1].split("\\:")[1]));
			
			ZoneId objZoneId = null;
			if(timeZone.equals("CT")) {
				objZoneId = ZoneId.of("America/Chicago");
			} else if (timeZone.equals("ET")){
				objZoneId = ZoneId.of("America/New_York");
			}
			ZonedDateTime objZonedDateTime = ZonedDateTime.now(objZoneId);
			LocalTime timeNow = objZonedDateTime.toLocalTime();
			data.addToLog(data.getCurrentElement(),"Time Window passed : " + timeWindow);
			data.addToLog(data.getCurrentElement(),"Current Time : " + timeNow);
			
			retValue = (
					(timeNow.isAfter(startTime) || timeNow.equals(startTime)) && 
					(timeNow.isBefore(endTime) || timeNow.equals(endTime)));
			if(retValue) break;
		}
		return retValue;
	
	}
	
	public static void main(String[] args) {
		String timeWindow = "03:00-12:00|12:00-23:00";
		boolean retValue = false;
		String[] timewindowArr = (Pattern.compile("\\|").split(timeWindow));
		for(String timeWindowObj : timewindowArr) {
			LocalTime startTime = LocalTime.of(Integer.parseInt(timeWindowObj.split("\\-")[0].split("\\:")[0]), Integer.parseInt(timeWindowObj.split("\\-")[0].split("\\:")[1]));
			LocalTime endTime = LocalTime.of(Integer.parseInt(timeWindowObj.split("\\-")[1].split("\\:")[0]), Integer.parseInt(timeWindowObj.split("\\-")[1].split("\\:")[1]));
			String timeZone = "CT";
			ZoneId objZoneId = null;
			if(timeZone.equals("CT")) {
				objZoneId = ZoneId.of("America/Chicago");
			} else if (timeZone.equals("ET")){
				objZoneId = ZoneId.of("America/New_York");
			}
			ZonedDateTime objZonedDateTime = ZonedDateTime.now(objZoneId);
			LocalTime timeNow = objZonedDateTime.toLocalTime();
			System.out.println("Time Window passed : " + timeWindow);
			System.out.println("Current Time : " + timeNow);
			
			retValue = (
					(timeNow.isAfter(startTime) || timeNow.equals(startTime)) && 
					(timeNow.isBefore(endTime) || timeNow.equals(endTime)));
					
			System.out.println(retValue);
			if(retValue) break;
		}
	
		
	}
	
}
