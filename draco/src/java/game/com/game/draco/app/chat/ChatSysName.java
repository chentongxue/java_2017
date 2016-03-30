package com.game.draco.app.chat;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum ChatSysName {
	
	System(0,TextId.CHAT_SYSTEM_NAME),
	Team(1,TextId.CHAT_SYSTEM_NAME),
	Union(2,TextId.CHAT_SYSTEM_NAME),
	Arena(3,TextId.CHAT_SYSTEM_NAME),
	Treasure(4,TextId.CHAT_SYSTEM_NAME),
	Active_Compass(5,TextId.CHAT_SYSTEM_NAME),
	Active_Samsara(6,TextId.CHAT_SYSTEM_NAME),
	Goods_Strengthen(7,TextId.CHAT_SYSTEM_NAME),
	Tower(8,TextId.CHAT_SYSTEM_NAME),
	Rank(9,TextId.CHAT_SYSTEM_NAME),
	BOSS_AI(10,""),
	Copy_Team(11,TextId.CHAT_SYSTEM_NAME),
	Camp_War(12,TextId.CHAT_SYSTEM_NAME),
	Arena_Top(13,TextId.ARENA_TOP_NAME),
	Goods_UpgradeStar(14,TextId.CHAT_SYSTEM_NAME),
	Survival_Team(15,TextId.CHAT_SYSTEM_NAME),
	;
	
	private final int type;
	private final String name;
	
	ChatSysName(int type, String name){
		this.type = type;
		this.name = name;
	}
	
	public int getType(){
		return this.type ;
	}
	
	public String getName() {
		return GameContext.getI18n().getText(name);
	}

	public static ChatSysName getChannelType(int type){
		for(ChatSysName item : ChatSysName.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null ;
	}
}
