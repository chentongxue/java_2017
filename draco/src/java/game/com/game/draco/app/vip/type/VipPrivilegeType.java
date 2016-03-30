package com.game.draco.app.vip.type;

public enum VipPrivilegeType {
	COMMON((byte)0, "普通"),
	REFRESH_QUEST_POKER((byte)1, "开启日常任务翻牌刷新功能"),
	SIGN_REFILL_TIMES((byte)2, "每月可以补签次数"),
	ATTRIBUTE_CEIL_PERCENT((byte)3,"属性上限百分比"),//1~10,000  日常经验
	ALCHEMY_COUNT((byte)4, "每日可炼金次数"),//5 点石成金能，14潜能
	LUCKEY_BOX_VIP_TIMES((byte)5, "每日可开启幸运宝箱次数"),
	HERO_ARENA_BUY_RESET_TIMES((byte)6, "英雄试炼重置次数"),
	COPY_SINGLE_MORE_TIMES((byte)7, "单个副本额外次数"),
	HERO_ARENA_REWARD_INCR((byte)9,"英雄试炼奖励增加"), 
	QUALIFY_BUY_TIMES((byte)10,"竞技场(即排位赛)购买次数"),
	QUALIFY_CD_CLEAR((byte)11,"开启竞技场(即排位赛)清CD功能"),
	SHOP_SECRET_TIMES((byte)12,"神秘商店每日刷新次数限制"),
	EQUIP_ONEKEY_STRENGTHEN((byte)13,"装备一键强化"),
	// 副本
	COPY_OPEN_RAIDS((byte)14, "开启扫荡等级"),
	COPY_SINGLE_BUY_TIMES((byte)15, "单个副本额外购买次数"),
	COPY_TYPE_MORE_TIME((byte)16, "副本大类总额外参与次数"),
	//日常任务（翻牌），VIP对应可以购买的轮数（每轮三张牌）
	QUEST_POKER_BUY_TIMES((byte)17, "日常任务（翻牌），VIP对应可以购买的轮数"),
	TOWER_RESET_TIMES((byte)18, "爬塔重置次数");
	;
	private final byte type;
	private final String name;
	
	private VipPrivilegeType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static VipPrivilegeType getPrivilegeType(int type) {
		for(VipPrivilegeType priv : VipPrivilegeType.values()){
			if(priv.getType() == type){
				return priv;
			}
		}
		return null ;
	}
}
