package sacred.alliance.magic.app.active.vo;

import lombok.Data;
import sacred.alliance.magic.base.CondCompareType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.util.KeySupport;

public @Data class DiscountCond implements KeySupport<Integer>{
	public static final int TIMES_NO_LIMIT = -1;
	
	private int condId;
	private String desc;
	private byte type; 
	private int minValue;
	private int maxValue;
	private int timesLimit; //-1表示无限制
	
	private CondCompareType condCompareType;
	@Override
	public Integer getKey() {
		return this.condId;
	}
	
	public Result init(){
		Result result = new Result();
		condCompareType = CondCompareType.get(type);
		if(timesLimit == 0){
			result.failure();
			result.setInfo("discountCond id=" + this.condId + ", timeLimit=0");
			return result;
		}
		if(minValue == 0){
			result.failure();
			result.setInfo("discountCond id=" + this.condId + ", minValue=0");
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
	public boolean isMeet(int condCount, int value){
		boolean result = false;
		switch(condCompareType){
		case _BIGANDEQUAL:
			if(value >= minValue){
				result = true;
			}
			break;
		case _SMALLANDEQUAL:
			if(value <= minValue){
				result = true;
			}
			break;
		case _BETWEEN:
			if(value >= minValue && value <= maxValue){
				result = true;
			}
			break;
		case _XTYPEXNUM:
			if(value >= maxValue){
				result = true;
			}
			break;
		}
		return result;
	}
}
