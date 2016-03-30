package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1905_BenefitLoginCountRewardReqMessage;
import com.game.draco.message.response.C1905_BenefitLoginCountRewardRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class BenefitLoginCountRewardAction extends BaseAction<C1905_BenefitLoginCountRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C1905_BenefitLoginCountRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getBenefitApp().takeLoginCountReward(role);
		C1905_BenefitLoginCountRewardRespMessage resp = new C1905_BenefitLoginCountRewardRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
