package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3300_LoginCompleteReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class LoginCompleteAction extends BaseAction<C3300_LoginCompleteReqMessage> {

	@Override
	public Message execute(ActionContext context, C3300_LoginCompleteReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		role.setLoginCompleted(true);
		//判断是否push选择阵营的面板
		GameContext.getCampBalanceApp().pushToSelectCampMessage(role);
		//角色等级小于2的不弹出
		if(role.getLevel() < 2){
			return null ;
		}
		//返回限时活动列表协议
		return GameContext.getActiveDiscountApp().createDiscountListMsg(role, false);
	}

}
