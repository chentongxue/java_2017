package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.MixFormula;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsGemMixInfoItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C0557_GoodsGemMixReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0557_GoodsGemMixRespMessage;

public class GoodsGemMixAction  extends BaseAction<C0557_GoodsGemMixReqMessage>{

	@Override
	public Message execute(ActionContext context, C0557_GoodsGemMixReqMessage reqMsg) {
		try {
			List<GoodsGemMixInfoItem> items = new ArrayList<GoodsGemMixInfoItem>();
			Map<Integer, MixFormula> all = GameContext.getGoodsApp().getAllMixFormula();
			for (MixFormula gf : all.values()) {
				if(null == gf){
					continue;
				}
				GoodsBase targetGoods = gf.getTargetGoods();
				if(null == targetGoods){
					continue;
				}
				GoodsGemMixInfoItem item = new GoodsGemMixInfoItem();
				item.setFee(gf.getFee());
				item.setSrcId(gf.getSrcId());
				item.setSrcNum(gf.getSrcNum());
				//目标信息
				GoodsLiteNamedItem target = targetGoods.getGoodsLiteNamedItem();
				item.setTargetItem(target);
				items.add(item);
			}
			C0557_GoodsGemMixRespMessage respMsg = new C0557_GoodsGemMixRespMessage();
			respMsg.setItems(items);
			return respMsg;
		}catch(Exception ex){
			logger.error("",ex);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Goods_System_Busy.getTips());
		}
	}

}
