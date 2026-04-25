package com.farmers.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Time {
	public static void main(String[] args) throws ParseException {
		
		String timewindow = "9:00-17:00|19:00-23:00";
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date currDate = new Date();
		Date startDate = new Date();
		Date endDate = new Date();
		String[] timewindowArr = (Pattern.compile("\\|").split(timewindow));
		boolean isOpenHours = false;
		for (int i=0;i<timewindowArr.length;i++) {  //9:00-13:00
			startDate = sdf.parse(timewindowArr[i].split("-")[0]);
			endDate = sdf.parse(timewindowArr[i].split("-")[1]);
			currDate = sdf.parse(sdf.format(currDate));
			System.out.println(startDate);
			System.out.println(endDate);
			System.out.println(currDate);
			System.out.println("--------------------");
			if(currDate.before(endDate) && currDate.after(startDate)){
				isOpenHours = true;
				break;
			}
		}
		System.out.println(isOpenHours);
	}

}
