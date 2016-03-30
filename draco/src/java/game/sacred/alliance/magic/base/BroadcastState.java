package sacred.alliance.magic.base;

public enum BroadcastState {
	unknow(0,"帤眭"),
	delete(1,"刉壺"),
	open(2,"羲ゐ"),
	close(3,"壽敕"),
	;
	
	private final int type;
	private final String name;
	BroadcastState(int type,String name){
		this.type = type;
		this.name = name;
	}
	public int getType() {
		return type;
	}
	
	public String getName(){
		return name;
	}
	public static BroadcastState get(int type) {
		for (BroadcastState state : BroadcastState.values()) {
			if (type == state.getType()) {
				return state;
			}
		}
		return unknow;
	}
}
