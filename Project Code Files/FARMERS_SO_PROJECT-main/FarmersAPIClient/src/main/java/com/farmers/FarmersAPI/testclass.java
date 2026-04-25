package com.farmers.FarmersAPI;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

public class testclass {
	
	public void runtestafni() {
		
		try {
			LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
			File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
			context.setConfigLocation(file.toURI());
			Afni_Post test = new Afni_Post();
			String url = "https://apigw-pod2.dm-us.informaticacloud.com/t/afni.com/CallDetailCapture";
			 String tid = "CiscoTest21";
			 String callerani = "5122333198";
			 String policynumber = "";
			 String accountnumber = "";
			 String originaldnis = "15541452123";
			 String originaldnisdescription = "";
			 String zipcode = "";
			 String quotenumber = "";
			 String state = "";
			 
			JSONObject resp = test.start(url, tid, callerani, policynumber, accountnumber, originaldnis, originaldnisdescription, zipcode, quotenumber, state, 10000, 10000, context);
			
			System.out.println(resp);
		}
		catch(Exception e){
			System.out.println("Exception Occured :: "+e);
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println(":: before calling test method :: "); 
		testclass test = new testclass();
		test.runtestafni();
		System.out.println(":: Success ::");
	}

}
