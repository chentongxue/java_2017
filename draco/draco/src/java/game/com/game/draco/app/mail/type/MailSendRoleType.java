package com.game.draco.app.mail.type;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum MailSendRoleType {
	
	GM(1,TextId.MAIL_SEND_ROLE_GM),
	Auction(3,TextId.MAIL_SEND_ROLE_AUCTION),
	Compass(4,TextId.MAIL_SEND_ROLE_COMPASS),
	Faction(6,TextId.MAIL_SEND_ROLE_FACTION),
	Social_Friend(7,TextId.MAIL_SEND_ROLE_SOCIAL_FRIEND),
	Pay(9,TextId.MAIL_SEND_ROLE_PAY),
	Active_Code(10,TextId.MAIL_SEND_ROLE_ACTIVE_CODE),
	Rank(12,TextId.MAIL_SEND_ROLE_RANK),
	Siege(13,TextId.MAIL_SEND_ROLE_SIEGE),
	System(15,TextId.MAIL_SEND_ROLE_SYSTEM),
	Arena1V1(16,TextId.MAIL_SEND_ROLE_ARENA1V1),
	AngelChest(17,TextId.MAIL_SEND_ROLE_ANGELCHEST),
	TreasureMap(18,TextId.MAIL_SEND_ROLE_TREASUREMAP),
	ArenaTop(20,TextId.ARENA_TOP_NAME),
	LuckyBox(21,TextId.MAIL_SEND_ROLE_LUCKYBOX),
	AsyncArena(22,TextId.MAIL_SEND_ROLE_ASYNC_ARENA_RANK_REWARD),
	VIP(23,TextId.MAIL_SEND_ROLE_VIP),
	;
	private final int type;
	private final String name;
	
	MailSendRoleType(int type,String name){
		this.type = type;
		this.name = name;
	}
	public static MailSendRoleType get(int type){
		for(MailSendRoleType item : MailSendRoleType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	public int getType() {
		return type;
	}
	public String getName() {
		return GameContext.getI18n().getText(name);
	}
	
}
