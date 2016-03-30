package sacred.alliance.magic.app.benefit;

public enum LoginCountStatus {
	
	Cannot_Receive((byte)0),
	Can_Receive((byte)1),
	Have_Received((byte)2),
	
	;
	
	private final byte type;
	
	LoginCountStatus(byte type){
		this.type = type;
	}

	public byte getType() {
		return type;
	}
	
	public static LoginCountStatus get(byte type){
		for(LoginCountStatus item : LoginCountStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
