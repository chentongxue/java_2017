package sacred.alliance.magic.base;

public enum HoleType {
	Non_mosaic((byte)0,(byte)-1),//空位 
	Locked((byte)-1,(byte)-2),//锁
	Mosaic((byte)1,(byte)0),//镶嵌 
	;
	
	private final byte type ;
	private final byte protoType ;//协议类型
	
	HoleType(byte type,byte protoType){
		this.type = type ;
		this.protoType = protoType;
	}
	
	public byte getType() {
		return type;
	}

	public byte getProtoType() {
		return protoType;
	}
	
	
	
}
