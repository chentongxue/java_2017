package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1901_RoleOnlineRewardTakeReqMessage;
import com.game.draco.message.response.C1901_RoleOnlineRewardTakeRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleOnlineRewardTakeAction extends BaseAction<C1901_RoleOnlineRewardTakeReqMessage> {

	@Override
	public Message execute(ActionContext context, C1901_RoleOnlineRewardTakeReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getOnlineRewardApp().takeReward(role);
		C1901_RoleOnlineRewardTakeRespMessage resp = new C1901_RoleOnlineRewardTakeRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		resp.setParameter(role.getOnlineRewardRemainTime());
		return resp;
	}

}
