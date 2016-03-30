package sacred.alliance.magic.app.chat.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.chat.Channel;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleEntity;

public class PublicizePersonalChannel extends Channel {
	
	public PublicizePersonalChannel() {
		super(ChannelType.Publicize_Personal);
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
	protected boolean isSendToSpeaker() {
		return true;
	}

	@Override
	protected boolean isFilterSocialShield() {
		return false;
	}
	
}
