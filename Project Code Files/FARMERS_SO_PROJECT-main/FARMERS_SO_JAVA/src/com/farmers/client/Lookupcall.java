package com.farmers.client;

import java.io.IOException;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONObject;

import com.farmers.AdminAPI.GetAfterHourRulesTableByKey;
import com.farmers.AdminAPI.GetAniGroupHandlingTableByBusinessObjects;
import com.farmers.AdminAPI.GetAniGroupTableByKey;
import com.farmers.AdminAPI.GetAniValidation;
import com.farmers.AdminAPI.GetBusinessObjectsByDnisKey;
import com.farmers.AdminAPI.GetCallerDetailsByAni;
import com.farmers.AdminAPI.GetIVRToTextTableByIntent;
import com.farmers.AdminAPI.GetIvrApiDetails;
import com.farmers.AdminAPI.GetMainMenuTableByKey;
import com.farmers.AdminAPI.GetMainTableByKey;
import com.farmers.AdminAPI.GetPrequeueMessageRulesByBusinessObjects;
import com.farmers.AdminAPI.GetPrequeueMessageRulessByKey;
import com.farmers.AdminAPI.GetRoutingPlanTableByKey;
import com.farmers.AdminAPI.GetRoutingTableByBusinessObjects;
import com.farmers.AdminAPI.GetRoutingTableByKey;
import com.farmers.AdminAPI.GetRoutingTableByMenuKey;
import com.farmers.AdminAPI.GetStateGroupTableByKey;
import com.farmers.AdminAPI.GetStateGroupTableByStateName;
import com.farmers.AdminAPI.GetStatePropertyTableByBusinessObjects;
import com.farmers.AdminAPI.GetStateTableByAreaCode;
import com.farmers.FarmersAPI.AccountLinkAniLookup_Post;
import com.farmers.FarmersAPI.AgentInfoRetrieveByAOR_Post;
import com.farmers.FarmersAPI.AgentInfo_Post;
import com.farmers.FarmersAPI.BillPresentmentretrieveBillingSummary_Post;
import com.farmers.FarmersAPI.EPCHaloBillingDetailsGroup_Post;
import com.farmers.FarmersAPI.FWSBillingLookup_Get;
import com.farmers.FarmersAPI.FWSPolicyLookup_Post;
import com.farmers.FarmersAPI.ForemostPolicyInquiry_Post;
import com.farmers.FarmersAPI.IVR2Text_Post;
import com.farmers.FarmersAPI.MdmFWSPPhoneNoLookup_Post;
import com.farmers.FarmersAPI.MulesoftFarmerPolicyInquiry_Post;
import com.farmers.FarmersAPI.OrderIDCard_21C_Post;
import com.farmers.FarmersAPI.OrderIDCard_BW_Post;
import com.farmers.FarmersAPI.OrderIDCard_FDS;
import com.farmers.FarmersAPI.OrderIDCard_FWS_Post;
import com.farmers.FarmersAPI.PointGeneralBilling_Get;
import com.farmers.FarmersAPI.PointOrderIDCard_Post;
import com.farmers.FarmersAPI.PointPolicyInquiry_Get;
import com.farmers.FarmersAPI.PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post;
import com.farmers.FarmersAPI.RiskAttritionAPILookup_Post;
import com.farmers.FarmersAPI_NP.AccountLinkAniLookup_NP_Post;
import com.farmers.FarmersAPI_NP.AgentInfoRetrieveByAOR_NP_Post;
import com.farmers.FarmersAPI_NP.BillPresentmentretrieveBillingSummary_NP_Post;
import com.farmers.FarmersAPI_NP.EPCHaloBillingDetailsGroup_NP_Post;
import com.farmers.FarmersAPI_NP.FWSBillingLookup_NP_Get;
import com.farmers.FarmersAPI_NP.FWSPolicyLookup_NP_Post;
import com.farmers.FarmersAPI_NP.ForemostPolicyInquiry_NP_Post;
import com.farmers.FarmersAPI_NP.IVR2Text_NP_Post;
import com.farmers.FarmersAPI_NP.MdmFWSPPhoneNoLookup_NP_Post;
import com.farmers.FarmersAPI_NP.MulesoftFarmerPolicyInquiry_NP_Post;
import com.farmers.FarmersAPI_NP.OrderIDCard_21C_NP_Post;
import com.farmers.FarmersAPI_NP.OrderIDCard_FWS_NP_Post;
import com.farmers.FarmersAPI_NP.OrderIDCard_NP_BW_Post;
import com.farmers.FarmersAPI_NP.OrderIDCard_NP_FDS;
import com.farmers.FarmersAPI_NP.PointGeneralBilling_NP_Get;
import com.farmers.FarmersAPI_NP.PointPolicyInquiry_NP_Get;
import com.farmers.FarmersAPI_NP.PolicyInquiry_RetrieveInsurancePoliciesbyParty_NP_Post;
import com.farmers.FarmersAPI_NP.RiskAttritionAPILookup_NP_Post;
import com.farmers.FarmersAPI_NP.NewClaimsLookup_NP_Post;
import com.farmers.FarmersAPI.NewClaimsLookup_Post;
//FNWL - Start
import com.farmers.FarmersAPI.FNWLACELookup_Post;
import com.farmers.FarmersAPI.FNWLEPCPaymentus_Post;
import com.farmers.FarmersAPI.FNWLMDMLookup_Post;
import com.farmers.FarmersAPI_NP.FNWLACELookup_NP_Post;
import com.farmers.FarmersAPI_NP.FNWLEPCPaymentus_NP_Post;
import com.farmers.FarmersAPI_NP.FNWLMDMLookup_NP_Post;
//FNWL - End
public class Lookupcall extends EPCHaloBillingDetailsGroup_Post 

{
	
	public org.json.simple.JSONObject GetEPCHaloBillingDetailsGroup_Post(String url, String tid, String policynumber,
			String actortype, int conntimeout, int readtimeout, LoggerContext context, String region,String UAT_FLAG) {
		// UAT ENV CHAE(PRIYA,SHAIK)

		org.json.simple.JSONObject resps = null;

		EPCHaloBillingDetailsGroup_Post GetPayments = new EPCHaloBillingDetailsGroup_Post();
		EPCHaloBillingDetailsGroup_NP_Post GetPaymentsNP = new EPCHaloBillingDetailsGroup_NP_Post();

		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetPaymentsNP.start(url, tid, policynumber, actortype, conntimeout, readtimeout, context, region);

		} else {

			resps = GetPayments.start(url, tid, policynumber, actortype, conntimeout, readtimeout, context);
		}

		return resps;
	}

	public org.json.simple.JSONObject GetFWSBillingLookup(String url, String tid, String policysource,
			String policystate, String strLOB, int conntimeout, int readtimeout, LoggerContext context, String region,String UAT_FLAG)
			throws IOException {
		org.json.simple.JSONObject resps = null;
		FWSBillingLookup_Get GetBills = new FWSBillingLookup_Get();

		FWSBillingLookup_NP_Get GetBillsNP = new FWSBillingLookup_NP_Get();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetBillsNP.start(url, tid, policysource, policystate, strLOB, conntimeout, readtimeout, context,
					region);
		} else {
			resps = GetBills.start(url, tid, policysource, policystate, strLOB, conntimeout, readtimeout, context);

		}

		return resps;

	}

	public org.json.simple.JSONObject GetAccountLookup(String url, String tid, String ani, int conntimeout,
		int readtimeout, LoggerContext context, String region, String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;
		AccountLinkAniLookup_Post GetBills = new AccountLinkAniLookup_Post();
		AccountLinkAniLookup_NP_Post GetBillsNP = new AccountLinkAniLookup_NP_Post();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetBillsNP.start(url, tid, ani, conntimeout, readtimeout, context, region);
		} else {
			resps = GetBills.start(url, tid, ani, conntimeout, readtimeout, context);
		}
		return resps;

	}

	public org.json.simple.JSONObject GetAgentPost(String url, String tid, String businessunit, String phonenumber,
			String userid, String systemname, int conntimeout, int readtimeout, LoggerContext context)
			throws IOException {
		AgentInfo_Post GetPost = new AgentInfo_Post();

		org.json.simple.JSONObject resps = GetPost.start(url, tid, businessunit, phonenumber, userid, systemname,
				conntimeout, readtimeout, context);

		return resps;

	}

	public org.json.simple.JSONObject Getpointgeneralbilling(String endPoint, String tid, Integer conntimeout,
			Integer readtimeout, LoggerContext context, String region,String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;
		PointGeneralBilling_Get GetPointBills = new PointGeneralBilling_Get();
		PointGeneralBilling_NP_Get GetPointBillsNP = new PointGeneralBilling_NP_Get();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetPointBillsNP.start(endPoint, tid, conntimeout, readtimeout, context, region);
		} else {
			resps = GetPointBills.start(endPoint, tid, conntimeout, readtimeout, context);
		}

		return resps;
	}

	public org.json.simple.JSONObject GetIVR2Text_Post(String url, String ivrcallid, String callerani,
			String destinationapp, String tollfreenumber, String callerinput, String poptype, String calledinto,
			String calledintime, String tollfreedescription, String callertype, String language, String an1,
			String upn1, String transferreason, String intent, String brand, boolean validpolicynumber,
			String callerrisklevel, int conntimeout, int readtimeout, LoggerContext context, String region,String UAT_FLAG)
			throws IOException {
		org.json.simple.JSONObject resps = null;
		IVR2Text_Post GetIVRText = new IVR2Text_Post();
		IVR2Text_NP_Post GetIVRTextNP = new IVR2Text_NP_Post();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetIVRTextNP.start(url, ivrcallid, callerani, destinationapp, tollfreenumber, callerinput, poptype,
					calledinto, calledintime, tollfreedescription, callertype, language, an1, upn1, transferreason,
					intent, brand, validpolicynumber, callerrisklevel, conntimeout, readtimeout, context, region);
		} else {
			resps = GetIVRText.start(url, ivrcallid, callerani, destinationapp, tollfreenumber, callerinput, poptype,
					calledinto, calledintime, tollfreedescription, callertype, language, an1, upn1, transferreason,
					intent, brand, validpolicynumber, callerrisklevel, conntimeout, readtimeout, context);
		}
		return resps;

	}

	public org.json.simple.JSONObject GetFWSPolicyLookup(String url, String tid, String policycontractnumber,
			String billingaccountnumber, String telephonenumber, int conntimeout, int readtimeout,
			LoggerContext context, String region,String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;
		FWSPolicyLookup_Post GetPolicy = new FWSPolicyLookup_Post();
		FWSPolicyLookup_NP_Post GetPolicyNP = new FWSPolicyLookup_NP_Post();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetPolicyNP.start(url, tid, policycontractnumber, billingaccountnumber, telephonenumber,
					conntimeout, readtimeout, context, region);
		} else {
			resps = GetPolicy.start(url, tid, policycontractnumber, billingaccountnumber, telephonenumber, conntimeout,
					readtimeout, context);
		}
		return resps;

	}
	
	public org.json.simple.JSONObject GetBillPresentmentretrieveBillingSummary(String url, String tid, String id,
			String customerid, boolean loopback, String policynumber, String productgroupname, int conntimeout,
			int readtimeout, LoggerContext context, String region,String UAT_FLAG) throws IOException {

		org.json.simple.JSONObject resps = null;

		BillPresentmentretrieveBillingSummary_Post GetBillPresentment = new BillPresentmentretrieveBillingSummary_Post();

		BillPresentmentretrieveBillingSummary_NP_Post GetBillPresentmentNP = new BillPresentmentretrieveBillingSummary_NP_Post();

		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetBillPresentmentNP.start(url, tid, id, customerid, loopback, policynumber, productgroupname,
					conntimeout, readtimeout, context, region);

		} else {

			resps = GetBillPresentment.start(url, tid, id, customerid, loopback, policynumber, productgroupname,
					conntimeout, readtimeout, context);
		}

		return resps;
	}

	// Admin API 1 GetBusinessObjectsByDnisKey
	public org.json.simple.JSONObject BusinessObjectsByDnisKey(String url, String callid, int tenantid, String key,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetBusinessObjectsByDnisKey GetBusinessObjectsByDnisKey = new GetBusinessObjectsByDnisKey();

		org.json.simple.JSONObject resps = GetBusinessObjectsByDnisKey.start(url, callid, tenantid, key, conntimeout,
				readtimeout, context);

		return resps;
	}

	// Admin API 2 GetAfterHourRulesTableByKey
	public org.json.simple.JSONObject AfterHourRulesTableByKey(String url, String callid, int tenantid, String key,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetAfterHourRulesTableByKey getafterhourrulestablebykey = new GetAfterHourRulesTableByKey();

		org.json.simple.JSONObject resps = getafterhourrulestablebykey.start(url, callid, tenantid, key, conntimeout,
				readtimeout, context);

		return resps;
	}

	// Admin API 3 GetAniGroupHandlingTableByBusinessObjects
	public org.json.simple.JSONObject AniGroupHandlingTableByBusinessObjects(String url, String callid, int tenantid,
			String key, String dnis, String dnisgroup, String lob, String category, int conntimeout, int readtimeout,
			LoggerContext context) throws IOException {
		GetAniGroupHandlingTableByBusinessObjects anigrouphandlingtablebybusinessobjects = new GetAniGroupHandlingTableByBusinessObjects();

		org.json.simple.JSONObject resps = anigrouphandlingtablebybusinessobjects.start(url, callid, tenantid, key,
				dnis, dnisgroup, lob, category, conntimeout, readtimeout, context);

		return resps;
	}

	// Admin API 4 GetAniGroupTableByKey
	public org.json.simple.JSONObject AniGroupTableByKey(String url, String callid, int tenantid, String key,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetAniGroupTableByKey anigrouptablebyKey = new GetAniGroupTableByKey();

		org.json.simple.JSONObject resps = anigrouptablebyKey.start(url, callid, tenantid, key, conntimeout,
				readtimeout, context);

		return resps;
	}

	// Admin API 5 GetAniGroupTableByKey
	public org.json.simple.JSONObject AniValidation(String url, String callid, String key, int conntimeout,
			int readtimeout, LoggerContext context) throws IOException {
		GetAniValidation anivalidation = new GetAniValidation();

		org.json.simple.JSONObject resps = anivalidation.start(url, callid, key, conntimeout, readtimeout, context);

		return resps;
	}

	// Admin API 6 GetCallerDetailsByAni
	public org.json.simple.JSONObject CallerDetailsByAni(String url, String callid, int tenantid, String ani,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetCallerDetailsByAni callerdetailsByAni = new GetCallerDetailsByAni();

		org.json.simple.JSONObject resps = callerdetailsByAni.start(url, callid, tenantid, ani, conntimeout,
				readtimeout, context);

		return resps;
	}

//	//Admin API 7 GetCallerDetailsByAni
//	public org.json.simple.JSONObject EmergencyAnnouncementByBusinessObjects(String url, String callid, int tenantid, String key, String dnis, String dnisgroup, String lob, String category, int conntimeout, int readtimeout, LoggerContext context) throws IOException
//	{
//		GetEmergencyAnnouncementByBusinessObjects emergencyannouncement = new  GetEmergencyAnnouncementByBusinessObjects();
//		
//		org.json.simple.JSONObject resps =  emergencyannouncement.start(url, callid,tenantid,key,dnis,dnisgroup,lob,category,conntimeout,readtimeout,context);
//
//		return resps;
//	}

	// Admin API 7 GetIvrApiDetails
	public org.json.simple.JSONObject IvrApiDetails(String url, String callid, int conntimeout, int readtimeout,
			LoggerContext context) throws IOException {
		GetIvrApiDetails Apiivrdetails = new GetIvrApiDetails();

		org.json.simple.JSONObject resps = Apiivrdetails.start(url, callid, conntimeout, readtimeout, context);

		return resps;
	}

	// Admin API 8 GetMainMenuTableByKey
	public org.json.simple.JSONObject MainMenuTableByKey(String url, String callid, int tenantid, String key,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetMainMenuTableByKey ivrdetails = new GetMainMenuTableByKey();

		org.json.simple.JSONObject resps = ivrdetails.start(url, callid, tenantid, key, conntimeout, readtimeout,
				context);

		return resps;
	}

	// Admin API 9 GetMainTableByKey
	public org.json.simple.JSONObject MainTableByKey(String url, String callid, int tenantid, String key,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetMainTableByKey ivrdetailss = new GetMainTableByKey();

		org.json.simple.JSONObject resps = ivrdetailss.start(url, callid, tenantid, key, conntimeout, readtimeout,
				context);

		return resps;
	}

	// Admin API 10 GetPrequeueMessageRulesByBusinessObjects
	public org.json.simple.JSONObject PrequeueMessageRulesByBusinessObjects(String url, String callid, int tenantid,
			String dnis, String dnisgroup, String lob, String category, int conntimeout, int readtimeout,
			LoggerContext context) throws IOException {
		GetPrequeueMessageRulesByBusinessObjects PrequeueMessage = new GetPrequeueMessageRulesByBusinessObjects();

		org.json.simple.JSONObject resps = PrequeueMessage.start(url, callid, tenantid, dnis, dnisgroup, lob, category,
				conntimeout, readtimeout, context);

		return resps;
	}

	// Admin API 11 GetPrequeueMessageRulessByKey
	public org.json.simple.JSONObject PrequeueMessageRulessByKey(String url, String callid, int tenantid, String key,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetPrequeueMessageRulessByKey PrequeueMessageByKey = new GetPrequeueMessageRulessByKey();

		org.json.simple.JSONObject resps = PrequeueMessageByKey.start(url, callid, tenantid, key, conntimeout,
				readtimeout, context);

		return resps;
	}

	// Admin API 12 GetRoutingPlanTableByKey
	public org.json.simple.JSONObject RoutingPlanTableByKey(String url, String callid, int tenantid, String key,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetRoutingPlanTableByKey PrequeueMessageByKey = new GetRoutingPlanTableByKey();

		org.json.simple.JSONObject resps = PrequeueMessageByKey.start(url, callid, tenantid, key, conntimeout,
				readtimeout, context);

		return resps;
	}

	// Admin API 13 GetRoutingTableByBusinessObjects
	public org.json.simple.JSONObject RoutingTableByBusiness(String url, String callid, String key, int tenantid,
			String dnis, String dnisgroup, String lob, String category, int conntimeout, int readtimeout,
			LoggerContext context) throws IOException {
		GetRoutingTableByBusinessObjects RoutingTableByBusine = new GetRoutingTableByBusinessObjects();

		org.json.simple.JSONObject resps = RoutingTableByBusine.start(url, callid, tenantid, key, dnis, dnisgroup, lob,
				category, conntimeout, readtimeout, context);

		return resps;
	}

	// Admin API 14 RoutingTableByKey
	public org.json.simple.JSONObject RoutingTableByKey(String url, String callid, int tenantid, String key,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetRoutingTableByKey RoutingTableByBusine = new GetRoutingTableByKey();

		org.json.simple.JSONObject resps = RoutingTableByBusine.start(url, callid, tenantid, key, conntimeout,
				readtimeout, context);

		return resps;
	}

	// Admin API 15 GetRoutingTableByMenuKey
	public org.json.simple.JSONObject RoutingTableByMenuKey(String url, String callid, int id, int tenantid, String key,
			String dnis, String dnisgroup, String category, String lineofbusiness, int conntimeout, int readtimeout,
			LoggerContext context) throws IOException {
		GetRoutingTableByMenuKey RoutingTableByMenuKey = new GetRoutingTableByMenuKey();

		org.json.simple.JSONObject resps = RoutingTableByMenuKey.start(url, callid, id, key, dnis, dnisgroup, category,
				lineofbusiness, conntimeout, readtimeout, context);

		return resps;
	}

	// Admin API 16 StateGroupTableByKey
	public org.json.simple.JSONObject StateGroupTableByKey(String url, String callid, int tenantid, String key,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetStateGroupTableByKey StateGroupbyKey = new GetStateGroupTableByKey();

		org.json.simple.JSONObject resps = StateGroupbyKey.start(url, callid, tenantid, key, conntimeout, readtimeout,
				context);

		return resps;
	}

	// Admin API 17 StateGroupTableByKey
	public org.json.simple.JSONObject TableByAreaCode(String url, String callid, int tenantid, String areacode,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {

		GetStateTableByAreaCode stateGroupbyAreaCode = new GetStateTableByAreaCode();

		org.json.simple.JSONObject resps = stateGroupbyAreaCode.start(url, callid, tenantid, areacode, conntimeout,
				readtimeout, context);

		return resps;
	}

	// Admin API 18 GetStatePropertyTableByBusinessObjects
	public org.json.simple.JSONObject GetStatePropertyTableByBusinessObjects(String url, String callid, int tenantid,
			String lob, String category, int conntimeout, int readtimeout, LoggerContext context) throws IOException {
		GetStatePropertyTableByBusinessObjects tableByBusinessObjects = new GetStatePropertyTableByBusinessObjects();

		org.json.simple.JSONObject resps = tableByBusinessObjects.start(url, callid, tenantid, lob, category,
				conntimeout, readtimeout, context);
		return resps;
	}

	// Admin API 18 GetStatePropertyTableByBusinessObjects
	public org.json.simple.JSONObject GetIVRTOTEXTINTENT(String url, String callid, int tenantid, String intent,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {

		
		com.farmers.AdminAPI.GetIVRToTextTableByIntent TableByBusinessObjects = new GetIVRToTextTableByIntent();

		org.json.simple.JSONObject resps = TableByBusinessObjects.start(url, callid, tenantid, intent, conntimeout,
				readtimeout, context);
		return resps;
	}
	

	public org.json.simple.JSONObject GetpointOrderIDCard(String url, String tid, String businessunit,
			String policycontractnumber, String mastercompany, String policysource, String deliverymethod, String size,
			String term, String documenttype, String faxnumber, int conntimeout, int readtimeout, LoggerContext context)
			throws IOException {
		PointOrderIDCard_Post GetBillPresentment = new PointOrderIDCard_Post();
		org.json.simple.JSONObject resps = GetBillPresentment.start(url, tid, businessunit, policycontractnumber,
				mastercompany, policysource, deliverymethod, size, term, documenttype, faxnumber, conntimeout,
				readtimeout, context);
		return resps;
	}

	public org.json.simple.JSONObject GetPolicyInquiry_RetrieveInsurance(String url, String tid, String telephonenumber,
			int conntimeout, int readtimeout, LoggerContext context, String region,String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;
		PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post GetPolicyInquiry = new PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post();
		PolicyInquiry_RetrieveInsurancePoliciesbyParty_NP_Post GetPolicyInquiryNP = new PolicyInquiry_RetrieveInsurancePoliciesbyParty_NP_Post();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {

			resps = GetPolicyInquiryNP.start(url, tid, telephonenumber, conntimeout, readtimeout, context, region);
		} else {
			resps = GetPolicyInquiry.start(url, tid, telephonenumber, conntimeout, readtimeout, context);
		}
		return resps;
	}

	public org.json.simple.JSONObject GetMulesoftFarmerPolicyInquiry(String url, String tid,
			String policycontractnumber, String billingaccountnumber, String telephonenumber, int conntimeout,
			int readtimeout, LoggerContext context, String region,String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;
		MulesoftFarmerPolicyInquiry_Post GetMulesoftFarmer = new MulesoftFarmerPolicyInquiry_Post();
		MulesoftFarmerPolicyInquiry_NP_Post GetMulesoftFarmerNP = new MulesoftFarmerPolicyInquiry_NP_Post();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetMulesoftFarmerNP.start(url, tid, policycontractnumber, billingaccountnumber, telephonenumber,
					conntimeout, readtimeout, context, region);
		} else {
			resps = GetMulesoftFarmer.start(url, tid, policycontractnumber, billingaccountnumber, telephonenumber,
					conntimeout, readtimeout, context);

		}
		return resps;
	}

	public org.json.simple.JSONObject GetforemostPolicyInquiry(String url, String tid, String policynumber,
			String systemdate, String policysource, int conntimeout, int readtimeout, LoggerContext context,
			String region,String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;
		ForemostPolicyInquiry_Post GetForemostPolicy = new ForemostPolicyInquiry_Post();
		ForemostPolicyInquiry_NP_Post GetForemostPolicyNP = new ForemostPolicyInquiry_NP_Post();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetForemostPolicyNP.start(url, tid, policynumber, systemdate, policysource, conntimeout,
					readtimeout, context, region);
		} else {
			resps = GetForemostPolicy.start(url, tid, policynumber, systemdate, policysource, conntimeout, readtimeout,
					context);
		}
		return resps;
	}

	public org.json.simple.JSONObject Getpointpolicyinquiry(String url, String input, int conntimeout, int readtimeout,
			LoggerContext context, String region,String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;

		PointPolicyInquiry_Get GetForemostPolicy = new PointPolicyInquiry_Get();
		PointPolicyInquiry_NP_Get GetForemostPolicyNP = new PointPolicyInquiry_NP_Get();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetForemostPolicyNP.start(url, input, conntimeout, readtimeout, context, region);
		} else {
			resps = GetForemostPolicy.start(url, input, conntimeout, readtimeout, context);
		}
		return resps;
	}

	public org.json.simple.JSONObject GetAccountLinkAniLookup(String url, String tid, String telephonenumber,
			int conntimeout, int readtimeout, LoggerContext context, String region,String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;
		AccountLinkAniLookup_Post GetAccountLink = new AccountLinkAniLookup_Post();
		AccountLinkAniLookup_NP_Post GetAccountLinkNP = new AccountLinkAniLookup_NP_Post();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetAccountLinkNP.start(url, tid, telephonenumber, conntimeout, readtimeout, context, region);
		} else {
			resps = GetAccountLink.start(url, tid, telephonenumber, conntimeout, readtimeout, context);
		}
		return resps;
	}

	// PointerIDOrderCard

	public org.json.simple.JSONObject BWOrderIDCard(String url, String tid, String policycontractnumber,
			String mastercompanycode, String deliverymethod, String size, String faxnumber, String emailaddress,
			int conntimeout, int readtimeout, LoggerContext context, String region,String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;
		OrderIDCard_BW_Post GetBWOrderID = new OrderIDCard_BW_Post();
		OrderIDCard_NP_BW_Post GetBWOrderIDNP = new OrderIDCard_NP_BW_Post();
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
			resps = GetBWOrderIDNP.start(url, tid, policycontractnumber, mastercompanycode, deliverymethod, size,
					faxnumber, emailaddress, conntimeout, readtimeout, context, region);
		} else {
			resps = GetBWOrderID.start(url, tid, policycontractnumber, mastercompanycode, deliverymethod, size,
					faxnumber, emailaddress, conntimeout, readtimeout, context);
		}
		return resps;

	}

	public org.json.simple.JSONObject TwentyOrderIDCard(String url, String tid, String policycontractnumber,
			String mastercompanycode, String deliverymethod, String size, String faxnumber, String emailaddress,
			int conntimeout, int readtimeout, LoggerContext context, String region,String UAT_FLAG) throws IOException {
		OrderIDCard_21C_Post Get21OrderID = new OrderIDCard_21C_Post();
		OrderIDCard_21C_NP_Post Get21OrderID_NP = new OrderIDCard_21C_NP_Post();
		org.json.simple.JSONObject resps=null;
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
		resps = Get21OrderID_NP.start(url, tid, policycontractnumber, mastercompanycode,
				deliverymethod, size, faxnumber, emailaddress, conntimeout, readtimeout, context,region);
		}else {
			resps = Get21OrderID.start(url, tid, policycontractnumber, mastercompanycode,
					deliverymethod, size, faxnumber, emailaddress, conntimeout, readtimeout, context);
		}

		return resps;

	}

	public org.json.simple.JSONObject FDSOrderIDCard(String url, String tid, String policycontractnumber,
			String policysource, String deliverymethod, String size, String faxnumber, String emailaddress,
			int conntimeout, int readtimeout, LoggerContext context, String region,String UAT_FLAG) throws IOException {
		OrderIDCard_FDS GetFDSOrderID = new OrderIDCard_FDS();
		//START Non prod Changes by Priya
		OrderIDCard_NP_FDS GetFDSOrderID_NP = new OrderIDCard_NP_FDS();
		org.json.simple.JSONObject resps=null;
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
	            resps = GetFDSOrderID_NP.start(url, tid, policycontractnumber, policysource,
				deliverymethod, size, faxnumber, emailaddress, conntimeout, readtimeout, context, region);
		}else {
			 resps = GetFDSOrderID.start(url, tid, policycontractnumber, policysource,
						deliverymethod, size, faxnumber, emailaddress, conntimeout, readtimeout, context);
		}
         
		//END Non Prod changes by Priya
		return resps;

	}

	public org.json.simple.JSONObject FWSOrderIDCard(String url, String tid, String policynumber, String policysource,
			String lob, String policystate, String policysuffix, String deliverymethod, String effectivedate,
			String faxnumber, String firstname, String lastname, String middlename, String internalpolicynumber,
			String internalpolicyversion, String emailAddress,int conntimeout, int readtimeout, LoggerContext context, String region,String UAT_FLAG) throws IOException {
		OrderIDCard_FWS_Post GetFWSOrderID = new OrderIDCard_FWS_Post();
		//START Non prod Changes by Priya
		OrderIDCard_FWS_NP_Post GetFWSOrder_NP_ID = new OrderIDCard_FWS_NP_Post();
		org.json.simple.JSONObject resps=null;
		if ("YES".equalsIgnoreCase(UAT_FLAG)) {
		resps = GetFWSOrder_NP_ID.start(url, tid, policynumber, policysource, lob, policystate,
				policysuffix, deliverymethod, effectivedate, faxnumber, firstname, lastname, middlename,
				internalpolicynumber, internalpolicyversion,emailAddress, conntimeout, readtimeout, context,region);
		}else {
			resps = GetFWSOrderID.start(url, tid, policynumber, policysource, lob, policystate,
					policysuffix, deliverymethod, effectivedate, faxnumber, firstname, lastname, middlename,
					internalpolicynumber, internalpolicyversion,emailAddress,conntimeout, readtimeout, context);
		}
		//END Non Prod changes by Priya

		return resps;

	}

	public org.json.simple.JSONObject TableByStateGroupName(String url, String callid, int tenantid, String name,
			int conntimeout, int readtimeout, LoggerContext context) throws IOException {

		GetStateGroupTableByStateName StateGroupbyStateName = new GetStateGroupTableByStateName();
		org.json.simple.JSONObject resps = StateGroupbyStateName.start(url, callid, tenantid, name, conntimeout,
				readtimeout, context);

		return resps;
	}

	public org.json.simple.JSONObject AgentInfoRetrieveByAOR(String url, String tid, String aorid, String businessunit,
			String userid, String systemname, String terminatedagentindicator, int conntimeout, int readtimeout,LoggerContext context, String region,String UAT_FLAG) throws IOException {
		org.json.simple.JSONObject resps = null;
		AgentInfoRetrieveByAOR_Post GetAgentInfoByAOR = new AgentInfoRetrieveByAOR_Post();
		AgentInfoRetrieveByAOR_NP_Post GetAgentInfoByAORNP = new AgentInfoRetrieveByAOR_NP_Post();
		if ("YES".equalsIgnoreCase(UAT_FLAG))
		{
			resps = GetAgentInfoByAORNP.start(url, tid, aorid, businessunit, userid, systemname,
					terminatedagentindicator, conntimeout, readtimeout, context, region);

		} else {
			resps = GetAgentInfoByAOR.start(url, tid, aorid, businessunit, userid, systemname, terminatedagentindicator,
					conntimeout, readtimeout, context);
		}
		return resps;

	}
		
		//START : CS1245054:Mdm FWS PhoneNoLookup changes
		
		public org.json.simple.JSONObject GetMdmFWSPhoneNoLookup(String url, String tid, String telephonenumber, String excludedSource, int conntimeout, int readtimeout,
				LoggerContext context, String region,String UAT_FLAG) throws IOException {
			org.json.simple.JSONObject resps = null;
			
			MdmFWSPPhoneNoLookup_Post GetPolicy = new MdmFWSPPhoneNoLookup_Post();
			MdmFWSPPhoneNoLookup_NP_Post GetPolicyNP = new MdmFWSPPhoneNoLookup_NP_Post();
			if ("YES".equalsIgnoreCase(UAT_FLAG)) {
				resps = GetPolicyNP.start(url, tid, telephonenumber, excludedSource,
						conntimeout, readtimeout, context, region);
			} else {
				resps = GetPolicy.start(url, tid, telephonenumber,excludedSource, conntimeout,
						readtimeout, context);
			}
			return resps;

		}

		public org.json.simple.JSONObject GetRiskAttritionAPILookup(String wsurl, String tid, String policyContractNumber, String policySource, String timestamp, int conntimeout,
				int readtimeout, LoggerContext context, String region, String UAT_FLAG) {
			
			org.json.simple.JSONObject resps = null;
			RiskAttritionAPILookup_Post GetPercentile = new RiskAttritionAPILookup_Post();
			RiskAttritionAPILookup_NP_Post GetPercentileNP = new RiskAttritionAPILookup_NP_Post();
			if ("YES".equalsIgnoreCase(UAT_FLAG)) {
				resps = GetPercentileNP.start(wsurl, tid, policyContractNumber,policySource,timestamp,
						conntimeout, readtimeout, context, region);
			} else {
				resps = GetPercentile.start(wsurl, tid, policyContractNumber,policySource, timestamp, conntimeout,
						readtimeout, context);
			}
			return resps;
		}
		
		//END : CS1245054:Mdm FWS PhoneNoLookup changes
		
		//CS1184350 - NewClaims API - Arshath - Start
		//add a variable function name 
		public org.json.simple.JSONObject GetClaimsLookup(String url, String tid, String telephonenumber, String functionName, String claimNumber,String policyNumber,int conntimeout,
				int readtimeout, LoggerContext context, String region, String UAT_FLAG) throws IOException {
				org.json.simple.JSONObject resps = null;
				NewClaimsLookup_Post GetClaims = new NewClaimsLookup_Post();
				NewClaimsLookup_NP_Post GetClaimsNP = new NewClaimsLookup_NP_Post();
				if ("YES".equalsIgnoreCase(UAT_FLAG)) {
					resps = GetClaimsNP.start(url, tid, telephonenumber,functionName,claimNumber,policyNumber, conntimeout, readtimeout, context, region);
				} else {
					resps = GetClaims.start(url, tid, telephonenumber,functionName,claimNumber,policyNumber,conntimeout,readtimeout, context);
				}
				return resps;

			}
		//CS1184350 - NewClaims API - Arshath - END

		public org.json.simple.JSONObject FNWLACELookup_Post(String url, String tid, String ani, String aor, String lookupType, String region, String UAT_FLAG, int conntimeout, int readtimeout, LoggerContext context) throws IOException {
			org.json.simple.JSONObject resps = null;
			FNWLACELookup_Post FNWLACELookup = new FNWLACELookup_Post();
			FNWLACELookup_NP_Post FNWLACELookupNP = new FNWLACELookup_NP_Post();
			
			if ("YES".equalsIgnoreCase(UAT_FLAG)) {
				resps = FNWLACELookupNP.start(url, tid, ani, aor, lookupType, region, conntimeout, readtimeout, context);
			}
			else{
				resps = FNWLACELookup.start(url, tid, ani, aor, lookupType, conntimeout, readtimeout, context);
			}
			return resps;
		}
		
		public org.json.simple.JSONObject FNWLMDMLookup_Post(String url, String tid, String ani, String policyNumber, String dob, String zip, String ssn, String lookupType, String region, String UAT_FLAG, int conntimeout, int readtimeout, LoggerContext context) throws IOException {
			org.json.simple.JSONObject resps = null;
			FNWLMDMLookup_Post FNWLMDMLookup = new FNWLMDMLookup_Post();
			FNWLMDMLookup_NP_Post FNWLMDMLookupNP = new FNWLMDMLookup_NP_Post();
			
			if ("YES".equalsIgnoreCase(UAT_FLAG)) {
				resps = FNWLMDMLookupNP.start(url, tid, ani, policyNumber, dob, zip, ssn, lookupType, region, conntimeout, readtimeout, context);
			}
			else {
				resps = FNWLMDMLookup.start(url, tid, ani, policyNumber, dob, zip, ssn, lookupType, conntimeout, readtimeout, context);
			}
			return resps;
		}
		
		public org.json.simple.JSONObject FNWLEPCPaymentUS_Post(String url, String tid, String operation, String id, String authtoken, String languagepreference, Double balanceDue, String policySource, String existingCustomer, String firstName, String lastName, long phoneNumber, String email, String line1, String state, String city, String zip, String country, String region, String UAT_FLAG, int conntimeout, int readtimeout, LoggerContext context) throws IOException {
			org.json.simple.JSONObject resps = null;
			FNWLEPCPaymentus_Post FNWLEPCPaymentUSLookup = new FNWLEPCPaymentus_Post();
			FNWLEPCPaymentus_NP_Post FNWLEPCPaymentUSLookupNP = new FNWLEPCPaymentus_NP_Post();
			
			if ("YES".equalsIgnoreCase(UAT_FLAG)) {
				resps = FNWLEPCPaymentUSLookupNP.start(url, tid, operation, id, authtoken, languagepreference, balanceDue, policySource, existingCustomer, firstName, lastName, phoneNumber, email, line1, state, city, zip, country, region, conntimeout, readtimeout, context);
			}
			else {
				resps = FNWLEPCPaymentUSLookup.start(url, tid, operation, id, authtoken, languagepreference, balanceDue, policySource, existingCustomer, firstName, lastName, phoneNumber, email, line1, state, city, zip, country, conntimeout, readtimeout, context);
			}
			return resps;
		}
	
}
