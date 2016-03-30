package com.game.draco.app.chat;


public interface ChannelManager {
	
	/**
	 * 注入聊天频道
	 * @param channel
	 */
	public void register(Channel channel) ;
	/**
	 * 获取聊天频道
	 * @param channelType
	 * @param speaker
	 * @return
	 */
	public Channel getChannel(ChannelType channelType) ;
	
}
