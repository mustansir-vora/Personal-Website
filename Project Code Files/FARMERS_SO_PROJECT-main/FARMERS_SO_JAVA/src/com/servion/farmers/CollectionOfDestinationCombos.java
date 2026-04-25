package com.servion.farmers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.audium.server.session.DecisionElementData;

public class CollectionOfDestinationCombos {
	
	private static CollectionOfDestinationCombos objCollectionOfDestinationCombos = null;
	
	private static ConcurrentHashMap<String, List<DestinationWeightage>> objDestinationCombos = null;
	
	private CollectionOfDestinationCombos() {
		objDestinationCombos = new ConcurrentHashMap<String, List<DestinationWeightage>>();
		//scheduleMidnightTask();
	}
	
	public static synchronized CollectionOfDestinationCombos getInstance() {
		if(objCollectionOfDestinationCombos == null) {
			objCollectionOfDestinationCombos = new CollectionOfDestinationCombos();
		}
		return objCollectionOfDestinationCombos;
	}
	
	public List<DestinationWeightage> getCombos(String comboName){
		return objDestinationCombos.get(comboName);
	}

	
	public void putCombos(String comboName, List<DestinationWeightage> destList) {
		objDestinationCombos.put(comboName, destList);
	}
	
	
	public void removeAndReplaceIfExistOrAddNewEntry(String comboName, List<DestinationWeightage> destList, DecisionElementData data) {
		String[] nameEntry = comboName.split("____");
		String routePlanName = nameEntry[0];
		
		data.addToLog(data.getCurrentElement(),"Name : " + routePlanName);
		
        String regex = routePlanName + "____\"\\\\d{4}-\\\\d{2}-\\\\d{2}T\\\\d{2}:\\\\d{2}:\\\\d{2}\\\\.\\\\d{2}(Mon|Tue|Wed|Thu|Fri|Sat|Sun)\\\\d{2}:\\\\d{2}-\\\\d{2}:\\\\d{2}\"";
        Pattern pattern = Pattern.compile(regex);

        Iterator<Map.Entry<String, List<DestinationWeightage>>> iterator = objDestinationCombos.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<DestinationWeightage>> entry = iterator.next();
            if (pattern.matcher(entry.getKey()).matches()) {
            	data.addToLog(data.getCurrentElement(),"Old DestList removed with Key : " + entry.getKey());
                iterator.remove();
            }
        }
        objDestinationCombos.put(comboName, destList);
        data.addToLog(data.getCurrentElement(),"New DestList added with Key : " + comboName);
	}
	
	
	/*
	public void clearHashMap() {
		objDestinationCombos.clear();
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
    */
}
