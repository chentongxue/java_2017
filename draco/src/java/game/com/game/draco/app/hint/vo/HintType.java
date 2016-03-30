package com.game.draco.app.hint.vo;

public enum HintType {

	target((byte) 1), //目标达成
	accumulatelogin((byte) 2), //登陆领奖
	levelgift((byte) 3), //等级礼包
	dailyplay((byte) 4),//活跃度达成
	choicegold((byte) 5),//金币抽卡
	choicegem((byte) 6),//钻石抽卡
	operate((byte)7),//运营活动
	firstpay((byte)8),//首充
	recovery((byte)9),//资源追回
	active((byte) 10),// 活动
	;

	private final byte id;
	
	HintType(byte id) {
		this.id = id;
	}

	public byte getId() {
		return id;
	}
	
	public static HintType get(byte id) {
		for (HintType item : HintType.values()) {
			if (item.getId() == id) {
				return item;
			}
		}
		return null;
	}
	
}
