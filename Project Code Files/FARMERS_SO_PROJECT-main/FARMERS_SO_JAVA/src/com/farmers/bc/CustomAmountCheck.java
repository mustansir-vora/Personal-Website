package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CustomAmountCheck extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		String customAmount = Constants.EmptyString;
		String fullBalance21st = Constants.EmptyString;
		String minimumDue21st = Constants.EmptyString;
		String strReturnValue = Constants.EmptyString;
		String strAmountValidationInvokedFrom = Constants.EmptyString;
		boolean validCustomAmount = false;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			strAmountValidationInvokedFrom = (String) data.getSessionData("S_AMOUNT_VALIDATION_INVOKED_FROM");
			data.addToLog(data.getCurrentElement(),
					"Amount Validation Invoked From :: " + strAmountValidationInvokedFrom);
			if (null != strAmountValidationInvokedFrom
					&& "TFGPA_MN_001".equalsIgnoreCase(strAmountValidationInvokedFrom)) {
				data.addToLog(data.getCurrentElement(), "Custom Amount enterred from without min due i.e MN_001");
				strReturnValue = data.getElementData("TFGPA_MN_001_Call", "Return_Value");
			} else if (null != strAmountValidationInvokedFrom
					&& "TFGPA_MN_002".equalsIgnoreCase(strAmountValidationInvokedFrom)) {
				data.addToLog(data.getCurrentElement(), "Custom Amount enterred from with min due i.e MN_002");
				strReturnValue = data.getElementData("TFGPA_MN_002_Call", "Return_Value");
			} else if (null != strAmountValidationInvokedFrom
					&& "TFGPA_MN_003".equalsIgnoreCase(strAmountValidationInvokedFrom)) {
				data.addToLog(data.getCurrentElement(),
						"Custom Amount enterred from with min due and re-entry i.e MN_003");
				strReturnValue = data.getElementData("TFGPA_MN_003_Call", "Return_Value");
			} else {
				data.addToLog(data.getCurrentElement(), "Custom Amount value is null from MN_001, MN_002, MN_0003");
			}
			String NewstrReturnValue = strReturnValue.replace("$", "");
			NewstrReturnValue = NewstrReturnValue.replaceAll("\\s", "");

			NewstrReturnValue = NewstrReturnValue.replaceAll(",", "");
			data.addToLog(currElementName, "Removed $ character in NewstrReturnvalue:: " + NewstrReturnValue);

			String amountReEntryFlag = (String) data.getSessionData("S_AMOUNT_RE_ENTRY_FLAG");

			if (null != amountReEntryFlag && "Y".equalsIgnoreCase(amountReEntryFlag)) {
				customAmount = NewstrReturnValue;
				fullBalance21st = (String) data.getSessionData("S_Renewal_Balance");
				minimumDue21st = (String) data.getSessionData("S_Current_Balance");
				data.addToLog(currElementName, "Amount re-entry Iteration");
				data.setSessionData("fullBalance21st", fullBalance21st);
				data.setSessionData("minimumDue21st", minimumDue21st);
			} else {
				customAmount = NewstrReturnValue;
				data.addToLog(currElementName, "Amount entry normal Iteration");
				fullBalance21st = (String) data.getSessionData("S_Renewal_Balance");
				minimumDue21st = (String) data.getSessionData("S_Current_Balance");
				data.setSessionData("fullBalance21st", fullBalance21st);
				data.setSessionData("minimumDue21st", minimumDue21st);
			}

			Pattern patt = Pattern.compile("\\d+");

			Matcher matcher = patt.matcher(customAmount);

			while (matcher.find()) {
				customAmount = matcher.group();
			}

			boolean onlyNumbers = patt.matcher(customAmount).matches();

			if (onlyNumbers) {
				data.addToLog(data.getCurrentElement(), "It has only numbers");
			} else {
				data.addToLog(data.getCurrentElement(), "It has not only numbers");
			}

			data.addToLog(data.getCurrentElement(), "Custom Amount Debugging :: " + customAmount + " :: fullBalance21st"
					+ fullBalance21st + "::minimumDue21st" + minimumDue21st);
			data.addToLog(data.getCurrentElement(), "New Custom Amount ::" + NewstrReturnValue);

			if ((null != customAmount) && (null != fullBalance21st && Float.parseFloat(fullBalance21st) > 0)) {
				if ((Float.parseFloat(customAmount) > 0)
						&& (Float.parseFloat(customAmount) <= Float.parseFloat(fullBalance21st))
						&& ((Float.parseFloat(customAmount) >= Float.parseFloat(minimumDue21st)))) {
					validCustomAmount = true;
					data.addToLog(data.getCurrentElement(), "Enterred amount is more than 0 :: " + customAmount);
					data.addToLog(data.getCurrentElement(),
							"Enterred amount is less than minimum Due :: Enterred amount :: " + customAmount
									+ " :: Min Due :: " + minimumDue21st);
				} else {
					data.addToLog(data.getCurrentElement(), "Enterred amount is less than 0 :: " + customAmount);
				}
				data.addToLog(data.getCurrentElement(), "Post validation of Custom amount :: " + validCustomAmount);

				if (validCustomAmount) {
					data.addToLog(data.getCurrentElement(),
							"Enterred Amount is valid :: " + customAmount + " :: Min Due :: " + minimumDue21st
									+ " :: full Balance :: " + fullBalance21st
									+ " :: Enterred amount is within min and full and so validation is successful");
					data.setSessionData("S_PAYMENTAMOUNT", customAmount);
					data.setSessionData("S_AMOUNT_TOBE_SENT", customAmount);
					data.setSessionData(Constants.VXMLParam1, (String) data.getSessionData("S_AMOUNT_TOBE_SENT"));
					strExitState = "YES";
				} else {
					data.addToLog(data.getCurrentElement(),
							"Enterred amount is not validated in the amount validations and so checking the min due :: min due amount :: "
									+ minimumDue21st + " :: custom amount :: " + customAmount);
					if (null != minimumDue21st && Float.parseFloat(minimumDue21st) > 0) {
						data.addToLog(data.getCurrentElement(),
								"Minimum due is present and so setting the exitstate as NO_WITH_MIN_DUE");
						strExitState = "NO_WITH_MIN_DUE";
					} else {
						data.addToLog(data.getCurrentElement(),
								"Minimum due is not present and so setting the exitstate as NO_WITHOUT_MIN_DUE");
						strExitState = "NO_WITHOUT_MIN_DUE";
					}
				}

				data.addToLog(data.getCurrentElement(),"Check whether it is rentry flow or not :: " + amountReEntryFlag);

				if (null != amountReEntryFlag && "Y".equalsIgnoreCase(amountReEntryFlag)) {
					data.addToLog(data.getCurrentElement(),
							"Re-Entry Flow and so routing the call to initial validation :: " + amountReEntryFlag);
					strExitState = "INVOKE_INITIAL_VALIDATION";
				}

			} else {
				data.addToLog(currElementName, "Custom Amount Full Balance is Null");
			}

			data.addToLog(data.getCurrentElement(), "Exit State from  Amount Validation :: " + strExitState);

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in CustomAmountCheck :: " + e);
			caa.printStackTrace(e);
		}

		data.addToLog(currElementName, "CustomAmountCheck :: " + strExitState);
		return strExitState;
	}

	public static void main(String a[]) {
		System.out.println("testing purpose");
		String customamount = "1.00";
		String fullBalance21st = "0.00";
		String minimumDue21st = "0.00";
		boolean validCustomAmount = false;

		if ((null != customamount) && (null != fullBalance21st && Float.parseFloat(fullBalance21st) > 0)) {
			System.out.println("Inside if else");

			if ((Float.parseFloat(customamount) > 0)
					&& (Float.parseFloat(customamount) <= Float.parseFloat(fullBalance21st))
					&& ((Float.parseFloat(customamount) >= Float.parseFloat(minimumDue21st)))) {
				System.out.println("Enterred amount is more than 0 :: " + customamount);
				validCustomAmount = true;
				System.out.println("Enterred amount is less than minimum Due :: Enterred amount :: " + customamount
						+ " :: Min Due :: " + minimumDue21st);
			} else {
				System.out.println("Enterred amount is ::" + customamount + ":: minimum due " + minimumDue21st
						+ "Full balance ::" + fullBalance21st);
			}
		} else {
			System.out.println("custom amount or full balance is null or fullbalance is not greater than zero");
		}

		if (validCustomAmount) {
			System.out.println("Enterred Amount is valid :: " + customamount + " :: Min Due :: " + minimumDue21st
					+ " :: full Balance :: " + fullBalance21st
					+ " :: Enterred amount is within min and full and so validation is successful");

		} else {
			System.out.println(
					"Enterred amount is not validated in the amount validations and so checking the min due :: min due amount :: "
							+ minimumDue21st + " :: custom amount :: " + customamount);
			if (null != minimumDue21st && Float.parseFloat(minimumDue21st) > 0) {
				System.out.println("Minimum due is present and so setting the exitstate as NO_WITH_MIN_DUE");
			} else {
				System.out.println("Minimum due is not present and so setting the exitstate as NO_WITHOUT_MIN_DUE");
			}
		}

	}

}
