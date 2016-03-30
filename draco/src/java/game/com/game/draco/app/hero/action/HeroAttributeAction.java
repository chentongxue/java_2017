package com.game.draco.app.hero.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.request.C1264_HeroAttributeReqMessage;
import com.game.draco.message.response.C1264_HeroAttributeRespMessage;
import com.google.common.collect.Lists;

public class HeroAttributeAction extends BaseAction<C1264_HeroAttributeReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1264_HeroAttributeReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		int heroId = reqMsg.getHeroId();
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(
				role.getRoleId(), heroId);
		if (null == hero) {
			// 提示参数错误
			return null ;
		}
		// 计算各部分属性
		List<AttriTypeStrValueItem> attriList = Lists.newArrayList();
		AttriBuffer buffer = GameContext.getHeroApp().getHeroAttriBuffer(hero);
		java.util.Map<Byte, AttriItem> attriMap = buffer.getMap();
		for (AttributeType at : GameContext.getHeroApp().getAttributeTypeList()) {
            AttriItem ai = attriMap.get(at.getType()) ;
            float value = (null == ai)?0:ai.getValue();
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(at.getType());
			item.setValue(AttributeType.formatValue(at.getType(),value));
			attriList.add(item);
		}
		C1264_HeroAttributeRespMessage respMsg = new C1264_HeroAttributeRespMessage();
		respMsg.setAttriList(attriList);
		respMsg.setHeroId(heroId);
		//战斗力
		respMsg.setBattleScore(GameContext.getHeroApp().getBattleScore(hero));
		return respMsg;
	}

}
