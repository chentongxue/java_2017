package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0572_GoodsWingUpgradeReqMessage;
import com.game.draco.message.response.C0572_GoodsWingUpgradeRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsWingUpgradeAction extends BaseAction<C0572_GoodsWingUpgradeReqMessage>{
	@Override
	public Message execute(ActionContext context, C0572_GoodsWingUpgradeReqMessage reqMsg) {
		C0572_GoodsWingUpgradeRespMessage respMsg = new C0572_GoodsWingUpgradeRespMessage();
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role) {
				return null;
			}
			return GameContext.getWingApp().wingUpgrade(role);
		}catch(Exception e){
			logger.error("GoodsWingInfoAction error:", e);
		}
		return respMsg ;
	}
}
