package sacred.alliance.magic.base;

public enum GenderType {
	
	MALE((byte)0, "雄性"),
	FEMAILE((byte)1, "雌性"),
	
	;
	
	private final byte type;
	private final String name;
	
	GenderType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType(){
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static GenderType getType(byte type){
		for(GenderType item : GenderType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return GenderType.FEMAILE;
	}
	
}
