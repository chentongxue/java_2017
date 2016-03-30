package com.game.draco.app.shop.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2102_ShopBuyGoodsReqMessage;
import com.game.draco.message.response.C2102_ShopBuyGoodsRespMessage;

public class ShopBuyGoodsAction extends BaseAction<C2102_ShopBuyGoodsReqMessage> {

	@Override
	public Message execute(ActionContext context, C2102_ShopBuyGoodsReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		/*int openLevel = GameContext.getParasConfig().getShopOpenRoleLevel();
		if(openLevel > 0 && role.getLevel() < openLevel){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Shop_Level_Not_Open.getTips().replace(Wildcard.Number, String.valueOf(openLevel)));
		}*/
		if(role == null){
			return null;
		}
		if(Util.isEmpty(req.getParam()) || req.getParam().length() < 3){
			return null;
		}
		String [] param = req.getParam().split(",");
		byte confirm = 0;
		if(param.length>3){
			confirm = 1;
		}
		Result result = null;
		try {
			byte moneyType = Byte.parseByte(param[0]);
			int goodsId = Integer.parseInt(param[1]);
			short number = Short.parseShort(param[2]);
			
			result = GameContext.getShopApp().shopping(role, moneyType, goodsId, number , false, confirm);

		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
		if(result.isIgnore()){
			return null;
		}
		C2102_ShopBuyGoodsRespMessage resp = new C2102_ShopBuyGoodsRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
