package com.game.draco.app.unionbattle.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.unionbattle.config.UnionIntegral;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C2540_UnionIntegralInfoReqMessage;
import com.game.draco.message.response.C2540_UnionIntegralInfoRespMessage;

/**
 * 查看公会积分战信息
 */
public class UnionIntegralInfoAction extends BaseAction<C2540_UnionIntegralInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2540_UnionIntegralInfoReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		
		C2540_UnionIntegralInfoRespMessage respMsg = new C2540_UnionIntegralInfoRespMessage();
		
		UnionIntegral integral = GameContext.getUnionIntegralBattleDataApp().getIntegral();
			
		respMsg.setInfo(integral.getDetails());
		
		GoodsLiteNamedItem goodsLiteItem = null;
		List<GoodsLiteItem> goodsLiteItemList = new ArrayList<GoodsLiteItem>();
		
		if(Util.isEmpty(integral.getRewGoodsId())){
			return respMsg;
		}
		
		String [] arrGoodsId = integral.getRewGoodsId().split(",");
		
		int i=0;
		for(;i<arrGoodsId.length;i++){
			goodsLiteItem = new GoodsLiteNamedItem();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(Integer.parseInt(arrGoodsId[i]));
			goodsLiteItem.setGoodsId(goodsBase.getId());
			goodsLiteItem.setGoodsName(goodsBase.getName());
			goodsLiteItem.setBindType(goodsBase.getBindType());
			goodsLiteItem.setNum((short)1);
			goodsLiteItem.setQualityType(goodsBase.getQualityType());
			goodsLiteItem.setGoodsLevel((byte)goodsBase.getLevel());
			goodsLiteItemList.add(goodsLiteItem);
		}
		
		respMsg.setGoodsItemList(goodsLiteItemList);
		return respMsg;
	}

}
