package sacred.alliance.magic.base;

/**
 * 飘字颜色类型
 *
 */
public enum AttrFontColorType {
	
	//Common_Attack((byte)0,"普通攻击"),
	Skill_Attack((byte)1,"技能伤害"),
	Be_Hurt((byte)2,"受到伤害"),
	HP_Revert((byte)3,"恢复HP"),
	//MP_Change((byte)4,"MP变化"),
	Attr_Hurt((byte)5,"受到属性相克伤害"),
	//Anger_Revert((byte)5,"恢复气力"),
	Special_State((byte)6,"特殊状态"),
	Pet_Attack((byte)7,"宠物攻击"),
	;
	
	private final byte type;
	private final String name;
	
	AttrFontColorType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public static AttrFontColorType get(byte type){
		for(AttrFontColorType item : AttrFontColorType.values()){
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
