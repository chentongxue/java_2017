package sacred.alliance.magic.base;

public enum CollectPointNotifyType {

	Refresh((byte)0), //刷新
	Disappear((byte)1), //消失
	CollectAble((byte)2),//可采
	CollectUnable((byte)3),//不可采
	;
	
	CollectPointNotifyType(byte type){
		this.type = type ;
	}

	private final byte type ;
	
	public byte getType() {
		return type;
	}
}
