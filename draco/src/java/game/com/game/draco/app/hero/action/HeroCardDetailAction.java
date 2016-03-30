package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsHero;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseHeroItem;
import com.game.draco.message.request.C1263_HeroCardDetailReqMessage;
import com.game.draco.message.response.C1263_HeroCardDetailRespMessage;

public class HeroCardDetailAction extends BaseAction<C1263_HeroCardDetailReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1263_HeroCardDetailReqMessage reqMsg) {
		int goodsId = reqMsg.getHeroId() ;
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, goodsId);
		C1263_HeroCardDetailRespMessage respMsg = new C1263_HeroCardDetailRespMessage();
		respMsg.setSource(reqMsg.getSource());
		respMsg.setBaseItem((GoodsBaseHeroItem)hero.getGoodsBaseInfo(null));
		return respMsg;
	}

}
