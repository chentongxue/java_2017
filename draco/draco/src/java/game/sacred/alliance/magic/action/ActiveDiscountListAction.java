package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2315_ActiveDiscountListReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveDiscountListAction extends BaseAction<C2315_ActiveDiscountListReqMessage>{

	@Override
	public Message execute(ActionContext context, C2315_ActiveDiscountListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		return GameContext.getActiveDiscountApp().createDiscountListMsg(role, true);
	}

}
