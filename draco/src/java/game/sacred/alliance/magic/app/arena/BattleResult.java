package sacred.alliance.magic.app.arena;

public enum BattleResult {
	fail((byte)0),
	win((byte)1),
	draw((byte)2),
	;
	private final byte type ;
	
	BattleResult(byte type){
		this.type = type ;
	}
	public byte getType() {
		return type;
	}
	
	
}
