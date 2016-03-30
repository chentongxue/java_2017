package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0855_AuctionBuyReqMessage;
import com.game.draco.message.response.C0855_AuctionBuyRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class AuctionBuyAction extends BaseAction<C0855_AuctionBuyReqMessage>{

	@Override
	public Message execute(ActionContext context, C0855_AuctionBuyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		C0855_AuctionBuyRespMessage resp = new C0855_AuctionBuyRespMessage();
		Result status = GameContext.getAuctionApp().buy(role, reqMsg.getId());
		if(status.isIgnore()){
			return null;
		}
		resp.setStatus(status.getResult());
		resp.setInfo(status.getInfo());
		resp.setId(reqMsg.getId());
		return resp;
	}
	
}
