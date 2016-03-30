package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1211_SocialGiveFlowerReqMessage;
import com.game.draco.message.response.C1211_SocialGiveFlowerRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class SocialGiveFlowerAction extends BaseAction<C1211_SocialGiveFlowerReqMessage> {

	@Override
	public Message execute(ActionContext context, C1211_SocialGiveFlowerReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getSocialApp().giveFlower(role, reqMsg.getRoleId(), reqMsg.getFlowerId());
		C1211_SocialGiveFlowerRespMessage resp = new C1211_SocialGiveFlowerRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}
	
}
