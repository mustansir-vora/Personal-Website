package com.farmers.global;
import com.audium.core.vfc.VException;
import com.audium.core.vfc.VPreference;
import com.audium.core.vfc.util.IfCondition;
import com.audium.core.vfc.util.VAction;
import com.audium.core.vfc.util.VEvent;
import com.audium.core.vfc.util.VIfGroup;
import com.audium.server.proxy.HoteventInterface;

public class VXMLError_Event implements HoteventInterface
{
	public void addEventBody(VPreference arg0, VEvent arg1) throws VException {
			VIfGroup vigroup = VIfGroup.getNew(arg0);
			IfCondition ifcond = new IfCondition("cisco_vxml_error_count",IfCondition.VALUE,IfCondition.GREATER_EQUAL,3);
			VAction vaction = VAction.getNew(arg0);
			vaction.add(VAction.VARIABLE,"caller_input","T",VAction.WITH_QUOTES);
			vaction.add(VAction.RETURN,"caller_input");
			vigroup.add(ifcond,null,vaction);
			arg1.addCount(1);
			arg1.addItem(1,vigroup);
			VAction vaction1 = VAction.getNew(arg0);
			vaction1.add(VAction.ASSIGN,"cisco_vxml_error_count","cisco_vxml_error_count + 1",VAction.WITHOUT_QUOTES);
			arg1.addItem(1,vaction1);
	}

}