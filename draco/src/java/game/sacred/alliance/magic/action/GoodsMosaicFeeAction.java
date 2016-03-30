package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0544_GoodsMosaicFeeReqMessage;
import com.game.draco.message.response.C0544_GoodsMosaicFeeRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.MosaicConfig;

public class GoodsMosaicFeeAction extends BaseAction<C0544_GoodsMosaicFeeReqMessage> {

	@Override
	public Message execute(ActionContext context, C0544_GoodsMosaicFeeReqMessage reqMsg) {
		C0544_GoodsMosaicFeeRespMessage respMsg = new C0544_GoodsMosaicFeeRespMessage();
		MosaicConfig config = GameContext.getGoodsApp().getMosaicConfig();
		respMsg.setMosaicMoney(config.getMosaicMoney());
		respMsg.setExciseMoney(config.getExciseMoney());
		return respMsg;
	}
}
