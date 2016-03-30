package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0852_AuctionDownShelfReqMessage;
import com.game.draco.message.response.C0852_AuctionDownShelfRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class AuctionDownShelfAction extends BaseAction<C0852_AuctionDownShelfReqMessage>{

	@Override
	public Message execute(ActionContext context, C0852_AuctionDownShelfReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		C0852_AuctionDownShelfRespMessage resp = new C0852_AuctionDownShelfRespMessage();
		Result status = GameContext.getAuctionApp().delisting(role, reqMsg.getId());
		if(status.isIgnore()){
			return null;
		}
		resp.setStatus(status.getResult());
		resp.setInfo(status.getInfo());
		resp.setId(reqMsg.getId());
		return resp;
	}
	
}
