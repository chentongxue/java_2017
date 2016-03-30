package sacred.alliance.magic.base;

public enum SkyEffectType {
	
	Both_Role((byte)0,"双方效果"),
	Both_Map((byte)1,"双方地图效果"),
	World((byte)2,"全服效果"),
	
	;
	
	private final byte type;
	private final String name;
	
	SkyEffectType(byte type, String name){
		this.name=name;
		this.type=type;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static SkyEffectType get(byte type){
		for(SkyEffectType item : SkyEffectType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
