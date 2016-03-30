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
import com.game.draco.app.hero.HeroLoveType;
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
		for (AttriItem ai : attriMap.values()) {
			if (ai.getValue() <= 0) {
				continue;
			}
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(ai.getAttriTypeValue());
			item.setValue(AttributeType.formatValue(ai.getAttriTypeValue(),
					ai.getValue()));
			attriList.add(item);
		}
		C1252_HeroDetailRespMessage respMsg = new C1252_HeroDetailRespMessage();
		respMsg.setAttriList(attriList);
		respMsg.setHeroId(heroId);
		respMsg.setMount(GameContext.getHeroApp().getHeroLoveStatus(hero,
				HeroLoveType.horse.getType()));
		respMsg.setGoddess(GameContext.getHeroApp().getHeroLoveStatus(
				hero, HeroLoveType.goddess.getType()));
		respMsg.setGodWeapon(GameContext.getHeroApp().getHeroLoveStatus(
				hero, HeroLoveType.godWeapon.getType()));
		return respMsg;
	}

}
