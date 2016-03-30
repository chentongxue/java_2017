package sacred.alliance.magic.base;

public enum FactionBuildFuncType {
	Faction_Null((byte)0,""),
	Faction_Soul((byte)1,"门派神兽"),
	Faction_Store((byte)2,"门派商店"),
	Faction_Skill((byte)3,"门派技能"),
	Faction_Warehouse((byte)4,"门派仓库"),
	;
	
	private final byte type;
	private final String name;
	
	FactionBuildFuncType(byte type,String name){
		this.type = type;
		this.name = name;
	}
	public final byte getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	public static FactionBuildFuncType get(byte type){
		for(FactionBuildFuncType func : FactionBuildFuncType.values()){
			if(func.getType() == type){
				return func;
			}
		}
		return null;
	}
 }
