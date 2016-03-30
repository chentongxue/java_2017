package com.game.draco.app.hero.action;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.HeroEquipBackpack;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.HeroEquipItem;
import com.game.draco.message.item.StorageContainerItem;
import com.game.draco.message.request.C1256_HeroEquipListReqMessage;
import com.game.draco.message.response.C1256_HeroEquipListRespMessage;
import com.google.common.collect.Lists;

public class HeroEquipListAction extends BaseAction<C1256_HeroEquipListReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C1256_HeroEquipListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1256_HeroEquipListRespMessage respMsg = new C1256_HeroEquipListRespMessage();
		Collection<HeroEquipBackpack> packList = GameContext.getUserHeroApp()
				.getEquipBackpack(role.getRoleId());
		if (null == packList) {
			return respMsg;
		}

		List<HeroEquipItem> equipItemList = Lists.newArrayList();
		for (HeroEquipBackpack pack : packList) {
			RoleGoods[] arr = pack.getGrids();
			if (null == arr || 0 == arr.length) {
				continue;
			}
			List<StorageContainerItem> list = GoodsHelper
					.createContainerItemList(arr);
			if (Util.isEmpty(list)) {
				continue;
			}
			HeroEquipItem equipItem = new HeroEquipItem();
			equipItem.setHeroId(pack.getHeroId());
			equipItem.setEquipList(list);
			equipItemList.add(equipItem);
		}
		respMsg.setEquipItemList(equipItemList);
		return respMsg;
	}

}
