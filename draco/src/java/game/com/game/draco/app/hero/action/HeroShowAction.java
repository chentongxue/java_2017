package com.game.draco.app.hero.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.HeroEquipBackpack;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.HeroEquip;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.item.HeroShowItem;
import com.game.draco.message.request.C1103_HeroShowReqMessage;
import com.game.draco.message.response.C1103_HeroShowRespMessage;
import com.google.common.collect.Lists;

/**
 * 查看别的玩家英雄 
 */
public class HeroShowAction extends BaseAction<C1103_HeroShowReqMessage> {

	@Override
	public Message execute(ActionContext context, C1103_HeroShowReqMessage reqMsg) {
		String roleId = String.valueOf(reqMsg.getRoleId()) ;
		C1103_HeroShowRespMessage respMsg = new C1103_HeroShowRespMessage() ;
		respMsg.setRoleId(reqMsg.getRoleId());
		List<RoleHero> heroList = GameContext.getHeroApp().getRoleSwitchableHeroList(roleId);
		if(Util.isEmpty(heroList)){
			return respMsg ;
		}
		boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(roleId) ;
		HeroEquip heroEquip = null ;
		if(!isOnline){
			heroEquip = GameContext.getHeroApp().getHeroEquipCache(roleId);
		}
		List<HeroShowItem> list = Lists.newArrayList() ;
		for(RoleHero hero : heroList){
			int heroId = hero.getHeroId() ;
			HeroShowItem item = new HeroShowItem();
			item.setHeroId(heroId);
			item.setStar(hero.getStar());
			item.setQuality(hero.getQuality());
			item.setLevel((byte)hero.getLevel());
			item.setBattleScore(GameContext.getHeroApp().getBattleScore(hero));
			GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
			if(null != goodsHero){
				item.setHeadId(goodsHero.getHeadId());
				item.setResId((short)goodsHero.getResId());
				//item.setWeaponResId(goodsHero.getWeaponResId());
				item.setName(goodsHero.getName());
				item.setSeriesId(goodsHero.getSeriesId());
				item.setGearId(goodsHero.getGearId());
			}
			List<RoleGoods> goodsList = null ;
			if(isOnline){
				HeroEquipBackpack pack = GameContext.getUserHeroApp().getEquipBackpack(roleId, heroId);
				goodsList = (null == pack)?null : pack.getAllGoods() ;
			}else if(null != heroEquip){
				goodsList = heroEquip.getEquipMap().get(heroId);
			}
			item.setEquipList(GoodsHelper.createGoodsOtherEquipmentItemList(goodsList));
			list.add(item);
		}
		respMsg.setHeroList(list);
		return respMsg;
	}

}
