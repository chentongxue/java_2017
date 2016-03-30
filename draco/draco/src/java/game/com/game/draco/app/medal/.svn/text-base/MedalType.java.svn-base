package com.game.draco.app.medal;

public enum MedalType {
	
	QiangHua(0, false),
	XiangQian(1, false),
	XiLian(2, false),
	TuLong(3, true),
	RongYu(4, true),
	ZhengYi(5, true),
	ChaoShen(6, true),
	WangZhe(7, true),
	
	;
	
	private final int type;
	private final boolean attribute;
	
	MedalType(int type, boolean attribute){
		this.type = type;
		this.attribute = attribute;
	}

	public int getType() {
		return this.type;
	}
	
	public boolean isAttribute() {
		return this.attribute;
	}

	public static MedalType get(int type){
		for(MedalType item : MedalType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null ;
	}
	
}
