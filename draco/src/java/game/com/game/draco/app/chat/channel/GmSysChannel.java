package com.game.draco.app.chat.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.game.draco.GameContext;
import com.game.draco.app.chat.Channel;
import com.game.draco.app.chat.ChannelType;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleEntity;

public class GmSysChannel extends Channel{
	
	/** GM发送系统消息(游戏用户将在世界频道收到消息) */
	public GmSysChannel() {
		super(ChannelType.GmSys);
	}

	@Override
	protected Collection<RoleEntity> getListeners(RoleEntity speaker, java.io.Serializable target) {
		Collection<RoleEntity> listeners = new ArrayList<RoleEntity>();
		listeners.addAll(GameContext.getOnlineCenter().getAllOnlineRole());
		return listeners;
	}

	@Override
	protected Status channelRule(RoleEntity speaker, Serializable target) {
		return Status.SUCCESS;
	}

	@Override
	protected boolean isFilterSocialShield() {
		return false;
	}
	
}
