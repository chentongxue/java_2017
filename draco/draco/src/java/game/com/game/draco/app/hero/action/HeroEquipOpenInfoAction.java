package com.game.draco.app.hero.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.config.HeroEquipOpen;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.HeroEquipOpenCondItem;
import com.game.draco.message.request.C1255_HeroEquipOpenInfoReqMessage;
import com.game.draco.message.response.C1255_HeroEquipOpenInfoRespMessage;
import com.google.common.collect.Lists;

public class HeroEquipOpenInfoAction extends BaseAction<C1255_HeroEquipOpenInfoReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1255_HeroEquipOpenInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C1255_HeroEquipOpenInfoRespMessage respMsg = new C1255_HeroEquipOpenInfoRespMessage();
		List<HeroEquipOpenCondItem> lockInfo = Lists.newArrayList();
		for(byte i=0;i<ParasConstant.HERO_EQUIP_MAX_NUM;i++){
			HeroEquipOpen conf = GameContext.getHeroApp().getHeroEquipOpen(i);
			if(null == conf){
				continue ;
			}
			if(GameContext.getHeroApp().isEquipPosOpenOrFreeOpen(role, i)){
				//已开启或自动开启
				continue ;
			}
			HeroEquipOpenCondItem condItem = new HeroEquipOpenCondItem();
			condItem.setPos(i);
			condItem.setRoleLevel((byte)conf.getRoleLevel());
			if(!conf.isFree()){
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(conf.getGoodsId());
				if(null == gb){
					continue ;
				}
				GoodsLiteNamedItem goodsItem = gb.getGoodsLiteNamedItem();
				goodsItem.setNum(conf.getGoodsNum());
				condItem.setGoodsItem(goodsItem);
			}
			lockInfo.add(condItem);
		}
		respMsg.setLockInfo(lockInfo);
		return respMsg;
	}

}
