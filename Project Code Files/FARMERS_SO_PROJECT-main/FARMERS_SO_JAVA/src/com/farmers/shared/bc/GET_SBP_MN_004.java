package com.farmers.shared.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SBP_MN_004 extends DecisionElementBase 
{
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String strReturnValue = (String) data.getElementData("SBP_MN_004_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SBP_MN_004"+" :: Menu Value : "+strReturnValue);
			if(strReturnValue.contains("$")) {
				strReturnValue = strReturnValue.replace("$", "");
				data.addToLog(currElementName, "Menu ID : "+"SBP_MN_004"+" :: Menu Value post $ replace : "+strReturnValue);
			}
			data.setSessionData("SBP_MN_004_VALUE", strReturnValue);
			if(strReturnValue != null && !strReturnValue.equals("")) {
				if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				}else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				}if (strReturnValue.equalsIgnoreCase("Minimum Due") || strReturnValue.equalsIgnoreCase("MinumumDue")) {
					data.setSessionData("S_Current_Balance", data.getSessionData("S_MINIMUMDUE"));
					strExitState = "Minimum Due";
				} else if (strReturnValue.equalsIgnoreCase("Full Balance")) {
					data.setSessionData("S_Current_Balance", data.getSessionData("S_FULLBALANCE"));
					strExitState = "Full Balance";
				} else {
					strExitState = "invalid";
					try {
						Pattern p = Pattern.compile("([0-9])");
						Matcher m = p.matcher(strReturnValue);
						if(m.find()) {
							data.addToLog(currElementName, "SBP_MN_004 value having all numbers");
							String nextPaymentAmount = (String)data.getSessionData("S_NextPayment_Amount");
							if(null == nextPaymentAmount || nextPaymentAmount.isEmpty()) nextPaymentAmount = "0";
							Double amount = Double.parseDouble(nextPaymentAmount);
							Double inputAmt = Double.parseDouble(strReturnValue);
							data.addToLog(currElementName, "post double conversion : nextPaymentAmount : "+amount+" SBP_MN_004 inputAmt : "+inputAmt);
							if(null != amount && null != inputAmt && inputAmt >= 1.00 && inputAmt <= amount) {
								data.setSessionData("Valid_Get_Payment", "TRUE");
								data.setSessionData("S_Current_Balance", inputAmt);
								strExitState = "valid";
								data.addToLog(currElementName, " SBP_MN_004 : Valid amount");
							}
						}
					} catch (Exception e) {
						data.addToLog(currElementName,"Exception in SBP_MN_004 while checking valid/invalid input :: "+e);
						caa.printStackTrace(e);
					}
					
				}
			}

		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SBP_MN_004 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SBP_MN_004 :: "+strExitState);
		return strExitState;

	}
	
	public static void main(String[] args) {
		String strReturnValue = "1";
		if(strReturnValue.contains("$")) {
			strReturnValue = strReturnValue.replace("$", "");
			System.out.println( "Menu ID : "+"FWS_SBP_MN_004"+" :: Menu Value post $ replace : "+strReturnValue);
		}
		
		String strExitState = "ER";
		String strInputAmt = "500";
		Pattern p = Pattern.compile("([0-9])");
		Matcher m = p.matcher(strInputAmt);
		if(m.find()) {
			 System.out.println("Hello "+m.find());
			strExitState = "valid";
			String nextPaymentAmount = "0";
			Double amount = Double.parseDouble(nextPaymentAmount);
			Double inputAmt = Double.parseDouble(strInputAmt);
			if(null != amount && null != inputAmt && inputAmt > 1.00 && inputAmt < amount) {
				  System.out.println("Valid_Get_Payment : TRUE ");
			} else {
				System.out.println("Valid_Get_Payment : False ");
			}
		} else {
			strExitState = "invalid";
		}
	}

}