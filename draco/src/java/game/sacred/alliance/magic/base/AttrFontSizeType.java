package sacred.alliance.magic.base;

/**
 * 飘字大小及特殊状态(击飞，击退)类型
 *
 */
public enum AttrFontSizeType {
	
	Cycle((byte)0,"周期性伤害或治疗"),
	Common((byte)1,"正常"),
	Crit((byte)2,"暴击"),
	//State((byte)3, "状态"),
	Attr((byte)4, "属性"),
	;
	
	private final byte type;
	private final String name;
	
	AttrFontSizeType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public static AttrFontSizeType get(byte type){
		for(AttrFontSizeType item : AttrFontSizeType.values()){
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
