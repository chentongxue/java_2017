package com.game.draco.app.camp.war.action;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.item.CampWarHeroInfoItem;
import com.game.draco.message.request.C0353_CampWarHeroListReqMessage;
import com.game.draco.message.response.C0353_CampWarHeroListRespMessage;
import com.google.common.collect.Lists;

public class CampWarHeroListAction extends BaseAction<C0353_CampWarHeroListReqMessage>{

	private final int FULL_HP_RATE = 10000 ;
	
	@Override
	public Message execute(ActionContext context,
			C0353_CampWarHeroListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C0353_CampWarHeroListRespMessage respMsg = new C0353_CampWarHeroListRespMessage();
		//角色的所有英雄
		Collection<RoleHero> allHeros = GameContext.getUserHeroApp().getAllRoleHero(role.getRoleId());
		if(Util.isEmpty(allHeros)){
			return respMsg ;
		}
		List<CampWarHeroInfoItem> heroList = Lists.newArrayList();
		for(RoleHero rh : allHeros){
			CampWarHeroInfoItem item = this.getHeroInfoItem(rh);
			if(null == item){
				continue ;
			}
			heroList.add(item);
		}
		respMsg.setHeroList(heroList);
		return respMsg;
	}

	
	private CampWarHeroInfoItem getHeroInfoItem(RoleHero rh){
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,
				rh.getHeroId());
		if(null == hero){
			return null ;
		}
		CampWarHeroInfoItem item = new CampWarHeroInfoItem();
		item.setHeroId(rh.getHeroId());
		
		/*item.setName(hero.getName());
		item.setLevel((byte)rh.getLevel());
		item.setQuality(rh.getQuality());
		item.setStar(rh.getStar());
		item.setOnBattle(rh.getOnBattle());
		//战斗力
		item.setBattleScore(GameContext.getHeroApp().getBattleScore(rh));
		item.setImageId(hero.getImageId());*/
		//获得hp百分比
		Float hpRate = GameContext.getCampWarApp().getHeroHpRate(rh.getRoleId(), rh.getHeroId());
		if(null == hpRate){
			item.setHpRate((short)FULL_HP_RATE);
		}else{
			item.setHpRate((short)(hpRate * FULL_HP_RATE)); 
		}
		return item ;
	}
}
