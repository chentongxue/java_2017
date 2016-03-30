package sacred.alliance.magic.app.chat.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import sacred.alliance.magic.app.chat.Channel;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleEntity;

public class SystemChannel extends Channel{
	
	public SystemChannel() {
		super(ChannelType.System);
	}

	@Override
	protected Collection<RoleEntity> getListeners(RoleEntity speaker, Serializable target) {
		Collection<RoleEntity> listeners = new ArrayList<RoleEntity>();
		if(null != target){
			listeners.add((RoleEntity)target);
		}
		return listeners;
	}

	@Override
	protected Status channelRule(RoleEntity speaker, Serializable target) {
		if(null == speaker){
			return Status.SUCCESS;
		}
		if(speaker.getRoleType() == RoleType.GM){
			return Status.SUCCESS;
		}
		return Status.Chat_Role_Send_SysMsg;
	}

	@Override
	protected boolean isSendToSpeaker() {
		return false;
	}

	@Override
	protected boolean isFilterSocialShield() {
		return false;
	}
}
