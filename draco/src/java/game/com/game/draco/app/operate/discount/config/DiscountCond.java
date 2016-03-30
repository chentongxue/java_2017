package com.game.draco.app.operate.discount.config;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.util.KeySupport;

import com.game.draco.app.operate.discount.type.DiscountCondCompareType;

public @Data class DiscountCond implements KeySupport<Integer> {

	public static final int TIMES_NO_LIMIT = -1;

	private int condId;// 条件Id
	private String desc;// 条件
	private byte type;// 比较类型
	private int param1;
	private int param2;
	private int timesLimit;// -1表示无限制

	private DiscountCondCompareType compareType;// 比较类型

	@Override
	public Integer getKey() {
		return this.condId;
	}

	/**
	 * 启服初始化
	 * @return
	 */
	public Result init() {
		Result result = new Result();
		if (timesLimit == 0) {
			result.setInfo("discountCond id=" + this.condId + ", timeLimit=0");
			return result;
		}
		this.compareType = DiscountCondCompareType.get(type);
		if (null == this.compareType) {
			result.setInfo("discountCond id=" + this.condId + ", CondCompareType not exist!");
			return result;
		}
		return result.success();
	}

	/**
	 * 判断次数限制和钱数是否满足条件
	 * @param condCount 以满足条件的次数
	 * @param value 具体值
	 * @return
	 */
	public boolean isMeet(int param1, int param2) {
		switch (compareType) {
		case equal:
			return param1 == this.param1;
		case larger:
			return param1 > this.param1;
		case larger_equal:
			return param1 >= this.param1;
		case less:
			return param1 < this.param1;
		case less_equal:
			return param1 <= this.param1;
		case open_interval:
			return param1 > this.param1 && param1 < this.param2;
		case closed_interval:
			return param1 >= this.param1 && param1 <= this.param2;
		}
		return false;
	}
	
	/**
	 * 是否符合参数
	 * @param value
	 * @return
	 */
	public boolean isMeetParam(int value) {
		switch (compareType) {
		case equal:
			return value == this.param1;
		case larger:
			return value > this.param1;
		case larger_equal:
			return value >= this.param1;
		case less:
			return value < this.param1;
		case less_equal:
			return value <= this.param1;
		case open_interval:
			return value > this.param1 && value < this.param2;
		case closed_interval:
			return value >= this.param1 && value <= this.param2;
		}
		return false;
	}
	
	/**
	 * 是否符合条件(>=param2)
	 * @param value
	 * @return
	 */
	public boolean isMeetCond(int value) {
		return value >= this.param2;
	}
	
}
