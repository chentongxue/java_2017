package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1205_SocialFriendListReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class SocialFriendListAction extends BaseAction<C1205_SocialFriendListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1205_SocialFriendListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		return GameContext.getSocialApp().getFriendListMessage(role);
	}
	
}
