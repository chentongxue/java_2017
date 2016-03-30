package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0574_GoodsWingGridGrowReqMessage;
import com.game.draco.message.response.C0574_GoodsWingGridGrowRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsWingGridUpgradeAction extends BaseAction<C0574_GoodsWingGridGrowReqMessage>{
	@Override
	public Message execute(ActionContext context, C0574_GoodsWingGridGrowReqMessage reqMsg) {
		C0574_GoodsWingGridGrowRespMessage respMsg = new C0574_GoodsWingGridGrowRespMessage();
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role) {
				return null;
			}
			Result result = GameContext.getWingApp().growWingGrid(role, reqMsg.getWingGridId());
			respMsg.setInfo(result.getInfo());
			respMsg.setType(result.getResult());
			respMsg.setWingGridId(reqMsg.getWingGridId());
		}catch(Exception e){
			logger.error("GoodsWingInfoAction error:", e);
		}
		return respMsg ;
	}
}
