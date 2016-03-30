package sacred.alliance.magic.base;

import java.util.List;

/**
 * 1	大于等于
 *	2	小于等于
 *	3	等于
 *	4	不等于
 *	5	区间
 */
public enum CondCompareType {

	_BIGANDEQUAL((byte)1),
	_SMALLANDEQUAL((byte)2),
	_EQUAL((byte)3),
	_NOTEQUAL((byte)4),
	_BETWEEN((byte)5),
	_OR((byte)6),
	_XTYPEXNUM((byte)7),
	;
	private final byte type;
	
	CondCompareType(byte type){
		this.type = type;
	}
	
	public byte getType(){
		return type;
	}
	
	public static CondCompareType get(int type){
		for(CondCompareType v : values()){
			if(type == v.getType()){
				return v;
			}
		}
		return null;
	}
	
	public static boolean isMeet(CondCompareType type,int attriValue,int minValue,int maxValue, List<Integer> condOrValue){
		switch(type){
		case _BIGANDEQUAL:
			return attriValue >= minValue ;
			
		case _SMALLANDEQUAL:
			return attriValue <= minValue ;
		case _EQUAL:
			return attriValue == minValue ;
		case _NOTEQUAL:
			return attriValue != minValue ;
		case _BETWEEN:
			return attriValue >= minValue && attriValue <= maxValue ;
		case _OR:
			return condOrValue.contains(attriValue);
		}
		return false;
	
	}
}
