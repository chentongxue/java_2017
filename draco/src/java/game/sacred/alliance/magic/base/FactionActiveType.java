package sacred.alliance.magic.base;

public enum FactionActiveType {
	
	Salary((byte)1,"门派工资"),
	Quest((byte)2,"门派任务"),
	Copy((byte)3,"妖兽副本"),
	Faction_War((byte)4,"门派战"),
	Faction_Soul_War((byte)5,"仙兽逆袭"),
	Hell_Copy((byte)6,"地狱副本")
	;
	
	private final byte type;
	private final String name;
	
	FactionActiveType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	
	public static FactionActiveType getPosition(byte type){
		for(FactionActiveType item : FactionActiveType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
