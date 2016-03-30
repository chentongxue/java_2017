package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1906_BenefitOfflineExpTakeReqMessage;
import com.game.draco.message.response.C1906_BenefitOfflineExpTakeRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class BenefitOfflineExpTakeAction extends BaseAction<C1906_BenefitOfflineExpTakeReqMessage>{
	@Override
	public Message execute(ActionContext context, C1906_BenefitOfflineExpTakeReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		C1906_BenefitOfflineExpTakeRespMessage resp = new C1906_BenefitOfflineExpTakeRespMessage();
		Result result = GameContext.getBenefitApp().takeOfflineExp(role, req.getIndex());
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
