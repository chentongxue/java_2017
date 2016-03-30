package com.game.draco.app.operate.vo;


public enum OperateActiveType {
	first_pay((byte) 1, "首冲双倍"),
	month_card((byte) 2, "月卡"),
	carnival((byte) 3, "嘉年华"),
	donate((byte) 4, "乐翻天捐献"), 
	pay_extra((byte) 5, "充值额外赠送"),
	discount((byte) 6, "充值折扣活动"),
	grow_fund((byte) 7, "成长基金"),
	simple((byte)8, "简单活动"),
	;

	private final byte type;
	private final String name;

	OperateActiveType(byte type, String name) {
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public OperateActiveType getOperateActiveType(byte type) {
		for (OperateActiveType activeType : OperateActiveType.values()) {
			if (null == activeType) {
				continue;
			}
			if (activeType.getType() == type) {
				return activeType;
			}
		}
		return null;
	}

}
