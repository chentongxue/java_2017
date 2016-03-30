package sacred.alliance.magic.base;

import sacred.alliance.magic.constant.TextId;

public enum FactionFuncType {
	
	Faction_Copy((byte)0,TextId.Faction_Copy),
	Active_Warlords((byte)1,TextId.Active_Warlords),
	
	;
	
	private final byte type;
	private final String name;
	
	FactionFuncType(byte type,String name){
		this.type = type;
		this.name = name;
	}
	public final byte getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	
	public static FactionFuncType get(byte type){
		for(FactionFuncType func : FactionFuncType.values()){
			if(func.getType() == type){
				return func;
			}
		}
		return null;
	}

 }
