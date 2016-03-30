package sacred.alliance.magic.app.trading;

public enum State {
	initial((byte)0),
	lock((byte)1),
	trading((byte)2),
	cancel((byte)3),
	complete((byte)4),
	;
	
	State(byte type){
		this.type = type ;
	}
	private final byte type ;
	
	public byte getType() {
		return type;
	}
	
	
}
