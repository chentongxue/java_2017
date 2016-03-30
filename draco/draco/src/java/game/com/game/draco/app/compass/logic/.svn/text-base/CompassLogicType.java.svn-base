package com.game.draco.app.compass.logic;

public enum CompassLogicType {

	taobao(0),
	turn(1),
	;
	
	private final int type ;
	private CompassLogicType(int type){
		this.type = type ;
	}
	
	public int getType() {
		return type;
	}
	
	public static CompassLogicType get(int type){
		for(CompassLogicType t : values()){
			if(t.getType() == type){
				return t ;
			}
		}
		return null ;
	}
	
	public CompassLogic createCompassLogic(){
		switch(this){
			case taobao:
				return new CompassTaobaoLogic() ;
			case turn :
				return new CompassTurnLogic();
			default :
				return null ;
		}
	}
	
	
}
