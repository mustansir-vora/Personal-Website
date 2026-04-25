package com.farmers.bc;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class DOBvalidation  extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String strCallerinputDOB = null;
		String strAPIDOB = null;
		try {

			if (data.getSessionData(Constants.S_CALLER_INPUT_DOB) != null) {

				strCallerinputDOB = (String) data.getSessionData(Constants.S_CALLER_INPUT_DOB);
				data.addToLog(currElementName,"S_CALLER_INPUT_DOB :: "+strCallerinputDOB);
			}

			if (data.getSessionData(Constants.S_API_DOB) != null) {

				strAPIDOB = (String) data.getSessionData(Constants.S_API_DOB);
				data.addToLog(currElementName,"S_API_DOB :: "+strAPIDOB);
			}

			String [] strAPIDOBArr = strAPIDOB.split(",");
			
			for(int singleStrAPIDOBArr = 0; singleStrAPIDOBArr < strAPIDOBArr.length;singleStrAPIDOBArr++) {
				strAPIDOB = strAPIDOBArr[singleStrAPIDOBArr];
				data.addToLog(currElementName,"strAPIDOB :: "+strAPIDOB);
				if(null != strAPIDOB && null != strCallerinputDOB && (strCallerinputDOB.equals(strAPIDOB)||strAPIDOB.contains(strCallerinputDOB))) {
					strExitState =  Constants.STRING_YES;
					data.setSessionData("IS_CALLED_SHARED_ID_AUTH","TRUE");
				}else if(null != strAPIDOB && null != strCallerinputDOB) {
					 SimpleDateFormat callerInputFormat = new SimpleDateFormat("yyyy-MM-dd");
				        SimpleDateFormat targetFormat = new SimpleDateFormat("M/d/yyyy");

				        Date callerDate = callerInputFormat.parse(strCallerinputDOB);
				        String formattedCallerDOB = targetFormat.format(callerDate);

				        if (formattedCallerDOB.equals(strAPIDOB)) {
				            strExitState = Constants.STRING_YES;
				            data.setSessionData("IS_CALLED_SHARED_ID_AUTH", "TRUE");
				        }
				}
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCBA_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"DOBvalidation :: "+strExitState);
		return strExitState;
	}
}