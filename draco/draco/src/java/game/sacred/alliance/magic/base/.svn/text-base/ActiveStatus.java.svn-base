package sacred.alliance.magic.base;

public enum ActiveStatus {
	
	Miss((byte)-1,"失效", (byte)127),//这种状态不需要给客户端发
	NotOpen((byte)0,"未开启", (byte)4),
	Underway((byte)1,"进行中", (byte)1),
	CanReward((byte)2,"可领取", (byte)0),
	Finished((byte)3,"已完成", (byte)3),
	CanAccept((byte)4,"可接取", (byte)2);
	
	private final byte type;
	private final String name;
	private final byte sortValue;
	
	private ActiveStatus(byte type, String name, byte sortValue) {
		this.type = type;
		this.name = name;
		this.sortValue = sortValue;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public byte getSortValue(){
		return sortValue;
	}
	
	public static ActiveStatus getActiveStatus(byte type){
		for(ActiveStatus item : ActiveStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
