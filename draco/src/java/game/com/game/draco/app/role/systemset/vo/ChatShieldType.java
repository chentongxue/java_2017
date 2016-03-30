package com.game.draco.app.role.systemset.vo;

import com.game.draco.app.chat.ChannelType;

/**
 * 聊天频道设置：
 * 对应位置的值标识是否屏蔽[0:不屏蔽 1:屏蔽]
 * 频道位置[1-私聊 2-队伍 3-门派 4-世界 5-喊话 6-地图 7-系统 8-阵营 11-大喇叭]
 */
public enum ChatShieldType {
	
	Private((byte)1, ChannelType.Private, 1),
	Team((byte)2, ChannelType.Team, 2),
	Union((byte)3, ChannelType.Union, 4),
	World((byte)4, ChannelType.World, 8),
	Speak((byte)5, ChannelType.Speak, 16),
	Map((byte)6, ChannelType.Map, 32),
	System((byte)7, ChannelType.System, 64),
	Camp((byte)8, ChannelType.Camp, 128),
	Horn((byte)11, ChannelType.Horn, 1024),
	
	;
	
	private final byte type;
	private final ChannelType channelType;
	private final int value;
	
	ChatShieldType(byte type, ChannelType channelType, int value){
		this.type = type;
		this.channelType = channelType;
		this.value = value;
	}
	
	public static ChatShieldType getByType(int type){
		for(ChatShieldType item : ChatShieldType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
	public static ChatShieldType getByChannel(ChannelType channelType){
		for(ChatShieldType item : ChatShieldType.values()){
			if(item.getChannelType() == channelType){
				return item;
			}
		}
		return null;
	}

	public byte getType() {
		return type;
	}

	public ChannelType getChannelType() {
		return channelType;
	}

	public int getValue() {
		return value;
	}
	
}
