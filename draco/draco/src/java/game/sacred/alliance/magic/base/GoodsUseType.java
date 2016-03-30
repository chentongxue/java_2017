package sacred.alliance.magic.base;

public enum GoodsUseType {
	
	Own((byte)0, "拥有"),//不扣物品
	Consume((byte)1, "消耗"),//扣除物品
	
	;
	
	private final byte type;
	private final String name;
	
	GoodsUseType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static GoodsUseType get(byte type){
		for(GoodsUseType item : GoodsUseType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
