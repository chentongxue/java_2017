package sacred.alliance.magic.app.benefit;

public enum SerialLoginStatus {
	
	Cannot_Receive((byte)0),
	Can_Receive((byte)1),
	Have_Received((byte)2),
	
	;
	
	private final byte type;
	
	SerialLoginStatus(byte type){
		this.type = type;
	}

	public byte getType() {
		return type;
	}
	
	public static SerialLoginStatus get(byte type){
		for(SerialLoginStatus item : SerialLoginStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
