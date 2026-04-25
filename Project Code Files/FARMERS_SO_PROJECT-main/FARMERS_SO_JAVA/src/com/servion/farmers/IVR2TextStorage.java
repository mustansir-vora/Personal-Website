package com.servion.farmers;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class IVR2TextStorage {

	private static ConcurrentHashMap<String, IVR2TextWeightage> ivrTextStorage = null;
	
	private static IVR2TextStorage objIvr2TextStorage = null;
	
	private IVR2TextStorage() {
		ivrTextStorage = new ConcurrentHashMap<String, IVR2TextWeightage>();
		scheduleMidnightTask();
	}
	
	public static synchronized IVR2TextStorage getInstance() {
		if(objIvr2TextStorage == null) {
			objIvr2TextStorage = new IVR2TextStorage();
		}
		return objIvr2TextStorage;
	}
	
	public void putIVR2TextIntent(String ivr2TextIntent, IVR2TextWeightage objIvr2TextWeightage) {
		ivrTextStorage.put(ivr2TextIntent, objIvr2TextWeightage);
	}
	
	public IVR2TextWeightage getIVR2TextIntent(String ivr2TextIntent) {
		return ivrTextStorage.get(ivr2TextIntent);
	}
	
	public void clearHashMap() {
		ivrTextStorage.clear();
    }
	
	private void scheduleMidnightTask() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                clearHashMap();
            }
        }, getTimeUntilMidnight(), TimeUnit.DAYS.toMillis(1));
    }
	
    private long getTimeUntilMidnight() {
        // Get current time in Central Time Zone
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
        // Calculate time until midnight
        ZonedDateTime midnight = now.toLocalDate().atStartOfDay(ZoneId.of("America/Chicago")).plusDays(1);
        Duration duration = Duration.between(now, midnight);
        return duration.toMillis();
    }
    
}
