package com.game.draco.app.equip.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.item.HeroEquipFormulaListItem;
import com.game.draco.message.request.C1274_HeroEquipFormulaListReqMessage;
import com.game.draco.message.response.C1274_HeroEquipFormulaListRespMessage;
import com.google.common.collect.Lists;

public class HeroEquipFormulaListAction extends BaseAction<C1274_HeroEquipFormulaListReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1274_HeroEquipFormulaListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		int[] heroIds = reqMsg.getHeroIds() ;
		if(null == heroIds){
			return null ;
		}
		C1274_HeroEquipFormulaListRespMessage respMsg = new C1274_HeroEquipFormulaListRespMessage();
		List<HeroEquipFormulaListItem> list = Lists.newArrayList();
		for (int i = 0; i < heroIds.length; i++) {
			int heroId = heroIds[i];
			RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId) ;
			if(null == roleHero){
				continue ;
			}
			HeroEquipFormulaListItem heroItem = new HeroEquipFormulaListItem();
			heroItem.setHeroId(heroId);
			heroItem.setFormulaList(GameContext.getEquipApp().getHeroEquipFormula(role, roleHero));
			list.add(heroItem);
		}
		respMsg.setList(list);
		return respMsg ;
	}

}
