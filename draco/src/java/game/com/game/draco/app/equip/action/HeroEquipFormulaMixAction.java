package com.game.draco.app.equip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.equip.config.StarUpgradeFormula;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.request.C1279_HeroEquipFormulaMixReqMessage;
import com.game.draco.message.response.C1279_HeroEquipFormulaMixRespMessage;

public class HeroEquipFormulaMixAction extends BaseAction<C1279_HeroEquipFormulaMixReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1279_HeroEquipFormulaMixReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		int goodsId = reqMsg.getGoodsId() ;
		C1279_HeroEquipFormulaMixRespMessage respMsg = new C1279_HeroEquipFormulaMixRespMessage();
		Result result = GameContext.getEquipApp().formulaMix(role, goodsId,Math.max(1,reqMsg.getMixNum()));
		if(result.isIgnore()){
			return null;
		}
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		respMsg.setHeroId(reqMsg.getHeroId());
		if(!result.isSuccess()){
			return respMsg ;
		}
		try {
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if (null == gb
					|| GoodsType.GoodsEquHuman.getType() != gb.getGoodsType()) {
				// 非装备
				return respMsg;
			}
			RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(
					role.getRoleId(), reqMsg.getHeroId());
			if (null == roleHero) {
				return respMsg;
			}
			String goodsInstanceId = null;
			// 获取第一个可可穿的装备
			RoleGoods[] allGoods = role.getRoleBackpack().getGrids();
			for (RoleGoods rg : allGoods) {
				if (null == rg) {
					continue;
				}
				if (rg.getGoodsId() != goodsId) {
					continue;
				}
				// 判断是否能穿
				StarUpgradeFormula formula = GameContext.getEquipApp()
						.getStarUpgradeFormula(rg.getGoodsId(),
								rg.getQuality(), rg.getStar());
				if (null == formula
						|| roleHero.getLevel() >= formula.getHeroLevel()) {
					goodsInstanceId = rg.getId();
					break;
				}
			}
			respMsg.setGoodsInstanceId(goodsInstanceId);
		}catch(Exception ex){
			logger.error("",ex);
		}
		return respMsg;
	}

}
