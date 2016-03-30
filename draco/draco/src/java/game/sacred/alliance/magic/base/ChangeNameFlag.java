package sacred.alliance.magic.base;

public enum ChangeNameFlag {
	
	canot((byte)0),
	notmust((byte)1),
	must((byte)2);
	
	private final byte type;
	
	private ChangeNameFlag(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public static ChangeNameFlag getActiveStatus(byte type){
		for(ChangeNameFlag item : ChangeNameFlag.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
