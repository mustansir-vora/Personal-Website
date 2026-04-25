package com.farmers.shared.adminhost;

import java.util.ArrayList;


import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FWSBillingLookup.ARS.PolicyBillingLookupARS;
import com.farmers.bean.StateGroup.Details;
import com.farmers.bean.StateGroup.Re;
import com.farmers.bean.StateGroup.StateGroup;
import com.farmers.bean.StateGroup.StateGroupRoot;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.google.gson.Gson;

public class StateGroupTableByKey extends DecisionElementBase 

{

	@Override
	public String doDecision(String name, DecisionElementData data) throws AudiumException 
	{
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;


		try
		{

			String STATEGROUPURL = (String) data.getSessionData(Constants.S_STATEGROUPURL);
			data.addToLog("STATEGROUPURL", STATEGROUPURL);
			int TENANTID = Constants.tenantid;
			String CALLID = (String) data.getSessionData(Constants.S_CALLID);
			data.addToLog("CALLID", CALLID);
			String KEY = (String) data.getSessionData(Constants.S_STATENAME);
			data.addToLog("KEY", KEY);
			String  conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
			String  readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			Lookupcall lookups = new Lookupcall();
			org.json.simple.JSONObject responses = lookups.TableByStateGroupName(STATEGROUPURL, CALLID, TENANTID,KEY,Integer.parseInt(conntimeout), Integer.parseInt(readtimeout),context);		
			String GSONLIB =responses.toString();
			Gson gsonobj = new Gson();

			StateGroupRoot obj = gsonobj.fromJson(GSONLIB, StateGroupRoot.class);
			ArrayList<Re> ArrayDetails = obj.getResponseBody().getRes(); 
			data.addToLog("Reponses", responses.toString());
			for(int i=0;i<ArrayDetails.size();i++)
			{
				String StateGroupName = ArrayDetails.get(i).getKey();

				if(StateGroupName.equalsIgnoreCase(Constants.FARMERSNOPRODUCTSTATE)||StateGroupName.equalsIgnoreCase(Constants.FARMERSNOPRODUCTSTATEFGIA))
				{
					data.setSessionData("S_STATEGROUP_STATUS", "AHNOPRODUCT");
					data.addToLog("S_STATEGROUP_STATUS", "AHNOPRODUCT");
					StrExitState = Constants.SU;

				}
				else if(StateGroupName.equalsIgnoreCase(Constants.FARMERSAUTOGREEN)||StateGroupName.equalsIgnoreCase(Constants.FARMERSAUTONEUTRAL)||StateGroupName.equalsIgnoreCase(Constants.FARMERSAUTORED))
				{
					data.setSessionData("S_STATEGROUP_STATUS", "AHOGNR");	
					data.addToLog("S_STATEGROUP_STATUS", "AHOGNR");	
					StrExitState = Constants.SU;

				}
				else if(StateGroupName.equalsIgnoreCase(Constants.FARMERSHOMEGREEN)||StateGroupName.equalsIgnoreCase(Constants.FARMERSHOMENEUTRAL)||StateGroupName.equalsIgnoreCase(Constants.FARMERSHOMERED))
				{
					data.setSessionData("S_STATEGROUP_STATUS", "AHOGNR");	
					data.addToLog("S_STATEGROUP_STATUS", "AHOGNR");	
					StrExitState = Constants.SU;

				}
				else if(StateGroupName.equalsIgnoreCase(Constants.FARMERSNOBINDAUTO)||StateGroupName.equalsIgnoreCase(Constants.FARMERSNOBINDHOME)||StateGroupName.equalsIgnoreCase(Constants.FARMERSNOBINDOTHR)
						||StateGroupName.equalsIgnoreCase(Constants.FARMERSNOBINDOTHRWBO))
				{
					data.setSessionData("S_STATEGROUP_STATUS", "AHONOBIND");	
					data.addToLog("S_STATEGROUP_STATUS", "AHONOBIND");	
					StrExitState = Constants.SU;
				}
				else if(StateGroupName.equalsIgnoreCase(Constants.FARMERSNOPRODUCTSTATE)||StateGroupName.equalsIgnoreCase(Constants.FARMERSNOPRODUCTSTATEFGIA))
				{
					data.setSessionData("S_STATEGROUP_STATUS", "AHNOPRODUCT");	
					data.addToLog("S_STATEGROUP_STATUS", "AHNOPRODUCT");	
					StrExitState = Constants.SU;

				}
				else if(StateGroupName.equalsIgnoreCase("HI"))
				{
					data.setSessionData("S_STATEGROUP_STATUS", "AHNOPRODUCT");	
					data.addToLog("S_STATEGROUP_STATUS", "AHNOPRODUCT");	
					StrExitState = Constants.SU;

				}
			}

		}	catch (Exception e)
		{
			data.addToLog(name,"Exception while forming host reporting for FSLS_ADM_001  StategroupName call  :: "+e);
			caa.printStackTrace(e);
		}


		return StrExitState;
	}

}
