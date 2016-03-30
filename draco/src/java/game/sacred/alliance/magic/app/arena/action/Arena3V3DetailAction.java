package sacred.alliance.magic.app.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3863_Arena3V3DetailReqMessage;

public class Arena3V3DetailAction extends BaseAction<C3863_Arena3V3DetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C3863_Arena3V3DetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return  null ;
		}
		return GameContext.getArena3V3App().getArena3V3DetailRespMessage(role);
	}
}
