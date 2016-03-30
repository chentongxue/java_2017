package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0859_AuctionFeeInfoReqMessage;
import com.game.draco.message.response.C0859_AuctionFeeInfoRespMessage;

import sacred.alliance.magic.app.auction.FeeInfoConfig;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class AuctionFeeInfoAction extends BaseAction<C0859_AuctionFeeInfoReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C0859_AuctionFeeInfoReqMessage reqMsg) {
		C0859_AuctionFeeInfoRespMessage respMsg = new C0859_AuctionFeeInfoRespMessage();
		FeeInfoConfig conf = GameContext.getAuctionApp().getFeeInfoConfig() ;
		respMsg.setFee8(conf.getFee8());
		respMsg.setFee24(conf.getFee24());
		respMsg.setFee48(conf.getFee48());
		respMsg.setMinGameMoney(conf.getMinGameMoney());
		respMsg.setMinGlodMoney(conf.getMinGlodMoney());
		return respMsg ;
	}

}
