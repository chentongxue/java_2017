package com.game.draco.app.social.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.social.domain.DracoSocialRelation;
import com.game.draco.message.item.SocialFriendItem;
import com.game.draco.message.request.C1205_SocialFriendListReqMessage;
import com.game.draco.message.response.C1205_SocialFriendListRespMessage;

public class SocialFriendListAction extends BaseAction<C1205_SocialFriendListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1205_SocialFriendListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1205_SocialFriendListRespMessage resp = new C1205_SocialFriendListRespMessage();
		List<SocialFriendItem> friendList = new ArrayList<SocialFriendItem>();
		SocialFriendItem item = null;
		for (DracoSocialRelation relation : GameContext.getSocialApp().getFriendList(role)) {
			item = new SocialFriendItem();
			item.setRoleId(Integer.parseInt(relation.getFriendId()));
			item.setRoleLevel((byte)relation.getFriendLevel());
			item.setRoleName(relation.getFriendName());
			item.setSex(relation.getFriendSex());
			item.setCamp(relation.getCamp());
			item.setHeadId((short) relation.getFriendHeadId());
			item.setIntimate(relation.getIntimate());
			item.setIntimatelevel((byte) relation.getIntimateLevel());
			item.setMaxIntimate(relation.getIntimateConfig().getMaxIntimate());
			item.setOnline(relation.getOnline());
			item.setPraise(relation.canPraise());
			friendList.add(item);
		}
		resp.setFriendList(friendList);
		resp.setFlowerNum(role.getRoleCount().getFlowerNum());
		resp.setCanGetGoods(role.getRoleCount().getRoleTimesToByte(CountType.HaveReceivePraiseGift));//.getHaveReceivePraiseGift());
		resp.setCanGetGoodsTimes(GameContext.getSocialApp().getCanGetGoodsTimes());
		resp.setGetPraiseTimes(role.getRoleCount().getRoleTimesToInt(CountType.TodayReceivePraiseTimes));//getTodayReceivePraiseTimes());
		resp.setGoods(GameContext.getSocialApp().getPraiseGoodsInfo(role));
		return resp;
	}

}
