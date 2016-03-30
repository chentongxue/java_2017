package com.game.draco.app.chat.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.game.draco.GameContext;
import com.game.draco.app.chat.Channel;
import com.game.draco.app.chat.ChannelType;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleEntity;
import sacred.alliance.magic.vo.RoleInstance;

public class CampChannel extends Channel {
	
	public CampChannel() {
		super(ChannelType.Camp);
	}
	
	@Override
	protected Collection<RoleEntity> getListeners(RoleEntity speaker, Serializable target) throws Exception {
		Collection<RoleEntity> listeners = new ArrayList<RoleEntity>();
		byte speakCampId = ((AbstractRole)speaker).getCampId();
		for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
			if(speakCampId != role.getCampId()){
				continue;
			}
			listeners.add(role);
		}
		return listeners;
	}
	
	@Override
	protected Status channelRule(RoleEntity speaker, Serializable target) {
		if(null == speaker){
			return Status.Chat_FAILURE;
		}
		return Status.SUCCESS;
	}

	@Override
	protected boolean isFilterSocialShield() {
		return true;
	}
	
}
