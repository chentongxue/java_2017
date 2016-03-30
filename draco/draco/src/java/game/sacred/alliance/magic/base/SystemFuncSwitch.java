package sacred.alliance.magic.base;

public enum SystemFuncSwitch {

	chat_voice(0),
	;
	private final int type ;
	
	SystemFuncSwitch(int type){
		this.type = type ;
	}

	public int getType() {
		return type;
	}
	
	
}
