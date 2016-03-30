package sacred.alliance.magic.base;

public enum FactionDescType {
	
	Faction_Info((byte)0,"门派信息"),
	Faction_Role((byte)1,"门派成员"),
	Faction_List((byte)2,"门派列表"),
	Apply_Info((byte)3,"申请信息"),
	
	;
	
	private final byte type;
	private final String name;
	
	FactionDescType(byte type,String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public static FactionDescType get(byte type){
		for(FactionDescType power : FactionDescType.values()){
			if(power.getType() == type){
				return power;
			}
		}
		return null;
	}

 }
