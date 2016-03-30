package com.game.draco.app.chat.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleEntity;

import com.game.draco.GameContext;
import com.game.draco.app.chat.Channel;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.union.domain.Union;

public class UnionChannel extends Channel{
	
	public UnionChannel() {
		super(ChannelType.Union);
	}

	@Override
	protected Collection<RoleEntity> getListeners(RoleEntity speaker, Serializable target) {
		Collection<RoleEntity> listeners = new ArrayList<RoleEntity>();
		if(null == target){
			return listeners;
		}
		listeners.addAll(GameContext.getUnionApp().getAllOnlineUnionMember((Union)target));
		return listeners;
	}

	@Override
	protected Status channelRule(RoleEntity speaker, Serializable target) {
		if(target == null){
			return Status.Chat_Faction_Null;
		}
		return Status.SUCCESS;
	}


	@Override
	protected boolean isFilterSocialShield() {
		return true;
	}
	
}
