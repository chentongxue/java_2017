package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2300_ActivePanelReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ActivePanelAction extends BaseAction<C2300_ActivePanelReqMessage> {

	@Override
	public Message execute(ActionContext context, C2300_ActivePanelReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		return GameContext.getActiveApp().createActivePanelListMsg(role);
	}
	
}
