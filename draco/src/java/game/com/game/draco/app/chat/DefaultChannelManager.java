package com.game.draco.app.chat;

import java.util.HashMap;
import java.util.Map;

import com.game.draco.app.chat.channel.CampChannel;
import com.game.draco.app.chat.channel.GmSysChannel;
import com.game.draco.app.chat.channel.HornChannel;
import com.game.draco.app.chat.channel.MapChannel;
import com.game.draco.app.chat.channel.PrivateChannel;
import com.game.draco.app.chat.channel.PublicizePersonalChannel;
import com.game.draco.app.chat.channel.PublicizeSystemChannel;
import com.game.draco.app.chat.channel.SpeakChannel;
import com.game.draco.app.chat.channel.SystemChannel;
import com.game.draco.app.chat.channel.TeamChannel;
import com.game.draco.app.chat.channel.UnionChannel;
import com.game.draco.app.chat.channel.WorldChannel;


public class DefaultChannelManager implements ChannelManager{

	private Map<ChannelType,Channel> channelMap = new HashMap<ChannelType,Channel>();
	
	public DefaultChannelManager(){
		this.register(new PrivateChannel());
		this.register(new TeamChannel());
		this.register(new UnionChannel());
		this.register(new WorldChannel());
		this.register(new SpeakChannel());
		this.register(new MapChannel());
		this.register(new CampChannel());
		this.register(new HornChannel());
		this.register(new SystemChannel());
		this.register(new GmSysChannel());
		//this.register(new GmChannel());
		this.register(new PublicizeSystemChannel());
		this.register(new PublicizePersonalChannel());
	}
	
	@Override
	public Channel getChannel(ChannelType channelType) {
		return channelMap.get(channelType) ;
	}

	@Override
	public void register(Channel channel) {
		if(null == channel){
			return ;
		}
		channelMap.put(channel.channelType,channel);
	}

}
