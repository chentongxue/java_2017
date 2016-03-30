package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1203_SocialFriendReplyReqMessage;
import com.game.draco.message.response.C1201_SocialFriendRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class SocialFriendReplyAction extends BaseAction<C1203_SocialFriendReplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1203_SocialFriendReplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		String targetRoleId = String.valueOf(reqMsg.getRoleId());
		Result result = GameContext.getSocialApp().friendReply(role, reqMsg.getType(), targetRoleId);
		C1201_SocialFriendRespMessage resp = new C1201_SocialFriendRespMessage();
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}
	
}
