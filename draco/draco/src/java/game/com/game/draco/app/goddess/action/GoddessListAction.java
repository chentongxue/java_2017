package com.game.draco.app.goddess.action;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.GoddessAppImpl;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.message.item.GoddessItem;
import com.game.draco.message.request.C1350_GoddessListReqMessage;
import com.game.draco.message.response.C1350_GoddessListRespMessage;
import com.google.common.collect.Lists;

public class GoddessListAction extends BaseAction<C1350_GoddessListReqMessage>{

	@Override
	public Message execute(ActionContext context, C1350_GoddessListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		//获得策划配置所有女神列表
		List<Integer> allGoddessIds = GameContext.getGoddessApp().getAllGoddessIds();
		if(Util.isEmpty(allGoddessIds)) {
			return null;
		}
		Map<Integer, RoleGoddess> allGoddess = GameContext.getUserGoddessApp().getAllRoleGoddess(role.getRoleId());
		List<GoddessItem> itemList = Lists.newArrayList();
		for(Integer goddessId : allGoddessIds) {
			GoddessItem item = this.getGoddessItem(goddessId, allGoddess);
			if(null == item) {
				continue;
			}
			itemList.add(item);
		}
		C1350_GoddessListRespMessage respMsg = new C1350_GoddessListRespMessage();
		respMsg.setGoddessItemList(itemList);
		return respMsg;
	}
	
	private GoddessItem getGoddessItem(int goddessId, Map<Integer, RoleGoddess> allGoddess) {
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goddessId);
		if(null == goodsGoddess) {
			return null;
		}
		GoddessItem item = new GoddessItem();
		item.setId(goddessId);
		item.setQuality(goodsGoddess.getQualityType());
		item.setImage(goodsGoddess.getImageId());
		if(null == allGoddess) {
			item.setLevel(goodsGoddess.getStartLevel());
			return item;
		}
		RoleGoddess roleGoddess = allGoddess.get(goddessId);
		if(null == roleGoddess) {
			item.setLevel(goodsGoddess.getStartLevel());
			return item;
		}
		item.setHad(GoddessAppImpl.OWN_YES);
		item.setOnBattle(roleGoddess.getOnBattle());
		item.setLevel(roleGoddess.getLevel());
		return item;
	}

}
