package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1904_BenefitPanelPopupReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class BenefitPanelPopupAction extends BaseAction<C1904_BenefitPanelPopupReqMessage> {

	@Override
	public Message execute(ActionContext context, C1904_BenefitPanelPopupReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		return GameContext.getBenefitApp().popupBenefitPanel(role);
	}
	
}
