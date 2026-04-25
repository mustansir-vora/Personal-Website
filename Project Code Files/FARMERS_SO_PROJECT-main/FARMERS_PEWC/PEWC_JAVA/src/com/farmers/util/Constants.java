package com.farmers.util;

import java.io.Serializable;

public class Constants implements Serializable{


	public Constants() {

	}

	public static final String A_CONTEXT = "A_CONTEXT";
	public static final String A_REPORT_DATA_PATH="A_REPORT_DATA_PATH";
	public static final String AppVarNameStartwith="A_";
	/*
	 * PEWC common
	 */
	public static final String LOG4J_CONFIG_PATH = "C:\\Servion\\PEWC_CVP\\log4j2.xml";
	public static final String IVR_CONFIG_PATH = "C:\\Servion\\PEWC_CVP\\PEWC.properties";
	//public static final String CONFIG_PATH="C:\\Servion\\PEWC_CVP\\";
	//public static final String LOG4J_XML_FILE="log4j2.xml";
	public static final String DONE = "done";
	public static final String DTMF_INPUT = "value";
	public static final String DIGIT_TRIES_EXCEEDED = "tries_exceeded";
	public static final int NUMERIC_2 = 2;
	public static final String EMPTY_STRING = "";
	public static final String ERROR = "ERROR";
	public static final String S_AUDIO_PATH = "S_AUDIO_PATH";
	public static final String REPLACE_IP = "$$IP$$";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String S_CUST_CONSENT_URL = "S_CUST_CONSENT_URL";
	public static final String S_DISCLAIME_RMSG = "S_DISCLAIME_RMSG";
	public static final String S_CALL_ID = "S_CALL_ID";


	/*
	 * PEWC Session Values
	 */
	public static final String S_MOBILE_NO = "S_MOBILE_NO";
	public static final String S_DIGIT_TRIES_COUNTER = "S_DIGIT_TRIES_COUNTER";
	public final static String S_HOST_DETAILS_LIST="S_HOST_DETAILS_LIST";
	public final static String S_SEQUENCE_COUNTER="S_SEQUENCE_COUNTER";
	public final static String S_IVRPATH="S_IVRPATH";
	public final static String S_CALLSTART_TIME="S_CALLSTART_TIME";
	public final static String S_CALLID = "S_CALLID";
	public final static String S_ICMID="S_ICMID";
	public final static String S_VXML_SERVER_IP="S_VXML_SERVER_IP";
	public final static String S_DNIS="S_DNIS";
	public final static String S_ANI="S_ANI";
	public final static String S_PREF_LANG = "S_PREF_LANG";
	public static final String S_AGENT="S_AGENT";
	public final static String S_ANI_GROUP = "S_ANI_GROUP";
	public final static String S_BRAND = "S_BRAND";
	public static final String S_KYC_AUTHENTICATED="S_KYC_AUTHENTICATED";
	public static final String S_CALL_OUTCOME="S_CALL_OUTCOME";
	public static final String S_CAT_CODE="S_CAT_CODE";
	public static final String S_FINAL_DESTNUM = "S_FINAL_DESTNUM";
	public final static String S_CALL_DURATION="S_CALL_DURATION";
	public final static String S_FINAL_ANI="S_FINAL_ANI";
	public final static String S_FINAL_CATEGORY="S_FINAL_CATEGORY";
	public final static String S_FINAL_DNIS="S_FINAL_DNIS";
	public final static String S_FINAL_DNIS_GROUP="S_FINAL_DNIS_GROUP";
	public static final String S_FINAL_INTENT="S_FINAL_INTENT";
	public final static String S_FINAL_LANG = "S_FINAL_LANG";
	public final static String S_FINAL_LOB="S_FINAL_LOB";
	public final static String S_FINAL_STATE_GROUP="S_FINAL_STATE_GROUP";
	public static final String S_HOLD_ON="S_HOLD_ON";
	public static final String S_CALLCENTER_OPEN_CLOSED = "S_CALLCENTER_OPEN_CLOSED";
	public static final String S_NO_INPUT="S_NO_INPUT";
	public static final String S_NO_MATCH="S_NO_MATCH";
	public final static String S_ORIGINAL_ANI="S_ORIGINAL_ANI";
	public final static String S_ORIGINAL_CATEGORY="S_ORIGINAL_CATEGORY";
	public final static String S_ORIGINAL_DNIS_GROUP="S_ORIGINAL_DNIS_GROUP";
	public final static String S_ORIGINAL_LANGUAGE="S_ORIGINAL_LANGUAGE";
	public final static String S_ORIGINAL_LOB="S_ORIGINAL_LOB";
	public final static String S_ORIGINAL_DNIS="S_ORIGINAL_DNIS";
	public final static String S_INTENT = "S_INTENT";
	public final static String S_ORIGINAL_STATE_GROUP="S_ORIGINAL_STATE_GROUP";
	public final static String S_PHONE_TYPE="S_PHONE_TYPE";
	public final static String S_TRANSFER_REASON="S_TRANSFER_REASON";
	public final static String S_TRANSFER_FLAG="S_TRANSFER_FLAG";
	public static final String S_POLICY_NUM = "S_POLICY_NUM";
	public static final String S_RCKEY="S_RCKEY";
	public static final String S_REPEAT="S_REPEAT";
	public static final String S_ROUTING_TRANSFER_ACTION = "S_ROUTING_TRANSFER_ACTION";
	public final static String S_ROUTING_KEY="S_ROUTING_KEY";
	public static final String S_STATENAME = "S_STATENAME";
	public static final String S_MENU_SELCTION_KEY = "S_MENU_SELCTION_KEY";
	public final static String ICM_CALLID = "callid";
	public final static String ICM_DNIS = "DNIS";
	public final static String ICM_ANI = "ANI";
	public final static String ICM_CCALLID = "CCallId";

	public final static String ICM_TTID = "ttid";
	public final static String ICM_TIDV = "tidv";
	public final static String ICM_TI = "ti";
	public final static String ICM_OANI = "OANI";
	public final static String ICM_OCALLID = "OCallId";
	public final static String ICM_BE = "BE";
	public final static String ICM_CS = "CS";
	public final static String ICM_CGUID = "CGUID";
	public final static String ICM_AREACODE = "areacode";
	public final static String ICM_BU = "BU";




	/**
	 * PEWC Application Data
	 */
	public static final String A_IVR_PROP = "A_IVR_PROP";

	/**
	 * API RESPONSE JSON KEY
	 */
	public static final String REQUEST_BODY  = "requestBody";
	public static final String RESPONSE_BODY  = "responseBody";
	public static final String RESPONSE_CODE  = "responseCode";
	public static final String RESPONSE_MSG  = "responseMsg";

	public final static String REPORT_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
	public final static String NA = "NA";
	public static final String AppVarName_ActivityLogLevel = "A_ACTIVITY_LOG_LEVEL";
	public final static String AppVarName_DefaultLanguage="A_DEFAULT_LANGUAGE";
	public final static String AppVarName_MEDIA_SERVER_IP="A_MEDIA_SERVER_IP";
	public final static String AppVarName_MEDIA_SERVER_PORT="A_MEDIA_SERVER_PORT";
	public final static String AppVarName_PROMPTS_PATH="A_PROMPTS_PATH";
	public final static String AppVarName_MEDIA_CONNECTION_MODE="A_MEDIA_CONNECTION_MODE";
	public final static String WS="WS";
	public static final String EmptyString = "";
	public static final String NOMATCH="NOMATCH";
	public static final String NOINPUT="NOINPUT";
	public static final String ER="ER";

}


