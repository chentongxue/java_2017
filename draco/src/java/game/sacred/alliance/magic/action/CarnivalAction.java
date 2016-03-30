package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1186_CarnivalReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class CarnivalAction extends BaseAction<C1186_CarnivalReqMessage>{

	@Override
	public Message execute(ActionContext context, C1186_CarnivalReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		return GameContext.getCarnivalApp().getActiveCarnival(reqMsg.getActiveId());
	}
}
