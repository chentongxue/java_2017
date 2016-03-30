package sacred.alliance.magic.base;

public enum ActiveClassifyType {
	
	Recommend((byte)0,"推荐活动"),
	Daily((byte)1,"日常活动"),
	ThisWeek((byte)2,"本周活动"),
	Today((byte)3,"今日可接活动");
	
	private final byte type;
	private final String name;
	
	private ActiveClassifyType(byte type, String name) {
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static ActiveClassifyType get(byte type){
		for(ActiveClassifyType actType:ActiveClassifyType.values()){
			if(actType.getType()==type){
				return actType;
			}
		}
		return null;
	}
}
