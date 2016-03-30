package com.game.draco.app.hero.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.item.HeroInfoItem;
import com.game.draco.message.request.C1250_HeroListReqMessage;
import com.game.draco.message.response.C1250_HeroListRespMessage;

public class HeroListAction extends BaseAction<C1250_HeroListReqMessage>{
	
	private Comparator<HeroInfoItem> comparator = new Comparator<HeroInfoItem>(){
		@Override
		public int compare(HeroInfoItem r1, HeroInfoItem r2) {
			//排序
			//onBattle > Quality > level > templateId
			if(r1.getOnBattle() < r2.getOnBattle()){
				return 1 ;
			}
			if(r1.getOnBattle() > r2.getOnBattle()){
				return -1 ;
			}
			if(r1.getQuality() < r2.getQuality()){
				return 1 ;
			}
			if(r1.getQuality() > r2.getQuality()){
				return -1 ;
			}
			if(r1.getStar() < r2.getStar()){
				return 1 ;
			}
			if(r1.getStar() > r2.getStar()){
				return -1 ;
			}
			if(r1.getLevel() < r2.getLevel()){
				return 1 ;
			}
			if(r1.getLevel() > r2.getLevel()){
				return -1 ;
			}
			if(r1.getSwallowExp() < r2.getSwallowExp()){
				return 1 ;
			}
			if(r1.getSwallowExp() > r2.getSwallowExp()){
				return -1 ;
			}
			return 0;
		}
	} ; 
	
	@Override
	public Message execute(ActionContext context, C1250_HeroListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		//触发发送英雄音乐
		GameContext.getHeroApp().pushHeroMusicList(role);
		//触发发送装备开启条件
		GameContext.getHeroApp().pushHeroEquipOpenCond(role);
		
		//获得角色的所有英雄
		Collection<RoleHero> allHeros = GameContext.getUserHeroApp().getAllRoleHero(role.getRoleId());
		C1250_HeroListRespMessage respMsg = new C1250_HeroListRespMessage();
		List<HeroInfoItem> items = new ArrayList<HeroInfoItem>();
		for(RoleHero rh : allHeros){
			HeroInfoItem item = GameContext.getHeroApp().getHeroInfoItem(rh);
			if(null == item){
				continue ;
			}
			items.add(item);
		}
		if(items.size() >1){
			Collections.sort(items, comparator);
		}
		respMsg.setHeroList(items);
		//设置最大等级
		respMsg.setHeroMaxLevel((byte)GameContext.getAreaServerNotifyApp().getMaxLevel());
		respMsg.setSwitchOpenLevel(GameContext.getHeroApp().getSwitchOpenLevel());
		respMsg.setHelpOpenLevel(GameContext.getHeroApp().getHelpOpenLevel());
		return respMsg ;
	}

}
