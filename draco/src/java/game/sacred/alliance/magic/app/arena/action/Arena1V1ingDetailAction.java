package sacred.alliance.magic.app.arena.action;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C3859_Arean1V1ingDetailReqMessage;

public class Arena1V1ingDetailAction extends Arena1V1AbstractAction<C3859_Arean1V1ingDetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C3859_Arean1V1ingDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		return this.buildIngDetailRespMessage(role,false);
	}
	
	
}
