package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0854_AuctionMoneyUpShelfReqMessage;
import com.game.draco.message.response.C0854_AuctionMoneyUpShelfRespMessage;

import sacred.alliance.magic.base.Money;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class AuctionMoneyUpShelfAction extends BaseAction<C0854_AuctionMoneyUpShelfReqMessage>{

	@Override
	public Message execute(ActionContext context, C0854_AuctionMoneyUpShelfReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C0854_AuctionMoneyUpShelfRespMessage respMsg = new C0854_AuctionMoneyUpShelfRespMessage();
		Result status = GameContext.getAuctionApp().putMoney(role, 
				new Money(reqMsg.getMoneyType(),reqMsg.getMoneyNum()),
				new Money(reqMsg.getPriceType(),reqMsg.getPrice()),
				reqMsg.getEffectTime());
		if(status.isIgnore()){
			return null;
		}
		respMsg.setStatus(status.getResult());
		respMsg.setInfo(status.getInfo());
		return respMsg;
		
	}
	
}
