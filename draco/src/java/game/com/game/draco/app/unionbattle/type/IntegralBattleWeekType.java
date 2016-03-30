package com.game.draco.app.unionbattle.type;

import sacred.alliance.magic.constant.TextId;

public enum IntegralBattleWeekType {
	/*
 	星期一：Monday（Mon）
	星期二：Tuesday（Tues）
	星期三：Wednesday（Wed）
	星期四：Thursday（Thurs）
	星期五：Friday（Fri）
	星期六：Saturday（Sat）
	星期日：Sunday（Sun）
	 */
	
	MON((byte)1,(byte)2,TextId.UNION_INTEGRAL_WEEK_MON),
	TUES((byte)2,(byte)3,TextId.UNION_INTEGRAL_WEEK_TUES),
	WED((byte)3,(byte)4,TextId.UNION_INTEGRAL_WEEK_WED),
	THURS((byte)4,(byte)5,TextId.UNION_INTEGRAL_WEEK_THURS),
	FRI((byte)5,(byte)6,TextId.UNION_INTEGRAL_WEEK_FRI),
	SAT((byte)6,(byte)7,TextId.UNION_INTEGRAL_WEEK_SAT),
	SUN((byte)7,(byte)1,TextId.UNION_INTEGRAL_WEEK_SUN),
	;
	
	private final byte week;
	private final byte sysWeek;
	private final String strWeek;
	
	public byte getSysWeek() {
		return sysWeek;
	}

	public String getStrWeek() {
		return strWeek;
	}

	public byte getWeek() {
		return week;
	}

	IntegralBattleWeekType(byte week,byte sysWeek,String strWeek){
		this.week = week ;
		this.sysWeek = sysWeek;
		this.strWeek = strWeek;
	}
	
	public static IntegralBattleWeekType get(int week){
		for(IntegralBattleWeekType t : values()){
			if(t.getWeek() == week){
				return t ;
			}
		}
		return null ;
	}
	
	public static IntegralBattleWeekType getWeek(int sysWeek){
		for(IntegralBattleWeekType t : values()){
			if(t.getSysWeek() == sysWeek){
				return t ;
			}
		}
		return null ;
	}
}
