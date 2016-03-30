package com.game.draco.app.chat.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.game.draco.app.chat.Channel;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.team.Team;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleEntity;

public class TeamChannel extends Channel{
	
	public TeamChannel() {
		super(ChannelType.Team);
	}

	@Override
	protected Collection<RoleEntity> getListeners(RoleEntity speaker, Serializable target) {
		Collection<RoleEntity> listeners = new ArrayList<RoleEntity>();
		if(null == target){
			return listeners;
		}
		listeners.addAll(((Team)target).getMembers());
		return listeners ;
	}

	@Override
	protected Status channelRule(RoleEntity speaker, Serializable target) {
		Team team = (Team)target;
		if(null == team){
			return Status.Chat_Team_Null;
		}
		if(team.getPlayerNum() <= 1){
			return Status.Chat_Team_Null;
		}
		return Status.SUCCESS;
	}


	@Override
	protected boolean isFilterSocialShield() {
		return true;
	}
}
