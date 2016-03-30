package sacred.alliance.magic.action;

import com.game.draco.message.request.C2111_QuickBuyAutoConfirmReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class QuickBuyAutoConfirmAction extends BaseAction<C2111_QuickBuyAutoConfirmReqMessage> {

	@Override
	public Message execute(ActionContext context, C2111_QuickBuyAutoConfirmReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		//在角色身上标记为自动
		role.setQuickBuyAutoConfirm(true);
		return null;
	}
	
}
