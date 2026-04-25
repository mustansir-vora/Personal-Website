package com.farmers.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalsCommon {

	public static String getExceptionTrace(Exception exc){
		StringWriter str= new StringWriter();
		exc.printStackTrace(new PrintWriter(str));
		return str.toString();
	}

	public static boolean isNullOrEmpty(String input){
		boolean result = false;
		if(input == null || input.isEmpty() || input.trim().isEmpty()){
			result = true;
		}
		return result;
	}

	public static String getDateByFormat(String format){
		String currentDate = Constants.EMPTY_STRING;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);  
			Date date = new Date();  
			currentDate = formatter.format(date); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentDate;
	}

	public static String getCurrentSysIP(){
		String ip = Constants.EMPTY_STRING;
		try {
			InetAddress inetAddress  = InetAddress.getLocalHost();
			ip = inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;

	}

	public static String convertNullToEmpty(String input){
		String result = input;
		if(input == null){
			result = "";
		}
		return result;
	}

	public static boolean isRepetitive(String number){
		boolean isRepetitive = false;
		try {
			String regex = "\\b([a-zA-Z0-9])\\1\\1+\\b";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(number);
			isRepetitive = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isRepetitive;
	}
	
	
}
