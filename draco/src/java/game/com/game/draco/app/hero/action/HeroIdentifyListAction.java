package com.game.draco.app.hero.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.domain.RoleHeroStatus;
import com.game.draco.message.item.HeroIdentifyItem;
import com.game.draco.message.request.C1261_HeroIdentifyListReqMessage;
import com.game.draco.message.response.C1261_HeroIdentifyListRespMessage;
import com.google.common.collect.Lists;

public class HeroIdentifyListAction extends BaseAction<C1261_HeroIdentifyListReqMessage>{

	//排序
	private Comparator<HeroIdentifyItem> comparator = new Comparator<HeroIdentifyItem>() {
		@Override
		public int compare(HeroIdentifyItem r1, HeroIdentifyItem r2) {
            if (r1.getLevel() < r2.getLevel()) {
                return 1;
            }
            if (r1.getLevel() > r2.getLevel()) {
                return -1;
            }
			if (r1.getQuality() < r2.getQuality()) {
				return 1;
			}
			if (r1.getQuality() > r2.getQuality()) {
				return -1;
			}
			if (r1.getStar() < r2.getStar()) {
				return 1;
			}
			if (r1.getStar() > r2.getStar()) {
				return -1;
			}
			return 0;
		}
	};
	
				
	@Override
	public Message execute(ActionContext context,
			C1261_HeroIdentifyListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		List<Integer> heroIdList = GameContext.getHeroApp().getHeroIdentifyList() ;
		C1261_HeroIdentifyListRespMessage respMsg = new C1261_HeroIdentifyListRespMessage();
		if(Util.isEmpty(heroIdList)){
			return respMsg ;
		}
		
		
		List<HeroIdentifyItem> haveList = Lists.newArrayList() ;
		List<HeroIdentifyItem> unHaveList = Lists.newArrayList() ;
		
		RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
		for(int goodsId : heroIdList){
			if(status.getSwitchHeroSet().contains(goodsId)
					|| status.getHelpHeroSet().contains(goodsId)){
				//已经出战的或者助威的
				continue ;
			}
			RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), goodsId);
            HeroIdentifyItem item = this.buildHeroIdentifyItem(goodsId,roleHero);
            if(null == item){
                continue ;
            }
			if(null == roleHero){
				unHaveList.add(item);
				continue ;
			}
			//用户已经拥有的
			haveList.add(item);
		}
		
		//出战英雄
		RoleHero battleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		int onBattleHeroId = (null == battleHero)?0:battleHero.getHeroId();
		
		List<HeroIdentifyItem> switchList = this.buildList(status.getSwitchHeroSet(), onBattleHeroId) ;
		//助威英雄
		List<HeroIdentifyItem> helpList = this.buildList(status.getHelpHeroSet(), 0);
		
		//排序
		this.sort(haveList);

		switchList.addAll(helpList) ;
		switchList.addAll(haveList);
		switchList.addAll(unHaveList);
		
		respMsg.setHeroList(switchList);
		return respMsg ;
	}
	
	private List<HeroIdentifyItem> buildList(Set<Integer> heroSet,int givenHeroId){
		List<HeroIdentifyItem> ret = Lists.newArrayList() ;
		for(int goodsId : heroSet){
			HeroIdentifyItem item = this.buildHeroIdentifyItem(goodsId);
			if(null == item){
				continue ;
			}
			if(givenHeroId == goodsId && ret.size() > 0){
				ret.add(0,item);
				continue ;
			}
			ret.add(item);
		}
		return ret ;
	}

    private HeroIdentifyItem buildHeroIdentifyItem(int goodsId){
        return buildHeroIdentifyItem(goodsId,null);
    }

	
	private HeroIdentifyItem buildHeroIdentifyItem(int goodsId,RoleHero roleHero){
		GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, goodsId);
		if(null == hero){
			return null ;
		}
		HeroIdentifyItem item = new HeroIdentifyItem();
		item.setHeroId(goodsId);
		item.setName(hero.getName());
		item.setImageId(hero.getImageId());
		item.setShadowId(hero.getShadowId());
		item.setNeedShadowNum((short) hero.getShadowNum());
		item.setMaxStar(GameContext.getHeroApp().getMaxStar(hero.getQualityType()));
		item.setSeriesId(hero.getSeriesId());
		item.setGearId(hero.getGearId());
		item.setSecondType(hero.getSecondType());
		GoodsBase shadowGoods = GameContext.getGoodsApp().getGoodsBase(hero.getShadowId());
		if(null != shadowGoods){
			item.setShadowName(shadowGoods.getName());
		}
        if(null != roleHero){
            item.setLevel((byte)roleHero.getLevel());
            item.setQuality(roleHero.getQuality());
            item.setStar(roleHero.getStar());
        }else {
            item.setLevel((byte)hero.getLevel());
            item.setQuality(hero.getQualityType());
            item.setStar(hero.getStar());
        }
		return item ;
	}
	
	private void sort(List<HeroIdentifyItem> heroList){
		if(Util.isEmpty(heroList) || 0 == heroList.size()){
			return ;
		}
		Collections.sort(heroList, comparator);
        //level,quality,star 需要发送模板的值
        for(HeroIdentifyItem item : heroList){
            GoodsHero hero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, item.getHeroId());
            if(null == hero){
                continue;
            }
            item.setLevel((byte)hero.getLevel());
            item.setQuality(hero.getQualityType());
            item.setStar(hero.getStar());
        }
	}
	
}
