package com.game.draco.app.buff;

public enum BuffLostType {
	dieLost(0,"死亡消失"),
	offlineLost(1,"离线消失"),
	changeLost(2,"变身消失"),
	;
	private final int type;
	private final String name;
	
	BuffLostType(int type, String name){
		this.name=name;
		this.type=type;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
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
