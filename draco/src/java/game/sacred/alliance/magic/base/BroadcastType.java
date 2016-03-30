package sacred.alliance.magic.base;

public enum BroadcastType {
	
	loot((byte)0),//掉落
	box((byte)1)//宝箱
	;
	
	private final byte type;
	
	private BroadcastType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}
	
	public static BroadcastType get(byte type){
		for(BroadcastType broadcastType : BroadcastType.values()){
			if(broadcastType.getType() == type){
				return broadcastType;
			}
		}
		return null;
	}
}
