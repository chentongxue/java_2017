package sacred.alliance.magic.app.faction.godbeast;



public enum FactionSoulInspireType {
	
	Role((byte)0, "个人"),
	FactionRole((byte)1, "门派成员"),
	;
	
	private final byte type;
	private final String name;
	
	FactionSoulInspireType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType(){
		return type;
	}
	
    public String getName() {
		return name;
	}
    
	public static FactionSoulInspireType get(byte type){
		for(FactionSoulInspireType item : FactionSoulInspireType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
    }
}
