package sacred.alliance.magic.base;

public enum ActiveCarnivalStatus {
	
	OutDate((byte)0),//已经过期
	InStatDate((byte)1),//在活动时间内
	NotOpen((byte)2);//未开始
	
	private final byte type;
	
	private ActiveCarnivalStatus(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public static ActiveCarnivalStatus getActiveStatus(byte type){
		for(ActiveCarnivalStatus item : ActiveCarnivalStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
