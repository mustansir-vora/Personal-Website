package com.farmers.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;

public class HotEventTransferHoursCheck {
	
	public void isWithInBusinessHours(DecisionElementData data) throws AudiumException {
		LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Chicago"));
		DayOfWeek dayOfWeek = now.getDayOfWeek();
		int hour = now.getHour();
		data.addToLog(data.getCurrentElement(), "HT Txr Check Current day : " + dayOfWeek.toString());
		data.addToLog(data.getCurrentElement(), "HT Txr Check Current hour : " + hour);
		
		//check for weekday
		if(!(dayOfWeek.equals(DayOfWeek.SATURDAY)) && !(dayOfWeek.equals(DayOfWeek.SUNDAY))) {
			data.addToLog(data.getCurrentElement(), "HT Txr Check weekday loop");
			if(hour >= 7 && hour < 19) {
				data.setSessionData("HT_TXR_OPEN", "true");
				data.addToLog(data.getCurrentElement(), "HT Txr Check Open");
			} else {
				data.setSessionData("HT_TXR_OPEN", "false");
				data.addToLog(data.getCurrentElement(), "HT Txr Check Close");
			}
		} else {
			//for weekend
			data.addToLog(data.getCurrentElement(), "HT Txr Check weekdend loop");
			if(hour >= 8 && hour < 20) {
				data.setSessionData("HT_TXR_OPEN", "true");
				data.addToLog(data.getCurrentElement(), "HT Txr Check Open");
			} else {
				data.setSessionData("HT_TXR_OPEN", "false");
				data.addToLog(data.getCurrentElement(), "HT Txr Check Close");
			}
		}
	}
}
