package sacred.alliance.magic.base;

/**
 * @author Administrator
 * 
 */
public enum BindingType {
	template((byte)-1),//根据模板决定
	no_binding((byte) 0), // 物品不绑定 ; 库.人
	already_binding((byte) 1), // 已绑定 ; 人
	equip_binding((byte) 2), // 装备绑定 ; 库.人
	gain_binding((byte) 3), // 物品拾取绑定 ; 库
	;
	public final byte type;

	BindingType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public static BindingType get(int type) {
		for (BindingType bt : BindingType.values()) {
			if (bt.getType() == type) {
				return bt;
			}
		}
		return already_binding;
	}

}
