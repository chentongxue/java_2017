package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1207_SocialBlackApplyReqMessage;
import com.game.draco.message.response.C1207_SocialBlackApplyRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class SocialBlackApplyAction extends BaseAction<C1207_SocialBlackApplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1207_SocialBlackApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int targetRoleId = reqMsg.getRoleId();
		String targetRoleName = reqMsg.getRoleName();
		C1207_SocialBlackApplyRespMessage resp = new C1207_SocialBlackApplyRespMessage();
		if(-1 == targetRoleId && targetRoleName.equals(role.getRoleName())){
			resp.setInfo(Status.Social_Black_Not_Self.getTips());
			return resp;
		}else if(targetRoleId == role.getIntRoleId()){
			resp.setInfo(Status.Social_Black_Not_Self.getTips());
			return resp;
		}
		Result result = GameContext.getSocialApp().blackApply(role, 
				String.valueOf(targetRoleId), targetRoleName);
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}
	
}
