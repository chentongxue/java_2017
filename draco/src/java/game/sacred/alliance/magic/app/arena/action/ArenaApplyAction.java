package sacred.alliance.magic.app.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.arena.ArenaResult;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3850_ArenaApplyReqMessage;
import com.game.draco.message.response.C3850_ArenaApplyRespMessage;

public class ArenaApplyAction extends BaseAction<C3850_ArenaApplyReqMessage>{

	@Override
	public Message execute(ActionContext context, C3850_ArenaApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		ArenaResult result = GameContext.getArenaApp().apply(role,reqMsg.getActiveId());
		C3850_ArenaApplyRespMessage respMsg = new C3850_ArenaApplyRespMessage();
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		respMsg.setApplyState(result.getCurrentApplyState().getType());
		return respMsg ;
	}

}
