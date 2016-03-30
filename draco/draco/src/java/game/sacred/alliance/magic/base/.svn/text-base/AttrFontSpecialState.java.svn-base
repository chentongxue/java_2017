package sacred.alliance.magic.base;

/**
 * 飘字特殊状态类型
 *
 */
public enum AttrFontSpecialState {
	
	Miss((byte)1,"闪躲"),
	Resist((byte)2,"抵抗"),
	Immunity((byte)3,"免疫"),
	Absorb((byte)4,"吸收"),
	Block((byte)5,"格挡"),
	BlowFly((byte)6,"击飞"),
	repel((byte)7,"击退"),
	
	;
	
	private final byte type;
	private final String name;
	
	AttrFontSpecialState(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public static AttrFontSpecialState get(byte type){
		for(AttrFontSpecialState item : AttrFontSpecialState.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
}
