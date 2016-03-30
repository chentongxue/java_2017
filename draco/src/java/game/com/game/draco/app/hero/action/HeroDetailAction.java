package com.game.draco.app.hero.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.config.HeroLevelup;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.request.C1252_HeroDetailReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1252_HeroDetailRespMessage;
import com.google.common.collect.Lists;

public class HeroDetailAction extends BaseAction<C1252_HeroDetailReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1252_HeroDetailReqMessage reqMsg) {
		
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		int heroId = reqMsg.getHeroId();
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(
				role.getRoleId(), heroId);
		if (null == hero) {
			// 提示参数错误
			C0002_ErrorRespMessage errMsg = new C0002_ErrorRespMessage();
			errMsg.setInfo(GameContext.getI18n()
					.getText(TextId.ERROR_INPUT));
			return errMsg;
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
			item.setValue(AttributeType.formatValue(at.getType(), value));
			attriList.add(item);
		}
		C1252_HeroDetailRespMessage respMsg = new C1252_HeroDetailRespMessage();
		respMsg.setAttriList(attriList);
		respMsg.setHeroId(heroId);
		respMsg.setLoveList(GameContext.getHeroApp().getHeroLoveItemList(role.getRoleId(), heroId));
		respMsg.setHeroLevel((byte)hero.getLevel());
		respMsg.setHeroExp(hero.getExp());
		HeroLevelup lu = GameContext.getHeroApp().getHeroLevelup(hero.getQuality(), hero.getLevel()) ;
		if(null == lu){
			respMsg.setHeroMaxExp(Integer.MAX_VALUE);
		}else {
			respMsg.setHeroMaxExp(lu.getMaxExp());
		}
		return respMsg;
	}

}
