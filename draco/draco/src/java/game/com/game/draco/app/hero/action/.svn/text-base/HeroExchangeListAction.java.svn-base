package com.game.draco.app.hero.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.item.HeroExchangeGoodsItem;
import com.game.draco.message.request.C1261_HeroExchangeListReqMessage;
import com.game.draco.message.response.C1261_HeroExchangeListRespMessage;
import com.google.common.collect.Lists;

public class HeroExchangeListAction extends BaseAction<C1261_HeroExchangeListReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1261_HeroExchangeListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		List<Integer> heroIdList = GameContext.getHeroApp().getHeroExchangeList() ;
		C1261_HeroExchangeListRespMessage respMsg = new C1261_HeroExchangeListRespMessage();
		if(Util.isEmpty(heroIdList)){
			return respMsg ;
		}
		
		List<HeroExchangeGoodsItem> heroList = Lists.newArrayList() ;
		for(int goodsId : heroIdList){
			GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, goodsId);
			if(null == hero){
				continue ;
			}
			RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), goodsId);
			if(null != roleHero){
				//用户已经拥有的
				continue ;
			}
			HeroExchangeGoodsItem item = new HeroExchangeGoodsItem();
			item.setGoodsId(goodsId);
			item.setName(hero.getName());
			item.setQuality(hero.getQualityType());
			item.setImageId(hero.getImageId());
			item.setShadowId(hero.getShadowId());
			item.setNeedShadowNum((short)hero.getShadowNum());
			item.setStar(hero.getBornStar());
			heroList.add(item);
		}
		respMsg.setHeroList(heroList);
		return respMsg ;
	}

}
