package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0571_GoodsWingReqMessage;
import com.game.draco.message.response.C0571_GoodsWingRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsWingInfoAction extends BaseAction<C0571_GoodsWingReqMessage>{
	@Override
	public Message execute(ActionContext context, C0571_GoodsWingReqMessage reqMsg) {
		C0571_GoodsWingRespMessage respMsg = new C0571_GoodsWingRespMessage();
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role) {
				return null;
			}
			respMsg = GameContext.getWingApp().getGoodsWingInfo(role);
		}catch(Exception e){
			logger.error("GoodsWingInfoAction error:", e);
		}
		return respMsg ;
	}
}
