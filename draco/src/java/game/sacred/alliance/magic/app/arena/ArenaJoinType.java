package sacred.alliance.magic.app.arena;

public enum ArenaJoinType {
	
	Not_Limit(0,"不限制"),
	Personal(1,"个人挑战"),
	Team(2,"组队挑战"),
	
	;
	private final int type;
	private final String name;
	
	ArenaJoinType(int type, String name){
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static ArenaJoinType get(int type){
		for(ArenaJoinType item : values()){
			if(type == item.getType()){
				return item;
			}
		}
		return null;
	}
	
}
