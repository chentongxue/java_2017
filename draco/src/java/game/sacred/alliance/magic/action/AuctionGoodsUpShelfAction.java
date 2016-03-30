package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0853_AuctionGoodsUpShelfReqMessage;
import com.game.draco.message.response.C0853_AuctionGoodsUpShelfRespMessage;

import sacred.alliance.magic.base.Money;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class AuctionGoodsUpShelfAction extends BaseAction<C0853_AuctionGoodsUpShelfReqMessage>{

	@Override
	public Message execute(ActionContext context, C0853_AuctionGoodsUpShelfReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		C0853_AuctionGoodsUpShelfRespMessage resp = new C0853_AuctionGoodsUpShelfRespMessage();
		Result status = GameContext.getAuctionApp().putGoods(role, 
				reqMsg.getId(),
				new Money(reqMsg.getPriceType(),reqMsg.getPrice()),
				reqMsg.getEffectTime());
		if(status.isIgnore()){
			return null;
		}
		resp.setStatus(status.getResult());
		resp.setInfo(status.getInfo());
		return resp ;
	}
	
}
