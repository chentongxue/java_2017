package sacred.alliance.magic.app.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C3873_Arena3V3DarkDetailReqMessage;

public class Arena3V3DarkDetailAction extends BaseAction<C3873_Arena3V3DarkDetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C3873_Arena3V3DarkDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		//GameContext.getDarkDoorApp().applyState(role.getRoleId() , (byte)ArenaType._3V3_DARK_DOOR.getType());
		return null;
	}
}
