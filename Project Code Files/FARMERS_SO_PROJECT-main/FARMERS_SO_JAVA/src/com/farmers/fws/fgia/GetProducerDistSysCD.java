
package com.farmers.fws.fgia;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;

public class GetProducerDistSysCD extends DecisionElementBase {
   public String doDecision(String arg0, DecisionElementData data) throws Exception {
      String strExitState = "OTHERS";
      CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

      try {
         if (data.getSessionData("S_FWS_BILLING_PROD_DIST_CD") != null) {
            String producerDistSysCD = (String)data.getSessionData("S_FWS_BILLING_PROD_DIST_CD");
            if (producerDistSysCD.equalsIgnoreCase("IA")) {
               strExitState = "IA";
            }
         }
      } catch (Exception var6) {
         data.addToLog(data.getCurrentElement(), "Exception while  :: " + var6);
         caa.printStackTrace(var6);
      }

      return strExitState;
   }
}