package com.farmers.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.audium.server.session.DecisionElementData;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.SequenceNumber;

public class SetHostDetails {
	
	public CommonAPIAccess caa;
	public HostBean objHostBean = new HostBean();
	SimpleDateFormat objSimpleDateFormat = new SimpleDateFormat(Constants.REPORT_DATE_FORMAT);
	
	public SetHostDetails(CommonAPIAccess caa){
		this.caa = caa;
		objHostBean.setHostAccStartTime(objSimpleDateFormat.format(Calendar.getInstance().getTimeInMillis()));
	}
	
	public void setinitalValue() {
		//objHostBean.setHostAccStartTime(objSimpleDateFormat.format(Calendar.getInstance().getTime()));
	}
	
	public void startHostReport(String hostID,String methodName,String InputParam,String Region, String url){
		
		try{
			SequenceNumber sequenceNumber=(SequenceNumber)caa.getFromSession(Constants.S_SEQUENCE_COUNTER,null);
			objHostBean.setSequenceNum(sequenceNumber.get());
			caa.setToSession(Constants.S_SEQUENCE_COUNTER, sequenceNumber);
			objHostBean.setHostID(hostID);
			objHostBean.setHostMethod(methodName);
			objHostBean.setHostInParams(InputParam);
			objHostBean.setHostType(Constants.WS);
			objHostBean.setHostRegion(Region);
			objHostBean.setHostEndpoint(url);
			//objHostBean.setHostAccStartTime(objSimpleDateFormat.format(Calendar.getInstance().getTime()));
		}catch(Exception e){
			caa.printStackTrace(e);
		}
	
	}
	
	@SuppressWarnings("unchecked")
	public void endHostReport(DecisionElementData data,String OutParam,String result, String responseCode, String apiString){
		ArrayList<HostBean> listHostDetails = null;
				
		try{
			objHostBean.setHostAccEndTime(objSimpleDateFormat.format(Calendar.getInstance().getTimeInMillis()));
			objHostBean.setHostOutParams(OutParam);
			objHostBean.setHostErrCode(responseCode);
			objHostBean.setHostErrDesc(Constants.EmptyString);
			objHostBean.setHostResponse(result);
			objHostBean.setStrHostString(apiString);
			
			listHostDetails = (ArrayList<HostBean>)data.getSessionData(Constants.S_HOST_DETAILS_LIST);
			if(listHostDetails !=null){
				listHostDetails.add(objHostBean);
			}else{
				listHostDetails = new ArrayList<HostBean>();
				listHostDetails.add(objHostBean);
			}
			data.setSessionData(Constants.S_HOST_DETAILS_LIST,listHostDetails);
			data.addToLog(data.getCurrentElement(), ""+objHostBean.getSequenceNum());
		}catch(Exception e){
			caa.printStackTrace(e);
		}
	
	}
	
}
