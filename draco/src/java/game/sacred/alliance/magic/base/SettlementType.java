package sacred.alliance.magic.base;

public enum SettlementType {

	Default((byte) 0, "默认"), 
	Hand((byte) 1, "挂机"), 
	Exit((byte) 2, "退出"), 
	;

	private final byte type;// 结算面板类型
	private final String name;

	private SettlementType(byte type, String name) {
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public static SettlementType get(byte type) {
		for (SettlementType actType : SettlementType.values()) {
			if (actType.getType() == type) {
				return actType;
			}
		}
		return null;
	}

}
