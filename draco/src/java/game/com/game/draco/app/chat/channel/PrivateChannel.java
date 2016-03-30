package com.game.draco.app.chat.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.game.draco.GameContext;
import com.game.draco.app.chat.Channel;
import com.game.draco.app.chat.ChannelType;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleEntity;

public class PrivateChannel extends Channel{
	
	public PrivateChannel() {
		super(ChannelType.Private);
	}

	protected boolean isSendToSpeaker(){
		return false ;
	}


	@Override
	protected Collection<RoleEntity> getListeners(RoleEntity speaker, Serializable target) {
		Collection<RoleEntity> listeners = new ArrayList<RoleEntity>();
		if(null != target){
			listeners.add((RoleEntity)target);
		}
		if(isSendToSpeaker()){
			listeners.add(speaker);
		}
		return listeners;
	}

	@Override
	protected Status channelRule(RoleEntity speaker, Serializable target) {
		if(null == target){
			return Status.Chat_Role_Offilne;
		}
		String roleId = speaker.getRoleId();
		String targetRoleId = ((RoleEntity)target).getRoleId();
		if(targetRoleId.equals(roleId)){
			return Status.Chat_Private_Self;
		}
		if(GameContext.getSocialApp().isShieldByTarget(roleId, targetRoleId)){
			return Status.Chat_Sheild_By_Target;
		}
		return Status.SUCCESS;
	}


	@Override
	protected boolean isFilterSocialShield() {
		return true;
	}
}
