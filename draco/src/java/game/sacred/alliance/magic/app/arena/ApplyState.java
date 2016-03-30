package sacred.alliance.magic.app.arena;

public enum ApplyState {

	not_apply((byte)0),
	had_apply((byte)1),
	;
	
	private final byte type ;
	private ApplyState(byte type){
		this.type = type ;
	}
	public byte getType() {
		return type;
	}
	
	
}
