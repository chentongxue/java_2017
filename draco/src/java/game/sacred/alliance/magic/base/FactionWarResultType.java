package sacred.alliance.magic.base;

public enum FactionWarResultType {
	Win((byte)0,"胜利"),
	Lose((byte)1,"失败"),
	;
	
	private final byte type;
	private final String name;
	
	FactionWarResultType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	public String getName(){
		return name;
	}
}
