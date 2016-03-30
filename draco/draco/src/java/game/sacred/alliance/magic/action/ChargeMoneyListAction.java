package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2801_ChargeMoneyListReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ChargeMoneyListAction extends BaseAction<C2801_ChargeMoneyListReqMessage>{

	@Override
	public Message execute(ActionContext context, C2801_ChargeMoneyListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getChargeApp().getChargeMoneyListRespMessage(role);
	}
	
}
