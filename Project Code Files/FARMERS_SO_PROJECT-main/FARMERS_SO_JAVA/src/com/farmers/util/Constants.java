package com.farmers.util;

import java.io.Serializable;

public class Constants implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Constants() {

	}

	public static final String APPLICATION_NAME = "FARMERS_SO_CVP";

	public static final String CONFIG_PATH = "C:\\Servion\\FARMERS_SO_CVP\\Config\\";

	public static final String LOG4J_PROP_FILE = "log4j.properties";
	public static final String LOG4J_XML_FILE = "log4j2.xml";
	public static final String IVR_CONFIG_PROP_FILE = "ivrConfig.properties";
	public static final String MENU_CONFIG_PROP_FILE = "MenuConfig.properties";
	public static final String MENU_SELECTION_CONFIG_PROP_FILE = "MenuSelectionKey.properties";

	// Application Global Constants
	public static final String APP_LEVEL_LOG4J_PROP_OBJECT = "APP_LOG4J_PROP";
	public static final String APP_LEVEL_IVRCONFIG_PROP_OBJECT = "APP_IVRCONFIG_PROP";
	public static final String APP_LEVEL_MENUCONFIG_PROP_OBJECT = "APP_MENUCONFIG_PROP";
	public static final String APP_LEVEL_MENU_SELECTION_PROP_OBJECT = "APP_MENUSELECTION_PROP";

	public static final String AppVarNameStartwith = "A_";
	public static final String SessionVarNameStartwith = "S_";

	public static final String A_AUDIO_MAP = "AUDIO_MAP";

	public static final String A_CONTEXT = "A_CONTEXT";

	public static final String EmptyString = "";
	public static final String ERROR = "ERROR";
	public static final String MENU_MAP = "MENU_MAP";
	public static final String ER = "ER";
	public static final String SU = "SU";

	public static final String S_TRANSCODE = "S_TRANSCODE";

	public final static String AppFolder = "app";
	public final static String SysFolder = "sys";
	public static final String S_MediaAppPath = "S_STR_MEDIA_APP_PATH";
	public static final String S_MediaSysPath = "S_STR_MEDIA_SYS_PATH";

	// Farmers server
	public final static String ICM_DNIS = "DNIS";
	// public final static String ICM_DNIS = "_dnis";
	public final static String ICM_ANI = "ANI";
	public final static String ICM_CALLID = "callid";
	public final static String S_CALLID = "S_CALLID";
	public final static String ICM_TTID = "ttid";
	public final static String ICM_TIDV = "tidv";
	public final static String ICM_TI = "ti";
	public final static String ICM_OANI = "OANI";
	public final static String ICM_OCALLID = "OCallId";
	public final static String ICM_CCALLID = "CCallId";
	public final static String ICM_BE = "BE";
	public final static String ICM_CS = "CS";
	public final static String ICM_CGUID = "CGUID";
	public final static String ICM_AREACODE = "areacode";
	public final static String ICM_BU = "BU";

	public final static String S_CVP_CALLID = "S_CVP_CALLID";
	public final static String S_ICMID = "S_ICMID";
	public final static String S_ANI = "S_ANI";
	public final static String S_ORIGINAL_ANI = "S_ORIGINAL_ANI";
	public final static String S_DNIS = "S_DNIS";
	public final static String S_ORIGINAL_DNIS = "S_ORIGINAL_DNIS";
	public final static String S_ORIGINAL_CATEGORY = "S_ORIGINAL_CATEGORY";
	public final static String S_ORIGINAL_DNIS_GROUP = "S_ORIGINAL_DNIS_GROUP";
	public final static String S_ORIGINAL_LANGUAGE = "S_ORIGINAL_LANGUAGE";
	public final static String S_ORIGINAL_LOB = "S_ORIGINAL_LOB";
	public final static String S_VXML_SERVER_IP = "S_VXML_SERVER_IP";
	public final static String S_CALLSTART_TIME = "S_CALLSTART_TIME";
	public final static String S_CALLEND_TIME = "S_CALLEND_TIME";
	public final static String S_CALL_DURATION = "S_CALL_DURATION";
	public final static String S_CALL_END_REASON = "S_CALL_END_REASON";
	public final static String S_CALLSTART_DATE_TIME = "S_CALLSTART_DATE_TIME";
	public final static String S_SESSION_ID = "S_SESSION_ID";
	public final static String S_DNISGROUP = "S_DNISGROUP";
	public final static String S_APP_NAME = "S_APP_NAME";
	public final static String S_BU = "S_BU";
	public final static String S_CATEGORY = "S_CATEGORY";
	public final static String S_LOB = "S_LOB";
	public final static String S_IVRPATH = "S_IVRPATH";
	public final static String S_OANI = "S_OANI";
	public final static String S_OCALLID = "S_OCALLID";
	public final static String S_CCALLID = "S_CCALLID";
	public final static String S_TTID = "S_TTID";
	public final static String S_TIDV = "S_TIDV";
	public final static String S_TI = "S_TI";
	public final static String S_BE = "S_BE";
	public final static String S_CS = "S_CS";
	public final static String S_CGUID = "S_CGUID";
	public final static String S_ROUTING_KEY = "S_ROUTING_KEY";
	public final static String S_CALL_ROUTED_STATUS = "S_CALL_ROUTED_STATUS";
	public final static String S_FINAL_ANI = "S_FINAL_ANI";
	public final static String S_FINAL_CATEGORY = "S_FINAL_CATEGORY";
	public final static String S_FINAL_DNIS = "S_FINAL_DNIS";
	public final static String S_FINAL_DNIS_GROUP = "S_FINAL_DNIS_GROUP";
	public final static String S_FINAL_LOB = "S_FINAL_LOB";
	public final static String S_SCREENPOPUP_ENABLED = "S_SCREENPOPUP_ENABLED";
	public final static String S_SEQUENCE_COUNTER = "S_SEQUENCE_COUNTER";
	public final static String S_STATE_GROUP = "S_STATE_GROUP";
	public final static String S_ORIGINAL_STATE_GROUP = "S_ORIGINAL_STATE_GROUP";
	public final static String S_FINAL_STATE_GROUP = "S_FINAL_STATE_GROUP";
	public final static String S_PHONE_TYPE = "S_PHONE_TYPE";
	public final static String S_TRANSFER_REASON = "S_TRANSFER_REASON";
	public final static String S_TRANSFER_FLAG = "S_TRANSFER_FLAG";

	public static final String AppVarName_ActivityLogLevel = "A_ACTIVITY_LOG_LEVEL";
	public final static String AppVarName_DefaultLanguage = "A_DEFAULT_LANGUAGE";
	public final static String AppVarName_MEDIA_SERVER_IP = "A_MEDIA_SERVER_IP";
	public final static String AppVarName_MEDIA_SERVER_PORT = "A_MEDIA_SERVER_PORT";
	public final static String AppVarName_PROMPTS_PATH = "A_PROMPTS_PATH";
	public final static String AppVarName_MEDIA_CONNECTION_MODE = "A_MEDIA_CONNECTION_MODE";

	public final static String S_DEFAULT_LANG = "S_DEFAULT_LANG";
	public final static String EN = "EN";
	public final static String SP = "SP";
	public final static String English = "English";
	public final static String Spanish = "Spanish";
	public final static String S_URL_POLICUNUM = "S_URL_POLICUNUM";
	public final static String S_URL_PHONENUM = "S_URL_PHONENUM";

	// Configuration for WAIT TIME menu
	public static final String DTMF_KEYPRESS = "dtmf_keypress";
	public static final String NOINPUT_TIMEOUT = "noinput_timeout";
	public static final String MAX_NOINPUT_COUNT = "max_noinput_count";
	public static final String MAX_NOMATCH_COUNT = "max_nomatch_count";
	public static final String MAX_ERROR_COUNT = "max_error_count";// max_error_count
	public static final String ERROR_AUDIO = "Error_audio";// Error_audio
	public static final String INITIAL_GROUP_SILENCE = "Intial_audio_silence";
	public static final String SILENCE_AUDIO = "silence_audio";
	public static final String NINM_AUDIO_COMMON = "NINM_audio_common";
	public static final String MAX_TRIES_DYN_MENU = "1";

	// Wav file format to be used
	public static final String AUDIO_FILE_FORMAT = ".wav";

	// Session variables for No Match audio and No Input Audio
	public static final String NO_MATCH_AUDIO = "S_NO_MATCH_AUDIO";
	public static final String NO_INPUT_AUDIO = "S_NO_INPUT_AUDIO";
	public static final String MAX_ERR_AUDIO = "S_MAX_ERR_AUDIO";

	public static final String MAX_TRIES = "S_MAX_TRIES";
	public static final String NO_INPUT_TIMEOUT = "S_NO_INPUT_TIMEOUT";

	// Form element's exit state on Maximum NoMatch
	public final static String MAX_NOMATCH = "max_nomatch";

	// Form element's exit state on Maximum NoInput
	public final static String MAX_NOINPUT = "max_noinput";

	// Form element's exit state on getting valid Input
	public final static String DONE = "done";

	// Form element's exit state on getting invalid Input
	public final static String MAX_ERROR = "max_error";
	public final static String MAX_ERROR_TRANSFER = "MAX_ERROR_TRANSFER";

	// Common counter for No Input and No Match tries
	public final static String TRIES_COUNTER = "S_TRIES_COUNTER";

	// Value entered in an element
	public final static String DTMF_INPUT = "value";

	// Counter to be used in form elements to have common counter
	public final static String NO_INPUT_COUNT = "1";
	public final static String NO_MATCH_COUNT = "1";
	public final static String DTMF = "DTMF";

	// When no menu option selected
	public final static String NONE = "NONE";
	public final static String ERROR_REP = "ERROR";
	public final static String DTMF_KEY_PRESS_0 = "0";
	public final static String DTMF_KEY_PRESS_1 = "1";
	public final static String DTMF_KEY_PRESS_2 = "2";
	public final static String DTMF_KEY_PRESS_3 = "3";
	public final static String DTMF_KEY_PRESS_4 = "4";
	public final static String DTMF_KEY_PRESS_5 = "5";
	public final static String DTMF_KEY_PRESS_6 = "6";
	public final static String DTMF_KEY_PRESS_7 = "7";
	public final static String DTMF_KEY_PRESS_8 = "8";
	public final static String DTMF_KEY_PRESS_9 = "9";
	public final static String DTMF_KEY_PRESS_STAR = "*";
	public final static String DTMF_KEY_PRESS_HASH = "#";

	public final static String EXIT_STATE_STAR = "star";
	public final static String EXIT_STATE_HASH = "hash";

	public static int DEF_MAX_TRIES = 3;

	// APP ERROR WAV
	public final static String APP_ERR_WAV = "APP_ERR_WAV";

	// values for Flags
	public final static String YES = "Y";
	public final static String NO = "N";
	public final static String TRUE = "TRUE";
	public final static String FALSE = "FALSE";

	// Call End Global Constants
	public final static String HANGUP = "Hang Up";
	public final static String DISCONNECT = "disconnect";
	public final static String S_CALL_DISPOSITION = "S_CALL_DISPOSITION";
	public final static String S_END_TYPE = "S_END_TYPE";

	public final static String IVR_DISCONNECT = "ID";
	public final static String CALLER_DISCONNECT = "CD";

	public final static String NA = "NA";

	// Exit states
	public final static String MAX_TRIES_REACHED = "MAX_TRIES";
	public final static String TRANSFER = "TRANSFER";

	// Delimiters
	public final static String COMMA = ",";
	public final static String SEMICOLON = ";";
	public final static String PIPE = "\\|";
	public final static String TILDE = "~";
	public final static String COLON = ":";
	public final static String PLUS = "+";
	public final static String MINUS = "-";
	public final static String HYPHEN = "-";
	public final static String STAR = "*";
	public final static String HASH = "#";
	public final static String POINT = ".";
	public final static String DOT = ".";
	public final static String DOT_SEPERATOR = "\\.";
	public final static String FORWARD_SLASH = "/";
	public final static String PIPE2 = "|";
	public static final String PIPE_SEPERATOR = "\\|";

	// Maintenance Flag
	public final static String VALID = "VALID";
	public final static String INVALID = "INVALID";

	public static final String N_FLAG = "N";
	public static final String Y_FLAG = "Y";
	public static final String Transfer = "T";
	public static final String Disconnect = "D";

	public static final String S_CallerInput = "S_CALLER_INPUT";
	public static final String S_EXIT_STATE = "S_EXIT_STATE";

	public static final String AppVarName_MaxnoinputCount = "A_MAX_NO_INPUT";
	public static final String AppVarName_MaxnomatchCount = "A_MAX_NO_MATCH";
	public static final String AppVarName_MaxtriesCount = "A_MAX_TRIES";
	public static final String AppVarName_NomatchAudio = "A_NO_MATCH_AUDIO";
	public static final String AppVarName_NoinputAudio = "A_NO_INPUT_AUDIO";
	public static final String AppVarName_MaxErrorNoinputAudio = "A_MAX_ERROR_NO_INPUT_AUDIO";
	public static final String AppVarName_MaxErrorNomactchAudio = "A_MAX_ERROR_NO_MATCH_AUDIO";
	public static final String AppVarName_MaxErrorAudio = "A_MAX_ERR_AUDIO";
	public static final String AppVarName_NoinputTimeout = "A_NO_INPUT_TIMEOUT";
	public static final String S_DYN_PROMPT = "S_DYN_PROMPT";

	// Values sent to ICM
	public final static String S_VXML0 = "S_VXML_0";
	public final static String S_VXML1 = "S_VXML_1";
	public final static String S_VXML2 = "S_VXML_2";
	public final static String S_VXML3 = "S_VXML_3";

	public static final String CALLED_FROM_PE = "CALLED_FROM_PE";
	public static final String NO_INPUT = "NI";
	public static final String NO_MATCH = "NM";
	public final static String NoMatch = "NoMatch";

	public static final String S_SilenceAudioFile = "S_SILENCE_AUDIO_FILE";

	public static final String STRING_YES = "YES";
	public static final String STRING_NO = "NO";
	public static final String SYSTEM_NAME = "VRS";
	public static final String USER_ID = "VRS-101";

	public static final String NINM = "NINM";
	public static final String NM = "NM";
	public static final String NI = "NI";

	public final static String S_AL_MENU_TRAVERSED = "S_AL_MENU_TRAVERSED";
	public final static String S_MENU_CODE = "S_MENU_CODE";

	public final static String S_AGENT_TRANSFER = "S_AGENT_TRANSFER";

	// NEW
	public final static String S_FIND_IVR_MAINFLOW = "S_FIND_IVR_MAINFLOW";
	public final static String REPORT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
	public final static String SHIFT_DATE_FORMAT = "HH:mm";
	public final static String CT_TIMEZONE = "America/Chicago";
	public final static String WS = "WS";
	public final static String S_HOST_DETAILS_LIST = "S_HOST_DETAILS_LIST";
	public final static String S_AGENTOFRECORDID = "S_AGENTOFRECORDID";
	public final static String S_AGENT_SEGMENTATION = "S_AGENT_SEGMENTATION";
	public final static String S_PRODUCER_CODE = "S_PRODUCER_CODE";
	public final static String S_PRODUCER_CODE_MATCH = "S_PRODUCER_CODE_MATCH";
	public final static String S_DOB = "S_DOB";
	public final static String S_CALLER_INPUT_DOB = "S_CALLER_INPUT_DOB";
	public final static String S_API_DOB = "S_API_DOB";
	public final static String S_POSTAL_CODE = "S_POSTAL_CODE";
	public final static String S_POLICY_CONTRACT_NUM = "S_POLICY_CONTRACT_NUM";
	public final static String S_BILLING_ACC_NUM = "S_BILLING_ACC_NUM";
	public final static String S_POLICY_SOURCE = "S_POLICY_SOURCE";
	public final static String S_POLICY_SEGMENTATION = "S_POLICY_SEGMENTATION";
	public final static String S_POLICY_ATTRIBUTES = "S_POLICY_ATTRIBUTES";
	public final static String S_DESTINATION_APP = "S_DESTINATION_APP";
	public final static String S_TOLLFREE_NUM = "S_TOLLFREE_NUM";
	public final static String S_CALLER_INPUT = "S_CALLER_INPUT";
	public final static String S_POPUP_TYPE = "S_POPUP_TYPE";
	public final static String S_POPUP_POLICYNUM = "01";
	public final static String S_POPUP_ACCOUNTNUM = "02";
	public final static String S_POPUP_VENDOR = "03";
	public final static String S_POPUP_IVRTOTEXT = "04";
	public final static String S_CALLED_INTO = "S_CALLED_INTO";
	public final static String S_CALLED_INTIME = "S_CALLED_INTIME";
	public final static String S_TOLLFREE_DESCRIPTION = "S_TOLLFREE_DESCRIPTION";
	public final static String S_CALLLER_TYPE = "S_CALLLER_TYPE";
	public final static String S_CALLLER_TYPE_CUSTOMER = "01";
	public final static String S_CALLLER_TYPE_AGENT = "02";
	public final static String S_CALLLER_TYPE_VENDOR = "03";
	public final static String S_PREF_LANG = "S_PREF_LANG";
	public final static String S_FINAL_LANG = "S_FINAL_LANG";
	public final static String S_AN1 = "S_AN1";
	public final static String S_UPN1 = "S_UPN1";
	public final static String S_TRANFER_REASON = "S_TRANFER_REASON";
	public final static String S_INTENT = "S_INTENT";
	public final static String S_BRAND = "S_BRAND";
	public final static String S_BRAND_LABEL = "S_BRAND_LABEL";
	public final static String S_VALID_POLICY_NUM = "S_VALID_POLICY_NUM";
	public final static String S_TI_SCORE = "S_TI_SCORE";
	public final static String S_ZIP_CODE = "S_ZIP_CODE";
	public final static String S_EXTRA_INFO1 = "S_EXTRA_INFO1";
	public final static String S_GUID = "S_GUID";
	public final static String S_BUSNO = "S_BUSNO";
	public final static String S_STATE = "S_STATE";
	public final static String S_STATE_CODE = "S_STATE_CODE";
	public final static String S_DNIS_DESCRIPTION = "S_DNIS_DESCRIPTION";
	public final static String S_ANI_EXIST = "S_ANI_EXIST";
	public final static String S_ANI_GROUP_BYPASSED = "S_ANI_GROUP_BYPASSED";
	public final static String S_ANI_GROUP_ACTION = "S_ANI_GROUP_ACTION";
	public final static String S_ANI_GROUP = "S_ANI_GROUP";
	public final static String S_PEEL_OFF_ENABLED = "S_PEEL_OFF_ENABLED";
	public final static String S_PEEL_OFF_DNIS = "S_PEEL_OFF_DNIS";
	public final static String S_LIVE_PERSON_CHECK_ENABLED = "S_LIVE_PERSON_CHECK_ENABLED";
	public final static String S_KYC_ENABLED = "S_KYC_ENABLED";
	public final static String S_NLU_ENABLED = "S_NLU_ENABLED";
	public final static String S_SECONDARY_LANG = "S_SECONDARY_LANG";
	public final static String S_PRODUCER_LINE = "S_PRODUCER_LINE";
	public final static String S_IS_MAIN_TABLE_CALLED = "S_IS_MAIN_TABLE_CALLED";
	public final static String S_EMERGENCY_ENABLED = "S_EMERGENCY_ENABLED";
	public final static String S_EMERGENCY_ACTION = "S_EMERGENCY_ACTION";
	public static final String IVR_INTENT_PROPERTY_VALUE = "IVR_INTENT_PROPERTY_VALUE";
	public static final String CALLER_CALLED_ROADSIDEASSISTANCE = "CALLER_CALLED_ROADSIDEASSISTANCE";
	public static final String S_AFTERHOURS_SELFSERVICE_AVAILABLE = "S_AFTERHOURS_SELFSERVICE_AVAILABLE";
	public static final String S_MN_FEIN_SSN_VALUE = "S_MN_FEIN_SSN_VALUE";
	public static final String S_API_FEIN_SSN_VALUE = "S_API_FEIN_SSN_VALUE";
	public static final String S_IS_SINGLE_POLICIY = "S_IS_SINGLE_POLICIY";
	public static final String S_FEIN_SSN_MATCHED = "S_FEIN_SSN_MATCHED";
	public static final String S_CALLCENTER_OPEN_CLOSED = "S_CALLCENTER_OPEN_CLOSED";
	public static final String S_DR = "S_DR";
	public static final String S_OPEN = "OPEN";
	public static final String CLOSED = "CLOSED";
	public static final String S_AGENT_OF_RECORDID = "S_AGENT_OF_RECORDID";
	public static final String S_NOOF_POLICIES_ANILOOKUP = "S_NOOF_POLICIES_ANILOOKUP";
	public static final String S_ACTIVE_CLAIM_AVAILABLE = "S_ACTIVE_CLAIM_AVAILABLE";
	public static final String IS_MORETHAN_ZERO_POLICIES = "IS_MORETHAN_ZERO_POLICIES";
	public static final String IS_MORETHAN_ONE_POLICIES = "IS_MORETHAN_ONE_POLICIES";
	public static final String S_MENU_SELCTION_KEY = "S_MENU_SELCTION_KEY";
	public static final String S_CC_NAME_LIST = "timetable2|timetable3|timetable4|timetable5|timetable";
	public static final String S_ROUTING_TRANSFER_ACTION = "S_ROUTING_TRANSFER_ACTION";
	public static final String S_FINAL_DESTNUM = "S_FINAL_DESTNUM";
	public static final String S_ROUTING_PRIORITY = "S_ROUTING_PRIORITY";
	public static final String S_BU_ID = "S_BU_ID";
	public static final String S_FINAL_STATE = "S_FINAL_STATE";
	public static final String S_OPERATION = "S_OPERATION";
	public static final String S_ID = "S_ID";
	public static final String S_AUTH_TOKEN = "S_AUTH_TOKEN";
	public static final String S_BRAND_CODE = "S_BRAND_CODE";
	public static final String S_FIRST_NAME = "S_FIRST_NAME";
	public static final String S_LAST_NAME = "S_LAST_NAME";
	public static final String S_EMAIL = "S_EMAIL";
	public static final String S_LINE1 = "S_LINE1";
	public static final String S_PAYOR_ZIP_CODE = "S_PAYOR_ZIP_CODE";
	public static final String S_CITY = "S_CITY";
	public static final String S_COUNTRY = "S_COUNTRY";
	public static final String S_SCREEN_POPUP_KEY = "S_SCREEN_POPUP_KEY";
	public static final String S_NOOF_POLICIES_ANILOOKUP_EXITSTATE = "S_NOOF_POLICIES_ANILOOKUP_EXITSTATE";
	public static final String S_SINGLE_POLICY_FOUND = "S_SINGLE_POLICY_FOUND";
	public static final String S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND = "S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND";
	public static final String S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND = "S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND";
	public static final String S_BAL_AMOUNT = "S_BAL_AMOUNT";
	public static final String S_POLICY_EFFECTIVE_DATE = "S_POLICY_EFFECTIVE_DATE";
	public static final String S_POLICY_STATE = "S_POLICY_STATE";
	public static final String S_POLICY_INPUT_TYPE_CD = "S_POLICY_INPUT_TYPE_CD";
	public static final String S_PRODUCER_ROLE_CODE = "S_PRODUCER_ROLE_CODE";
	public static final String S_CALL_ROUTING_INDICATOR = "S_CALL_ROUTING_INDICATOR";
	public static final String S_IA_POLICY_INDICATOR = "S_IA_POLICY_INDICATOR";
	public static final String S_GPC = "S_GPC";
	public static final String S_COMPANY_PRODUCT_CODE = "S_COMPANY_PRODUCT_CODE";
	public static final String S_SERVICE_LEVELS = "S_SERVICE_LEVELS";
	public static final String S_COMBO_PACKAGE_INDICATOR = "S_COMBO_PACKAGE_INDICATOR";
	public static final String S_INTERNAL_POLICY_NUMBER = "S_INTERNAL_POLICY_NUMBER";
	public static final String MATCH = "MATCH";
	public static final String S_API_NAME = "S_API_NAME";
	public static final String S_IS_API_DISABLED = "S_IS_API_DISABLED";
	public static final String S_WARHEAD_OPEN_CLOSED = "S_WARHEAD_OPEN_CLOSED";
	public static final String S_AREACODE = "S_AREACODE";
	public static final String S_STATENAME = "S_STATENAME";
	public static final String S_STATECODE = "S_STATECODE";
	public static final String S_FARMERS_STATECODE = "S_FARMERS_STATECODE";
	public static final String S_AGENT = "S_AGENT";
	public static final String S_KYC_AUTHENTICATED = "S_KYC_AUTHENTICATED";
	public static final String S_CALL_OUTCOME = "S_CALL_OUTCOME";
	public static final String S_CAT_CODE = "S_CAT_CODE";
	public static final String S_LAST_FUNCTION_NAME = "S_LAST_FUNCTION_NAME";
	public static final String S_LAST_FUNCTION_RESULT = "S_LAST_FUNCTION_RESULT";
	public static final String S_NO_INPUT = "S_NO_INPUT";
	public static final String S_NO_MATCH = "S_NO_MATCH";
	public static final String S_RCKEY = "S_RCKEY";
	public static final String S_REPEAT = "S_REPEAT";
	public static final String S_HOLD_ON = "S_HOLD_ON";
	public static final String S_TRANFER_RECORDING_ENABLED = "S_TRANFER_RECORDING_ENABLED";

	// LOB OR BU
	public final static String BW = "BW";
	public final static String Farmers = "Farmers";
	public final static String Foremost = "Foremost";
	public final static String FWS = "FWS";
	public final static String BU_21st = "21st";
	public final static String BU_Entity = "M";

	// HARI
	public static final String S_URL = "S_URL";
	public static final String S_BUSINESS_UNIT = "S_BUSINESS_UNIT";
	public static final String S_PHONE_NUM = "S_PHONE_NUM";
	public static final String S_USER_ID = "S_USER_ID";
	public static final String S_SYSTEM_NAME = "S_SYSTEM_NAME";
	public static final String S_CONN_TIMEOUT = "S_CON_TIMEOUT";
	public static final String S_READ_TIMEOUT = "S_READ_TIMEOUT";
	public static final String _RESP = "_RESP";
	public static final String INITIAL_AUDIO_GROUP = "initial_audio_group";
	public static final String INITIAL = "Initial";

	public static final String REQUEST_BODY = "requestBody";
	public static final String RESPONSE_BODY = "responseBody";
	public static final String RESPONSE_CODE = "responseCode";
	public static final String RESPONSE_MSG = "responseMsg";

	// CS1173483: Separate TimeOut Configurations For AccLinkAniLookup API
	public static final String S_ANILOOKUP_CONN_TIMEOUT = "S_ANILOOKUP_CONN_TIMEOUT";
	public static final String S_ANILOOKUP_READ_TIMEOUT = "S_ANILOOKUP_READ_TIMEOUT";

	// FARMERS API URL's
	public static final String S_ACE_LOOKUP_URL = "S_ACE_LOOKUP_URL";
	public static final String S_RESULT_RETRIEVAL_URL = "S_RESULT_RETRIEVAL_URL";
	public static final String S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL = "S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL";
	public static final String S_MULESOFT_FARMER_POLICYINQUIRY_URL = "S_MULESOFT_FARMER_POLICYINQUIRY_URL";
	public static final String S_FOREMOST_POLICYINQUIIRY_URL = "S_FOREMOST_POLICYINQUIIRY_URL";
	public static final String S_FWS_POLICYLOOKUP_URL = "S_FWS_POLICYLOOKUP_URL";
	public static final String S_POINT_POLICYINQUIIRY_URL = "S_POINT_POLICYINQUIIRY_URL";
	public static final String S_CISCO_SRMCTI_URL = "S_CISCO_SRMCTI_URL";
	public static final String S_KM2_URL = "S_KM2_URL";
	public static final String S_CLEARHARBOR_URL = "S_CLEARHARBOR_URL";
	public static final String S_FWS_SRMCTI_URL = "S_FWS_SRMCTI_URL";
	public static final String S_AFNI_URL = "S_AFNI_URL";
	public static final String S_APEX_URL = "S_APEX_URL";
	public static final String S_COMMERCIAL_SRMCTI_URL = "S_COMMERCIAL_SRMCTI_URL";
	public static final String S_COMMERCIAL_MDM_URL = "S_COMMERCIAL_MDM_URL";
	public static final String S_COMMERCIAL_BIZAGENT_URL = "S_COMMERCIAL_BIZAGENT_URL";
	public static final String S_ACCLINK_ANILOOKUP_URL = "S_ACCLINK_ANILOOKUP_URL";
	public static final String S_COMMERCIAL_BILLING_SUMMARY = "S_COMMERCIAL_BILLING_SUMMARY";
	public static final String S_ACCNUM = "S_ACCNUM";
	public static final String S_POLICY_LIST = "S_POLICY_LIST";
	public static final String S_COMMBIZ_AGENTLOOKUP_URL = "S_COMMBIZ_AGENTLOOKUP_URL";
	public static final String S_EPC_PAYMENTUS_GROUP_URL = "S_EPC_PAYMENTUS_GROUP_URL";
	public static final String S_FWS_EPC_PAYMENTUSS_URL = "S_FWS_EPC_PAYMENTUSS_URL";
	public static final String S_WARHEAD_URL = "S_WARHEAD_URL";

	// ADMIN API URL's
	public static final String S_GET_IVR_API_URL = "S_GET_IVR_API_URL";
	public static final String S_CHECK_ANI_EXISTS_URL = "S_CHECK_ANI_EXISTS_URL";
	public static final String S_ANI_GROUP_URL = "S_ANI_GROUP_URL";
	public static final String S_MAIN_DETAILS_URL = "S_MAIN_DETAILS_URL";
	public static final String S_EMERGENCY_DETAILS_URL = "S_EMERGENCY_DETAILS_URL";
	public static final String S_IVR_PROPERTIES_URL = "S_IVR_PROPERTIES_URL";
	public static final String S_BUSSINESS_OBJECTS_URL = "S_BUSSINESS_OBJECTS_URL";
	public static final String S_KYC_LOWTRUST_PROMPTS_URL = "S_KYC_LOWTRUST_PROMPTS_URL";
	public static final String S_PREQUE_MSG_RULES_URL = "S_PREQUE_MSG_RULES_URL";
	public static final String S_ROUTING_URL = "S_ROUTING_URL";
	public static final String S_GET_CALLER_DETAILS_ANI_URL = "S_GET_CALLER_DETAILS_ANI_URL";
	public static final String S_GET_STATE_BY_AREACODE_URL = "S_GET_STATE_BY_AREACODE_URL";
	public static final String S_GET_STATE_KEY_BY_NAME_URL = "S_GET_STATE_KEY_BY_NAME_URL";

	// Menu formation keys

	public static final String _Formation = "_Formation";
	public static final String _Event = "_Event";
	public static final String _DTMF = "_DTMF";
	public static final String _SPEECH = "_SPEECH";
	public static final String S_NOINPUT_PROMPT = "S_NOINPUT_PROMPT";
	public static final String S_NOMATCH_PROMPT = "S_NOMATCH_PROMPT";
	public static final String S_MENUMAP = "S_MENUMAP";

	public static final String MenuID = "MenuID";
	public static final String MenuID_Event = "MenuID_Event";
	public static final String MenuID_InitialPrompt = "MenuID_InitialPrompt";
	public static final String MenuID_NoInputPrompt_1 = "MenuID_NoInputPrompt_1";
	public static final String MenuID_NoInputPrompt_2 = "MenuID_NoInputPrompt_2";
	public static final String MenuID_NoMatchPrompt_1 = "MenuID_NoMatchPrompt_1";
	public static final String MenuID_NoMatchPrompt_2 = "MenuID_NoMatchPrompt_2";
	public static final String MenuID_Tries = "MenuID_Tries";
	public static final String MenuID_DTMF_Grammar = "MenuID_DTMF_Grammar";
	public static final String MenuID_SPEECH_Grammar = "MenuID_SPEECH_Grammar";

	public static final String SIP_MN_001_Formation = "SIP_MN_001_Formation";

	public static final int tenantid = 2;

	public static final String MainMenu_Direct_Transfer = "Direct transfer";
	public static final String MainMenu_Welcome_and_Transfer = "Welcome and Transfer";
	public static final String MainMenu_IVR_Treatment = "IVR Treatment";
	public static final String MainMenu_Disconnect = "Disconnect";
	public static final String Roadside_Assistance_Interval = "Roadside Assistance Interval";
	public static final String AfeterHours_SS_Availble = "ForemostCustomer|ForemostAARP|ForemostAgent|BristolWestProducer|FDS|FWS|BWCustomer|21st";
	public static final String StateGroup = "stategroup";
	public static final String Language = "language";
	public static final String BrandLabel = "brandlabel";
	public static final String AnigroupHandling = "anigrouphandling";
	public static final String PolicySegmentation = "policysegmentation";
	public static final String AgentSegmentation = "agentsegmentation";
	public static final String PolicySource = "policysource";
	public static final String PolicyAttributes = "policyattributes";

	public static final String SANI_PA_001_WAV = "SANI_PA_001_WAV";
	public static final String SANI_PA_001_TTS = "SANI_PA_001_TTS";
	public static final String EM_PA_001_WAV = "EM_PA_001_WAV";
	public static final String EM_PA_001_TTS = "EM_PA_001_TTS";
	public static final String EM_PA_002_WAV = "EM_PA_002_WAV";
	public static final String EM_PA_002_TTS = "EM_PA_002_TTS";
	public static final String EM_MN_001_WAV = "EM_MN_001_WAV";
	public static final String EM_MN_001_TTS = "EM_MN_001_TTS";
	public static final String KYCLTM_PA_004 = "KYCLTM_PA_004";
	public static final String STR_PA_004_WAV = "STR_PA_004_WAV";
	public static final String STR_PA_004_TTS = "STR_PA_004_TTS";
	public static final String STR_PA_001_WAV = "STR_PA_001_WAV";
	public static final String STR_PA_001_TTS = "STR_PA_001_TTS";
	public static final String STR_PA_002 = "STR_PA_002";
	public static final String STR_PA_003_WAV = "STR_PA_003_WAV";
	public static final String STR_PA_003_TTS = "STR_PA_003_TTS";
	public static final String Entry_HOOP_PA_001_WAV = "Entry_HOOP_PA_001_WAV";
	public static final String Entry_HOOP_PA_001_TTS = "Entry_HOOP_PA_001_TTS";
	public static final String HOOP_PA_001_WAV = "HOOP_PA_001_WAV";
	public static final String HOOP_PA_001_TTS = "HOOP_PA_001_TTS";
	public static final String WH_PA_001_WAV = "WH_PA_001_WAV";
	public static final String WH_PA_001_TTS = "WH_PA_001_TTS";

	// GDF
	public static final String NOMATCH = "NOMATCH";
	public static final String NOINPUT = "NOINPUT";
	public static final String value = "value";
	public static final String agent = "agent";
	public static final String Idontknow = "I dont know";
	public static final String MENU_CONFIG_FILE = "MenuConfig.properties";

	public static final String KYCMF_MN_001_VALUE = "KYCMF_MN_001_VALUE";
	public static final String CMMF_MN_001_VALUE = "CMMF_MN_001_VALUE";
	public static final String CMMF_MN_002_VALUE = "CMMF_MN_002_VALUE";
	public static final String CAIF_MN_001_VALUE = "CAIF_MN_001_VALUE";

	public static final String CMMF_MN_001_MakeAPayment = "Make a Payment";
	public static final String CMMF_MN_001_AccInfo = "Account information";
	public static final String CMMF_MN_001_MailingAddress = "Mailing address";
	public static final String CMMF_MN_001_WebsiteInfo = "Website";
	public static final String CMMF_MN_001_FarmersRealTImeMailing = "Farmers Real Time Billing";
	public static final String CMMF_MN_001_OtherBilling = "Other Billing";
	public static final String CMMF_MN_002_PaymentMailingAddress = "Payment Mailing";
	public static final String CMMF_MN_002_GenCorr = "General correspondence";
	public static final String CBIZ_MN_001_DialAnExtension = "Dial an Extension";
	public static final String CBIZ_MN_001_PropertyAndCasualty = "Property and Casualty";
	public static final String CBIZ_MN_001_WorkersComp = "Workers Compensation";
	public static final String CBIZ_MN_001_BillingandReinstatement = "billing and Reinstatements";
	public static final String CBIZ_MN_001_RealTimeBilling = "Real Time Billing";
	public static final String CBIZ_MN_001_ContactInformation = "Contact Information";
	public static final String CBIZ_MN_002_AppetiteEligibility = "Appetite Eligibility";
	public static final String CBIZ_MN_002_Endorsement = "Endorsement";
	public static final String CBIZ_MN_002_Lienholder = "Lienholder";
	public static final String CBIZ_MN_002_Navigation = "Navigation";
	public static final String CBIZ_MN_002_SignatureForms = "Signature Forms";
	public static final String CBIZ_MN_002_SomethingElse = "Something Else";
	public static final String CBIZ_MN_003_RealTimeBilling = "Real Time Billing";
	public static final String CBIZ_MN_003_AppetiteEligibility = "Appetite Eligibility";
	public static final String CBIZ_MN_003_Audit = "Audit";
	public static final String CBIZ_MN_003_SomethingElse = "Something Else";
	public static final String CBIZB_MN_001_RealTimeBilling = "Farmers Real Time Billing";
	public static final String CBIZB_MN_001_OtherBilling = "Other Billing";
	public static final String CBIZB_MN_001_StandardPay = "Standard Pay";
	public static final String CBIZB_MN_002_InvoiceandElectronicPayments = "Invoice and Electronic Payments ";
	public static final String CBIZB_MN_002_Reinstatement = "Reinstatement";
	public static final String CBIZB_MN_002_AddressandPaymentPlanChanges = "Address and Payment Plan Changes";
	public static final String CBIZB_MN_002_SomethingElse = "Something Else";
	public static final String CBIZB_MN_002_StandardPay = "Standard Pay";
	public static final String CBIZID_MN_001_NoAgentId = "No Agent ID";
	public static final String CAIF_MN_001_MakeAPayment = "Make A Payment";
	public static final String CAIF_MN_002_MainMenu = "Main Menu";
	public static final String CAIF_MN_002_DifferentAccount = "Different Account";
	public static final String CIDA_MN_001_RepresentativeRequest = "RepresentativeRequest";
	public static final String CIDA_MN_001_AccountNumberProvided = "AccountNumberProvided";
	public static final String CIDA_MN_001_Dontknow = "Dontknow";
	public static final String CIDA_MN_003_NoZip = "NoZip";

	public static final String S_SPEECH_MN_NM_TRUE_FLAG = "N";

	public static final String S_URL_POLICYNUM = "S_URL_POLICYNUM";
	public static final String S_URL_ACCNUM = "S_URL_ACCNUM";
	public static final String S_COMMERCIAL_BILLING_SUMMARY_POLICYNUM_URL = "S_COMMERCIAL_BILLING_SUMMARY_POLICYNUM_URL";
	public static final String S_COMMERCIAL_BILLING_SUMMARY_ACCNUM_URL = "S_COMMERCIAL_BILLING_SUMMARY_ACCNUM_URL";
	public static final String S_COMMERCIAL_BILLING_JSON = "S_COMMERCIAL_BILLING_JSON";
	public static final String S_API_ACCNUM = "S_API_ACCNUM";
	public static final String S_IS_ACCNO_PROVIDED = "S_IS_ACCNO_PROVIDED";
	public static final String S_IS_POLICYNUM_PROVIDED = "S_IS_POLICYNUM_PROVIDED";

	public static final String CIDA_HOST_001_GreaterThan1Acc_Resp = "GreaterThan1Account";
	public static final String CIDA_HOST_001_NoAccFound_PolicyNumberAsked_Resp = "NoAccFound_PolicyNumberAsked";
	public static final String CIDA_HOST_001_NoAccFound_Resp = "NoAccountFound";
	public static final String CIDA_HOST_001_AccountfoundbyPolicyNumber_Resp = "AccountfoundbyPolicyNumber";
	public static final String CIDA_HOST_001_AccountfoundbyAccNumber_Resp = "AccountNumberFound";

	public static final String VXMLParam1 = "VXMLParam1";
	public static final String VXMLParam2 = "VXMLParam2";
	public static final String VXMLParam3 = "VXMLParam3";
	public static final String VXMLParam4 = "VXMLParam4";
	public static final String VXMLParam5 = "VXMLParam5";
	public static final String TRANSFER_ACTION_PLAY_AND_DISCONNECT = "Play and Disconnect";
	public static final String TRANSFER_ACTION_DISCONNECT = "Disconnect";
	public static final String TRANSFER_ACTION_TRANSFER = "Transfer";

	public static final String AUTO_PRODUCTTYPECOUNT_KYC = "AUTO_PRODUCTTYPECOUNT_KYC";
	public static final String HOME_PRODUCTTYPECOUNT_KYC = "HOME_PRODUCTTYPECOUNT_KYC";
	public static final String RV_PRODUCTTYPECOUNT_KYC = "RV_PRODUCTTYPECOUNT_KYC";
	public static final String UMBRELLA_PRODUCTTYPECOUNT_KYC = "UMBRELLA_PRODUCTTYPECOUNT_KYC";
	public static final String MR_PRODUCTTYPECOUNT_KYC = "MR_PRODUCTTYPECOUNT_KYC";
	public static final String SP_PRODUCTTYPECOUNT_KYC = "SP_PRODUCTTYPECOUNT_KYC";
	public static final String IS_MORETHAN_ONE_POLICY_PRODUCTTYPE = "IS_MORETHAN_ONE_POLICY_PRODUCTTYPE";
	public static final String ACCLINKANIJSONRESPSTRING = "ACCLINKANIJSONRESPSTRING";
	// public final static String S_TID="S_TID";
	public final static String S_ACCOUNT_NUMBER = "S_ACCOUNTNUMBER";
	public final static String S_ORIGINAL_DNIS_DESCRIPTION = "S_ORIGINAL_DNIS_DESCRIPTION";
	public final static String S_QUOTE_NUMBER = "S_QUOTE_NUMBER";
	// Kirthik Change
	public static final String KYCBA_MN_001_Dontknow = "I Dont Know";
	public static final String DoNotMakePayment = "Do Not Make Payment";
	public static final String IS_PEELOFF_MENU_OPTED = "IS_PEELOFF_MENU_OPTED";
	public static final String MAXNOINPUT = "max_noinput";
	public static final String MAXERROR = "max_error";
	public static final String MAXNOMATCH = "max_nomatch";
	public static final String MAXCOMBINEDTRIES = "max_combinedtries";
	public static final String NO_SELECTION = "No_Selection";
	public static final String CLAIMS = "Claims";
	public static final String BILLING_ENQUIRIES = "Billing_Enquiries";
	public static final String REVIEW_POLICY = "Review_Policy";

	// Mustan KYC Number of policies

	public static final String NONE_OF_THESE = "NoneOfThese";
	public static final String FILTERED_POLICIES = "FILTERED_POLICIES";
	public static final String PREVIOUS_MENU_BRANDCHECK = "PREVIOUS_MENU_BRANDCHECK";
	public static final String KYCNP_MN_002_MENUNAME = "KYCNP_MN_002";
	public static final String KYCNP_MN_004_MENUNAME = "KYCNP_MN_004";
	public static final String S_FINAL_BRAND = "S_FINAL_BRAND";
	public static final String FINALPOLICYOBJECT_KYCNP = "FINALPOLICYOBJECT_KYCNP";
	public static final String S_API_BU_BW = "BW";
	public static final String S_API_BU_FDS_PLA = "PLA";
	public static final String S_API_BU_FDS_GWPC = "GWPC";
	public static final String S_API_BU_FWS_ARS = "FWS-ARS";
	public static final String S_API_BU_FWS_A360 = "FWS-A360";
	public static final String S_API_BU_FDS = "FDS";
	public static final String S_API_BU_FM = "FM";
	public static final String S_API_BU_21C = "21C";
	public static final String S_API_FINAL_POLICY_NUMBER = "S_API_FINAL_POLICY_NUMBER";
	public static final String ONE_TO_FIVE_ALLOFTHESE = "One_To_Five_AllOfThese";
	public static final String PRODUCTTYPE_AUTO = "AUTO";
	public static final String PRODUCTTYPE_HOME = "HOME";
	public static final String PRODUCTTYPE_RV = "RV";
	public static final String PRODUCTTYPE_MR = "MR";
	public static final String PRODUCTTYPE_SP = "SP";
	public static final String PRODUCTTYPE_UMBRELLA = "UMBRELLA";

	// Hari FWS - Set-2
	public static final String S_FWS_BILLING_LOOKUP_URL = "S_FWS_BILLING_LOOKUP_URL";
	public static final String S_FWS_POLICY_LOOKUP_URL = "S_FWS_POLICY_LOOKUP_URL";
	public static final String S_FWS_IA_INPUT_TYPE = "S_FWS_IA_INPUT_TYPE";
	public static final String FWS_ZIP_MATCH = "ZIPCODE_MATCH";
	public static final String FWS_ZIP_NO_MATCH = "ZIPCODE_NO MATCH";
	public static final String S_FWS_ZIP_CODE = "S_FWS_ZIP_CODE";

	// POLICY LOOKUP API RESPONSE SESSION NAME
	public static final String S_FWS_POLICY_LOB = "S_FWS_POLICY_LOB";
	public static final String S_FWS_INT_POLICY_NO = "S_FWS_INT_POLICY_NO";
	public static final String S_FWS_POLICY_SUFFIX = "S_FWS_POLICY_SUFFIX";
	public static final String S_FWS_POLICY_GPC = "S_FWS_POLICY_GPC";
	public static final String S_FWS_POLICY_EFF_DATE = "S_FWS_POLICY_EFF_DATE";
	public static final String S_FWS_POLICY_COMPANY_CODE = "S_FWS_POLICY_COMPANY_CODE";
	public static final String S_FWS_POLICY_BILLING_ACCT_NO = "S_FWS_POLICY_BILLING_ACCT_NO";

	// BILLING LOOKUP API RESPONSE SESSION NAME
	public static final String S_FWS_BILLING_ACTIVITY_CD = "S_FWS_BILLING_ACTIVITY_CD";
	public static final String S_FWS_BILLING_PROD_DIST_CD = "S_FWS_BILLING_PROD_DIST_CD";

	// MSP
	public static final String S_PAK_II_OPEN = "S_PAK_II_OPEN";
	public static final String FWSARC_MN_001 = "FWSARC_MN_001";
	public static final String FWSARC_MN_002 = "FWSARC_MN_002";
	public static final String FWSARC_PWD_RESET = "PASSWORD RESET";
	public static final String FWSARC_NO_INPUT = "NO INPUT";
	public static final String FWSARC_NO_MATCH = "ALL OTHER SELECTIONS";
	public static final String FWSARS_POLICIES = "ARS POLICIES";
	public static final String FWSARS_NEW_BUSINESS = "ARS NEW BUSINESS";
	public static final String FWSARC_AGENT_360_POLICIES = "AGENT 360 POLICIES";
	public static final String FWSARC_AGENT_360_NEW_BUSINESS = "AGENT 360 NEW BUSINESS";
	public static final String FWSARC_PAK_II_OPEN = "PAK II OPEN";
	public static final String FWSARC_PAK_II_CLOSED = "PAK II CLOSED";
	public static final String FWSARC_PWRESET = "PASSWORD RESET";
	public static final String FWSARC_CLAIMS = "CLAIMS";

	// START : FWS Commissions flow
	public static final String FWSC_MN_001 = "FWSC_MN_001";
	public static final String FWSC_COMMISSION = "COMMISSION";
	public static final String FWSC_INDEPENDENT_AGENT = "INDEPENDENT AGENT";
	public static final String FWSC_PARTY_EXTENSION = "PARTY EXTENSION";
	public static final String FWSCM_MN_002 = "FWSCM_MN_002";
	public static final String FWSCM_BROKER_INQUIRY = "BROKER INQUIRY";
	public static final String FWSCM_BROKER_ISSUE = "BROKER ISSUE";
	// END : FWS Commissions flow

	/**
	 * NLU Change
	 */
	// App Tag Config File
	public static final String APPTAG_CONFIG_PROP_FILE = "AppTag_Details.csv";
	// App Tag Config Session Data
	public static final String A_MAP_APPTAG_CONFIG = "S_MAP_APPTAG_CONFIG";
	public static final String APPTAG = "APPTAG";
	public static final String APPTAG_SCORE = "APPTAG_SCORE";
	public static final String IS_AUTHREQ = "IS_AUTHREQ";
	public static final String IS_IMMEDIATE_TXF = "IS_IMMEDIATE_TXF";
	public static final String IS_CLARIFIER = "IS_CLARIFIER";
	public static final String NEXT_ACTION = "NEXT_ACTION";
	public final static String AVAILABLE = "AVL";
	public final static String CLARIFIER_PP = "CLARIFIER_PP";

	public final static String HYPHEN_SEPERATOR = "\\-";
	public final static String COLON_SEPERATOR = "\\:";
	public final static String UNDERSCORE = "_";

	public final static String AppTagTraversal = "AppTagTraversal";
	public final static String NLUResult = "NLUResult";
	public final static String Success = "Success";
	public final static String NLU_FAILURE = "NLU_FAILURE";
	public final static String NLU = "NLU";
	/**
	 * NLU Change
	 */

	// Screen pop exit state
	public static final String FOREMOST_SERVICE_FOREMOST_CISCO = "Foremost Service_Foremost Cisco";
	public static final String FARMERS_FARMERS_CISCO = "Farmers_Farmers Cisco";
	public static final String BW_BRISTOL_WEST_CISCO = "BW_Bristol West Cisco";
	public static final String TWENTYFIRST_HAWAII_BRISTOL_WEST_CISCO_HI = "21st Hawaii_Bristol West Cisco HI";
	public static final String TWENTYFIRST_SERVICE_TWENTYFIRST_SERVICE_CISCO = "21st Service_21st Service Cisco";
	public static final String BW_BRISTOL_WEST_KM2 = "BW_Bristol West KM2";
	public static final String FWS_FWS_KM2 = "FWS_FWS KM2";
	public static final String FDS_FARMERS_KM2 = "FDS_Farmers KM2";
	public static final String COMMERCIAL_COMMERCIAL_KM2 = "Commercial_Commercial KM2";
	public static final String BW_BRISTOL_WEST_CH = "BW_Bristol West CH";
	public static final String FWS_FWS = "FWS_FWS";
	public static final String FARMERS_FARMERS_CH = "Farmers_Farmers CH";
	public static final String FORMOST_SERVICE_FOREMOST_CH = "Foremost Service_Foremost CH";
	public static final String FARMERS_FARMERS_AFNI = "Farmers_Farmers AFNI";
	public static final String EXL_FWS = "EXL_FWS";
	public static final String FWS_FWS_AFNI = "FWS_FWS AFNI";
	public static final String FWS_SALES_FWS_APEX_SALES = "FWS Sales_FWS Apex Sales";
	public static final String FOREMOST_SALES_FOREMOST_SRM = "Foremost Sales_Foremost SRM";
	public static final String TWENTYFIRST_SALES_21stC = "21st Sales_21stC";
	public static final String COMMERCIAL_COMMERCIAL_CISCO = "Commercial_Commercial Cisco";
	public static final String COMMERCIAL_4BIZ_COMMERCIAL_BIZ = "Commercial 4BIZ_Commercial Biz";

	// MSP keys for HOST
	public static final String ACTIVE_RTB_ACCOUNT = "ACTIVE RTB ACCOUNT";
	public static final String API_FAILURE = "API FAILURE";
	public static final String FAILURE = "FAILURE";
	public static final String ELITE = "ELITE";
	public static final String NON_ELITE = "NON ELITE";
	public static final String PAYMENT_BLOCKED = "PAYMENT BLOCKED";
	public static final String DEFAULT = "DEFAULT";
	public static final String SUCCESS = "SUCCESS";

	public static final String MEDIUM = "MEDIUM";
	public static final String HIGH = "HIGH";
	public static final String LOW = "LOW";
	public static final String exceptiondayoverrides = "exceptiondayoverrides";

	public static final String A_REPORT_DATA_PATH = "A_REPORT_DATA_PATH";
	public static final String REPRESENTATIVE = "Representative";
	public static final String MAKE_A_PAYMENT = "MAKE_A_PAYMENT";

	public static final String S_IS_ELITE = "S_IS_ELITE";
	public static final String CBIZ_MN_001_ExitState = "CBIZ_MN_001_ExitState";
	public static final String CBIZ_MN_001_DirectBilling = "Direct Billing";
	public static final String CMMF_MN_001_Make_a_Payment = "CMMF_MN_001_Make_a_Payment";

	public static final String S_POST_QUEUE_MSG_ENABLED = "S_POST_QUEUE_MSG_ENABLED";
	public static final String S_PRE_QUEUE_MSG_ENABLED = "S_PRE_QUEUE_MSG_ENABLED";

	// SABARI
	public static final String S_ACCOUNTLINKANILOOKUPURL = "S_ACCOUNTLINKANILOOKUPURL";
	public static final String S_TELEPHONENUMBER = "S_TELEPHONUMBER";
	public static final String S_USERID = "S_USERID";
	public final static String ACElookupAPI = "ACElookupAPI";

	public static final String BILLPRESENTMENTRETRIEVEBILLINGSUMMARYURL = "BillPresentmentretrieveBillingSummaryURL";
	public static final String POLICYNUMBER = "PolicyNumber";
	public static final String ID = "id";
	public static final String CUSTOMERID = "customerid";
	public static final String PRODUCTGROUPNAME = "productgroupname";

	public static final String IVRCALLID = "IVRCallID";
	public static final String DESTINATIONAPP = "DestinationApp";
	public static final String TOLLFREENUMBER = "TollFreeNumber";
	public static final String POPTYPE = "PopType";
	public static final String CALLERANI = "CallerANI";
	public static final String CALLEDINTO = "CalledInto";
	public static final String LANGUAGE = "Language";
	public static final String CALLEDINTIME = "CalledInTime";
	public static final String TOLLFREEDESCRIPTION = "TollFreeDescription";
	public static final String TRANSFERREASON = "TransferReason";
	public static final String INTENT = "Intent";
	public static final String BRAND = "Brand";
	public static final String IVR2TEXTURL = "IVR2TextURL";
	public static final String INPUT = "Input";
	public static final String S_POINTGENERALBILLINGURL = "S_POINTGENERALBILLINGURL";
	public static final String S_EPCHALO_BILLING_DETAILSGROUP_URL = "S_EPCHALO_BILLING_DETAILSGROUP_URL";
	public static final String S_POLICYNUMBER = "S_POLICYNUMBER";
	public static final String S_ACTORTYPE = "S_ACTORTYPE";
	public static final String S_FWSPOLICY_LOOKUPURL = "S_FWSPOLICY_LOOKUPURL";
	public static final String S_PRODUCT_GROUP_NAME = "S_PRODUCT_GROUP_NAME";
	public static final String S_CUSTOMERID = "S_CUSTOMERID";
	public static final String S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL = "S_BILL_PRESENTMENT_RETRIEVE_BILLINGSUMMARY_URL";
	public static final String S_FWSPOLICY_LOOKUP_CLIENTID = "S_FWSPOLICY_LOOKUP_CLIENTID";
	public static final String S_FWSPOLICY_LOOKUP_CLIENTSECRET = "S_FWSPOLICY_LOOKUP_CLIENTSECRET";
	public static final String S_POLICY_CONTRACT_NUMBERS = "S_POLICY_CONTRACT_NUMBERS";
	public static final String S_POINTGENERAL_BILLING_URL = "S_POINTGENERAL_BILLING_URL";
	public static final String S_API_POLICY_SOURCE = "S_API_POLICY_SOURCE";
	public static final String S_API_POLICY_STATE = "S_API_POLICY_STATE";
	public static final String S_API_POLICY_POLICYNUMBER = "S_API_POLICY_POLICYNUMBER";
	public static final String S_API_POLICY_EFFECTIVEDATE = "S_API_POLICY_EFFECTIVEDATE";
	public static final String S_API_POLICY_SUFFIX = "S_API_POLICY_SUFFIX";
	public static final String S_API_POLICY_BILLINGACNUMB = "S_API_POLICY_BILLINGACNUMB";
	public static final String TENANTID = "TENANTID";
	public static final String AREACODESURL = "AREACODESURL";
	public static final String AREACODE = "AREACODE";
	public static final String NAME = "NAME";
	public static final String KEY = "KEY";
	public static final String STATEGROUPURL = "STATEGROUPURL";
	public static final String GETAFTERHOURSURL = "GETAFTERHOURSURL";
	public static final String ROUTINGURL = "ROUTINGURL";
	public static final String ROUTINGGURL = "ROUTINGGURL";
	public static final String BUSINESSOBJURL = "BUSINESSOBJURL";
	public static final String BUSINESSGRUPOBJURL = "BUSINESSGRUPOBJURL";
	public static final String S_KEY = "S_KEY";
	public static final String ANIGROUPURL = "ANIGROUPURL";
	public static final String MAINTABLEURL = "MAINTABLEURL";
	public static final String S_AGENTPOST_URL = "S_AGENTPOST_URL";
	public static final String S_PHONENUMBER = "S_PHONENUMBER";
	public static final String S_TELE_PHONE_NUMBER = "S_TELE_PHONE_NUMBER";
	public static final String S_ZIP = "S_ZIP";

	public static final String S_MASTERCOMPANYCODE = "S_MASTERCOMPANYCODE";
	public static final String S_DELIVERY_METHOD = "S_DELIVERY_METHOD";
	public static final String S_SIZE = "S_SIZE";
	public static final String S_FAX_NUMBER = "S_FAX_NUMBER";
	public static final String S_EMAILADDRESS = "S_EMAILADDRESS";
	public static final String S_POINTERID_URL = "S_POINTERID_URL";
	public static final String S_FIRSTNAME = "S_FIRSTNAME";
	public static final String S_MIDDLENAME = "S_MIDDLENAME";
	public static final String S_LASTNAME = "S_LASTNAME";
	public static final String S_INTERNAL_POLICY_VERSION = "S_INTERNAL_POLICY_VERSION";
	public static final String S_GetStatePropertyTableByBusinessObjects_URL = "S_GetStatePropertyTableByBusinessObjects_URL";
	public static final String S_TENTEDID = "S_TENTEDID";
	public static final String S_TID = "S_TID";
	public static final String S_ACCOUNT_LOOKUPURL = "S_ACCOUNT_LOOKUPURL";
	public static final String CMMF_MN_002_MainMenu = "Main Menu";
	public static final String CMMF_MN_002_DifferentAccount = "Different Account";

	public static final String S_FARMERS_SALES_QUOTE_WHICH_MENU = "S_FARMERS_SALES_QUOTE_WHICH_MENU";
	public static final String S_CONSENT_PROVIDED = "S_CONSENT_PROVIDED";

	// SET-2 SABARI
	public static final String MAINMENU = "Main Menu";
	public static final String REPRENSTATIVE = "Representative";
	public static final String AGENT = "Agent";
	public static final String AGENTINFORMATIONS = "Agent Information";
	public static final String BILLINGANDPAYMENTS = "Billing and Payments";
	public static final String IDCARDS = "ID Cards";
	public static final String SALESQUOTES = "Sales or Quotes";
	public static final String CANCELPOLICY = "Cancel Policy";
	public static final String WEBSITEHELP = "Website Help";
	// SAHM_MN_001

	public static final String BYFAX = "";
	public static final String POLICYCHANGE = "Policy Change";
	public static final String POLICYQUESTION = "Policy Question";
	public static final String DONTHAVE = "Dont have it";
	public static final String IVR2TEXTByIntentAPI = "IVR2TEXTByIntentAPI";
	public static final String RV_PRODUCTTYPECOUNT_SHAUTH = "0";
	public static final String UMBRELLA_PRODUCTTYPECOUNT_SHAUTH = "0";
	public static final String MR_PRODUCTTYPECOUNT_SHAUTH = "0";
	public static final String SP_PRODUCTTYPECOUNT_SHAUTH = "0";
	public static final String AUTO_PRODUCTTYPECOUNT_SHAUTH = "AUTO_PRODUCTTYPECOUNT_SHAUTH";
	public static final String HOME_PRODUCTTYPECOUNT_SHAUTH = "HOME_PRODUCTTYPECOUNT_SHAUTH";
	public static final String PRODUCTTYPE_AUTOA = "A";
	public static final String S_NOOF_POLICIES_LOOKUP = "S_NOOF_POLICIES_LOOKUP";
	public static final String PRODUCTTYPE_AUTOP = "AUTOP";
	public final static String S_INTENET = "S_INTENET";

	// Menu names
	public final static String IVRTT_MN_001_VALUE = "IVRTT_MN_001_VALUE";
	public final static String SACI_MN_001_VALUE = "SACI_MN_001_VALUE";
	public final static String SAHM_MN_001_VALUE = "SAHM_MN_001_VALUE";
	public final static String SAHM_MN_002_VALUE = "SAHM_MN_002_VALUE";
	public final static String SIDA_MN_001_VALUE = "SIDA_MN_001_VALUE";
	public final static String SIDA_MN_002_VALUE = "SIDA_MN_002_VALUE";
	public final static String SIDA_MN_003_VALUE = "SIDA_MN_003_VALUE";
	public final static String SIDA_MN_004_VALUE = "SIDA_MN_004_VALUE";
	public final static String SIDA_MN_005_VALUE = "SIDA_MN_005_VALUE";
	public final static String SIDA_MN_006_VALUE = "SIDA_MN_006_VALUE";
	public final static String SIDA_MN_007_VALUE = "SIDA_MN_007_VALUE";
	public final static String SIDC_MN_001_VALUE = "SIDC_MN_001_VALUE";
	public final static String SIDC_MN_002_VALUE = "SIDC_MN_002_VALUE";
	public final static String SIDC_MN_003_VALUE = "SIDC_MN_003_VALUE";
	public final static String SIDC_MN_004_VALUE = "SIDC_MN_004_VALUE";
	public final static String SIDC_MN_005_VALUE = "SIDC_MN_005_VALUE";
	public final static String SMM_MN_001_VALUE = "SMM_MN_001_VALUE";
	public final static String SMM_MN_002_VALUE = "SMM_MN_002_VALUE";
	public final static String SMM_MN_003_VALUE = "SMM_MN_003_VALUE";
	public final static String SMM_MN_004_VALUE = "SMM_MN_004_VALUE";
	public final static String SMM_MN_005_VALUE = "SMM_MN_005_VALUE";
	public final static String SPCQ_MN_001_VALUE = "SPCQ_MN_001_VALUE";
	public final static String SPCQ_MN_002_VALUE = "SPCQ_MN_002_VALUE";
	public final static String SQT_MN_001_VALUE = "SQT_MN_001_VALUE";
	public final static String SQT_MN_002_VALUE = "SQT_MN_002_VALUE";
	public final static String CBIZ_MN_001_VALUE = "CBIZ_MN_001_VALUE";
	public final static String CBIZ_MN_002_VALUE = "CBIZ_MN_002_VALUE";
	public final static String CBIZ_MN_003_VALUE = "CBIZ_MN_003_VALUE";
	public final static String CBIZ_MN_004_VALUE = "CBIZ_MN_004_VALUE";
	public final static String CBIZB_MN_001_VALUE = "CBIZB_MN_001_VALUE";
	public final static String CBIZB_MN_002_VALUE = "CBIZB_MN_002_VALUE";
	public final static String CBIZID_MN_001_VALUE = "CBIZID_MN_001_VALUE";
	public final static String CIDA_MN_001_VALUE = "CIDA_MN_001_VALUE";
	public final static String CIDA_MN_002_VALUE = "CIDA_MN_002_VALUE";
	public final static String CIDA_MN_003_VALUE = "CIDA_MN_003_VALUE";
	public final static String CIDA_MN_004_VALUE = "CIDA_MN_004_VALUE";
	public final static String EPCUS_MN_001_VALUE = "EPCUS_MN_001_VALUE";
	public final static String KYCAF_MN_001_VALUE = "KYCAF_MN_001_VALUE";
	public final static String KYCAF_MN_002_VALUE = "KYCAF_MN_002_VALUE";
	public final static String KYCAF_MN_003_VALUE = "KYCAF_MN_003_VALUE";
	public final static String KYCBA_MN_001_VALUE = "KYCBA_MN_001_VALUE";
	public final static String KYCCF_MN_001_VALUE = "KYCCF_MN_001_VALUE";
	public final static String KYCNP_MN_001_VALUE = "KYCNP_MN_001_VALUE";
	public final static String KYCNP_MN_002_VALUE = "KYCNP_MN_002_VALUE";
	public final static String KYCNP_MN_003_VALUE = "KYCNP_MN_003_VALUE";
	public final static String KYCNP_MN_004_VALUE = "KYCNP_MN_004_VALUE";
	public final static String SD_MN_001_VALUE = "SD_MN_001_VALUE";
	public final static String SD_MN_002_VALUE = "SD_MN_002_VALUE";
	public final static String SGETSR_MN_001_VALUE = "SGETSR_MN_001_VALUE";
	public final static String SIP_MN_001_VALUE = "SIP_MN_001_VALUE";

	// Saikiran

	public static final String S_AGENTINFO_URL = "S_AGENTINFO_URL";
	public static final String S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL = "S_BW_BILLING_POLICY_INQUIRY_GENERAL_URL";
	public static final String S_BW_BILLING_POLICY_INQUIRY_PAYMENT_URL = "S_BW_BILLING_POLICY_INQUIRY_PAYMENT_URL";
	public static final String S_STATEGROUPTABLE_URL = "S_STATEGROUPTABLE_URL";
	public static final String S_BRANDSTABLE_URL = "S_BRANDSTABLE_URL";
	public static final String S_EPC_BRANDS_URL = "S_EPC_BRANDS_URL";
	// public static final String S_TID = "S_TID";
	public static final String S_POLICY_SOURCE_FOREMOST = "Foremost";
	public static final String S_POLICY_NUM = "S_POLICY_NUM";
	public static final String NOT_EMPTY = "Not Empty";
	public static final String EMPTY = "Empty";
	public static final String POLICY_FOUND = "Policy Found";
	public static final String POLICY_NOT_FOUND = "Policy Not Found";
	public static final String SYS_NAME = "VRS";
	public static final String FUNCTION_NAME_GENERAL = "PolicyLookupGeneral";
	public static final String FUNCTION_NAME_PAYMENTS = "PolicyLookupPayments";
	public static final String POSTAL_CODE = "S_POSTAL_CODE";
	public static final String AGREEMENT_MODE = "AGREEMENT_MODE";
	public static final String AGREEMENT_SYMBOL = "AGREEMENT_SYMBOL";
	public static final String BILLING_INFO = "billingInfo";
	public static final String BILLING_DETAILS = "billingDetails";
	public static final String POLICY_DETAILS = "policyDetails";
	public static final String RENEWAL_POLICY_BILLING_DETAILS = "renewalPolicyBillingDetails";
	public static final String RENEWAL_POLICY_DETAILS = "renewalPolicyDetails";
	public static final String CALL_ID = "callid";
	public static final String S_STATEGROUP_KEY = "S_STATEGROUP_KEY";
	public static final String ALL_OTHER_SELECTIONS = "All Other Selections";
	public static final String LOOKUP_ANOTHER_POLICY = "Lookup Another Policy";
	public static final String OTHER_BILLING_QUESTIONS = "Other Billing Questions";
	public static final String MAIN_MENU = "Main Menu";
	public static final String LIENHOLDER = "Lienholder";
	public static final String CUSTOMER_BILLING = "Customer Billing";
	public static final String POLICY_STATUS = "Policy Status";
	public static final String REFUNDS = "Refunds";
	public static final String COMMISSIONS = "Commissions";
	public static final String OTHER_BILLING = "Other Billing";
	public static final String NEXT = "Next";
	public static final String TECHNICAL_SUPPORT = "TechnicalSupport";
	public static final String AGENCY_CONTRACT_APPOINTMENT = "Agency Contracting and Appointments";
	public static final String BILLING = "Billing";
	public static final String POLICY_QUESTIONS = "Policy Questions";
	public static final String BILLING_QUESTIONS = "Billing Questions";
	public static final String EXTENSION = "Extension";
	public static final String GOT_POLICY = "Got Policy";
	public static final String NO_POLICY_FOUND = "No Policy Found";
	public static final String MOBILE_HOME = "Mobile Home";
	public static final String MOTORCYCLE = "Motorcycle";
	public static final String ALL_OTHER_PRODUCTS = "All Other Products";
	public static final String SPECIALTY_DWELLING = "Specialty Dwelling";
	public static final String QUOTES = "Quotes";
	public static final String Make_a_Payment = "Make a Payment";
	public static final String EXISTING_POLICY = "Existing Policy";
	public static final String WEBSITE_HELP = "Website Help";
	public static final String CANCELLATIONS = "Cancellations";
	public static final String ID_CARDS = "ID Cards";
	public static final String MOTOR_HOME = "Motor Home";
	public static final String OFF_ROAD_VEHICLE = "Off Road Vehicle";
	public static final String NEW_HANDLING = "New Handling";
	public static final String OPT_OUT = "Opt-out";
	public static final String LAND_LORD = "Landlord";
	public static final String VACANT = "Vacant";
	public static final String SEASONAL_HOME = "Seasonal Home";
	public static final String TRAVEL_TRAILER = "Travel Trailer";
	public static final String COLLECTED = "Collected";
	public static final String NOT_COLLECTED = "Not Collected";
	public static final String BOAT = "Boat";
	public static final String WATERCRAFT = "Watercraft";
	public static final String POLICY_ASSISTANCE = "Policy Assistance";
	// public static final String AGENT = "Agent";
	public static final String CUSTOMER = "Customer";
	public static final String AUTO = "Auto";
	public static final String SERVICE = "Service";
	public static final String SALES = "Sales";
	public static final String POLICY_CHANGES = "Policy Changes";
	public static final String SUCCESSFUL = "Successful";
	public static final String BW_BILLING = "BW Billing";
	public static final String SPECIALTY_BILLING = "Specialty Billing";
	public static final String BILLING_STATUS = "Billing Status";
	public static final String PAYMENT_ADDRESS = "Payment Address";
	public static final String REPEAT = "Repeat";
	public static final String OTHER = "Other";
	public static final String S_MENU_SELECTION_KEY = "S_MENU_SELCTION_KEY";
	public final static String BW_CLAIMS = "BW Claims";
	public final static String SPECIALTY_CLAIMS = "Specialty Claims";
	public final static String SPECIALTY = "Specialty";
	public static final String S_BRANDS_TABLE_URL = "S_BRANDSTABLE_URL";
	public final static String S_AreaCode = "S_AreaCode";
	public static final String OTHER_BILLING_QUESTIONS_AGENT = "Other Billing Questions Agent";
	public static final String OTHER_BILLING_QUESTIONS_CUSTOMER = "Other Billing Questions Customer";
	public static final String REPRESENTATIVE_IF_AGENT = "Representative If Agent";
	public static final String REPRESENTATIVE_IF_CUSTOMER = "Representative If Customer";
	public static final String BOAT_OR_WATERCRAFT = "Boat or Watercraft";

	// JayaSurya
	public static final String BANK_ROUTING_NUMBER = "9 DIGIT BANK ROUTING NUMBER";
	public static final String S_SAVING = "Saving";
	public static final String S_CHECKING = "Checking";
	public static final String S_RECORDFIRSTANDLASTNAME = "FIRST AND LAST NAME";
	public static final String S_CUSTOMER = "Customer";
	public static final String S_LIENHOLDER = "Lineholder";
	public static final String S_REPRESENTATIVE = "Representative";
	public static final String S_DEFAULT = "Default";
	public static final String S_ANTIQUEAUTO = "Antique Auto";
	public static final String S_MAKEAPAYMENT = "Make a Payment";
	public static final String S_SCHEDULES = "Schedules";
	public static final String BW_POLICY_NUMBER = "BW POLICY NUMBER";
	public static final String S_MOBILE_HOME = "Mobile Home";
	public static final String S_SPECIALTY_DWELLING = "Specialty Dwelling";
	public static final String S_AUTO = "Auto";
	public static final String S_RECREATIONALPRODUCTS = "Recreational Products";
	public static final String S_PAYBYPHONE = "Pay By Phone";
	public static final String S_POLICYCHANGES = "Policy Changes";
	public static final String S_PAYMENTADDRESS = "Payment Address";
	public static final String S_SOMETHINGELSE = "Something Else";
	public static final String S_SALES = "Sales";
	public static final String S_SERVICE = "Service";
	public static final String S_EXTENTION = "Extention";
	public static final String S_AMOUNT = "S_AMOUNT";
	public static final String S_CANCEL = "Cancel It";
	public static final String S_MAKETHEPAYMENT = "Make The Payment";
	public static final String S_CARD_NUMBER = "CARD_NUMBER";
	public static final String S_EXPIRATIONDATE = "EXPIRATIONDATE";
	public static final String S_NAMERECORDED = "NAME RECORDED";
	public static final String S_DIFFERENTCARD = "Card";
	public static final String S_BANKACCOUNT = "Bank Acc";
	public static final String S_MAILINGADDRESS = "Mailing Address";
	public static final String S_CREDITCARD = "Credit Card";
	public static final String S_DEBITCARD = "Debit Card";
	public static final String S_BILLINGDETAILS = "Billing Detials";
	public static final String S_MAINMENU = "Main menu";
	public static final String S_HANGUP = "Hang Up";
	public static final String S_POLICES = "POLICES";
	public static final String POLICIES = "POLICIES";
	public static final String S_INTERMEDIATERINFORMATIONMGMT_URL = "S_INTERMEDIATERINFORMATIONMGMT_URL";
	public static final String S_SYSTEMNAME = "SYSTEM NAME";
	public static final String S_POINTAUTOPOLICYMANAGER_URL = "S_POINTAUTOPOLICYMANAGER_URL";
	public static final String S_SYSTEMDATE = "S_SYSTEMDATE";
	public static final String S_POINTONETIMEPAYMENT_URL = "S_POINTONETIMEPAYMENT_URL";
	public static final String S_PAYMENTAMOUNT = "Payment Amount";
	public static final String S_ADDRESS = "Address";
	public static final String S_POSTALCODE = "Postal Code";
	public static final String S_PAYMENTMETHODTYPE = "PAYMENT METHOD TYPE";
	public static final String S_BACKACCNUMBER = "BANK ACCOUNT NUMBER";
	public static final String S_PAYMENTID = "PAYMENT ID";
	public static final String S_ADDCHARGEAMT = "ADDITIONAL CHARGE AMOUNT";
	public static final String S_CARDEXPDATE = "CARD EXPIRY DATE";
	public static final String S_ADDITIONALCHARGETYPE = "ADDITIONAL CHARGE TYPE";
	public static final String S_BANKACCTYPE = "BANK ACCOUNT TYPE";
	public static final String S_CVVNUMBER = "CVV NUMBER";
	public static final String S_CARDNUMBER = "CARD NUMBER";
	public static final String S_ROUTINGNUMBER = "ROUTING NUMBER";
	public static final String S_POINTVALIDATECARDROUTINGNUM_URL = "S_POINTVALIDATECARDROUTINGNUM_URL";
	public static final String S_ACCOUNTNUMBERLENGTH = "ACCOUNTNUMBERLENGTH";
	public static final String FL = "FAILURE";
	public static final String S_BILLINGZIPCODE = "Z_BILLINGZIPCODE";
	public static final String S_LAST_FOUR_CARDDIGITS = "S_LAST_FOUR_CARDDIGITS";
	// 30-03-24
	public static final String S_BANKACCOUNTNUMBER = "S_BANKACCOUNTNUMBER";
	public static final String S_LAST_FOUR_BANKACCNUMBER = "S_LAST_FOUR_BANKACCNUMBER";
	public static final String S_BANK_ROUTING_NUMBER = "S_BANK_ROUTING_NUMBER";
	public static final String S_POLICYSERVICES = "Policy Services";
	public static final String S_BILLING = "Billing";
	public static final String S_FOREMOSTSTAR = "Foremost Star";
	public static final String S_CLAIMS = "Claims";
	public static final String S_SUPPLIES = "Supplies";
	public static final String S_LINEHOLDER = "Lienholder";

	// BHAVANA
	public final static String PAYMENTS = "Payments";
	public final static String OTHERBILLINGQUESTIONS = "Other Billing Questions";
	public final static String MISAPPLIED_MONEY = "Misapplied Money";
	public final static String INSTALLMENT_QUESTIONS = "Installment Questions";
	public final static String EFT = "EFT";
	public final static String POLICYCHANGES = "Policy Changes";
	public final static String H_DISCOUNTS = "Discounts";
	public final static String H_RATEQUESTIONS = "Rate Questions";
	public final static String ELIGIBILITY = "Eligibility";
	public final static String H_SURCHARGES = "Surcharges";
	public final static String H_UNDERWRITING = "UnderWriting";
	public final static String OUTOFHOUSEHOLD = "Out Of Household";
	public final static String ADDORREMOVEDRIVER = "Add or Remove Driver";
	public final static String CANCELAPOLICY = "Cancel a Policy";
	public final static String PTO = "PTO";
	public final static String UNPAIDTIMEOFF = "Unpaid Time off";
	public final static String BEREAVEMENT = "Bereavement";
	public final static String PARTIALDAY = "Partial Day";
	public final static String FULLDAY = "Full Day";
	public static final String SOMETHINGELSE = "Something Else";
	public static final String DIAL_AN_EXTENSION = "Dial an Extension";
	public static final String NEW_BUSINESS = "New Business";
	public static final String POLICY_SERVICES = "Policy Services";
	public static final String PRE_SALES_SUPPORT = "Pre Sales Support";
	public static final String ANNUITIES = "Annuities";

	// Mathavan

	public final static String List_Of_States = "AL,AK,CA,CO,CT,FL,GA,ID,IN,IA,KS,MD,MI,MN,MO,MS,MT,NE,NJ,NM,NY,ND,OH,OK,OR,PA,SD,TN,TX,UT,VA,WA,WI,WY";
	public final static String S_AGENTINFO_POST_URL = "S_AGENTINFO_POST_URL";
	public static final String FASHM_MN_001_VALUE = "VALUE";
	public static final String FASHM_MN_001_PolicyChanges = "PolicyChanges";
	public static final String FASHM_MN_001_Billing = "Billing";
	public static final String FASHM_MN_001_Eligibility = "Eligibility";
	public static final String FASHM_MN_001_Inspections = "Inspections";
	public static final String FASHM_MN_001_Underwriting = "Underwriting";
	public static final String FASHM_MN_002_VALUE = "VALUE";
	public static final String FASUM_MN_001_Value = "Value";
	public static final String FASHM_MN_002_360_Value = "360_Value";
	public static final String FASHM_MN_002_Cancel_a_Policy = "FASHM_MN_002_Cancel_a_Policy";
	public static final String FASHM_MN_002_Something_Else = "FASHM_MN_002_Something_Else";
	public static final String FASHM_MN_002_Inspections = "FASHM_MN_002_Inspections";
	public static final String FASHM_MN_002_Underwriting = "FASHM_MN_002_Underwriting";
	public static final String FASUM_MN_001_PolicyChanges = "FASUM_MN_001_PolicyChanges";
	public static final String FASUM_MN_001_Billing = "Billing";
	public static final String FASUM_MN_001_Eligibility = "Eligibility";
	public static final String FASUM_MN_001_Underwriting = "Underwriting";
	public static final String FASMM_MN_001_VALUE = "VALUE";
	public static final String FASMM_MN_001_Home = "Home";
	public static final String FASMM_MN_001_Auto = "Auto";
	public static final String FASMM_MN_001_Umberlla = "Umberlla";
	public static final String FASMM_MN_001_Billing = "Billing";
	public static final String FASMM_MN_001_More_Options = "More_Options";
	public static final String FASMM_MN_001_Underwriting = "underwriting";
	public static final String FASMM_MN_002_VALUE = "VALUE";
	public static final String PL_Agent_Exception = "PL_Agent_Exception";
	public static final String Others = "Others";
	public static final String S_GetStateTableByAreaCode_URL = "S_GetStateTableByAreaCode_URL";

	public static final String ADDREMOVEVECHILE = "Add or remove a vehicle";
	public static final String GENERALPOLICY = "General Policy";
	public static final String POLICYSTATUS = "Policy Status";
	public static final String ADDVECHILE = "Add a Vehicle";
	public static final String REMOVEVECHILE = "Remove a Vehicle";
	public static final String MAKECOVERAGE = "Make a Coverage Change";
	public static final String NEWQUOTE = "New Quote";

	// SET 2 BILLING & PAYMENTS
	public static final String S_POLICY_STATUS = "S_POLICY_STATUS";
	public static final String ACTIVE = "ACTIVE";
	public static final String CANCEL = "CANCEL";
	public static final String CANCELLED = "CANCELLED";
	public static final String EXPIRED = "EXPIRED";
	public static final String S_LAST_PMNT_AVAIL = "S_LAST_PMNT_AVAIL";
	public static final String S_NEXT_PMNT_AVAIL = "S_NEXT_PMNT_AVAIL";
	public static final String S_BILLINGINFO_RES = "S_BILLINGINFO_RES";
	public static final String S_PAYMENT_DUE_DATE = "S_PaymentDue_Date";
	public static final String S_LASTPAYMENT_AMOUNT = "S_LastPayment_Amount";
	public static final String S_LASTPAYMENT_DATE = "S_LastPayment_Date";

	public static final String S_NEXTPAYMENT_AMOUNT = "S_NextPayment_Amount";
	public static final String S_NEXTPAYMENT_DATE = "S_NextPayment_Date";
	public static final String S_TOT_OUT_AMT = "S_TOT_OUT_AMT";
	public static final String S_CURRENT_BAL = "S_Current_Balance";
	public static final String S_NEXT_PAYMENT_ALLOWED = "S_NEXT_PAYMENT_ALLOWED";
	public static final String PAYMENT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String S_LESS_THAN_DUE_DATE = "S_LESS_THAN_DUE_DATE";
	public static final String S_POLICY_EXPIRATION_DATE = "S_POLICY_EXPIRATION_DATE";
	public static final String S_AUTO_PAY = "S_AUTO_PAY";
	public static final String RV = "Rv";
	public static final String HOME = "Home";
	public static final String HOMEOWNERS = "Home Owners";

	public static final String BristolWest = "Bristol West";
	public static final String TwentyFirstCentury = "21st Century";
	public static final String SIDC_HOST_004 = "SIDC_HOST_004";
	public static final String S_POLICY_EMAIL = "S_POLICY_EMAIL";
	public static final String S_IS_POLICY_EMAIL_AVIALABLE = "S_IS_POLICY_EMAIL_AVIALABLE";
	public static final String S_EMAIL_MAIL_FAX_ENABLED = "S_EMAIL_MAIL_FAX_ENABLED";
	public static final String EMF = "EMF";
	public static final String FM = "FM";
	public static final String EM = "EM";
	public static final String M = "M";
	public static final String Mail = "Mail";
	public static final String Fax = "Fax";
	public static final String Email = "Email";
	public static final String SIDC_MN_005_MainMenu = "Main Menu";

	// FARMER_SALES
	public static final String FARMERSNOBINDAUTO = "Farmers No Bind - Auto";
	public static final String FARMERSNOBINDHOME = "Farmers No Bind - Home";
	public static final String FARMERSNOBINDOTHR = "Farmers No Bind - Other Insurance";
	public static final String FARMERSNOBINDOTHRWBO = "Farmers No Bind - Other Insurance w/o Website";

	public static final String FARMERSNOPRODUCTSTATE = "Farmers - No Product State";
	public static final String FARMERSNOPRODUCTSTATEFGIA = "Farmers - No Product State (FGIA)";
	public static final String FARMERSHOMENEUTRAL = "Farmers Home - Neutral";
	public static final String FARMERSHOMERED = "Farmers Home - Red";
	public static final String FARMERSHOMEGREEN = "Farmers Home - Green";
	public static final String FARMERSAUTONEUTRAL = "Farmers Auto - Neutral";
	public static final String FARMERSAUTORED = "Farmers Auto - Red";
	public static final String FARMERSAUTOGREEN = "Farmers Auto - Green";

	public static final String FARMERSOTHERSINSURANCEGREEN = "Farmers Other Insurance - Green";
	public static final String FARMERSOTHERSINSURANCENETURAL = "Farmers Other Insurance - Neutral";
	public static final String FARMERSOTHERSINSURANCRED = "Farmers Other Insurance - Red";

	// VENKATESH
	public static final String FASAD_MN_001_VALUE = "FASAD_MN_001_VALUE";
	public static final String FASAD_MN_002_VALUE = "FASAD_MN_002_VALUE";
	public static final String FASAD_MN_001_Extension = "Extension";
	public static final String FASAD_MN_001_FarmersNewWorldLife = "Farmers New World Life";
	public static final String FASAD_MN_001_Commercial = "Commercial";
	public static final String FASAD_MN_001_ForemostSpecialty = "Foremost Specialty";
	public static final String FASAD_MN_001_BristolWest = "BristolWest";
	public static final String FASAD_MN_001_HelpPoint = "HelpPoint";
	public static final String FASAD_MN_001_Flood_Svc_Center = "Flood Svc Center";
	public static final String FASAD_MN_001_AgentSuport = "AgentSupport";
	public static final String FASAD_MN_002_TechnicalSupport = "Technical Support";
	public static final String FASAD_MN_002_AgentServices = "Agency Services";
	public static final String FASAD_MN_002_Accounting = "Accounting";
	public static final String FASAD_MN_002_Underwriting = "Underwriting";
	public static final String FASAD_MN_002_MainMenu = "Main Menu";

	// SET - 4 MATHAVAN & BHAVANA
	public static final String FNAG_MN_001_VALUE = "FNAG_MN_001_VALUE";
	public static final String FNCS_MN_001_VALUE = "FNCS_MN_001_VALUE";
	public static final String FMHB_MN_001_VALUE = "FMHB_MN_001_VALUE";

	public static final String RVV = "RV";

	public static final String S_FWS_EPC_PAYMENTUSS_URL_GET = "S_FWS_EPC_PAYMENTUSS_URL_GET";
	public static final String S_EPCPaymentusGroup_URL_GET = "S_EPCPaymentusGroup_URL_GET";
	public static final String FMET_MN_001_VALUE = "FMET_MN_001_VALUE";
	public static final String S_CUSTOMAMT = "S_CUSTOMAMT";
	public static final String S_FULLPOLICYAMT = "S_FULLPOLICYAMT";

	public static final String S_API_ZIP = "S_API_ZIP";
	public static final String S_POLICYDETAILS_MAP = "S_POLICYDETAILS_MAP";
	public static final String NoneOfThese = "None of these";
	public static final String SomethingElse = "Something else";
	public static final String ValidSinglePolicy = "ValidSinglePolicy";
	public static final String ValidMultiplePolicy = "ValidMultiplePolicy";
	public static final String AutoPolicy = "Auto policy";
	public static final String HomePolicy = "home policy";
	public static final String BoatPolicy = "boat policy";
	public static final String MotorcyclePolicy = "motorcycle policy";
	public static final String MotorHomePolicy = "motor home policy";
	public static final String SpecialtyDwelling = "specialty dwelling";
	public static final String MobilehomePolicy = "mobile home policy";
	public static final String UmbrellaPolicy = "umbrella policy";
	public static final String PRODUCTTYPE_Y = "Y";
	public static final String PRODUCTTYPE_A = "A";
	public static final String PRODUCTTYPE_H = "H";
	public static final String PRODUCTTYPE_F = "F";
	public static final String PRODUCTTYPE_U = "U";
	public static final String PRODUCTTYPE_077 = "077";
	public static final String PRODUCTTYPE_103 = "103";
	public static final String PRODUCTTYPE_105 = "105";
	public static final String PRODUCTTYPE_106 = "106";
	public static final String PRODUCTTYPE_107 = "107";
	public static final String PRODUCTTYPE_255 = "255";
	public static final String PRODUCTTYPE_276 = "276";
	public static final String PRODUCTTYPE_381 = "381";
	public static final String PRODUCTTYPE_444 = "444";
	public static final String PRODUCTTYPE_601 = "601";
	public static final String PRODUCTTYPE_602 = "602";
	public static final String POLICY_DETAILS_FOR_MN3 = "POLICY_DETAILS_FOR_MN3";
	public static final String ONE = "one";
	public static final String TWO = "two";
	public static final String THREE = "three";
	public static final String FOUR = "four";
	public static final String FIVE = "five";

	// LOB
	public static final String BU_FARMERS = "FARMERS";
	public static final String BU_21ST = "21ST";
	public static final String BU_FWS = "FWS";
	public static final String BU_FOREMOST = "FOREMOST";
	public static final String BU_EMP_SRV = "EMP_SRV";
	public static final String BU_BW_HI = "BWHI";
	public static final String BU_BW = "BW";
	public static final String BU_NOT_MATCH = "BU_NOT_MATCH";
	public static final String S_STATEGROUPURL = "S_STATEGROUPURL";
	public static final String BU_FOREMOST_USAA = "FOREMOST_USAA";
	public static final String BU_FOREMOST_AARP = "FOREMOST_AARP";
	public static final String BU_FOREMOST_CUST = "FOREMOST_CUST";
	public static final String BU_FOREMOST_AGENT = "FOREMOST_AGENT";
	public static final String BU_21STHAWAII = "21ST_HAWAI";

	public static final String AutoService = "Auto-Service";
	public static final String S_RetrieveBillingDetailsByPolicyNumber_URL = "S_RetrieveBillingDetailsByPolicyNumber_URL";
	public static final String email = "email";
	public static final String S_API_EMAIL = "S_API_EMAIL";

	public static final String S_CARD_EMAIL_AVAILABLE = "S_CARD_EMAIL_AVAILABLE";
	public static final String S_FINAL_POLICY_OBJ = "S_FINAL_POLICY_OBJ";
	public static final String S_CARD_DELIVERY_METHOD = "S_CARD_DELIVERY_METHOD";

	public static final String IS_ELIGIBLE_INTENT = "IS_ELIGIBLE_INTENT";
	public static final String IVR2TEXT_AUDIOS_URL = "IVR2TEXT_AUDIOS_URL";
	public static final String IVR2TEXT_INATENT_URL = "IVR2TEXT_INATENT_URL";
	public static final String IVRTT_MN_001 = "IVRTT_MN_001";
	public static final String IVRTT_PA_001 = "IVRTT_PA_001";
	public static final String IVRTT_PA_002 = "IVRTT_PA_002";
	public static final String invite_prompt = "invite_prompt";
	public static final String success_prompt = "success_prompt";
	public static final String failure_prompt = "failure_prompt";
	public static final String key = "key";

	public static final String S_LEAVETYPE = "S_LEAVETYPE";
	public static final String S_FULLORPARTIAL = "S_FULLORPARTIAL";
	public static final String S_LEAVESTARTTIME = "S_LEAVESTARTTIME";
	public static final String S_LEAVEENDTIME = "S_LEAVEENDTIME";
	public static final String S_LOGINID = "S_LOGINID";
	public static final String S_GET_EPSL_DETAILS_URL = "S_GET_EPSL_DETAILS_URL";
	public static final String S_UPDATE_EPSL_DETAILS_URL = "S_UPDATE_EPSL_DETAILS_URL";
	public static final String S_AGENTID = "S_AGENTID";
	public static final String S_IEXUSERID = "S_IEXUSERID";
	public static final String S_LEAVEAPPLIED = "S_LEAVEAPPLIED";
	public static final String S_VXML4 = "S_VXML4";
	public static final String S_AGENTID_1 = "S_AGENTID_1";
	public static final String S_AgentID_2 = "S_AgentID_2";
	public static final String S_LEAVEAPPLIED_1 = "1";

	public static final String IS_MOBILE_CALLER = "IS_MOBILE_CALLER";
	public static final String IVRTT_GET_ANI_CHECK = "IVRTT_GET_ANI_CHECK";
	public static final String IVRTT_POST_ANI_CHECK = "IVRTT_POST_ANI_CHECK";

	public static final String S_IVR2TEXT_AUDIOS_URL = "S_IVR2TEXT_AUDIOS_URL";
	public static final String S_IVR2TEXT_INTENT_URL = "S_IVR2TEXT_INTENT_URL";
	public static final String S_IVRTT_GET_ANI_CHECK = "S_IVRTT_GET_ANI_CHECK";
	public static final String S_IVRTT_POST_ANI_CHECK = "S_IVRTT_POST_ANI_CHECK";
	public static final String S_IVR2TEXTURL = "S_IVR2TextURL";
	public static final String S_FINAL_INTENT = "S_FINAL_INTENT";
	public static final String S_ESPL_AGENT_MATCH_TRIES = "S_ESPL_AGENT_MATCH_TRIES";

	public static final String P_4 = "4";
	public static final String P_6 = "6";
	public static final String P_9 = "9";

	public static final String P_G = "G";
	public static final String P_M = "M";
	public static final String P_W = "W";
	public static final String P_A = "A";
	public static final String P_H = "H";

	public static final String FINAL_PRODUCTTYPE = "FINAL_PRODUCTTYPE";

	public static final String A_CISCO_SRM_CTI_API = "A_CISCO_SRM_CTI_API";
	public static final String A_KM2_API = "A_KM2_API";
	public static final String A_CLEAR_HARBOR = "A_CLEAR_HARBOR";
	public static final String A_FWS_SRM_CTI = "A_FWS_SRM_CTI";
	public static final String A_COMM_SRM_CTI = "A_COMM_SRM_CTI";
	public static final String A_APEX = "A_APEX";
	public static final String A_AFNI = "A_AFNI";

	// Shared QUOTE MSP KEY
	public static final String MSP_POLICY_QUESTIONS = "POLICY QUESTIONS";
	public static final String MSP_COVERAGE_CHANGE = "COVERAGE CHANGE";
	public static final String MSP_REMOVE_VEHICLE = "REMOVE VEHICLE";
	public static final String MSP_ADD_VEHICLE = "ADD VEHICLE";

	public static final String S_AGENTPOST_RETRIEVEBYAOR_URL = "S_AGENTPOST_RETRIEVEBYAOR_URL";

	// FWS ROuting
	public static final String S_BILLING_ACTIVITY_CD = "S_BILLING_ACTIVITY_CD";
	public static final String S_SERVICE_LEVEL = "S_SERVICE_LEVEL";
	public static final String S_PRODUCER_DISTRIBUTION_CODE = "S_PRODUCER_DISTRIBUTION_CODE";
	public static final String S_ACTIVITY_DESC = "S_ACTIVITY_DESC";
	public static final String S_ROUTING_POLICY_SOURCE = "S_ROUTING_POLICY_SOURCE";
	public static final String S_POLICY_DATA_SOURCE = "S_POLICY_DATA_SOURCE";
	public static final String S_FWSRouting_URL = "S_FWSRouting_URL";
	public static final String S_PAYMENT_SITE_CD = "S_PAYMENT_SITE_CD";
	public static final String S_COMBO_INDICATOR = "S_COMBO_INDICATOR";
	public static final String SHARED_AUTH_RETURN = "SHARED_AUTH_RETURN";
	public static final String S_COMMUNICATION_SOURCE = "S_COMMUNICATION_SOURCE";
	public static final String S_IVR2TEXT_TRANSFERREASON = "IVR to Text";

	public static final String S_FAS_AGENT_SEGMENTATION = "S_FAS_AGENT_SEGMENTATION";
	public static final String S_PC_ADMIN_YEAR = "S_PC_ADMIN_YEAR";
	public static final String S_FAS_PC = "PC";
	public static final String S_FAS_DM = "DM";
	public static final String S_FAS_OTHERS = "Others";
	public static final String S_FAS_AGENTCODE = "agentCode";
	public static final String S_AGENT_SEGMENTATION_URL = "S_AGENT_SEGMENTATION_URL";
	public static final String S_FAS_PC_YEARS = "years";
	public final static String S_ForemostMenu = "ForemostMenu";

	public final static String S_POLICY_STATE_CODE = "S_POLICY_STATE_CODE";

	public static final String S_FULL_POLICY_NUM = "S_FULL_POLICY_NUM";

	// TFAR_MN_002 - Change Request (New Menu)

	public static final String FARMERS_CASTATE = "YES";
	public static final String FARMERS_OTHERSTATE = "NO";

	// Speciality HALO API

	public static final String S_HALO_API_STATUS = "S_HALO_API_STATUS";
	public static final String S_HALO_API_BILLING_STATUS_CODE = "S_HALO_API_BILLING_STATUS_CODE";
	public static final String S_HALO_API_nextPaymentdue = "S_HALO_API_nextPaymentdue";
	public static final String S_HALO_API_nextPaymentdueDate = "S_HALO_API_nextPaymentdueDate";
	public static final String S_HALO_API_outstandingBalance = "S_HALO_API_outstandingBalance";
	public static final String S_HALO_API_ZerooutstandingBalance = "S_HALO_API_ZerooutstandingBalance";
	public static final String S_HALO_API_Waiting_TO_Bill = "S_HALO_API_Waiting_TO_Bill";
	public static final String S_HALO_API_BillDue = "S_HALO_API_BillDue";
	public static final String S_HALO_API_BillAmount = "S_HALO_API_BillAmount";
	public static final String S_HALO_API_PayMentStatus = "S_HALO_API_PayMentStatus";
	public static final String S_HALO_API_PaymentStatusCode = "S_HALO_API_PaymentStatusCode";
	public static final String S_HALO_API_lastpaymentAmount = "S_HALO_API_lastpaymentAmount";
	public static final String S_HALO_API_lastPaymentDate = "S_HALO_API_lastPaymentDate";
	public static final String S_HALO_API_BillingFlag = "S_HALO_API_BillingFlag";
	public static final String S_HALO_API_ExpirePendingBillDue = "S_HALO_API_ExpirePendingBillDue";
	public static final String S_HALO_API_CancelPendingBillDue = "S_HALO_API_CancelPendingBillDue";
	public static final String S_HALO_API_PendingFlag = "S_HALO_API_PendingFlag";
	public static final String S_HALO_API_ExpiredFlag = "S_HALO_API_ExpiredFlag";
	public static final String S_HALO_API_CancelledGenericFlag = "S_HALO_API_CancelledGenericFlag";
	public static final String S_HALO_API_CancelledEPFlag = "S_HALO_API_CancelledEPFlag";
	public static final String S_HALO_API_CancelledFlag = "S_HALO_API_CancelledFlag";

	// Venkatesh K M Caller Verification Project

	public static final String S_CALLER_VERIFICATION = "S_CALLER_VERIFICATION";
	public final static String CV_MN_001_VALUE = "CV_MN_001_VALUE";
	public final static String S_CCAI_STATE = "S_CCAI_STATE";
	public static final String S_CCAI_NAME = "S_CCAI_NAME";
	public final static String S_CV_GDF_CONNECTOR_FLAG = "S_CV_GDF_CONNECTOR_FLAG";
	public final static String S_KYC_AUTHENTICATED_CV = "S_KYC_AUTHENTICATED_CV";
	public final static String S_KYC_AUTHENTICATED_CCAI = "S_KYC_AUTHENTICATED_CCAI";
	public final static String S_IVR_AUTHENTICATED_CCAI = "S_IVR_AUTHENTICATED_CCAI";
	public final static String S_AgentType_CCAI = "S_AgentType_CCAI";
	public static final String S_AGENCY_NAME = "S_AGENCY_NAME";
	public static final String A_CV_SCREENPOPUP_CHECK = "A_CV_SCREENPOPUP_CHECK";
	public static final String A_CV_SCREENPOPUP_CHECK_COMM = "A_CV_SCREENPOPUP_CHECK_COMM";

	public static final String CCAI_Agent_FirstName = "CCAI_Agent_FirstName";
	public final static String CCAI_Agent_LastName = "CCAI_Agent_LastName";
	public final static String CCAI_Agent_State = "CCAI_Agent_State";
	public static final String CCAI_Agent_Code_Producer_Code = "CCAI_Agent_Code_Producer_Code";
	public final static String CCAI_Agency_Name = "CCAI_Agency_Name";
	public final static String CCAI_Agent_Verified = "CCAI_Agent_Verified";
	public final static String CCAI_Agent_Policy_Number = "CCAI_Agent_Policy_Number";

	public final static String CCAI_Customer_FirstName = "CCAI_Customer_FirstName";
	public final static String CCAI_Customer_LastName = "CCAI_Customer_LastName";
	public static final String CCAI_Customer_SSN_Last_4 = "CCAI_Customer_SSN_Last_4";
	public static final String CCAI_Customer_DOB = "CCAI_Customer_DOB";
	public static final String CCAI_Customer_DLNumber = "CCAI_Customer_DLNumber";
	public final static String CCAI_Customer_Address_Street_1 = "CCAI_Customer_Address_Street_1";
	public final static String CCAI_Customer_Address_Street_2 = "CCAI_Customer_Address_Street_2";
	public static final String CCAI_Customer_Address_City = "CCAI_Customer_Address_City";
	public static final String CCAI_Customer_Address_State = "CCAI_Customer_Address_State";
	public static final String CCAI_Customer_Address_ZipCode = "CCAI_Customer_Address_ZipCode";
	public final static String CCAI_Customer_Address_Country = "CCAI_Customer_Address_Country";
	public final static String CCAI_Customer_Address_Type = "CCAI_Customer_Address_Type";
	public static final String CCAI_Customer_Policy_Number = "CCAI_Customer_Policy_Number";
	public static final String CCAI_Customer_BillingAccount_Number = "CCAI_Customer_BillingAccount_Number";
	public static final String CCAI_Customer_Verified = "CCAI_Customer_Verified";
	public static final String CCAI_Customer_ECN = "CCAI_Customer_ECN";
	public static final String CCAI_Event_Data = "CCAI_Event_Data";

	public static final String S_REP_HANDLING_COUNTER = "S_REP_HANDLING_COUNTER";

	public static final String S_REP_HANDLING_COUNTER_FLAG = "S_REP_HANDLING_COUNTER_FLAG";

	public static final String S_FAILOUT_REP_HANDLING_COUNTER_FLAG = "S_FAILOUT_REP_HANDLING_COUNTER_FLAG";

	public static final String New_Policy = "New Policy";

	// Error Handling Venkatesh K M

	public static final String S_TRANSFER_ENTER = "S_TRANSFER_ENTER";

	// START : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add
	// Routing for 'True Direct' HouseAccountSubCategory
	public static final String S_CONFIG_CATEGORIES = "S_CONFIG_CATEGORIES";

	// END : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add
	// Routing for 'True Direct' HouseAccountSubCategory

	// START : CS1245054 : Mdm FWS Policy Lookup changes based on phone number
	public static final String S_MDM_PHONENO_LOOKUP_URL = "S_MDM_PHONENO_LOOKUP_URL";
	public static final String S_EXCLUDED_SOURCE = "S_EXCLUDED_SOURCE";
	public static final String S_MDM_PHONENO_LOOKUP_CONN_TIMEOUT = "S_MDM_PHONENO_LOOKUP_CONN_TIMEOUT";
	public static final String S_MDM_PHONENO_LOOKUP_READ_TIMEOUT = "S_MDM_PHONENO_LOOKUP_READ_TIMEOUT";
	public static final String S_MDM_PHONENO_LOOKUP_FLAG = "S_MDM_PHONENO_LOOKUP_FLAG";
	// END : CS1245054 : Mdm FWS Policy Lookup changes based on phone number
	// Non-Prod Changes-Priya
	public static final String NONPROD_DNISCHECK_URL = "S_NONPROD_DNISCHECK_URL";
	public static final String IS_UAT_DNIS = "IS_UAT_DNIS";
	public static final String RegionHM = "RegionHM";
	// Non prod changes-Priya

	// CS1280321 Add Phase 2 States to BW Producer / FM Agent Relaunch Criteria
	// (successor to CS1268136)
	public static final String S_IA_STATE_CODE = "S_IA_STATE_CODE";

	// CS1328453 Farmers Insurance | US | IA Relaunch IVR routing - Add/Remove
	// States
	public static final String BW_IA_STATE_CODE = "S_BW_IA_STATE_CODE";
	public static final String FWS_IA_STATE_CODE = "S_FWS_IA_STATE_CODE";
	public static final String SPECIALITY_IA_STATE_CODE = "S_SPECIALITY_IA_STATE_CODE";

	// CS1191070 Farmers Insurance | US | Premier Agent Priority Handling and
	// Routing
	public static final String S_ACE_LOOKUP_RESULT = "S_ACE_LOOKUP_RESULT";
	public static final String S_AOR_ANI = "S_AOR_ANI";

	// CS1271390 Farmers Insurance | US | FDS/FWS/BW/Specialty - Model-based
	// Customer Retention call routing
	public static final String S_RISKATTRITION_LOOKUPURL = "S_RISKATTRITION_LOOKUPURL";
	public static final String S_KYC_RETENTION_CHECK = "S_KYC_RETENTION_CHECK";
	public static final String S_SIDA_RETENTION_CHECK = "S_SIDA_RETENTION_CHECK";

	// CS1240953 - General Id and Auth Simplification
	public static final String S_NEW_VIA_FLAG = "S_NEW_VIA_FLAG";
	
	//CS1332683-Move-Caller-Type-Disambig - Arshath - Start
	public static final String KYCMF_MN_002_VALUE="KYCMF_MN_002_VALUE";
	//CS1332683-Move-Caller-Type-Disambig - Arshath - End
   
	// Specialty new products:
		public static final String TRAVEL_PRODUCTTYPECOUNT_KYC = "TRAVEL_PRODUCTTYPECOUNT_KYC";
		public static final String MOTORHOME_PRODUCTTYPECOUNT_KYC = "MOTORHOME_PRODUCTTYPECOUNT_KYC";
		public static final String MOTORCYCLE_PRODUCTTYPECOUNT_KYC = "MOTORCYCLE_PRODUCTTYPECOUNT_KYC";
		public static final String OFFROAD_PRODUCTTYPECOUNT_KYC = "OFFROAD_PRODUCTTYPECOUNT_KYC";
		public static final String MOBILEHOME_PRODUCTTYPECOUNT_KYC = "MOBILEHOME_PRODUCTTYPECOUNT_KYC";
		public static final String RENTALHOME_PRODUCTTYPECOUNT_KYC = "RENTALHOME_PRODUCTTYPECOUNT_KYC";
		public static final String SPHOME_PRODUCTTYPECOUNT_KYC = "SPHOME_PRODUCTTYPECOUNT_KYC";
		public static final String PRODUCTTYPE_TT = "TT";
		public static final String PRODUCTTYPE_MH = "MH";
		public static final String PRODUCTTYPE_MC = "MC";
		public static final String PRODUCTTYPE_OFF = "OFF";
		public static final String PRODUCTTYPE_MOB = "MOB";
		public static final String PRODUCTTYPE_REN = "REN";
		public static final String PRODUCTTYPE_SPH = "SPH";

		// CS1336023 - Cancel policy - Arshath - start
		public final static String CPD_MN_001_VALUE = "CPD_MN_001_VALUE";
		public final static String CPD_MN_002_VALUE = "CPD_MN_002_VALUE";
		public final static String S_MDM_POLICY_STATUS ="S_MDM_POLICY_STATUS";
		// CS1336023 - Cancel policy - Arshath - End
		
		public static final String MainMenu_Welcome_and_Transfer_With_ANI_Lookup = "Welcome and Transfer w/ANI Lookup";
		
		//CS1337446 - Morethan5PolciesMDM - Arshath - Start
		public final static String Continue = "Continue";
		public final static String MorethanFive ="Morethanfive";
		public final static String LessthanFive ="Lessthanfive";
		public final static String AllofThese = "All of these";
		//CS1337446 - Morethan5PolciesMDM - Arshath - END
        
		//CS1184350 - NewClaims API - Arshath - Start
		public static final String S_NEWCLAIMS_LOOKUPURL = "S_NEWCLAIMS_LOOKUPURL";
		public static final String SIP_MN_002_VALUE ="SIP_MN_002_VALUE";
		public static final String FunctionName = "S_FunctionName";
		//CS1184350 - NewClaims API - Arshath - END
		
		//CS1348016 - All BU's - Onboarding Line Routing
		public static final String S_APP_ONBOARDING_ELIGIBLE_FLAG="S_APP_ONBOARDING_ELIGIBLE_FLAG";
		//public static final String S_MDM_INCEPTION_DATE="S_MDM_INCEPTION_DATE";
		//public static final String S_POLICY_INCEPTION_DATE="S_POLICY_INCEPTION_DATE";
		public static final String S_ONBOARDING_ELIGIBLE_DAYS="S_ONBOARDING_ELIGIBLE_DAYS";
		public static final String S_ONBOARDING_ELIGIBLE="S_ONBOARDING_ELIGIBLE";
		
		//FNWL Keys ::
				public static final String S_FNWL_ACE_ANI_LOOKUP_URL = "S_FNWL_ACE_ANI_LOOKUP_URL";
				public static final String S_FNWL_ACE_AOR_LOOKUP_URL = "S_FNWL_ACE_AOR_LOOKUP_URL";
				public static final String S_FNWL_MDM_ANI_LOOKUP_URL = "S_FNWL_MDM_ANI_LOOKUP_URL";
				public static final String S_FNWL_MDM_PCN_LOOKUP_URL = "S_FNWL_MDM_PCN_LOOKUP_URL";
				public static final String S_FNWL_MDM_DETERMINISTIC_LOOKUP_URL = "S_FNWL_MDM_DETERMINISTIC_LOOKUP_URL";
				public static final String S_FNWL_PAYMENTUS_URL = "S_FNWL_PAYMENTUS_URL";
				public static final String FNWL_HOST_001_RESP = "FNWL_HOST_001_RESP";
				public static final String FNWL_AGENT_LIST_VIA_ANI = "FNWL_AGENT_LIST_VIA_ANI";
				public static final String FNWL_CUSTOMER_LIST_VIA_ANI = "FNWL_CUSTOMER_LIST_VIA_ANI";
				public static final String FNWL_ACE_VIA_ANI_CALLED = "FNWL_ACE_VIA_ANI_CALLED";
				public static final String FNWL_MDM_VIA_ANI_CALLED = "FNWL_MDM_VIA_ANI_CALLED";
				public static final String FNWL_AOR_FOUND_VIA_ANI = "FNWL_AOR_FOUND_VIA_ANI";
				public static final String FNWL_POLICY_FOUND_VIA_ANI = "FNWL_POLICY_FOUND_VIA_ANI";
				public static final String FNWL_POLICY_FOUND_VIA_POLICY = "FNWL_POLICY_FOUND_VIA_POLICY";
				public static final String FNWL_MN_001_VALUE = "FNWL_MN_001_VALUE";
				public static final String FNWL_MN_002_VALUE = "FNWL_MN_002_VALUE";
				public static final String FNWL_MN_003_VALUE = "FNWL_MN_003_VALUE";
				public static final String FNWL_MN_004_VALUE = "FNWL_MN_004_VALUE";
				public static final String FNWL_MN_005_VALUE = "FNWL_MN_005_VALUE";
				public static final String FNWL_MN_006_VALUE = "FNWL_MN_006_VALUE";
				public static final String FNWL_MN_007_VALUE = "FNWL_MN_007_VALUE";
				public static final String FNWL_MN_008_VALUE = "FNWL_MN_008_VALUE";
				public static final String FNWL_MN_009_VALUE = "FNWL_MN_009_VALUE";
				public static final String FNWL_MN_010_VALUE = "FNWL_MN_010_VALUE";
				public static final String FNWL_MN_011_VALUE = "FNWL_MN_011_VALUE";
				public static final String FNWL_MN_012_VALUE = "FNWL_MN_012_VALUE";
				public static final String FNWL_MN_013_VALUE = "FNWL_MN_013_VALUE";
				public static final String FNWL_MN_014_VALUE = "FNWL_MN_014_VALUE";
				public static final String FNWL_MN_015_VALUE = "FNWL_MN_015_VALUE";
				public static final String FNWL_MN_016_VALUE = "FNWL_MN_016_VALUE";
				public static final String FNWL_MN_017_VALUE = "FNWL_MN_017_VALUE";
				public static final String FNWL_MN_018_VALUE = "FNWL_MN_018_VALUE";
				public static final String FNWL_AOR_ID = "FNWL_AOR_ID";
				public static final String FNWL_UPN = "FNWL_UPN";
				public static final String FNWL_AGENT_CODE = "FNWL_AGENT_CODE";
				public static final String FNWL_AGENT_TYPE = "FNWL_AGENT_TYPE";
				public static final String FNWL_AGENT_AWARDS = "FNWL_AGENT_AWARDS";
				public static final String FNWL_AOR_FOUND_VIA_AOR = "FNWL_AOR_FOUND_VIA_AOR";
				public static final String HOLD_ON = "Hold On";
				public static final String FNWL_PRIORITY_AGENT_FLAG = "FNWL_PRIORITY_AGENT_FLAG";
				public static final String FNWL_TECHNICAL_SUPPORT = "Technical Support";
				public static final String FNWL_SALES_SUPPORT = "Sales Support";
				public static final String FNWL_UNDERWRITING_QUESTIONS = "Underwriting Questions";
				public static final String FNWL_SINGLE_MDM_POLICY = "FNWL_SINGLE_MDM_POLICY";
				public static final String FNWL_POLICY_NUM = "FNWL_POLICY_NUM";
				public static final String FNWL_POLICY_TYPE = "FNWL_POLICY_TYPE";
				public static final String FNWL_POLICY_BOOKTYPE = "FNWL_POLICY_BOOKTYPE";
				public static final String FNWL_POLICY_FOUND_VIA_DETERMINISTIC_SEARCH = "FNWL_POLICY_FOUND_VIA_DETERMINISTIC_SEARCH";
				public static final String FNWL_POLICY_FOUND = "FNWL_POLICY_FOUND";
				public static final String FNWL_SINGLE_MDM_POLICY_FOUND_VIA_DETERMINISTIC_SEARCH = "FNWL_SINGLE_MDM_POLICY_FOUND_VIA_DETERMINISTIC_SEARCH";
				public static final String FNWL_VUL = "VUL";
				public static final String FNWL_IUL = "IUL";
				public static final String FNWL_Others = "OTHERS";
				public static final String FNWL_FRONTBOOK = "F";
				public static final String FNWL_BACKBOOK = "B";
				public static final String FNWL_MIDBOOK = "M";
				public static final String FNWL_POLICY_LOB = "FNWL_POLICY_LOB";
				public static final String FNWL_LIFE = "LIFE";
				public static final String FNWL_FARMERS_VARIABLE_ANNUITY = "FARMERS VARIABLE ANNUITY";
				public static final String FNWL_FARMERS_VARIABLE_UNIVERSAL_LIFE = "Farmers Variable Universal Life";
				public static final String FNWL_FARMERS_VARIABLE_ANNUITY_IRA = "Variable Annuity – Ira";
				public static final String FNWL_FARMERS_VARIABLE_ANNUITY_NON_QUALIFIED = "Variable Annuity – Non Qualified";
				public static final String FNWL_FARMERS_VARIABLE_ANNUITY_ROTH = "Variable Annuity – Roth";
				public static final String FNWL_Variable_Universal_Life = "Variable Universal Life";
				public static final String FNWL_VARIABLE_UNIVERSAL_LIFE = "VARIABLE UNIVERSAL LIFE";
				public static final String FNWL_FARMERS_VUL_LIFEACCUMULATOR = "VUL LifeAccumulator";
				public static final String FNWL_FARMERS_INDEX_UNIVERSAL_LIFE = "Farmers Index Universal Life";
				public static final String FNWL_FARMERS_INDEXED_UNIVERSAL_LIFE = "INDEXED UNIVERSAL LIFE";
				public static final String FNWL_POLICY_ISSUE_DATE = "FNWL_POLICY_ISSUE_DATE";
				public static final String FNWL_BEFORE = "Before";
				public static final String FNWL_AFTER = "After";
				public static final String FNWL_IUL_COMPARISON_DATE = "10-01-2019";
				public static final String FNWL_POLICY_DOB = "FNWL_POLICY_DOB";
				public static final String FNWL_POLICY_ZIP = "FNWL_POLICY_ZIP";
				public static final String FNWL_POLICY_SSN = "FNWL_POLICY_SSN";
				public static final String FNWL_POLICY_TAXID = "FNWL_POLICY_TAXID";
				public static final String FNWL_USER_ENTERED_DOB = "FNWL_USER_ENTERED_DOB";
				public static final String FNWL_USER_ENTERED_ZIP = "FNWL_USER_ENTERED_ZIP";
				public static final String FNWL_USER_ENTERED_SSN = "FNWL_USER_ENTERED_SSN";
				public static final String PENDING_APPLICATION = "Pending Application";
				public static final String SALES_INQUIRIES = "Sales Inquiries";
				public static final String FORCE_POLICY_SERVICES = "Force Policy Services";
				public static final String FNWL_NEW_BUSINESS = "New Business";
				public static final String FWNL_POLICY_SERVICES = "Policy Services";
				public static final String CUSTOMER_SERVICE = "Customer Service";
				public static final String INITIAL_PAYMENT = "Initial Payment";
				public static final String NULL = "NULL";
				public static final String FNWL_DOB_MATCHED = "FNWL_DOB_MATCHED";
				public static final String FNWL_ZIP_MATCHED = "FNWL_ZIP_MATCHED";
				public static final String FNWL_SSN_MATCHED = "FNWL_SSN_MATCHED";
				public static final Double FNWL_BALANCE_DUE_AMOUNT = 0.0;
				public static final String FNWL_EPC_OPERATION = "SALE";
				public static final String FNWL = "FNWL";
				public static final String FNWL_Y = "Y";
				public static final String PRESALE = "PRESALE";
				public static final String FNWL_POLICYMAP_VIA_ANI = "FNWL_POLICYMAP_VIA_ANI";
				public static final String FNWL_POLICYMAP_VIA_PCN = "FNWL_POLICYMAP_VIA_PCN";
				public static final String FNWL_POLICYMAP_VIA_DETERMINISTICSEARCH = "FNWL_POLICYMAP_VIA_DETERMINISTICSEARCH";
				public static final String FNWL_AGENTMAP_VIA_ANI = "FNWL_AGENTMAP_VIA_ANI";
				public static final String FNWL_AGENTMAP_VIA_AOR = "FNWL_AGENTMAP_VIA_AOR";
				public static final String FNWL_AGENT = "Agent";
				public static final String FNWL_CUSTOMER = "Customer";
				public static final String PENDING_APPLICATIONS = "Pending Applications";
				public static final String FNWL_CALLER_TYPE = "FNWL_CALLER_TYPE";
				//END :: FNWL Keys
				
				//CS1347819 | Transfer Reduction - Sales to Service - Start
				public static final String FSEL_MN_001_VALUE = "FSEL_MN_001_VALUE";
				//CS1347819 | Transfer Reduction - Sales to Service - END
				
				//CallBack
				public static final String S_CALLBACK_TYPE="S_CALLBACK_TYPE";
				public static final String S_TIMEZONE="S_TIMEZONE";
				public static final String S_CALLBACK_TIME="S_CALLBACK_TIME";
				public static final String S_SALES_CALLBACK_KEY="S_SALES_CALLBACK_KEY";
				//Callback Segment API
				public static final String S_CALLBACK_SEGMENT_URL="S_CALLBACK_SEGMENT_URL";
				public static final String S_GET_STATE_GROUP_TABLE_BY_KEY="S_GET_STATE_GROUP_TABLE_BY_KEY";
				
				//CS1360621-Add 'IA' Routing Qualifier
				public static final String S_POLICY_CATEGORY = "S_POLICY_CATEGORY";
}
