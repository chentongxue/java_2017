package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0516_EquipSuitInfoReqMessage;
import com.game.draco.message.response.C0516_EquipSuitInfoRespMessage;

import sacred.alliance.magic.app.goods.suit.Suit;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class EquipSuitInfoAction extends BaseAction<C0516_EquipSuitInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, C0516_EquipSuitInfoReqMessage reqMsg) {
		short suitId = reqMsg.getSuitId();
		Suit suit = GameContext.getSuitApp().getSuit(suitId);
		if(null == suit){
			C0516_EquipSuitInfoRespMessage respMsg = new C0516_EquipSuitInfoRespMessage();
			respMsg.setSuitId(suitId);
			respMsg.setStatus((byte)2); //服务器不存在此套装信息,不需要再次请求
			return respMsg ;
		}
		return suit.getDetailMessage();
	}

}
