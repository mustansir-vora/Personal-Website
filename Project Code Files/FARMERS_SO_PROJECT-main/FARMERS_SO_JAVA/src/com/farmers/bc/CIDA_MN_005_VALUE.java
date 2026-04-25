 package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CIDA_MN_005_VALUE extends DecisionElementBase {
   public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
      String strExitState = "ER";
      CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

      try {
         String strReturnValue = data.getElementData("CIDA_MN_005_Call", "Return_Value");
         data.addToLog(currElementName, " CIDA_MN_005_Call Before : Return_Value :: " + strReturnValue);
         data.setSessionData("CIDA_MN_005_VALUE", strReturnValue);
         if (!strReturnValue.isEmpty()) {
            strReturnValue = Character.toUpperCase(strReturnValue.charAt(0)) + strReturnValue.substring(1);
         }

         String regex = "(.)*(\\d)(.)*";
         Pattern pattern = Pattern.compile(regex);
         Matcher matcher = pattern.matcher(strReturnValue);
         if (matcher.matches()) {
            if (strReturnValue.contains("-")) {
               strReturnValue = strReturnValue.split("\\-")[0];
            }

            if (strReturnValue.length() == 10 && strReturnValue.startsWith("3")) {
               strReturnValue = strReturnValue.replaceFirst("3", "F");
            }

            data.setSessionData("S_ACCNUM", strReturnValue);
            data.setSessionData("S_IS_ACCNO_PROVIDED", "TRUE");
            data.addToLog(currElementName, " CIDA_MN_005_Call After : Return_Value :: " + strReturnValue);
            strExitState = "SU";
         } else if (strReturnValue.equalsIgnoreCase("NOINPUT")) {
            strExitState = "NOINPUT";
         } else if (strReturnValue.equalsIgnoreCase("NOMATCH")) {
            strExitState = "NOMATCH";
         } else if (strReturnValue.equalsIgnoreCase("Representative")) {
            strExitState = "Representative";
            data.setSessionData("LAST_MENU_NAME_REP", "CIDA_MN_005");
         } else if (strReturnValue.equalsIgnoreCase("Dontknow")) {
            strExitState = "Dontknow";
         } else {
            strExitState = "ER";
         }
      } catch (Exception e) {
         data.addToLog(currElementName, "Exception in CIDA_MN_005_VALUE :: " + e);
         caa.printStackTrace(e);
      }

      data.addToLog(currElementName, "CIDA_MN_005_VALUE :: " + strExitState);
      data.setSessionData("S_INTENT", strExitState);
      return strExitState;
   }
}
    