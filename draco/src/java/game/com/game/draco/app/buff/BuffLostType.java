package com.game.draco.app.buff;

public enum BuffLostType {
	dieLost(0),//死亡消失
	offlineLost(1),//离线消失
	swtichHero(2),//切换英雄消失
	transLost(3),//传送消失
	exitInsLost(4),//退出副本消失
	;
	private final int type;
	
	BuffLostType(int type){
		this.type=type;
	}

	public int getType() {
		return type;
	}

	public static BuffLostType get(int type){
		for(BuffLostType tt : values()){
			if(tt.getType() == type){
				return tt ;
			}
		}
		return null ;
	}
}
