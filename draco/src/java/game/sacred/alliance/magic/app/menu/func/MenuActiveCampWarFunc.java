package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C0352_CampWarPanelReqMessage;

public class MenuActiveCampWarFunc extends MenuAbstractActiveFunc{

	public MenuActiveCampWarFunc() {
		super(MenuIdType.Active_CampWar);
	}

	
	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		C0352_CampWarPanelReqMessage reqMsg = new C0352_CampWarPanelReqMessage();
		reqMsg.setOpen((byte)1);
		return reqMsg ;
	}


}
