package sacred.alliance.magic.base;

public enum ServerType {

	Local((byte)0),
	Dark((byte)1),
	;

	private final byte type ;
	private ServerType(byte type){
		this.type = type ;
	}
	
	public byte getType() {
		return type;
	}
	
}
