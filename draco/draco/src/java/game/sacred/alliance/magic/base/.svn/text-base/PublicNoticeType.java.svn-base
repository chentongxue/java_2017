package sacred.alliance.magic.base;

public enum PublicNoticeType {
	
	System_Notice((byte)0,"系统公告"),
	//Active_Notice((byte)1,"活动公告"),
	
	;
	
	private final byte type;
	private final String name;
	
	private PublicNoticeType(byte type, String name) {
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static PublicNoticeType get(byte type){
		for(PublicNoticeType item : PublicNoticeType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
	public static boolean containsType(byte type){
		for(PublicNoticeType item : PublicNoticeType.values()){
			if(item.getType() == type){
				return true;
			}
		}
		return false;
	}
}
