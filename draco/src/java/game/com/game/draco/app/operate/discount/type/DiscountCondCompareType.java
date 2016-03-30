package com.game.draco.app.operate.discount.type;

/**
 * 0. 条件比较，逻辑类中实现
 * 1. = param1
 * 2. > param1
 * 3. >= param1
 * 4. < param1
 * 5. <= param1
 * 6. > param1 && < param2
 * 7. >= param1 && <= param2
 */
public enum DiscountCondCompareType {

	condition((byte) 0),
	equal((byte) 1),
	larger((byte) 2),
	larger_equal((byte) 3),
	less((byte) 4),
	less_equal((byte) 5),
	open_interval((byte) 6),
	closed_interval((byte) 7),
	;

	private final byte type;

	DiscountCondCompareType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public static DiscountCondCompareType get(int type) {
		for (DiscountCondCompareType v : values()) {
			if (type == v.getType()) {
				return v;
			}
		}
		return null;
	}

}
