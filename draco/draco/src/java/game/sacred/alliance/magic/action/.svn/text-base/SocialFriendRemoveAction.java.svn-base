package sacred.alliance.magic.action;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1204_SocialFriendRemoveReqMessage;
import com.game.draco.message.response.C1204_SocialFriendRemoveRespMessage;

public class SocialFriendRemoveAction extends BaseAction<C1204_SocialFriendRemoveReqMessage> {

	@Override
	public Message execute(ActionContext context, C1204_SocialFriendRemoveReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int targRoleId = reqMsg.getRoleId();
		String targetRoleId = String.valueOf(targRoleId);
		Result result = GameContext.getSocialApp().friendRemove(role, targetRoleId);
		C1204_SocialFriendRemoveRespMessage resp = new C1204_SocialFriendRemoveRespMessage();
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		resp.setRoleId(targRoleId);
		return resp;
	}
	
}
