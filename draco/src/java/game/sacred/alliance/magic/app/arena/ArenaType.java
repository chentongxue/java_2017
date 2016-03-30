package sacred.alliance.magic.app.arena;

import sacred.alliance.magic.base.MapLogicType;

public enum ArenaType {
	_1V1(0,MapLogicType.arenaLogic),
	_LEARN(1,MapLogicType.arenaPK), //切磋
	_3V3(2,MapLogicType.arena3V3),
	_3V3_DARK_DOOR(3,MapLogicType.arena3V3),//跨服
	;
	private final int type ;
	private final MapLogicType mapLogicType ;
	
	ArenaType(int type,MapLogicType mapLogicType){
		this.type = type ;
		this.mapLogicType = mapLogicType ;
	}

	public int getType() {
		return type;
	}
	
	
	public MapLogicType getMapLogicType() {
		return mapLogicType;
	}

	public static ArenaType get(int type){
		for(ArenaType v : values()){
			if(type == v.getType()){
				return v ;
			}
		}
		return null ;
	}
	
	public Arena createArena() {
		switch (this) {
		case _1V1:
			return new Arena_1V1();
		case _3V3:
			return new Arena_3V3() ;
		default:
			return null;
		}
	}
}
