package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0573_GoodsWingGridReqMessage;
import com.game.draco.message.response.C0573_GoodsWingGridRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsWingGridInfoAction extends BaseAction<C0573_GoodsWingGridReqMessage>{
	@Override
	public Message execute(ActionContext context, C0573_GoodsWingGridReqMessage reqMsg) {
		C0573_GoodsWingGridRespMessage respMsg = new C0573_GoodsWingGridRespMessage();
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role) {
				return null;
			}
			respMsg = GameContext.getWingApp().getGoodsWingGridInfo(role,reqMsg.getWingGridId());
		}catch(Exception e){
			logger.error("GoodsWingGridInfoAction error:", e);
		}
		return respMsg ;
	}
}
