package sacred.alliance.magic.action;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.request.C0511_GoodsInfoViewGoodsIdReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0504_GoodsInfoViewRespMessage;
public class GoodsInfoViewByGoodsIdAction extends BaseAction<C0511_GoodsInfoViewGoodsIdReqMessage> {
	@Override
	public Message execute(ActionContext context, C0511_GoodsInfoViewGoodsIdReqMessage reqMsg) {
		try{
			int goodsId = reqMsg.getGoodsId();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == goodsBase){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
			}
			GoodsBaseItem goodsParItem = goodsBase.getGoodsBaseInfo(null);
			if(null == goodsParItem ){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
			}
			C0504_GoodsInfoViewRespMessage baseMsg = new C0504_GoodsInfoViewRespMessage();
			baseMsg.setId(String.valueOf(goodsId));
			baseMsg.setBaseItem(goodsParItem);
			return baseMsg ;
		}catch(Exception e){
			logger.error("",e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.SYSTEM_ERROR));
			
		}
		
	}
}

