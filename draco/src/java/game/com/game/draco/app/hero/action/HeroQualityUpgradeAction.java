package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.vo.HeroQualityUpgradeResult;
import com.game.draco.message.request.C1267_HeroQualityUpgradeReqMessage;
import com.game.draco.message.response.C1267_HeroQualityUpgradeRespMessage;

public class HeroQualityUpgradeAction extends BaseAction<C1267_HeroQualityUpgradeReqMessage>{
	private final byte SUCCESS_AND_INCR_LEVEL = (byte)2;
	
	@Override
	public Message execute(ActionContext context,
			C1267_HeroQualityUpgradeReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		int heroId = reqMsg.getHeroId() ;
		HeroQualityUpgradeResult result = GameContext.getHeroApp().heroQualityUpgrade(role, heroId);
		C1267_HeroQualityUpgradeRespMessage respMsg = new C1267_HeroQualityUpgradeRespMessage();
		respMsg.setInfo(result.getInfo());
		if(!result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.FAILURE);
			return respMsg ;
		}
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		byte status = result.getStatus();
		respMsg.setHeroId(heroId);
		respMsg.setStatus(status);
		if(SUCCESS_AND_INCR_LEVEL == status){
			respMsg.setHeroInfo(GameContext.getHeroApp().getHeroInfoItem(hero));
			//push面板刷新
			role.getBehavior().sendMessage(GameContext.getHeroApp().buildHeroQualityInfoMessage(hero));
		}else{
			respMsg.setProgress((short)hero.getQualityProgress());
		}
		return respMsg ;
	}

}
