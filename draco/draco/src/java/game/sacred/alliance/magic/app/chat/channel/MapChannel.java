package sacred.alliance.magic.app.chat.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import sacred.alliance.magic.app.chat.Channel;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleEntity;

public class MapChannel extends Channel{
	
	public MapChannel() {
		super(ChannelType.Map);
	}

	@Override
	protected Collection<RoleEntity> getListeners(RoleEntity speaker, java.io.Serializable target) {
		Collection<RoleEntity> listeners = new ArrayList<RoleEntity>();
		if(null == target){
			return listeners;
		}
		listeners.addAll(((MapInstance) target).getRoleList());
		return listeners;
	}

	@Override
	protected Status channelRule(RoleEntity speaker, Serializable target) {
		if(null == target){
			return Status.Chat_FAILURE;
		}
		return Status.SUCCESS;
	}

	@Override
	protected boolean isSendToSpeaker() {
		return false;
	}

	@Override
	protected boolean isFilterSocialShield() {
		return true;
	}
	
}
