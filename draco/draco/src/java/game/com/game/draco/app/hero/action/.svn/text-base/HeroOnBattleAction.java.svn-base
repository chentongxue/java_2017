package com.game.draco.app.hero.action;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.RoleSkillItem;
import com.game.draco.message.request.C1254_HeroOnBattleReqMessage;
import com.game.draco.message.response.C1254_HeroOnBattleRespMessage;
import com.google.common.collect.Lists;

public class HeroOnBattleAction extends BaseAction<C1254_HeroOnBattleReqMessage>{

	@Override
	public Message execute(ActionContext context, C1254_HeroOnBattleReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C1254_HeroOnBattleRespMessage respMsg = new C1254_HeroOnBattleRespMessage();
		Result result = GameContext.getHeroApp().onBattle(role, reqMsg.getHeroId());
		respMsg.setInfo(result.getInfo());
		if(!result.isSuccess()){
			return respMsg ;
		}
		//出战成功
		RoleHero onBattleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
		//外形资源
		GoodsHero gb = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, onBattleHero.getHeroId());
		respMsg.setResId((short)gb.getResId());
		respMsg.setHeroHeadId(gb.getHeadId());
		
		//技能
		List<RoleSkillItem> skillItems = Lists.newArrayList() ;
		Map<Short,RoleSkillStat> skillMap = onBattleHero.getSkillMap() ;
		if(!Util.isEmpty(skillMap)){
			for(RoleSkillStat stat : skillMap.values()){
				Skill skill = GameContext.getSkillApp().getSkill(stat.getSkillId());
				RoleSkillItem item = sacred.alliance.magic.util.Converter.getRoleSkillItem(role, 
						skill, stat.getSkillLevel(), stat.getLastProcessTime());
				skillItems.add(item);
			}
			Collections.sort(skillItems, new Comparator<RoleSkillItem>() {
				@Override
				public int compare(RoleSkillItem o1, RoleSkillItem o2) {
					if(o1.getSkillId() < o2.getSkillId()) {
						return -1;
					}
					if(o1.getSkillId() > o2.getSkillId()) {
						return 1;
					}
					return 0;
				}
			});
			respMsg.setSkillItems(skillItems);
		}
		
		respMsg.setHeroId(reqMsg.getHeroId());
		respMsg.setResRate(gb.getResRate());
		respMsg.setWeaponResId((short)gb.getWeaponResId());
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg ;
	}

}
