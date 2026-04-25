package com.farmers.APIUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.core.Logger;

public class stacktrace {
	
	public static void printStackTrace(Logger logger,Exception e)
	{   
		try{
			StringWriter stw = new java.io.StringWriter();
			PrintWriter pw = new java.io.PrintWriter(stw);
			e.printStackTrace(pw);    
			logger.error("Exception :"+stw.toString());
			stw.close();
			pw.close();	
		}catch(Exception e2){
			logger.error("Exception in stact trace :"+e2.getMessage());
		}

	}

}
