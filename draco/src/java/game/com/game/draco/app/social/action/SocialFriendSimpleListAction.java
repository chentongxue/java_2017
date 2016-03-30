package com.game.draco.app.social.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.social.domain.DracoSocialRelation;
import com.game.draco.message.item.SocialFriendItem;
import com.game.draco.message.request.C1206_SocialFriendSimpleListReqMessage;
import com.game.draco.message.response.C1206_SocialFriendSimpleListRespMessage;

public class SocialFriendSimpleListAction extends BaseAction<C1206_SocialFriendSimpleListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1206_SocialFriendSimpleListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		List<SocialFriendItem> friendList = new ArrayList<SocialFriendItem>();
		SocialFriendItem item = null;
		for(DracoSocialRelation relation : GameContext.getSocialApp().getSimpleFriendList(role)){
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
		C1206_SocialFriendSimpleListRespMessage resp = new C1206_SocialFriendSimpleListRespMessage();
		resp.setFriendList(friendList);
		return resp;
	}
	
}
