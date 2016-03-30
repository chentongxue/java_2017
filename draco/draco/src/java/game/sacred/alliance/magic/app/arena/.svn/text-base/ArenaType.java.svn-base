package sacred.alliance.magic.app.arena;

import sacred.alliance.magic.base.MapLogicType;

public enum ArenaType {
	_1V1(0,MapLogicType.arenaLogic),
	_LEARN(1,MapLogicType.arenaPK) //切磋
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
		default:
			return null;
		}
	}
}
