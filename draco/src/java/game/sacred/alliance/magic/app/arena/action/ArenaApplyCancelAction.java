package sacred.alliance.magic.app.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.arena.ArenaResult;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3851_ArenaApplyCancelReqMessage;
import com.game.draco.message.response.C3851_ArenaApplyCancelRespMessage;

public class ArenaApplyCancelAction extends BaseAction<C3851_ArenaApplyCancelReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C3851_ArenaApplyCancelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		ArenaResult result = GameContext.getArenaApp().applyCancel(role,reqMsg.getActiveId());
		C3851_ArenaApplyCancelRespMessage respMsg = new C3851_ArenaApplyCancelRespMessage();
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		respMsg.setApplyState(result.getCurrentApplyState().getType());
		return respMsg ;
	}

}
