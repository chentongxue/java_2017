package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1903_BenefitPanelReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class BenefitPanelAction extends BaseAction<C1903_BenefitPanelReqMessage> {

	@Override
	public Message execute(ActionContext context, C1903_BenefitPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		return GameContext.getBenefitApp().getBenefitPanelRespMessage(role);
	}
	
}
