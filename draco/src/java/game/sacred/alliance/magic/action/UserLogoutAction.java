package sacred.alliance.magic.action;

import com.game.draco.message.request.C0107_UserLogoutReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class UserLogoutAction extends BaseAction<C0107_UserLogoutReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C0107_UserLogoutReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		role.getBehavior().closeNetLink();
		return null ;
	}
	
}
