package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class PreTransfer extends ActionElementBase {
	public void doAction(String name, ActionElementData data) throws AudiumException {
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String strElementName = data.getCurrentElement();
		try	{
			
			String strFinalDestNum = (String)caa.getFromSession(Constants.S_FINAL_DESTNUM, "");
			String strCcallid = (String)caa.getFromSession(Constants.S_OCALLID, "");
			String strActiveLang = (String)caa.getFromSession(Constants.S_PREF_LANG, "");
			Object strPriority = (Object)caa.getFromSession(Constants.S_ROUTING_PRIORITY,"");
			String strStateCode = (String)caa.getFromSession(Constants.S_STATECODE, "UN");
			String strBUid =(String)caa.getFromSession(Constants.S_BU_ID, "");
			String strCallRoutedStatus = (String)caa.getFromSession(Constants.S_CALL_ROUTED_STATUS, "");
			String strCGUID = (String)caa.getFromSession(Constants.S_CGUID, "");
			String strOriginalDNIS = (String)caa.getFromSession(Constants.S_ORIGINAL_DNIS, "");
			String strOriginalANI = (String)caa.getFromSession(Constants.S_ORIGINAL_ANI, "");
			String strRoutingKey = (String)caa.getFromSession(Constants.S_ROUTING_KEY, "");
			String strBE = (String)caa.getFromSession(Constants.S_BE, "M");
			String strSFD = (String)caa.getFromSession("SFID", "");
			String strDR = (String)caa.getFromSession(Constants.S_DR,"N");
			data.setSessionData("S_TRANSFER_FLAG","Y");
			
			// START : CS1254867 , Short description: All Brands - IVR is passing incorrect Variable2 string to UCCE, resulting in single-digit State Value populating in TCD tab
			if (strPriority == null || strPriority.toString().isEmpty()) {
			    strPriority = "08";
			    data.addToLog(strElementName,"strPriority:: "+strPriority);
			} else {
			    strPriority = "0" + strPriority.toString();
			    data.addToLog(strElementName,"strPriority::: "+strPriority);
			}
			//END : CS1254867 , Short description: All Brands - IVR is passing incorrect Variable2 string to UCCE, resulting in single-digit State Value populating in TCD tab
			
			//strPriority="0"+strPriority;
			caa.printToLog("strFinalDestNum :"+strFinalDestNum,CommonAPIAccess.INFO);
			caa.printToLog("strCcallid :"+strCcallid,CommonAPIAccess.INFO);
			caa.printToLog("strActiveLang :"+strActiveLang,CommonAPIAccess.INFO);
			caa.printToLog("strPriority :"+strPriority,CommonAPIAccess.INFO);
			caa.printToLog("strStateCode :"+strStateCode,CommonAPIAccess.INFO);
			caa.printToLog("strBUid :"+strBUid,CommonAPIAccess.INFO);
			caa.printToLog("strCallRoutedStatus :"+strCallRoutedStatus,CommonAPIAccess.INFO);
			caa.printToLog("strOriginalDNIS :"+strOriginalDNIS,CommonAPIAccess.INFO);
			caa.printToLog("strOriginalANI :"+strOriginalANI,CommonAPIAccess.INFO);
			caa.printToLog("strRoutingKey :"+strRoutingKey,CommonAPIAccess.INFO);
			caa.printToLog("strBE :"+strBE,CommonAPIAccess.INFO);
			caa.printToLog("strCGUID :"+strCGUID,CommonAPIAccess.INFO);
			caa.printToLog("strDR :"+strDR,CommonAPIAccess.INFO);
			
			try {
				if(strFinalDestNum!=null && strFinalDestNum.startsWith("*")) {
					strRoutingKey="";
				}else {
					long checkFinalDest = Long.parseLong(strFinalDestNum);
					strRoutingKey="";
				}
				
				caa.printToLog("strRoutingKey :"+strRoutingKey+"strFinalDestNum :"+strFinalDestNum,CommonAPIAccess.INFO);
			}catch(Exception e) {
				strRoutingKey=strFinalDestNum;
				strFinalDestNum="";
				caa.printToLog("strRoutingKey :"+strRoutingKey+"strFinalDestNum :"+strFinalDestNum,CommonAPIAccess.INFO);
				
			}
			
			//For testing
			//strBUid = "025";
			String strVXML0=new StringBuffer(strFinalDestNum).append('|').append(strCcallid).append('|').append(strActiveLang).append('|').append(strPriority.toString()).append('|')
					.append(strStateCode).append('|').append(strBUid).append('|').append(strBE).append('|').append(strDR).toString();
			String strVXML1=new StringBuffer(strCGUID).append('|').append(strOriginalDNIS).append('|').append(strOriginalANI).append('|').append(strSFD).toString();
			String strVXML2=new StringBuffer(strRoutingKey).toString();
			
			caa.setToSession(Constants.S_VXML0, strVXML0);
			caa.setToSession(Constants.S_VXML1, strVXML1);
			caa.setToSession(Constants.S_VXML2, strVXML2);

			caa.printToLog(new StringBuffer(" | VXML[0] = ").append(strVXML0).toString(),CommonAPIAccess.INFO);
			caa.printToLog(new StringBuffer(" | VXML[1] = ").append(strVXML1).toString(),CommonAPIAccess.INFO);
			caa.printToLog(new StringBuffer(" | VXML[2] = ").append(strVXML2).toString(),CommonAPIAccess.INFO);


		} catch (Exception e) {
			data.addToLog(strElementName,"Exception in apiResponseManupulation  :: "+e);
			caa.printStackTrace(e);
		}
	}
	
	public static void main(String[] args) {
		Long.parseLong("99922299922");
	}
}
