package com.game.draco.app.asyncarena.vo;


public enum AsyncArenaBattleScoreType {
	
	/**
	 * A组：战斗力相当于玩家105%-120% 橙
		B组：战斗力相当于玩家95%-105% 紫
		C组：战斗力相当于玩家的80%-95% 蓝
		D组：战斗力大于玩家的120% 红
		E组：战斗力小于玩家80% 绿
	 */
	green(-1,8000,(byte)2),
	blue(8000,9500,(byte)3),
	purple(9500,10500,(byte)4),
	orange(10500,12000,(byte)5),
	red(12000,-1,(byte)6),
	;
	
	public final static float TEN_ASYNC_F = 10000.f;
	
	private final int scoreStart;
	
	private final int scoreEnd;
	
	private final byte quailty;
	
	AsyncArenaBattleScoreType(int scoreStart, int scoreEnd,byte quailty) {
		this.scoreStart = scoreStart;
		this.scoreEnd = scoreEnd;
		this.quailty = quailty;
	}

	public int getScoreStart() {
		return scoreStart;
	}

	public int getScoreEnd() {
		return scoreEnd;
	}
	
	public byte getQuailty() {
		return quailty;
	}

	public static byte getQuality(int bs,int tbs){
		for(AsyncArenaBattleScoreType type : values()){
			if(type.getScoreStart() == -1){
				if(tbs < bs  * (type.getScoreEnd() / TEN_ASYNC_F) ){
					return type.getQuailty();
				}
			}else if(type.getScoreEnd() == -1){
				if(tbs >= bs  * (type.getScoreStart() / TEN_ASYNC_F) ){
					return type.getQuailty();
				}
			}
			if(tbs >= bs * (type.getScoreStart() / TEN_ASYNC_F) 
					&& tbs < bs * (type.getScoreEnd() / TEN_ASYNC_F)){
				return type.getQuailty();
			}
		}
		return 0;
	}
	
}
