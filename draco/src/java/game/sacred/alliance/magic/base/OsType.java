package sacred.alliance.magic.base;

public enum OsType {
	
	Android((byte)0,"etc"),
	Ios((byte)1,"pvr"),
	Windows((byte)2,"etc"),
	Emulator((byte)3,"etc"),
	;
	
	private final byte type;
	private final String resType ;
	
	private OsType(byte type,String resType) {
		this.type = type;
		this.resType = resType ;
	}

	public byte getType() {
		return type;
	}
	
	public String getResType() {
		return resType;
	}

	public static OsType get(byte type){
		for(OsType item : OsType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return Android;
	}
	
	public static OsType getByResType(String resType){
		if(null == resType){
			return Android ;
		}
		for(OsType item : OsType.values()){
			if(resType.equals(item.getResType())){
				return item;
			}
		}
		return Android;
	}
}
