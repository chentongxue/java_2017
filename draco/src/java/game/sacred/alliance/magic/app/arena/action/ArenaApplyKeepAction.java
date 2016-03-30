package sacred.alliance.magic.app.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.arena.ArenaResult;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3853_ArenaApplyKeepReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class ArenaApplyKeepAction extends BaseAction<C3853_ArenaApplyKeepReqMessage>{

	@Override
	public Message execute(ActionContext context, C3853_ArenaApplyKeepReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		ArenaResult result = GameContext.getArenaApp().applyKeep(role.getRoleId(), reqMsg.getSelected());
		if(result.isSuccess()){
			return null;
		}
		return new C0002_ErrorRespMessage(reqMsg.getCommandId(),result.getInfo());
	}

}
