package sacred.alliance.magic.base;

public enum RateType {
	unknow(-1,"未定义"),
	system_exp_award(1,"系统双倍经验"),
	;
	
	private final int type;
	private final String name;
	RateType(int type,String name) {
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}
	
	public String getName(){
		return name;
	}

	public static RateType get(int type) {
		for (RateType rateType : RateType.values()) {
			if (type == rateType.getType()) {
				return rateType;
			}
		}
		return unknow;
	}

}
