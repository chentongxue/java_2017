package com.game.draco.app.shop.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.shop.domain.ShopGoods;
import com.game.draco.app.shop.type.ShopMoneyType;
import com.game.draco.app.shop.type.ShopShowType;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.ShopGoodsItem;
import com.game.draco.message.request.C2101_ShopGoodsListReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C2101_ShopGoodsListRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopGoodsListAction extends BaseAction<C2101_ShopGoodsListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2101_ShopGoodsListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int openLevel = GameContext.getParasConfig().getShopOpenRoleLevel();
		if(openLevel > 0 && role.getLevel() < openLevel){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Shop_Level_Not_Open.getTips().replace(Wildcard.Number, String.valueOf(openLevel)));
		}
		ShopShowType showType = ShopShowType.get(reqMsg.getType());
		C2101_ShopGoodsListRespMessage resp = new C2101_ShopGoodsListRespMessage();
		if(null == showType){
			return resp;
		}
		ShopMoneyType moneyType = ShopMoneyType.GoldMoney;
		if(!showType.isGoldMoney()){
			moneyType = ShopMoneyType.BindMoney;
		}
		List<ShopGoods> goodsList = GameContext.getShopApp().getShopGoodsList(showType);
		List<ShopGoodsItem> shopGoodsList = new ArrayList<ShopGoodsItem>();
		if(!Util.isEmpty(goodsList)){
			for(ShopGoods shopGoods : goodsList){
				if(null == shopGoods){
					continue;
				}
				if(!shopGoods.canSell()){
					continue;
				}
				ShopGoodsItem item = new ShopGoodsItem();
				int goodsId = shopGoods.getGoodsId();
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == goodsBase){
					continue ;
				}
				byte bindType = goodsBase.getBindType();
				if(showType.isGoldMoney()){
					//金币购买绑定类型根据配置
					if(shopGoods.getGoldBindType() != BindingType.template.getType()){
						bindType = shopGoods.getGoldBindType();
					}
					item.setPrice(shopGoods.getGoldPrice());
					item.setDisPrice(shopGoods.getDisGoldPrice());
					
				}else{
					//绑金购买一定绑定
					bindType = BindingType.already_binding.getType();
					item.setPrice(shopGoods.getBindPrice());
					item.setDisPrice(shopGoods.getDisBindPrice());
				}
				item.setStatus(shopGoods.getStatus(moneyType).getType());
				
				GoodsLiteNamedItem liteItem = goodsBase.getGoodsLiteNamedItem();
				//绑定类型
				liteItem.setBindType(bindType);
				//默认数目
				int stackNum = Math.min(goodsBase.getOverlapCount(), 
						shopGoods.getDefaultBuyNum());
				liteItem.setNum((short)stackNum);
				item.setGoodsItem(liteItem);
				//试穿资源ID
				item.setResId((short) goodsBase.getResId());
				shopGoodsList.add(item);
			}
		}
		resp.setMoneyType(moneyType.getType());
		resp.setShopGoodsList(shopGoodsList);
		return resp;
	}

}
