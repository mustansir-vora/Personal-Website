package com.farmers.FarmersAPI;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

public class FWSEPCPaymentus_Post {
	
	//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);

	public JSONObject start(String url, String tid, String operation, String id, String authtoken, String languagepreference, String brandcode, String firstname, String lastname, long phonenumber, String email, String addressline1, String state, String zipcode, String city, String country, double balancedueamount, String policysource, String existingcustomerind, String policynumber, String policyeffectivedate, String policystate, String policyinputtypecd, String producerrolecode, String callroutingindicator, String iapolicyindicator, String gpc, String companyproductcode, String servicelevels, String combopackageindicator, String paymentsitecd, String internalpolicynumber, String internalpolicyversion, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+operation+", "+id+", "+authtoken+", "+languagepreference+", "+firstname+", "+lastname+", "+phonenumber+", "+email+", "+addressline1+", "+state+", "+zipcode+", "+city+", "+country+", "+balancedueamount+", "+policysource+", "+existingcustomerind+", "+policynumber+", "+policyeffectivedate+", "+policystate+", "+policyinputtypecd+", "+producerrolecode+", "+callroutingindicator+", "+iapolicyindicator+", "+gpc+", "+companyproductcode+", "+servicelevels+", "+combopackageindicator+", "+paymentsitecd+", "+internalpolicynumber+", "+internalpolicyversion+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.payment.create";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(operation, id, authtoken, languagepreference, firstname, lastname, phonenumber, email, addressline1, state, zipcode, city, country, balancedueamount, policysource, existingcustomerind, policynumber, policyeffectivedate, policystate, policyinputtypecd, producerrolecode, callroutingindicator, iapolicyindicator, gpc, companyproductcode, servicelevels, combopackageindicator, paymentsitecd, internalpolicynumber, internalpolicyversion);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid, logger, policysource);
			if (null != headers && headers.containsKey("TOKEN_ERROR")) {
				response = formatErrorResponse("OAUTH TOKEN_ERROR", 1);
			} else {
				RequestBean requestBean = new RequestBean(url, requestBody, "POST", headers);
				response = connection.sendHttpRequest(requestBean, logger, tid, conntimeout, readtimeout);
				logger.info(tid+" : Final Response = "+response);
			}
		} catch (Exception e) {
			// Handle other exceptions
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		return response;
	}

	private Map<String, String> getHeaders(String Scope, String tid, Logger logger, String policysource) {

		Map<String, String> headers = null;
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
	        
	        String client_id = prop.getProperty("Farmers.Host.ClientID");
	        String client_secret = prop.getProperty("Farmers.Host.ClientSecret");
	        Encrypt_Decrypt Decryptor = new Encrypt_Decrypt();
	        client_id = Decryptor.decrypt(client_id);
	        client_secret = Decryptor.decrypt(client_secret);
			
			headers = new HashMap<String, String>();
			headers.put("frms_source", prop.getProperty("Farmers.Host.frms_source"));
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_region", prop.getProperty("Farmers.Host.frms_region"));
			if (policysource.toUpperCase().contains("ARS")) {
				headers.put("frms_brand", "FWSARS");
			}
			else {
				headers.put("frms_brand", "FWSA360");
			}
			headers.put("client_id", client_id);
			headers.put("client_secret", client_secret);
			headers.put("Content-Type", "application/json");
			headers.put("frms_brand_group", "FWS");
			
			// access_token = new String();
			TokenInvocation tokeninvocation = new TokenInvocation();
			String access_token = tokeninvocation.getAccessToken(Scope, logger, tid);
			if (null != access_token) {
				headers.put("Authorization", "Bearer " + access_token);
			} else {
				headers.put("TOKEN_ERROR", "true");
			}
			
			logger.info(tid+" : Headers : "+headers);
			
		} catch (Exception e) {
			// Handle other exceptions
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		return headers;
	}

	public JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}

	public String formRequestBody(String operation, String id, String authtoken, String languagepreference, String firstname, String lastname, long phonenumber, String email, String addressline1, String state, String zipcode, String city, String country, double balancedueamount, String policysource, String existingcustomerind, String policynumber, String policyeffectivedate, String policystate, String policyinputtypecd, String producerrolecode, String callroutingindicator, String iapolicyindicator, String gpc, String companyproductcode, String servicelevels, String combopackageindicator, String paymentsitecd, String internalpolicynumber, String internalpolicyversion) {

		JSONObject address = new JSONObject();
		JSONObject customer = new JSONObject();
		JSONObject callinfo = new JSONObject();

		address.put("line1", addressline1);
		address.put("state", state);
		address.put("zipCode", zipcode);
		address.put("city", city);
		address.put("country", country);
		
		customer.put("firstName", firstname);	
		customer.put("lastName", lastname);	
		customer.put("phoneNumber", phonenumber);	
		customer.put("email", email);	
		customer.put("address", address);	
		
		List<Map<String, String>> dynamicproperties = new ArrayList<Map<String, String>>();
		
		dynamicproperties.add(createDynamicProperty("policyNumber", policynumber));
		dynamicproperties.add(createDynamicProperty("policyEffectiveDate", policyeffectivedate));
		dynamicproperties.add(createDynamicProperty("policyState", policystate));
		dynamicproperties.add(createDynamicProperty("policyInputtypeCd", policyinputtypecd));
		dynamicproperties.add(createDynamicProperty("producerRoleCode", producerrolecode));
		dynamicproperties.add(createDynamicProperty("callRoutingIndicator", callroutingindicator));
		dynamicproperties.add(createDynamicProperty("iaPolicyIndicator", iapolicyindicator));
		dynamicproperties.add(createDynamicProperty("gpc", gpc));
		dynamicproperties.add(createDynamicProperty("companyProductCode", companyproductcode));
		dynamicproperties.add(createDynamicProperty("serviceLevels", servicelevels));
		dynamicproperties.add(createDynamicProperty("comboPackageIndicator", combopackageindicator));
		dynamicproperties.add(createDynamicProperty("paymentSiteCd", paymentsitecd));
		if (policysource.toUpperCase().contains("ARS")) {
			dynamicproperties.add(createDynamicProperty("policySource", "FWSARS"));
		}
		else {
			dynamicproperties.add(createDynamicProperty("policySource", "FWSA360"));
		}
		dynamicproperties.add(createDynamicProperty("internalPolicyNumber", internalpolicynumber));
		dynamicproperties.add(createDynamicProperty("internalPolicyVersion", internalpolicyversion));
		
		callinfo.put("operation", operation);
		callinfo.put("id", id);
		callinfo.put("authToken", authtoken);
		callinfo.put("languagePreference", languagepreference);
		callinfo.put("customer", customer);
		callinfo.put("balanceDueAmount", balancedueamount);
		callinfo.put("policySource", policysource);
		callinfo.put("existingCustomerInd", existingcustomerind);
		callinfo.put("dynamicProperties", dynamicproperties);
		
		JSONObject reqBody = new JSONObject();
		reqBody.put("callInfo", callinfo);

		return reqBody.toJSONString();
		
	}
	
	private JSONObject createDynamicProperty(String name, String value) {
	    JSONObject prop = new JSONObject();
	    prop.put("name", name);
	    prop.put("value", value);
	    return prop;
	}

	
	  public static void main(String[] args) {
	  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());  
		  
	  String url = "https://api-ss.farmersinsurance.com/ivrms/v1/calls/8e25fa24530d11eebdb0005056af20bf?idType=accountNumber&operation=paymenttransfer";
	  String tid = "Ciscotest";
	  
	  String operation = "SALE";
	  String id = "G00930251210";
	  String authtoken = "95002";
	  String languagepreference = "es";
	  String brandcode = "BW";
	  String firstname = "";
	  String lastname = "";
	  long phonenumber = 4088354360L; 
	  String email = "";
	  String addressline1 = "";
	  String state = "";
	  String zipcode = "95002";
	  String city = "";
	  String country = "";
	  double balancedueamount = 100.0;
	  String policysource = "FWSARS";
	  String existingcustomerind = "N";
	  String policynumber = "A8065201600";
	  String policyeffectivedate = "05/01/2023";
	  String policystate = "CA";
	  String policyinputtypecd = "P";
	  String producerrolecode = "Group";
	  String callroutingindicator = "Y";
	  String iapolicyindicator = "";
	  String gpc = "BSB";
	  String companyProductCode = "A";
	  String serviceLevels = "";
	  String combopackageindicator = "";
	  String paymentsitecd = "00";
	  String internalpolicynumber = "806520160";
	  String internalpolicyversion = "0";
	  
	  FWSEPCPaymentus_Post test = new FWSEPCPaymentus_Post();
	  JSONObject resp = null; 
	  resp = test.start(url, tid, operation, id, authtoken, languagepreference, brandcode, firstname, lastname, phonenumber, email, addressline1, state, zipcode, city, country, balancedueamount, policysource, existingcustomerind, policynumber, policyeffectivedate, policystate, policyinputtypecd, producerrolecode, callroutingindicator, iapolicyindicator, gpc, companyProductCode, serviceLevels, combopackageindicator, paymentsitecd, internalpolicynumber, internalpolicyversion, 10000, 10000, context);
	  }
	
}
