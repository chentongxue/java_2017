package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C1180_SummonDetailReqMessage;
import com.game.draco.message.response.C1180_SummonDetailRespMessage;

import sacred.alliance.magic.app.summon.Summon;
import sacred.alliance.magic.app.summon.SummonHelper;
import sacred.alliance.magic.app.summon.vo.SummonResult;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class SummonDetailAction extends BaseAction<C1180_SummonDetailReqMessage> {

	@Override
	public Message execute(ActionContext context, C1180_SummonDetailReqMessage req) {
		
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		Map<String,String> paramMap = Util.urlParamParser(req.getParam());
		int summonId = Integer.parseInt(paramMap.get(SummonHelper.SummonId_Key));
		Summon summon = GameContext.getSummonApp().getSummonById(summonId);
		if(null == summon) {
			return null;
		}
		C1180_SummonDetailRespMessage resp = new C1180_SummonDetailRespMessage();
		resp.setSummonId((short)summonId);
		resp.setDesc(summon.getDesc());
		SummonResult status = GameContext.getSummonApp().canSummon(role, summon, false);
		if(status.getStatus().isSuccess()) {
			resp.setCanSummon((byte)1);
		}
		resp.setFrequencyType(summon.getFrequencyType());
		resp.setCurNum((byte)summon.getSummonCount(role));
		resp.setMaxNum(summon.getFrequencyValue());
		resp.setConsumeNumberList(summon.getConsumeNumList());
		//消耗
		List<GoodsLiteNamedItem> consumeGoodsList = new ArrayList<GoodsLiteNamedItem>();
		Map<Integer, Integer> consumeGoods = summon.getConsumeGoods();
		for(Integer goodsId : consumeGoods.keySet()) {
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == gb) {
				continue;
			}
			GoodsLiteNamedItem showGoodsItem = gb.getGoodsLiteNamedItem() ;
			//设置数量
			showGoodsItem.setNum(consumeGoods.get(goodsId).shortValue());
			consumeGoodsList.add(showGoodsItem);
		}
		resp.setConsumeGoodsList(consumeGoodsList);
		return resp;
	}
}
